package br.ufsm.csi.pilacoin.mock;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import br.ufsm.csi.pilacoin.bloco.model.ValidacaoBloco;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import br.ufsm.csi.pilacoin.mock.dto.*;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MockController {

    private final MockService service;

    @GetMapping("/pilaCoin")
    public List<PilaCoin> getPilaCoin() {
        return service.findAllPilaCoins();
    }

    @GetMapping("/bloco")
    public Bloco getBloco() {
        return service.createBloco();
    }

    @GetMapping("/transacao")
    public Transacao getTransacao() {
        return service.createTransacao(null);
    }

    @GetMapping("/validacaoPilaCoin")
    public List<ValidacaoPilaCoin> getValidacaoPilaCoin() {
        return service.findallValidacoesPilaCoin();
    }

    @GetMapping("/validacaoBloco")
    public ValidacaoBloco getValidacaoBloco() {
        return service.createValidacaoBloco();
    }

    @GetMapping("/dificuldade")
    public Dificuldade getDificuldade() {
        return service.createDificuldade();
    }
}
