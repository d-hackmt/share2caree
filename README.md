# ShareToCare 🤝

ShareToCare is a donation management platform designed to bridge the gap between donors and verified NGOs. Our mission is to enable a transparent and efficient redistribution of essential items such as food, clothing, books, and games to those in need.

## 🌟 Key Features

- **Donor Registration**: Easily register as a donor to contribute items.
- **NGO Management**: Verified NGOs can manage their status and receive notifications.
- **Proximity Notifications**: (In Development) Notifying NGOs based on their radius from donors.
- **Transparent Tracking**: Ensuring donations reach their intended destination.

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:
- [Java 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [MySQL Server](https://dev.mysql.com/downloads/installer/)

### Backend Setup (Spring Boot)

1.  **Configure Database**:
    - Create a database named `shareToCare` in your MySQL server.
    - Open `Backend/src/main/resources/application.properties` and update the credentials:
      ```properties
      spring.datasource.url=jdbc:mysql://localhost:3306/shareToCare
      spring.datasource.username=your_username
      spring.datasource.password=your_password
      ```

2.  **Build and Run**:
    - Open a terminal in the `Backend` directory.
    - Execute the following command:
      ```bash
      mvn spring-boot:run
      ```
    - The server will start on [http://localhost:8080](http://localhost:8080).

### Frontend Setup (HTML/JS/CSS)

The frontend is built with vanilla web technologies and does not require a complex build process.

1.  Open `Frontend/index.html` directly in any modern web browser.
2.  Make sure the Backend is running to allow for registration and data interactions.

## 📂 Project Structure

```text
ShareToCare/
├── Backend/          # Spring Boot Main Application
│   ├── src/          # Java Source and Resources
│   └── pom.xml       # Maven Dependencies
├── Frontend/         # Static Web Files
│   ├── admin/        # Admin Dashboard
│   ├── donor/        # Donor Interface
│   ├── ngo/          # NGO Interface
│   └── js/           # logic and API calls
└── .gitignore        # Centralized ignore rules
```

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot, Spring Data JPA, MySQL
- **Frontend**: Modern HTML5, CSS3, JavaScript
- **Management**: Maven, Git

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details (if applicable).
