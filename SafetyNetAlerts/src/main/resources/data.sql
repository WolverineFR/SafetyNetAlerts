DROP TABLE IF EXISTS person;

CREATE TABLE person (
  id INT AUTO_INCREMENT PRIMARY KEY,
  firstName VARCHAR(250) NOT NULL,
  lastName VARCHAR(250) NOT NULL,
  address VARCHAR(500) NOT NULL,
  city VARCHAR(250) NOT NULL,
  zip VARCHAR(100) NOT NULL,
  phone VARCHAR(100) NOT NULL,
  email VARCHAR(250) NOT NULL
);

INSERT INTO person (firstName, lastName, address, city, zip, phone, email) VALUES
  ("John","Boyd","1509 Culver St","Culver","97451","841-874-6512","jaboyd@email.com"),
  ("Jacob","Boyd","1509 Culver St","Culver","97451","841-874-6513","drk@email.com");
