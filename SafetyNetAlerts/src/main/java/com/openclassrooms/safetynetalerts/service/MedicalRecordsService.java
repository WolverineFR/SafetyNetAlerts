package com.openclassrooms.safetynetalerts.service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;

@Service
public class MedicalRecordsService {

	@Autowired
	private final CustomProperties jsonFile;

	Gson gson = new Gson();

	private String jsonFilePath;

	public MedicalRecordsService(CustomProperties jsonFile) {
		this.jsonFile = jsonFile;
	}

	// Recuperer tout les MedicalRecords
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		jsonFilePath = jsonFile.getJsonFile();
		InputStream in = getClass().getResourceAsStream("/" + jsonFilePath);

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			JsonObject medicalRecordsJson = gson.fromJson(br, JsonObject.class);
			String response = medicalRecordsJson.get("medicalrecords").toString();

			Type medicalRecordsListType = new TypeToken<List<MedicalRecords>>() {
			}.getType();
			return gson.fromJson(response, medicalRecordsListType);

		} catch (JsonSyntaxException e) {
			throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
		}
	}
	
	// Sauvegarder un medical record en json
		private void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) throws Exception {
			jsonFilePath = jsonFile.getJsonFile();
			JsonObject medicalrecordsJson = new JsonObject();
			medicalrecordsJson.add("medicalrecords", gson.toJsonTree(allMedicalRecordsList));

			try (FileWriter fileWriter = new FileWriter(getClass().getResource("/" + jsonFilePath).getFile())) {
				gson.toJson(medicalrecordsJson, fileWriter);
			} catch (Exception e) {
				throw new RuntimeException("Erreur lors de la sauvegarde des données dans le fichier JSON");
			}
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
	public MedicalRecords updateMedicalrecords(MedicalRecords updateMedicalRecord) throws Exception {
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
			throw new RuntimeException("Les données sont incorrect");
		}
	}
	
}
