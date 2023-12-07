package br.ufsm.csi.pilacoin.pila.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(NON_NULL)
public class PilaCoin {
    private Long dataCriacao;
    private byte[] chaveCriador;
    private String nomeCriador;
    private String nonce;
    private String status;

    // getters and setters...
}