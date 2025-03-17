package com.openclassrooms.safetynetalerts.model;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class MedicalRecords {
	private String firstName;
	private String lastName;
	private String birthDate;
	private List<String> medications;
	private List<String> allergies;

	public MedicalRecords( String firstName, String lastName, String birthDate, List<String> medications, List<String> allergies) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthDate = birthDate;
		this.medications = medications;
		this.allergies = allergies;
	}

	// Getter and Setter for First Name
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// Getter and Setter for Last Name
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// Getter and Setter for birth date
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	// Getter and Setter for medications
	public List<String> getMedications() {
		return medications;
	}

	public void setMedications(List<String> medications) {
		this.medications = medications;
	}

	// Getter and Setter for allergies
	public List<String> getAllergies() {
		return allergies;
	}

	public void setAllergies(List<String> allergies) {
		this.allergies = allergies;
	}

}
