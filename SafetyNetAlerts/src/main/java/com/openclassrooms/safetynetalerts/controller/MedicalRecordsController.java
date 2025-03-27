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
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

import jakarta.validation.Valid;

@RestController
public class MedicalRecordsController {

	@Autowired
	private MedicalRecordsService medicalRecordsService;

	private static final Logger logger = LogManager.getLogger(MedicalRecordsService.class);
	Gson gson = new Gson();

	@GetMapping("/medicalrecord/all")
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return medicalRecordsService.getAllMedicalRecords();
	}

	@PostMapping("/medicalrecord")
	public MedicalRecords addNewMedicalrecord(@Valid @RequestBody MedicalRecords newMedicalRecords) throws Exception {
		medicalRecordsService.addMedicalRecord(newMedicalRecords);
		logger.info("Rapport medical enregistré avec succès ! : " + gson.toJson(newMedicalRecords));
		return newMedicalRecords;
	}

	@PutMapping("/medicalrecord/{firstName}/{lastName}")
	public MedicalRecords updateMedicalRecord(@Valid @PathVariable String firstName, @PathVariable String lastName,
			@Valid @RequestBody MedicalRecords updateMedicalRecord) throws Exception {
		medicalRecordsService.updateMedicalRecord(updateMedicalRecord);
		logger.info("Rapport medical modifié avec succès ! : " + gson.toJson(updateMedicalRecord));
		return updateMedicalRecord;
	}

	@DeleteMapping("/medicalrecord/{firstName}/{lastName}")
	public MedicalRecords deleteMedicalRecord(@Valid @PathVariable String firstName, @PathVariable String lastName,
			MedicalRecords deleteMedicalRecord) throws Exception {
		medicalRecordsService.deleteMedicalRecord(deleteMedicalRecord);
		logger.info("Rapport medical supprimé avec succès ! : " + gson.toJson(deleteMedicalRecord));
		return deleteMedicalRecord;

	}

}
