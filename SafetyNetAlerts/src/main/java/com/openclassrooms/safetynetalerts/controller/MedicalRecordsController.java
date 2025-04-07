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
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

import jakarta.validation.Valid;

@RestController
public class MedicalRecordsController {

	private MedicalRecordsService medicalRecordsService;
	private static final Logger logger = LogManager.getLogger(MedicalRecordsService.class);
	private final ObjectMapper objectMapper;

	@Autowired
	public MedicalRecordsController(MedicalRecordsService medicalRecordsService, ObjectMapper objectMapper) {
		this.medicalRecordsService = medicalRecordsService;
		this.objectMapper = objectMapper;
	}

	@GetMapping("/medicalrecord/all")
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return medicalRecordsService.getAllMedicalRecords();
	}

	@PostMapping("/medicalrecord")
	public ResponseEntity<MedicalRecords> addNewMedicalrecord(@Valid @RequestBody MedicalRecords newMedicalRecords)
			throws Exception {
		MedicalRecords newMedicalRecord = medicalRecordsService.addMedicalRecord(newMedicalRecords);
		String medicalRecordsJson = objectMapper.writeValueAsString(newMedicalRecords);
		logger.info("Rapport medical enregistré avec succès ! : " + medicalRecordsJson);
		return ResponseEntity.status(HttpStatus.CREATED).body(newMedicalRecord);
	}

	@PutMapping("/medicalrecord/{firstName}/{lastName}")
	public ResponseEntity<MedicalRecords> updateMedicalRecord(@Valid @PathVariable String firstName,
			@PathVariable String lastName, @Valid @RequestBody MedicalRecords updateMedicalRecord) throws Exception {
		MedicalRecords updateMr = medicalRecordsService.updateMedicalRecord(firstName, lastName, updateMedicalRecord);
		String medicalRecordsJson = objectMapper.writeValueAsString(updateMedicalRecord);
		logger.info("Rapport medical modifié avec succès ! : " + medicalRecordsJson);
		return ResponseEntity.status(HttpStatus.OK).body(updateMr);
	}

	@DeleteMapping("/medicalrecord/{firstName}/{lastName}")
	public ResponseEntity<MedicalRecords> deleteMedicalRecord(@Valid @PathVariable String firstName,
			@PathVariable String lastName, MedicalRecords deleteMedicalRecord) throws Exception {
		medicalRecordsService.deleteMedicalRecord(firstName, lastName);
		String medicalRecordsJson = objectMapper.writeValueAsString(deleteMedicalRecord);
		logger.info("Rapport medical supprimé avec succès ! : " + medicalRecordsJson);
		return ResponseEntity.noContent().build();

	}

}
