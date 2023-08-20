package com.example.notas;
//Importamos las librerias correspondientes
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

// Clase NoteDetailsActivity que extiende de AppCompatActivity y se encarga de mostrar los detalles de una nota
public class NoteDetailsActivity extends AppCompatActivity {

    // Declaración de variables para los elementos de la interfaz de usuario
    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title,content,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextViewBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        // Vinculación de los elementos de la interfaz de usuario con las variables
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteNoteTextViewBtn  = findViewById(R.id.delete_note_text_view_btn);

        // Obtener el título, contenido y docId de la nota enviados desde la actividad anterior mediante .gettext
        title = getIntent().getStringExtra("Titulo");
        content= getIntent().getStringExtra("Contenido");
        docId = getIntent().getStringExtra("docId");

        // Verificar si el docId no es nulo ni está vacío para determinar si es un modo de edición
        if(docId!=null && !docId.isEmpty()){
            isEditMode = true;
        }

        // Asignar los valores del título y contenido de la nota a los elementos de la interfaz de usuario
        titleEditText.setText(title);
        contentEditText.setText(content);

        // Si está en modo de edición, cambiar el texto del título de la página y mostrar el botón de eliminar nota
        if(isEditMode){
            pageTitleTextView.setText("Edita tu nota");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        // Configurar el listener para el botón "Guardar nota" que llama al método saveNote()
        saveNoteBtn.setOnClickListener( (v)-> saveNote());

        // Configurar el listener para el botón "Eliminar nota" que llama al método deleteNoteFromFirebase()
        deleteNoteTextViewBtn.setOnClickListener((v)-> deleteNoteFromFirebase() );
    }

    // Método para guardar la nota en Firebase
    void saveNote(){
        // Obtener el título y contenido de la nota desde los elementos de la interfaz de usuario mediante.getText
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        // Verificamos si el título está vacío y mostrar un error en caso afirmativo
        if(noteTitle==null || noteTitle.isEmpty() ){
            titleEditText.setError("Se requiere título");
            return;
        }

        // Crear una nueva instancia de la clase Note con el título, contenido y marca de tiempo actuales
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        // Guardar la nota en Firebase llamando al método saveNoteToFirebase()
        saveNoteToFirebase(note);
    }

    // Método para guardar la nota en Firebase
    void saveNoteToFirebase(Note note){
        // Crear una referencia al documento de la nota en Firestore
        DocumentReference documentReference;
        // Verificar si está en modo de edición o si es una nueva nota y asignar la referencia correspondiente
        if(isEditMode){
            //actualizamos la nota
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        }else{
            //se crea una nueva nota
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        // Guardar la nota en Firestore
        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    //Si la nota se ha guardado, mostramos mensaje y finalizar la actividad
                    Utility.showToast(NoteDetailsActivity.this,"Nota añadida con éxito");
                    finish();
                }else{
                    //Caso contratiop mandamos un mensaje de error
                    Utility.showToast(NoteDetailsActivity.this,"Error al agregar la nota");
                }
            }
        });
    }

    //Metodo para eliminar la nota de firebase
    void deleteNoteFromFirebase(){
        // Crear una referencia al documento de la nota en Firestore
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        //Eliminamos la nota de firestone
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // si la nota se ha eliminado,  mostramos mensaje y finaliza la actividad
                    Utility.showToast(NoteDetailsActivity.this,"Nota eliminada con éxito");
                    finish();
                }else{
                    //Caso contrario, mandamos un mensaje de error y mandamos mensjae
                    Utility.showToast(NoteDetailsActivity.this,"Error al eliminar la nota");
                }
            }
        });
    }
}