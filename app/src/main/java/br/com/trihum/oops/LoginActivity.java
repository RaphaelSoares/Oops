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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    // componentes da tela
    EditText editTextLoginUsuario;
    EditText editTextLoginSenha;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String TAG = "Oops";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount googleSignInAccount;

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


        //****************************************
        // Aqui é instanciada a api google para efetuar login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //****************************************
        // Objetos Firebase
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

    //****************************************************************
    // Login comum email/senha
    //****************************************************************
    public void onLoginClick(View v)
    {
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

        showProgressDialog();

        mAuth.signInWithEmailAndPassword(usuario, senha)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Constantes.tipoLogin = Constantes.TIPO_LOGIN_COMUM;
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            Toast.makeText(LoginActivity.this, "Não foi possível efetuar o login",
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                    }
                });

    }

    private void onAuthSuccess(FirebaseUser user) {

        if (user.isEmailVerified())
        {
            Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Verifique o e-mail enviado para "+user.getEmail(),
                    Toast.LENGTH_LONG).show();
        }
    }

    //****************************************************************
    // Cadastro para login comum email/senha
    //****************************************************************
    public void onCadastreClick(View v)
    {
        Intent i =  new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }

    //****************************************************************
    // Login Google
    //****************************************************************
    public void onLoginGoogleClick(View v)
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                googleSignInAccount = result.getSignInAccount();
                firebaseAuthWithGoogle(googleSignInAccount);
            } else {
                Toast.makeText(LoginActivity.this, "Não foi possível autenticar o login no Google",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        showProgressDialog();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            Constantes.tipoLogin = Constantes.TIPO_LOGIN_REDE_SOCIAL;
                            onAuthGoogleSuccess(task.getResult().getUser());
                        }
                        else {
                            //Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Não foi possível entrar usando as credenciais fornecidas pelo Google",
                                    Toast.LENGTH_LONG).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void onAuthGoogleSuccess(FirebaseUser user) {

        if (googleSignInAccount != null)
        {
            gravarUsuarioRedeSocial(user.getUid(),
                    googleSignInAccount.getDisplayName(),
                    googleSignInAccount.getEmail(),
                    (googleSignInAccount.getPhotoUrl()!=null)?googleSignInAccount.getPhotoUrl().toString():"");
        }

        Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void gravarUsuarioRedeSocial(String userId, String nomeCompleto, String email, String foto_perfil) {

        UsuarioApp usuarioApp = new UsuarioApp(nomeCompleto, email, foto_perfil);

        //TODO verificar se existe uma opcao de onSuccess para verificar se a gravacao de fato ocorreu
        mDatabase.child("usuarios_app").child(userId).setValue(usuarioApp);
    }


    //TODO Temporario! Aqui é só para nao ter que ficar digitando email e senha toda hora, deve ser retirado depois!
    public void onLogoClick (View v)
    {
        editTextLoginUsuario.setText("raphasm@gmail.com");
        editTextLoginSenha.setText("12345678");
        onLoginClick(v);
    }

}
