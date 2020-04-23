/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package quarano.department;

import lombok.RequiredArgsConstructor;
import quarano.core.DataInitializer;
import quarano.department.TrackedCase.TrackedCaseIdentifier;
import quarano.tracking.Quarantine;
import quarano.tracking.TrackedPersonDataInitializer;
import quarano.tracking.TrackedPersonRepository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Drotbohm
 */
@Component
@RequiredArgsConstructor
@Order(660)
public class TrackedCaseDataInitializer implements DataInitializer {

	private final TrackedCaseRepository cases;
	private final TrackedPersonRepository trackedPeople;
	private final DepartmentRepository departments;

	public static final TrackedCaseIdentifier TRACKED_CASE_SANDRA = TrackedCaseIdentifier
			.of(UUID.fromString("e55ce370-7dbe-11ea-bc55-0242ac134711"));

	/*
	 * (non-Javadoc)
	 * @see quarano.core.DataInitializer#initialize()
	 */
	@Override
	public void initialize() {

		var person1 = trackedPeople.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON1_ID_DEP1).orElseThrow();
		var person2 = trackedPeople.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON2_ID_DEP1).orElseThrow();
		var person3 = trackedPeople.findById(TrackedPersonDataInitializer.VALID_TRACKED_PERSON3_ID_DEP2).orElseThrow();

		var department1 = departments.findById(DepartmentDataInitializer.DEPARTMENT_ID_DEP1).orElseThrow();
		var department2 = departments.findById(DepartmentDataInitializer.DEPARTMENT_ID_DEP2).orElseThrow();

		cases.save(new TrackedCase(person1, department1) //
				.setTrackedPerson(person1) //
				.setType(CaseType.CONTACT));

		cases.save(new TrackedCase(person2, department1));

		LocalDate start = LocalDate.now().minusWeeks(1);
		LocalDate end = start.plusWeeks(4);

		cases.save(new TrackedCase(TRACKED_CASE_SANDRA, person3, department2) //
				.setQuarantine(Quarantine.of(start, end)) //
				.submitEnrollmentDetails() //
				.submitQuestionnaire(new InitialReport() //
						.setBelongToLaboratoryStaff(true) //
						.setBelongToMedicalStaff(true) //
						.setBelongToNursingStaff(true) //
						.setDirectContactWithLiquidsOfC19pat(false) //
						.setFamilyMember(true) //
						.setFlightCrewMemberWithC19Pat(false) //
						.setFlightPassengerCloseRowC19Pat(true) //
						.setMin15MinutesContactWithC19Pat(true) //
						.setNursingActionOnC19Pat(false) //
						.withoutSymptoms()) //
				.markEnrollmentCompleted(EnrollmentCompletion.WITHOUT_ENCOUNTERS));
	}
}
