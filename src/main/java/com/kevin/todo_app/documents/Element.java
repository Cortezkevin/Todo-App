package com.kevin.todo_app.documents;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// TODO: Anotación para permitir serialización polimórfica con Jackson
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,  // Define el tipo de identificación que se usara, en este caso con un nombre
        include = JsonTypeInfo.As.PROPERTY, // Define como se incluirá en el json, en este caso como propiedad
        property = "type" // Define el nombre de la propiedad que se usara para determinar la implementación adecuada
)
// TODO: Anotación para definir explícitamente que clase concreta usar en el tipo correspondiente
@JsonSubTypes({
        @JsonSubTypes.Type(value = Paragraph.class, name = "text"), // Relaciona la clase concreta Paragraph con la propiedad 'text'
        @JsonSubTypes.Type(value = Task.class, name = "task") // Relaciona la clase concreta Paragraph con la propiedad 'task'
})
public interface Element {
    /*
    * TODO: Esta es una forma dinámica de definir el tipo de cada implementación,
    *  usar un getter de la propiedad para implementar en cada clase concreta,
    *  jackson usará este getType para comparar la propiedad que se pasara en el json
     * */
    String getType();
}
