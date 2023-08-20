package com.example.notas;

//Importamos las librerias a usar
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;


//importamos las funciones principales a usar de firebase
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    // Declararamos las variables para los elementos de la interfaz de usuario
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Vinculamos los elementos de la interfaz de usuario con las variables

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vinculación de los elementos de la interfaz de usuario con las variables
        addNoteBtn = findViewById(R.id.add_note_btn);
        recyclerView = findViewById(R.id.recyler_view);
        menuBtn = findViewById(R.id.menu_btn);

        // Configuración del listener para el botón agregar notaa que abre la actividad NoteDetailsActivity
        addNoteBtn.setOnClickListener((v)-> startActivity(new Intent(MainActivity.this,NoteDetailsActivity.class)) );
        //Configuración del listener para el botón de menú que muestra el menú emergente
        menuBtn.setOnClickListener((v)->showMenu() );
        // Configuración del RecyclerView para mostrar la lista de notas
        setupRecyclerView();
    }

    // Método para mostrar el menú emergente de opciones
    void showMenu(){
        // Crear un menú emergente (popupMenu) asociado al botón de menú
        PopupMenu popupMenu  = new PopupMenu(MainActivity.this,menuBtn);

        // Agregamos la opción "Cerrar sesión" al menú emergente
        popupMenu.getMenu().add("Cerrar sesión");
        // Mostrar el menú emergente
        popupMenu.show();
        // Configurar el listener para cuando se seleccione una opción del menú
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Verificar si se seleccionó la opción cerrar sesion
                if(menuItem.getTitle()=="Cerrar sesión"){
                    // Cerrar la sesión del usuario y redirigir a la actividad de inicio de sesión (LoginActivity)
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }

    // metodo para configurar el RecyclerView y mostrar la lista de notas
    void setupRecyclerView(){
        // Obtener una referencia a la colección de notas ordenadas por marca de tiempo en orden descendente
        Query query  = Utility.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);

        // Configurar las opciones para el adaptador de FirestoreRecyclerOptions utilizando la clase Note
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();

        // Configurar el diseño lineal para el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear el adaptador personalizado (NoteAdapter) utilizando las opciones y el contexto de esta actividad
        noteAdapter = new NoteAdapter(options,this);
        // Establecer el adaptador en el RecyclerView
        recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();         // Iniciar la escucha del adaptador para obtener actualizaciones de la base de datos
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detener la escucha del adaptador cuando la actividad está en segundo plano
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Notificar al adaptador que los datos pueden haber cambiado (útil al regresar a esta actividad desde otra)
        noteAdapter.notifyDataSetChanged();
    }

}