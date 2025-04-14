package com.openclassrooms.safetynetalerts.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.exception.PersonException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;
import com.openclassrooms.safetynetalerts.repository.PersonRepository;

@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	@Mock
	private PersonRepository personRepository;
	@Mock
	private FireStationRepository fireStationRepository;
	@Mock
	private MedicalRecordsService medicalRecordsService;

	@InjectMocks
	private PersonService personService;

	@BeforeEach
	public void setUp() {
		personService = new PersonService(medicalRecordsService, personRepository, fireStationRepository);
	}

	@Test
	void addPersonTest() throws PersonException {
		Person newPerson = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");
		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", null, null);
		FireStation fireStation = new FireStation("1 rue des fleurs", 1);

		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord));
		when(fireStationRepository.getAllFireStation()).thenReturn(List.of(fireStation));
		when(personRepository.addPerson(any(Person.class))).thenReturn(newPerson);

		Person result = personService.addPerson(newPerson);

		assertNotNull(result);
		assertEquals("Jean", result.getFirstName());
	}

	@Test
	void addPersonWithMissingFirstNameTest() {
		Person newPerson = new Person("", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");

		PersonException exception = assertThrows(PersonException.class, () -> personService.addPerson(newPerson));
		assertEquals("Le prénom et/ou le nom de la personne ne peuvent pas être vides.", exception.getMessage());
	}

	@Test
	void addPersonNoMedicalRecordTest() {
		Person newPerson = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");

		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of());
		assertThrows(RuntimeException.class, () -> personService.addPerson(newPerson));
	}

	@Test
	void updatePersonTest() throws ResourceNotFoundException {
		Person existingPerson = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");
		Person updatedPerson = new Person("Jean", "Martin", "15 rue des roses", "Paris", "75015", "0701020304",
				"jean.martin@email.com");

		when(personRepository.updatePerson("Jean", "Martin", updatedPerson)).thenReturn(updatedPerson);

		Person result = personService.updatePerson("Jean", "Martin", updatedPerson);

		assertNotNull(result);
		assertEquals("15 rue des roses", result.getAddress());
	}

	@Test
	void deletePersonTest() throws ResourceNotFoundException {
		Person personToDelete = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");

		when(personRepository.getAllPerson()).thenReturn(List.of(personToDelete));

		personService.deletePerson("Jean", "Martin");

		verify(personRepository).savePersonToJson(anyList());
	}

	@Test
	void deletePersonNotFoundTest() {
		when(personRepository.getAllPerson()).thenReturn(List.of());

		assertThrows(ResourceNotFoundException.class, () -> personService.deletePerson("Jean", "Martin"));
	}

	// TESTS DES URLS

	@Test
	void getChildrenByAddressTest() throws Exception {
		Person person1 = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");
		Person person2 = new Person("Pierre", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"pierre-dupont@email.com");

		MedicalRecords medicalRecord1 = new MedicalRecords("Jean", "Martin", "01/01/1990", null, null);
		MedicalRecords medicalRecord2 = new MedicalRecords("Pierre", "Martin", "03/05/2015", null, null);

		when(personRepository.getAllPerson()).thenReturn(List.of(person1, person2));
		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord1, medicalRecord2));

		List<ChildByAddressDTO> children = personService.getChildrenByAddress("1 rue des fleurs");

		assertNotNull(children);
		assertEquals(2, children.size());
	}

	@Test
	void getPersonInfoByLastNameTest() throws Exception {
		Person person = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");
		MedicalRecords medicalRecord = new MedicalRecords("Jean", "Martin", "01/01/1990", null, null);

		when(personRepository.getAllPerson()).thenReturn(List.of(person));
		when(medicalRecordsService.getAllMedicalRecords()).thenReturn(List.of(medicalRecord));

		List<PersonInfoLastNameDTO> personInfo = personService.getPersonInfoByLastName("Martin");

		assertNotNull(personInfo);
		assertEquals(1, personInfo.size());
	}

	@Test
	void getEmailOfAllPersonByCityTest() throws Exception {
		Person person = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean.martin@email.com");

		when(personRepository.getAllPerson()).thenReturn(List.of(person));

		EmailOfAllPersonDTO result = personService.getEmailOfAllPersonByCity("Paris");

		assertNotNull(result);
		assertEquals(person.getEmail(), "jean.martin@email.com");
	}

}
