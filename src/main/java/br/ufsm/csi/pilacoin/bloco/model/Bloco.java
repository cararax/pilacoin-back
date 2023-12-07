package br.ufsm.csi.pilacoin.bloco.model;

import br.ufsm.csi.pilacoin.api.dto.Transacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Document
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(NON_NULL)
public class Bloco {
    private Integer numeroBloco;
    private String nonceBlocoAnterior;
    private String nonce;
    private byte[] chaveUsuarioMinerador;
    private String nomeUsuarioMinerador;
    private List<Transacao> transacoes;

}