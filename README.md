# Inventory Management Backend

## Legal
License: MIT\
Author: Stefan Gass

### inventoryManagementWebservice (Up to release 22.06.1):
Authors: Stefan Gass & Isabella Zaby\
in cooperation with the University of Applied Sciences Burgenland, department Information Technology

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

## Python packages
For inventoryManagementWebservice:
```bash
pip3 install brother-ql qrcode pillow reportlab
```
Keep in mind that the path to the "brother-ql" file has to be added to your $PATH variable for example like that (on Linux):
```bash
cd ~
nano ./.profile
# add the following line to the end of the script:
export PATH=$PATH:/home/userfolder/.local/bin
# safe, then proceed with
source ~/.profile
```

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
# IMPORTANT: Not the folder containing the parent bom, but the different subfolders instead, e. g. apiGatewayLoadBalancer !
mvn clean install
```

To start the application:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
# or
java -jar -Dspring.profiles.active=prod ./target/[application-version].jar
```

### Searching

Files must be manually reindexed if an exsting database is used or if data is modified outside Hibernate.
To Reindex call the following endpoint:

```
POST http://localhost:8089/api/inventorymanagement/inventory/search/reindex
```

## Description

Provides backend application featuring:

+ Discovery server
+ API gateway + Load balancer
+ Restful webservices

Works only in combination with the InventoryManagement-Frontend application and MariaDB database.

## Testing
To start the application in dev mode:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# or
java -jar -Dspring.profiles.active=dev ./target/[application-version].jar
```

To access Swagger enter the following URL in your browser:
```
http://ip.of.your.webapplication:port/swagger-ui.html
```
