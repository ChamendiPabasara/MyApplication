package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private CircleImageView setup_image;
    private Uri mainImageUri = null;
    private String user_id;
    private EditText setupName;
    private Button SetupBtn;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private ProgressBar setUpProgress;
    private FirebaseFirestore firebaseFirestore;
    private boolean isChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);



        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setSupportActionBar(setupToolbar);
        getSupportActionBar().setTitle("Account Settings");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setup_image = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        SetupBtn = findViewById(R.id.setup_btn);
        setUpProgress=findViewById(R.id.setupProgress);

        user_id = firebaseAuth.getCurrentUser().getUid();

        setUpProgress.setVisibility(View.VISIBLE);
        SetupBtn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){

                        //Toast.makeText(SetupActivity.this, "Data Exists ..", Toast.LENGTH_LONG).show();
                        String name = task.getResult().getString("name");
                        String image = task.getResult().getString("image");
                        mainImageUri = Uri.parse(image);

                        setupName.setText(name);
                        setup_image.setImageURI(Uri.parse(image));



                        /*RequestOptions placeholderRequest = new RequestOptions();
                        placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setup_image);*/
                     }
                    /*else{
                        Toast.makeText(SetupActivity.this, " Data Doesn't Exists .." ,Toast.LENGTH_LONG).show();
                    }*/

                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, " Firestore Retrieve : " + error, Toast.LENGTH_LONG).show();
                }

                setUpProgress.setVisibility(View.INVISIBLE);
                SetupBtn.setEnabled(true);
            }

        });

        SetupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String user_name = setupName.getText().toString();
                if (!TextUtils.isEmpty(user_name) && mainImageUri != null) {
                setUpProgress.setVisibility(View.VISIBLE);

                if (isChanged) {




                        user_id = firebaseAuth.getCurrentUser().getUid();


                        StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                        image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                if (task.isSuccessful()) {

                                    storeFireStore(task,user_name);

                                } else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SetupActivity.this," Image Error : " + error,Toast.LENGTH_LONG).show();

                                    setUpProgress.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
                    }else {

                    storeFireStore(null,user_name);
                }
                }
            }
        });

        setup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        Toast.makeText(SetupActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        ActivityCompat .requestPermissions(SetupActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    }else {

                        /*CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(SetupActivity.this);*/

                        BringImagePicker();
                    }
                }else {

                    BringImagePicker();
                }

            }
        });

    }

    private void storeFireStore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {

        Task<Uri> download_uri = task.getResult().getMetadata().getReference().getDownloadUrl();
        //Toast.makeText(SetupActivity.this, "The Image is Uploaded...", Toast.LENGTH_LONG).show();

        Map<String,String> userMap = new HashMap<>();

        userMap.put("name",user_name);
        userMap.put("image",download_uri.toString());

        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    Toast.makeText(SetupActivity.this, " The User Settings are Updated ..." , Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(SetupActivity.this,ItemList.class);
                    startActivity(intent);
                    finish();

                }else{

                    String error = task.getException().getMessage();
                    Toast.makeText(SetupActivity.this, " FireStore Error : " + error, Toast.LENGTH_LONG).show();
                }
                setUpProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SetupActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mainImageUri= result.getUri();
                setup_image.setImageURI(mainImageUri);

                isChanged =true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
