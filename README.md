Sorry, i'm unsuccessfully tried set up docker, so you need do it manually.

=== ADMIN CREDENTIALS ===
login: -Hako 
password: 100

=== POSTGRES SETTINGS ===
postgres must have a database "account_management_db" with chema that has the same name. 
ports 5432:5432. 
username: postgres, password: root.

=== TO RUN APPICATION ===
$ mvn clean package

browser url:    http://localhost:8080/auth.html
