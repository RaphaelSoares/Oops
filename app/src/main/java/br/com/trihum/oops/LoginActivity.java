package br.com.trihum.oops;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    // componentes da tela
    EditText editTextLoginUsuario;
    EditText editTextLoginSenha;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String TAG = "OOPS";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInAccount googleSignInAccount;
    private CallbackManager callbackManager;

    //private AuthCredential emailPasswordCredential;
    //private AuthCredential googleCredential;
    //private AuthCredential facebookCredencial;

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


        //****************************************
        // Aqui é instanciada a api facebook para efetuar login
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }

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
    // No sucesso de login...
    //****************************************************************
    private void onAuthSuccess(FirebaseUser user) {

        if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_COMUM)
        {
            //linkWithCredential(emailPasswordCredential);
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
        else if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_GOOGLE)
        {
            //linkWithCredential(googleCredential);
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
        else if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_FACEBOOK)
        {
            //linkWithCredential(facebookCredencial);
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();
                Log.d("OOPS","providerID = "+providerId);

                if (providerId.equals("facebook.com"))
                {
                    // UID specific to the provider
                    String uid = profile.getUid();
                    Log.d("OOPS","user.uid = "+user.getUid());
                    Log.d("OOPS","uid = "+uid);

                    // Name, email address, and profile photo Url
                    String name = profile.getDisplayName();
                    Log.d("OOPS","name = "+name);
                    String email = profile.getEmail();
                    Log.d("OOPS","user.email = "+user.getEmail());
                    Log.d("OOPS","email = "+email);
                    Uri photoUrl = profile.getPhotoUrl();
                    Log.d("OOPS","photoUrl = "+photoUrl);

                    gravarUsuarioRedeSocial(user.getUid(),name,user.getEmail(),(photoUrl!=null)?photoUrl.toString():"");
                    break;
                }
            };

            Intent i =  new Intent(LoginActivity.this, PrincipalActivity.class);
            startActivity(i);
        }

    }

    private void linkWithCredential(AuthCredential credential)
    {
        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //onAuthSuccess(task.getResult().getUser());
                        }
                        else {
                            Log.d(TAG, "linkWithCredential = "+task.getException());
                        }

                    }
                });
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

        Constantes.provedorLogin = Constantes.PROVEDOR_LOGIN_COMUM;
        Constantes.tipoLogin = Constantes.TIPO_LOGIN_COMUM;
        //emailPasswordCredential = EmailAuthProvider.getCredential(usuario, senha);

        mAuth.signInWithEmailAndPassword(usuario, senha)
        //mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

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

    //****************************************************************
    // Login Google
    //****************************************************************
    public void onLoginGoogleClick(View v)
    {
        Constantes.provedorLogin = Constantes.PROVEDOR_LOGIN_GOOGLE;
        Constantes.tipoLogin = Constantes.TIPO_LOGIN_REDE_SOCIAL;
        showProgressDialog();

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
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential googleCredential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(googleCredential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //linkWithCredential(credential);
                            onAuthSuccess(task.getResult().getUser());
                        }
                        else {
                            Log.d(TAG, "firebaseAuthWithGoogle signInWithCredential = "+task.getException());
                            Toast.makeText(LoginActivity.this, "Não foi possível entrar usando as credenciais fornecidas pelo Google",
                                    Toast.LENGTH_LONG).show();
                        }

                        hideProgressDialog();
                    }
                });
    }

    //****************************************************************
    // Login Facebook
    //****************************************************************
    public void onLoginFacebookClick(View v)
    {
        //showProgressDialog();
        Constantes.provedorLogin = Constantes.PROVEDOR_LOGIN_FACEBOOK;
        Constantes.tipoLogin = Constantes.TIPO_LOGIN_REDE_SOCIAL;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //Log.d("OOPS", "Login success");

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential facebookCredencial = FacebookAuthProvider.getCredential(token.getToken());


        mAuth.signInWithCredential(facebookCredencial)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful())
                        {
                            //linkWithCredential(facebookCredencial);
                            onAuthSuccess(task.getResult().getUser());
                        }
                        else {

                            Log.d(TAG, "task exception : " + task.getException());
                        }

                        // ...
                    }
                });
    }




    //*************************************************************************************
    private void gravarUsuarioRedeSocial(String userId, String nomeCompleto, String email, String foto_perfil) {

        UsuarioApp usuarioApp = new UsuarioApp(nomeCompleto, email, foto_perfil);

        //TODO verificar se existe uma opcao de onSuccess para verificar se a gravacao de fato ocorreu
        mDatabase.child("usuarios_app").child(userId).setValue(usuarioApp);
    }

    //****************************************************************
    // Cadastro para login comum email/senha
    //****************************************************************
    public void onCadastreClick(View v)
    {
        Intent i =  new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(i);
    }


    //TODO Temporario! Aqui é só para nao ter que ficar digitando email e senha toda hora, deve ser retirado depois!
    public void onLogoClick (View v)
    {
        editTextLoginUsuario.setText("raphasm@gmail.com");
        editTextLoginSenha.setText("12345678");
        onLoginClick(v);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}
