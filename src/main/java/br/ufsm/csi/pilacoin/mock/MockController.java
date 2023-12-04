package br.ufsm.csi.pilacoin.mock;

import br.ufsm.csi.pilacoin.mock.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class MockController {

    private final MockService service;

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
    @PostMapping("/transacao")
    public Transacao insert(@RequestBody Transacao transacao) {
        return new ResponseEntity<>(transacao, HttpStatus.CREATED).getBody();
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
