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

	@GetMapping("/firestation/all")
	public ResponseEntity<List<FireStation>> getAllFireStation() {
		try {
			List<FireStation> allFireStations = fireStationService.getAllFireStation();
			return ResponseEntity.ok(allFireStations);
		} catch (Exception e){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

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

	@PutMapping("/firestation/{address}")
	public ResponseEntity<FireStation> updateFireStation(@Valid @PathVariable String address,@Valid @PathVariable int station,
			@Valid @RequestBody FireStation updateFireStation) {
		try {
			FireStation updateFS = fireStationService.updateFireStation(address, station, updateFireStation);
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

	@DeleteMapping("/firestation/{address}/{station}")
	public ResponseEntity<Void> deleteFireStation( @PathVariable String address, @PathVariable int station,
			FireStation deleteFireStation) {
		try {
			fireStationService.deleteFireStation(address, station);
			logger.info("Caserne de pompier supprimée avec succès ! : à l'adresse : {} station numéro : {}",address, station);
			
			return ResponseEntity.noContent().build();
		} catch (ResourceNotFoundException e) {
			logger.error("Erreur lors de la suppression de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la suppression de la caserne de pompier : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}

	// URL
	@GetMapping("/firestation")
	public FireStationCoverageDTO getPersonsByStationNumber(@RequestParam int stationNumber) throws Exception {
		return fireStationService.getPersonsByStationNumber(stationNumber);
	}

	@GetMapping("/phoneAlert")
	public FireStationCoveragePhoneNumberDTO getPhoneNumberByStationNumber(@RequestParam int firestation)
			throws Exception {
		return fireStationService.getPhoneNumberByStationNumber(firestation);
	}

	@GetMapping("fire")
	public List<PersonByAddressDTO> getPersonByAddress(@RequestParam String address) throws Exception {
		return fireStationService.getPersonByAddress(address);
	}

	@GetMapping("flood/station")
	public List<FloodListOfStationNumberDTO> getPersonByListOfStationNumber(@RequestParam int firestation)
			throws Exception {
		return fireStationService.getPersonByListOfStationNumber(firestation);
	}

}
