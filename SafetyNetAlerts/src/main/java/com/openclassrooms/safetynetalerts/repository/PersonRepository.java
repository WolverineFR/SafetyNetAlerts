package com.openclassrooms.safetynetalerts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.safetynetalerts.model.Person;


@Repository
public interface PersonRepository extends CrudRepository<Person, Long>{

}
