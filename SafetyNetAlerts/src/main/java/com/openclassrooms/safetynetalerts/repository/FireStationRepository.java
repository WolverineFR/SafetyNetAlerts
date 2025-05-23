package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
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
	

	/**
	 * Récupère toutes les casernes de pompiers à partir du fichier JSON.
	 *
	 * @return Liste de toutes les FireStation.
	 */
	public List<FireStation> getAllFireStation() {
		return jsonService.readJsonFromFile(new TypeReference<List<FireStation>>() {}, category);
	}
	
	/**
	 * Sauvegarde la liste de casernes de pompiers dans le fichier JSON.
	 *
	 * @param allFireStationList : La liste à sauvegarder.
	 */
		public void saveFireStationToJson(List<FireStation> allFireStationList) {
			jsonService.writeJsonToFile(category, allFireStationList);
		}
		
		/**
		 * Ajoute une nouvelle caserne de pompier à la liste et la sauvegarde.
		 *
		 * @param newFireStation : La nouvelle caserne à ajouter.
		 * @return La caserne ajoutée.
		 */
		public FireStation addFireStation(FireStation newFireStation) {
			List<FireStation> allFireStationList = getAllFireStation();
				allFireStationList.add(newFireStation);
				saveFireStationToJson(allFireStationList);
				return newFireStation;
			
		}
		
		/**
		 * Met à jour une caserne de pompier existante à partir de son adresse.
		 *
		 * @param address : Adresse de la caserne à mettre à jour.
		 * @param updateFireStation : Nouvelle version de la caserne.
		 * @return La caserne mise à jour.
		 * @throws ResourceNotFoundException : Si la caserne n'existe pas.
		 */
		public FireStation updateFireStation(String address ,FireStation updateFireStation) {
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
		
		/**
		 * Supprime une caserne de pompier à partir de son adresse et numéro de station.
		 *
		 * @param deleteFireStation : La caserne à supprimer.
		 * @return La caserne supprimée.
		 * @throws ResourceNotFoundException : Si aucune caserne correspondante n'est trouvée.
		 */
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
