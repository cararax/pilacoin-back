package br.ufsm.csi.pilacoin.transferir;

import br.ufsm.csi.pilacoin.key.KeyPairGenerator;
import br.ufsm.csi.pilacoin.pila.model.PilaCoin;
import br.ufsm.csi.pilacoin.pila.model.ValidacaoPilaCoin;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.Signature;
import java.util.Date;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransferenciaPilaService {
    private ObjectMapper mapper = new ObjectMapper();
    private String username = "carara-schmitzhaus";

    private final KeyPairGenerator keyPairGenerator;
    private final RabbitTemplate rabbitTemplate;

    public void transferirPila(TransferenciaPilaRequest request) {

        TransferenciaPilaRequest transferenciaPilaRequest = generateValidationMessage(request);
        publishTransacao(transferenciaPilaRequest);


    }
    @SneakyThrows
    public void publishTransacao(TransferenciaPilaRequest transferenciaPilaRequest) {
        rabbitTemplate.convertAndSend("traferir-pila", convertToJson(transferenciaPilaRequest));
        log.info("Transação sent to mined queue");
    }

    @SneakyThrows
    private String convertToJson(Object object) {
        return mapper.writeValueAsString(object);
    }

    @SneakyThrows
    private TransferenciaPilaRequest generateValidationMessage(TransferenciaPilaRequest transferenciaPilaRequest) {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPairGenerator.getPrivateKey());

        String pilaJson = mapper.writeValueAsString(transferenciaPilaRequest);

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] pilaHash = sha256.digest(pilaJson.getBytes(StandardCharsets.UTF_8));
        signature.update(pilaHash);

        byte[] signatureBytes = signature.sign();
        return TransferenciaPilaRequest
                .builder()
                .chaveUsuarioOrigem(keyPairGenerator.getPublicKey().getEncoded())
                .chaveUsuarioDestino(transferenciaPilaRequest.getChaveUsuarioDestino())
                .nomeUsuarioOrigem(username)
                .nomeUsuarioDestino(transferenciaPilaRequest.getNomeUsuarioDestino())
                .assinatura(signatureBytes)
                .noncePila(transferenciaPilaRequest.getNoncePila())
                .dataTransacao(new Date().getTime())
                .build();
//                .validacaoPilaCoin(
//                )
//                .nomeValidador(username)
//                .chavePublicaValidador(keyPairGenerator.getPublicKey().getEncoded())
//                .assinaturaPilaCoin(signatureBytes).pilaCoinJson(transferenciaPilaRequest).build();
    }
}
