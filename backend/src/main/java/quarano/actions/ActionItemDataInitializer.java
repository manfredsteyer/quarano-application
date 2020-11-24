package quarano.actions;

import lombok.RequiredArgsConstructor;
import quarano.actions.ActionItem.ItemType;
import quarano.core.DataInitializer;
import quarano.department.CaseType;
import quarano.diary.DiaryManagement;
import quarano.diary.Slot;
import quarano.tracking.BodyTemperature;
import quarano.tracking.TrackedPerson;
import quarano.tracking.TrackedPersonDataInitializer;
import quarano.tracking.TrackedPersonRepository;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Drotbohm
 */
@Order(800)
@Component
@RequiredArgsConstructor
public class ActionItemDataInitializer implements DataInitializer {

	private final TrackedPersonRepository people;
	private final DiaryManagement diaries;
	private final ActionItemRepository items;
	private final AnomaliesProperties config;

	/*
	 * (non-Javadoc)
	 * @see quarano.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		TrackedPerson sandra = people.findRequiredById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON3_ID_DEP2);
		TrackedPerson jessica = people.findRequiredById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON4_ID_DEP2);
		TrackedPerson gustav = people.findRequiredById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON4_ID_DEP1);
		TrackedPerson nadine = people.findRequiredById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON5_ID_DEP1);

		items.save(new DiaryEntryMissingActionItem(sandra.getId(), Slot.now().previous()));
		items.save(new DiaryEntryMissingActionItem(jessica.getId(), Slot.now().previous()));
		items.save(new DiaryEntryActionItem(gustav.getId(), diaries.findDiaryFor(gustav).iterator().next(),
				ItemType.MEDICAL_INCIDENT,
				Description.forIncreasedTemperature(BodyTemperature.of(40.1f),
						config.getTemperatureThreshold(CaseType.CONTACT))));
		items.save(new DiaryEntryActionItem(nadine.getId(), diaries.findDiaryFor(nadine).iterator().next(),
				ItemType.MEDICAL_INCIDENT,
				Description.forIncreasedTemperature(BodyTemperature.of(40.1f),
						config.getTemperatureThreshold(CaseType.CONTACT))));
	}
}
