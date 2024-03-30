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
     (for authentication in production mode)

## Python packages

For inventoryManagementWebservice:

```bash
pip3 install brother-ql qrcode pillow reportlab
```

Keep in mind that the path to the "brother-ql" file has to be added to your $PATH variable for example like that (on
Linux):

```bash
cd ~
nano ./.profile
# add the following line to the end of the script:
export PATH=$PATH:/home/userfolder/.local/bin
# safe, then proceed with
source ~/.profile
```

For brother-ql to work, TCP port forwarding on port 9100 must be enabled on your firewall.

## Set up database

If you want to use a local database install MariaDB according to the included create_database.sql file. If you want to
use docker instead, enter the following command in a new terminal window:

```bash
cd path_to_project_folder_containing_docker-compose.yml
docker-compose up
```

## Manually add password files

Before starting, the following files with the listed content must be included:

userManagementWebservice/main/resources/active-directory-binding-pwd.csv:

```
yourActiveDirectoryBindingPassword
```

For testing purpose, the file can contain anything, however, it must exist, otherwise one test will fail.

## Change settings

For production use, go through all the settings inside [webserice]/src/main/resources/application.properties,
/application-dev.properties & /application-prod.properties and replace the values by your personal settings:

```
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=https://YOUR.DOMAIN.NET
spring.datasource.password=YOUR_SUPER_SECRET_PASSWORD

active.directory.domain=YOUR.DOMAIN.NET
active.directory.url=ldap://YOUR.ACTIVE.DIRECTORY.URL:PORT
active.directory.url=YOUR.ACTIVE.DIRECTORY.URL:PORT
active.directory.search.base=OU=USERS,DC=YOUR,DC=DOMAIN,DC=NET
active.directory.binding.user=BINDING_USER

user.control.url=https://YOUR.DOMAIN.NET/api/usercontrol
```

If you want to start the services from your root folder instead, like e. g. IntelliJ does by default, don't forget to
change the paths
inside the [webserice]/src/main/resources/application.properties,
/application-dev.properties & /application-prod.properties files:

```
The name of the webservice is required in front of the relative path, for example: 
userManagementWebservice/src/main/resources/ instead of src/main/resources/
inventoryManagementWebservice/src/main/resources/ instead of src/main/resources/
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

## Description

Provides backend application featuring:

+ Discovery server
+ API gateway + Load balancer (optional)
+ Restful webservices

Developed for use in combination with the InventoryManagement-Frontend application and MariaDB database.

## Testing

Active directory is disabled for testing purposes, instead you can just log in with the testing user:\
Username: Super Admin\
Password: password

However, keep in mind that the password file userManagementWebservice/main/resources/active-directory-binding-pwd.csv
has to be created anyway, otherwise the matching test will fail.

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
