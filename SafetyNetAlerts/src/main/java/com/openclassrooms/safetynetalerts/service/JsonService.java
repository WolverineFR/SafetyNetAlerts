package com.openclassrooms.safetynetalerts.service;

import java.io.IOException;
import java.io.File;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class JsonService {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String FILE_PATH = "target/classes/data.json";

	public <T> T readJsonFromFile(TypeReference<T> typeOfT, String category) {
		try {
			JsonNode rootNode = objectMapper.readTree(new File(FILE_PATH));
			JsonNode categoryNode = rootNode.get(category);
			if (categoryNode == null) {
				throw new RuntimeException("Catégorie introuvable : " + category);
			}
			return objectMapper.convertValue(categoryNode, typeOfT);
		} catch (IOException e) {
			throw new RuntimeException("Erreur lors de la lecture du fichier JSON", e);
		}
	}

	public void writeJsonToFile(String category, Object data) {
		try {
			JsonNode rootNode = objectMapper.readTree(new File(FILE_PATH));
			((ObjectNode) rootNode).set(category, objectMapper.valueToTree(data));
			objectMapper.writeValue(new File(FILE_PATH), rootNode);
		} catch (Exception e) {
			throw new RuntimeException("Erreur lors de la sauvegarde des données dans le fichier JSON", e);
		}
	}
}
