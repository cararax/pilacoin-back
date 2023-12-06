package br.ufsm.csi.pilacoin.report;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReportService {

    @SneakyThrows
    @RabbitListener(queues = "${queue.report}")
    public void listen(String message) {
        log.info("REPORT: {}", message);
    }
}
