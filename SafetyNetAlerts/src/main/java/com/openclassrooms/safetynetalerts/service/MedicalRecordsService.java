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
import com.google.gson.JsonElement;
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
	
	public MedicalRecordsService(CustomProperties jsonFile) {
		this.jsonFile = jsonFile;
	}
	
	public List<MedicalRecords> getAllMedicalRecords() throws Exception {
		String jsonFilePath = jsonFile.getJsonFile();
		InputStream in = getClass().getResourceAsStream("/" + jsonFilePath);
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			JsonObject medicalRecordsJson = gson.fromJson(br,JsonObject.class);
			String response = medicalRecordsJson.get("medicalrecords").toString();
			
			Type medicalRecordsListType = new TypeToken<List<MedicalRecords>>(){}.getType();
			return gson.fromJson(response, medicalRecordsListType);
			
		
		} catch (JsonSyntaxException e) {
	        throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
	    }
	}

	public MedicalRecords addMedicalRecord() throws Exception {
		String jsonFilePath = jsonFile.getJsonFile();
		InputStream in = getClass().getResourceAsStream("/" + jsonFilePath);
		
		try {
			FileWriter writer = new FileWriter("data.json");
			JsonElement record = null;
			gson.toJson(record, writer);
			writer.close();

		} catch (JsonSyntaxException e){
			throw new RuntimeException("Erreur lors de l'envoie vers le fichier JSON", e);
		}
		
		return null;
		
	}
}

