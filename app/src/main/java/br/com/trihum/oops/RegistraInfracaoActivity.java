package br.com.trihum.oops;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegistraInfracaoActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    GPSTracker gps;
    double latitude;
    double longitude;
    String endereco;
    String dataRegistro;
    String horaRegistro;

    TextView registroEnderecoInfracao;
    TextView registroDataInfracao;
    FrameLayout frameTipoInfracao;
    Button btnTipoInfracao;
    FloatingActionButton fabConfirmaRegistroInfracao;
    RadioGroup rgGrupoRegistro1;
    EditText txtArea;
    int tipoEscolhido;

    String encoded_full;
    String encoded_mini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_infracao);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ImageView foto = (ImageView) findViewById(R.id.snapshot_capturado);
        registroEnderecoInfracao = (TextView) findViewById(R.id.registroEnderecoInfracao);
        registroDataInfracao = (TextView) findViewById(R.id.registroDataInfracao);
        frameTipoInfracao = (FrameLayout) findViewById(R.id.frame_radio_tipo_infracao);
        btnTipoInfracao = (Button) findViewById(R.id.btnTipoInfracao);
        fabConfirmaRegistroInfracao = (FloatingActionButton) findViewById(R.id.fabConfirmaRegistroInfracao);
        rgGrupoRegistro1 = (RadioGroup) findViewById(R.id.rgGrupoRegistro1);
        txtArea = (EditText) findViewById(R.id.txtArea);

        frameTipoInfracao.setVisibility(View.GONE);

        if(getIntent().hasExtra("byteArray")) {
            byte[] arrayBytesFoto = getIntent().getByteArrayExtra("byteArray");

            Bitmap bitmap = BitmapFactory.decodeByteArray(arrayBytesFoto, 0, arrayBytesFoto.length);
            foto.setImageBitmap(bitmap);

            encoded_full = Base64.encodeToString(arrayBytesFoto, Base64.DEFAULT);
        }
        if(getIntent().hasExtra("byteArrayMini")) {
            byte[] arrayBytesFotoMini = getIntent().getByteArrayExtra("byteArrayMini");

            encoded_mini = Base64.encodeToString(arrayBytesFotoMini, Base64.DEFAULT);
        }

        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Date now = new Date();
        dataRegistro = new SimpleDateFormat("yyyy-MM-dd").format(now);
        horaRegistro = new SimpleDateFormat("HH:mm:ss").format(now);
        registroDataInfracao.setText(new SimpleDateFormat("dd/MM/yyyy").format(now));

        latitude = 0;
        longitude = 0;
        endereco = "";
        // Verificando o GPS e se consegue obter as coordenadas
        gps = new GPSTracker(RegistraInfracaoActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Geocoder geoCoder = new Geocoder(this);
            try {
                List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                endereco = bestMatch.getAddressLine(0)+" "+bestMatch.getLocality()+" "+bestMatch.getAdminArea();
                Log.d("OOPS","endereco = "+endereco);
                registroEnderecoInfracao.setText(endereco);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed()
    {
        try {
            Intent intent = new Intent(RegistraInfracaoActivity.this, PrincipalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } catch (Exception e){
        }

        super.onBackPressed();  // optional depending on your needs
    }

    public void onSalvarInfracaoClick (View v)
    {
        // Verificando se escolheu o tipo de infração
        int checkedRadioButtonId = rgGrupoRegistro1.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            tipoEscolhido = -1;
            Toast.makeText(RegistraInfracaoActivity.this, "Escolha o tipo de infração",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if (checkedRadioButtonId == R.id.rb1Gr1Registro) tipoEscolhido = 1;
            else if (checkedRadioButtonId == R.id.rb2Gr1Registro) tipoEscolhido = 2;
            else if (checkedRadioButtonId == R.id.rb3Gr1Registro) tipoEscolhido = 3;
            else if (checkedRadioButtonId == R.id.rb4Gr1Registro) tipoEscolhido = 4;
            else if (checkedRadioButtonId == R.id.rb5Gr1Registro) tipoEscolhido = 5;
            else if (checkedRadioButtonId == R.id.rb6Gr1Registro) tipoEscolhido = 6;
            else if (checkedRadioButtonId == R.id.rb7Gr1Registro) tipoEscolhido = 7;
            else if (checkedRadioButtonId == R.id.rb8Gr1Registro) tipoEscolhido = 8;
        }

        if (latitude==0 && longitude==0)
        {
            Toast.makeText(RegistraInfracaoActivity.this, "Não foi possível obter as coordenadas do local da infração. Por favor, tente novamente.",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            showProgressDialog();
            obtemChaveESalvaDadosInfracao(latitude,longitude);
        }

    }

    public void obtemChaveESalvaDadosInfracao(double latitude, double longitude) {

        final double lat = latitude;
        final double lon = longitude;

        DatabaseReference countRef = mDatabase.child("controles").child("contador_infracao");

        countRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                String valorS = mutableData.getValue(String.class);

                if (valorS == null) {
                    return Transaction.success(mutableData);
                }

                String result = String.format("%04d", (Integer.parseInt(valorS) + 1) );

                // Set value and report transaction success
                mutableData.setValue(result);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d("OOPS", "postTransaction:onComplete:" + dataSnapshot.getValue(String.class));
                if (databaseError != null)
                {
                    hideProgressDialog();
                    Toast.makeText(RegistraInfracaoActivity.this, "Não foi possível salvar os dados da infração",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    salvarDadosInfracao(dataSnapshot.getValue(String.class),lat,lon);
                }

            }
        });
    }

    public void salvarDadosInfracao(String key, double latitude, double longitude)
    {
        Date now = new Date();

        // Salvar dados de infracao
        Infracao infracao = new Infracao();
        infracao.setTipo(String.format("%02d", tipoEscolhido ));
        infracao.setStatus("01"); // Nova Infração
        infracao.setData(dataRegistro);
        infracao.setHora(horaRegistro);
        infracao.setComentario(txtArea.getText().toString());
        infracao.setUid(mAuth.getCurrentUser().getUid());

        mDatabase.child("infracoes").child(key).setValue(infracao);

        //Salvar dados de detalhe_infracao
        InfracaoDetalhe infracaoDetalhe = new InfracaoDetalhe();
        infracaoDetalhe.setFoto("data:image/jpeg;base64,"+encoded_full);
        infracaoDetalhe.setFoto_mini("data:image/jpeg;base64,"+encoded_mini);
        infracaoDetalhe.setLatitude(latitude);
        infracaoDetalhe.setLongitude(longitude);
        infracaoDetalhe.setEndereco(endereco);

        mDatabase.child("detalhes_infracoes").child(key).setValue(infracaoDetalhe);

        Intent intent = new Intent(RegistraInfracaoActivity.this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        //hideProgressDialog();
        finish();
    }

    public void onTipoInfracaoClick(View v) // public void onLoginClick(View v)
    {
        if (frameTipoInfracao.getVisibility() == View.VISIBLE) frameTipoInfracao.setVisibility(View.GONE);
        else {
            frameTipoInfracao.setVisibility(View.VISIBLE);
        }
    }

    public void onEscolhaTipoInfracaoClick(View v)
    {
        frameTipoInfracao.setVisibility(View.GONE);
    }

}
