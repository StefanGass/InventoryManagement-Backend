# Inventory Management Backend

## Legal

License: MIT\
Author: Stefan Gass

### inventoryManagementWebservice (Up to release 22.06.1):

Authors: Stefan Gass & Isabella Zaby\
in cooperation with the University of Applied Sciences Burgenland, department Information Technology

## Prerequisites

    Java >= 17
    Maven >= 3.8
    Python >= 3.10
    PythonVenv >= 3.10

    MariaDB >= 10.2
     or
    Docker >= 20

    For production use:
    Webserver like Apache or nginx
    Microsoft active directory
     (for authentication in production mode)

    Optional for printing: 
    Brother QL-820NWB label printer
    17x54mm labels

## Description

Provides backend application featuring:

+ Discovery server, running on port 8081
+ API gateway + Load balancer (optional), running on port 8083
+ User authentication webservice, running on port 8086
+ User management webservice, running on port 8087
+ Inventory management webservice, running on port 8089

Developed for use in combination with the InventoryManagement-Frontend
application ( https://github.com/StefanGass/InventoryManagement-Frontend ) and MariaDB database.

## Getting started

### Set up python virtual environment

Before starting the application, the required python packages have to be installed. You can set up a virtual environment
like this:

```bash
cd PATH/TO/PROJECT/ROOT/DIRECTORY
python3 -m venv ./venv
source ./venv/bin/activate
pip3 install -r ./pythonPackages.txt
```

The according libraries will then be installed automatically as listed inside pythonPackages.txt.\
Note: For brother-ql library to work, TCP port forwarding on port 9100 must be enabled on your firewall.

### Set up database

#### Use docker to set up the database automatically...

If you want to use docker instead, enter the following command in a new terminal window:

```bash
docker compose up
```

#### ... or set up the database locally and manually

If you want to use a local database install MariaDB according to the included create_database.sql file.

**ATTENTION**\
Note, that you have to create own database tables and users for each and every webservice.\
If you want to use different passwords, replace YOUR_SUPER_SECRET_PASSWORD in line 1 and 2 of create_database.sql with
your personal passwords. Don't forget that you also have to use those passwords inside the spring configurations files (
application.properties, application-dev-properties, application-prod.properties), as described later in the process.\
Furthermore, if you want to insert the test user and the test values into the database, uncomment the lines 47 - 51 and
205 - 231 by removing the trailing # in every line.

### Manually add password files for active directory

Next, the following files with the listed content must be created for the authentication process to work:

userManagementWebservice/main/resources/active-directory-binding-pwd.csv:

```
yourActiveDirectoryBindingPassword
```

For testing purpose, the file can contain any string, however, **it must exist, otherwise one test execution will fail
**.

### Change spring boot settings

For production use, replace all variables written in capital letters in the following files with your personal data:

**apiGatewayLoadBalancer/src/main/resources/application.properties**\
line 27: spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=https://YOUR.DOMAIN.NET

**apiGatewayLoadBalancer/src/main/resources/application-prod.properties**\
line 5: spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=https://YOUR.DOMAIN.NET

**userControlWebservice/src/main/resources/application.properties**\
line 12: active.directory.domain=YOUR.DOMAIN.NET
line 13: active.directory.url=ldap://YOUR.ACTIVE.DIRECTORY.URL:PORT

**userManagementWebservice/src/main/resources/application.properties**\
line 8, only if you changed the database password beforehand: spring.datasource.password=YOUR_SUPER_SECRET_PASSWORD
line 18: active.directory.url=YOUR.ACTIVE.DIRECTORY.URL:PORT
line 19 (do not change OU and DC): active.directory.search.base=OU=USERS,DC=YOUR,DC=DOMAIN,DC=NET
line 21: active.directory.binding.user=BINDING_USER
line 29: user.control.url=https://YOUR.DOMAIN.NET/api/usercontrol

**userManagementWebservice/src/main/resources/application-prod.properties**\
line 5: user.control.url=https://YOUR.DOMAIN.NET/api/usercontrol

**inventoryManagementWebservice/src/main/resources/application.properties**\
line 8, only if you changed the database password beforehand: spring.datasource.password=YOUR_SUPER_SECRET_PASSWORD

Note: You should start the microservices from within their dedicated directory, e. g. apiGatewayLoadBalancer.
If you want to start the services from your root folder instead, like e. g. IntelliJ does by default, don't forget
to change the relative paths inside the userManagementWebservice/src/main/resources/application.properties, line 26, and
inventoryManagementWebservice/src/main/resources/application.properties, lines 21 - 25, like described in the comments
above those lines.

## Compile and run the application

### Compile the applications

Open a terminal session and run the following commands:

```bash
cd PATH/TO/PROJECT/ROOT/DIRECTORY
source ./venv/bin/activate # otherwise the tests for the inventoryManagementWebservice will fail
mvn clean install
```

### Every microservice needs its own terminal session

Now that everything's compiled, you can start all applications either in production or development mode.\
Note that every microservice needs it own terminal session. I recommend using screens for this:

```bash
apt install screen # to install screen
screen -AmdS nameOfTheMicroservice # to open a new screen session
screen -r # to list all running screen sessions
screen -r nameOfTheMicroservice # to resume an already running screen session
# Ctrl + A + D to leave a running screen session without killing it
```

### Starting order

The application has to be started in the following order:

1. discoveryServer
2. apiGatewayLoadBalancer
3. userControlWebservice
4. userManagementWebservice
5. inventoryManagementWebservice or any other webservice you might include yourself in any order after starting
   everything else

### Run the application in production mode...

Open terminal sessions and run the following commands:

```bash
cd PATH/TO/PROJECT/ROOT/DIRECTORY/MICROSERVICE
source ../venv/bin/activate # only for inventoryManagementWebservice, as it needs to call some python scripts
mvn spring-boot:run -Dspring-boot.run.profiles=prod
# or alternatively, even though I recommend using the above command
java -jar -Dspring.profiles.active=prod ./target/[application-version].jar
```

### ...or run the application in development mode

Open terminal sessions and run the following commands:

```bash
cd PATH/TO/PROJECT/ROOT/DIRECTORY/MICROSERVICE
source ../venv/bin/activate # only for inventoryManagementWebservice, as it needs to call some python scripts
mvn spring-boot:run -Dspring-boot.run.profiles=dev
# or alternatively, even though I recommend using the above command
java -jar -Dspring.profiles.active=prod ./target/[application-version].jar
```

## Access the web application

For accessing the backend, you need to use my frontend application or any other frontend application you developed
yourself.

For logging into the webservice, a microsoft active directory is needed. However, the active directory is disabled 
for testing purposes if you started the application in development mode. Instead you can just log in with the testing 
user, **if and only if the user exists in the database's user table, meaning you have uncommented the according 
lines 47 - 51 in create_database.sql like mentioned above or if you manually inserted the user later on** :\
Username: Super Admin\
Password: password

To access userManagements or inventoryManagements swagger, e. g. for testing purposes if you don't want to use a frontend 
application, enter the following URL in your browser:

```
http://IP.OF.YOUR.MICROSERVICE:PORT/swagger-ui.html
```

Additionally, some of the microservice offer web GUIs by default, e. g. the discovery server, which you can also access
in your web browser:

```
http://IP.OF.YOUR.MICROSERVICE:PORT
```

## Managing user permissions

As soon as an user tries to log into the inventory management webservice through the frontend and it exists within active 
directory, it will be added to the database's user table. However, for security reasons, it does not have permissions to 
enter the according webservice by default. Therefor you have to set auth_inventorymanagement, admin or super_admin to true 
manually inside the database.

## Logging

Every microservice has logging implemented. Log files will be saved inside the following folders:
PATH/TO/PROJECT/ROOT/DIRECTORY/MICROSERVICE/logs