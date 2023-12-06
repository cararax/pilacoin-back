package br.ufsm.csi.pilacoin.dificuldade;

import br.ufsm.csi.pilacoin.mock.dto.Bloco;
import br.ufsm.csi.pilacoin.mock.dto.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.ValidacaoBloco;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class BlockProcessor implements Runnable {
    private String message;
    private AtomicReference<Dificuldade> difficulty;
    private BlocoService blocoService;

    public BlockProcessor(String message, AtomicReference<Dificuldade> difficulty, BlocoService blocoService) {
        this.message = message;
        this.difficulty = difficulty;
        this.blocoService = blocoService;
    }

    @Override
    @SneakyThrows
    public void run() {
        while (true) {
            if (Objects.isNull(difficulty)) {
                log.info("Dificuldade não definida. Bloco não será minerado");
            }
            Bloco bloco = blocoService.convertJsonToDifficulty(message);
            bloco = blocoService.populateBloco(bloco);
//            if (blocoService.isCreatedByCurrentUser(bloco)
//                    || blocoService.isAlreadyValidated(bloco)) {
//                blocoService.publishMinedBlock(bloco);
//                return;
//            }
            if (blocoService.isBlocoValid(bloco)) {
                ValidacaoBloco validacaoBloco = blocoService.createValidacaoBloco(bloco);
                log.info("Bloco válido: {}", bloco);
                blocoService.publishValidatedBlock(validacaoBloco);
            } else {
                log.info("Bloco inválido: {}", bloco);
            }
        }
    }
}
