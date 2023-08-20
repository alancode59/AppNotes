package com.example.notas;

//Importamos las librerias necesarias
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

public class CreateAccountActivity extends AppCompatActivity {

    // Declararamos las variables para los elementos de la interfaz de usuario
    EditText emailEditText,passwordEditText,confirmPasswordEditText;
    Button createAccountBtn;
    ProgressBar progressBar;
    TextView loginBtnTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Vinculamos los elementos de la interfaz de usuario con las variables
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edit_text);
        createAccountBtn = findViewById(R.id.create_account_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginBtnTextView = findViewById(R.id.login_text_view_btn);

        // Configuramos  los listeners para los botones de crear cuenta e Iniciar sesión
        createAccountBtn.setOnClickListener(v-> createAccount());
        loginBtnTextView.setOnClickListener(v-> finish());

    }

    // Definimos un metodo para crear una nueva cuenta de usuario
    void createAccount(){

        //Mediante getText obtenemos los datos ingresados por el usuario
        String email  = emailEditText.getText().toString();
        String password  = passwordEditText.getText().toString();
        String confirmPassword  = confirmPasswordEditText.getText().toString();

        //Validamos los datos ingresados por el usuario
        boolean isValidated = validateData(email,password,confirmPassword);
        if(!isValidated){
            return;
        }

        //Creamos la cvuenta en firebase y le parseamos email y password
        createAccountInFirebase(email,password);
    }

    //Creamos un metodo para crear la cuenta de usuario en firebase
    void createAccountInFirebase(String email,String password){
        //Mostramos la barra de progreso y ocultamos el botón de crear cuenta
        changeInProgress(true);

        //Creamos una instancia en firebase FirebaseAuth
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //Creamos la cuenta de usuario con el email y contraseña proporcionados, le parseamos los campos como email y password
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Ocultamos la barra de progreso y mostrar el botón crear cuennta nuevamente
                        changeInProgress(false);


                        // Verificamos si la creación de la cuenta fue exitosa
                        if(task.isSuccessful()){

                            //Mostramos mensaje que se creo la cuenta y mandamos un correo de verificacion
                            Utility.showToast(CreateAccountActivity.this,"Cuenta creada con exito, verifique su correo");
                            //Toast.makeText(CreateAccountActivity.this,"Cuenta creada con exito, verifique su correo",Toast.LENGTH_SHORT).show();

                            // Cerramos la sesión del usuario y finalizar la actividad de creación de cuenta
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //En caso de que exista un error, mostrar un mensaje de error
                            Utility.showToast(CreateAccountActivity.this,task.getException().getLocalizedMessage());
                            //Toast.makeText(CreateAccountActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }


    // Generamos un método para cambiar la visibilidad de la barra de progreso y el botón  crear cuenta
    void changeInProgress(boolean inProgress){
        if(inProgress){
            //Si es verdadero, significa que estamos en proceso de creación de la cuenta
            // Por lo tanto, hacemos que la barra de progreso sea visible y ocultamos el botón crear cuenta
            progressBar.setVisibility(View.VISIBLE);
            createAccountBtn.setVisibility(View.GONE);


        }else{
            //De lo contrario el proceso de creacion de cuenta ha terminado o no se concreto
            //ocultamos la barra de progreso y mostramos el boton de crear cuenta
            progressBar.setVisibility(View.GONE);
            createAccountBtn.setVisibility(View.VISIBLE);
        }
    }

    //Creamos metodo para validar los datos ingresados por el usuariop
    boolean validateData(String email,String password,String confirmPassword){



        //Validamos el formatro de correo electronico
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            // Si no tiene formato válido, establece un mensaje de error en el campo de correo electrónic, indicando que el correo electrónico es inválido
            emailEditText.setError("Correo invalido");
            return false; //retornamos falso
        }

        //Validamos para que la contraseña sea mayor o igual a 6 caracteres
        if(password.length()<6){

            // Si la contraseña es menor a 6 caracteres, mandamos un mensaje de error en el campo de contraseña
            passwordEditText.setError("La longitud de la contraseña no es válida");
            return false; //Retornamos falso
        }

        //Validar que la contraseña y la confirmacion coincidan
        if(!password.equals(confirmPassword)){
            // Si la contraseña y su confirmación no coinciden, establece un mensaje de error en el campo de confirmación de contraseña
            confirmPasswordEditText.setError("Contraseña no coincidente");
            return false; //Retornamos falso
        }
        return true; //Si todos los datos son validos, validaciones verdaderas retornamos un true
    }

}