CREATE USER 'usermanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';
CREATE USER 'inventorymanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';

DROP DATABASE IF EXISTS inventorymanagement;
DROP DATABASE IF EXISTS usermanagement;

CREATE DATABASE IF NOT EXISTS inventorymanagement;
CREATE DATABASE IF NOT EXISTS usermanagement;

GRANT ALL PRIVILEGES ON *.* TO 'inventorymanagement'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'usermanagement'@'%';


USE usermanagement;

DROP TABLE IF EXISTS user;

CREATE TABLE user
(
    `id`           int auto_increment primary key,
    `first_name`   varchar(255),
    `last_name`    varchar(255),
    `mail_address` varchar(255),
    `group_id`     int,
    `team_leader`  boolean,
    `admin`        boolean,
    `super_admin`  boolean,
    `last_login`   DATETIME,
    `active`       boolean
);

INSERT into user
values (1, 'Super', 'Admin', null, 1, 1, 1, 1, null, 1);

USE inventorymanagement;

DROP TABLE IF EXISTS change_history;
DROP TABLE IF EXISTS picture;
DROP TABLE IF EXISTS inventory_item;
DROP TABLE IF EXISTS department_member;
DROP TABLE IF EXISTS printer;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS type;
DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    `id`            int auto_increment primary key,
    `category_name` varchar(255),
    `prefix`        varchar(10)
);

CREATE TABLE `type`
(
    `id`          int auto_increment primary key,
    `type_name`   varchar(255),
    `category_id` int,

    FOREIGN KEY (`category_id`)
        REFERENCES category (`id`)
        ON DELETE CASCADE
);

CREATE TABLE location
(
    `id`            int auto_increment primary key,
    `location_name` varchar(255)
);

CREATE TABLE supplier
(
    `id`            int auto_increment primary key,
    `supplier_name` varchar(255),
    `link`          varchar(255)
);

CREATE TABLE department
(
    `id`              int auto_increment primary key,
    `department_name` varchar(255)
);

CREATE TABLE printer
(
    `id`            int auto_increment primary key,
    `printer_name`  varchar(255),
    `printer_model` varchar(255),
    `printer_ip`    varchar(255),
    `label_format`  varchar(255)
);

CREATE TABLE department_member
(
    `id`                int auto_increment primary key,
    `user_id`           int,
    `department_id`     int,
    `dropping_reviewer` boolean,
    `printer_id`        int,

    FOREIGN KEY (department_id)
        REFERENCES department (id)
        ON DELETE CASCADE,

    FOREIGN KEY (printer_id)
        REFERENCES printer (id)
        ON DELETE CASCADE
);

CREATE TABLE inventory_item
(
    `id`                   int auto_increment primary key,
    `item_internal_number` varchar(255),
    `type_id`              int,
    `item_name`            varchar(255),
    `serial_number`        varchar(255),
    `location_id`          int,
    `supplier_id`          int,
    `pieces`               int,
    `pieces_stored`        int,
    `pieces_issued`        int,
    `pieces_dropped`       int,
    `issued_to`            varchar(1275),
    `delivery_date`        DATETIME,
    `issue_date`           DATETIME,
    `dropping_date`        DATETIME,
    `dropping_reason`      varchar(1275),
    `comments`             varchar(1275),
    `status`               varchar(255),
    `active`               boolean,
    `dropping_queue`       boolean,
    `department_id`        int,
    `old_item_number`      varchar(255),

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

CREATE TABLE picture
(
    `id`            int auto_increment primary key,
    `picture_url`   varchar(255),
    `thumbnail_url` varchar(255),
    `inventory_id`  int,

    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

CREATE TABLE change_history
(
    `id`             int auto_increment primary key,
    `user`           varchar(255),
    `change_date`    DATETIME,
    `change_status`  varchar(255),
    `change_history` varchar(2550),
    `inventory_id`   int,

    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

INSERT into category
values (1, 'Testcategory', 'TEST');

INSERT into type
values (1, 'Testtype', 1);

INSERT into location
values (1, 'Testlocation');

INSERT into supplier
values (1, 'Testsupplier', 'https://www.testsupplier.net');

INSERT into department
values (1, 'Testdepartment');

INSERT into printer
values (1, 'TEST-1', 'QL-820NWB', 'tcp://192.168.0.5', '17x54');

INSERT into department_member
values (1, 1, 1, true, 1);

INSERT into inventory_item
values (1, 'TEST-2022-0001', 1, 'test', 'ABC123', 1, 1, 11, 11, 0, 0, '', null, null, null, '', '', 'LAGERND', true,
        false, 1, '');

INSERT into change_history
values (1, 1, '2022-08-18 10:17:26', 'Inventargegenstand angelegt.', 'Test test test', 1);