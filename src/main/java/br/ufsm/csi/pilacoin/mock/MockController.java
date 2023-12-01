package br.ufsm.csi.pilacoin.mock;

import br.ufsm.csi.pilacoin.mock.dto.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MockController {


    @GetMapping("/pilaCoin")
    public PilaCoin getPilaCoin() {
        return MockService.createPilaCoin();
    }

    @GetMapping("/bloco")
    public Bloco getBloco() {
        return MockService.createBloco();
    }

    @GetMapping("/transacao")
    public Transacao getTransacao() {
        return MockService.createTransacao(null);
    }

    @GetMapping("/validacaoPilaCoin")
    public ValidacaoPilaCoin getValidacaoPilaCoin() {
        return MockService.createValidacaoPilaCoin();
    }

    @GetMapping("/validacaoBloco")
    public ValidacaoBloco getValidacaoBloco() {
        return MockService.createValidacaoBloco();
    }

    @GetMapping("/dificuldade")
    public Dificuldade getDificuldade() {
        return MockService.createDificuldade();
    }
}
