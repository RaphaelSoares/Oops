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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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

        showProgressDialog();

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

                        hideProgressDialog();
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

        if (user.isEmailVerified())
        {
            Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(i);
        }
        else
        {
            Toast.makeText(LoginActivity.this, "Verifique o e-mail enviado para "+user.getEmail(),
                    Toast.LENGTH_SHORT).show();
        }

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
        /*UsuarioApp usuarioApp = new UsuarioApp(email);

        mDatabase.child("usuarios_app").child(userId).setValue(usuarioApp);*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Log.d(TAG, "Não foi possível efetuar o login via Google");
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            onAuthGoogleSuccess(task.getResult().getUser());
                        }
                        else {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void onAuthGoogleSuccess(FirebaseUser user) {
        Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
        startActivity(i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onLoginGoogleClick(View v)
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
