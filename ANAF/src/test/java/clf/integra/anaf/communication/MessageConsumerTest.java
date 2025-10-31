package clf.integra.anaf.communication;

import clf.integra.SalaryRequestMessage;
import clf.integra.anaf.service.AnafService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MessageConsumerTest {

    @Mock
    private AnafService anafService;

    @InjectMocks
    private MessageConsumer messageConsumer;

    @Test
    void testReceiveMessage_validMessage_callsAnafService() {
        UUID userId = UUID.randomUUID();
        SalaryRequestMessage message = new SalaryRequestMessage(userId);
        int expectedSalary = 5000;

        when(anafService.getUserSalary(userId)).thenReturn(expectedSalary);

        messageConsumer.receiveMessage(message);

        verify(anafService).getUserSalary(userId);
    }

    @Test
    void testReceiveMessage_multipleMessages_processesEach() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        SalaryRequestMessage message1 = new SalaryRequestMessage(userId1);
        SalaryRequestMessage message2 = new SalaryRequestMessage(userId2);

        when(anafService.getUserSalary(userId1)).thenReturn(3000);
        when(anafService.getUserSalary(userId2)).thenReturn(4000);

        messageConsumer.receiveMessage(message1);
        messageConsumer.receiveMessage(message2);

        verify(anafService).getUserSalary(userId1);
        verify(anafService).getUserSalary(userId2);
    }
}
