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

	public void addMedicalRecord(MedicalRecords newMedicalRecords) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();

		allMedicalRecordsList.add(newMedicalRecords);

		jsonFilePath = jsonFile.getJsonFile();
		FileWriter fileWriter = new FileWriter(getClass().getResource("/" + jsonFilePath).getFile());
		JsonObject medicalrecordsJson = new JsonObject();
		medicalrecordsJson.add("medicalrecords", gson.toJsonTree(allMedicalRecordsList));

		try {
			gson.toJson(medicalrecordsJson, fileWriter);
			fileWriter.close();
		} catch (JsonSyntaxException e) {
			throw new RuntimeException("Erreur lors de l'envoie vers le fichier JSON", e);
		}

	}
	
	private void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) throws Exception {
	    String jsonFilePath = jsonFile.getJsonFile();
	    FileWriter writer = new FileWriter(getClass().getResource("/" + jsonFilePath).getFile()); 
	    
	    gson.toJson(allMedicalRecordsList, writer);
	    writer.flush();
	    writer.close();
	}

	public MedicalRecords updateMedicalRecord(String firstName, String lastName, MedicalRecords updateMedicalRecords) throws Exception {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		
		for (MedicalRecords record : allMedicalRecordsList) {
			if (record.getFirstName().equalsIgnoreCase(firstName) && record.getLastName().equalsIgnoreCase(lastName)) {
				record.setBirthDate(updateMedicalRecords.getBirthDate());
				record.setMedications(updateMedicalRecords.getMedications());
				record.setAllergies(updateMedicalRecords.getAllergies());
				
				saveMedicalRecordsToJson(allMedicalRecordsList);
				
				return record;
			}
		}
		return null;

	}
}
