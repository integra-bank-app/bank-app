package clf.integra.backend.producer;

import clf.integra.backend.dto.SalaryRequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(SalaryRequestMessage message) {
        rabbitTemplate.convertAndSend(ProducerConfig.INTEGRA_TO_ANAF, message);
    }
}
