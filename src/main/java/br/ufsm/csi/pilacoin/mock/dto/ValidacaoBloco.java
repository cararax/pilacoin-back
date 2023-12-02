package br.ufsm.csi.pilacoin.mock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class ValidacaoBloco {
    private String nomeValidador;
    private byte[] chavePublicaValidador;
    private byte[] assinaturaBloco;
    private Bloco bloco;

    // getters and setters...
}