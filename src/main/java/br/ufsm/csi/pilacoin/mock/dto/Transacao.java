package br.ufsm.csi.pilacoin.mock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
public class Transacao {
    private byte[] chaveUsuarioOrigem;
    private byte[] chaveUsuarioDestino;
    private byte[] assinatura;
    private String noncePila;
    private Long dataTransacao;
    private String id;
    private String status;

    // getters and setters...
}