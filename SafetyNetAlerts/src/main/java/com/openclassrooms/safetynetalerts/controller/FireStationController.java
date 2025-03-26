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
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.PersonFireStationDTO;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.service.FireStationService;
import com.openclassrooms.safetynetalerts.service.PersonService;

import jakarta.validation.Valid;

@RestController
public class FireStationController {

	@Autowired
	private FireStationService fireStationService;
	private PersonFireStationDTO fireStationNumberDTO;
	private PersonService personService;


	private static final Logger logger = LogManager.getLogger(FireStationService.class);
	Gson gson = new Gson();

	@GetMapping("/firestation/all")
	public List<FireStation> getAllFireStation() throws Exception {
		return fireStationService.getAllFireStation();
	}

	@PostMapping("/firestation")
	public FireStation addNewFireStation(@Valid @RequestBody FireStation newFireStation) throws Exception {
		fireStationService.addFireStation(newFireStation);
		logger.info("Caserne de pompier enregistrée avec succès ! : " + gson.toJson(newFireStation));
		return newFireStation;
	}

	@PutMapping("/firestation/{address}")
	public FireStation updateFireStation(@Valid @PathVariable String address,
			@Valid @RequestBody FireStation updateFireStation) throws Exception {
		fireStationService.updateFireStation(updateFireStation);
		logger.info("Caserne de pompier modifiée avec succès ! : " + gson.toJson(updateFireStation));
		return updateFireStation;
	}

	@DeleteMapping("/firestation/{address}/{station}")
	public FireStation deleteFireStation(@Valid @PathVariable String address, @PathVariable int station,
			FireStation deleteFireStation) throws Exception {
		fireStationService.deleteFireStation(deleteFireStation);
		logger.info("Caserne de pompier supprimée avec succès ! : " + gson.toJson(deleteFireStation));
		return deleteFireStation;

	}
	
	@GetMapping("/firestation")
    public FireStationCoverageDTO getPersonsByStationNumber(@RequestParam int stationNumber) throws Exception {
        return fireStationService.getPersonsByStationNumber(stationNumber);
    }

}
