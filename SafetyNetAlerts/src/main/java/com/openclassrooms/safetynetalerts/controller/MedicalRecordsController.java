package com.openclassrooms.safetynetalerts.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.MedicalRecordsService;

@RestController
public class MedicalRecordsController {

	@Autowired
	private MedicalRecordsService medicalRecordsService;
	
	@GetMapping("/medicalrecord")
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return medicalRecordsService.getAllMedicalRecords();
	}
	
	@PostMapping("medicalrecord")
	public MedicalRecords saveNewMedicalrecord() throws Exception {
		return medicalRecordsService.addMedicalRecord();
	}
}
