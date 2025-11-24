package apa.ifpb.edu.br.APA;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ApaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApaApplication.class, args);
	}

}
