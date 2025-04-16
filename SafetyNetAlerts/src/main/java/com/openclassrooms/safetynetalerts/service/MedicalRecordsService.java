package com.openclassrooms.safetynetalerts.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.MedicalRecordException;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.repository.MedicalRecordsRepository;

/**
 * Service qui gère les opérations sur les dossiers médicaux, telles que l'ajout, la mise à jour, la suppression,
 * le calcul de l'âge et la récupération de tous les dossiers médicaux.
 * 
 */
@Service
public class MedicalRecordsService {

	@Autowired
	private final MedicalRecordsRepository medicalRecordsRepository;
	private final PersonService personService;
	
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	 /**
     * Constructeur du service MedicalRecordsService.
     * 
     * @param medicalRecordsRepository : Le repository pour accéder aux dossiers médicaux.
     * @param personService : Le service permettant de gérer les personnes.
     */
	public MedicalRecordsService(MedicalRecordsRepository medicalRecordsRepository, @Lazy PersonService personService) {
		this.medicalRecordsRepository = medicalRecordsRepository;
		this.personService = personService;
	}

	 /**
     * Récupère tous les dossiers médicaux disponibles dans le système.
     * 
     * @return Une liste de tous les dossiers médicaux.
     */
	public List<MedicalRecords> getAllMedicalRecords(){
		return medicalRecordsRepository.getAllMedicalRecords();
	}

	 /**
     * Ajoute un nouveau dossier médical après avoir validé les données.
     * 
     * @param newMedicalRecords : Le dossier médical à ajouter.
     * @return Le dossier médical ajouté.
     * @throws MedicalRecordException : Si les données sont invalides ou si un dossier existe déjà pour cette personne.
     */
	public MedicalRecords addMedicalRecord(MedicalRecords newMedicalRecords) throws MedicalRecordException {
		if (newMedicalRecords.getFirstName() == null || newMedicalRecords.getFirstName().isBlank()
				|| newMedicalRecords.getLastName() == null || newMedicalRecords.getLastName().isBlank()) {
			throw new MedicalRecordException("Le prénom et le nom ne peuvent pas être vides.");
		}

		if (newMedicalRecords.getBirthDate() == null || newMedicalRecords.getBirthDate().isBlank()) {
			throw new MedicalRecordException("La date de naissance ne peut pas être vide.");
		}

		try {
			LocalDate.parse(newMedicalRecords.getBirthDate(), FORMATTER);
		} catch (DateTimeParseException e) {
			throw new MedicalRecordException("La date de naissance doit être au format MM/dd/yyyy.");
		}

		List<MedicalRecords> allMedicalRecords = medicalRecordsRepository.getAllMedicalRecords();

		boolean alreadyExists = allMedicalRecords.stream()
				.anyMatch(mr -> mr.getFirstName().equalsIgnoreCase(newMedicalRecords.getFirstName())
						&& mr.getLastName().equalsIgnoreCase(newMedicalRecords.getLastName()));

		if (alreadyExists) {
			throw new MedicalRecordException("Un dossier médical existe déjà pour cette personne.");
		}

		return medicalRecordsRepository.addMedicalRecord(newMedicalRecords);
	}

	/**
     * Met à jour un dossier médical existant pour une personne donnée.
     * 
     * @param firstName : Le prénom de la personne à mettre à jour.
     * @param lastName : Le nom de la personne à mettre à jour.
     * @param updateMedicalRecord : Les nouvelles informations du dossier médical.
     * @return Le dossier médical mis à jour.
     * @throws ResourceNotFoundException : Si aucun dossier médical n'est trouvé pour la personne.
     * @throws IllegalArgumentException : Si les informations du nom et prénom dans l'URL ne correspondent pas à celles du corps de la requête.
     */
	public MedicalRecords updateMedicalRecord(String firstName, String lastName, MedicalRecords updateMedicalRecord)
			throws ResourceNotFoundException {
		if (!firstName.equalsIgnoreCase(updateMedicalRecord.getFirstName())
				|| !lastName.equalsIgnoreCase(updateMedicalRecord.getLastName())) {
			throw new IllegalArgumentException(
					"Prénom et nom de l'URL ne correspondent pas à ceux du corps de la requête.");
		}
		return medicalRecordsRepository.updateMedicalRecord(firstName, lastName, updateMedicalRecord);
	}

	/**
     * Supprime un dossier médical pour une personne donnée.
     * 
     * @param firstName : Le prénom de la personne dont le dossier médical doit être supprimé.
     * @param lastName : Le nom de la personne dont le dossier médical doit être supprimé.
     * @throws ResourceNotFoundException : Si aucun dossier médical n'est trouvé pour la personne.
     */
	public void deleteMedicalRecord(String firstName, String lastName) throws ResourceNotFoundException {
		List<MedicalRecords> allMedicalRecords = getAllMedicalRecords();
		boolean removed = allMedicalRecords.removeIf(mr -> mr.getFirstName().equalsIgnoreCase(firstName)
				&& mr.getLastName().equalsIgnoreCase(lastName));

		if (removed) {
			medicalRecordsRepository.saveMedicalRecordsToJson(allMedicalRecords);
		} else {
			throw new ResourceNotFoundException("Aucun dossier médical trouvé pour cette personne.");
		}
		
		try {
		    personService.deletePerson(firstName, lastName);
		} catch (ResourceNotFoundException e) {
		}
	}
	
	/**
     * Calcule l'âge d'une personne à partir de son dossier médical.
     * 
     * @param medicalRecords : Le dossier médical de la personne.
     * @return L'âge de la personne calculé en années.
     */
	public int calculateAge(MedicalRecords medicalRecords) {
		String birthDate = medicalRecords.getBirthDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyy");
		LocalDate birthLocalDate = LocalDate.parse(birthDate, formatter);

		return Period.between(birthLocalDate, LocalDate.now()).getYears();
	}

}
