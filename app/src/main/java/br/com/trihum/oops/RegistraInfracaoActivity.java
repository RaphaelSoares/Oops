package br.com.trihum.oops;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
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
import android.widget.RadioButton;
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
import br.com.trihum.oops.utilities.GPSMonitor;
import br.com.trihum.oops.utilities.GPSTracker;
import br.com.trihum.oops.utilities.Globais;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegistraInfracaoActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private GPSMonitor gps;
    private double latitude;
    private double longitude;
    public String endereco;
    public String localidade;
    private String dataRegistro;
    private String horaRegistro;

    private TextView registroCoordenadasInfracao;
    private TextView registroEnderecoInfracao;
    private TextView registroDataInfracao;
    private FrameLayout frameTipoInfracao;
    private Button btnTipoInfracao;
    private FloatingActionButton fabConfirmaRegistroInfracao;
    private RadioGroup rgGrupoRegistro1;
    private RadioButton rb1Gr1Registro;
    private RadioButton rb2Gr1Registro;
    private RadioButton rb3Gr1Registro;
    private RadioButton rb4Gr1Registro;
    private RadioButton rb5Gr1Registro;
    private RadioButton rb6Gr1Registro;
    private RadioButton rb7Gr1Registro;
    private RadioButton rb8Gr1Registro;
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
        registroCoordenadasInfracao = (TextView) findViewById(R.id.registroCoordenadasInfracao);
        registroEnderecoInfracao = (TextView) findViewById(R.id.registroEnderecoInfracao);
        registroDataInfracao = (TextView) findViewById(R.id.registroDataInfracao);
        frameTipoInfracao = (FrameLayout) findViewById(R.id.frame_radio_tipo_infracao);
        btnTipoInfracao = (Button) findViewById(R.id.btnTipoInfracao);
        fabConfirmaRegistroInfracao = (FloatingActionButton) findViewById(R.id.fabConfirmaRegistroInfracao);
        rgGrupoRegistro1 = (RadioGroup) findViewById(R.id.rgGrupoRegistro1);
        txtArea = (EditText) findViewById(R.id.txtArea);
        rb1Gr1Registro = (RadioButton) findViewById(R.id.rb1Gr1Registro);
        rb2Gr1Registro = (RadioButton) findViewById(R.id.rb2Gr1Registro);
        rb3Gr1Registro = (RadioButton) findViewById(R.id.rb3Gr1Registro);
        rb4Gr1Registro = (RadioButton) findViewById(R.id.rb4Gr1Registro);
        rb5Gr1Registro = (RadioButton) findViewById(R.id.rb5Gr1Registro);
        rb6Gr1Registro = (RadioButton) findViewById(R.id.rb6Gr1Registro);
        rb7Gr1Registro = (RadioButton) findViewById(R.id.rb7Gr1Registro);
        rb8Gr1Registro = (RadioButton) findViewById(R.id.rb8Gr1Registro);

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

        gps = new GPSMonitor(this);
        gps.getLocation();
    }

    public void obteveCoordenadas()
    {
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        geocodeTask = new GeocodeTask(this,latitude,longitude);
        geocodeTask.execute("");
    }

    public void informaCoordenadas(Location location, String provider)
    {
        registroCoordenadasInfracao.setText("Lat. "+location.getLatitude()+", Long. "+location.getLongitude()+"\n(precisão : "+location.getAccuracy()+"m, "+provider+")");
        registroEnderecoInfracao.setTextColor(ContextCompat.getColor(this, R.color.corTextoRegistroInfracao));
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
    protected void onPause() {
        // Make sure that when the activity goes to
        // background, the device stops getting locations
        // to save battery life.
        if (gps!=null)
        {
            gps.stopUsingGPS();
        }
        super.onPause();
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
        registroEnderecoInfracao.setTextColor(ContextCompat.getColor(this, R.color.corTextoDestaqueRegistroInfracao));
        //Log.d("OOPS","endereco atualizado = "+endereco);
    }

    public int obterTipoEscolhido()
    {
        // Verificando se escolheu o tipo de infração
        int checkedRadioButtonId = rgGrupoRegistro1.getCheckedRadioButtonId();
        if (checkedRadioButtonId == -1) {
            return -1;
        }
        else{
            if (checkedRadioButtonId == R.id.rb1Gr1Registro) return 1;
            else if (checkedRadioButtonId == R.id.rb2Gr1Registro) return 2;
            else if (checkedRadioButtonId == R.id.rb3Gr1Registro) return 3;
            else if (checkedRadioButtonId == R.id.rb4Gr1Registro) return 4;
            else if (checkedRadioButtonId == R.id.rb5Gr1Registro) return 5;
            else if (checkedRadioButtonId == R.id.rb6Gr1Registro) return 6;
            else if (checkedRadioButtonId == R.id.rb7Gr1Registro) return 7;
            else if (checkedRadioButtonId == R.id.rb8Gr1Registro) return 8;
        }

        return -1;
    }

    public void onSalvarInfracaoClick (View v)
    {
        tipoEscolhido = obterTipoEscolhido();
        if (tipoEscolhido == -1)
        {
            Toast.makeText(RegistraInfracaoActivity.this, "Escolha o tipo de infração",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (Globais.conectado)
        {
            if (latitude==0 && longitude==0)
            {
                Toast.makeText(RegistraInfracaoActivity.this, "As coordenadas ainda não foram obtidas. Favor aguardar.",
                        Toast.LENGTH_SHORT).show();
                /*if(gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                }*/
            }
            else if (geocodeTask==null || geocodeTask.isRunning)
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Obtendo o endereço. Favor aguardar.",
                        Toast.LENGTH_SHORT).show();
            }
            else if (!geocodeTask.isRunning && (endereco==null || endereco.equals("")))
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Não foi possível obter o endereço. Tentando novamente. Favor aguardar.",
                        Toast.LENGTH_SHORT).show();
                //geocodeTask = new GeocodeTask(this,latitude,longitude);
                geocodeTask.setCoordenadas(latitude,longitude);
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
            if (latitude==0 && longitude==0)
            {
                Toast.makeText(RegistraInfracaoActivity.this, "As coordenadas ainda não foram obtidas. Favor aguardar.",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(RegistraInfracaoActivity.this, "Sem conexão à Internet no momento. A infração será armazenada e posteriormente enviada.",
                        Toast.LENGTH_LONG).show();
                salvarDadosInfracaoOffline(latitude,longitude);
            }

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

    public void onEscolheTipoInfracaoClick(View v)
    {
        frameTipoInfracao.setVisibility(View.GONE);

        int tipo = obterTipoEscolhido();

        if (tipo!=-1)
        {
            if (tipo==1) btnTipoInfracao.setText(rb1Gr1Registro.getText());
            if (tipo==2) btnTipoInfracao.setText(rb2Gr1Registro.getText());
            if (tipo==3) btnTipoInfracao.setText(rb3Gr1Registro.getText());
            if (tipo==4) btnTipoInfracao.setText(rb4Gr1Registro.getText());
            if (tipo==5) btnTipoInfracao.setText(rb5Gr1Registro.getText());
            if (tipo==6) btnTipoInfracao.setText(rb6Gr1Registro.getText());
            if (tipo==7) btnTipoInfracao.setText(rb7Gr1Registro.getText());
            if (tipo==8) btnTipoInfracao.setText(rb8Gr1Registro.getText());
        }

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
