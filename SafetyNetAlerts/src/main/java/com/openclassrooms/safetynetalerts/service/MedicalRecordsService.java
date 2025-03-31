package com.openclassrooms.safetynetalerts.service;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;

@Service
public class MedicalRecordsService {

	private JsonService jsonService;
	private static String category = "medicalrecords";

	public MedicalRecordsService(JsonService jsonService) {
		this.jsonService = jsonService;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		return jsonService.readJsonFromFile(new TypeReference<List<MedicalRecords>>() {}, category);
	}

	// Sauvegarder un medical record en json
	private void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) {
		jsonService.writeJsonToFile(category, allMedicalRecordsList);
	}

	// Ajouter un MedicalRecord
	public void addMedicalRecord(MedicalRecords newMedicalRecords) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
			allMedicalRecordsList.add(newMedicalRecords);
			saveMedicalRecordsToJson(allMedicalRecordsList);
	}

	// Mise à jour des données
	public MedicalRecords updateMedicalRecord(MedicalRecords updateMedicalRecord) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		boolean isUpdated = false;

		for (int i = 0; i < allMedicalRecordsList.size(); i++) {
			MedicalRecords mr = allMedicalRecordsList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(updateMedicalRecord.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(updateMedicalRecord.getLastName())) {
				allMedicalRecordsList.set(i, updateMedicalRecord);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveMedicalRecordsToJson(allMedicalRecordsList);
			return updateMedicalRecord;
		} else {
			throw new RuntimeException("Aucun dossier médical correspondant trouvé.");
		}
	}

	// Supression d'un medical record
	public MedicalRecords deleteMedicalRecord(MedicalRecords deleteMedicalRecord) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		boolean isUpdated = false;

		for (int i = 0; i < allMedicalRecordsList.size(); i++) {
			MedicalRecords mr = allMedicalRecordsList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(deleteMedicalRecord.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(deleteMedicalRecord.getLastName())) {
				allMedicalRecordsList.remove(i);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveMedicalRecordsToJson(allMedicalRecordsList);
			return deleteMedicalRecord;
		} else {
			throw new RuntimeException("Ce rapport medical n'existe pas");
		}
	}
	
	public int calculateAge(MedicalRecords medicalRecords) {
		 String birthDate = medicalRecords.getBirthDate();
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyy");
		 LocalDate birthLocalDate = LocalDate.parse(birthDate, formatter);
		
		return Period.between(birthLocalDate, LocalDate.now()).getYears();
	}

}
