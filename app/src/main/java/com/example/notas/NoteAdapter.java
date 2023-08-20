package com.example.notas;

//Importamos las librerias necesarias a usar
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// Clase NoteAdapter que extiende FirestoreRecyclerAdapter y se encarga de mostrar la lista de notas
public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {

    //Vairable para almacenar el contexto
    Context context;

    // Constructor de la clase que recibe las opciones de FirestoreRecyclerOptions y el contexto
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        // Llamamos al constructor de la superclase (FirestoreRecyclerAdapter) con las opciones proporcionadas
        super(options);
        this.context = context;

    }

    // metodo que se llama cuando se debe enlazar un ViewHolder con una nota específica en la lista
    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        // Asignamos el título, contenido y marca de tiempo de la nota al ViewHolder correspondiente
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.timestampTextView.setText(Utility.timestampToString(note.timestamp));

        // Configuramos un listener para cuando se haga clic en un elemento de la lista
        holder.itemView.setOnClickListener((v)->{
            // Crea un intent para abrir la actividad NoteDetailsActivity y le pasa información de la nota seleccionada
            Intent intent = new Intent(context,NoteDetailsActivity.class);
            intent.putExtra("Titulo",note.title);
            intent.putExtra("Contenido",note.content);

            // Obtiene el ID del documento de la nota seleccionada en Firestore y lo pasa al intent
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId",docId);
            // Inicia la actividad NoteDetailsActivity con el intent configurado
            context.startActivity(intent);
        });

    }
    // Método que se llama cuando se crea un nuevo ViewHolder para un elemento de la lista
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla el diseño del elemento de la lista utilizando el contexto y lo asigna a una nueva View 'view'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item,parent,false);
        // Crea y devuelve un nuevo ViewHolder con la View 'view'
        return new NoteViewHolder(view);
    }

    // Clase NoteViewHolder que extiende RecyclerView.ViewHolder y representa un elemento de la lista
    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView,contentTextView,timestampTextView;

        // Constructor de la clase que recibe la View que representa el elemento de la lista
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            // Vinculamos los TextViews con sus respectivos elementos de la interfaz de usuario
            titleTextView = itemView.findViewById(R.id.note_title_text_view);
            contentTextView = itemView.findViewById(R.id.note_content_text_view);
            timestampTextView = itemView.findViewById(R.id.note_timestamp_text_view);
        }
    }
}
