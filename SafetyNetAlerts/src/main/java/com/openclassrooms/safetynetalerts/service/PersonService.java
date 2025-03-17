package com.openclassrooms.safetynetalerts.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.safetynetalerts.exception.PersonDoesntExistException;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.PersonRepository;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PersonService {
/*
	private List<Person> personList = new ArrayList<>();;

	// Test with fake Data
	@PostConstruct
	public void Data() {
		Person person1 = new Person(1, "John", "Boyd", "1509 Culver St", "Culver", "97451", "841-874-6512",
				"jaboyd@email.com");
		Person person2 = new Person(2, "Kevin", "Mall", "132 Culver St", "Yep", "56777", "841-874-6512",
				"kev@email.com");

		personList.addAll(Arrays.asList(person1, person2));
	}

	@Autowired
	private PersonRepository personRepository;

	public PersonService(PersonRepository personRepository) {
		this.personRepository = personRepository;
	}

	public Iterable<Person> getAllPerson() {
		return personRepository.findAll();
	}

	public Optional<Person> getPersonById(Integer id) {
		Optional<Person> person = personRepository.findById(id);
		if (person.isEmpty()) {
			throw new PersonDoesntExistException("The person with ID number " + id + " doesn't exist.");
		}
		return personRepository.findById(id);

	}

	/*
	 * public Iterable<Person> getPerson() { return personRepository.findAll(); }
	 * 
	 * public void deletePerson(final Long id) { personRepository.deleteById(id); }
	 * 
	 * public Person savePerson(Person person) { Person savedPerson =
	 * personRepository.save(person); return savedPerson; }
	 */
	
}
