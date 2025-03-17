package com.openclassrooms.safetynetalerts.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;

@Service
public class MedicalRecordsService {
	
	
	public List<MedicalRecords> getAllMedicalRecords() {
		return Arrays.asList(
	            new MedicalRecords("John", "Boyd", "03/06/1984", 
	                    Arrays.asList("aznol:350mg", "hydrapermazol:100mg"), 
	                    Arrays.asList("nillacilan")),
	                
	                new MedicalRecords("Jacob", "Boyd", "03/06/1989", 
	                    Arrays.asList("pharmacol:5000mg", "terazine:10mg", "noznazol:250mg"), 
	                    Arrays.asList()),

	                new MedicalRecords("Tenley", "Boyd", "02/18/2012", 
	                    Arrays.asList(), 
	                    Arrays.asList("peanut"))
	            );
	}
	
	
	
	/*

	@Autowired
	private final CustomProperties jsonFile;
	
	Gson gson = new Gson();
	
	public MedicalRecordsService(CustomProperties jsonFile) {
		this.jsonFile = jsonFile;
	}
	
	public List<MedicalRecords> readDataFromFile() throws Exception {
		String jsonFilePath = jsonFile.getJsonFile();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(jsonFilePath));
			JsonObject countryObj = gson.fromJson(br,JsonObject.class);
			String response = countryObj.get("medicalrecords").toString();
			
			System.out.println(response);
			Type utilisateurListType = new TypeToken<List<MedicalRecords>>(){}.getType();
			return gson.fromJson(response, utilisateurListType);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	*/
}
