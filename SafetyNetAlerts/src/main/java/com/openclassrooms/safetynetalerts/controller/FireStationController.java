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
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.FloodListOfStationNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
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
	public List<FireStation> getAllFireStation() throws Exception {
		return fireStationService.getAllFireStation();
	}

	@PostMapping("/firestation")
	public FireStation addNewFireStation(@Valid @RequestBody FireStation newFireStation) throws Exception {
		fireStationService.addFireStation(newFireStation);
		String fireStationJson = objectMapper.writeValueAsString(newFireStation);
		logger.info("Caserne de pompier enregistrée avec succès ! : " + fireStationJson);
		return newFireStation;
	}

	@PutMapping("/firestation/{address}")
	public FireStation updateFireStation(@Valid @PathVariable String address,
			@Valid @RequestBody FireStation updateFireStation) throws Exception {
		fireStationService.updateFireStation(updateFireStation);
		String fireStationJson = objectMapper.writeValueAsString(updateFireStation);
		logger.info("Caserne de pompier modifiée avec succès ! : " + fireStationJson);
		return updateFireStation;
	}

	@DeleteMapping("/firestation/{address}/{station}")
	public FireStation deleteFireStation(@Valid @PathVariable String address, @PathVariable int station,
			FireStation deleteFireStation) throws Exception {
		fireStationService.deleteFireStation(deleteFireStation);
		String fireStationJson = objectMapper.writeValueAsString(deleteFireStation);
		logger.info("Caserne de pompier supprimée avec succès ! : " + fireStationJson);
		return deleteFireStation;

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
