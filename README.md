# Sistema de Gestión de Álbum de Láminas (Spring Boot + PostgreSQL)

Proyecto final: API REST para gestionar una colección de álbumes y láminas (faltantes, repetidas y carga masiva), desarrollado con **Spring Boot** y **PostgreSQL**.

## Tecnologías
- Java (recomendado 17+)
- Spring Boot 3.x
- Maven (wrapper: `./mvnw`)
- Spring Web
- Spring Data JPA (Hibernate)
- PostgreSQL
- Validation (Jakarta Validation)

## Funcionalidades
### Álbumes
- CRUD completo de álbumes.
- Campos principales: nombre, portada (URL), fecha de lanzamiento, tipo de lámina, total de láminas, descripción.

### Láminas
- CRUD completo de láminas por álbum.
- **Carga masiva** de números de lámina (permite repetidas).
- Consulta de **faltantes**.
- Consulta de **repetidas** (con cantidad por número).
- **Foto opcional por lámina** (subida en multipart/form-data).
  - Las imágenes se guardan localmente en carpeta `uploads/` y se exponen por `/files/**`.

## Requisitos previos
1. Tener **PostgreSQL** instalado y corriendo en local (puerto 5432 por defecto).
2. Crear base de datos y usuario (ejemplo):

```sql
CREATE DATABASE album_laminas;
CREATE USER album_user WITH PASSWORD 'album_pass';
GRANT ALL PRIVILEGES ON DATABASE album_laminas TO album_user;
