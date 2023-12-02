package br.ufsm.csi.pilacoin.mock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class PilaCoin {
    private Long dataCriacao;
    private byte[] chaveCriador;
    private String nomeCriador;
    private String nonce;

    // getters and setters...
}