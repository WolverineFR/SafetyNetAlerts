package com.openclassrooms.safetynetalerts.model;

import jakarta.persistence.Entity;
import lombok.Data;

@Data
@Entity
public class FireStation {
	private int id;
	private String address;
	private int station;
	
	public FireStation (int id, String address, int station) {
		this.id =id;
		this.address = address;
		this.station = station;
	}

	// Getter and Setter for ID
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	// Getter and Setter for address
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	// Getter and Setter for station number
	public int getStation() {
		return station;
	}

	public void setStation(int station) {
		this.station = station;
	}
}
