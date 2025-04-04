package com.openclassrooms.safetynetalerts.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
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

	public PersonService(MedicalRecordsService medicalRecordsService, PersonRepository personRepository,FireStationRepository fireStationRepository) {
		this.medicalRecordsService = medicalRecordsService;
		this.personRepository = personRepository;
		this.fireStationRepository = fireStationRepository;
	}

	// Recuperer toutes les personnes
	public List<Person> getAllPerson() throws Exception {
		return personRepository.getAllPerson();
	}

	// Ajouter un Person
	public void addPerson(Person newPerson) throws Exception {
		 List<MedicalRecords> medicalRecords = medicalRecordsService.getAllMedicalRecords();
		    boolean hasMedicalRecord = medicalRecords.stream().anyMatch(med ->
		        med.getFirstName().equalsIgnoreCase(newPerson.getFirstName()) &&
		        med.getLastName().equalsIgnoreCase(newPerson.getLastName())
		    );

		    if (!hasMedicalRecord) {
		        throw new RuntimeException("Aucun dossier médical trouvé pour cette personne.");
		    }

		    boolean addressCovered = fireStationRepository.getAllFireStation().stream().anyMatch(fs ->
		        fs.getAddress().equalsIgnoreCase(newPerson.getAddress())
		    );

		    if (!addressCovered) {
		        throw new RuntimeException("L'adresse de la personne n'est pas couverte par une caserne.");
		    }
		
		personRepository.addPerson(newPerson);
	}

	// Mise à jour des données
	public Person updatePerson(Person updatePerson) throws Exception {
		return personRepository.updatePerson(updatePerson);
	}

	// Supression d'un medical record
	public Person deletePerson(Person deletePerson) throws Exception {
		Person deleted = personRepository.deletePerson(deletePerson);

	    medicalRecordsService.deleteMedicalRecord(deletePerson.getFirstName(), deletePerson.getLastName());

	    return deleted;
	}

	/// URL

	// Recuperer liste d'enfants par leurs adresse
	public List<ChildByAddressDTO> getChildrenByAddress(String address) throws Exception {
		List<Person> getAllPerson = getAllPerson();
		List<MedicalRecords> getAllMedicalRecords = medicalRecordsService.getAllMedicalRecords();

		List<ChildByAddressDTO> filteredPersons = new ArrayList<>();
		List<String> familyMembers = new ArrayList<>();

		for (Person person : getAllPerson) {
			if (address.contains(person.getAddress())) {
				for (MedicalRecords medicalRecord : getAllMedicalRecords) {
					if (person.getFirstName().equals(medicalRecord.getFirstName())
							&& person.getLastName().equals(medicalRecord.getLastName())) {
						if (person.getLastName().equals(medicalRecord.getLastName())) {
							familyMembers.add(medicalRecord.getFirstName() + " " + medicalRecord.getLastName());
						}
						int age = medicalRecordsService.calculateAge(medicalRecord);
						if (age <= 18) {

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
