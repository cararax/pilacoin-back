package br.ufsm.csi.pilacoin.mock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
public class Dificuldade {
    private String dificuldade;
    private Long inicio;
    private Long validadeFinal;

    // getters and setters...   
}