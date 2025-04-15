package com.openclassrooms.safetynetalerts.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.openclassrooms.safetynetalerts.exception.PersonException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class PersonController {

	private PersonService personService;
	private static final Logger logger = LogManager.getLogger(PersonService.class);
	private final ObjectMapper objectMapper;

	@Autowired
	public PersonController(PersonService personService, ObjectMapper objectMapper) {
		this.personService = personService;
		this.objectMapper = objectMapper;
	}

	@GetMapping("/person/all")
	public ResponseEntity<List<Person>> getAllPerson() {
		try {
			List<Person> allPersons = personService.getAllPerson();
			return ResponseEntity.ok(allPersons);
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des personnes : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/person")
	public ResponseEntity<Person> addNewPerson(@Valid @RequestBody Person newPerson) {
		try {
			Person addnewPerson = personService.addPerson(newPerson);
			String PersonJson = objectMapper.writeValueAsString(addnewPerson);
			logger.info("La personne est enregistrée avec succès ! : " + PersonJson);
			return ResponseEntity.status(HttpStatus.CREATED).body(addnewPerson);
		} catch (PersonException e) {
			logger.error("Erreur lors de l'ajout de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue lors de l'ajout de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PutMapping("/person/{firstName}/{lastName}")
	public ResponseEntity<Person> updatePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			@Valid @RequestBody Person updatePerson) {
		try {
			Person updateP = personService.updatePerson(firstName, lastName, updatePerson);
			String PersonJson = objectMapper.writeValueAsString(updateP);
			logger.info("La personne a été modifiée avec succès ! : " + PersonJson);
			return ResponseEntity.status(HttpStatus.OK).body(updateP);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur lors de la modification des données de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (IllegalArgumentException e) {
			logger.error("Erreur de correspondance des données : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la modification des données de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping("/person/{firstName}/{lastName}")
	public ResponseEntity<Void> deletePerson(@Valid @PathVariable String firstName, @PathVariable String lastName) {
		try {
			personService.deletePerson(firstName, lastName);
			logger.info("Personne supprimé avec succès ! : {} {}", firstName, lastName);
			return ResponseEntity.noContent().build();
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur lors de la suppression de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la suppression de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	// URL

	@GetMapping("childAlert")
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		return personService.getChildrenByAddress(address);
	}

	@GetMapping("/personInfo")
	public ResponseEntity<List<PersonInfoLastNameDTO>> getPersonInfoByLastName(@RequestParam String lastName) {
		try {
			List<PersonInfoLastNameDTO> personInfoList = personService.getPersonInfoByLastName(lastName);
			logger.info("Infos récupérées avec succès pour les personnes portant le nom : {}", lastName);
			return ResponseEntity.ok(personInfoList);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur 404 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue dans /personInfo : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/communityEmail")
	public ResponseEntity<EmailOfAllPersonDTO> getEmailOfAllPersonByCity(@RequestParam String city) {
		try {
			EmailOfAllPersonDTO emailList = personService.getEmailOfAllPersonByCity(city);
			logger.info("Liste des emails récupérée avec succès pour la ville : {}", city);
			return ResponseEntity.ok(emailList);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur 404 : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue dans /communityEmail : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
