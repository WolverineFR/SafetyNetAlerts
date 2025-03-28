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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;


import jakarta.validation.Valid;

@RestController
public class PersonController {
	
	@Autowired
	private PersonService PersonService;

	private static final Logger logger = LogManager.getLogger(PersonService.class);
	Gson gson = new Gson();

	@GetMapping("/person/all")
	public List<Person> getAllPerson() throws Exception {
		return PersonService.getAllPerson();
	}

	@PostMapping("/person")
	public Person addNewPerson(@Valid @RequestBody Person newPerson) throws Exception {
		PersonService.addPerson(newPerson);
		logger.info("La personne est enregistrée avec succès ! : " + gson.toJson(newPerson));
		return newPerson;
	}

	@PutMapping("/person/{firstName}/{lastName}")
	public Person updatePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			@Valid @RequestBody Person updatePerson) throws Exception {
		PersonService.updatePerson(updatePerson);
		logger.info("La personne a été modifiée avec succès ! : " + gson.toJson(updatePerson));
		return updatePerson;
	}

	@DeleteMapping("/person/{firstName}/{lastName}")
	public Person deletePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			Person deletePerson) throws Exception {
		PersonService.deletePerson(deletePerson);
		logger.info("Personne supprimé avec succès ! : " + gson.toJson(deletePerson));
		return deletePerson;

	}
	
	@GetMapping("childAlert")
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		return PersonService.getChildrenByAddress(address);
	}
	
	@GetMapping("/personInfo")
	public List<PersonInfoLastNameDTO> getPersonInfoByLastName(@RequestParam String lastName) throws Exception {
		return PersonService.getPersonInfoByLastName(lastName);
		
	}
	
	@GetMapping("/communityEmail")
	public EmailOfAllPersonDTO getEmailOfAllPersonByCity(@RequestParam String city) throws Exception {
		return PersonService.getEmailOfAllPersonByCity(city);
		
	}

}
