package br.com.trihum.oops;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegistraInfracaoActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    String encoded;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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

        // combo
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        //fim


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
        // Salvar dados de infracao
        Infracao infracao = new Infracao();
        infracao.setTipo("01");
        infracao.setStatus("01");
        infracao.setData("2017-01-06");
        infracao.setHora("02:00:00");
        infracao.setComentario("Infracao de teste");
        infracao.setUid(mAuth.getCurrentUser().getUid());

        String key = retornaContadorInfracao();
        mDatabase.child("infracoes").child(key).setValue(infracao);

        //Salvar dados de detalhe_infracao
        InfracaoDetalhe infracaoDetalhe = new InfracaoDetalhe();
        infracaoDetalhe.setFoto("data:image/jpeg;base64,"+encoded);
        infracaoDetalhe.setFoto_mini("data:image/jpeg;base64,"+encoded);
        infracaoDetalhe.setLatitude(-22.347823);
        infracaoDetalhe.setLongitude(-43.561298);

        mDatabase.child("detalhes_infracoes").child(key).setValue(infracaoDetalhe);
    }

    public String retornaContadorInfracao()
    {
        String key = mDatabase.child("infracoes").push().getKey();
        return key;
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Top 250");
        //listDataHeader.add("Now Showing");
        //listDataHeader.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        /*List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report"); */

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        //listDataChild.put(listDataHeader.get(1), nowShowing);
        //listDataChild.put(listDataHeader.get(2), comingSoon);
    }

}
