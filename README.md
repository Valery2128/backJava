# API de Franquicias - Prueba Técnica Accenture

He desarrollado esta API REST reactiva con **Spring Boot 3** para gestionar franquicias, sucursales y productos. Mi enfoque principal fue la escalabilidad y el rendimiento, aprovechando el paradigma reactivo para manejar flujos de datos de manera eficiente.

## 🏗️ Mi Enfoque de Arquitectura (El "Por qué")
Para esta prueba, elegí un **modelo de documentos embebidos** en MongoDB. 

### Relación de Entidades:
La jerarquía sigue este orden: **Franquicia 1 ➔ N Sucursales 1 ➔ N Productos**. 
- En lugar de separar las sucursales y productos en tablas o colecciones distintas, he decidido embeberlas dentro de la **Franquicia** raz.
- **Ventaja Técnica**: El requisito de obtener el "Producto con más stock por sucursal" se resuelve en una sola lectura de disco. No necesito hacer JOINs manuales ni múltiples consultas, lo que garantiza que la API responda en milisegundos incluso con mucha carga.

## 🚀 Mi Stack Tecnológico
- **Java 17** y **Spring Boot 3.2.5**
- **Spring WebFlux**: Programación Reactiva para evitar bloqueos de hilos.
- **Spring Data Reactive MongoDB**: Persistencia NoSQL.
- **Docker & Docker Compose**: Para un despliegue "plug-and-play".
- **Terraform**: Demostración de Infraestructura como Código.

---

## 🛠️ Cómo puedes ejecutarlo

### El camino rápido (Docker) 🐳
1. Abre una terminal en la raíz.
2. Ejecuta:
   ```bash
   docker-compose up --build
   ```
3. La API estará lista en `http://localhost:8080`.

### El camino manual
1. Requiere **MongoDB** en `localhost:27017`.
2. Ejecuta: `mvn spring-boot:run`.

---

## 📖 Prueba la API (Swagger UI)
Una vez que la app esté corriendo, puedes usar la interfaz visual para probar todo:
🔗 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Ejemplo de flujo sugerido para probar:
1. **POST /api/franchises**: Crea una franquicia llamada "Accenture Corp".
2. **POST /.../branches**: Usa el ID anterior para crear la sucursal "Sede Bogotá".
3. **POST /.../products**: Agrega "Laptop" con stock 10 y "Monitor" con stock 50.
4. **GET /.../top-stock**: Verás que el sistema te reporta automáticamente que el **Monitor** es el producto líder en la "Sede Bogotá".

---

## 📌 Resumen de Endpoints Principales

| Método | Path | Mi Lógica de Negocio |
|---|---|---|
| POST | `/api/franchises` | Registro el documento raíz. |
| POST | `/api/franchises/{id}/branches` | Agrego sucursales al array interno. |
| POST | `/api/franchises/{id}/branches/{bId}/products` | Inserto productos en la lista de la sucursal. |
| GET | `/api/franchises/{id}/top-stock` | **Lógica Estrella**: Uso Java Streams para comparar stocks en memoria. |
| PATCH | `.../name` | Endpoints Plus para actualizar nombres en cualquier nivel. |

## ☁️ Infraestructura (Terraform)
En la carpeta `/terraform` incluí un `main.tf` que muestra cómo automatizaría el despliegue de un clúster de **MongoDB Atlas** en AWS. Esto demuestra mi capacidad para manejar el ciclo de vida completo de la app (DevOps).

## 📄 Notas de mi Desarrollo
- **Clean Code**: Usé **Lombok** para evitar el código basura (getters/setters).
- **Resiliencia**: Implementé un **GlobalExceptionHandler** para capturar errores y devolver mensajes claros (como cuando un ID no existe).
- **Reactividad**: Todo el flujo, desde el controlador hasta la base de datos, es no bloqueante.
