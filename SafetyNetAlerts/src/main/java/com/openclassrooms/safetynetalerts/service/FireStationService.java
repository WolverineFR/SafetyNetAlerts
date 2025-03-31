package com.openclassrooms.safetynetalerts.service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.PersonFireStationDTO;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;

@Service
public class FireStationService {
	
	private final PersonService personService;
	private final MedicalRecordsService medicalRecordsService;

	private JsonService jsonService;
	private static String category = "firestations";

	public FireStationService(MedicalRecordsService medicalRecordsService, PersonService personService, JsonService jsonService) {
		this.jsonService = jsonService;
		this.personService = personService;
		this.medicalRecordsService = medicalRecordsService;
	}

	// Recuperer toutes les FireStation
	public List<FireStation> getAllFireStation() throws Exception {
		return jsonService.readJsonFromFile(new TypeReference<List<FireStation>>() {}, category);
	}

	// Sauvegarder une FireStation en json
	private void saveFireStationToJson(List<FireStation> allFireStationList) {
		jsonService.writeJsonToFile(category, allFireStationList);
	}

	// Ajouter une FireStation
	public void addFireStation(FireStation newFireStation) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
			allFireStationList.add(newFireStation);
			saveFireStationToJson(allFireStationList);
		
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

	// Recuperer les person par numero de station
	public FireStationCoverageDTO getPersonsByStationNumber(int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fireStation -> fireStation.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());

		List<Person> persons = personService.getAllPerson();
		List<MedicalRecords> medicalRecords = medicalRecordsService.getAllMedicalRecords();
		List<PersonFireStationDTO> filteredPersons = new ArrayList<>();

		int numberOfAdults = 0;
		int numberOfChildren = 0;

		for (Person person : persons) {
			if (coveredAddresses.contains(person.getAddress())) {
				for (MedicalRecords medicalRecord : medicalRecords) {
					if (person.getFirstName().equals(medicalRecord.getFirstName())
							&& person.getLastName().equals(medicalRecord.getLastName())) {

						int age = medicalRecordsService.calculateAge(medicalRecord);
						if (age <= 18) {
							numberOfChildren++;
						} else {
							numberOfAdults++;
						}

					}
				}

				filteredPersons.add(new PersonFireStationDTO(person.getFirstName(), person.getLastName(),
						person.getAddress(), person.getPhone()));
			}
		}
		return new FireStationCoverageDTO(filteredPersons, numberOfAdults, numberOfChildren);
	}

	// recuperer les personnes par leurs numero de telephone
	public FireStationCoveragePhoneNumberDTO getPhoneNumberByStationNumber(int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fireStation -> fireStation.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());

		List<Person> persons = personService.getAllPerson();
		List<String> filteredPersons = new ArrayList<>();

		for (Person person : persons) {
			if (coveredAddresses.contains(person.getAddress())) {
				filteredPersons.add(person.getPhone());
			}
		}
		return new FireStationCoveragePhoneNumberDTO(filteredPersons);
	}

	// Recuperer les personnes par adresse avec numero de station
	public List<PersonByAddressDTO> getPersonByAddress(String address) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		List<Person> getAllPerson = personService.getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();

		List<PersonByAddressDTO> filteredPersons = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (address.contains(person.getAddress())) {
				for (MedicalRecords medicalRecord : getAllMedicalRecords) {
					if (person.getFirstName().equals(medicalRecord.getFirstName())
							&& person.getLastName().equals(medicalRecord.getLastName())) {
						for (FireStation fireStation : allFireStationList) {
							if (person.getAddress().contains(fireStation.getAddress())) {
								int age = medicalRecordsService.calculateAge(medicalRecord);

								filteredPersons.add(new PersonByAddressDTO(fireStation.getStation(),
										person.getFirstName(), person.getLastName(),person.getPhone(), age,
										medicalRecord.getMedications(), medicalRecord.getAllergies()));
							}
						}
					}
				}
			}
		}

		return filteredPersons;
	}
}
