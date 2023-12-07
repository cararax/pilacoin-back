package br.ufsm.csi.pilacoin.transferir;

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
public class TransferenciaPilaRequest {
    private byte[] chaveUsuarioOrigem;
    private byte[] chaveUsuarioDestino;
    private String nomeUsuarioOrigem;
    private String nomeUsuarioDestino;
    private byte[] assinatura;
    private String noncePila;
    private Long dataTransacao;

    // getters e setters
}