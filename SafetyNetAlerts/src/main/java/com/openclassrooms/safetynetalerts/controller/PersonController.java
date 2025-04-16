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


/**
 * Contrôleur REST pour gérer les opérations liées aux personnes.
 */
@RestController
public class PersonController {

	private PersonService personService;
	private static final Logger logger = LogManager.getLogger(PersonService.class);
	private final ObjectMapper objectMapper;

	/**
	 * Constructeur avec injection des dépendances.
	 *
	 * @param personService service de gestion des personnes
	 * @param objectMapper  outil de sérialisation JSON
	 */
	@Autowired
	public PersonController(PersonService personService, ObjectMapper objectMapper) {
		this.personService = personService;
		this.objectMapper = objectMapper;
	}

	/**
	 * Récupère toutes les personnes enregistrées.
	 *
	 * @return une réponse HTTP contenant la liste des personnes
	 */
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

	/**
	 * Ajoute une nouvelle personne.
	 *
	 * @param newPerson l'objet Person à ajouter
	 * @return une réponse HTTP contenant la personne créée
	 */
	@PostMapping("/person")
	public ResponseEntity<Person> addNewPerson(@Valid @RequestBody Person newPerson) {
		try {
			Person addnewPerson = personService.addPerson(newPerson);
			String personJson = objectMapper.writeValueAsString(addnewPerson);
			logger.info("La personne est enregistrée avec succès ! : " + personJson);
			return ResponseEntity.status(HttpStatus.CREATED).body(addnewPerson);
		} catch (PersonException e) {
			logger.error("Erreur lors de l'ajout de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue lors de l'ajout de la personne : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * Met à jour les informations d'une personne existante.
	 *
	 * @param firstName     prénom de la personne à mettre à jour
	 * @param lastName      nom de la personne à mettre à jour
	 * @param updatePerson  les nouvelles données de la personne
	 * @return une réponse HTTP contenant la personne modifiée
	 */
	@PutMapping("/person/{firstName}/{lastName}")
	public ResponseEntity<Person> updatePerson(@Valid @PathVariable String firstName, @PathVariable String lastName,
			@Valid @RequestBody Person updatePerson) {
		try {
			Person updateP = personService.updatePerson(firstName, lastName, updatePerson);
			String personJson = objectMapper.writeValueAsString(updateP);
			logger.info("La personne a été modifiée avec succès ! : " + personJson);
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

	/**
	 * Supprime une personne par prénom et nom.
	 *
	 * @param firstName prénom de la personne
	 * @param lastName  nom de la personne
	 * @return une réponse HTTP vide si la suppression est réussie
	 */
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

	// URLs

	/**
	 * Récupère les enfants d'une adresse donnée.
	 *
	 * @param address : l'adresse à rechercher
	 * @return une liste d'enfants avec leur foyer
	 * @throws Exception en cas d'erreur lors du traitement
	 */
	@GetMapping("childAlert")
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		return personService.getChildrenByAddress(address);
	}
	
	/**
	 * Récupère les informations détaillées des personnes portant un nom de famille donné.
	 *
	 * @param lastName : le nom de famille à rechercher
	 * @return une réponse HTTP contenant une liste d'informations des personnes
	 */
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

	/**
	 * Récupère tous les emails des personnes vivant dans une ville donnée.
	 *
	 * @param city : la ville ciblée
	 * @return une réponse HTTP contenant la liste des emails
	 */
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
