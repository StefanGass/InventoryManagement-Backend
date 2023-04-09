CREATE USER 'usermanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';
CREATE USER 'inventorymanagement'@'%' IDENTIFIED BY 'YOUR_SUPER_SECRET_PASSWORD';

CREATE DATABASE IF NOT EXISTS inventorymanagement;
CREATE DATABASE IF NOT EXISTS usermanagement;

GRANT ALL PRIVILEGES ON *.* TO 'inventorymanagement'@'%';
GRANT ALL PRIVILEGES ON *.* TO 'usermanagement'@'%';
