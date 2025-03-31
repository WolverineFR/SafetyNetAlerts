package com.openclassrooms.safetynetalerts.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;


import jakarta.validation.Valid;

@RestController
public class PersonController {
	
	private PersonService personService;
	private static final Logger logger = LogManager.getLogger(PersonService.class);
	private final ObjectMapper objectMapper;
	
	@Autowired
	public PersonController (PersonService personService, ObjectMapper objectMapper) {
		this.personService = personService;
		this.objectMapper = objectMapper;
	}

	@GetMapping("/person/all")
	public List<Person> getAllPerson() throws Exception {
		return personService.getAllPerson();
	}

	@PostMapping("/person")
	public Person addNewPerson(@Valid @RequestBody Person newPerson) throws Exception {
		personService.addPerson(newPerson);
		String PersonJson = objectMapper.writeValueAsString(newPerson);
		logger.info("La personne est enregistrée avec succès ! : " + PersonJson);
		return newPerson;
	}

	@PutMapping("/person/{firstName}/{lastName}")
	public Person updatePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			@Valid @RequestBody Person updatePerson) throws Exception {
		personService.updatePerson(updatePerson);
		String PersonJson = objectMapper.writeValueAsString(updatePerson);
		logger.info("La personne a été modifiée avec succès ! : " + PersonJson);
		return updatePerson;
	}

	@DeleteMapping("/person/{firstName}/{lastName}")
	public Person deletePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			Person deletePerson) throws Exception {
		personService.deletePerson(deletePerson);
		String PersonJson = objectMapper.writeValueAsString(deletePerson);
		logger.info("Personne supprimé avec succès ! : " + PersonJson);
		return deletePerson;

	}
	
	@GetMapping("childAlert")
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		return personService.getChildrenByAddress(address);
	}
	
	@GetMapping("/personInfo")
	public List<PersonInfoLastNameDTO> getPersonInfoByLastName(@RequestParam String lastName) throws Exception {
		return personService.getPersonInfoByLastName(lastName);
		
	}
	
	@GetMapping("/communityEmail")
	public EmailOfAllPersonDTO getEmailOfAllPersonByCity(@RequestParam String city) throws Exception {
		return personService.getEmailOfAllPersonByCity(city);
		
	}

}
