package br.ufsm.csi.pilacoin.api;


import java.util.Arrays;
import java.util.List;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import br.ufsm.csi.pilacoin.bloco.model.ValidacaoBloco;
import br.ufsm.csi.pilacoin.dificuldade.model.Dificuldade;
import br.ufsm.csi.pilacoin.api.dto.*;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.pila.repository.PilaCoinRepository;
import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import br.ufsm.csi.pilacoin.pila.repository.ValidacaoPilaCoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MockService {

    // Shared
    private  final Long timestamp = 4234234L;
    private  final byte[] chavePublica = new byte[]{01, 10, 11};
    private  final byte[] assinatura = new byte[]{01, 01, 01};
    private  final String nonce = "123456";

    // PilaCoin
    private  final String nomeCriador = "nomeCriador";

    public PilaCoin createPilaCoin() {
        return PilaCoin.builder()
                .dataCriacao(timestamp)
                .chaveCriador(chavePublica)
                .nomeCriador(nomeCriador)
                .nonce(nonce)
                .build();
    }

    // Transacao
    private  final byte[] chaveUsuarioDestino = new byte[]{11, 10, 01};
    private  final String idDefault = "id";
    private  final String status = "status";

    public  Transacao createTransacao(String id) {
        return Transacao.builder()
                .chaveUsuarioOrigem(chavePublica)
                .chaveUsuarioDestino(chaveUsuarioDestino)
                .assinatura(assinatura)
                .noncePila(nonce)
                .dataTransacao(timestamp)
                .id(id != null ? id : idDefault)
                .status(status)
                .build();
    }

    // Bloco
    private  final Integer numeroBloco = 1;
    private  final String nomeUsuarioMinerador = "minerador";

//    public Bloco createBloco() {
//        List<Transacao> transacoes = Arrays
//                .asList(createTransacao("tr1"),
//                        createTransacao("tr2"),
//                        createTransacao("tr3"));
//        return Bloco.builder()
//                .numeroBloco(numeroBloco)
//                .nonceBlocoAnterior(nonce)
//                .nonce(nonce)
//                .chaveUsuarioMinerador(chavePublica)
//                .nomeUsuarioMinerador(nomeUsuarioMinerador)
//                .transacoes(transacoes)
//                .build();
//    }

    // Validacao
    private  final String nomeValidador = "validador";

    public ValidacaoPilaCoin createValidacaoPilaCoin() {
        return ValidacaoPilaCoin.builder()
                .nomeValidador(nomeValidador)
                .chavePublicaValidador(chavePublica)
                .assinaturaPilaCoin(assinatura)
                .pilaCoinJson(createPilaCoin())
                .build();
    }

//    public ValidacaoBloco createValidacaoBloco() {
//        return ValidacaoBloco.builder()
//                .nomeValidador(nomeValidador)
//                .chavePublicaValidador(chavePublica)
//                .assinaturaBloco(assinatura)
//                .bloco(createBloco())
//                .build();
//    }

    // Dificuldade
    private  final String dificuldade = "123456";
    private  final Long validadeFinal = 23412341234L;

    public Dificuldade createDificuldade() {
        return Dificuldade.builder()
                .dificuldade(dificuldade)
                .inicio(timestamp)
                .validadeFinal(validadeFinal)
                .build();
    }

    private final PilaCoinRepository pilaCoinRepository;
    private final ValidacaoPilaCoinRepository validacaoRepository;

    public List<PilaCoin> findAllPilaCoins() {
        return pilaCoinRepository.findAll();
    }
    public List<ValidacaoPilaCoin> findallValidacoesPilaCoin() {
        return validacaoRepository.findAll();
    }
}
