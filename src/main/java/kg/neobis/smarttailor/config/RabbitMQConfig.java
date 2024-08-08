package kg.neobis.smarttailor.config;


import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${rabbitmq.host}")
    String host;
    @Bean
    public ConnectionFactory connectionFactory() {
      ConnectionFactory connectionFactory = new ConnectionFactory();
      connectionFactory.setHost(host);
      return connectionFactory;
    }


}