# Smart Contact Manager (SCM 2.0)

A secure, cloud-based web app to store, search, and manage your contacts — with a modern, responsive SaaS-style UI and light/dark themes.

## 🔗 Live Demo

**https://smart-contract-manager.onrender.com**

> Hosted on Render's free tier, so the first request after a period of inactivity may take ~50 seconds to wake up. After that it's fast.

## ✨ Features

- Email + password authentication (register / login)
- Add, edit, delete, and search contacts (by name, email, or phone)
- Mark contacts as favorites
- Contact detail view + profile management with photo upload
- Direct email messaging to a contact
- Feedback submission
- Export contacts to Excel
- Fully responsive (tables become cards on mobile) with light & dark mode

## 🛠️ Tech Stack

- **Backend:** Spring Boot 3.2 (Spring MVC, Spring Security, Spring Data JPA / Hibernate), Java 21
- **Frontend:** Thymeleaf, Tailwind CSS, Flowbite, JavaScript
- **Database:** MySQL
- **Media:** Cloudinary (image uploads)
- **Build & Deploy:** Maven, Docker, Render (app) + Aiven (MySQL)

## 🚀 Run Locally

```bash
# 1. Start a MySQL database (or use Docker)
docker compose up -d mysql

# 2. Run the app
./mvnw spring-boot:run
```

Then open http://localhost:8081

Configuration is via environment variables (`MYSQL_*`, and optionally `EMAIL_*`, `CLOUDINARY_*`) — see `.env.example`.
