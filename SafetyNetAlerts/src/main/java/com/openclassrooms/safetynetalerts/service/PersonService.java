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

@Service
public class PersonService {

	private final MedicalRecordsService medicalRecordsService;

	@Autowired
	private final PersonRepository personRepository;
	private final FireStationRepository fireStationRepository;

	public PersonService(MedicalRecordsService medicalRecordsService, PersonRepository personRepository,
			FireStationRepository fireStationRepository) {
		this.medicalRecordsService = medicalRecordsService;
		this.personRepository = personRepository;
		this.fireStationRepository = fireStationRepository;
	}

	// Recuperer toutes les personnes
	public List<Person> getAllPerson() {
		return personRepository.getAllPerson();
	}

	// Ajouter une Personne
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

	// Mise à jour des données
	public Person updatePerson(String firstName, String lastName, Person updatePerson)
			throws ResourceNotFoundException {
		if (!firstName.equalsIgnoreCase(updatePerson.getFirstName())
				|| !lastName.equalsIgnoreCase(updatePerson.getLastName())) {
			throw new IllegalArgumentException(
					"Le prénom et/ou le nom de l'URL ne correspondent pas à ceux du corps de la requête.");
		}

		return personRepository.updatePerson(firstName, lastName, updatePerson);
	}

	// Supression d'une personne
	public void deletePerson(String firstName, String lastName) throws ResourceNotFoundException {
		List<Person> allPersons = getAllPerson();
		boolean removed = allPersons.removeIf(person -> person.getFirstName().equalsIgnoreCase(firstName)
				&& person.getLastName().equalsIgnoreCase(lastName));

		if (removed) {
			personRepository.savePersonToJson(allPersons);
		} else {
			throw new ResourceNotFoundException("Aucune personne ne correspond à cette requete");
		}
	}

	/// URL

	// Recuperer liste d'enfants par leurs adresse
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

	// Recuperer les infos des personnes par leurs nom
	public List<PersonInfoLastNameDTO> getPersonInfoByLastName(String lastName) throws Exception {
		List<Person> getAllPerson = getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();

		List<PersonInfoLastNameDTO> filteredPersons = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (lastName.equals(person.getLastName())) {
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

		return filteredPersons;
	}

	// Recuperer email de chaques habitants
	public EmailOfAllPersonDTO getEmailOfAllPersonByCity(String city) throws Exception {
		List<Person> getAllPerson = getAllPerson();

		List<String> filteredPersons = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (city.contains(person.getCity())) {
				filteredPersons.add(person.getEmail());
			}
		}

		return new EmailOfAllPersonDTO(filteredPersons);
	}

}
