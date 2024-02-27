package pcrc.alcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity(debug = true)
@SpringBootApplication
public class AlchaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlchaApplication.class, args);
    }

}
