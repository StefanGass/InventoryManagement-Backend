# Inventory Management Backend

## Legal information
License: MIT\
Authors: Stefan Gass & Isabella Zaby\
in cooperation with the University of Applied Sciences Burgenland, Department Information Technology

## Description
Provides backend application featuring:
+ Discovery server
+ API gateway + Load balancer
+ Restful webservices

Works only in combination with the InventoryManagement-Frontend application and MariaDB database.

## Prerequisites
    Java >= 17
    MVN >= 3.8
    Python >= 3.8

    MariaDB >= 10.2
     or
    Docker >= 20
    Docker-Compose >= 1

    Active directory
     (for authentication)

## Set up database
If you want to use a local database install MariaDB according to the included create_database.sql file. If you want to use docker instead, enter the following command in a new terminal window:
```bash
cd path_to_project_folder_containing_docker-compose.yml
docker-compose up
```

## Get started
To compile, open new terminal window, then:
```bash
cd path_to_project_folder_containing_pom.xml
mvn clean install
```
To start the application:
```bash
mvn spring-boot:run
# or
java -jar ./target/[application-version].jar
```

## Testing
To access Swagger enter the following URL in your browser:
```
http://ip.of.your.webapplication:port/swagger-ui.html
```
