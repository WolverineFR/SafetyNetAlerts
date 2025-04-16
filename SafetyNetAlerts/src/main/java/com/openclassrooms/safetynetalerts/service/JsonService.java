package com.openclassrooms.safetynetalerts.service;

import java.io.IOException;
import java.io.File;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Service permettant la lecture et l'écriture de données JSON depuis et vers un fichier.
 * Ce service centralise l'accès au fichier `data.json` utilisé comme base de données.
 */
@Service
public class JsonService {
	private final ObjectMapper objectMapper = new ObjectMapper();
	private static final String FILE_PATH = "target/classes/data.json";

	/**
	 * Lit une catégorie spécifique du fichier JSON et la convertit en un objet Java.
	 *
	 * @param <T>         le type de l'objet attendu.
	 * @param typeOfT     la référence de type pour la désérialisation.
	 * @param category    le nom de la catégorie à lire (ex: "persons", "firestations"...).
	 * @return            un objet de type T correspondant à la catégorie lue.
	 * @throws RuntimeException si le fichier ne peut pas être lu ou si la catégorie est absente.
	 */
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

	/**
	 * Écrit les données dans une catégorie spécifique du fichier JSON.
	 * Si la catégorie existe, elle est remplacée ; sinon, elle est ajoutée.
	 *
	 * @param category : Le nom de la catégorie à modifier ou ajouter.
	 * @param data     : Les données à écrire (doivent être compatibles JSON).
	 * @throws RuntimeException si une erreur se produit lors de l'écriture.
	 */
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
