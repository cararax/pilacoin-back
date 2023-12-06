//package br.ufsm.csi.pilacoin.query.service;
//
//import br.ufsm.csi.pilacoin.query.model.Query;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@RequiredArgsConstructor
//public class QueryService {
//
//    private final RabbitTemplate rabbitTemplate;
//
//    private ObjectMapper mapper = new ObjectMapper();
//
//
//    public CompletableFuture<String> sendMessageAndReceiveResponseAsync(Query query) {
//        CompletableFuture<String> result = new CompletableFuture<>();
//
//        String message = convertToJson(query);
//
//        rabbitTemplate.convertAndSend("query", message);
//
//        // Configurar ouvinte assíncrono para processar a resposta
//        rabbitTemplate.setReceiveTimeout(5000); // Configurar o timeout para a resposta
//
//        rabbitTemplate.receive("carara-schmitzhaus-query", (replyMessage) -> {
//            if (replyMessage != null) {
//                Query response = convertToQuery(replyMessage);
//                result.complete(new String(replyMessage.getBody()));
//            } else {
//                result.complete("No response received.");
//            }
//        });
//
//        return result;
//    }
//
//    @SneakyThrows
//    public Query convertToQuery(String message) {
//        return mapper.readValue(message, Query.class);
//    }
//    @SneakyThrows
//    private String convertToJson(Object object) {
//        return mapper.writeValueAsString(object);
//    }
//
//
//
//}
