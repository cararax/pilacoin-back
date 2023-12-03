package br.ufsm.csi.pilacoin.mock.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Dificuldade {
    private String dificuldade;
    private Long inicio;
    private Long validadeFinal;

    // getters and setters...   
}