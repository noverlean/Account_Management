Sorry, i'm unsuccessfully tried set up docker, so you need do it manually.

=== POSTGRES SETTINGS ===
postgres must have a database "account_management_db" with chema that has the same name. 
ports 5432:5432. 
username: postgres, password: root.

=== FOR RUN APPICATION ===
mvn clean package
