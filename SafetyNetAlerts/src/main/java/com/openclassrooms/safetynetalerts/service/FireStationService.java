package com.openclassrooms.safetynetalerts.service;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.model.FireStation;

@Service
public class FireStationService {

	@Autowired
	private final CustomProperties jsonFile;

	private JsonService jsonService;
	private static String category = "firestations";

	public FireStationService(CustomProperties jsonFile, JsonService jsonService) {
		this.jsonFile = jsonFile;
		this.jsonService = jsonService;
	}

	// Recuperer toutes les FireStation
	public List<FireStation> getAllFireStation() throws Exception {
		Type listType = new TypeToken<List<FireStation>>() {
		}.getType();
		return jsonService.readJsonFromFile( listType, category);
	}

	// Sauvegarder une FireStation en json
	private void saveFireStationToJson(List<FireStation> allFireStationList) {
		jsonService.writeJsonToFile( category, allFireStationList);
	}

	// Ajouter une FireStation
	public void addFireStation(FireStation newFireStation) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		try {
			allFireStationList.add(newFireStation);
			saveFireStationToJson(allFireStationList);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException("Erreur lors de l'envoie vers le fichier JSON", e);
		}
	}

	// Mise à jour des données
	public FireStation updateFireStation(FireStation updateFireStation) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		boolean isUpdated = false;

		for (int i = 0; i < allFireStationList.size(); i++) {
			FireStation mr = allFireStationList.get(i);
			if (mr.getAddress().equalsIgnoreCase(updateFireStation.getAddress())) {
				allFireStationList.set(i, updateFireStation);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveFireStationToJson(allFireStationList);
			return updateFireStation;
		} else {
			throw new RuntimeException("Aucunes casernes de pompier correspondante trouvées.");
		}
	}

	// Supression d'un medical record
	public FireStation deleteFireStation(FireStation deleteFireStation) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		boolean isUpdated = false;

		for (int i = 0; i < allFireStationList.size(); i++) {
			FireStation mr = allFireStationList.get(i);
			if (mr.getAddress().equalsIgnoreCase(deleteFireStation.getAddress())
					&& mr.getStation() == (deleteFireStation.getStation())) {
				allFireStationList.remove(i);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveFireStationToJson(allFireStationList);
			return deleteFireStation;
		} else {
			throw new RuntimeException("Cette caserne de pompier n'existe pas.");
		}
	}
}
