package br.com.trihum.oops;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    // componentes da tela
    EditText editTextLoginUsuario;
    EditText editTextLoginSenha;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //****************************************
        // obtendo os componentes da tela
        editTextLoginUsuario = (EditText)findViewById(R.id.editTextLoginUsuario);
        editTextLoginSenha = (EditText)findViewById(R.id.editTextLoginSenha);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();



    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    public void onLoginClick(View v)
    {
        Log.d("TESTE","Login");

        String usuario = editTextLoginUsuario.getText().toString();
        String senha = editTextLoginSenha.getText().toString();

        if (usuario.equals(""))
        {
            Toast.makeText(LoginActivity.this, "Informe o usuário", Toast.LENGTH_SHORT).show();
            return;
        }
        if (senha.equals(""))
        {
            Toast.makeText(LoginActivity.this, "Informe a senha", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(usuario, senha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("LOGIN", "signIn:onComplete:" + task.isSuccessful());


                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Não foi possível efetuar o login",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public void onCadastreClick(View v)
    {
        Intent i =  new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }


    private void onAuthSuccess(FirebaseUser user) {

        /*String username = usernameFromEmail(user.getEmail());

        // Go to MainActivity
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        finish();*/

        gravarUsuario(user.getUid(), user.getEmail());

        /*Toast.makeText(LoginActivity.this, "Sign In Success with user "+user.getEmail()+", UID = "+user.getUid(),
                Toast.LENGTH_SHORT).show();*/

        Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
        startActivity(i);



        /*
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Infracao infracao = dataSnapshot.getValue(Infracao.class);

                Log.d("TESTE","UID = "+infracao.uid+", TIPO = "+infracao.tipo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        mPostReference = FirebaseDatabase.getInstance().getReference().child("infracoes").child("0001");
        mPostReference.addValueEventListener(postListener);
        */

    }

    private void gravarUsuario(String userId, String email) {
        UsuarioApp usuarioApp = new UsuarioApp(email);

        mDatabase.child("usuarios_app").child(userId).setValue(usuarioApp);
    }
}
