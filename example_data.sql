USE usermanagement;

INSERT into user
values (1, 'Super', 'Admin', null, 1, 1, 1, 1, null, 1);

USE inventorymanagement;

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