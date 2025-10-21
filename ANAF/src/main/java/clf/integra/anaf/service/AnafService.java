package clf.integra.anaf.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnafService {

    private static final Logger logger = LoggerFactory.getLogger(AnafService.class);

    private final RabbitTemplate rabbitTemplate;
    private final String queueName;

    public AnafService(RabbitTemplate rabbitTemplate, @Value("${queue.integraPay.name:integraPayQueue}") String queueName) {
        this.rabbitTemplate = rabbitTemplate;
        this.queueName = queueName;
    }

    public int getUserSalary(UUID id) {
        int salary = (int) (Math.random() * (250000 - 2500 + 1) + 2500);
        logger.info("User with id {} has {} salary", id, salary);

        String meesage = String.format("{\"userId\": \"%s\", \"salary\": %d}", id, salary);
        rabbitTemplate.convertAndSend(queueName, message);
        logger.info("Sent salary message for user {} to queue {}", id, queueName);
        return salary;
    }
}
