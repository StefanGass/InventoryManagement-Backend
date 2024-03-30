CREATE USER 'usermanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';
CREATE USER 'inventorymanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';

DROP DATABASE IF EXISTS inventorymanagement;
DROP DATABASE IF EXISTS usermanagement;

CREATE DATABASE IF NOT EXISTS inventorymanagement;
CREATE DATABASE IF NOT EXISTS usermanagement;

GRANT ALL PRIVILEGES ON *.* TO 'inventorymanagement'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'usermanagement'@'%';

############################################################################

USE usermanagement;

# DROP TABLE IF EXISTS user;
# DROP TABLE IF EXISTS team;

CREATE TABLE IF NOT EXISTS team
(
    `id`   INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS user
(
    `id`                        INT AUTO_INCREMENT PRIMARY KEY,
    `user_logon_name`           VARCHAR(255),
    `first_name`                VARCHAR(255),
    `last_name`                 VARCHAR(255),
    `mail_address`              VARCHAR(255),
    `team_id`                   INT,
    `team_leader`               BOOLEAN,
    `admin`                     BOOLEAN,
    `super_admin`               BOOLEAN,
    `last_login`                DATETIME,
    `active`                    BOOLEAN,
    `auth_user_management`      BOOLEAN,
    `auth_inventory_management` BOOLEAN,

    FOREIGN KEY (team_id)
        REFERENCES team (id)
        ON DELETE CASCADE
);

INSERT INTO team
VALUES (1, 'Test-Gruppe');

INSERT INTO user
VALUES (1, 'Super Admin', 'Super', 'Admin', null, 1, true, true, true, null, true, false, false);

############################################################################

USE inventorymanagement;

# DROP TABLE IF EXISTS change_history;
# DROP TABLE IF EXISTS picture;
# DROP TABLE IF EXISTS inventory_item;
# DROP TABLE IF EXISTS department_member;
# DROP TABLE IF EXISTS printer;
# DROP TABLE IF EXISTS department;
# DROP TABLE IF EXISTS supplier;
# DROP TABLE IF EXISTS location;
# DROP TABLE IF EXISTS type;
# DROP TABLE IF EXISTS category;

CREATE TABLE IF NOT EXISTS category
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `category_name` VARCHAR(255),
    `prefix`        VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS `type`
(
    `id`          INT AUTO_INCREMENT PRIMARY KEY,
    `type_name`   VARCHAR(255),
    `category_id` INT,

    FOREIGN KEY (`category_id`)
        REFERENCES category (`id`)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS location
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `location_name` VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS supplier
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `supplier_name` VARCHAR(255),
    `link`          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS department
(
    `id`              INT AUTO_INCREMENT PRIMARY KEY,
    `department_name` VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS printer
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `printer_name`  VARCHAR(255),
    `printer_model` VARCHAR(255),
    `printer_ip`    VARCHAR(255),
    `label_format`  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS department_member
(
    `id`                INT AUTO_INCREMENT PRIMARY KEY,
    `user_id`           INT,
    `department_id`     INT,
    `dropping_reviewer` BOOLEAN,
    `printer_id`        INT,

    FOREIGN KEY (department_id)
        REFERENCES department (id)
        ON DELETE CASCADE,

    FOREIGN KEY (printer_id)
        REFERENCES printer (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS inventory_item
(
    `id`                       INT AUTO_INCREMENT PRIMARY KEY,
    `item_internal_number`     VARCHAR(255),
    `type_id`                  INT,
    `item_name`                VARCHAR(255),
    `serial_number`            VARCHAR(255),
    `location_id`              INT,
    `supplier_id`              INT,
    `pieces`                   INT,
    `pieces_stored`            INT,
    `pieces_issued`            INT,
    `pieces_dropped`           INT,
    `issued_to`                VARCHAR(1275),
    `delivery_date`            DATETIME,
    `issue_date`               DATETIME,
    `dropping_date`            DATETIME,
    `dropping_reason`          VARCHAR(1275),
    `comments`                 VARCHAR(1275),
    `status`                   VARCHAR(255),
    `active`                   BOOLEAN,
    `department_id`            INT,
    `old_item_number`          VARCHAR(255),
    `dropping_queue`           VARCHAR(100),
    `dropping_queue_pieces`    INT,
    `dropping_queue_reason`    VARCHAR(1275),
    `dropping_queue_requester` INT,
    `dropping_queue_date`      DATETIME,

    FOREIGN KEY (location_id)
        REFERENCES location (id)
        ON DELETE CASCADE,

    FOREIGN KEY (supplier_id)
        REFERENCES supplier (id)
        ON DELETE CASCADE,

    FOREIGN KEY (type_id)
        REFERENCES type (id)
        ON DELETE CASCADE,

    FOREIGN KEY (department_id)
        REFERENCES department (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS picture
(
    `id`            INT AUTO_INCREMENT PRIMARY KEY,
    `picture_url`   VARCHAR(255),
    `thumbnail_url` VARCHAR(255),
    `inventory_id`  INT,

    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS change_history
(
    `id`             INT AUTO_INCREMENT PRIMARY KEY,
    `user`           VARCHAR(255),
    `change_date`    DATETIME,
    `change_status`  VARCHAR(255),
    `change_history` VARCHAR(2550),
    `inventory_id`   INT,

    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

INSERT INTO category
VALUES (1, 'Testcategory', 'TEST');

INSERT INTO type
VALUES (1, 'Testtype', 1);

INSERT INTO location
VALUES (1, 'Testlocation');

INSERT INTO supplier
VALUES (1, 'Testsupplier', 'https://www.testsupplier.at');

INSERT INTO department
VALUES (1, 'Testdepartment');

INSERT INTO printer
VALUES (1, 'TEST-1', 'QL-820NWB', 'tcp://192.168.6.181', '17x54');

INSERT INTO department_member
VALUES (1, 1, 1, true, 1);

INSERT INTO inventory_item
VALUES (1, 'TEST-2022-0001', 1, 'test', 'ABC123', 1, 1, 11, 11, 0, 0, '', null, null, null, '', '', 'LAGERND', true,
        1, '', null, null, null, null, null);

INSERT INTO change_history
VALUES (1, 1, '2022-08-18 10:17:26', 'Inventargegenstand angelegt.', 'Test test test', 1);
