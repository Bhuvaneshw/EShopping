package com.acutecoder.eshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login = findViewById(R.id.login);
        TextView signup = findViewById(R.id.signup);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        database = openOrCreateDatabase("EShopping", MODE_PRIVATE, null);

        login.setOnClickListener(v -> login(email.getText().toString(), password.getText().toString()));
        signup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
            finish();
        });
    }

    private void login(String email, String password) {
        if (email == null || password == null) {
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!email.contains("@")) {
            Toast.makeText(this, "Enter valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 8) {
            Toast.makeText(this, "Password must be 6 to 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog dialog = new ProgressDialog(this, R.style.Theme_EShopping_Dialog);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Login");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                database.execSQL("create table if not exists user (username varchar(30), password varchar(10), email varchar(50));");
                Cursor query = database.rawQuery("select password from user where email = '" + email + "'", null);
                if (query != null && query.getCount() > 0)
                    if (query.moveToFirst()) {
                        String result = query.getString(0);
                        result.equals(password);
                    }
                assert query != null;
                query.close();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }
}