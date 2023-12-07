package br.ufsm.csi.pilacoin.bloco.model;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@ToString
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(NON_NULL)
public class ValidacaoBloco {
    private String nomeValidador;
    private byte[] chavePublicaValidador;
    private byte[] assinaturaBloco;
    private Bloco bloco;

    // getters and setters...
}