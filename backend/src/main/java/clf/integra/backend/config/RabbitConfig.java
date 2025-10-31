package clf.integra.backend.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static clf.integra.QueueName.ANAF_TO_INTEGRA;
import static clf.integra.QueueName.INTEGRA_TO_ANAF;

@Configuration
public class RabbitConfig {

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

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
