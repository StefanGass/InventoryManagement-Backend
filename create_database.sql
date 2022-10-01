CREATE USER 'inventorymanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';
CREATE USER 'usermanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';

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
    `group_id`     varchar(255),
    `team_leader`  boolean,
    `super_admin`  boolean,
    `last_login`   DATETIME,
    `active`       boolean
);


USE inventorymanagement;

DROP TABLE IF EXISTS change_history;
DROP TABLE IF EXISTS inventory_item;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS type;
DROP TABLE IF EXISTS category;

CREATE TABLE category
(
    `id`            int auto_increment primary key,
    `category_name` varchar(255)
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
    `id`                   int auto_increment primary key,
    `department_name`      varchar(255)
);

CREATE TABLE department_member
(
    `id`                   int auto_increment primary key,
    `user_id`              int,
    `department_id`        int,
    FOREIGN KEY (department_id)
        REFERENCES department (id)
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
    `issued_to`            varchar(255),
    `delivery_date`        DATETIME,
    `issue_date`           DATETIME,
    `dropping_date`        DATETIME,
    `dropping_reason`      varchar(255),
    `comments`             varchar(255),
    `status`               varchar(255),
    `active`               boolean,
    `department_id`        int,
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
    `id`           int auto_increment primary key,
    `picture_url`  varchar(255),
    `inventory_id` int,
    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

CREATE TABLE change_history
(
    `id`            int auto_increment primary key,
    `user`          varchar(255),
    `change_date`   DATETIME,
    `change_status` varchar(255),
    `inventory_id`  int,
    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

INSERT into category values(1, 'Testcategory');
INSERT into type values(1, 'Testtype', 1);
INSERT into location values(1, 'Testlocation');
INSERT into supplier values(1, 'Testsupplier', 'https://www.testsupplier.net');
INSERT into department values(1, 'Testdepartment');
INSERT into department_member values(1, 1, 1);
INSERT into inventory_item values(1, '123', null, 'test', 'ABC123', null, null, 11, 11, 0, 0, 'Testuser', null, null, null, null, null, null, true, 1);