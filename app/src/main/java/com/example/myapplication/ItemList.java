package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;


public class ItemList extends AppCompatActivity {

   private Toolbar mainToolbar;
   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        mAuth=FirebaseAuth.getInstance();

        mainToolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("    Items");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_logout_btn:

                logOut();
                return true;

            case R.id.action_settings_btn:

                Intent settingsIntent = new Intent(ItemList.this, SetupActivity.class);
                startActivity(settingsIntent);

                return true;

             default:
                      return false;

        }


    }

    private void logOut() {

        mAuth.signOut();

        Intent itemIntent = new Intent(ItemList.this,MainActivity.class);
        startActivity(itemIntent);
        finish();
    }
}


