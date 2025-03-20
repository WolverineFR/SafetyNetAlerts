package com.openclassrooms.safetynetalerts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

import ch.qos.logback.core.model.Model;
import jakarta.validation.Valid;

@RestController
public class MedicalRecordsController {

	@Autowired
	private MedicalRecordsService medicalRecordsService;

	@GetMapping("/medicalrecord")
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return medicalRecordsService.getAllMedicalRecords();
	}

	@PostMapping("medicalrecord")
	public ResponseEntity<String> addNewMedicalrecord(@Valid @RequestBody MedicalRecords newMedicalRecords) {

		try {
			medicalRecordsService.addMedicalRecord(newMedicalRecords);
			return ResponseEntity.status(HttpStatus.CREATED).body("Rapport medical enregistré avec succès !");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erreur lors de l'ajout du rapport médical: " + e.getMessage());
		}

	}

	@PutMapping("/medicalrecord/{firstName}/{lastName}")
	public ResponseEntity<String> updateMedicalRecord (@PathVariable String firstName, @PathVariable String lastName, MedicalRecords updateMedicalRecord) {
		try {
			medicalRecordsService.updateMedicalrecords(updateMedicalRecord);
			return ResponseEntity.status(HttpStatus.OK).body("Modification enregistré avec succeès !");
			
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la modification: " + e.getMessage());
		}
		
		
	}
	
}
