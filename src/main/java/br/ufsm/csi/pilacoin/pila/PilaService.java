package br.ufsm.csi.pilacoin.pila;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.PilaCoin;
import br.ufsm.csi.pilacoin.mock.dto.ValidacaoPilaCoin;
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
    private final Set<PilaCoin> minedPilaCoins = Collections.synchronizedSet(new HashSet<>());

    public void startMining(Dificuldade dificuldade) {
        this.dificuldadeAtual.set(dificuldade);
        int threadNumber = Runtime.getRuntime().availableProcessors();
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
        rabbitTemplate.convertAndSend(pilaValidadoQueue, convertValidatedToJson(validatedPilaCoin));
        log.info("PilaCoin sent to validated queue");
    }

    @SneakyThrows
    @RabbitListener(queues = "${queue.pila-minerado}")
    public void validatePilaCoin(String message) {
        PilaCoin pilaCoin = mapper.readValue(message, PilaCoin.class);
        if (isCreatedByCurrentUser(pilaCoin) || isAlreadyValidated(pilaCoin) || isPilaInvalid(pilaCoin)) {
            publishMinedPila(pilaCoin);
            return;
        }
        minedPilaCoins.add(pilaCoin);
        publishValidatedPila(generateValidationMessage(pilaCoin));
        log.info("PilaCoin validated and sent to queue");
    }


    private boolean isPilaValid(PilaCoin pilaCoin) {
        return minerWorker.hashMeetsDifficulty(pilaCoin, dificuldadeAtual.get());
    }

    private boolean isPilaInvalid(PilaCoin pilaCoin) {
        return !isPilaValid(pilaCoin);
    }

    private boolean isCreatedByCurrentUser(PilaCoin pilaCoin) {
        return pilaCoin.getNomeCriador().equals(username);
    }

    @SneakyThrows
    private void resendToQueue(PilaCoin pilaCoin) {
        rabbitTemplate.convertAndSend(pilaMineradoQueue, convertPilaToJson(pilaCoin));
        log.info("PilaCoin resent to queue");
    }

    private boolean isAlreadyValidated(PilaCoin pilaCoin) {
        return minedPilaCoins.contains(pilaCoin);
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
        return ValidacaoPilaCoin.builder().nomeValidador(username).chavePublicaValidador(keyPairGenerator.getPublicKey().getEncoded()).assinaturaPilaCoin(signatureBytes).pilaCoinJson(pilaCoin).build();
    }

    @SneakyThrows
    private String convertToJson(Object object) {
        return mapper.writeValueAsString(object);
    }
    @SneakyThrows
    private String convertPilaToJson(PilaCoin pila) {
        return mapper.writeValueAsString(pila);
    }

    @SneakyThrows
    private String convertValidatedToJson(ValidacaoPilaCoin validated) {
        return mapper.writeValueAsString(validated);
    }

}

