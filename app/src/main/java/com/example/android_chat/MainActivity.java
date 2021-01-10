package com.example.android_chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private Button btnSignIn;
    private EditText txtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        UserDAO.insertUsers();

        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        txtName = (EditText) findViewById(R.id.name);

        this.getSupportActionBar().hide();

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!txtName.getText().toString().equals("")) {
                    String name = txtName.getText().toString();

                    for(User user: UserDAO.getUsersList()) {
                        if(user.getName().equals(name)) {
                            Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("user", (Serializable) user);
                            intent.putExtras(bundle);
                            startActivity(intent);

                            Toast.makeText(getApplication(),
                                    "Seja bem vindo(a) " + user.getName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    Toast.makeText(getApplicationContext(),
                            "Este contato não existe", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "Por favor, insira seu nome para entrar", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
