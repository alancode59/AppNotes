package com.example.notas;

//Importamos las librerias a utilizar
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

// Clase Utility que contiene métodos de utilidad
public class Utility {

    // Método estático showToast para mostrar un mensaje Toast en el contexto dado
    static void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    // Método estático getCollectionReferenceForNotes para obtener una referencia a la colección "my_notes" en Firestore
    static CollectionReference getCollectionReferenceForNotes(){

        // Obtener el usuario actualmente autenticado desde Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Crear y devolver una referencia a la colección "my_notes" perteneciente al usuario actual
        return FirebaseFirestore.getInstance().collection("notes")
                .document(currentUser.getUid()).collection("my_notes");
    }

    // Método estático timestampToString para convertir un objeto Timestamp en un formato de cadena de fecha
    static String timestampToString(Timestamp timestamp){
        // Crear un objeto SimpleDateFormat para el formato de fecha "MM/dd/yyyy"
        return new SimpleDateFormat("MM/dd/yyyy").format(timestamp.toDate());
    }


}
