package kg.neobis.smarttailor;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRabbit
@EnableCaching
public class SmartTailorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTailorApplication.class, args);
    }

}
