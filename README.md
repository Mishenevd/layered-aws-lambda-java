# AWS SAM Lambda Layered Template

This is the scratch monorepo of simple **Layered AWS Lambda Function** to easily jump-start your serverless development.

* Multi-module gradle project. Supports independent Lambda Functions & Layers development.
* Convenient local development workflow.
* Easy-to-mock Postgres DB as an external component.

## Prerequisites

* Have Docker, AWS CLI, AWS SAM installed.

## Build
We can build the function and the dependent modules locally using **SAM Framework**

```bash
sam build
```

## Run Locally

1. We need to have postgres available at the host network. Below the example of setting up Postgres through 

```bash
docker run -p 5432:5432 -e POSTGRES_PASSWORD=password postgres:13
```

```bash
docker network connect <host-network-id> <your-postgres-container-id>
```

2. **CreateBookFunction** Lambda Function and the corresponding **BookValidationLayer** are being compiled through the **AWS SAM** using `template.yaml` 

```bash
sam build
```


## Deploy To AWS

```bash
sam deploy
```