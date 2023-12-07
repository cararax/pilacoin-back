package br.ufsm.csi.pilacoin.pila.model;

import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document
@JsonInclude(NON_NULL)
public class ValidacaoPilaCoin {
    private String nomeValidador;
    private byte[] chavePublicaValidador;
    private byte[] assinaturaPilaCoin;
    private PilaCoin pilaCoinJson;

}