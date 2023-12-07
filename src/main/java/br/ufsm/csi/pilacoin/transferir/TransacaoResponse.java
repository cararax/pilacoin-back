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
public class TransacaoResponse {
    private String id;
    private String status;

    // getters e setters
}