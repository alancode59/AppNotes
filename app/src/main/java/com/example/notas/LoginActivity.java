package com.example.notas;

//Importamos las librerias que vamos a usar
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // Declaramos las variables para los elementos de la interfaz de usuario
    EditText emailEditText, passwordEditText;
    Button loginBtn;
    ProgressBar progressBar;
    TextView createAccountBtnTextView;

    TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vinculamos los elementos de la interfaz de usuario con las variables
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        createAccountBtnTextView = findViewById(R.id.create_account_text_view_btn);

        //Configuración de los listeners para el botón "Iniciar sesión" y el enlace para crear una cuenta
        loginBtn.setOnClickListener((v) -> loginUser());
        createAccountBtnTextView.setOnClickListener((v) -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));

        // Vinculamos el texto de que corresponde con la variable
        forgotPassword = findViewById(R.id.forgot_password);

        //Configuración el listener mediante onclick
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creamos un cuadro de diálogo personalizado para restablecer la  contraseña
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_forgot, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                builder.setView(dialogView);
                AlertDialog dialog = builder.create();

                dialogView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Obtenemos el correo del suario mediante gettext
                        String userEmail = emailBox.getText().toString();

                        //Validamos el correo que se ingreso
                        if (TextUtils.isEmpty(userEmail) || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                            //Si el correo electrónico es inválido, mostrar un mensaje de error
                            Toast.makeText(LoginActivity.this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
                            return; //Retornamos la funcion si se cumple
                        }

                        //enviamos un correo para restablecer la contraseña utilizando Firebase Auth
                        FirebaseAuth.getInstance().sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //Verificamos si el correo fue enviado correctamente
                                if (task.isSuccessful()) {
                                    //Si se ejecuto la tarea mandamos un mensaje para que el usuario revise el correo
                                    Toast.makeText(LoginActivity.this, "Revisa tu correo electrónico", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss(); //Mostramos mensaje
                                } else {
                                    //caso contrario, mostramos un mensaje de error
                                    Toast.makeText(LoginActivity.this, "Error: No se pudo enviar el correo", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                //Configuracion del listener para el botón cancelar en el cuadro de diálogo
                dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Cerramos el cuadro de dialogo
                        dialog.dismiss();
                    }
                });

                // Establecer el fondo del cuadro de diálogo como transparente
                if (dialog.getWindow() != null) {
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                // Mostrar el cuadro de diálogo
                dialog.show();
            }
        });
    }

    //Metodo para iniciar sesion con el correo y contraseña
    void loginUser() {

        //Obtenemos el correo electrónico y contraseña ingresados por el usuario
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        //Validamos los datos ingresados por el usuario
        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }
        //Iniciamos el proceso de inicio de sesion en firebase, le parseamos el correo y el password
        loginAccountInFirebase(email, password);
    }

    //Creamos metodo para iniciar sesión en Firebase con el correo electrónico y contraseña proporcionados
    void loginAccountInFirebase(String email, String password) {

        //Instanciamos firebase
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //mostramos la barra de progreso y ocultar el botón iniciar sesión
        changeInProgress(true);

        //Iniciar sesión con el correo electrónico y contraseña proporcionados mediante Firebase Auth
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Ocultamos la barra de progreso y mostranos el boton de iniciar
                changeInProgress(false);

                //Verificamos si el inicio de sesion fue exitoso
                if (task.isSuccessful()) {

                    //Verificamos si el correo fue verificado
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        //si esta verificado, lo mandamos a la actividad principal
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        //Si el correo no está verificado, mostramos un mensaje de error
                        Utility.showToast(LoginActivity.this, "Correo electrónico no verificado, por favor verifique su correo electrónico.");
                    }
                } else {
                    // Si el inicio de sesión falló, mostramos un mensaje de error con la descripción del error
                    Utility.showToast(LoginActivity.this, task.getException().getMessage());
                }
            }
        });
    }

    //metodo para cambiar la visibilidad de la barra de progreso y el botón iiciar sesión
    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            //si es verdadero, muestra la barra de progreso y oculta el botón iniciar sesión
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        } else {
            //si es falso, oculta la barra de progreso y muestra el botón "Iniciar sesión"
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    //Creamos un metodo para validar los datos ingresados por el usuario
    boolean validateData(String email, String password) {
        //validar el formato del correo
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Si el correo no cumple con el formato válido, mandamos un mensaje de error
            emailEditText.setError("Correo inválido");
            return false; //retornamos falso
        }

        // Validamos la longitud de la contraseña mayor o igual a 6 caracteres
        if (password.length() < 6) {
            // Si la contraseña es menor a 6 caracteres, establece un mensaje de error
            passwordEditText.setError("La longitud de la contraseña no es válida");
            return false; //retornamos falso
        }
        return true; //Si todos los datos son validos y las validaciones son correctas, retornamos true
    }
}
