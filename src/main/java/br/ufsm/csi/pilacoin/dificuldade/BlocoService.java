package br.ufsm.csi.pilacoin.dificuldade;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.mock.dto.Bloco;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.ValidacaoBloco;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Log4j2
@RequiredArgsConstructor
public class BlocoService {

    private static final ObjectMapper mapper = new ObjectMapper();
    private String username = "carara-schmitzhaus";
    private final KeyPairGenerator keyPairGenerator;
    private final SecureRandom random = new SecureRandom();
    private static final int radix = 16;
    private final String algorithm = "SHA-256";
    private AtomicReference<Dificuldade> difficulty = new AtomicReference<>();

    private final RabbitTemplate rabbitTemplate;
    @Value("${queue.bloco-minerado}")
    private String blocoMineradoQueue;
    @Value("${queue.bloco-validado}")
    private String blocoValidadoQueue;
    private final Set<Bloco> minedBlocks = Collections.synchronizedSet(new HashSet<>());


    void setBlocoDifficulty(Dificuldade difficulty) {
        log.info("Dificuldade do bloco atualizada: {}", difficulty);
        this.difficulty.set(difficulty);
    }

    @SneakyThrows
    @RabbitListener(queues = "${queue.descobre-bloco}")
    public void listen(String message) {
        log.error("Bloco recebido: {}", message);

        int numberOfThreads = 8; //altere conforme necessário
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            BlockProcessor blockProcessor = new BlockProcessor(message, difficulty, this);
            executorService.submit(blockProcessor);
        }

        executorService.shutdown();
    }
    boolean isBlocoValid(Bloco bloco) {
        return hashMeetsDifficulty(bloco, difficulty.get());
    }

    private Bloco convertJsonToPilaCoin(String message) throws JsonProcessingException {
        return mapper.readValue(message, Bloco.class);
    }
    @SneakyThrows
    private String convertToJson(Object object) {
        return mapper.writeValueAsString(object);
    }

    @SneakyThrows
    public void publishMinedBlock(Bloco bloco) {
        rabbitTemplate.convertAndSend(blocoMineradoQueue, convertToJson(bloco));
        log.info("Block sent to mined queue");
    }

    @SneakyThrows
    public void publishValidatedBlock(ValidacaoBloco validacaoBloco) {
        rabbitTemplate.convertAndSend(blocoValidadoQueue, convertToJson(validacaoBloco));
        log.error("Block sent to validated queue");
    }


    @SneakyThrows
    @RabbitListener(queues = "${queue.bloco-minerado}")
    public void validatePilaCoin(String message) {
        log.info("Validating Block");
        Bloco bloco = convertJsonToPilaCoin(message);
        //todo: exceção quando nao tem dificuldade
        if (isCreatedByCurrentUser(bloco) || isAlreadyValidated(bloco)) {
            publishMinedBlock(bloco);
            return;
        }

        if (isBlockInvalid(bloco)) return;
        log.error("VALIDOU");
        ValidacaoBloco validated = createValidacaoBloco(bloco);
        //todo: salvar no banco
        minedBlocks.add(bloco);
        publishValidatedBlock(validated);
        log.info("PilaCoin validated and sent to queue");
    }
    private boolean isBlockValid(Bloco bloco) {
        boolean isValid = hashMeetsDifficulty(bloco, difficulty.get());
        if (isValid) log.info("PilaCoin is valid");
        return isValid;
    }

    @SneakyThrows
    boolean hashMeetsDifficulty(Bloco bloco, Dificuldade difficulty) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        String pilaJson = mapper.writeValueAsString(bloco);
        byte[] digest = md.digest(pilaJson.getBytes(StandardCharsets.UTF_8));

        BigInteger hashNum = new BigInteger(digest).abs();
        BigInteger difficultyNum = new BigInteger(difficulty.getDificuldade(), radix).abs();

        return hashNum.compareTo(difficultyNum) < 0;
    }
    private boolean isBlockInvalid(Bloco bloco) {
        return !isBlockValid(bloco);
    }
    public boolean isAlreadyValidated(Bloco bloco) {
        boolean itsValid = minedBlocks.contains(bloco);
        if (itsValid) log.info("PilaCoin already validated");
        return itsValid;
    }
    public boolean isCreatedByCurrentUser(Bloco bloco) {
        if (bloco.getNomeUsuarioMinerador() == null) return false;
        boolean itsMine = bloco.getNomeUsuarioMinerador().equals(username);
        if (itsMine) log.info("PilaCoin created by current user");
        return false;
    }
    Bloco populateBloco(Bloco bloco) {
        bloco.setChaveUsuarioMinerador(keyPairGenerator.getPublicKey().toString().getBytes(StandardCharsets.UTF_8));
        bloco.setNomeUsuarioMinerador(username);
        bloco.setNonce(generateNonce().toString());
        return bloco;
    }
    @SneakyThrows
    private BigInteger generateNonce() {
        byte[] nonce = new byte[32];
        random.nextBytes(nonce);
        String nonceHex = bytesToHex(nonce);
        return new BigInteger(MessageDigest.getInstance(algorithm).digest(nonceHex.getBytes(StandardCharsets.UTF_8))).abs();
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    Bloco convertJsonToBlock(String message) throws JsonProcessingException {
        return mapper.readValue(message, Bloco.class);
    }

    @SneakyThrows
    public ValidacaoBloco createValidacaoBloco(Bloco bloco) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPairGenerator.getPrivateKey());

        String blocoJson = mapper.writeValueAsString(bloco);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] pilaHash = sha256.digest(blocoJson.getBytes(StandardCharsets.UTF_8));
        signature.update(pilaHash);

        byte[] signatureBytes = signature.sign();
        return ValidacaoBloco.builder()
                .nomeValidador(username)
                .chavePublicaValidador(keyPairGenerator.getPublicKey().getEncoded())
                .assinaturaBloco(signatureBytes).bloco(bloco).build();


    }
}
