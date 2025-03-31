package com.openclassrooms.safetynetalerts.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.dto.ChildByAddressDTO;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;

@Service
public class PersonService {
	
	private JsonService jsonService;
	private static String category = "persons";

	private final MedicalRecordsService medicalRecordsService;

	public PersonService(MedicalRecordsService medicalRecordsService,
			JsonService jsonService) {
		this.jsonService = jsonService;
		this.medicalRecordsService = medicalRecordsService;
	}

	// Recuperer toutes les personnes
	public List<Person> getAllPerson() throws Exception {
		return jsonService.readJsonFromFile(new TypeReference<List<Person>>() {}, category);
	}

	// Sauvegarder un medical record en json
	private void savePersonToJson(List<Person> allPersonList) {
		jsonService.writeJsonToFile(category, allPersonList);
	}

	// Ajouter un Person
	public void addPerson(Person newPerson) throws Exception {
		List<Person> allPersonList = getAllPerson();
			allPersonList.add(newPerson);
			savePersonToJson(allPersonList);
	}

	// Mise à jour des données
	public Person updatePerson(Person updatePerson) throws Exception {
		List<Person> allPersonList = getAllPerson();
		boolean isUpdated = false;

		for (int i = 0; i < allPersonList.size(); i++) {
			Person mr = allPersonList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(updatePerson.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(updatePerson.getLastName())) {
				allPersonList.set(i, updatePerson);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			savePersonToJson(allPersonList);
			return updatePerson;
		} else {
			throw new RuntimeException("Aucunes personnes correspondante trouvées.");
		}
	}

	// Supression d'un medical record
	public Person deletePerson(Person deletePerson) throws Exception {
		List<Person> allPersonList = getAllPerson();
		boolean isUpdated = false;

		for (int i = 0; i < allPersonList.size(); i++) {
			Person mr = allPersonList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(deletePerson.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(deletePerson.getLastName())) {
				allPersonList.remove(i);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			savePersonToJson(allPersonList);
			return deletePerson;
		} else {
			throw new RuntimeException("Cette personne n'existe pas");
		}
	}

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
