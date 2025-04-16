package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.Person;
import com.openclassrooms.safetynetalerts.service.JsonService;

@Repository
public class PersonRepository {

	@Autowired
	private JsonService jsonService;
	private static String category = "persons";

	public PersonRepository(JsonService jsonService) {
		this.jsonService = jsonService;
	}

	/**
	 * Récupère toutes les personnes depuis le fichier JSON.
	 *
	 * @return une liste de Person.
	 */
	public List<Person> getAllPerson() {
		return jsonService.readJsonFromFile(new TypeReference<List<Person>>() {
		}, category);
	}

	/**
	 * Sauvegarde la liste des personnes dans le fichier JSON.
	 *
	 * @param allPersonList : La liste des personnes à sauvegarder.
	 */
	public void savePersonToJson(List<Person> allPersonList) {
		jsonService.writeJsonToFile(category, allPersonList);
	}

	/**
	 * Ajoute une nouvelle personne à la liste et la sauvegarde.
	 *
	 * @param newPerson : La personne à ajouter.
	 * @return La personne ajoutée.
	 */
	public Person addPerson(Person newPerson) {
		List<Person> allPersonList = getAllPerson();
		allPersonList.add(newPerson);
		savePersonToJson(allPersonList);
		return newPerson;
	}

	/**
	 * Met à jour les informations d'une personne existante.
	 *
	 * @param firstName : Le prénom de la personne.
	 * @param lastName : Le nom de la personne.
	 * @param updatePerson : Les nouvelles données à mettre à jour.
	 * @return La personne mise à jour.
	 * @throws ResourceNotFoundException : Si aucune personne correspondante n’est trouvée.
	 */
	public Person updatePerson(String firstName, String lastName, Person updatePerson) {
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
			throw new ResourceNotFoundException("Aucunes personnes correspondante trouvées.");
		}
	}

	/**
	 * Supprime une personne de la liste.
	 *
	 * @param deletePerson : La personne à supprimer.
	 * @return La personne supprimée.
	 * @throws ResourceNotFoundException : Si la personne n'existe pas.
	 */
	public Person deletePerson(Person deletePerson) {
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
			throw new ResourceNotFoundException("Cette personne n'existe pas");
		}
	}
}
