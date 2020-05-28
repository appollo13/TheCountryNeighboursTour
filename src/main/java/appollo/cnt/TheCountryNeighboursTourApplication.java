package appollo.cnt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TheCountryNeighboursTourApplication {

    public static void main(String[] args) {
        SpringApplication.run(TheCountryNeighboursTourApplication.class, args);
    }
}
