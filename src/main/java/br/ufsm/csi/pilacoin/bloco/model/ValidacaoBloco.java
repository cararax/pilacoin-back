package br.ufsm.csi.pilacoin.bloco.model;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@ToString
@JsonInclude(NON_NULL)
public class ValidacaoBloco {
    private String id;
    private String nomeValidador;
    private byte[] chavePublicaValidador;
    private byte[] assinaturaBloco;
    private Bloco bloco;

    // getters and setters...
}