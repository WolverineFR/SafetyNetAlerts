package com.openclassrooms.safetynetalerts.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.exception.PersonException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.repository.FireStationRepository;
import com.openclassrooms.safetynetalerts.repository.PersonRepository;

/**
 * Service qui gère les opérations sur les personnes, telles que l'ajout, la mise à jour, la suppression,
 * et la récupération des informations détaillées de certaines personnes, en fonction de critères comme le nom
 * ou l'adresse.
 */
@Service
public class PersonService {

	private final MedicalRecordsService medicalRecordsService;

	@Autowired
	private final PersonRepository personRepository;
	private final FireStationRepository fireStationRepository;

	 /**
     * Constructeur du service PersonService.
     * 
     * @param medicalRecordsService : Le service permettant de gérer les dossiers médicaux.
     * @param personRepository : Le repository permettant d'accéder aux données des personnes.
     * @param fireStationRepository : Le repository permettant d'accéder aux informations des casernes.
     */
	public PersonService(MedicalRecordsService medicalRecordsService, PersonRepository personRepository,
			FireStationRepository fireStationRepository) {
		this.medicalRecordsService = medicalRecordsService;
		this.personRepository = personRepository;
		this.fireStationRepository = fireStationRepository;
	}

	/**
     * Récupère toutes les personnes.
     * 
     * @return Une liste de toutes les personnes.
     */
	public List<Person> getAllPerson() {
		return personRepository.getAllPerson();
	}

	/**
     * Ajoute une nouvelle personne après avoir validé les données.
     * 
     * @param newPerson : La personne à ajouter.
     * @return La personne ajoutée.
     * @throws PersonException : Si les informations de la personne sont invalides.
     * @throws RuntimeException : Si aucun dossier médical n'est trouvé ou si l'adresse n'est pas couverte.
     */
	public Person addPerson(Person newPerson) throws PersonException {
		if (newPerson.getFirstName() == null || newPerson.getFirstName().isBlank() || newPerson.getLastName() == null
				|| newPerson.getLastName().isBlank()) {
			throw new PersonException("Le prénom et/ou le nom de la personne ne peuvent pas être vides.");
		}

		if (newPerson.getAddress() == null || newPerson.getAddress().isBlank() || newPerson.getCity() == null
				|| newPerson.getCity().isBlank() || newPerson.getZip() == null || newPerson.getZip().isBlank()) {
			throw new PersonException("L'adresse de la personne ne peut pas être vide.");
		}

		if (newPerson.getPhone() == null || newPerson.getPhone().isBlank()) {
			throw new PersonException("Le numéro de téléphone de la personne ne peut pas être vide.");
		}

		if (newPerson.getEmail() == null || newPerson.getEmail().isBlank()) {
			throw new PersonException("L'email de la personne ne peut pas être vide.");
		}

		List<MedicalRecords> medicalRecords = medicalRecordsService.getAllMedicalRecords();
		boolean hasMedicalRecord = medicalRecords.stream()
				.anyMatch(mr -> mr.getFirstName().equalsIgnoreCase(newPerson.getFirstName())
						&& mr.getLastName().equalsIgnoreCase(newPerson.getLastName()));

		if (!hasMedicalRecord) {
			throw new RuntimeException("Aucun dossier médical trouvé pour cette personne.");
		}

		boolean addressCovered = fireStationRepository.getAllFireStation().stream()
				.anyMatch(fs -> fs.getAddress().equalsIgnoreCase(newPerson.getAddress()));

		if (!addressCovered) {
			throw new RuntimeException("L'adresse de la personne n'est pas couverte par une caserne.");
		}

		return personRepository.addPerson(newPerson);
	}

	/**
     * Met à jour les informations d'une personne existante.
     * 
     * @param firstName : Le prénom de la personne à mettre à jour.
     * @param lastName : Le nom de la personne à mettre à jour.
     * @param updatePerson : Les nouvelles informations de la personne.
     * @return La personne mise à jour.
     * @throws ResourceNotFoundException : Si aucune personne ne correspond aux critères.
     * @throws IllegalArgumentException : Si les informations dans l'URL ne correspondent pas à celles du corps de la requête.
     */
	public Person updatePerson(String firstName, String lastName, Person updatePerson)
			throws ResourceNotFoundException {
		if (!firstName.equalsIgnoreCase(updatePerson.getFirstName())
				|| !lastName.equalsIgnoreCase(updatePerson.getLastName())) {
			throw new IllegalArgumentException(
					"Le prénom et/ou le nom de l'URL ne correspondent pas à ceux du corps de la requête.");
		}

		return personRepository.updatePerson(firstName, lastName, updatePerson);
	}

	/**
     * Supprime une personne.
     * 
     * @param firstName : Le prénom de la personne à supprimer.
     * @param lastName : Le nom de la personne à supprimer.
     * @throws ResourceNotFoundException : Si aucune personne ne correspond aux critères.
     */
	public void deletePerson(String firstName, String lastName) throws ResourceNotFoundException {
		List<Person> allPersons = new ArrayList<>(getAllPerson());
		boolean removed = allPersons.removeIf(person -> person.getFirstName().equalsIgnoreCase(firstName)
				&& person.getLastName().equalsIgnoreCase(lastName));

		if (removed) {
			personRepository.savePersonToJson(allPersons);
		} else {
			throw new ResourceNotFoundException("Aucune personne ne correspond à cette requete");
		}
	}

	/// URLs

	 /**
     * Récupère une liste d'enfants vivant à une adresse donnée.
     * 
     * @param address : L'adresse pour laquelle les enfants doivent être récupérés.
     * @return Une liste d'objets ChildByAddressDTO contenant les informations des enfants si il y en a sinon retourne une chaine vide.
     * @throws Exception : Si une erreur se produit lors de la récupération des données.
     */
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		List<Person> getAllPerson = getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();

		List<ChildByAddressDTO> filteredPersons = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (address.contains(person.getAddress())) {
				for (MedicalRecords medicalRecord : getAllMedicalRecords) {
					if (person.getFirstName().equals(medicalRecord.getFirstName())
							&& person.getLastName().equals(medicalRecord.getLastName())) {

						int age = medicalRecordsService.calculateAge(medicalRecord);
						if (age <= 18) {

							List<String> familyMembers = getAllPerson.stream()
									.filter(pers -> address.equalsIgnoreCase(pers.getAddress())
											&& !(pers.getFirstName().equalsIgnoreCase(person.getFirstName())
													&& pers.getLastName().equalsIgnoreCase(person.getLastName())))
									.map(pers -> pers.getFirstName() + " " + pers.getLastName())
									.collect(Collectors.toList());

							filteredPersons.add(new ChildByAddressDTO(person.getFirstName(), person.getLastName(), age,
									familyMembers));
						}
					}
				}
			}
		}

		return filteredPersons;
	}

	/**
     * Récupère les informations des personnes par leur nom de famille.
     * 
     * @param lastName : Le nom de famille des personnes à rechercher.
     * @return Une liste d'objets PersonInfoLastNameDTO contenant les informations des personnes.
     * @throws Exception : Si une erreur se produit lors de la récupération des données.
     * @throws ResourceNotFoundException : Si aucune personne n'est trouvée avec ce nom.
     */
	public List<PersonInfoLastNameDTO> getPersonInfoByLastName(String lastName) throws Exception {
		List<Person> getAllPerson = getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();

		List<PersonInfoLastNameDTO> filteredPersons = new ArrayList<>();
		boolean matchFound = false;

		for (Person person : getAllPerson) {
			if (lastName.equals(person.getLastName())) {
				matchFound = true;
				for (MedicalRecords medicalRecord : getAllMedicalRecords) {
					if (person.getLastName().equals(medicalRecord.getLastName())) {
						int age = medicalRecordsService.calculateAge(medicalRecord);
						filteredPersons.add(new PersonInfoLastNameDTO(person.getLastName(), person.getAddress(), age,
								person.getEmail(), medicalRecord.getMedications(), medicalRecord.getAllergies()));
						break;
					}
				}
			}
		}
		if (!matchFound) {
			throw new ResourceNotFoundException("Aucune personne trouvée avec le nom : " + lastName);
		}

		return filteredPersons;
	}

	/**
     * Récupère les adresses email de toutes les personnes d'une ville donnée.
     * 
     * @param city : La ville des personnes dont les emails doivent être récupérés.
     * @return Un objet EmailOfAllPersonDTO contenant la liste des emails.
     * @throws Exception : Si une erreur se produit lors de la récupération des données.
     * @throws ResourceNotFoundException : Si aucune personne n'est trouvée dans la ville.
     */
	public EmailOfAllPersonDTO getEmailOfAllPersonByCity(String city) throws Exception {
		List<Person> getAllPerson = getAllPerson();

		List<String> filteredPersons = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (city.contains(person.getCity())) {
				filteredPersons.add(person.getEmail());
			}
			if (filteredPersons.isEmpty()) {
				throw new ResourceNotFoundException("Aucune personne trouvée avec la ville : " + city);
			}
		}

		return new EmailOfAllPersonDTO(filteredPersons);
	}

}
