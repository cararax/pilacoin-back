package br.ufsm.csi.pilacoin.pila.service;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.pila.miner.MinerWorker;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import br.ufsm.csi.pilacoin.pila.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.pila.repository.ValidacaoPilaCoinRepository;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Log4j2
@RequiredArgsConstructor
public class PilaService {

    private AtomicReference<Dificuldade> dificuldadeAtual = new AtomicReference<>();

    private final RabbitTemplate rabbitTemplate;

    private final KeyPairGenerator keyPairGenerator;

    @Value("${queue.pila-minerado}")
    private String pilaMineradoQueue;
    @Value("${queue.pila-validado}")
    private String pilaValidadoQueue;

    @Value("${pilacoin.username:carara-schmitzhaus}")
    private String username;

    private MinerWorker minerWorker;
    private ObjectMapper mapper = new ObjectMapper();

    private final PilaCoinRepository pilaCoinRepository;
    private final ValidacaoPilaCoinRepository validacaoRepository;
    private final Set<PilaCoin> minedPilaCoins = Collections.synchronizedSet(new HashSet<>());

    public void startMining(Dificuldade dificuldade) {
        this.dificuldadeAtual.set(dificuldade);
        int threadNumber = 1;// Runtime.getRuntime().availableProcessors();
        log.info("Começando a minerar com a nova dificuldade: {}", dificuldadeAtual);
        log.info("Número de threads: {}", threadNumber);

        for (int i = 0; i < threadNumber; i++) {
            minerWorker = new MinerWorker("Thread-" + i, dificuldadeAtual, keyPairGenerator, this);
            minerWorker.start();
        }
    }

    @SneakyThrows
    public void publishMinedPila(PilaCoin pilaCoin) {
        rabbitTemplate.convertAndSend(pilaMineradoQueue, convertToJson(pilaCoin));
        log.info("PilaCoin sent to mined queue");
    }

    @SneakyThrows
    public void publishValidatedPila(ValidacaoPilaCoin validatedPilaCoin) {
        rabbitTemplate.convertAndSend(pilaValidadoQueue, convertToJson(validatedPilaCoin));
        log.info("PilaCoin sent to validated queue");
    }

    @SneakyThrows
    @RabbitListener(queues = "${queue.pila-minerado}")
    public void validatePilaCoin(String message) {
        log.info("Validating PilaCoin");
        PilaCoin pilaCoin = convertJsonToPilaCoin(message);
        //todo: exceção quando nao tem dificuldade
        if (isCreatedByCurrentUser(pilaCoin) || isAlreadyValidated(pilaCoin)) {
            publishMinedPila(pilaCoin);
            return;
        }

        if (isPilaInvalid(pilaCoin)) return;
        log.error("VALIDOU");
        ValidacaoPilaCoin validated = generateValidationMessage(pilaCoin);
        pilaCoinRepository.save(pilaCoin);
        validacaoRepository.save(validated);
        minedPilaCoins.add(pilaCoin);
        publishValidatedPila(validated);
        log.info("PilaCoin validated and sent to queue");
    }

    private PilaCoin convertJsonToPilaCoin(String message) throws JsonProcessingException {
        return mapper.readValue(message, PilaCoin.class);
    }

    private boolean isPilaValid(PilaCoin pilaCoin) {
        boolean isValid = MinerWorker.hashMeetsDifficulty(pilaCoin, dificuldadeAtual.get());
        if (isValid) log.info("PilaCoin is valid");
        return isValid;
    }

    private boolean isPilaInvalid(PilaCoin pilaCoin) {
        return !isPilaValid(pilaCoin);
    }

    private boolean isCreatedByCurrentUser(PilaCoin pilaCoin) {
        if (pilaCoin.getNomeCriador() == null) return false;
        boolean itsMine = pilaCoin.getNomeCriador().equals(username);
        if (itsMine) log.info("PilaCoin created by current user");
        return itsMine;
    }

    @SneakyThrows
    private void resendToQueue(PilaCoin pilaCoin) {
        rabbitTemplate.convertAndSend(pilaMineradoQueue, convertToJson(pilaCoin));
        log.info("PilaCoin resent to queue");
    }

    private boolean isAlreadyValidated(PilaCoin pilaCoin) {
        boolean itsValid = minedPilaCoins.contains(pilaCoin);
        if (itsValid) log.info("PilaCoin already validated");
        return itsValid;
    }

    @SneakyThrows
    private ValidacaoPilaCoin generateValidationMessage(PilaCoin pilaCoin) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPairGenerator.getPrivateKey());

        String pilaJson = mapper.writeValueAsString(pilaCoin);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] pilaHash = sha256.digest(pilaJson.getBytes(StandardCharsets.UTF_8));
        signature.update(pilaHash);

        byte[] signatureBytes = signature.sign();
        return ValidacaoPilaCoin.builder()
                .nomeValidador(username)
                .chavePublicaValidador(keyPairGenerator.getPublicKey().getEncoded())
                .assinaturaPilaCoin(signatureBytes).pilaCoinJson(pilaCoin).build();
    }

    @SneakyThrows
    private String convertToJson(Object object) {
        return mapper.writeValueAsString(object);
    }

}

