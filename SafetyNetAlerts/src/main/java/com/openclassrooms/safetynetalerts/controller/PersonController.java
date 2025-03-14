package com.openclassrooms.safetynetalerts.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;

@RestController
public class PersonController {

	private final PersonService personService;

	public PersonController(PersonService personService) {
		this.personService = personService;
	}

	@GetMapping("/persons")
	public Iterable<Person> getAllPerson() {
		return personService.getAllPerson();
	}

	@GetMapping("/person/{id}")
	public Optional<Person> getPersonById(@PathVariable int id) {
		return personService.getPersonById(id);
	}
}
