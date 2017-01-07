package br.com.trihum.oops;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegistraInfracaoActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    FrameLayout frameTipoInfracao;
    Button btnTipoInfracao;
    FloatingActionButton fabConfirmaRegistroInfracao;

    String encoded;

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

        frameTipoInfracao = (FrameLayout) findViewById(R.id.frame_radio_tipo_infracao);
        btnTipoInfracao = (Button) findViewById(R.id.btnTipoInfracao);
        fabConfirmaRegistroInfracao = (FloatingActionButton) findViewById(R.id.fabConfirmaRegistroInfracao);

        frameTipoInfracao.setVisibility(View.GONE);

        if(getIntent().hasExtra("byteArray")) {
            byte[] arrayBytesFoto = getIntent().getByteArrayExtra("byteArray");

            Bitmap bitmap = BitmapFactory.decodeByteArray(arrayBytesFoto, 0, arrayBytesFoto.length);
            foto.setImageBitmap(bitmap);

            encoded = Base64.encodeToString(arrayBytesFoto, Base64.DEFAULT);
        }

        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
        showProgressDialog();
        obtemChaveESalvaDadosInfracao();
    }

    public void obtemChaveESalvaDadosInfracao() {

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

                salvarDadosInfracao(dataSnapshot.getValue(String.class));
            }
        });
    }

    public void salvarDadosInfracao(String key)
    {
        // Salvar dados de infracao
        Infracao infracao = new Infracao();
        infracao.setTipo("01");
        infracao.setStatus("01");
        infracao.setData("2017-01-06");
        infracao.setHora("02:00:00");
        infracao.setComentario("Infracao de teste");
        infracao.setUid(mAuth.getCurrentUser().getUid());

        mDatabase.child("infracoes").child(key).setValue(infracao);

        //Salvar dados de detalhe_infracao
        InfracaoDetalhe infracaoDetalhe = new InfracaoDetalhe();
        infracaoDetalhe.setFoto("data:image/jpeg;base64,"+encoded);
        infracaoDetalhe.setFoto_mini("data:image/jpeg;base64,"+encoded);
        infracaoDetalhe.setLatitude(-22.347823);
        infracaoDetalhe.setLongitude(-43.561298);

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
        else frameTipoInfracao.setVisibility(View.VISIBLE);
    }

    public void onEscolhaTipoInfracaoClick(View v)
    {
        frameTipoInfracao.setVisibility(View.INVISIBLE);
    }

}
