package quarano;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.quarano.sormas.client.api.CaseControllerApi;
import de.quarano.sormas.client.invoker.SormasIntegrationConfig;

@SpringBootApplication
@Import(SormasIntegrationConfig.class)
public class SormasTest {

	public static void main(String[] args) {
		SpringApplication.run(SormasTest.class, args);
	}

	@Autowired
	private CaseControllerApi petApi;
	
	
	@Bean
	@Primary
	PasswordEncoder getPasswordEncoder2() {
	    List<String> allUuids1 = petApi.getAllUuids1();
	    
	    return new BCryptPasswordEncoder();
	}
}
