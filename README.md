# tbm-sura
# 📦 Proyecto TBM - Microservicio de Reportes

![image](https://github.com/user-attachments/assets/12907f47-691b-4676-997b-a6a1199ce33d)

Este proyecto es un microservicio **Spring Boot WebFlux** modularizado para gestión de gastos por viaje y reportes mensuales.  
Incluye seguridad JWT, base de datos PostgreSQL y documentación.

![image](https://github.com/user-attachments/assets/9b5fd049-923e-4f53-9673-5c64fb436326)

---

## 🚀 Despliegue local con Docker Compose

1️⃣ Clona el repositorio:
```bash
git clone https://github.com/JEYBAN37/tbm-sura
cd tbm
2️⃣ Compila todos los módulos con Gradle:

./gradlew clean build

3️⃣ Levanta todo con Docker Compose:

docker-compose up --build

Esto:
Construye tu imagen de la aplicación.

Inicia el contenedor app y el contenedor db (PostgreSQL).

Ejecuta automáticamente el script init.sql para crear tablas y esquemas.

⚙️ Variables de entorno
El archivo .env define:

POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=dbtbm

📚 Documentación
Cuando la aplicación esté corriendo, abre en tu navegador:

OpenAPI JSON: http://localhost:8080/v3/api-docs

✅ Integración Continua
Este proyecto está integrado con SonarCloud para análisis de calidad de código.

Pipeline principal (build.gradle):

Ejecuta tests unitarios y de cobertura con Jacoco.

Sube reportes a SonarCloud.

📌 Endpoints principales
1️⃣ Generar Reporte Mensual
Método: POST

Path: v1/generarreporte/

Descripción: Calcula gastos por empleado en un periodo, genera totales con IVA y guarda/actualiza registros.

Body JSON ejemplo:

json
{
  "anio": "2025",
  "mes": "06"
}
Respuesta exitosa:

json

[
  {
    "dni": "123456",
    "monto": 1000000.00,
    "iva": 190000.00,
    "montoTotal": 1190000.00,
    "fecha": "2025-06-01",
    "asume": true
  }
]
2️⃣ Listar Gastos por Viaje
Método: GET

Path: /v1/gastosxempleado/

Descripción: Devuelve un listado de todos los gastos registrados por empleado.

Respuesta ejemplo:

json

[
  {
    "dniempleado": "123456",
    "fegasto": "2025-06-12T15:30:00",
    "dsvalor": 50000.00,
    "dsmotivo": "Capacitación",
    "dsciudad": "Bogotá"
  },
  {
    "dniempleado": "789012",
    "fegasto": "2025-06-15T10:00:00",
    "dsvalor": 75000.00,
    "dsmotivo": "Reunión",
    "dsciudad": "Medellín"
  }
]
🧩 Pruebas en Postman
✅ En la carpeta /docs encontrarás un archivo tbm-collection.postman.json con todos los endpoints listos para probar en Postman.

1️⃣ Importa el .json en Postman.
2️⃣ Actualiza el Bearer Token en la variable de entorno o en el header Authorization.

📌 Panel de documentación
👉 https://jeyban37.atlassian.net/jira/software/projects/BTS/list?atlOrigin=eyJpIjoiYWIzZGFlMDFhZmU4NDM1YzkxN2I0ZWRjYjkyMWE3ODUiLCJwIjoiaiJ9

🗂️ Estructura
applications/app-service → Módulo principal Spring Boot

domain-model → Entidades y modelos de dominio

domain-usecase → Casos de uso

driven-adapters-r2dbc-repository → Repositorios R2DBC

driven-adapters-jwt → Autenticación JWT

entry-points-reactive-web → Controladores API REST

⚡ Autor
🚀 SURA TBM - Microservicio 

---
