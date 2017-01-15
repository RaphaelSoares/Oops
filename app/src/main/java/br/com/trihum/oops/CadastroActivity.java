package br.com.trihum.oops;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.trihum.oops.model.UsuarioApp;
import br.com.trihum.oops.utilities.Funcoes;

public class CadastroActivity extends BaseActivity {

    // componentes da tela
    EditText editTextCadastroNomeCompleto;
    EditText editTextCadastroEmail;
    EditText editTextCadastroEmailRepete;
    EditText editTextCadastroSenha;
    EditText editTextCadastroSenhaRepete;

    String nomeCompleto;
    String email;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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

    public void onEfetuarCadastroClick (View v)
    {
        nomeCompleto = editTextCadastroNomeCompleto.getText().toString();
        email = editTextCadastroEmail.getText().toString();
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
            Toast.makeText(CadastroActivity.this, "Os Emails informados estão diferentes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(senhaRepete))
        {
            Toast.makeText(CadastroActivity.this, "As Senhas informadas estão diferentes", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length()<6)
        {
            Toast.makeText(CadastroActivity.this, "A Senha informada precisa ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

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
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {

                                if (((FirebaseAuthUserCollisionException) task.getException()).getErrorCode().equals("ERROR_EMAIL_ALREADY_IN_USE"))
                                {
                                    Toast.makeText(CadastroActivity.this, "Usuário já existe na base de dados", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(CadastroActivity.this, "Não foi possível criar o usuário", Toast.LENGTH_LONG).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(CadastroActivity.this, "Não foi possível criar o usuário", Toast.LENGTH_LONG).show();
                            }

                        }

                        hideProgressDialog();
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser user) {

        // Write new user
        gravarUsuario(user.getUid(), nomeCompleto, user.getEmail());

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.i("Success", "Yes");
                }
                else{
                    Log.i("Success", "No");}
            }
        });

        Toast.makeText(CadastroActivity.this, "Verifique o e-mail enviado para "+user.getEmail(),
                Toast.LENGTH_LONG).show();
        finish();
    }


    private void gravarUsuario(String userId, String nomeCompleto, String email) {
        UsuarioApp usuarioApp = new UsuarioApp(nomeCompleto, email, "");

        mDatabase.child("usuarios_app").child(Funcoes.convertEmailInKey(email)).setValue(usuarioApp);
    }


    /* Caso seja preciso implementar um botão de redefinir senha...
    String emailAddress = "user@example.com";

auth.sendPasswordResetEmail(emailAddress)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                }
            }
        });
    * */

}
