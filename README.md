# 1. Get latest code
git pull origin main

# 2. Start PostgreSQL
brew services start postgresql

# 3. Create database (only needed first time)
createdb bookagent

# 4. Open PostgreSQL (only needed first time)
psql bookagent

# 5. Enable pgvector
CREATE EXTENSION IF NOT EXISTS vector;

# 6. Exit PostgreSQL
\q

# The embedding model currently used by the project is MiniLM, which produces a 384-dimensional embedding. 

# The database therefore needs an embedding column compatible with vector(384) 

# After the books table has been created, check it with:
psql bookagent

# Then:
\d books

# The table should contain an embedding column similar to: embedding | vector(384)

# If the column does not exist, add it:
ALTER TABLE books
ADD COLUMN IF NOT EXISTS embedding vector(384);

# Then exit:
\q

# Configure the Solr authentication environment variables required before running Solr-related tests.
export SOLR_USERNAME='...'
export SOLR_PASSWORD='...'

# Current LLM is not actually used for these retrieval tests,
# but Spring config currently expects this variable
export DASHSCOPE_API_KEY=dummy-key

# Before running individual tests
./mvnw compile
# Expected result:
BUILD SUCCESS

# Test embedding model
./mvnw -Dtest=EmbeddingModelTest test

# Test Solr connection
./mvnw -Dtest=SolrClientServiceTest test

# Test Solr -> Book mapping
./mvnw -Dtest=SolrBookMapperServiceTest test

# Import Solr books -> PostgreSQL + generate embeddings
./mvnw -Dtest=BookIngestionServiceTest test

# Test semantic recommendation using real catalogue data
./mvnw -Dtest=RealBookRetrievalTest test