CREATE TABLE IF NOT EXISTS user
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