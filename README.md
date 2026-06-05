# Smart Library 

Système de gestion de bibliothèque construit sur une architecture microservices complète.  

---

## Vue d'ensemble

Smart Library démontre une architecture de production moderne : 9 microservices indépendants, communication synchrone et asynchrone, authentification distribuée par JWT, programmation réactive avec WebFlux, et tests de contrat entre services.

**Stack** : Java 21 · Spring Boot 3.3.2 · Spring Cloud 2023.0.3 · RabbitMQ · H2 · Maven

---

## Architecture

```
Clients
   │
   ▼
API Gateway :8888  ◄──────── Eureka Server :8761
   │                         (service discovery)
   ├── auth-service       :8081  (JWT)
   ├── user-service       :8085  (JPA)
   ├── book-service       :8082  (JPA + Spring Cloud Contract)
   ├── inventory-service  :8086  (JPA)
   ├── loan-service       :8083  (JPA + RabbitMQ producer)
   ├── search-service     :8087  (WebFlux / Reactive)
   └── notification-service :8084 (WebFlux + RabbitMQ consumer)

loan-service ──[REST sync]──► book-service
loan-service ──[RabbitMQ]───► notification-service
search-service ──[WebClient]─► book-service + inventory-service
```

---

## Services

| Service | Port | Technologie clé | Rôle |
|---|---|---|---|
| `discovery-server` | 8761 | Eureka Server | Registre de services |
| `api-gateway` | 8888 | Spring Cloud Gateway | Point d'entrée unique, routage, CORS |
| `auth-service` | 8081 | JWT (jjwt 0.11) | Login, génération et validation de tokens |
| `user-service` | 8085 | JPA + Security | Utilisateurs, rôles, statuts |
| `book-service` | 8082 | JPA + Contract Verifier | Catalogue, Spring Cloud Contract producer |
| `inventory-service` | 8086 | JPA | Stock physique, copies disponibles |
| `loan-service` | 8083 | JPA + AMQP | Emprunts, appels REST vers book, publication RabbitMQ |
| `search-service` | 8087 | WebFlux (Mono/Flux) | Recherche réactive multi-critères + SSE |
| `notification-service` | 8084 | WebFlux + AMQP | Réception async, stockage, streaming SSE |

---

## Concepts couverts

| Concept | Implémentation |
|---|---|
| REST Microservices | APIs REST sur tous les services |
| JPA + communication inter-services | `RestTemplate` loan → book |
| Service Discovery + Gateway | Eureka + Spring Cloud Gateway |
| Spring Security + JWT | Filtre JWT sur chaque service |
| Messagerie asynchrone | RabbitMQ loan → notification |
| Tests de contrat | Spring Cloud Contract producer/consumer |
| Programmation réactive | WebFlux Mono/Flux + Server-Sent Events |

---

## Prérequis

- Java 21
- Maven 3.8+
- Docker (pour RabbitMQ)

---

## Démarrage

**1. Lancer RabbitMQ**

```bash
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:management
```

**2. Démarrer les services dans l'ordre**

```bash
# Terminal 1 — toujours en premier
cd discovery-server && mvn spring-boot:run

# Terminal 2
cd api-gateway && mvn spring-boot:run

# Terminaux 3 à 9 (ordre libre ensuite)
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd book-service && mvn spring-boot:run
cd inventory-service && mvn spring-boot:run
cd loan-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
cd search-service && mvn spring-boot:run
```

**3. Vérifier**

- Eureka dashboard : http://localhost:8761 (8 services enregistrés)
- RabbitMQ dashboard : http://localhost:15672 (guest/guest)

---

## Utilisation rapide

**Authentification**

```bash
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# → {"token":"eyJ...","username":"admin"}
```

Copier le token et l'utiliser dans les appels suivants :

```bash
TOKEN="eyJ..."
```

**Créer un emprunt** (déclenche REST sync + message RabbitMQ)

```bash
curl -X POST http://localhost:8888/loans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"bookId":1,"nomUtilisateur":"Jean Dupont"}'
```

À ce moment, observer dans les consoles :
1. `loan-service` → appel REST vers `book-service` (vérification disponibilité)
2. `notification-service` → message RabbitMQ reçu, notification créée

**Retourner le livre**

```bash
curl -X PUT http://localhost:8888/loans/1/return \
  -H "Authorization: Bearer $TOKEN"
```

**Recherche réactive**

```bash
# Tous les livres enrichis (book + inventory)
curl http://localhost:8888/search -H "Authorization: Bearer $TOKEN"

# Recherche par auteur
curl http://localhost:8888/search/author/hugo -H "Authorization: Bearer $TOKEN"

# Stream Server-Sent Events
curl http://localhost:8888/search/stream \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: text/event-stream"
```

**Tests de contrat**

```bash
# Producer (génère les stubs)
cd book-service && mvn clean test

# Consumer (utilise les stubs, book-service n'a pas besoin de tourner)
cd loan-service && mvn clean test
```

---

## Flux métier principal

```
1. Login ──────────────────────────────► auth-service → Token JWT

2. Recherche livre ────────────────────► search-service
                                          └── WebClient → book-service
                                          └── WebClient → inventory-service

3. Emprunt ────────────────────────────► loan-service
                                          ├── REST → book-service (vérif. dispo)
                                          ├── REST → book-service (mise à jour dispo)
                                          └── RabbitMQ → notification-service

4. Retour ─────────────────────────────► loan-service
                                          ├── REST → book-service (rend disponible)
                                          └── RabbitMQ → notification-service
```

---

## Endpoints principaux

Tous les endpoints passent par le gateway `http://localhost:8888`.

```
POST   /auth/login
GET    /auth/validate

GET    /books                   GET /books/disponibles
POST   /books                   PUT /books/{id}
PATCH  /books/{id}/disponibilite

GET    /inventory               GET /inventory/public/check/{bookId}
GET    /inventory/stats         GET /inventory/low-stock

GET    /loans                   POST /loans
PUT    /loans/{id}/return       GET  /loans/en-cours

GET    /users                   POST /users
PATCH  /users/{id}/status       GET  /users/public/check/{id}

GET    /search                  GET /search/title/{title}
GET    /search/author/{author}  GET /search/query?q=...
GET    /search/available        GET /search/stream   (SSE)

GET    /notifications           GET /notifications/unread
GET    /notifications/stream    (SSE)
PUT    /notifications/{id}/read
```

---

## Données initiales

**Utilisateurs** (admin/admin123 · user/user123 pour l'auth)

| Username | Rôle | Statut |
|---|---|---|
| admin | ADMIN | ACTIVE |
| librarian | LIBRARIAN | ACTIVE |
| jean | MEMBER | ACTIVE |
| pierre | PREMIUM_MEMBER | ACTIVE |
| sophie | MEMBER | BLOCKED |

**Livres** (5 classiques de la littérature française, IDs 1–5)

Le Petit Prince · Les Misérables · L'Étranger · Germinal · Madame Bovary

---

## Dépannage rapide

| Problème | Solution |
|---|---|
| Service non enregistré dans Eureka | Vérifier que `discovery-server` a démarré en premier, attendre ~30s |
| Token expiré (401) | Relancer `POST /auth/login` |
| RabbitMQ inaccessible | `docker restart rabbitmq` |
| Port déjà utilisé | `netstat -ano \| findstr :PORT` (Windows) ou `lsof -i :PORT` (Mac/Linux) |

---

## Structure du projet

```
library/
├── discovery-server/       Eureka Server
├── api-gateway/            Spring Cloud Gateway + CORS
├── auth-service/           JWT login/validate
├── user-service/           Gestion utilisateurs/rôles
├── book-service/           Catalogue + contrats Groovy
│   └── src/test/resources/contracts/book/
├── inventory-service/      Stock physique
├── loan-service/           Emprunts + RabbitMQ publisher
│   └── src/test/…contracts/ Stub runner consumer test
├── search-service/         WebFlux + WebClient
├── notification-service/   WebFlux + RabbitMQ listener
└── pom.xml                 Aggregator Maven
```

---

## Auteur
ZOUAGHI Ghofran 
