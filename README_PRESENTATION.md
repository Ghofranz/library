# 📚 Smart Library - Système de Gestion de Bibliothèque

## Projet Microservices - ING-A2-GL TP Développement Avancé 2025-2026

---

## 📋 Table des Matières

1. [Présentation du Projet](#-présentation-du-projet)
2. [Architecture](#-architecture)
3. [Technologies Utilisées](#-technologies-utilisées)
4. [Microservices](#-microservices)
5. [Fonctionnalités](#-fonctionnalités)
6. [Prérequis](#-prérequis)
7. [Installation et Démarrage](#-installation-et-démarrage)
8. [Guide de Démonstration](#-guide-de-démonstration)
9. [Concepts TP Couverts](#-concepts-tp-couverts)

---

## 🎯 Présentation du Projet

**Smart Library** est un système de gestion de bibliothèque moderne basé sur une **architecture microservices**. 

Le projet permet de :
- 📖 Gérer un catalogue de livres
- 👥 Gérer les utilisateurs et leurs rôles
- 📦 Suivre l'inventaire et le stock des livres
- 📝 Gérer les emprunts et retours
- 🔔 Envoyer des notifications automatiques
- 🔍 Rechercher des livres de manière réactive

### Objectifs Pédagogiques

Ce projet met en pratique les concepts suivants :
- Architecture Microservices
- Communication synchrone (REST) et asynchrone (RabbitMQ)
- Service Discovery avec Eureka
- API Gateway pour le routage centralisé
- Authentification JWT
- Programmation réactive avec Spring WebFlux
- Tests de contrat avec Spring Cloud Contract

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                         SMART LIBRARY SYSTEM                                    │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│                              ┌─────────────────┐                                │
│                              │   Eureka Server │                                │
│                              │      :8761      │                                │
│                              └────────┬────────┘                                │
│                                       │                                         │
│                              ┌────────┴────────┐                                │
│           Clients ─────────► │   API Gateway   │                                │
│                              │      :8888      │                                │
│                              └────────┬────────┘                                │
│                                       │                                         │
│     ┌─────────┬─────────┬─────────┬───┴───┬─────────┬─────────┬─────────┐       │
│     │         │         │         │       │         │         │         │       │
│     ▼         ▼         ▼         ▼       ▼         ▼         ▼         ▼       │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐       │
│  │ Auth │ │ User │ │ Book │ │Inven-│ │ Loan │ │Search│ │Notif │ │Rabbit│       │
│  │:8081 │ │:8085 │ │:8082 │ │ tory │ │:8083 │ │:8087 │ │:8084 │ │  MQ  │       │
│  │      │ │      │ │      │ │:8086 │ │      │ │      │ │      │ │:5672 │       │
│  │ JWT  │ │Users │ │Books │ │Stock │ │Loans │ │WebFlux│ │WebFlux│ │:15672│       │
│  └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘ └──────┘       │
│                                  │       │         │                  │         │
│                                  └───────┴─────────┘                  │         │
│                                          │                            │         │
│                              Communication REST              Communication      │
│                                 synchrone                    asynchrone         │
│                                                             (RabbitMQ)          │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 🛠 Technologies Utilisées

| Technologie | Usage |
|-------------|-------|
| **Java 21** | Langage de programmation |
| **Spring Boot 3.3.2** | Framework principal |
| **Spring Cloud 2023.0.3** | Outils microservices |
| **Spring Cloud Netflix Eureka** | Service Discovery |
| **Spring Cloud Gateway** | API Gateway |
| **Spring Security + JWT** | Authentification |
| **Spring Data JPA** | Accès aux données |
| **Spring WebFlux** | Programmation réactive |
| **Spring AMQP** | Messagerie RabbitMQ |
| **Spring Cloud Contract** | Tests de contrat |
| **H2 Database** | Base de données en mémoire |
| **RabbitMQ** | Message Broker |
| **Maven** | Gestion des dépendances |
| **Docker** | Conteneurisation (RabbitMQ) |

---

## 📦 Microservices

### Vue d'ensemble

| Service | Port | Description | Technologie Clé |
|---------|------|-------------|-----------------|
| **discovery-server** | 8761 | Registre des services | Eureka Server |
| **api-gateway** | 8888 | Point d'entrée unique | Spring Cloud Gateway |
| **auth-service** | 8081 | Authentification | JWT |
| **user-service** | 8085 | Gestion des utilisateurs | JPA + Security |
| **book-service** | 8082 | Catalogue des livres | JPA + REST |
| **inventory-service** | 8086 | Gestion du stock | JPA + REST |
| **loan-service** | 8083 | Gestion des emprunts | JPA + RabbitMQ |
| **search-service** | 8087 | Recherche réactive | WebFlux |
| **notification-service** | 8084 | Notifications | WebFlux + RabbitMQ |

---

### Détail de chaque service

#### 1. Discovery Server (Eureka)
- **Port** : 8761
- **Rôle** : Registre centralisé où tous les services s'enregistrent
- **Dashboard** : http://localhost:8761

#### 2. API Gateway
- **Port** : 8888
- **Rôle** : Point d'entrée unique pour toutes les requêtes
- **Fonctionnalités** :
  - Routage vers les microservices
  - Load balancing
  - Configuration CORS

#### 3. Auth Service
- **Port** : 8081
- **Rôle** : Authentification et génération de tokens JWT
- **Endpoints** :
  - `POST /auth/login` - Connexion
  - `GET /auth/validate` - Validation du token
- **Utilisateurs préconfigurés** :
  - admin / admin123
  - user / user123

#### 4. User Service
- **Port** : 8085
- **Rôle** : Gestion complète des utilisateurs
- **Entités** : User (id, username, email, firstName, lastName, role, status)
- **Rôles** : ADMIN, LIBRARIAN, MEMBER, PREMIUM_MEMBER
- **Statuts** : ACTIVE, BLOCKED, SUSPENDED, PENDING
- **Endpoints** :
  - `GET /users` - Liste des utilisateurs
  - `GET /users/{id}` - Détails d'un utilisateur
  - `GET /users/username/{username}` - Par username
  - `GET /users/role/{role}` - Par rôle
  - `GET /users/search?q=xxx` - Recherche
  - `POST /users` - Créer un utilisateur
  - `PUT /users/{id}` - Modifier
  - `PATCH /users/{id}/status` - Changer le statut
  - `PATCH /users/{id}/role` - Changer le rôle
  - `GET /users/public/check/{id}` - Vérifier si peut emprunter

#### 5. Book Service
- **Port** : 8082
- **Rôle** : Catalogue des livres
- **Entité** : Book (id, titre, auteur, isbn, disponible)
- **Endpoints** :
  - `GET /books` - Liste des livres
  - `GET /books/{id}` - Détails d'un livre
  - `GET /books/disponibles` - Livres disponibles
  - `POST /books` - Ajouter un livre
  - `PUT /books/{id}` - Modifier un livre
  - `PATCH /books/{id}/disponibilite` - Changer disponibilité
  - `DELETE /books/{id}` - Supprimer

#### 6. Inventory Service
- **Port** : 8086
- **Rôle** : Gestion du stock physique
- **Entité** : Inventory (bookId, totalCopies, availableCopies, location, stockStatus)
- **Statuts** : IN_STOCK, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED
- **Endpoints** :
  - `GET /inventory` - Tout l'inventaire
  - `GET /inventory/book/{bookId}` - Par livre
  - `GET /inventory/available` - Stock disponible
  - `GET /inventory/low-stock` - Stock faible
  - `GET /inventory/out-of-stock` - Rupture de stock
  - `GET /inventory/stats` - Statistiques
  - `POST /inventory` - Créer inventaire
  - `POST /inventory/borrow/{bookId}` - Emprunter
  - `POST /inventory/return/{bookId}` - Retourner
  - `POST /inventory/add-copies/{bookId}` - Ajouter copies
  - `GET /inventory/public/check/{bookId}` - Vérifier disponibilité

#### 7. Loan Service
- **Port** : 8083
- **Rôle** : Gestion des emprunts avec notification asynchrone
- **Entité** : Loan (bookId, nomUtilisateur, dateEmprunt, dateRetourPrevue, status)
- **Statuts** : EN_COURS, RETOURNE, EN_RETARD
- **Communication** :
  - REST vers book-service (vérification disponibilité)
  - RabbitMQ vers notification-service (notifications)
- **Endpoints** :
  - `GET /loans` - Liste des emprunts
  - `GET /loans/{id}` - Détails d'un emprunt
  - `GET /loans/user/{nom}` - Emprunts par utilisateur
  - `GET /loans/en-cours` - Emprunts en cours
  - `POST /loans` - Créer un emprunt
  - `PUT /loans/{id}/return` - Retourner un livre

#### 8. Search Service (WebFlux)
- **Port** : 8087
- **Rôle** : Recherche réactive agrégant book-service et inventory-service
- **Technologie** : Spring WebFlux (Mono/Flux)
- **Fonctionnalités** :
  - Recherche par titre, auteur, ISBN
  - Recherche globale
  - Filtrage par disponibilité
  - Score de pertinence
  - Streaming en temps réel (SSE)
- **Endpoints** :
  - `GET /search` - Tous les livres enrichis
  - `GET /search/title/{title}` - Par titre
  - `GET /search/author/{author}` - Par auteur
  - `GET /search/query?q=xxx` - Recherche globale
  - `GET /search/available` - Disponibles uniquement
  - `GET /search/isbn/{isbn}` - Par ISBN
  - `GET /search/book/{id}` - Détails complets
  - `GET /search/advanced` - Recherche avancée
  - `GET /search/stats` - Statistiques
  - `GET /search/stream` - Stream temps réel (SSE)

#### 9. Notification Service (WebFlux)
- **Port** : 8084
- **Rôle** : Réception et stockage des notifications
- **Technologie** : Spring WebFlux + RabbitMQ Listener
- **Entité** : Notification (loanId, bookId, bookTitre, nomUtilisateur, type, message)
- **Types** : EMPRUNT, RETOUR
- **Endpoints** :
  - `GET /notifications` - Toutes les notifications
  - `GET /notifications/{id}` - Par ID
  - `GET /notifications/user/{nom}` - Par utilisateur
  - `GET /notifications/type/{type}` - Par type
  - `GET /notifications/unread` - Non lues
  - `PUT /notifications/{id}/read` - Marquer comme lue
  - `GET /notifications/count` - Compteur
  - `GET /notifications/stream` - Stream temps réel

---

## ⚡ Fonctionnalités

### Fonctionnalités Principales

| # | Fonctionnalité | Services Impliqués |
|---|----------------|-------------------|
| 1 | Authentification JWT | auth-service |
| 2 | Gestion des utilisateurs et rôles | user-service |
| 3 | Catalogue de livres CRUD | book-service |
| 4 | Gestion du stock et copies | inventory-service |
| 5 | Emprunts avec mise à jour automatique | loan-service → book-service |
| 6 | Notifications asynchrones | loan-service → RabbitMQ → notification-service |
| 7 | Recherche réactive multi-critères | search-service → book-service + inventory-service |
| 8 | Streaming temps réel (SSE) | search-service, notification-service |
| 9 | Service Discovery | Eureka |
| 10 | Routage centralisé | API Gateway |
| 11 | Tests de contrat | book-service ↔ loan-service |

### Flux Métier Principal

```
1. Utilisateur se connecte → auth-service → Token JWT

2. Utilisateur cherche un livre → search-service → book + inventory

3. Utilisateur emprunte → loan-service:
   ├── Vérifie disponibilité (book-service)
   ├── Crée l'emprunt
   ├── Met à jour disponibilité (book-service)
   └── Envoie notification (RabbitMQ → notification-service)

4. Utilisateur retourne → loan-service:
   ├── Met à jour l'emprunt
   ├── Rend le livre disponible (book-service)
   └── Envoie notification (RabbitMQ → notification-service)
```

---

## 📋 Prérequis

- **Java 21** (JDK)
- **Maven 3.8+**
- **Docker** (pour RabbitMQ)
- **IDE** : IntelliJ IDEA recommandé
- **Postman** ou **cURL** pour les tests

---

## 🚀 Installation et Démarrage

### Étape 1 : Cloner/Préparer le projet

```bash
# Structure attendue
library/
├── discovery-server/
├── api-gateway/
├── auth-service/
├── user-service/
├── book-service/
├── inventory-service/
├── loan-service/
├── search-service/
└── notification-service/
```

### Étape 2 : Démarrer RabbitMQ

```bash
# Si RabbitMQ n'est pas installé
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:management

# Si déjà installé
docker start rabbitmq
```

**Dashboard RabbitMQ** : http://localhost:15672 (guest/guest)

### Étape 3 : Ordre de démarrage des services

⚠️ **L'ordre est important !**

```bash
# Terminal 1 - Discovery Server (PREMIER)
cd discovery-server
mvn spring-boot:run

# Terminal 2 - API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 3 - Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 4 - User Service
cd user-service
mvn spring-boot:run

# Terminal 5 - Book Service
cd book-service
mvn spring-boot:run

# Terminal 6 - Inventory Service
cd inventory-service
mvn spring-boot:run

# Terminal 7 - Loan Service
cd loan-service
mvn spring-boot:run

# Terminal 8 - Notification Service
cd notification-service
mvn spring-boot:run

# Terminal 9 - Search Service
cd search-service
mvn spring-boot:run
```

### Étape 4 : Vérification

1. **Eureka Dashboard** : http://localhost:8761
   - Tous les services doivent être enregistrés (8 services)

2. **RabbitMQ** : http://localhost:15672
   - Queue `loan.notification.queue` doit exister

---

## 🎬 Guide de Démonstration

### Variables à configurer

```bash
# Après le login, remplacer TOKEN par le vrai token reçu
TOKEN="eyJhbGciOiJIUzI1NiJ9..."
```

---

### 📌 PARTIE 1 : Infrastructure et Service Discovery

#### 1.1 Montrer Eureka Dashboard

```
Ouvrir : http://localhost:8761
```

**Points à mentionner** :
- Tous les 8 services sont enregistrés
- Chaque service a son instance avec statut UP
- Eureka permet le load balancing automatique

#### 1.2 Montrer RabbitMQ Dashboard

```
Ouvrir : http://localhost:15672
Credentials : guest / guest
```

**Points à mentionner** :
- Queue `loan.notification.queue` créée automatiquement
- Exchange `loan.exchange` pour le routage des messages

---

### 📌 PARTIE 2 : Authentification JWT

#### 2.1 Login et obtention du token

```bash
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

**Réponse attendue** :
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "expiresIn": 3600000
}
```

**Points à mentionner** :
- Token JWT signé avec clé secrète
- Expiration configurée à 1 heure
- Utilisé pour sécuriser tous les autres services

#### 2.2 Valider le token

```bash
curl -X GET http://localhost:8888/auth/validate \
  -H "Authorization: Bearer TOKEN"
```

#### 2.3 Test sans token (erreur 403)

```bash
curl -X GET http://localhost:8888/books
```

**Points à mentionner** :
- Sans token → Accès refusé
- Sécurité sur tous les endpoints protégés

---

### 📌 PARTIE 3 : Gestion des Utilisateurs

#### 3.1 Lister tous les utilisateurs

```bash
curl -X GET http://localhost:8888/users \
  -H "Authorization: Bearer TOKEN"
```

**Réponse** : 5 utilisateurs préconfigurés avec différents rôles

#### 3.2 Rechercher un utilisateur

```bash
curl -X GET "http://localhost:8888/users/search?q=jean" \
  -H "Authorization: Bearer TOKEN"
```

#### 3.3 Voir les utilisateurs par rôle

```bash
curl -X GET http://localhost:8888/users/role/MEMBER \
  -H "Authorization: Bearer TOKEN"
```

#### 3.4 Créer un nouvel utilisateur

```bash
curl -X POST http://localhost:8888/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "username": "nouveau",
    "email": "nouveau@library.com",
    "firstName": "Nouveau",
    "lastName": "Utilisateur"
  }'
```

#### 3.5 Bloquer un utilisateur

```bash
curl -X PATCH http://localhost:8888/users/3/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"status": "BLOCKED"}'
```

#### 3.6 Vérifier si utilisateur peut emprunter

```bash
curl -X GET http://localhost:8888/users/public/check/3
```

**Points à mentionner** :
- Gestion des rôles (ADMIN, LIBRARIAN, MEMBER, PREMIUM_MEMBER)
- Gestion des statuts (ACTIVE, BLOCKED, SUSPENDED)
- Limite d'emprunts par utilisateur
- Premium members ont plus de droits

---

### 📌 PARTIE 4 : Catalogue des Livres

#### 4.1 Lister tous les livres

```bash
curl -X GET http://localhost:8888/books \
  -H "Authorization: Bearer TOKEN"
```

**Réponse** : 5 livres de littérature française

#### 4.2 Voir un livre spécifique

```bash
curl -X GET http://localhost:8888/books/1 \
  -H "Authorization: Bearer TOKEN"
```

#### 4.3 Livres disponibles

```bash
curl -X GET http://localhost:8888/books/disponibles \
  -H "Authorization: Bearer TOKEN"
```

#### 4.4 Ajouter un nouveau livre

```bash
curl -X POST http://localhost:8888/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "titre": "Les Fleurs du Mal",
    "auteur": "Charles Baudelaire",
    "isbn": "978-2-07-040852-8",
    "disponible": true
  }'
```

#### 4.5 Modifier un livre

```bash
curl -X PUT http://localhost:8888/books/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "titre": "Le Petit Prince (Édition Collector)",
    "auteur": "Antoine de Saint-Exupéry",
    "isbn": "978-2-07-040850-4",
    "disponible": true
  }'
```

---

### 📌 PARTIE 5 : Gestion de l'Inventaire

#### 5.1 Voir tout l'inventaire

```bash
curl -X GET http://localhost:8888/inventory \
  -H "Authorization: Bearer TOKEN"
```

#### 5.2 Vérifier le stock d'un livre

```bash
curl -X GET http://localhost:8888/inventory/public/check/1
```

**Réponse** :
```json
{
  "bookId": 1,
  "bookTitle": "Le Petit Prince",
  "available": true,
  "availableCopies": 5,
  "totalCopies": 5,
  "stockStatus": "IN_STOCK",
  "location": "Rayon A - Étagère 1"
}
```

#### 5.3 Voir les statistiques

```bash
curl -X GET http://localhost:8888/inventory/stats \
  -H "Authorization: Bearer TOKEN"
```

#### 5.4 Voir les livres en stock faible

```bash
curl -X GET http://localhost:8888/inventory/low-stock \
  -H "Authorization: Bearer TOKEN"
```

#### 5.5 Ajouter des copies

```bash
curl -X POST http://localhost:8888/inventory/add-copies/5 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"copies": 3}'
```

**Points à mentionner** :
- Gestion du stock physique séparée du catalogue
- Statuts automatiques (IN_STOCK, LOW_STOCK, OUT_OF_STOCK)
- Localisation physique des livres

---

### 📌 PARTIE 6 : Emprunts et Notifications (RabbitMQ)

⚠️ **C'est la partie la plus importante - Communication inter-services + Messagerie asynchrone**

#### 6.1 Vérifier l'état initial

```bash
# Stock du livre 1
curl -X GET http://localhost:8888/inventory/public/check/1

# Notifications (vide au début)
curl -X GET http://localhost:8888/notifications
```

#### 6.2 Créer un emprunt 🔥

```bash
curl -X POST http://localhost:8888/loans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{
    "bookId": 1,
    "nomUtilisateur": "Jean Dupont"
  }'
```

**Réponse** :
```json
{
  "success": true,
  "message": "Emprunt créé avec succès",
  "loan": {
    "id": 1,
    "bookId": 1,
    "nomUtilisateur": "Jean Dupont",
    "status": "EN_COURS",
    "dateRetourPrevue": "2025-12-23T..."
  }
}
```

**Points à montrer** :
1. **Console loan-service** : Logs de création d'emprunt
2. **Console notification-service** : Message RabbitMQ reçu
3. **RabbitMQ Dashboard** : Message dans la queue

#### 6.3 Vérifier les changements

```bash
# Le livre n'est plus disponible
curl -X GET http://localhost:8888/books/1 \
  -H "Authorization: Bearer TOKEN"

# Le stock a diminué
curl -X GET http://localhost:8888/inventory/public/check/1

# Notification créée
curl -X GET http://localhost:8888/notifications
```

#### 6.4 Voir les emprunts en cours

```bash
curl -X GET http://localhost:8888/loans/en-cours \
  -H "Authorization: Bearer TOKEN"
```

#### 6.5 Retourner le livre 🔥

```bash
curl -X PUT http://localhost:8888/loans/1/return \
  -H "Authorization: Bearer TOKEN"
```

**Points à montrer** :
1. **Console notification-service** : Notification RETOUR reçue
2. Le livre redevient disponible
3. Le stock est restauré

#### 6.6 Vérifier après retour

```bash
# Livre disponible à nouveau
curl -X GET http://localhost:8888/books/1 \
  -H "Authorization: Bearer TOKEN"

# Stock restauré
curl -X GET http://localhost:8888/inventory/public/check/1

# Deux notifications (EMPRUNT + RETOUR)
curl -X GET http://localhost:8888/notifications
```

**Points à mentionner** :
- Communication synchrone REST (loan → book)
- Communication asynchrone RabbitMQ (loan → notification)
- Mise à jour automatique de la disponibilité
- Découplage des services

---

### 📌 PARTIE 7 : Recherche Réactive (WebFlux)

#### 7.1 Recherche globale avec enrichissement

```bash
curl -X GET http://localhost:8888/search \
  -H "Authorization: Bearer TOKEN"
```

**Réponse** : Livres avec infos du catalogue ET de l'inventaire

#### 7.2 Recherche par titre

```bash
curl -X GET http://localhost:8888/search/title/petit \
  -H "Authorization: Bearer TOKEN"
```

#### 7.3 Recherche par auteur

```bash
curl -X GET http://localhost:8888/search/author/hugo \
  -H "Authorization: Bearer TOKEN"
```

#### 7.4 Recherche globale (titre OU auteur)

```bash
curl -X GET "http://localhost:8888/search/query?q=victor" \
  -H "Authorization: Bearer TOKEN"
```

#### 7.5 Livres disponibles seulement

```bash
curl -X GET http://localhost:8888/search/available \
  -H "Authorization: Bearer TOKEN"
```

#### 7.6 Recherche par ISBN

```bash
curl -X GET http://localhost:8888/search/isbn/978-2-07-040850-4 \
  -H "Authorization: Bearer TOKEN"
```

#### 7.7 Recherche avancée avec filtres

```bash
curl -X GET "http://localhost:8888/search/advanced?author=hugo&available=true" \
  -H "Authorization: Bearer TOKEN"
```

#### 7.8 Statistiques

```bash
curl -X GET http://localhost:8888/search/stats \
  -H "Authorization: Bearer TOKEN"
```

#### 7.9 Stream en temps réel (SSE) 🔥

```bash
curl -X GET http://localhost:8888/search/stream \
  -H "Authorization: Bearer TOKEN" \
  -H "Accept: text/event-stream"
```

**Points à mentionner** :
- Spring WebFlux avec Mono et Flux
- Agrégation de données de plusieurs services
- Score de pertinence calculé
- Server-Sent Events pour le streaming
- Non-bloquant et scalable

---

### 📌 PARTIE 8 : Notifications Réactives

#### 8.1 Toutes les notifications

```bash
curl -X GET http://localhost:8888/notifications
```

#### 8.2 Notifications par utilisateur

```bash
curl -X GET http://localhost:8888/notifications/user/Jean%20Dupont
```

#### 8.3 Notifications par type

```bash
curl -X GET http://localhost:8888/notifications/type/EMPRUNT
```

#### 8.4 Notifications non lues

```bash
curl -X GET http://localhost:8888/notifications/unread
```

#### 8.5 Marquer comme lue

```bash
curl -X PUT http://localhost:8888/notifications/ID_NOTIFICATION/read
```

#### 8.6 Compteur de notifications

```bash
curl -X GET http://localhost:8888/notifications/count
```

#### 8.7 Stream des notifications (SSE)

```bash
curl -X GET http://localhost:8888/notifications/stream \
  -H "Accept: text/event-stream"
```

---

### 📌 PARTIE 9 : Tests de Contrat (Spring Cloud Contract)

#### 9.1 Exécuter les tests Producer (book-service)

```bash
cd book-service
mvn clean test
```

**Points à montrer** :
- Tests générés automatiquement depuis les contrats Groovy
- Stubs générés dans `target/stubs/`

#### 9.2 Exécuter les tests Consumer (loan-service)

```bash
cd loan-service
mvn clean test
```

**Points à mentionner** :
- Consumer utilise les stubs générés
- Pas besoin de book-service en cours d'exécution
- Garantit la compatibilité entre services

---

## 📚 Concepts TP Couverts

| TP | Concept | Implémentation |
|----|---------|----------------|
| **TP1** | REST Microservices | Tous les services exposent des APIs REST |
| **TP2** | JPA + Communication inter-services | book-service, loan-service, RestTemplate |
| **TP3** | Eureka + Gateway | discovery-server, api-gateway |
| **TP4** | Spring Security + JWT | auth-service, filtres JWT sur tous les services |
| **TP5** | RabbitMQ Messaging | loan-service → notification-service |
| **TP6** | Spring Cloud Contract | Tests entre book-service et loan-service |
| **TP7** | Spring WebFlux | search-service, notification-service |

---

## 📊 Résumé des Endpoints

### Authentification
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | /auth/login | Connexion |
| GET | /auth/validate | Valider token |

### Utilisateurs
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /users | Liste |
| GET | /users/{id} | Par ID |
| GET | /users/search?q= | Recherche |
| POST | /users | Créer |
| PATCH | /users/{id}/status | Changer statut |

### Livres
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /books | Liste |
| GET | /books/{id} | Par ID |
| GET | /books/disponibles | Disponibles |
| POST | /books | Créer |
| PUT | /books/{id} | Modifier |
| DELETE | /books/{id} | Supprimer |

### Inventaire
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /inventory | Liste |
| GET | /inventory/public/check/{bookId} | Vérifier stock |
| GET | /inventory/stats | Statistiques |
| POST | /inventory/borrow/{bookId} | Emprunter |
| POST | /inventory/return/{bookId} | Retourner |

### Emprunts
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /loans | Liste |
| GET | /loans/en-cours | En cours |
| POST | /loans | Créer emprunt |
| PUT | /loans/{id}/return | Retourner |

### Recherche
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /search | Tous les livres enrichis |
| GET | /search/title/{title} | Par titre |
| GET | /search/author/{author} | Par auteur |
| GET | /search/query?q= | Recherche globale |
| GET | /search/stream | Stream SSE |

### Notifications
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /notifications | Liste |
| GET | /notifications/unread | Non lues |
| PUT | /notifications/{id}/read | Marquer lue |
| GET | /notifications/stream | Stream SSE |

---

## 🎯 Points Forts du Projet

1. **Architecture Microservices Complète** - 9 services indépendants
2. **Service Discovery** - Enregistrement automatique avec Eureka
3. **API Gateway** - Point d'entrée unique et sécurisé
4. **Sécurité JWT** - Authentification sur tous les services
5. **Communication Synchrone** - REST entre services
6. **Communication Asynchrone** - RabbitMQ pour les notifications
7. **Programmation Réactive** - WebFlux avec Mono/Flux
8. **Streaming Temps Réel** - Server-Sent Events
9. **Tests de Contrat** - Spring Cloud Contract
10. **Gestion du Stock** - Séparation catalogue/inventaire

---

## 👨‍💻 Auteur

Projet réalisé dans le cadre du cours **Développement Avancé** - ING-A2-GL 2025-2026

---

## 📝 Licence

Projet académique - Usage éducatif uniquement
