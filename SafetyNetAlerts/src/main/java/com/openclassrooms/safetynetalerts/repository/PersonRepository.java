package com.openclassrooms.safetynetalerts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.openclassrooms.safetynetalerts.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
	Person findById(int id);
	
	
}
