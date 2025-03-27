package com.openclassrooms.safetynetalerts.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.openclassrooms.safetynetalerts.CustomProperties;
import com.openclassrooms.safetynetalerts.dto.EmailOfAllPersonDTO;
import com.openclassrooms.safetynetalerts.dto.PersonInfoLastNameDTO;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.model.Person;

@Service
public class PersonService {
	@Autowired
	private final CustomProperties jsonFile;

	private JsonService jsonService;
	private static String category = "persons";

	private final MedicalRecordsService medicalRecordsService;

	public PersonService(MedicalRecordsService medicalRecordsService, CustomProperties jsonFile,
			JsonService jsonService) {
		this.jsonFile = jsonFile;
		this.jsonService = jsonService;
		this.medicalRecordsService = medicalRecordsService;
	}

	// Recuperer toutes les personnes
	public List<Person> getAllPerson() throws Exception {
		Type listType = new TypeToken<List<Person>>() {
		}.getType();
		return jsonService.readJsonFromFile(listType, category);
	}

	// Sauvegarder un medical record en json
	private void savePersonToJson(List<Person> allPersonList) {
		jsonService.writeJsonToFile(category, allPersonList);
	}

	// Ajouter un Person
	public void addPerson(Person newPerson) throws Exception {
		List<Person> allPersonList = getAllPerson();

		try {
			allPersonList.add(newPerson);
			savePersonToJson(allPersonList);
		} catch (JsonSyntaxException e) {
			throw new RuntimeException("Erreur lors de l'envoie vers le fichier JSON", e);
		}
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
