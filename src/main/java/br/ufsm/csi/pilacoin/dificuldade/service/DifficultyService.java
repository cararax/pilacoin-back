package br.ufsm.csi.pilacoin.dificuldade.service;

import br.ufsm.csi.pilacoin.bloco.service.BlocoService;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import br.ufsm.csi.pilacoin.pila.service.PilaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@Log4j2
@RequiredArgsConstructor
public class DifficultyService {

    private final PilaService pilaService;
    private final BlocoService blocoService;
    private final ObjectMapper mapper = new ObjectMapper();
    private Dificuldade dificuldadeAtual = new Dificuldade();

    @SneakyThrows
    @RabbitListener(queues = "${queue.dificuldade}")
    public void listen(String message) {
        //todo: tratar quando dificuldade for nula
        //pilaService e blocoService precisam
        //salvar dificuldade?
        Dificuldade dificuldadeRecebida = convertJsonToDifficulty(message);
        if (isDifficultyUnchanged(dificuldadeAtual, dificuldadeRecebida)) return;
        if (isDifficultyInvalid(dificuldadeRecebida)) return;
        dificuldadeAtual = dificuldadeRecebida;
        pilaService.startMining(dificuldadeAtual);
        blocoService.setBlocoDifficulty(dificuldadeAtual);
    }

    private boolean isDifficultyUpdated(Dificuldade dificuldadeAtual, Dificuldade dificuldadeRecebida) {
        if (!Objects.equals(dificuldadeAtual, dificuldadeRecebida)) {
            log.info((dificuldadeAtual != null) ? "Dificuldade anterior: {}" : "", dificuldadeAtual);
            log.info("Dificuldade atualizada. Valor: {}", dificuldadeRecebida);
            return true;
        }
        return false;
    }

    private boolean isDifficultyUnchanged(Dificuldade dificuldadeAtual, Dificuldade dificuldadeRecebida) {
        return !isDifficultyUpdated(dificuldadeAtual, dificuldadeRecebida);
    }

    private boolean isDifficultyValid(Dificuldade dificuldade) {
        Instant now = Instant.ofEpochMilli(System.currentTimeMillis());
        Instant validadeFinal = Instant.ofEpochMilli(dificuldade.getValidadeFinal());
        boolean isValid = now.isBefore(validadeFinal);
        String status = isValid ? "válida" : "inválida";
        log.info("A dificuldade é {}. Hora atual: {}, Validade Final: {}", status, now, validadeFinal);
        return isValid;
    }

    private boolean isDifficultyInvalid(Dificuldade dificuldade) {
        return !isDifficultyValid(dificuldade);
    }

    private Dificuldade convertJsonToDifficulty(String message) throws JsonProcessingException {
        return mapper.readValue(message, Dificuldade.class);
    }
}
