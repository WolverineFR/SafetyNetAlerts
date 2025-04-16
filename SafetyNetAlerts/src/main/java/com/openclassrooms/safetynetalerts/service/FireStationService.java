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
import com.openclassrooms.safetynetalerts.exception.FireStationException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.FireStation;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;

/**
 * Service responsable de la gestion des casernes de pompiers et des informations relatives.
 * Ce service permet d'ajouter, de modifier, de supprimer et de récupérer des informations sur les casernes de pompiers.
 */
@Service
public class FireStationService {
	
	private final PersonService personService;
	private final MedicalRecordsService medicalRecordsService;
	private final FireStationRepository fireStationRepository;

	 /**
     * Constructeur pour injecter les dépendances nécessaires.
     * 
     * @param medicalRecordsService : Service responsable de la gestion des dossiers médicaux
     * @param personService : Service responsable de la gestion des personnes
     * @param fireStationRepository : Repository pour interagir avec la base de données des casernes
     */
	@Autowired
	public FireStationService(MedicalRecordsService medicalRecordsService, PersonService personService,FireStationRepository fireStationRepository ) {
		this.personService = personService;
		this.medicalRecordsService = medicalRecordsService;
		this.fireStationRepository = fireStationRepository;
	}

	 /**
     * Récupère toutes les casernes de pompiers enregistrées.
     * 
     * @return Liste de toutes les casernes de pompiers
     */
	public List<FireStation> getAllFireStation() {
		return fireStationRepository.getAllFireStation();
	}


	/**
     * Ajoute une nouvelle caserne de pompiers.
     * 
     * @param newFireStation : Nouvelle caserne de pompiers à ajouter
     * @return La caserne de pompiers ajoutée
     * @throws FireStationException : Si les données de la caserne sont invalides ou si une caserne existe déjà à la même adresse
     */
	public FireStation addFireStation(FireStation newFireStation) throws FireStationException {
		if (newFireStation.getAddress() == null || newFireStation.getAddress().isBlank() || newFireStation.getStation() == 0 ) {
			throw new FireStationException("L'adresse de la caserne ne peut pas être vide et son numéro doit être supérieur à 0.");
		}
			List<FireStation> allFireStations = fireStationRepository.getAllFireStation();
			
			boolean alreadyExists = allFireStations.stream().anyMatch(fs -> fs.getAddress().equalsIgnoreCase(newFireStation.getAddress()));
			
			if (alreadyExists) {
				throw new FireStationException("Une caserne de pompier à cette adresse existe déjà.");
			}
			return fireStationRepository.addFireStation(newFireStation);
	}

	/**
     * Met à jour une caserne de pompiers existante.
     * 
     * @param address : L'adresse de la caserne à mettre à jour
     * @param updateFireStation : Les nouvelles informations de la caserne
     * @return La caserne de pompiers mise à jour
     * @throws ResourceNotFoundException : Si la caserne n'est pas trouvée
     * @throws IllegalArgumentException : Si l'adresse dans l'URL ne correspond pas à celle dans le corps de la requête
     */
	public FireStation updateFireStation(String address ,FireStation updateFireStation) throws ResourceNotFoundException {
		if (!address.equalsIgnoreCase(updateFireStation.getAddress())) {
			throw new IllegalArgumentException("L'adresse de la caserne dans l'url ne correspond pas au corps de la requete");
		}
		return fireStationRepository.updateFireStation(address,updateFireStation);
	}

	 /**
     * Supprime une caserne de pompiers en fonction de son adresse et de son numéro.
     * 
     * @param address : L'adresse de la caserne à supprimer
     * @param station : Le numéro de la station de pompiers à supprimer
     * @throws ResourceNotFoundException : Si aucune caserne n'est trouvée avec ces informations
     */
	public void deleteFireStation( String address, int station) throws ResourceNotFoundException {
	 List<FireStation> allFireStations = getAllFireStation();
	 boolean removed = allFireStations.removeIf(fs -> fs.getAddress().equalsIgnoreCase(address) && (fs.getStation() == station));
		if (removed) {
			fireStationRepository.saveFireStationToJson(allFireStations);
		} else {
			throw new ResourceNotFoundException("Aucune casernes trouvé pour ces informations");
		}
	}

	
	
	// URL
	
	/**
     * Récupère la liste des personnes couvertes par une caserne de pompiers donnée par numéro de station.
     * 
     * @param stationNumber : Le numéro de la caserne pour laquelle on veut obtenir les personnes couvertes
     * @return Un DTO contenant les informations des personnes couvertes par la caserne, incluant le nombre d'adultes et d'enfants
     * @throws ResourceNotFoundException : Si aucune caserne n'est trouvée avec le numéro de station donné
     * @throws Exception : Si une erreur inattendue se produit
     */
	public FireStationCoverageDTO getPersonsByStationNumber(int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fs -> fs.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());
		
		if (coveredAddresses.isEmpty()) {
	        throw new ResourceNotFoundException("Aucune caserne trouvée pour le numéro : " + stationNumber);
	    }

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

	/**
	 * Récupère les numéros de téléphone des personnes couvertes par une caserne donnée, identifiée par son numéro de station.
	 * 
	 * @param stationNumber : Le numéro de la caserne pour laquelle on souhaite récupérer les numéros de téléphone des personnes couvertes.
	 * @return Un DTO contenant la liste des numéros de téléphone des personnes couvertes par la caserne.
	 * @throws ResourceNotFoundException : Si aucune caserne n'est trouvée avec le numéro de station donné.
	 * @throws Exception : Si une erreur inattendue se produit lors de l'exécution.
	 */
	public FireStationCoveragePhoneNumberDTO getPhoneNumberByStationNumber(int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();

		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fs -> fs.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());
		
		if (coveredAddresses.isEmpty()) {
	        throw new ResourceNotFoundException("Aucune caserne trouvée pour le numéro : " + stationNumber);
	    }

		List<Person> persons = personService.getAllPerson();
		List<String> filteredPersons = new ArrayList<>();

		for (Person person : persons) {
			if (coveredAddresses.contains(person.getAddress())) {
				filteredPersons.add(person.getPhone());
			}
		}
		return new FireStationCoveragePhoneNumberDTO(filteredPersons);
	}

	/**
	 * Récupère les personnes présentes à une adresse spécifique, en fournissant des informations détaillées sur chaque personne, 
	 * y compris leur âge, leurs médicaments et leurs allergies, ainsi que la caserne de pompiers couverte.
	 * 
	 * @param address : L'adresse pour laquelle on souhaite obtenir les personnes présentes.
	 * @return Une liste de DTO contenant les informations des personnes présentes à l'adresse, y compris les informations médicales et de contact.
	 * @throws ResourceNotFoundException : Si aucune caserne n'est trouvée pour l'adresse spécifiée.
	 * @throws Exception : Si une erreur inattendue se produit lors de l'exécution.
	 */
	public List<PersonByAddressDTO> getPersonByAddress(String address) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		List<Person> getAllPerson = personService.getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();
		
		boolean addressExists = allFireStationList.stream()
	            .anyMatch(fs -> fs.getAddress().equalsIgnoreCase(address));

	    if (!addressExists) {
	        throw new ResourceNotFoundException("Aucune caserne trouvée pour l'adresse : " + address);
	    }

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
	
	/**
	 * Récupère la liste des personnes couvertes par une caserne donnée, identifiée par son numéro de station, 
	 * avec des informations détaillées sur leur âge, leur adresse, leurs médicaments et leurs allergies.
	 * 
	 * @param stationNumber : Le numéro de la caserne pour laquelle on souhaite récupérer les informations des personnes couvertes.
	 * @return Une liste de DTO contenant les informations détaillées des personnes couvertes par la caserne.
	 * @throws ResourceNotFoundException : Si aucune caserne n'est trouvée avec le numéro de station donné.
	 * @throws Exception : Si une erreur inattendue se produit lors de l'exécution.
	 */
	public List<FloodListOfStationNumberDTO> getPersonByListOfStationNumber (int stationNumber) throws Exception {
		List<FireStation> allFireStationList = getAllFireStation();
		List<Person> getAllPerson = personService.getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();
		
		List<String> coveredAddresses = allFireStationList.stream()
				.filter(fs -> fs.getStation() == stationNumber).map(FireStation::getAddress)
				.collect(Collectors.toList());
		
		if (coveredAddresses.isEmpty()) {
	        throw new ResourceNotFoundException("Aucune caserne trouvée pour le numéro : " + stationNumber);
	    }

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
