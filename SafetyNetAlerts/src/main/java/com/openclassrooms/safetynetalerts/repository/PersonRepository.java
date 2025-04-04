package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.JsonService;

@Repository
public class PersonRepository {
	
	@Autowired
	private JsonService jsonService;
	private static String category = "persons";
	
	public PersonRepository (JsonService jsonService) {
		this.jsonService = jsonService;
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
				Person person = allPersonList.get(i);
				if (person.getFirstName().equalsIgnoreCase(updatePerson.getFirstName())
						&& person.getLastName().equalsIgnoreCase(updatePerson.getLastName())) {
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
				Person person = allPersonList.get(i);
				if (person.getFirstName().equalsIgnoreCase(deletePerson.getFirstName())
						&& person.getLastName().equalsIgnoreCase(deletePerson.getLastName())) {
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
}
