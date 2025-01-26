package comprestigebankv4;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Prestige Bank App",
				description = "Backend REST API for Prestige Bank",
				version= "v4.0",
				contact = @Contact(
						name ="Murad Mammadli",
						email = "muraga07@mail.ru",
						url = "https://github.com/P3rturbat0r"
				)
		)
)

public class Prestigebankv4Application {

	public static void main(String[] args) {
		SpringApplication.run(Prestigebankv4Application.class, args);
	}

}
