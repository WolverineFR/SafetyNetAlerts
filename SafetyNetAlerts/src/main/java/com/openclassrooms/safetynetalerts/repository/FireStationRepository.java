package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetyalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.service.JsonService;

@Repository
public class FireStationRepository {

	@Autowired
	private JsonService jsonService;
	private static String category = "firestations";
	
	public FireStationRepository(JsonService jsonService) {
        this.jsonService = jsonService;
    }
	

	// Recuperer toutes les FireStation
	public List<FireStation> getAllFireStation() {
		return jsonService.readJsonFromFile(new TypeReference<List<FireStation>>() {}, category);
	}
	
	// Sauvegarder une FireStation en json
		public void saveFireStationToJson(List<FireStation> allFireStationList) {
			jsonService.writeJsonToFile(category, allFireStationList);
		}
		
		// Ajouter une FireStation
		public FireStation addFireStation(FireStation newFireStation) {
			List<FireStation> allFireStationList = getAllFireStation();
				allFireStationList.add(newFireStation);
				saveFireStationToJson(allFireStationList);
				return newFireStation;
			
		}
		
		// Mise à jour des données
		public FireStation updateFireStation(String address ,int station,FireStation updateFireStation) {
			List<FireStation> allFireStationList = getAllFireStation();
			boolean isUpdated = false;

			for (int i = 0; i < allFireStationList.size(); i++) {
				FireStation fs = allFireStationList.get(i);
				if (fs.getAddress().equalsIgnoreCase(updateFireStation.getAddress())) {
					allFireStationList.set(i, updateFireStation);
					isUpdated = true;
					break;
				}
			}

			if (isUpdated) {
				saveFireStationToJson(allFireStationList);
				return updateFireStation;
			} else {
				throw new ResourceNotFoundException("Aucunes casernes de pompier correspondante trouvées.");
			}
		}
		
		// Supression d'un medical record
		public FireStation deleteFireStation(FireStation deleteFireStation) {
			List<FireStation> allFireStationList = getAllFireStation();
			boolean isUpdated = false;

			for (int i = 0; i < allFireStationList.size(); i++) {
				FireStation fs = allFireStationList.get(i);
				if (fs.getAddress().equalsIgnoreCase(deleteFireStation.getAddress())
						&& fs.getStation() == (deleteFireStation.getStation())) {
					allFireStationList.remove(i);
					isUpdated = true;
					break;
				}
			}

			if (isUpdated) {
				saveFireStationToJson(allFireStationList);
				return deleteFireStation;
			} else {
				throw new ResourceNotFoundException("Cette caserne de pompier n'existe pas.");
			}
		}
	
}
