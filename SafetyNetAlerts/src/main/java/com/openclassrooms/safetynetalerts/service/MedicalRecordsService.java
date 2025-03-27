package com.openclassrooms.safetynetalerts.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;

@Service
public class MedicalRecordsService {

	@Autowired
	private final CustomProperties jsonFile;

	private JsonService jsonService;
	private static String category = "medicalrecords";

	public MedicalRecordsService(CustomProperties jsonFile, JsonService jsonService) {
		this.jsonFile = jsonFile;
		this.jsonService = jsonService;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		Type listType = new TypeToken<List<MedicalRecords>>() {
		}.getType();
		return jsonService.readJsonFromFile(listType, category);
	}

	// Sauvegarder un medical record en json
	private void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) {
		jsonService.writeJsonToFile(category, allMedicalRecordsList);
	}

	// Ajouter un MedicalRecord
	public void addMedicalRecord(MedicalRecords newMedicalRecords) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();

		try {
			allMedicalRecordsList.add(newMedicalRecords);
			saveMedicalRecordsToJson(allMedicalRecordsList);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException("Erreur lors de l'envoie vers le fichier JSON", e);
		}
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
