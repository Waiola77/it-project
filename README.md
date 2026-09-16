# NextChapter – AI-Powered Book Recommendation System

NextChapter is a personalised book recommendation prototype developed for the RealSAM project.

The system recommends books from the RealSAM catalogue based on a user's natural-language request, existing preferences, reading history, and feedback. It combines LLM-based intent understanding, catalogue retrieval, user preference modelling, and recommendation ranking to provide personalised recommendations with explanations.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Project Motivation](#project-motivation)
- [Key Features](#key-features)
- [System Architecture](#system-architecture)
- [Recommendation Pipeline](#recommendation-pipeline)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Demo Users](#demo-users)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [Documentation](#documentation)

---

## Project Overview

NextChapter is a proof-of-concept personalised book recommendation system designed to support users in discovering books from the RealSAM catalogue.

Users can describe what they would like to read using natural language. The backend interprets the request, retrieves relevant books from the catalogue, incorporates existing user preferences and feedback, and returns a ranked list of recommendations.

Each recommendation contains information that can be presented directly by the frontend, including:

- Book title
- Author
- Description
- Recommendation rank
- Personalised recommendation reason

The current prototype focuses primarily on the recommendation engine and backend integration.

---

## Project Motivation

Traditional catalogue search often requires users to know specific titles, authors, subjects, or search terms.

NextChapter explores a more conversational recommendation approach where users can express preferences naturally, for example:

> "I want an interesting science book about space."

The system combines this request with available information about the user, such as previous reading activity and feedback, to provide more personalised recommendations.

---

## Key Features

### Natural-Language Preference Parsing

User requests are interpreted using an LLM to identify relevant recommendation intent and preferences.

### Catalogue-Based Recommendations

Recommendations are restricted to books available in the RealSAM catalogue.

### Semantic Book Retrieval

Book embeddings are used with PostgreSQL and pgvector to retrieve catalogue books that are semantically related to a user's request.

### Exact Title and Author Retrieval

The recommendation pipeline also supports requests that explicitly refer to a book title or author.

### Personalised Ranking

Candidate books are ranked using the current user request together with available user preference information.

### Recommendation Explanations

Each recommended book includes a short explanation describing why it may be relevant to the user.

### Demo User Profiles

Demo profiles provide initial user information such as:

- Stated interests
- Favourite books
- Liked authors
- Disliked subjects
- Reading history
- Previously rejected books

These profiles allow personalised recommendation behaviour to be demonstrated without requiring a production user account system.

### User Feedback

Users can provide feedback through:

- **Like** – provides a positive preference signal for future recommendations.
- **Not For Me** – records negative feedback and prevents the rejected book from being recommended again.

### Reading History Filtering

Books recorded in a demo user's reading history are excluded from new recommendations while the reading history can still contribute information about the user's preferences.

---

## System Architecture

The current system consists of a React/Vite frontend and a Spring Boot backend.

A simplified architecture is shown below:

```text
User
 │
 ▼
React / Vite Frontend
 │
 │ HTTP Request
 ▼
Spring Boot Backend
 │
 ├── LLM Intent Extraction
 │
 ├── User Preference Modelling
 │     ├── Demo User Profile
 │     └── User Feedback
 │
 ├── Book Retrieval
 │     ├── Exact Title / Author Retrieval
 │     └── Semantic Retrieval
 │
 ├── Candidate Filtering
 │     ├── Reading History
 │     └── Rejected Books
 │
 └── LLM Recommendation Ranking
          │
          ▼
 Ranked Recommendations
          │
          ▼
       Frontend
```

---

## Recommendation Pipeline

The main recommendation process is:

1. Receive the user's natural-language request and user ID.
2. Parse the request to identify recommendation intent.
3. Retrieve candidate books from the catalogue.
4. Load available user preference information.
5. Remove books that should not be recommended again.
6. Rank the remaining candidates according to the current request and user preferences.
7. Generate a short recommendation reason.
8. Enrich the recommendation response with catalogue metadata.
9. Return the ranked recommendations to the frontend.

User feedback can then update the preference information used by future recommendation requests.

---

## Technology Stack

### Backend

- Java
- Spring Boot
- Maven
- LangChain4j

### Data and Retrieval

- PostgreSQL
- pgvector
- Solr
- MiniLM embeddings

The current embedding model produces 384-dimensional vectors.

### Frontend

- React
- Vite

### LLM Integration

The backend uses an LLM through an OpenAI-compatible API interface for tasks including intent extraction and recommendation ranking.

---

## Getting Started

### Prerequisites

Before running the project, install:

- Java
- PostgreSQL
- pgvector
- Node.js and npm
- Git

Clone the repository or update an existing local copy:

```bash
git pull origin main
```

---

## Configuration

### PostgreSQL

Start PostgreSQL:

```bash
brew services start postgresql
```

Create the project database if it does not already exist:

```bash
createdb bookagent
```

Open the database:

```bash
psql bookagent
```

Enable pgvector:

```sql
CREATE EXTENSION IF NOT EXISTS vector;
```

Exit PostgreSQL:

```text
\q
```

### Embedding Column

The current embedding model uses 384-dimensional embeddings.

Check the `books` table:

```bash
psql bookagent
```

Then:

```text
\d books
```

The table should contain an embedding column compatible with:

```text
vector(384)
```

If required:

```sql
ALTER TABLE books
ADD COLUMN IF NOT EXISTS embedding vector(384);
```

### Environment Variables

Configure the required environment variables before running services that access Solr or the LLM.

```bash
export SOLR_USERNAME='your-username'
export SOLR_PASSWORD='your-password'
export DASHSCOPE_API_KEY='your-api-key'
```

Do not commit real credentials or API keys to the repository.

---

## Running the Application

The application consists of a Spring Boot backend and a React/Vite frontend.

### Start the Backend

From the project root:

```bash
./mvnw spring-boot:run
```

The backend runs on:

```text
http://localhost:8080
```

### Start the Frontend

Open another terminal:

```bash
cd realsam-frontend
```

For the first run, install dependencies:

```bash
npm install
```

Start the development server:

```bash
npm run dev
```

The frontend runs on:

```text
http://localhost:5173
```

Keep both backend and frontend processes running when testing the complete application.

---

## API Documentation

### Get Recommendations

```http
GET /chat
```

Parameters:

| Parameter | Description |
|---|---|
| `userId` | ID of the current user or demo profile |
| `message` | Natural-language recommendation request |

Example:

```bash
curl -G "http://localhost:8080/chat" \
  --data-urlencode "userId=science-reader" \
  --data-urlencode "message=Recommend me an interesting book"
```

A recommendation contains:

```json
{
  "recordId": "56415",
  "title": "Example Book",
  "author": "Example Author",
  "description": "Book description...",
  "rank": 1,
  "reason": "Explanation of why this book matches the user."
}
```

### Like a Book

```http
POST /feedback/like
```

Parameters:

- `userId`
- `bookId`

Example:

```bash
curl -X POST \
  "http://localhost:8080/feedback/like?userId=demo-user&bookId=56415"
```

### Reject a Recommendation

```http
POST /feedback/reject
```

Parameters:

- `userId`
- `bookId`

Example:

```bash
curl -X POST \
  "http://localhost:8080/feedback/reject?userId=demo-user&bookId=56415"
```

---

## Demo Users

The project includes predefined demo user profiles for demonstrating different recommendation behaviours.

Examples include users interested in:

- Science and technology
- Romance
- Mystery
- History and adventure
- Self-improvement
- Gentle fiction

Demo profiles are stored in:

```text
src/main/resources/demo-users.json
```

A demo profile may contain stated preferences, favourite books, liked authors, disliked subjects, reading history, and previously rejected books.

Runtime Like and Not For Me feedback is stored separately and can further influence future recommendations.

---

## Testing

Compile the project:

```bash
./mvnw clean compile
```

Run the test suite:

```bash
./mvnw test
```

Individual components can also be tested separately.

### Embedding Model

```bash
./mvnw -Dtest=EmbeddingModelTest test
```

### Solr Connection

```bash
./mvnw -Dtest=SolrClientServiceTest test
```

### Solr Book Mapping

```bash
./mvnw -Dtest=SolrBookMapperServiceTest test
```

### Book Ingestion

Imports catalogue books into PostgreSQL and generates embeddings:

```bash
./mvnw -Dtest=BookIngestionServiceTest test
```

### Semantic Retrieval

```bash
./mvnw -Dtest=RealBookRetrievalTest test
```

---

## Project Structure

A simplified project structure is:

```text
it-project/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/consultant/
│   │   │       ├── aiservices/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       └── demo-users.json
│   │
│   └── test/
│
├── realsam-frontend/
├── pom.xml
└── README.md
```

---

## Documentation

The README provides the information required to understand, configure, run, and test the current prototype.

More detailed project documentation can be maintained separately, including:

- System architecture and design decisions
- Recommendation engine design
- LLM integration
- Solr investigation
- API design
- Testing and evaluation
- Development documentation

A link to the team's documentation workspace can be added here when available.