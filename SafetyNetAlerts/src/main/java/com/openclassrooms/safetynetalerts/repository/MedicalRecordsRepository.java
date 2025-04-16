package com.openclassrooms.safetynetalerts.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.openclassrooms.safetynetalerts.exception.ResourceNotFoundException;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;
import com.openclassrooms.safetynetalerts.service.JsonService;

@Repository
public class MedicalRecordsRepository {

	@Autowired
	private JsonService jsonService;
	private static String category = "medicalrecords";

	public MedicalRecordsRepository(JsonService jsonService) {
		this.jsonService = jsonService;
	}

	/**
	 * Récupère tous les dossiers médicaux depuis le fichier JSON.
	 *
	 * @return une liste de MedicalRecords.
	 */
	public List<MedicalRecords> getAllMedicalRecords() {
		return jsonService.readJsonFromFile(new TypeReference<List<MedicalRecords>>() {
		}, category);
	}

	/**
	 * Sauvegarde la liste des dossiers médicaux dans le fichier JSON.
	 *
	 * @param allMedicalRecordsList : La liste à sauvegarder.
	 */
	public void saveMedicalRecordsToJson(List<MedicalRecords> allMedicalRecordsList) {
		jsonService.writeJsonToFile(category, allMedicalRecordsList);
	}

	/**
	 * Ajoute un nouveau dossier médical et le sauvegarde.
	 *
	 * @param newMedicalRecords : Le dossier médical à ajouter.
	 * @return le dossier ajouté.
	 */
	public MedicalRecords addMedicalRecord(MedicalRecords newMedicalRecords) {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		allMedicalRecordsList.add(newMedicalRecords);
		saveMedicalRecordsToJson(allMedicalRecordsList);
		return newMedicalRecords;
	}

	/**
	 * Met à jour un dossier médical existant en fonction du prénom et du nom.
	 *
	 * @param firstName : Le prénom de la personne.
	 * @param lastName : Le nom de la personne.
	 * @param updateMedicalRecord : Les nouvelles données à mettre à jour.
	 * @return le dossier mis à jour.
	 * @throws ResourceNotFoundException : Si aucun dossier correspondant n’est trouvé.
	 */
	public MedicalRecords updateMedicalRecord(String firstName, String lastName, MedicalRecords updateMedicalRecord) {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		boolean isUpdated = false;

		for (int i = 0; i < allMedicalRecordsList.size(); i++) {
			MedicalRecords mr = allMedicalRecordsList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(updateMedicalRecord.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(updateMedicalRecord.getLastName())) {
				allMedicalRecordsList.set(i, updateMedicalRecord);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveMedicalRecordsToJson(allMedicalRecordsList);
			return updateMedicalRecord;
		} else {
			throw new ResourceNotFoundException("Aucun dossier médical correspondant trouvé.");
		}
	}

	/**
	 * Supprime un dossier médical existant.
	 *
	 * @param deleteMedicalRecord : Le dossier à supprimer.
	 * @return le dossier supprimé.
	 * @throws ResourceNotFoundException : Si le dossier n'existe pas.
	 */
	public MedicalRecords deleteMedicalRecord(MedicalRecords deleteMedicalRecord) {
		List<MedicalRecords> allMedicalRecordsList = getAllMedicalRecords();
		boolean isUpdated = false;

		for (int i = 0; i < allMedicalRecordsList.size(); i++) {
			MedicalRecords mr = allMedicalRecordsList.get(i);
			if (mr.getFirstName().equalsIgnoreCase(deleteMedicalRecord.getFirstName())
					&& mr.getLastName().equalsIgnoreCase(deleteMedicalRecord.getLastName())) {
				allMedicalRecordsList.remove(i);
				isUpdated = true;
				break;
			}
		}

		if (isUpdated) {
			saveMedicalRecordsToJson(allMedicalRecordsList);
			return deleteMedicalRecord;
		} else {
			throw new ResourceNotFoundException("Ce rapport medical n'existe pas");
		}
	}
}
