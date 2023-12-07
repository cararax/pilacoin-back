package br.ufsm.csi.pilacoin.pila.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
    @JsonIgnore
    private String id;
    private Long dataCriacao;
    private byte[] chaveCriador;
    private String nomeCriador;
    private String nonce;
    private String status;

    // getters and setters...
}