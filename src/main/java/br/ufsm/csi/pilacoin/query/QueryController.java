//package br.ufsm.csi.pilacoin.query;
//
//import br.ufsm.csi.pilacoin.query.model.Query;
//import br.ufsm.csi.pilacoin.query.model.QueryResponse;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.springframework.amqp.core.MessageListener;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//
//@RestController
//public class QueryController {
//
//    private static final String REQUEST_QUEUE = "carara-schmitzhaus";
//    private static final String RESPONSE_QUEUE = "carara-schmitzhaus-query";
//
//    private final RabbitTemplate rabbitTemplate;
//    private ObjectMapper mapper;
//
//    public QueryController(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//
//        // Define Jackson2JsonMessageConverter como default MessageConverter
//        MessageConverter messageConverter = new Jackson2JsonMessageConverter();
//        rabbitTemplate.setMessageConverter(messageConverter);
//    }
//
//    @PostMapping("/query")
//    public QueryResponse sendQuery(@RequestBody Query query) throws Exception {
//        CompletableFuture<QueryResponse> futureResponse = new CompletableFuture<>();
//        MessageListener listener = msg -> {
//            QueryResponse response = (QueryResponse) rabbitTemplate.getMessageConverter().fromMessage(msg);
//            futureResponse.complete(response);
//        };
//
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setConnectionFactory(rabbitTemplate.getConnectionFactory());
//        container.setQueueNames(RESPONSE_QUEUE);
//        container.setMessageListener(listener);
//        container.start();
//
//        rabbitTemplate.convertAndSend(REQUEST_QUEUE, query);
//
//        // Espera até 10 segundos para que a resposta venha
//        QueryResponse queryResponse = futureResponse.get(10, TimeUnit.SECONDS);
//
//        container.stop();
//
//        return queryResponse;
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
//}