package com.openclassrooms.safetynetalerts.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.PersonFireStationDTO;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.Person;

@Service
public class FireStationService {

	@Autowired
	private final CustomProperties jsonFile;
	private final PersonService personService;

	private JsonService jsonService;
	private static String category = "firestations";

	public FireStationService(PersonService personService, CustomProperties jsonFile, JsonService jsonService) {
		this.jsonFile = jsonFile;
		this.jsonService = jsonService;
		this.personService = personService;
	}

	// Recuperer toutes les FireStation
	public List<FireStation> getAllFireStation() throws Exception {
		Type listType = new TypeToken<List<FireStation>>() {
		}.getType();
		return jsonService.readJsonFromFile(listType, category);
	}

	// Sauvegarder une FireStation en json
	private void saveFireStationToJson(List<FireStation> allFireStationList) {
		jsonService.writeJsonToFile(category, allFireStationList);
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

	//Recuperer les person par numero de station
	public FireStationCoverageDTO getPersonsByStationNumber(int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fireStation -> fireStation.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());

		List<Person> persons = personService.getAllPerson();
		List<PersonFireStationDTO> filteredPersons = new ArrayList<>();
		int numberOfAdults = 0;
		int numberOfChildren = 0;

		for (Person person : persons) {
			if (coveredAddresses.contains(person.getAddress())) {

				int age = 18; // calculateAge(person);
				if (age <= 18) {
					numberOfChildren++;
				} else {
					numberOfAdults++;
				}

				filteredPersons.add(new PersonFireStationDTO(person.getFirstName(), person.getLastName(),
						person.getAddress(), person.getPhone()));
			}
		}
		return new FireStationCoverageDTO(filteredPersons, numberOfAdults, numberOfChildren);
	}

}
