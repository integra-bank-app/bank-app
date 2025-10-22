package clf.integra.backend.producer;


import clf.integra.backend.dto.SalaryRequestMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessageProducer messageProducer;

    @Test
    void testSend_validMessage_returnSuccess() {
        UUID id = UUID.randomUUID();
        SalaryRequestMessage message = new SalaryRequestMessage(id);

        messageProducer.send(message);

        verify(rabbitTemplate).convertAndSend(
                RabbitConfig.INTEGRA_TO_ANAF,
                message
        );
    }
}

