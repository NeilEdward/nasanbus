# Postman

Generate the OpenAPI specification:

```powershell
.\gradlew.bat generateOpenApiDocs
```

Import `NasanBus.openapi.json` into Postman. Postman creates and groups the
requests from the API paths, parameters, validation constraints, and schemas.

When the API changes, rerun the task and import the updated specification.
Swagger UI is available at `http://localhost:8080/swagger-ui.html` while the
application is running.
