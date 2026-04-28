# 🎬 FICHE DE DÉMONSTRATION RAPIDE
## Smart Library - Guide de Présentation

---

## ⚡ DÉMARRAGE RAPIDE (5 min)

### Ordre de lancement (9 terminaux)

```bash
# 1. RabbitMQ (Docker)
docker start rabbitmq

# 2. Discovery Server
cd discovery-server && mvn spring-boot:run

# 3. API Gateway  
cd api-gateway && mvn spring-boot:run

# 4. Auth Service
cd auth-service && mvn spring-boot:run

# 5. User Service
cd user-service && mvn spring-boot:run

# 6. Book Service
cd book-service && mvn spring-boot:run

# 7. Inventory Service
cd inventory-service && mvn spring-boot:run

# 8. Loan Service
cd loan-service && mvn spring-boot:run

# 9. Notification Service
cd notification-service && mvn spring-boot:run

# 10. Search Service
cd search-service && mvn spring-boot:run
```

### URLs à ouvrir
- **Eureka** : http://localhost:8761
- **RabbitMQ** : http://localhost:15672 (guest/guest)

---

## 🔑 ÉTAPE 1 : AUTHENTIFICATION

```bash
# Login
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

📋 **Copier le token reçu et remplacer TOKEN dans les commandes suivantes**

---

## 👥 ÉTAPE 2 : UTILISATEURS

```bash
# Lister les utilisateurs
curl -X GET http://localhost:8888/users \
  -H "Authorization: Bearer TOKEN"

# Rechercher
curl -X GET "http://localhost:8888/users/search?q=jean" \
  -H "Authorization: Bearer TOKEN"

# Bloquer un utilisateur
curl -X PATCH http://localhost:8888/users/3/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"status": "BLOCKED"}'

# Vérifier si peut emprunter
curl -X GET http://localhost:8888/users/public/check/3
```

---

## 📚 ÉTAPE 3 : LIVRES

```bash
# Lister les livres
curl -X GET http://localhost:8888/books \
  -H "Authorization: Bearer TOKEN"

# Livres disponibles
curl -X GET http://localhost:8888/books/disponibles \
  -H "Authorization: Bearer TOKEN"

# Ajouter un livre
curl -X POST http://localhost:8888/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"titre":"Les Fleurs du Mal","auteur":"Charles Baudelaire","isbn":"978-2-07-040852-8","disponible":true}'
```

---

## 📦 ÉTAPE 4 : INVENTAIRE

```bash
# Voir l'inventaire
curl -X GET http://localhost:8888/inventory \
  -H "Authorization: Bearer TOKEN"

# Vérifier stock d'un livre
curl -X GET http://localhost:8888/inventory/public/check/1

# Statistiques
curl -X GET http://localhost:8888/inventory/stats \
  -H "Authorization: Bearer TOKEN"

# Stock faible
curl -X GET http://localhost:8888/inventory/low-stock \
  -H "Authorization: Bearer TOKEN"
```

---

## 📝 ÉTAPE 5 : EMPRUNTS + RABBITMQ ⭐ (LE PLUS IMPORTANT)

### 5.1 État initial
```bash
# Vérifier livre disponible
curl -X GET http://localhost:8888/inventory/public/check/1

# Notifications (vide)
curl -X GET http://localhost:8888/notifications
```

### 5.2 Créer un emprunt 🔥
```bash
curl -X POST http://localhost:8888/loans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN" \
  -d '{"bookId":1,"nomUtilisateur":"Jean Dupont"}'
```

**👀 MONTRER :**
1. Console loan-service → logs emprunt
2. Console notification-service → message RabbitMQ reçu
3. RabbitMQ Dashboard → message traité

### 5.3 Vérifier les changements
```bash
# Livre non disponible maintenant
curl -X GET http://localhost:8888/books/1 \
  -H "Authorization: Bearer TOKEN"

# Stock diminué
curl -X GET http://localhost:8888/inventory/public/check/1

# Notification créée
curl -X GET http://localhost:8888/notifications
```

### 5.4 Retourner le livre 🔥
```bash
curl -X PUT http://localhost:8888/loans/1/return \
  -H "Authorization: Bearer TOKEN"
```

### 5.5 Vérifier après retour
```bash
# Livre disponible à nouveau
curl -X GET http://localhost:8888/inventory/public/check/1

# 2 notifications (EMPRUNT + RETOUR)
curl -X GET http://localhost:8888/notifications
```

---

## 🔍 ÉTAPE 6 : RECHERCHE WEBFLUX ⭐

```bash
# Tous les livres enrichis (book + inventory)
curl -X GET http://localhost:8888/search \
  -H "Authorization: Bearer TOKEN"

# Recherche par titre
curl -X GET http://localhost:8888/search/title/petit \
  -H "Authorization: Bearer TOKEN"

# Recherche par auteur
curl -X GET http://localhost:8888/search/author/hugo \
  -H "Authorization: Bearer TOKEN"

# Recherche globale
curl -X GET "http://localhost:8888/search/query?q=victor" \
  -H "Authorization: Bearer TOKEN"

# Livres disponibles
curl -X GET http://localhost:8888/search/available \
  -H "Authorization: Bearer TOKEN"

# Recherche avancée
curl -X GET "http://localhost:8888/search/advanced?author=hugo&available=true" \
  -H "Authorization: Bearer TOKEN"

# Stream temps réel (SSE) 🔥
curl -X GET http://localhost:8888/search/stream \
  -H "Authorization: Bearer TOKEN" \
  -H "Accept: text/event-stream"
```

---

## 🔔 ÉTAPE 7 : NOTIFICATIONS

```bash
# Toutes les notifications
curl -X GET http://localhost:8888/notifications

# Par utilisateur
curl -X GET "http://localhost:8888/notifications/user/Jean%20Dupont"

# Non lues
curl -X GET http://localhost:8888/notifications/unread

# Stream temps réel
curl -X GET http://localhost:8888/notifications/stream \
  -H "Accept: text/event-stream"
```

---

## ✅ ÉTAPE 8 : TESTS DE CONTRAT

```bash
# Tests Producer (book-service)
cd book-service
mvn clean test

# Tests Consumer (loan-service)  
cd loan-service
mvn clean test
```

---

## 📊 TABLEAU RÉCAPITULATIF

| Service | Port | Rôle | Technologie |
|---------|------|------|-------------|
| discovery-server | 8761 | Registre | Eureka |
| api-gateway | 8888 | Routage | Spring Cloud Gateway |
| auth-service | 8081 | Auth | JWT |
| user-service | 8085 | Utilisateurs | JPA |
| book-service | 8082 | Catalogue | JPA |
| inventory-service | 8086 | Stock | JPA |
| loan-service | 8083 | Emprunts | JPA + RabbitMQ |
| search-service | 8087 | Recherche | **WebFlux** |
| notification-service | 8084 | Notifications | **WebFlux + RabbitMQ** |

---

## 🎯 POINTS CLÉS À MENTIONNER

### Architecture
- ✅ 9 microservices indépendants
- ✅ Service Discovery (Eureka)
- ✅ API Gateway (point d'entrée unique)

### Sécurité
- ✅ JWT sur tous les services
- ✅ Gestion des rôles (ADMIN, MEMBER, etc.)

### Communication
- ✅ REST synchrone (loan → book)
- ✅ RabbitMQ asynchrone (loan → notification)

### Réactivité
- ✅ WebFlux (Mono/Flux)
- ✅ Server-Sent Events (streaming)

### Tests
- ✅ Spring Cloud Contract
- ✅ Tests Producer/Consumer

### Concepts TP Couverts
- TP1 : REST Microservices
- TP2 : JPA + Communication inter-services  
- TP3 : Eureka + Gateway
- TP4 : Spring Security + JWT
- TP5 : RabbitMQ
- TP6 : Spring Cloud Contract
- TP7 : Spring WebFlux

---

## 🚨 EN CAS DE PROBLÈME

### Service ne démarre pas
```bash
# Vérifier que le port n'est pas utilisé
netstat -ano | findstr :PORT
```

### RabbitMQ ne fonctionne pas
```bash
docker restart rabbitmq
```

### Token expiré
```bash
# Refaire le login
curl -X POST http://localhost:8888/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Service non enregistré dans Eureka
- Vérifier que discovery-server est démarré en premier
- Attendre 30 secondes après le démarrage

---

## 🏆 BONNE PRÉSENTATION !
