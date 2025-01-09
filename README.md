# Notas API REST

Bienvenido al repositorio de la **API REST** para una **aplicación web de notas**. Esta API permite a los usuarios gestionar sus notas de manera eficiente y sencilla.

## Descripción

Esta API REST proporciona un conjunto de servicios RESTful que permiten interactuar con una base de datos de notas. Los usuarios pueden gestionar sus notas de manera intuitiva, almacenándolas y modificándolas, ademas de otras funcionalidades según sea necesario. 

## Funcionalidades

- **Crear una nueva nota**: Permite a los usuarios agregar nuevas notas con un título y contenido que puede ser texto o tareas.
- **Obtener todas las notas**: Permite visualizar todas las notas almacenadas, ademas de contar con paginación.
- **Obtener una nota específica**: Permite obtener los detalles de una nota en particular.
- **Busqueda de notas**: Permite buscar notas a traves de algunos criterios, como titulo y etiquetas.
- **Actualizar una nota**: Permite modificar el título y contenido de una nota existente.
- **Fijar una nota**: Permite fijar o desfijar una nota por encima de las demas.
- **Eliminación Logica de una nota**: Permite eliminar una nota de forma logica, cambiando el estado de la nota en eliminado.
- **Eliminación Logica de muchas notas**: Permite eliminar muchas notas de forma logica.
- **Eliminación Física de una nota**: Permite eliminar una nota de forma física, borrandola completamente de la base de datos.
- **Eliminación Logica de muchas notas**: Permite eliminar muchas notas de forma física.
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
        "completed: false
      }
    ],
    "tags": [
      "tag1","tag2"
    ],
    "color": "GREEN",
    "user": "Usuario"
}
