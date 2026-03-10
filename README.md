# 🏋️ AI Fitness App — Microservice Architecture

A full-stack AI-powered fitness tracking application built with **Spring Boot microservices**, **React (Vite)**, **Keycloak** for authentication, **RabbitMQ** for async messaging, and **Google Gemini** for AI-generated workout recommendations.

---

## 📐 Architecture Overview

```
                        ┌─────────────────────┐
                        │   React Frontend     │
                        │   (Vite + MUI)       │
                        │   localhost:5173     │
                        └────────┬────────────┘
                                 │ HTTP (Bearer JWT)
                        ┌────────▼────────────┐
                        │    API Gateway       │
                        │    port: 8085        │
                        │  (Spring Cloud GW)   │
                        └──┬──────┬──────┬────┘
                           │      │      │
             /api/users/**  │      │ /api/activities/**
                           │      │      │ /api/ai/**
               ┌───────────┘      │      └──────────────┐
               ▼                  ▼                      ▼
        ┌────────────┐   ┌─────────────────┐   ┌───────────────┐
        │ User Svc   │   │  Activity Svc   │   │   AI Service  │
        │ port: 8082 │   │  port: 8083     │   │  port: 8084   │
        └────────────┘   └────────┬────────┘   └───────┬───────┘
                                  │ RabbitMQ            │ Gemini API
                                  └─────────────────────┘
                                  fitness.exchange
                                  activity.queue

        ┌──────────────┐   ┌──────────────┐
        │  Eureka      │   │ Config Svr   │
        │  port: 8761  │   │ port: 8888   │
        └──────────────┘   └──────────────┘
```

---

## 🧩 Services

| Service | Port | Description |
|---|---|---|
| **API Gateway** | 8085 | Single entry point. Routes requests, validates JWT via Keycloak, syncs users |
| **User Service** | 8082 | Manages user profiles |
| **Activity Service** | 8083 | Tracks user workouts, publishes to RabbitMQ |
| **AI Service** | 8084 | Consumes activity events, generates recommendations via Gemini |
| **Eureka Server** | 8761 | Service discovery registry |
| **Config Server** | 8888 | Centralised configuration for all services |
| **Frontend** | 5173 | React + Vite SPA |

---

## 🛠️ Tech Stack

**Backend**
- Java 21 / Spring Boot 3
- Spring Cloud Gateway, Eureka, Config Server
- Spring Data MongoDB
- Spring AMQP (RabbitMQ)
- Keycloak (OAuth2 / PKCE)
- Google Gemini API

**Frontend**
- React 19 + Vite
- Material UI (MUI v7)
- Redux Toolkit
- Axios
- React Router DOM v7
- `react-oauth2-code-pkce`

**Infrastructure**
- MongoDB
- RabbitMQ
- Keycloak

---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- MongoDB (running on `localhost:27017`)
- RabbitMQ (running on `localhost:5672`)
- Keycloak (running on `localhost:8086`, realm: `fitness-oauth2`)

### 1. Start Infrastructure Services

Start MongoDB, RabbitMQ, and Keycloak locally (or via Docker):

```bash
# RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

# MongoDB
docker run -d --name mongodb -p 27017:27017 mongo

# Keycloak
docker run -d --name keycloak -p 8086:8080 \
  -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

### 2. Start Backend Services (in order)

```bash
# 1. Config Server
cd configserver && ./mvnw spring-boot:run

# 2. Eureka
cd eureka && ./mvnw spring-boot:run

# 3. User Service
cd userservice && ./mvnw spring-boot:run

# 4. Activity Service
cd activityservice && ./mvnw spring-boot:run

# 5. AI Service (requires Gemini env vars)
cd aiservice && \
  GEMINI_API_URL="https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" \
  GEMINI_API_KEY="your-gemini-api-key" \
  ./mvnw spring-boot:run

# 6. API Gateway
cd gateway && ./mvnw spring-boot:run
```

### 3. Start the Frontend

```bash
cd fitness-ai-app-frontend
npm install
npm run dev
```

App will be available at **http://localhost:5173**

---

## 🔐 Authentication Flow

This app uses **OAuth2 Authorization Code + PKCE** via Keycloak:

1. User clicks **Login** → redirected to Keycloak (`localhost:8086`)
2. On success, Keycloak redirects back to `localhost:5173` with an auth code
3. Frontend exchanges the code for a JWT access token
4. Token is stored in `localStorage` and sent as `Authorization: Bearer <token>` on all API requests
5. API Gateway validates the token and forwards `X-User-ID` header to downstream services

**Keycloak Config:**
- Realm: `fitness-oauth2`
- Client ID: `oauth2-pkce-client`
- Redirect URI: `http://localhost:5173`

---

## 🤖 AI Recommendation Flow

```
POST /api/activities
    → Activity saved to MongoDB (fitnessactivity)
    → Activity published to RabbitMQ (fitness.exchange / activity.tracking)
        → AI Service consumes from activity.queue
        → Checks if recommendation already exists for this activityId
        → If not: calls Google Gemini API to generate personalised recommendation
        → Saves Recommendation to MongoDB (fitnessrecommendations)

GET /api/ai/recommendations/activity/:activityId
    → Returns the stored recommendation
```

**Recommendation fields:**
| Field | Description |
|---|---|
| `activityType` | Type of workout (e.g. RUNNING, CYCLING) |
| `recommendation` | Full AI-generated feedback paragraph |
| `improvements` | Array of targeted improvement suggestions |
| `suggestions` | Array of specific workout drill suggestions |
| `safety` | Array of safety tips |

---

## 📁 Project Structure

```
AIFitnessAppMicroservice/
├── configserver/          # Spring Cloud Config Server
├── eureka/                # Service discovery (Netflix Eureka)
├── gateway/               # API Gateway (Spring Cloud Gateway)
├── userservice/           # User management
├── activityservice/       # Activity tracking + RabbitMQ publisher
├── aiservice/             # Gemini AI recommendations consumer
└── fitness-ai-app-frontend/   # React + Vite SPA
    └── src/
        ├── components/
        │   ├── ActivityForm.jsx    # Add new activity form
        │   ├── ActivityList.jsx    # Grid of user activities
        │   └── ActivityDetail.jsx  # Full recommendation view
        ├── services/
        │   └── api.js             # Axios API client
        ├── store/
        │   ├── store.js           # Redux store
        │   └── authSlice.js       # Auth state (token, userId)
        ├── authConfig.js          # OAuth2 PKCE config
        └── App.jsx                # Root component + routing
```

---

## 🌐 API Reference

| Method | Path | Service | Description |
|---|---|---|---|
| `POST` | `/api/activities` | Activity | Track a new activity |
| `GET` | `/api/activities/user/:userId` | Activity | Get all activities for a user |
| `GET` | `/api/activities/getActivity/:id` | Activity | Get a single activity |
| `GET` | `/api/ai/recommendations/activity/:id` | AI | Get recommendation for an activity |
| `GET` | `/api/ai/recommendations/user/:userId` | AI | Get all recommendations for a user |
| `GET` | `/api/users/**` | User | User profile endpoints |

All requests go through the gateway at `http://localhost:8085`.

---

## 🗄️ Databases

| Database | Service | Collections / Data |
|---|---|---|
| `fitnessactivity` | Activity Service | `activities` |
| `fitnessrecommendations` | AI Service | `recommendations` |
| User Service DB | User Service | `users` |

---

## ⚙️ Environment Variables

| Variable | Service | Description |
|---|---|---|
| `GEMINI_API_URL` | AI Service | Gemini endpoint URL |
| `GEMINI_API_KEY` | AI Service | Google Gemini API key |

---

## 📝 License

MIT
