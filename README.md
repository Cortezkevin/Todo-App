# Notas API REST

Bienvenido al repositorio de la **API REST** para una **aplicación web de notas**. Esta API permite a los usuarios gestionar sus notas de manera eficiente y sencilla.

## Descripción

Esta API REST proporciona un conjunto de servicios RESTful que permiten interactuar con una base de datos de notas. Los usuarios pueden gestionar sus notas de manera intuitiva, almacenándolas y modificándolas, ademas de otras funcionalidades según sea necesario. 

## Funcionalidades

- **Crear una nueva nota**: Permite a los usuarios agregar nuevas notas con un título y contenido que puede ser texto o tareas.
- **Obtener todas las notas**: Permite visualizar todas las notas almacenadas, además de contar con paginación.
- **Obtener una nota específica**: Permite obtener los detalles de una nota en particular.
- **Búsqueda de notas**: Permite buscar notas a travels de algunos criterios, como titulo y etiquetas.
- **Actualizar una nota**: Permite modificar el título y contenido de una nota existente.
- **Fijar una nota**: Permite fijar o desfijar una nota por encima de las demás.
- **Eliminación Lógica de una nota**: Permite eliminar una nota de forma logic, cambiando el estado de la nota en eliminado.
- **Eliminación Lógica de muchas notas**: Permite eliminar muchas notas de forma logic.
- **Eliminación Física de una nota**: Permite eliminar una nota de forma física, borrándola completamente de la base de datos.
- **Eliminación Lógica de muchas notas**: Permite eliminar muchas notas de forma física.
- **Restauración de una nota**: Permite restaurar una nota previamente eliminada de forma lógica.
- **Restauración de muchas notas**: Permite restaurar muchas notas.
- **Agregar una nota a favoritos**: Permite marcar una nota como favorita.
- **Agregar muchas notas a favoritos**: Permite marcar muchas notas como favoritas.
- **Crear un usuario**: Permite crearte una cuenta para poder hacer uso de las endpoints.
- **Login de usuario**: Permite loguearse con una cuenta ya existente.

## Endpoints

### 1. `POST /api/notes`
Crea una nueva nota.

**Request body**:
```json
{
    "title": "Titulo de la nota",
    "content": [
      {
        "index": 0,
        "type": "text",
        "text": "Contenido"
      }
      {
        "index": 1,
        "type": "task",
        "description": "Descripcion de la tarea",
        "completed": false
      }
    ],
    "tags": [
      "tag1","tag2"
    ],
    "color": "GREEN",
    "user": "Usuario"
}
```
### 2. `GET /api/notes`
Listar todas las notas con sus datos minimos por usuario.

### Parámetros de consulta (Query Parameters):

- `page` (opcional): Npumero de pagina a mostrar.
  - Tipo: `int`
  - Ejemplo: `1,2,3...`
  - Valor por defecto: `1`
  
- `size` (opcional): Cantidad de notas por pagina.
  - Tipo: `int`
  - Ejemplo: `5,6,7...`
  - Valor por defecto: `5`

**Response body**:
```json
[
    {
        "id": "677b0f54711d370d1c1dd0b1",
        "title": "Nota 1",
        "color": "GREEN",
        "tags": [
            "tag1",
            "tag2"
        ],
        "createdAt": "2025-01-05T18:01:40.587",
        "updatedAt": "2025-01-06T14:46:27.63",
        "favorite": false,
        "fixed": true
    },
    {
        "id": "677f3b2b052bdf255af60359",
        "title": "Nota 2",
        "color": "GREEN",
        "tags": [],
        "createdAt": "2025-01-08T21:57:47.529",
        "updatedAt": null,
        "favorite": false,
        "fixed": false
    }
]
```
### 3. `GET /api/note/{id}`
Buscar nota por ID, perteneciente al usuario autenticado.

### 4. `PUT /api/note`
Realiza la actualización de una nota existente.

### 5. `PUT /api/note/logicalDelete/{id}`
Elimina lógicamente una nota por su ID, la agrega a la papelera.

### 5. `PUT /api/note/restore/{id}`
Restaura una nota de la papelera por su ID
