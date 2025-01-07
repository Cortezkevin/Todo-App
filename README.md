# Notas API REST

Bienvenido al repositorio de la **API REST** para una **aplicación web de notas**. Esta API permite a los usuarios crear, leer, actualizar y eliminar notas de manera eficiente y sencilla.

## Descripción

Esta API proporciona un conjunto de servicios RESTful que permiten interactuar con una base de datos de notas. Los usuarios pueden gestionar sus notas de manera intuitiva, almacenándolas y modificándolas según sea necesario. 

## Funcionalidades

- **Crear una nueva nota**: Permite a los usuarios agregar nuevas notas con un título y contenido.
- **Obtener todas las notas**: Permite visualizar todas las notas almacenadas.
- **Obtener una nota específica**: Permite obtener los detalles de una nota en particular.
- **Actualizar una nota**: Permite modificar el título y contenido de una nota existente.
- **Eliminar una nota**: Permite eliminar una nota de la base de datos.

## Endpoints

### 1. `POST /api/notes`
Crea una nueva nota.

**Request body**:
```json
{
  "title": "Título de la nota",
  "content": "Contenido de la nota"
}
