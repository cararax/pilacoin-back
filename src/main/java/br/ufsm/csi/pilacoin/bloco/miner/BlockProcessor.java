package br.ufsm.csi.pilacoin.bloco.miner;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import br.ufsm.csi.pilacoin.bloco.model.ValidacaoBloco;
import br.ufsm.csi.pilacoin.bloco.service.BlocoService;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class BlockProcessor implements Runnable {
    private String message;
    private AtomicReference<Dificuldade> difficulty;
    private BlocoService blocoService;
    private ObjectMapper mapper = new ObjectMapper();


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
            Bloco bloco = blocoService.convertJsonToBlock(message);
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
