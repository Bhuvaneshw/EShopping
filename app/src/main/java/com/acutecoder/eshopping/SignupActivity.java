package com.acutecoder.eshopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView login = findViewById(R.id.login);
        Button signup = findViewById(R.id.signup);
        EditText email = findViewById(R.id.email);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);
        EditText cPassword = findViewById(R.id.cpassword);
        database = openOrCreateDatabase("EShopping", MODE_PRIVATE, null);

        login.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
        signup.setOnClickListener(view -> {
            signup(email.getText().toString(), username.getText().toString(), password.getText().toString(), cPassword.getText().toString());
        });
    }

    private void signup(String email, String username, String password, String cPassword) {
        if ((email == null || username == null || password == null) && (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password))) {
            Toast.makeText(this, "Some fields are empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        assert email != null;
        assert password != null;
        if (!email.contains("@")) {
            Toast.makeText(this, "Enter valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6 || password.length() > 8) {
            Toast.makeText(this, "Password must be 6 to 8 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(cPassword)) {
            Toast.makeText(this, "Password do not match!", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Creating Account!");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            dialog.dismiss();
            if (task.isSuccessful()) {
                database.execSQL("create table if not exists user (username varchar(30), password varchar(10), email varchar(50));");
                SQLiteStatement statement = database.compileStatement("insert into user values(?,?,?)");
                statement.bindString(1, username);
                statement.bindString(2, password);
                statement.bindString(3, email);
                statement.executeInsert();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}