package com.openclassrooms.safetynetalerts.repository;

import java.io.IOException;

import org.springframework.stereotype.Repository;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.openclassrooms.safetynetalerts.model.MedicalRecords;

@Repository
public class MedicalRecordsRepository extends TypeAdapter<MedicalRecords> {

	@Override
	public void write(JsonWriter out, MedicalRecords value) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MedicalRecords read(JsonReader in) throws IOException {
		return null;
	}


}
