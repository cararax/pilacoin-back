package br.ufsm.csi.pilacoin.dificuldade;

import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.pila.PilaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Log4j2
public class DifficultyService {

    @Autowired
    PilaService pilaService;

    private ObjectMapper mapper = new ObjectMapper();

    private Dificuldade dificuldadeAtual = new Dificuldade();

    @RabbitListener(queues = "${queue.dificuldade}")
    public void listen(String message) {
        try {
            Dificuldade dificuldadeRecebida = convertJsonToDifficulty(message);
            if (isDifficultyChanged(dificuldadeAtual, dificuldadeRecebida)) {
                logDifficultyChange(dificuldadeAtual, dificuldadeRecebida);
                dificuldadeAtual = dificuldadeRecebida;
                if (isDifficultyValid(dificuldadeAtual)) {
                    pilaService.startMining(dificuldadeAtual);
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Erro ao interpretar mensagem de dificuldade: {}", e.getMessage());
        }
    }

    private boolean isDifficultyChanged(Dificuldade dificuldadeAtual, Dificuldade dificuldadeRecebida) {
        return !dificuldadeAtual.equals(dificuldadeRecebida);
    }

    private void logDifficultyChange(Dificuldade dificuldadeAtual, Dificuldade dificuldadeRecebida) {
        if (dificuldadeAtual != null)
            log.info("Dificuldade atual antes da atualização: {}", dificuldadeAtual);
        log.info("Dificuldade atualizada. Valor: {}", dificuldadeRecebida);
    }

    private boolean isDifficultyValid(Dificuldade dificuldade) {
        long nowMillis = System.currentTimeMillis();
        Instant now = Instant.ofEpochMilli(nowMillis);
        Instant validadeFinal = Instant.ofEpochMilli(dificuldade.getValidadeFinal());
        if (now.isAfter(validadeFinal)) {
            log.info("Dificuldade expirou. Hora atual: {}, Validade Final: {}", now, validadeFinal);
            return false;
        }
        log.info("Dificuldade ainda é válida. Hora atual: {}, Validade Final: {}", now, validadeFinal);
        return true;
    }

    private Dificuldade convertJsonToDifficulty(String message) throws JsonProcessingException {
        return mapper.readValue(message, Dificuldade.class);
    }
}
