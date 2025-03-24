package com.openclassrooms.safetynetalerts.service;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Service
public class JsonService {
	private final Gson gson = new Gson();

	public <T> T readJsonFromFile(String filePath, Type typeOfT, String category) {
		try (InputStream in = getClass().getResourceAsStream("/" + filePath)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			JsonObject jsonObject = gson.fromJson(br, JsonObject.class);
			String response = jsonObject.get(category).toString();

			return gson.fromJson(response, typeOfT);
		} catch (IOException e) {
			throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
		}
	}

	public void writeJsonToFile(String filePath, String key, Object data) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.add(key, gson.toJsonTree(data));

		try (FileWriter fileWriter = new FileWriter(getClass().getResource("/" + filePath).getFile())) {
			gson.toJson(jsonObject, fileWriter);
		} catch (Exception e) {
			throw new RuntimeException("Erreur lors de la sauvegarde des données dans le fichier JSON", e);
		}
	}
}
