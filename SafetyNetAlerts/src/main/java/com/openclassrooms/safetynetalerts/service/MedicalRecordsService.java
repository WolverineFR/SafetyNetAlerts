package com.openclassrooms.safetynetalerts.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.repository.MedicalRecordsRepository;

@Service
public class MedicalRecordsService {

	@Autowired
	private final MedicalRecordsRepository medicalRecordsRepository;

	public MedicalRecordsService(MedicalRecordsRepository medicalRecordsRepository) {
		this.medicalRecordsRepository = medicalRecordsRepository;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return medicalRecordsRepository.getAllMedicalRecords();
	}

	// Ajouter un MedicalRecord
	public MedicalRecords addMedicalRecord(MedicalRecords newMedicalRecords) throws Exception {
	return	medicalRecordsRepository.addMedicalRecord(newMedicalRecords);
	}

	// Mise à jour des données
	public MedicalRecords updateMedicalRecord(MedicalRecords updateMedicalRecord) throws Exception {
		return medicalRecordsRepository.updateMedicalRecord(updateMedicalRecord);
	}

	// Supression d'un medical record
	public MedicalRecords deleteMedicalRecord(MedicalRecords deleteMedicalRecord) throws Exception {
		return medicalRecordsRepository.deleteMedicalRecord(deleteMedicalRecord);
	}

	// Methode de calcule de l'age d'une personne
	public int calculateAge(MedicalRecords medicalRecords) {
		String birthDate = medicalRecords.getBirthDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyy");
		LocalDate birthLocalDate = LocalDate.parse(birthDate, formatter);

		return Period.between(birthLocalDate, LocalDate.now()).getYears();
	}

}
