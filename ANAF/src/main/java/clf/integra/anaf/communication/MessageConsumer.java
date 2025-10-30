package clf.integra.anaf.communication;

import clf.integra.SalaryRequestMessage;
import clf.integra.anaf.service.AnafService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static clf.integra.QueueName.INTEGRA_TO_ANAF;

@Component
public class MessageConsumer {
    private final AnafService anafService;

    public MessageConsumer(AnafService anafService) {
        this.anafService = anafService;
    }

    @RabbitListener(queues = INTEGRA_TO_ANAF, containerFactory = "rabbitListenerContainerFactory")
    public void receiveMessage(SalaryRequestMessage message) {
        int salary = anafService.getUserSalary(message.userId());
        // TODO: send salary back to integra
    }
}
