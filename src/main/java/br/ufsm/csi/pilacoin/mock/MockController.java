package br.ufsm.csi.pilacoin.mock;

import br.ufsm.csi.pilacoin.mock.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MockController {

    MockService service;

    @GetMapping("/pilaCoin")
    public PilaCoin getPilaCoin() {
        return service.createPilaCoin();
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
    public ValidacaoPilaCoin getValidacaoPilaCoin() {
        return service.createValidacaoPilaCoin();
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
