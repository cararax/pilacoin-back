package br.ufsm.csi.pilacoin.query.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

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
public class Query {
    private int idQuery;
    private String nomeUsuario;
    private String tipoQuery;
    private String status;
    private String usuarioMinerador;
    private String nonce;
    private int idBloco;
}
