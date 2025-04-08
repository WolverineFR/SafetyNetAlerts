package com.openclassrooms.safetynetalerts.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetyalerts.exception.MedicalRecordException;
import com.openclassrooms.safetyalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.repository.MedicalRecordsRepository;

@Service
public class MedicalRecordsService {

	@Autowired
	private final MedicalRecordsRepository medicalRecordsRepository;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");


	public MedicalRecordsService(MedicalRecordsRepository medicalRecordsRepository) {
		this.medicalRecordsRepository = medicalRecordsRepository;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords(){
		return medicalRecordsRepository.getAllMedicalRecords();
	}

	// Ajouter un MedicalRecord
	public MedicalRecords addMedicalRecord(MedicalRecords newMedicalRecords) throws MedicalRecordException {
		if (newMedicalRecords.getFirstName() == null || newMedicalRecords.getFirstName().isBlank()
				|| newMedicalRecords.getLastName() == null || newMedicalRecords.getLastName().isBlank()) {
			throw new MedicalRecordException("Le prénom et le nom ne peuvent pas être vides.");
		}

		if (newMedicalRecords.getBirthDate() == null || newMedicalRecords.getBirthDate().isBlank()) {
			throw new MedicalRecordException("La date de naissance ne peut pas être vide.");
		}

		try {
			LocalDate.parse(newMedicalRecords.getBirthDate(), FORMATTER);
		} catch (DateTimeParseException e) {
			throw new MedicalRecordException("La date de naissance doit être au format MM/dd/yyyy.");
		}

		List<MedicalRecords> allMedicalRecords = medicalRecordsRepository.getAllMedicalRecords();

		boolean alreadyExists = allMedicalRecords.stream()
				.anyMatch(record -> record.getFirstName().equalsIgnoreCase(newMedicalRecords.getFirstName())
						&& record.getLastName().equalsIgnoreCase(newMedicalRecords.getLastName()));

		if (alreadyExists) {
			throw new MedicalRecordException("Un dossier médical existe déjà pour cette personne.");
		}

		return medicalRecordsRepository.addMedicalRecord(newMedicalRecords);
	}

	// Mise à jour des données
	public MedicalRecords updateMedicalRecord(String firstName, String lastName, MedicalRecords updateMedicalRecord)
			throws ResourceNotFoundException {
		if (!firstName.equalsIgnoreCase(updateMedicalRecord.getFirstName())
				|| !lastName.equalsIgnoreCase(updateMedicalRecord.getLastName())) {
			throw new IllegalArgumentException(
					"Prénom et nom de l'URL ne correspondent pas à ceux du corps de la requête.");
		}
		return medicalRecordsRepository.updateMedicalRecord(firstName, lastName, updateMedicalRecord);
	}

	// Supression d'un medical record
	public void deleteMedicalRecord(String firstName, String lastName) throws ResourceNotFoundException {
		List<MedicalRecords> allRecords = getAllMedicalRecords();
		boolean removed = allRecords.removeIf(record -> record.getFirstName().equalsIgnoreCase(firstName)
				&& record.getLastName().equalsIgnoreCase(lastName));

		if (removed) {
			medicalRecordsRepository.saveMedicalRecordsToJson(allRecords);
		} else {
			throw new ResourceNotFoundException("Aucun dossier médical trouvé pour cette personne.");
		}
	}

	// Methode de calcule de l'age d'une personne
	public int calculateAge(MedicalRecords medicalRecords) {
		String birthDate = medicalRecords.getBirthDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyy");
		LocalDate birthLocalDate = LocalDate.parse(birthDate, formatter);

		return Period.between(birthLocalDate, LocalDate.now()).getYears();
	}

}
