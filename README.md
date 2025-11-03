## 🌐 Языки / Languages

- 🇷🇺 [Русский](README.ru.md)

# 💳 Account Management — Technical Test: Bank Account Simulator

Account Management is a simple bank account simulator developed as a technical assignment. The application demonstrates basic balance operations, user state handling, and protection against actions when the user is blocked.

---

## ⚙️ Features

### 👤 User

- Create a new user  
- View user information  
- Block a user (e.g., by admin decision)  
- Check block status before performing operations  

### 💰 Balance

- Deposit a specified amount  
- Withdraw funds (if sufficient balance)  
- Check current balance  
- Prevent operations if the user is blocked  

---

## 🛠️ Technologies

- Java 17+  
- Spring Boot  
- REST API  
- PostgreSQL  
- Liquibase  
- Docker  

---

## 🚀 Quick Start

1. Clone the repository:
   ```bash
   git clone https://github.com/noverlean/Account_Management.git
   cd Account_Management
   ```
   
2. Run the project using Docker:
```
bash
docker-compose up --build
```
3. Open in your browser:

```browser
http://localhost:8080/auth.html
```

🔐 Admin Credentials
```note
Login:    -Hako
Password:   100
```

---

##🗄️ PostgreSQL Settings
- Database: account_management_db
- Schema: account_management_db
- Port: 5432:5432
- Username: postgres
- Password: root
