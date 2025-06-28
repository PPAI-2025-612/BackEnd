
# Backend - Cierre de orden de inspección

API REST desarrollada con Spring Boot para gestionar el cierre de órdenes de inspección dentro del sistema de monitoreo de estaciones sismológicas.

---

## 🚀 Requisitos

- [Java JDK 17 o superior](https://adoptium.net/)
- [Apache Maven 3.8 o superior](https://maven.apache.org/)
- IDE recomendado: [IntelliJ IDEA](https://www.jetbrains.com/idea/) o [Eclipse](https://www.eclipse.org/)

---

## 📦 Instalación

1. Clonar el repositorio:

   ```bash
   git clone <URL-del-repo>
   cd <nombre-del-proyecto>
   ```

2. Ejecutar la aplicación:

   ```bash
   mvn spring-boot:run
   ```

3. La API estará disponible en:

   ```
   http://localhost:8080/api
   ```

---

## 🌐 Endpoints principales

| Método | Endpoint            | Descripción                                         |
|--------|---------------------|-----------------------------------------------------|
| GET    | `/api/ordenes`      | Obtiene la lista de órdenes de inspección simuladas |
| GET    | `/api/motivos`      | Obtiene los motivos posibles para el cierre         |
| POST   | `/api/cerrar-orden` | Cierra una orden con motivos y observaciones        |

---

## 📁 Estructura del proyecto

```
src/
├── main/
│   ├── java/com/dsi/ppai/redsismica/
│   │   ├── controller/     # Controladores REST (CU37Controller.java)
│   │   ├── dto/            # Clases DTO (datos de entrada/salida)
│   │   ├── model/          # Entidades del dominio (simuladas)
│   │   ├── services/       # Lógica de negocio
│   │   ├── repository/     # Repositorios simulados
│   │   └── RedsismicaApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/...            # Pruebas unitarias
```

---

## ⚙️ Configuración CORS

Para permitir solicitudes desde el frontend (por defecto en `http://localhost:3000`), se incluye la siguiente anotación en el controlador:

```java
@CrossOrigin(origins = "http://localhost:3000")
```

Podés modificar el origen si tu frontend está alojado en otro dominio o puerto.

---

## 🧪 Pruebas

Para ejecutar los tests:

```bash
mvn test
```

---

## 📌 Notas

- La API trabaja con datos simulados en memoria (sin base de datos).
- Ideal para pruebas funcionales y de integración con el frontend.
- Puede integrarse fácilmente con una base de datos real en el futuro.

---