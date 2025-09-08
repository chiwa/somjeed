# Somjeed Chatbot Demo

Somjeed is a lightweight chatbot prototype built for **XXXXXX assignment**.  
It demonstrates conversation flow with greeting, intent prediction, intent detection, inactivity handling, and feedback collection.

---

## 🚀 Tech Stack
- **Backend:** Spring Boot 3.5 (Java 21), REST API, Swagger/OpenAPI
- **Frontend:** React 18 (Vite), simple chat UI
- **Infra:** Docker Compose (multi-service FE+BE)
- **Testing:** JUnit 5, Mockito, MockMvc
- **Feedback Storage:** In-Memory Repository (pluggable for DB later)

---

## 🏗️ System Architecture
![High-Level Architecture](/docs/diagrams/architecture.png)

---

## 🔄 Conversation Sequence
![Conversation Flow](/docs/diagrams/sequence.png)

## 🔄 Conversation Activity
![Conversation Flow](/docs/diagrams/activity.png)

Flow includes:
1. Greeting + Intent Prediction
2. User response (yes → answerForYes, else → detection)
3. Inactivity nudges (10s → ask again, 20s → goodbye & request feedback)
4. Feedback submission

---

## 📦 Class Diagram
![Class Diagram](/docs/diagrams/class-diagram.png)

---

## ⚡ How to Run

### 1. Build & Run with Docker Compose
```bash
docker compose down -v --remove-orphans
docker builder prune -f
docker image prune -f
docker compose up --build -d