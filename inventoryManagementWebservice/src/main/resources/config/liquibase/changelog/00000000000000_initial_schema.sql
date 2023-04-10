CREATE TABLE IF NOT EXISTS category
(
    `id`            int auto_increment primary key,
    `category_name` varchar(255),
    `prefix`        varchar(10)
);

CREATE TABLE IF NOT EXISTS `type`
(
    `id`          int auto_increment primary key,
    `type_name`   varchar(255),
    `category_id` int,

     FOREIGN KEY (`category_id`)
        REFERENCES category (`id`)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS location
(
    `id`            int auto_increment primary key,
    `location_name` varchar(255)
);
CREATE TABLE IF NOT EXISTS supplier
(
    `id`            int auto_increment primary key,
    `supplier_name` varchar(255),
    `link`          varchar(255)
);

CREATE TABLE IF NOT EXISTS department
(
    `id`              int auto_increment primary key,
    `department_name` varchar(255)
);

CREATE TABLE IF NOT EXISTS printer
(
    `id`            int auto_increment primary key,
    `printer_name`  varchar(255),
    `printer_model` varchar(255),
    `printer_ip`    varchar(255),
    `label_format`  varchar(255)
);

CREATE TABLE IF NOT EXISTS department_member
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

CREATE TABLE IF NOT EXISTS inventory_item
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

CREATE TABLE IF NOT EXISTS picture
(
    `id`            int auto_increment primary key,
    `picture_url`   varchar(255),
    `thumbnail_url` varchar(255),
    `inventory_id`  int,

    FOREIGN KEY (inventory_id)
        REFERENCES inventory_item (id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS change_history
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