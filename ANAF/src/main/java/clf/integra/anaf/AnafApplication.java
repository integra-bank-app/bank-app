package clf.integra.anaf;

import clf.integra.anaf.service.AnafService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class AnafApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnafApplication.class, args);
    }

    /*
        * Just for manual testing purposes
     */
    @Bean
    CommandLineRunner demo(AnafService anafService) {
        return args -> {
            UUID userId = UUID.randomUUID();
            anafService.getUserSalary(userId);
        };
    }
}
