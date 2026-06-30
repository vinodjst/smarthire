# SmartHire — Azure Deployment Runbook

This document records how the SmartHire Spring Boot application was containerized and deployed to Azure, including the real infrastructure issues hit along the way and how they were resolved. Written as both a personal reference and an interview-ready walkthrough.

## Architecture overview

```
Local machine → Docker image → Azure Container Registry (ACR) → Azure Container Apps → Public HTTPS URL
```

The core idea: build once, package as a container, push to a private registry, then let a managed cloud runtime pull and run it. This decouples *where the app is built* from *where it runs*.

## Stack

- **Application**: Spring Boot (Java 19 runtime, Spring Boot 4.1.0)
- **Containerization**: Docker, base image `eclipse-temurin:19-jre-alpine`
- **Registry**: Azure Container Registry (ACR) — private, Basic SKU
- **Runtime**: Azure Container Apps — serverless container hosting with autoscaling
- **Region**: Central India (`centralindia`)

## Step-by-step

### 1. Built and verified the app locally

```bash
./mvnw clean package -DskipTests
java -jar target/smarthire-0.0.1-SNAPSHOT.jar
```

Confirmed it ran correctly on `localhost:8080` before containerizing anything. Also discovered the actual runtime Java version was 19 (not 25 as briefly assumed from a `pom.xml` property), which determined the Docker base image choice.

### 2. Containerized with Docker

`Dockerfile`:
```dockerfile
FROM eclipse-temurin:19-jre-alpine
WORKDIR /app
COPY target/smarthire-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

`.dockerignore`:
```
target/
.git/
.gitignore
*.md
.idea/
.mvn/
```

Built and ran locally to confirm the containerized app behaved identically to the local run:
```bash
docker build -t smarthire:1.0 .
docker run -d -p 8080:8080 --name smarthire-test smarthire:1.0
docker logs -f smarthire-test
```

Verified Spring Boot started cleanly and served a request inside the container.

### 3. Committed the Dockerfile to version control

```bash
git add Dockerfile .dockerignore
git commit -m "Add Dockerfile for containerized deployment"
git push
```

This matters: a CI/CD pipeline (future step) needs the Dockerfile in the repo to build automatically on every push. Without this, there's no real CI/CD story.

### 4. Set up Azure

- Created a free Azure account ($200 credit, 30-day trial)
- Installed the Azure CLI locally
- Authenticated: `az login`

### 5. Created the resource group and container registry

```bash
az group create --name smarthire-rg --location eastus

az provider register --namespace Microsoft.ContainerRegistry
# wait for registrationState: Registered

az acr create --resource-group smarthire-rg --name smarthireacr2026 --sku Basic
```

A resource group is a logical container for related Azure resources. ACR is a private Docker registry scoped to the Azure subscription.

### 6. Pushed the image to ACR

```bash
az acr login --name smarthireacr2026
docker tag smarthire:1.0 smarthireacr2026.azurecr.io/smarthire:1.0
docker push smarthireacr2026.azurecr.io/smarthire:1.0
```

`az acr login` doesn't create a persistent connection — it fetches a short-lived access token from Azure and uses it to authenticate a standard `docker login` against the registry hostname. Docker caches that credential locally until it expires.

### 7. Infrastructure issues encountered (and how they were resolved)

| Issue | Cause | Resolution |
|---|---|---|
| `Pip failed with status code 2` installing the `containerapp` CLI extension on Windows | A transitive dependency (`cryptography`, pulled in via `kubernetes`) had no precompiled wheel for the local Python/platform and needed a Rust-based build tool (`maturin`) not present locally | Switched to **Azure Cloud Shell** (browser-based, pre-configured Linux environment) to avoid local Windows/Python/Rust toolchain issues entirely |
| `AKSCapacityHeavyUsage` creating the Container Apps environment in `eastus` | Azure region temporarily out of backend capacity (Container Apps environments run on AKS infrastructure) | Retried in `centralindia`, which had capacity |
| `MaxNumberOfRegionalEnvironmentsInSubExceeded` / `MaxNumberOfGlobalEnvironmentsInSubExceeded` | Free-tier Azure subscriptions are capped at **one Container Apps environment per subscription**, and a partially-created environment from the capacity failure was still counted until fully deleted | Deleted the broken environment, waited for the `ScheduledForDelete` state to fully resolve (~10-15 minutes), then created a clean environment |

### 8. Created the Container Apps environment

```bash
az provider register --namespace Microsoft.App
az provider register --namespace Microsoft.OperationalInsights

az containerapp env create \
  --name smarthire-env2 \
  --resource-group smarthire-rg \
  --location centralindia
```

The environment is the shared infrastructure layer (networking, logging) that one or more container apps run inside.

### 9. Deployed the container app

```bash
az acr update -n smarthireacr2026 --admin-enabled true
az acr credential show --name smarthireacr2026

az containerapp create \
  --name smarthire-app \
  --resource-group smarthire-rg \
  --environment smarthire-env2 \
  --image smarthireacr2026.azurecr.io/smarthire:1.0 \
  --target-port 8080 \
  --ingress external \
  --registry-server smarthireacr2026.azurecr.io \
  --registry-username smarthireacr2026 \
  --registry-password <ACR_PASSWORD>
```

Key flags:
- `--target-port 8080` — the port the app listens on inside the container (matches Spring Boot's default / `EXPOSE 8080` in the Dockerfile)
- `--ingress external` — exposes the app on a public HTTPS URL rather than keeping it internal-only
- `--registry-*` — credentials so Container Apps can pull the private image from ACR

### 10. Result

```
provisioningState: Succeeded
runningStatus: Running
```

Live URL:
```
https://smarthire-app.calmmeadow-4954083b.centralindia.azurecontainerapps.io/
```

## Useful operational commands

Check app logs:
```bash
az containerapp logs show --name smarthire-app --resource-group smarthire-rg --follow
```

Check environment status:
```bash
az containerapp env show --name smarthire-env2 --resource-group smarthire-rg --query "properties.provisioningState" --output tsv
```

## Possible next steps

- [ ] Move secrets (DB credentials, API keys) into Azure Key Vault instead of plain env vars
- [ ] Connect a real database (Azure Database for PostgreSQL/MySQL)
- [ ] Set up Application Insights for monitoring and request tracing
- [ ] Build a GitHub Actions pipeline to auto-build and redeploy on every push to `main`
- [ ] Clean up orphaned Log Analytics workspaces left behind by earlier failed environment creation attempts
