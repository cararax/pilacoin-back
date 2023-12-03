package br.ufsm.csi.pilacoin.pila;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.PilaCoin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public void startMining(Dificuldade dificuldade) {
        this.dificuldadeAtual.set(dificuldade);
        int numeroDeThreads = Runtime.getRuntime().availableProcessors();
        log.info("Começando a minerar com a nova dificuldade: {}", dificuldadeAtual);
        log.info("Número de threads: {}", numeroDeThreads);

        for (int i = 0; i < numeroDeThreads; i++) {
            MinerWorker minerWorker = new MinerWorker("Thread-" + i, dificuldadeAtual, keyPairGenerator, this);
            minerWorker.start();
        }
    }

    public void publishMinedPila(PilaCoin pilaCoin) {
        try {
            rabbitTemplate.convertAndSend(pilaMineradoQueue, new ObjectMapper().writeValueAsString(pilaCoin));
            log.info("PilaCoin sent to queue");
        } catch (JsonProcessingException e) {
            log.error("Error converting PilaCoin to JSON string: {}", e.getMessage());
        }
    }

}
