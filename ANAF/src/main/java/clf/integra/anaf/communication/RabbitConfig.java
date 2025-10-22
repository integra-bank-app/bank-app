package clf.integra.anaf.communication;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {
    public static final String ANAF_TO_INTEGRA = "anaf-to-integra";
    public static final String INTEGRA_TO_ANAF = "integra-to-anaf";

    @Bean
    public Queue anafToIntegra() {
        return new Queue(ANAF_TO_INTEGRA, true);
    }

    @Bean
    public Queue integraToAnaf() {
        return new Queue(INTEGRA_TO_ANAF, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
