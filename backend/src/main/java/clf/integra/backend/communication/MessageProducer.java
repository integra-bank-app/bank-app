package clf.integra.backend.communication;

import clf.integra.SalaryRequestMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import static clf.integra.QueueName.INTEGRA_TO_ANAF;

@Component
public class MessageProducer {
    private final RabbitTemplate rabbitTemplate;

    public MessageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(SalaryRequestMessage message) {
        rabbitTemplate.convertAndSend(INTEGRA_TO_ANAF, message);
    }
}
