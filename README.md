# 📦 Granotec Inventory System

Sistema web para la **gestión de inventario** desarrollado con **Spring Boot (Java)**, **Angular**, y **MySQL**, desplegable en **AWS**.  

Este proyecto se desarrolla en equipo como parte del curso universitario, siguiendo buenas prácticas de control de versiones y flujo de trabajo colaborativo con Git.

---

## 🚀 Tecnologías utilizadas

- **Backend**: [Spring Boot](https://spring.io/projects/spring-boot) (Java)
- **Frontend**: [Angular](https://angular.io/)
- **Base de datos**: [MySQL](https://www.mysql.com/)
- **Control de versiones**: [Git & GitHub](https://github.com/)
- **Despliegue**: [AWS](https://aws.amazon.com/)

---

## 📂 Estructura del proyecto

inventory-system/
│
├── backend/ # Proyecto Spring Boot (Java)
│ ├── src/ # Código fuente del backend
│ ├── pom.xml # Dependencias (Maven)
│ └── application.properties
│
├── frontend/ # Proyecto Angular
│ ├── src/ # Código fuente del frontend
│ ├── package.json # Dependencias (npm)
│ └── angular.json
│
├── database/ # Scripts de base de datos (opcional)
│ ├── schema.sql
│ └── data.sql
│
├── README.md # Documentación principal
└── CONTRIBUTING.md # Guía para contribuir al proyecto


---

## ⚙️ Configuración del backend (Spring Boot + MySQL)

1. Crear la base de datos en MySQL:
   ```sql
   CREATE DATABASE inventory_db;

2. Configurar las credenciales en backend/src/main/resources/application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Configuración de JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

3. Ejecutar el backend:
cd backend
./mvnw spring-boot:run

El backend estará disponible en:
👉 http://localhost:8080

## 🎨 Configuración del frontend (Angular) 
1. Instalar dependencias: 
cd frontend/inventory-frontend
npm install

2. Ejecutar el servidor de desarrollo: 
ng serve -o

El frontend estará disponible en:
👉 http://localhost:4200

## 👥 Colaboración y ramas

main: rama estable (producción).
develop: rama de integración.
feature/...: ramas de funcionalidades individuales.

