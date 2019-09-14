package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText reg_emailField;
    private EditText reg_PassField;
    private EditText reg_confirmPassFied;
    private Button reg_btn;
    private ProgressBar reg_progressBar;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.reg_login_btn);
        reg_btn =findViewById(R.id.reg_btn);
        reg_emailField=findViewById(R.id.reg_email);
        reg_PassField=findViewById(R.id.reg_pass);
        reg_confirmPassFied=findViewById(R.id.reg_confirm_pass);
        reg_progressBar=findViewById(R.id.reg_progress);

        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Registration ");


        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = reg_emailField.getText().toString();
                String pass = reg_PassField.getText().toString();
                String confirmPass = reg_confirmPassFied.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) && !TextUtils.isEmpty(confirmPass)){

                    if (pass.equals(confirmPass)){

                        reg_progressBar.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()){

                                    Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                                    startActivity(setupIntent);
                                    finish();

                                }else {

                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                }
                                reg_progressBar.setVisibility(View.INVISIBLE);

                            }
                        });


                    }else {
                        Toast.makeText(RegisterActivity.this,"Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    public void onButtonClick(View view) {

        if(view.getId()==R.id.reg_login_btn)
        {
            openLoginPage();
        }
    }

    public void openLoginPage()
    {
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();
        if (currentUser != null){
            sendToItem_list();
        }
    }

    private void sendToItem_list() {

        Intent intent = new Intent(RegisterActivity.this,ItemList.class);
        startActivity(intent);
        finish();
    }
}
