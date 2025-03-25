package com.openclassrooms.safetynetalerts.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class JsonService {
	private final Gson gson = new Gson();
	private static final String filePath = "target/classes/data.json";

	public <T> T readJsonFromFile(Type typeOfT, String category) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
			String response = jsonObject.get(category).toString();

			return gson.fromJson(response, typeOfT);
		} catch (IOException e) {
			throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
		}
	}

	public void writeJsonToFile(String key, Object data) {
		try {
			FileReader fileReader = new FileReader(filePath);

			JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);
			jsonObject.add(key, gson.toJsonTree(data));

			try (FileWriter fileWriter = new FileWriter(filePath)) {
				gson.toJson(jsonObject, fileWriter);
			}

		} catch (Exception e) {
			throw new RuntimeException("Erreur lors de la sauvegarde des données dans le fichier JSON", e);
		}
	}
}
