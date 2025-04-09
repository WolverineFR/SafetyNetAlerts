package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.JsonService;

@Repository
public class MedicalRecordsRepository {

	@Autowired
	private JsonService jsonService;
	private static String category = "medicalrecords";

	public MedicalRecordsRepository(JsonService jsonService) {
		this.jsonService = jsonService;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords() {
		return jsonService.readJsonFromFile(new TypeReference<List<MedicalRecords>>() {
		}, category);
	}

	// Sauvegarder un medical record en json
	public void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) {
		jsonService.writeJsonToFile(category, allMedicalRecordsList);
	}

	// Ajouter un MedicalRecord
	public MedicalRecords addMedicalRecord(MedicalRecords newMedicalRecords) {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		allMedicalRecordsList.add(newMedicalRecords);
		saveMedicalRecordsToJson(allMedicalRecordsList);
		return newMedicalRecords;
	}

	// Mise à jour des données
	public MedicalRecords updateMedicalRecord(String firstName, String lastName, MedicalRecords updateMedicalRecord) {
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
			throw new ResourceNotFoundException("Aucun dossier médical correspondant trouvé.");
		}
	}

	// Supression d'un medical record
	public MedicalRecords deleteMedicalRecord(MedicalRecords deleteMedicalRecord) {
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
			throw new ResourceNotFoundException("Ce rapport medical n'existe pas");
		}
	}
}
