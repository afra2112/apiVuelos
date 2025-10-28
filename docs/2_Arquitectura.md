
## 🧠 2. DOCUMENTACIÓN DE ARQUITECTURA (cómo está construida)

- 📘 [Guía de Usuario](docs/1_Guia_Usuario.md)
- 🧩 [Arquitectura](docs/2_Arquitectura.md)
- 🚀 [Guía de Swagger](docs/3_Swagger.md)
- 🧪 [Tests](docs/4_Tests.md)
- 📄 [Swagger UI](http://localhost:8080/swagger-ui.html)

# Arquitectura del Sistema 🧩

## Patrón de arquitectura
La API sigue el patrón **Arquitectura en Capas (Layered Architecture)**:

- **Controller:** recibe peticiones HTTP y delega en los servicios.
- **Service (interfaz):** delega y propone la logica de negocio.
- **implement:** implementa la logica de negocio que propone el service.
- **Repository:** accede a la base de datos mediante JPA.
- **Entity:** define las entidades del dominio.

## Estructura de paquetes
```md
com.api.vuelos
├── controller/
├── service/
├── implement/
├── repository/
├── entity/
├── config/
├── exception/
├── specification/
└── dto/
```

## Diagrama de arquitectura
```
┌──────────────────────────────┐
│         Usuario/Cliente      │
│ (Frontend, Postman, Móvil)   │
└──────────────┬───────────────┘
│  HTTP (REST)
▼
┌─────────────────────┐
│   API Spring Boot   │
├─────────────────────┤
│  Controller Layer   │
│  (Recibe peticiones)│
├─────────────────────┤
│  Service Layer      │
│  (Lógica de negocio)│
├─────────────────────┤
│  Repository Layer   │
│ (JPA/Hibernate)     │
├─────────────────────┤
│  Entities/Models    │
└─────────┬───────────┘
│ JDBC/JPA
▼
┌────────────────┐
│ Base de Datos   │
│  MySQL / H2     │
└────────────────┘
```


## Flujo CI/CD
```
[Desarrollador]
     │
     ▼
[Repositorio GitHub]
     │ (Push, Pull Request)
     ▼
[Pipeline CI/CD]
 ├─ Etapa 1: Build
 │    (Compila proyecto con Maven)
 ├─ Etapa 2: Test
 │    (Ejecuta pruebas JUnit)
 ├─ Etapa 3: Build Docker Image
 │    (opcional)
 └─ Etapa 4: Deploy
      (a entorno local, servidor o nube)
     ▼
[Entorno de despliegue]
```

## Tecnologías
| Capa | Tecnología |
|------|-------------|
| Backend | Spring Boot 3 |
| Base de Datos | MySQL |
| Autenticación | JWT |
| Documentación | Swagger (Springdoc OpenAPI) |
| CI/CD | GitHub Actions (build + test + deploy) |

