package quarano.masterdata.web;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import quarano.account.DepartmentProperties;
import quarano.masterdata.EmailText;
import quarano.masterdata.FrontendText;

import java.util.Map;

import org.springframework.hateoas.server.core.Relation;
import org.springframework.stereotype.Component;

/**
 * @author Jens Kutzsche
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
class TextRepresentations {

	private final @NonNull DepartmentProperties departmentConfiguration;

	public TextDto toRepresentation(FrontendText entity, boolean rawText) {

		var placeholders = placeholderMap(rawText,
				Map.of("departmentName", departmentConfiguration.getDefaultDepartment().getName()));

		return new TextDto(entity.getTextKey(), entity.expand(placeholders));
	}

	public TextDto toRepresentation(EmailText entity) {
		return new TextDto(entity.getTextKey(), entity.expand(Map.of()));
	}

	private Map<String, String> placeholderMap(boolean rawText, Map<String, String> placeholder) {
		return rawText ? Map.of() : placeholder;
	}

	@Relation(collectionRelation = "texts")
	@Value
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	static class TextDto {
		String key, text;
	}
}
