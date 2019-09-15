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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;
    private ProgressBar LoginprogressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login_btn);
        loginRegBtn=findViewById(R.id.login_reg_btn);
        loginEmailText=findViewById(R.id.login_email);
        loginPassText=findViewById(R.id.login_pass);
        LoginprogressBar=findViewById(R.id.progressBar);





        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail = loginEmailText.getText().toString();
                String loginPass  =loginPassText.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){

                    LoginprogressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(loginEmail,loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                sendToItemList();

                            }else {
                                String erroMessage = task.getException().getMessage();
                                Toast.makeText(MainActivity.this,"Error :"+erroMessage,Toast.LENGTH_LONG).show();

                            }
                            LoginprogressBar.setVisibility(View.INVISIBLE);

                        }
                    });
                }


            }
        });


    }





    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){

            sendToItemList();
        }

    }
    public void sendToItemList()
    {
        Intent itemIntent = new Intent(MainActivity.this,ItemList.class);
        startActivity(itemIntent);
        finish();
    }

    public void onButtonClick(View view) {

        if(view.getId()==R.id.login_reg_btn)
        {
            openRegister();
        }
    }

    public void openRegister()
    {
        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}
