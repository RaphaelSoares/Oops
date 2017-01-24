package br.com.trihum.oops;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.trihum.oops.fragment.PrincipalFragment;
import br.com.trihum.oops.model.Infracao;
import br.com.trihum.oops.model.InfracaoComDetalhe;
import br.com.trihum.oops.model.InfracaoDetalhe;
import br.com.trihum.oops.utilities.Constantes;
import br.com.trihum.oops.utilities.GPSTracker;
import br.com.trihum.oops.utilities.Globais;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegistraInfracaoActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private GPSTracker gps;
    private double latitude;
    private double longitude;
    public String endereco;
    public String localidade;
    private String dataRegistro;
    private String horaRegistro;

    private TextView registroEnderecoInfracao;
    private TextView registroDataInfracao;
    private FrameLayout frameTipoInfracao;
    private Button btnTipoInfracao;
    private FloatingActionButton fabConfirmaRegistroInfracao;
    private RadioGroup rgGrupoRegistro1;
    private EditText txtArea;
    private int tipoEscolhido;

    private String encoded_full;
    private String encoded_mini;

    private GeocodeTask geocodeTask;

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
        registroEnderecoInfracao.setText("");

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

        Date now = new Date();
        dataRegistro = new SimpleDateFormat("yyyy-MM-dd").format(now);
        horaRegistro = new SimpleDateFormat("HH:mm:ss").format(now);
        registroDataInfracao.setText(new SimpleDateFormat("dd/MM/yyyy").format(now));


        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //****************************************
        // Monitora a conexão
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Globais.conectado = true;
                    Log.d("OOPS","connected");
                } else {
                    Globais.conectado = false;
                    Log.d("OOPS","not connected");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //System.err.println("Listener was cancelled");
            }
        });


        latitude = 0;
        longitude = 0;
        endereco = "";
        localidade = "";
        // Verificando o GPS e se consegue obter as coordenadas
        gps = new GPSTracker(RegistraInfracaoActivity.this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            geocodeTask = new GeocodeTask(this,latitude,longitude);
            geocodeTask.execute("");

            /*Geocoder geoCoder = new Geocoder(this);
            try {
                // Esta linha aqui pode dar throw exception por timeout
                // ver alternativa em:
                // http://stackoverflow.com/questions/23638067/geocoder-getfromlocation-function-throws-timed-out-waiting-for-server-response
                List<Address> matches = geoCoder.getFromLocation(latitude, longitude, 1);
                Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
                endereco = bestMatch.getAddressLine(0)+" "+bestMatch.getLocality()+" "+bestMatch.getAdminArea();
                registroEnderecoInfracao.setText(endereco);

                atualizaInfoEndereco(endereco);
            }
            catch (IOException e) {
                e.printStackTrace();
            }*/

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

    public void atualizaInfoEndereco(String endereco, String localidade)
    {
        this.endereco = endereco;
        this.localidade = localidade;
        registroEnderecoInfracao.setText(endereco);
        Log.d("OOPS","endereco atualizado = "+endereco);
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

        if (Globais.conectado)
        {
            if (geocodeTask.isRunning)
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Aguarde que o endereço está sendo obtido.",
                        Toast.LENGTH_SHORT).show();
            }
            else if (latitude==0 && longitude==0)
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Não foi possível obter as coordenadas do local da infração. Por favor, tente novamente.",
                        Toast.LENGTH_SHORT).show();
                if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }
            }
            else if (!geocodeTask.isRunning && (endereco==null || endereco.equals("")))
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Não foi possível obter o endereço. Por favor, tente novamente.",
                        Toast.LENGTH_SHORT).show();
                geocodeTask = new GeocodeTask(this,latitude,longitude);
                geocodeTask.execute("");
            }
            else
            {
                showProgressDialog();
                obtemChaveESalvaDadosInfracao(latitude,longitude);
            }
        }
        else
        {
            Toast.makeText(RegistraInfracaoActivity.this, "Sem conexão à Internet no momento. A infração será armazenada e posteriormente enviada.",
                    Toast.LENGTH_LONG).show();
            salvarDadosInfracaoOffline(latitude,longitude);
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
        infracao.setEmail(Globais.emailLogado);
        infracao.setOrgao(PrincipalFragment.obterOrgaoPorLocalidade(localidade));

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

    public void salvarDadosInfracaoOffline(double latitude, double longitude)
    {
        Date now = new Date();

        // Salvar dados de infracao
        InfracaoComDetalhe infracao = new InfracaoComDetalhe();
        infracao.setTipo(String.format("%02d", tipoEscolhido ));
        infracao.setStatus("00"); // Infração não enviada
        infracao.setData(dataRegistro);
        infracao.setHora(horaRegistro);
        infracao.setComentario(txtArea.getText().toString());
        infracao.setEmail(Globais.emailLogado);
        infracao.setId("0");

        //Salvar dados de detalhe_infracao
        infracao.setFoto("data:image/jpeg;base64,"+encoded_full);
        infracao.setFoto_mini("data:image/jpeg;base64,"+encoded_mini);
        infracao.setLatitude(latitude);
        infracao.setLongitude(longitude);
        infracao.setEndereco(endereco);

        //gravar no arrayInfracoes
        PrincipalFragment.arrayInfracoes.add(infracao);
        PrincipalFragment.notificaAtualizacaoArray();

        /*Intent intent = new Intent(RegistraInfracaoActivity.this, PrincipalActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/

        //hideProgressDialog();
        finish();

    }

    public void onTipoInfracaoClick(View v) // public void onLoginClick(View v)
    {
        if (frameTipoInfracao.getVisibility() == View.VISIBLE) frameTipoInfracao.setVisibility(View.GONE);
        else {
            frameTipoInfracao.setVisibility(View.VISIBLE);
        }
        recolheTeclado();
    }

    public void onEscolhaTipoInfracaoClick(View v)
    {
        frameTipoInfracao.setVisibility(View.GONE);
    }

    public void recolheTeclado()
    {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
