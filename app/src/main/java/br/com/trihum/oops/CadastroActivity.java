package br.com.trihum.oops;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CadastroActivity extends AppCompatActivity {

    // componentes da tela
    EditText editTextCadastroNomeCompleto;
    EditText editTextCadastroEmail;
    EditText editTextCadastroEmailRepete;
    EditText editTextCadastroSenha;
    EditText editTextCadastroSenhaRepete;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        //****************************************
        // obtendo os componentes da tela
        editTextCadastroNomeCompleto = (EditText)findViewById(R.id.editTextCadastroNomeCompleto);
        editTextCadastroEmail = (EditText)findViewById(R.id.editTextCadastroEmail);
        editTextCadastroEmailRepete = (EditText)findViewById(R.id.editTextCadastroEmailRepete);
        editTextCadastroSenha = (EditText)findViewById(R.id.editTextCadastroSenha);
        editTextCadastroSenhaRepete = (EditText)findViewById(R.id.editTextCadastroSenhaRepete);

        mAuth = FirebaseAuth.getInstance();
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

    public void onEfetuarCadastroClick (View v)
    {
        // TODO efetuar o cadastro
        String nomeCompleto = editTextCadastroNomeCompleto.getText().toString();
        String email = editTextCadastroEmail.getText().toString();
        String emailRepete = editTextCadastroEmailRepete.getText().toString();
        String senha = editTextCadastroSenha.getText().toString();
        String senhaRepete = editTextCadastroSenhaRepete.getText().toString();

        if (nomeCompleto.equals("") || email.equals("") ||
                emailRepete.equals("") || senha.equals("") || senhaRepete.equals(""))
        {
            Toast.makeText(CadastroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.equals(emailRepete))
        {
            Toast.makeText(CadastroActivity.this, "Email informados estão diferentes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(senhaRepete))
        {
            Toast.makeText(CadastroActivity.this, "Senhas informadas estão diferentes", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("OOPS", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        }
                        else
                        {
                            Toast.makeText(CadastroActivity.this, "Falha na criação do usuário", Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        gravarUsuario(user.getUid(), user.getEmail());

        //user.sendEmailVerification();

        finish();
    }

    private void gravarUsuario(String userId, String email) {
        /*UsuarioApp usuarioApp = new UsuarioApp(email);

        mDatabase.child("usuarios_app").child(userId).setValue(usuarioApp);*/
    }


}
