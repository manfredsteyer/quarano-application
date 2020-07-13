package de.quarano.sormas.client.invoker;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.quarano.sormas.client.api.CaseControllerApi;
import de.quarano.sormas.client.invoker.auth.Authentication;
import de.quarano.sormas.client.invoker.auth.HttpBasicAuth;

@Configuration
public class SormasIntegrationConfig {

	@Bean
	public CaseControllerApi petApi() {

		return new CaseControllerApi(apiClient());
	}

	@Bean
	public ApiClient apiClient() {

		ApiClient apiClient = new ApiClient();
		apiClient.setUsername("SurvOff");
		apiClient.setPassword("SurvOff");

		return apiClient;
	}
}
