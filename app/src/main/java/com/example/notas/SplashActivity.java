package com.example.notas;
//Importamos las librerias que usaremos
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// Clase SplashActivity que extiende de AppCompatActivity y se encarga de mostrar una pantalla de presentación
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Se crea un nuevo Handler para ejecutar una acción después de un cierto retraso
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Obtenemos el usuario actualmente autenticado desde Firebase
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser==null){
                    // Si el usuario no está autenticado, iniciar LoginActivity
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                }else{
                    // Si el usuario está autenticado, iniciar MainActivity
                    startActivity(new Intent(SplashActivity.this,MainActivity.class));
                }
                // Finalizar la actividad SplashActivity
                finish();
            }
        },1000);   // El Handler espera 1000 milisegundos (1 segundo) antes de ejecutar la acción

    }
}