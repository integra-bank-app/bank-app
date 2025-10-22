package clf.integra.anaf.communication;

import clf.integra.anaf.dto.SalaryRequestMessage;
import clf.integra.anaf.service.AnafService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {
    private final AnafService anafService;

    public MessageListener(AnafService anafService) {
        this.anafService = anafService;
    }

    @RabbitListener(queues = "integra-to-anaf")
    public void receiveMessage(SalaryRequestMessage message) {
        int salary = anafService.getUserSalary(message.userId());
        System.out.println(salary);
        // TODO: send salary back to integra-to-anaf queue
    }
}
