package com.openclassrooms.safetynetalerts.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.FloodListOfStationNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
import com.openclassrooms.safetynetalerts.exception.FireStationException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.service.FireStationService;

import jakarta.validation.Valid;

/**
 * Contrôleur REST pour gérer les opérations liées aux casernes de pompiers.
 * Fournit des endpoints pour la création, la mise à jour, la suppression et la consultation
 * des casernes de pompiers, ainsi que des fonctionnalités spécifiques comme la couverture
 * d'une station, les numéros de téléphone des personnes couvertes, etc.
 */
@RestController
public class FireStationController {

	private FireStationService fireStationService;
	private static final Logger logger = LogManager.getLogger(FireStationService.class);
	private final ObjectMapper objectMapper;

	@Autowired
	public FireStationController(FireStationService fireStationService, ObjectMapper objectMapper) {
		this.fireStationService = fireStationService;
		this.objectMapper = objectMapper;
	}

	 /**
     * Récupère toutes les casernes de pompiers.
     *
     * @return la liste de toutes les FireStation
     */
	@GetMapping("/firestation/all")
	public ResponseEntity<List<FireStation>> getAllFireStation() {
		try {
			List<FireStation> allFireStations = fireStationService.getAllFireStation();
			return ResponseEntity.ok(allFireStations);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
     * Ajoute une nouvelle caserne de pompiers.
     *
     * @param newFireStation : la nouvelle FireStation à enregistrer
     * @return la caserne ajoutée
     */
	@PostMapping("/firestation")
	public ResponseEntity<FireStation> addNewFireStation(@Valid @RequestBody FireStation newFireStation) {
		try {
			FireStation addNewFS = fireStationService.addFireStation(newFireStation);
			String fireStationJson = objectMapper.writeValueAsString(newFireStation);
			logger.info("Caserne de pompier enregistrée avec succès ! : " + fireStationJson);

			return ResponseEntity.status(HttpStatus.CREATED).body(addNewFS);
		} catch (FireStationException e) {
			logger.error("Erreur lors de l'ajout de la caserne de pompier : {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue lors de l'ajout de la caserne de pompier : {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	 /**
     * Met à jour une caserne de pompiers existante à partir de son adresse.
     *
     * @param address : l'adresse de la caserne à mettre à jour
     * @param updateFireStation : les nouvelles données à appliquer
     * @return la caserne mise à jour
     */
	@PutMapping("/firestation/{address}")
	public ResponseEntity<FireStation> updateFireStation(@Valid @PathVariable String address,
			@Valid @RequestBody FireStation updateFireStation) {
		try {
			FireStation updateFS = fireStationService.updateFireStation(address, updateFireStation);
			String fireStationJson = objectMapper.writeValueAsString(updateFireStation);
			logger.info("Caserne de pompier modifiée avec succès ! : " + fireStationJson);
			return ResponseEntity.status(HttpStatus.OK).body(updateFS);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur lors de la modification de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (IllegalArgumentException e) {

			logger.error("Erreur de correspondance des données : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la modification de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
     * Supprime une caserne de pompiers par son adresse et numéro de station.
     *
     * @param address : l'adresse de la caserne
     * @param station : le numéro de la station
     * @return un statut HTTP indiquant le succès ou l'échec de l'opération
     */
	@DeleteMapping("/firestation/{address}/{station}")
	public ResponseEntity<Void> deleteFireStation(@PathVariable String address, @PathVariable int station,
			FireStation deleteFireStation) {
		try {
			fireStationService.deleteFireStation(address, station);
			logger.info("Caserne de pompier supprimée avec succès ! Adresse : {}, Station numéro : {}", address,
					station);

			return ResponseEntity.noContent().build();
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur lors de la suppression de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la suppression de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	// URLs
	
	/**
     * Récupère les personnes couvertes par une station de pompiers spécifique.
     *
     * @param stationNumber : le numéro de la station
     * @return un objet FireStationCoverageDTO contenant les personnes et statistiques
     */
	@GetMapping("/firestation")
	public ResponseEntity<FireStationCoverageDTO> getPersonsByStationNumber(@RequestParam int stationNumber) {
		try {
			FireStationCoverageDTO result = fireStationService.getPersonsByStationNumber(stationNumber);
			return ResponseEntity.ok(result);
		} catch (ResourceNotFoundException e) {
			logger.error("Aucune caserne trouvée pour le numéro : {}", stationNumber);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue sur /firestation : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	 /**
     * Récupère les numéros de téléphone des personnes couvertes par une station de pompiers.
     *
     * @param firestation : le numéro de la station
     * @return un objet FireStationCoveragePhoneNumberDTO contenant les numéros de téléphone
     */
	@GetMapping("/phoneAlert")
	public ResponseEntity<FireStationCoveragePhoneNumberDTO> getPhoneNumberByStationNumber(
			@RequestParam int firestation) {
		try {
			FireStationCoveragePhoneNumberDTO result = fireStationService.getPhoneNumberByStationNumber(firestation);
			return ResponseEntity.ok(result);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur 404 : {}", firestation);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue sur /phoneAlert : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
     * Récupère les informations des personnes vivant à une adresse donnée.
     *
     * @param address : l'adresse recherchée
     * @return la liste des PersonByAddressDTO
     */
	@GetMapping("/fire")
	public ResponseEntity<List<PersonByAddressDTO>> getPersonByAddress(@RequestParam String address) {
		try {
			List<PersonByAddressDTO> result = fireStationService.getPersonByAddress(address);
			return ResponseEntity.ok(result);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur 404 : {}", address);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue sur /fire : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	 /**
     * Récupère les informations des personnes couvertes par une stations.
     *
     * @param firestation : le numéro de la station
     * @return une liste de FloodListOfStationNumberDTO
     */
	@GetMapping("/flood/station")
	public ResponseEntity<List<FloodListOfStationNumberDTO>> getPersonByListOfStationNumber(
			@RequestParam int firestation) {
		try {
			List<FloodListOfStationNumberDTO> result = fireStationService.getPersonByListOfStationNumber(firestation);
			return ResponseEntity.ok(result);
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur 404 : {}", firestation);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (Exception e) {
			logger.error("Erreur inattendue sur /flood/station : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
