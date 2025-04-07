package com.example.ingatlan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private static final String LOG_TAG = SignUpActivity.class.getName();
    private static final String PREF_KEY = SignUpActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 12345;

    private EditText email;
    private EditText password;
    private EditText password_conf;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        int secretKey = getIntent().getIntExtra("SECRET_KEY", SECRET_KEY);

        if (secretKey != 12345) {
            finish();
        }

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        password_conf = findViewById(R.id.password_conf);

        sharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        this.email.setText(email);
        this.password.setText(password);
        this.password_conf.setText(password);

        firebaseAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");
    }

    public void register(View view) {
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();
        String password_conf = this.password_conf.getText().toString();

        if(!password.equals(password_conf) ){
            Log.e(LOG_TAG, "Nem egyezik a két jelszó");
            return;
        }

        Log.i(LOG_TAG, "Email cím: " + email + ", Sikeresen regisztrált!");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Sikeres registráció");
                            startListing();
                        } else {
                            Log.d(LOG_TAG, "Sikertelen registráció");
                        }
                    }

                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }

    public void cancel(View view) {
        finish();
    }

    private void startListing(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}