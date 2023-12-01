package br.ufsm.csi.pilacoin.mock.dto;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
public class Bloco {
    private Integer numeroBloco;
    private String nonceBlocoAnterior;
    private String nonce;
    private byte[] chaveUsuarioMinerador;
    private String nomeUsuarioMinerador;
    private List<Transacao> transacoes;

}