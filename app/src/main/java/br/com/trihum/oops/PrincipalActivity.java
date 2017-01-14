package br.com.trihum.oops;

import android.*;
import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.URL;

public class PrincipalActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private GoogleApiClient mGoogleApiClient;

    private boolean criouFragment;
    Fragment principalFragment;
    private boolean exibindoFragmentPrincipal;

    // API do Google para fazer reverse geocoding
    //https://developers.google.com/maps/documentation/geocoding/start
    //https://maps.googleapis.com/maps/api/geocode/json?latlng=-22.8851519,-43.0878057&key=AIzaSyBVBvxNP36i8jGrtLxGskNn9-EKHGC6kkM
    //http://stackoverflow.com/questions/15191037/how-to-reverse-geocode-in-google-maps-api-2-android

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        criouFragment = false;

        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }

        //*************************************************************
        // Se o app já recebeu um token, gravo no Firebase
        String token = FirebaseInstanceId.getInstance().getToken();
        //Log.d("OOPS","token = "+ token);

        if (token!=null && !token.equals(""))
        {
            mDatabase.child("usuarios_app/"+mAuth.getCurrentUser().getUid()+"/token").setValue(token);
        }
        //*************************************************************


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //********************************************************
        // Pegando componentes do navigator e atribuindo os dados do perfil
        final ImageView fotoPerfil = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_foto_perfil);
        final TextView txtNomeCompleto = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_nome_completo);
        final TextView txtEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_email);

        // Foto do perfil
        mDatabase.child("usuarios_app").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                UsuarioApp usuarioApp = snapshot.getValue(UsuarioApp.class);

                if (fotoPerfil != null) {
                    if (usuarioApp.foto_perfil.startsWith("http")) {
                        try {
                            new DownloadImageTask(fotoPerfil,true).execute(usuarioApp.foto_perfil);
                        } catch (Exception e) {
                        }

                    } else if (usuarioApp.foto_perfil.startsWith("data")) {
                        fotoPerfil.setImageBitmap(Constantes.decodeFrom64toRound(usuarioApp.foto_perfil));
                    }
                }

                // Nome completo e email
                txtNomeCompleto.setText(usuarioApp.nome_completo);
                txtEmail.setText(usuarioApp.email);

                // Carrega em variaveis static para que possam ser lidas pelo fragment
                Constantes.nomeCompleto = usuarioApp.nome_completo;
                Constantes.fotoPerfil = usuarioApp.foto_perfil;
                Constantes.email = usuarioApp.email;

                // coloca o fragment principal se ainda nao foi feito
                if (!criouFragment)
                {
                    criouFragment = true;

                    exibeFragmentPrincipal();

                    /*Fragment fragment = new PrincipalFragment();
                    if (fragment != null)
                    {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_principal, fragment).commit();
                        //this.setTitle("Principal");
                    }*/
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //********************************************************


        //****************************************
        // Aqui é instanciada a api google para efetuar logout/revoke
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //*************************************
        // Aqui vou verificar se é primeira execução. Se sim, vou chamar o tutorial
        SharedPreferences preferences = this.getSharedPreferences(Constantes.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		boolean exibeTutorial = preferences.getBoolean(Constantes.SHARED_PREFERENCES_KEY_EXIBE_TUTORIAL, true);

        if (exibeTutorial)
        {
            Intent i =  new Intent(PrincipalActivity.this, TutorialActivity.class);
            startActivity(i);
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (exibindoFragmentPrincipal)
            {
                sair();
            }
            else
            {
                exibeFragmentPrincipal();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_feedback) {

            Fragment fragment = new FeedbackFragment();

            if (fragment != null)
            {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_principal, fragment).commit();

                exibindoFragmentPrincipal = false;
            }

        } else if (id == R.id.nav_manter_projeto) {

        } else if (id == R.id.nav_saiba_mais) {

            Intent i =  new Intent(PrincipalActivity.this, TutorialActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_sair) {

            sair();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void exibeFragmentPrincipal()
    {
        if (principalFragment == null) principalFragment = new PrincipalFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_principal, principalFragment).commit();

        exibindoFragmentPrincipal = true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void sair()
    {
        if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_COMUM)
        {
            mAuth.signOut();

            finish();
        }
        else if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_GOOGLE)
        {
                /*FirebaseAuth.getInstance().getCurrentUser().unlink(GoogleAuthProvider.PROVIDER_ID)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("OOPS","unlink Google");
                                }
                            }
                        });*/

            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            //updateUI(null);

                            mAuth.signOut();

                            finish();

                        }
                    });
        }
        else if (Constantes.provedorLogin == Constantes.PROVEDOR_LOGIN_FACEBOOK)
        {
            LoginManager.getInstance().logOut();

            mAuth.signOut();

            finish();

        }
    }
}
