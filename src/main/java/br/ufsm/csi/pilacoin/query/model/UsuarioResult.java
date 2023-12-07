package br.ufsm.csi.pilacoin.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(NON_NULL)
public class UsuarioResult {
    private int id;
    private String chavePublica;
    private String nome;
}
