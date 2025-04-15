package com.openclassrooms.safetynetalerts.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.JsonService;

@ExtendWith(MockitoExtension.class)
public class PersonRepositoryTest {

	@Mock
	private JsonService jsonService;

	@InjectMocks
	private PersonRepository personRepository;

	private Person person;

	@BeforeEach
	void setUp() {
		person = new Person("Jean", "Martin", "1 rue des fleurs", "Paris", "75001", "0601020304",
				"jean-martin@email.com");
	}

	@SuppressWarnings("unchecked")
	@Test
	void getAllPersons() {
		List<Person> personList = List.of(person);

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		List<Person> result = personRepository.getAllPerson();

		assertEquals("Jean", result.get(0).getFirstName());
		assertEquals("Martin", result.get(0).getLastName());

	}

	@SuppressWarnings("unchecked")
	@Test
	void addPersonTest() {
		List<Person> personList = new ArrayList<>();
		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		Person result = personRepository.addPerson(person);

		assertNotNull(result);
		assertEquals("Jean", result.getFirstName());
		assertEquals("Martin", result.getLastName());
		verify(jsonService, times(1)).writeJsonToFile(eq("persons"), anyList());
	}

	@SuppressWarnings("unchecked")
	@Test
	void updatePersonTest() {
		Person updatePerson = new Person("Jean", "Martin", "15 rue des roses", "Paris", "75015", "0601020304",
				"jean-michel@eamil.com");
		List<Person> personList = new ArrayList<>(List.of(person));

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		Person result = personRepository.updatePerson("Jean", "Martin", updatePerson);

		assertEquals("15 rue des roses", result.getAddress());
		assertEquals("75015", result.getZip());

		verify(jsonService, times(1)).writeJsonToFile(eq("persons"), anyList());

	}

	@SuppressWarnings("unchecked")
	@Test
	void updatePersonNotFoundTest() {
		List<Person> personList = List.of(); // Liste vide

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		assertThrows(ResourceNotFoundException.class, () -> {
			personRepository.updatePerson("Patrce", "Dufour", person);
		});

	}

	@SuppressWarnings("unchecked")
	@Test
	void deletePersonTest() {
		List<Person> personList = new ArrayList<>(List.of(person));

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		Person result = personRepository.deletePerson(person);

		assertEquals("Jean", result.getFirstName());
		assertEquals("Martin", result.getLastName());

		verify(jsonService, times(1)).writeJsonToFile(eq("persons"), anyList());
	}

	@SuppressWarnings("unchecked")
	@Test
	void deletePersonNotFoundTest() {
		List<Person> personList = List.of(); // liste vide

		when(jsonService.readJsonFromFile(any(TypeReference.class), eq("persons"))).thenReturn(personList);

		assertThrows(ResourceNotFoundException.class, () -> {
			personRepository.deletePerson(person);
		});
	}

}
