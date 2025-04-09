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
import com.openclassrooms.safetynetalerts.exception.MedicalRecordException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
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
	public ResponseEntity<List<MedicalRecords>> getAllMedicalRecords() {
		try {
			List<MedicalRecords> allMedicalRecords = medicalRecordsService.getAllMedicalRecords();

			return ResponseEntity.ok(allMedicalRecords);
		} catch (Exception e) {
			logger.error("Erreur lors de la récupération des dossiers médicaux : {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PostMapping("/medicalrecord")
	public ResponseEntity<MedicalRecords> addNewMedicalrecord(@Valid @RequestBody MedicalRecords newMedicalRecords) {
		try {
			MedicalRecords newMedicalRecord = medicalRecordsService.addMedicalRecord(newMedicalRecords);
			String medicalRecordsJson = objectMapper.writeValueAsString(newMedicalRecords);
			logger.info("Rapport médical enregistré avec succès : {}", medicalRecordsJson);

			return ResponseEntity.status(HttpStatus.CREATED).body(newMedicalRecord);

		} catch (MedicalRecordException e) {
			logger.error("Erreur lors de l'ajout du dossier médical : {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de l'ajout du dossier médical : {}", e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@PutMapping("/medicalrecord/{firstName}/{lastName}")
	public ResponseEntity<MedicalRecords> updateMedicalRecord(@PathVariable String firstName,
			@PathVariable String lastName, @Valid @RequestBody MedicalRecords updateMedicalRecord) {
		try {
			MedicalRecords updatedMr = medicalRecordsService.updateMedicalRecord(firstName, lastName,
					updateMedicalRecord);
			String medicalRecordsJson = objectMapper.writeValueAsString(updatedMr);
			logger.info("Rapport médical modifié avec succès : {}", medicalRecordsJson);
			return ResponseEntity.status(HttpStatus.OK).body(updatedMr);

		} catch (ResourceNotFoundException e) {

			logger.error("Erreur lors de la modification du dossier médical : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

		} catch (IllegalArgumentException e) {

			logger.error("Erreur de correspondance des données : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la modification du dossier médical : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@DeleteMapping("/medicalrecord/{firstName}/{lastName}")
	public ResponseEntity<Void> deleteMedicalRecord(@PathVariable String firstName, @PathVariable String lastName) {
		try {
			medicalRecordsService.deleteMedicalRecord(firstName, lastName);
			logger.info("Rapport médical supprimé avec succès : {} {}", firstName, lastName);
			return ResponseEntity.noContent().build();

		} catch (ResourceNotFoundException e) {

			logger.error("Erreur lors de la suppression du dossier médical : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

		} catch (Exception e) {
			logger.error("Erreur inattendue lors de la suppression du dossier médical : {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
