package com.example.notas;

// Importación de la clase Timestamp de Firebase
import com.google.firebase.Timestamp;

// Definición de la clase Note
public class Note {

    // Declaración de variables de instancia para almacenar el título, el contenido y la marca de tiempo de una nota
    String title;
    String content;
    Timestamp timestamp;

    // Constructor por defecto de la clase Note, se requiere para usar firebase
    public Note() {
    }

    //metodo para obtener el título de la nota
    public String getTitle() {
        return title;
    }

    //metodo para establecer el título de la nota
    public void setTitle(String title) {
        this.title = title;
    }


    //metodo para obtener el contenido de la nota
    public String getContent() {
        return content;
    }

    //metodo para establecer el contenido de la nota
    public void setContent(String content) {
        this.content = content;
    }


    //metodo para obtener la marca de tiempo de la nota
    public Timestamp getTimestamp() {
        return timestamp;
    }


    //metodo para establecer la marca de tiempo de la nota
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
