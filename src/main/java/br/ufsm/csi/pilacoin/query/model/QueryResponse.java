package br.ufsm.csi.pilacoin.query.model;

import br.ufsm.csi.pilacoin.bloco.model.Bloco;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

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
public class QueryResponse {
    private int idQuery;
    private String usuario;
    private List<PilaCoin> pilasResult;
    private List<Bloco> blocosResult;
    private List<UsuarioResult> usuariosResult;
}
