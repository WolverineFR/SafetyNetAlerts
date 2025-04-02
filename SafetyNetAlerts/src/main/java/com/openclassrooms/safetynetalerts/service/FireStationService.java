package com.openclassrooms.safetynetalerts.service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.dto.FireStationCoverageDTO;
import com.openclassrooms.safetynetalerts.dto.FireStationCoveragePhoneNumberDTO;
import com.openclassrooms.safetynetalerts.dto.FloodListOfStationNumberDTO;
import com.openclassrooms.safetynetalerts.dto.PersonByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.PersonFireStationDTO;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;

@Service
public class FireStationService {
	
	private final PersonService personService;
	private final MedicalRecordsService medicalRecordsService;
	
	@Autowired
	private final FireStationRepository fireStationRepository;

	public FireStationService(MedicalRecordsService medicalRecordsService, PersonService personService,FireStationRepository fireStationRepository ) {
		this.personService = personService;
		this.medicalRecordsService = medicalRecordsService;
		this.fireStationRepository = fireStationRepository;
	}

	// Recuperer toutes les FireStation
	public List<FireStation> getAllFireStation() throws Exception {
		return fireStationRepository.getAllFireStation();
	}


	// Ajouter une FireStation
	public void addFireStation(FireStation newFireStation) throws Exception {
		fireStationRepository.addFireStation(newFireStation);
		
	}

	// Mise à jour des données
	public FireStation updateFireStation(FireStation updateFireStation) throws Exception {
		return fireStationRepository.updateFireStation(updateFireStation);
	}

	// Supression d'un medical record
	public void deleteFireStation(FireStation deleteFireStation) throws Exception {
	 fireStationRepository.deleteFireStation(deleteFireStation);
	}

	
	//// Ajouter ce qui suit dans repo
	
	
	
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
	
	public List<FloodListOfStationNumberDTO> getPersonByListOfStationNumber (int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		List<Person> getAllPerson = personService.getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();
		
		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fireStation -> fireStation.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());

		List<FloodListOfStationNumberDTO> filteredPersons = new ArrayList<>();
		
		for (Person person : getAllPerson) {
			if (coveredAddresses.contains(person.getAddress())) {
				for (MedicalRecords medicalRecord : getAllMedicalRecords) {
					if (person.getFirstName().equals(medicalRecord.getFirstName())
							&& person.getLastName().equals(medicalRecord.getLastName())) {
						int age = medicalRecordsService.calculateAge(medicalRecord);
						filteredPersons.add( new FloodListOfStationNumberDTO(person.getFirstName(), person.getLastName(), person.getAddress(), person.getPhone(), age, medicalRecord.getMedications(), medicalRecord.getAllergies()));
					}
				}
			}
				
			}
		return filteredPersons;
	}
}
