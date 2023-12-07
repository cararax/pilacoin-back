package br.ufsm.csi.pilacoin.api.dto;

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
@Document
@JsonPropertyOrder(alphabetic = true)
@JsonInclude(NON_NULL)
public class Transacao {
    private byte[] chaveUsuarioOrigem;
    private byte[] chaveUsuarioDestino;
    private byte[] assinatura;
    private String noncePila;
    private Long dataTransacao;
    private String id;
    private String status;

    // getters and setters...
}