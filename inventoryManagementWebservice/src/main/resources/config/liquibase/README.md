# Database changes via Liquibase

Any changes to the database need to be done with Liquibase changelogs.
In subfolder changelog, create any SQL modification scripts you want to have applied to the database. The scripts will be executed in alphabetical order, therfore please follow the below naming conventions:
date_name.sql

The date needs to be in following format:
yyyyMMddHHmmss

For example, a changelog to create a table "teacher" added on 7th April 2023 17:11:21 would be named:
20230407171121_create_table_teacher.sql