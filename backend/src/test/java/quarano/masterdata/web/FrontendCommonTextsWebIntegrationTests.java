package quarano.masterdata.web;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import net.minidev.json.JSONArray;
import quarano.AbstractDocumentation;
import quarano.QuaranoWebIntegrationTest;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;

import com.jayway.jsonpath.JsonPath;

@QuaranoWebIntegrationTest
@SpringBootTest(properties = { "quarano.department.default-department.rkiCode=xx" })
class FrontendCommonTextsWebIntegrationTests extends AbstractDocumentation {

	@Test
	void getOneText() throws Exception {

		var result = mvc.perform(get("/frontendtexts")
				.contentType(MediaType.APPLICATION_JSON)
				.param("key", "welcome-index"))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		assertThatResultContains(result, "Sie haben vom GA Mannheim", "welcome-index");
	}

	@Test
	void getOneTextEn() throws Exception {

		var result = mvc.perform(get("/frontendtexts")
				.contentType(MediaType.APPLICATION_JSON)
				.locale(Locale.UK)
				.param("key", "welcome-index"))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		assertThatResultContains(result, "Your GA Mannheim", "welcome-index");
	}

	@Test
	void getOneTextWithExplicitEnAndRawtext() throws Exception {

		var result = mvc.perform(get("/frontendtexts")
				.contentType(MediaType.APPLICATION_JSON)
				.param("key", "welcome-index")
				.param("lang", Locale.ENGLISH.toLanguageTag())
				.param("rawtext", "true"))
				.andExpect(status().is2xxSuccessful())
				.andReturn().getResponse().getContentAsString();

		assertThatResultContains(result, "Your {departmentName}", "welcome-index");
	}

	void assertThatResultContains(String result, String excampleString, Object... keys) {

		var document = JsonPath.parse(result);
		var read = document.read("$._embedded.texts", JSONArray.class);

		assertThat(read).extracting("key").contains(keys);
		assertThat(read).extracting("text", String.class)
				.noneMatch(ObjectUtils::isEmpty)
				.anyMatch(it -> it.contains(excampleString));
	}
}
