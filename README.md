# NextChapter — RealSAM Book Recommendation Prototype

NextChapter is a proof-of-concept book recommendation application developed for the RealSAM project. A React frontend sends natural-language requests to a Spring Boot backend, which retrieves catalogue-grounded books, incorporates demo user profiles and stored feedback, and returns ranked recommendations with short reasons.

## Contents

1. [Scope and features](#scope-and-features)
2. [Architecture and recommendation flow](#architecture-and-recommendation-flow)
3. [Technology and versions](#technology-and-versions)
4. [Repository layout](#repository-layout)
5. [Prerequisites](#prerequisites)
6. [Configuration](#configuration)
7. [Database and catalogue ingestion](#database-and-catalogue-ingestion)
8. [Build and run locally](#build-and-run-locally)
9. [API reference](#api-reference)
10. [Testing and CI](#testing-and-ci)
11. [Build and deployment](#build-and-deployment)
12. [Data and model information](#data-and-model-information)
13. [Known limitations](#known-limitations)
14. [Additional documentation](#additional-documentation)

## Scope and features

- Natural-language book requests and LLM-assisted search-intent extraction.
- Exact title/author retrieval when the intent specifies one, or embedding-based semantic retrieval for general requests.
- Catalogue-grounded recommendations using imported RealSAM Solr book metadata.
- Demo user preferences, reading-history exclusion, rejection exclusion, and stored Like/Not For Me feedback.
- LLM evaluation of candidate match strength and explicit negative-preference conflicts; results are ranked and annotated with recommendation reasons.
- React demonstration interface, initially showing three recommendations, with additional returned recommendations held in reserve.

This is a **prototype**, not a complete production RealSAM account or audio system. It does not implement speech recognition, text-to-speech, production authentication or a general-purpose catalogue search interface.

## Architecture and recommendation flow

```text
React / Vite Frontend
    |
    | User request + user ID
    v
Spring Boot Backend
    |
    |-- LLM extracts search intent
    |
    |-- Select retrieval strategy
    |     |-- Exact title / author search
    |     |-- Semantic search using DashScope embeddings
    |         and PostgreSQL / pgvector
    |
    |-- Filter previously read or rejected books
    |   using user profile and feedback history
    |
    |-- LLM evaluates candidate relevance,
    |   negative preference conflicts,
    |   and generates recommendation reasons
    |
    |-- Remove conflicting or invalid candidates
    |
    |-- Rank by match strength
    |   (strong > moderate > weak)
    |
    |-- Return up to 10 recommendations
    v
React Frontend
    |
    |-- Display first 3 recommendations
    |-- Keep remaining recommendations in reserve
    |
    |-- User selects Like or Not for me
    |     |-- Send feedback to backend
    |     |-- Store feedback in PostgreSQL
    |     |-- On rejection, remove the book and
    |         append the next reserve recommendation
```

The semantic retrieval stage retrieves up to 20 candidate books.
After filtering previously read or rejected books, the LLM evaluates
the remaining candidates and ranks them by match strength
(strong > moderate > weak). The backend returns up to 10 recommendations,
depending on the number of valid candidates.

The frontend initially displays the first 3 recommendations and keeps
the remaining results in reserve. When a user selects Not For Me,
the rejected book is removed and the next reserve recommendation is
displayed without rerunning the recommendation pipeline. Stored feedback
influences subsequent recommendation requests.

## Technology and versions

| Component | Version / implementation |
| --- | --- |
| Java | 17 (`pom.xml`; Docker images also use 17) |
| Spring Boot | 4.1.1 |
| Build | Maven wrapper (`./mvnw`) |
| LangChain4j | 1.19.0-beta29 |
| Database | PostgreSQL with pgvector; server version not pinned in repository |
| LLM API | Alibaba Cloud DashScope OpenAI-compatible endpoint; configured chat model `deepseek-v4-flash-0731` |
| Embeddings | DashScope `text-embedding-v4`, **512 dimensions** |
| Catalogue | RealSAM Solr staging endpoint, `combinedbooks` core |
| Frontend | React ^19.2.8, React Router ^7.18.3, Vite ^8.2.2 |
| Frontend build / CI runtime | Node.js 22 in GitHub Actions |

Dependency resolution is governed by `pom.xml` and `realsam-frontend/package-lock.json`. The package manifest uses semver ranges; use the lockfile for reproducible installs.

## Repository layout

```text
it-project/
├── .github/workflows/ci.yml          # GitHub Actions checks
├── Dockerfile                         # Backend multi-stage container build
├── pom.xml                            # Java dependencies
├── mvnw, mvnw.cmd, .mvn/              # Maven wrapper
├── src/
│   ├── main/java/org/example/consultant/
│   │   ├── aiservices/                # LLM interfaces
│   │   ├── config/                    # Model and CORS configuration
│   │   ├── controller/                # HTTP endpoints
│   │   ├── model/                     # Book, profile and response types
│   │   ├── repository/                # JPA and pgvector queries
│   │   └── service/                   # Retrieval, ingestion, feedback, ranking
│   ├── main/resources/
│   │   ├── application.yml            # Spring database defaults
│   │   └── demo-users.json            # Demo profile fixtures
│   └── test/java/                     # JUnit/Spring tests
└── realsam-frontend/
    ├── package.json, package-lock.json
    ├── src/config.js                  # Frontend API URL
    ├── src/pages/                     # Demo pages
    └── wrangler.jsonc                 # Cloudflare Workers static assets config
```

## Prerequisites

- JDK 17, Git, Node.js 22 and npm.
- PostgreSQL with the `vector` extension installed and enabled.
- Network access and authorized credentials for the RealSAM staging Solr catalogue (required for ingestion).
- A valid DashScope API key with access to the configured chat and embedding models (required for recommendation and ingestion embeddings).
- A PostgreSQL user authorized to create/update tables in the selected development database.

**Use a separate development/test database.** `spring.jpa.hibernate.ddl-auto=update` may change schema on application startup, and integration tests can read or write database records. Do not point local tests at production.

## Configuration

Backend variables are read from the process environment. Create a private shell configuration or deployment secrets; do **not** commit actual credentials.

| Variable | Required | Meaning |
| --- | --- | --- |
| `DB_URL` | Optional if using local default | JDBC URL; default `jdbc:postgresql://localhost:5432/bookagent` |
| `DB_USERNAME` | Yes | PostgreSQL username |
| `DB_PASSWORD` | Required if database uses a password | Password; defaults to empty string |
| `DASHSCOPE_API_KEY` | Yes for model-backed operations | Chat and embedding API key |
| `SOLR_USERNAME` | Yes for catalogue ingestion | RealSAM Solr basic-auth username |
| `SOLR_PASSWORD` | Yes for catalogue ingestion | RealSAM Solr basic-auth password |
| `VITE_API_URL` | Optional for frontend | Backend base URL; defaults to `http://localhost:8080` |
| `PORT` | Optional for Docker runtime | Container HTTP port; defaults to `8080` |

An example configuration file is provided in `.env.example`.

Copy the example file and replace the placeholders with your own
environment settings and credentials. Environment variables must
be loaded into the application process before starting the backend,
as Spring Boot does not automatically load `.env` files.

Example shell setup (replace placeholders locally; do not paste secrets into issue reports):

```bash
export DB_URL='jdbc:postgresql://localhost:5432/bookagent'
export DB_USERNAME='YOUR_DB_USER'
export DB_PASSWORD='YOUR_DB_PASSWORD'
export DASHSCOPE_API_KEY='YOUR_DASHSCOPE_API_KEY'
export SOLR_USERNAME='YOUR_SOLR_USER'
export SOLR_PASSWORD='YOUR_SOLR_PASSWORD'
```

For frontend configuration, set `VITE_API_URL` **before** `npm run build` when targeting a deployed backend. Vite embeds `VITE_*` values in the browser bundle; **never** put secrets in them.

The current Solr host/core and model names are hardcoded in Java (`SolrClientService`, `ChatModelConfig`, `EmbeddingService`). Changing providers or environments requires code/config changes.

## Database and catalogue ingestion

1. Create a database (example):

   ```bash
   createdb bookagent
   psql -d bookagent -c 'CREATE EXTENSION IF NOT EXISTS vector;'
   ```

2. Set the backend environment variables above.
3. From the repository root, import a development subset:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--ingest-1000"
   ```

   Or import the available full catalogue:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.arguments="--ingest-all"
   ```

The ingestion runner reads from the `combinedbooks` Solr core, normalizes metadata, stores book records and generates 512-dimensional embeddings using DashScope. Ingestion can take time and incur API charges. Verify available quota and catalogue access before starting a full import. The database itself is **not** bundled with the Git source.

**Important:** The embedding model in this snapshot uses `vector(512)`. An older README referred to MiniLM and `vector(384)`; those instructions are obsolete. Do not blindly alter an existing populated embedding column to another dimension. For migrations from older embeddings, plan a clean re-embedding and schema migration.

After ingestion, verify the local data source distribution:

```sql
SELECT source, COUNT(*)
FROM books
GROUP BY source
ORDER BY COUNT(*) DESC;
```

Some tests specifically require `source = 'VA'`; a database containing only `rnib` records will not satisfy those tests even if retrieval works for its own data.

## Build and run locally

Backend (repository root):

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Backend default: `http://localhost:8080`.

Frontend (separate terminal):

```bash
cd realsam-frontend
npm ci
npm run dev
```

Frontend default Vite address: `http://localhost:5173`.

Open the frontend in a browser. The backend CORS controller annotations currently allow `http://localhost:5173`; a separately hosted frontend may require updating the backend CORS configuration.

## API reference

The examples below are illustrative; actual records and recommendations depend on catalogue contents, profiles and model output.

### Recommendations

`GET /chat` with query parameters `userId` and `message`.

```bash
curl -G 'http://localhost:8080/chat'   --data-urlencode 'userId=YOUR_DEMO_USER_ID'   --data-urlencode 'message=I want a romantic love story'
```

The response is an object containing `recommendations`, an array of entries with fields including `recordId`, `title`, `author`, `description`, `reason`, `matchStrength`, `negativeConflict` and `rank`. The array may contain fewer than 10 entries.

### Feedback

```bash
curl -X POST 'http://localhost:8080/feedback/like?userId=YOUR_DEMO_USER_ID&bookId=BOOK_RECORD_ID'
curl -X POST 'http://localhost:8080/feedback/reject?userId=YOUR_DEMO_USER_ID&bookId=BOOK_RECORD_ID'
curl 'http://localhost:8080/feedback/liked-books?userId=YOUR_DEMO_USER_ID'
curl 'http://localhost:8080/feedback/rejected-books?userId=YOUR_DEMO_USER_ID'
```

The POST endpoints take **query parameters**, not a JSON request body. The two GET feedback endpoints return book lists. Use IDs from `src/main/resources/demo-users.json` for the included demo profiles.

## Testing and CI

Run all backend tests:

```bash
./mvnw test
```

Run the main isolated recommendation/profile tests:

```bash
./mvnw -Dtest=BookChatServiceTest test
./mvnw -Dtest=UserPreferenceServiceTest test
```

Run frontend checks:

```bash
cd realsam-frontend
npm ci
npm run lint
npm run build
```

GitHub Actions (`.github/workflows/ci.yml`) compiles the backend and runs selected Java test classes (recommendation, demo profiles, normalization, Solr mapping), then runs frontend lint and build on Node 22. **CI does not run the entire backend integration test suite.**

**Known local test outcome (reported 25 October 2026):** A local `./mvnw test` run reported 40 tests: 37 passed, 1 failed, 1 errored and 1 skipped. `RealBookRetrievalTest` expected results from `VA`, but the local `books` table contained 1,001 `rnib` records and no `VA` records. This explains the empty-result assertion under the repository's source-filtered query. `BookEmbeddingRepositoryTest` requires pre-existing book records with IDs vec-001, vec-002, and vec-003. The test fails with `NoSuchElementException` if a required record is missing. These are environment-dependent integration tests; do not claim the full suite passes on every installation. Keep a clean test database and document its required fixtures.

## Build and deployment

### Backend JAR

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

### Backend Docker image

The included `Dockerfile` builds with Temurin JDK 17 and runs on Temurin JRE 17:

```bash
docker build -t nextchapter-backend .
docker run --rm -p 8080:8080   --env-file /ABSOLUTE/PATH/TO/private-backend.env   nextchapter-backend
```

**Container networking:** The default JDBC host `localhost` means the container itself, not the host Mac. Set `DB_URL` to a database hostname reachable from the container. Provide all secrets through the deployment platform or a private env file. The Docker build uses `-DskipTests`; build success is not evidence of passing tests.

### Frontend production build

```bash
cd realsam-frontend
npm ci
VITE_API_URL='https://YOUR-BACKEND-HOST' npm run build
```

Deploy the generated `dist/` assets to a compatible static host. The repository includes `wrangler.jsonc` for a Cloudflare static-assets deployment, but no verified live URL, deployment account, custom domain or shutdown date is recorded in this README. Those must be confirmed by the deployment owner and documented in the handover.

Before any public deployment, confirm CORS settings, database/network access, service credentials, API quotas, HTTPS, and whether the prototype should remain publicly accessible.

## Data and model information

- **Book catalogue:** Book metadata is obtained from RealSAM's Solr catalogue and stored in PostgreSQL for retrieval.
- **User profiles:** Demo user profiles are provided in `src/main/resources/demo-users.json`.
- **User feedback:** Like and rejection records are stored in PostgreSQL.
- **Embedding model:** DashScope `text-embedding-v4` generates 512-dimensional embeddings for semantic retrieval.
- **LLM:** A hosted model accessed through DashScope is used for intent extraction, candidate evaluation and recommendation explanations.
- **Model training:** No custom models are trained as part of this project. The system uses externally hosted pretrained models.
- **Data usage:** Access to and redistribution of catalogue data are subject to the relevant provider's permissions and restrictions.

## Known limitations

- The system is a proof of concept using predefined demo user profiles, without production authentication or account management.
- Recommendation generation depends on external services, including Solr and DashScope.
- The number of recommendations may be reduced by filtering and evaluation.
- Rejected recommendations are replaced from a finite reserve pool rather than through immediate regeneration.
- Recommendation quality and response time depend on catalogue coverage, model performance and external API availability.
- Some integration tests require specific catalogue data and database configurations.
- Cloudflare deployment has not yet been confirmed as successful.

## Additional documentation

Project documentation and other handover materials are provided separately.

Refer to `STARTHERE.md` in the final handover package for a complete inventory of delivered files.