package br.com.trihum.oops;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.trihum.oops.model.Infracao;
import br.com.trihum.oops.model.InfracaoDetalhe;
import br.com.trihum.oops.utilities.Constantes;
import br.com.trihum.oops.utilities.Funcoes;
import br.com.trihum.oops.utilities.Globais;

public class DetalheInfracaoActivity extends BaseActivity {

    private DatabaseReference mDatabase;

    ImageView imageFotoInfracao;
    ProgressBar progressBarFoto;
    FrameLayout frameRegistroEnviado;
    FrameLayout frameInfracaoValidada;
    FrameLayout frameAcaoEducativa;
    TextView barraRegistroDetalheInfracao;
    TextView registroEnderecoInfracao;
    TextView registroDataInfracao;
    TextView registroComentarioInfracao;
    Button btnSituacaoRegistro;
    Button btnSituacaoInfracao;
    Button btnSituacaoAcaoEducativa;
    EditText txtAreaSituacaoRegistro;
    EditText txtAreaSituacaoInfracao;
    EditText txtAreaSituacaoAcaoEducativa;

    Infracao infracaoSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_infracao);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        imageFotoInfracao = (ImageView) findViewById(R.id.imageFotoInfracao);
        progressBarFoto = (ProgressBar) findViewById(R.id.progressBarFoto);
        barraRegistroDetalheInfracao = (TextView) findViewById(R.id.barraRegistroDetalheInfracao);
        registroEnderecoInfracao = (TextView) findViewById(R.id.registroEnderecoInfracao);
        registroDataInfracao = (TextView) findViewById(R.id.registroDataInfracao);
        registroComentarioInfracao = (TextView) findViewById(R.id.registroComentarioInfracao);
        btnSituacaoRegistro = (Button) findViewById(R.id.btnSituacaoRegistro);
        btnSituacaoInfracao = (Button) findViewById(R.id.btnSituacaoInfracao);
        btnSituacaoAcaoEducativa = (Button) findViewById(R.id.btnSituacaoAcaoEducativa);
        frameRegistroEnviado = (FrameLayout) findViewById(R.id.frame_registro_enviado);
        frameInfracaoValidada = (FrameLayout) findViewById(R.id.frame_infracao_validada);
        frameAcaoEducativa = (FrameLayout) findViewById(R.id.frame_acao_educativa);
        txtAreaSituacaoRegistro = (EditText) findViewById(R.id.txtAreaSituacaoRegistro);
        txtAreaSituacaoInfracao = (EditText) findViewById(R.id.txtAreaSituacaoInfracao);
        txtAreaSituacaoAcaoEducativa = (EditText) findViewById(R.id.txtAreaSituacaoAcaoEducativa);

        frameRegistroEnviado.setVisibility(View.GONE);
        frameInfracaoValidada.setVisibility(View.GONE);
        frameAcaoEducativa.setVisibility(View.GONE);

        //****************************************
        // Objetos Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //****************************************
        Intent intent = this.getIntent();
        infracaoSelecionada = new Infracao();
        infracaoSelecionada.setId(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_ID));
        infracaoSelecionada.setStatus(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS));
        infracaoSelecionada.setTipo(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO));
        infracaoSelecionada.setData(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_DATA));
        infracaoSelecionada.setHora(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_HORA));
        //infracaoSelecionada.setUid(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_UID));
        infracaoSelecionada.setEmail(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_EMAIL));
        infracaoSelecionada.setComentario(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_COMENTARIO));

        barraRegistroDetalheInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO_TEXTO));

        if (infracaoSelecionada.getStatus().equals("01"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
            //btnSituacaoInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("02"))
        {
            btnSituacaoRegistro.setText("Registro em análise");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
            //btnSituacaoInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("03"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("04"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("05"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoInfracao.setText("Infração Validada");
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoAcaoEducativa.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
        }

        registroDataInfracao.setText(Funcoes.dataDiaMesAno(infracaoSelecionada.getData()));
        registroComentarioInfracao.setText(infracaoSelecionada.getComentario());

        txtAreaSituacaoRegistro.setText("");
        txtAreaSituacaoInfracao.setText("");
        txtAreaSituacaoAcaoEducativa.setText("");

        mDatabase.child("detalhes_infracoes/"+infracaoSelecionada.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                InfracaoDetalhe infracaoDetalhe = dataSnapshot.getValue(InfracaoDetalhe.class);

                if (infracaoDetalhe == null) return;

                progressBarFoto.setVisibility(View.INVISIBLE);

                registroEnderecoInfracao.setText(infracaoDetalhe.getEndereco());

                String comentarioOrgao = (infracaoDetalhe.getComentario_orgao()!=null)?infracaoDetalhe.getComentario_orgao():"";
                String msgOrgao = (infracaoDetalhe.getMsg_orgao()!=null)?infracaoDetalhe.getMsg_orgao():"";
                String conteudoTexto = (msgOrgao.equals(""))?comentarioOrgao:"MENSAGEM: " + msgOrgao + "\n\n" + comentarioOrgao;

                if (infracaoSelecionada.getStatus().equals("01"))
                {
                    txtAreaSituacaoRegistro.setText(Globais.mensagemPadraoRegistroRecebido);
                }
                if (infracaoSelecionada.getStatus().equals("02"))
                {
                    txtAreaSituacaoRegistro.setText(conteudoTexto);
                }
                else if (infracaoSelecionada.getStatus().equals("03") || infracaoSelecionada.getStatus().equals("04"))
                {
                    txtAreaSituacaoInfracao.setText(conteudoTexto);
                }
                else
                {
                    txtAreaSituacaoAcaoEducativa.setText(conteudoTexto);
                }

                if (infracaoDetalhe.getFoto()!=null && infracaoDetalhe.getFoto().length()>0)
                {
                    imageFotoInfracao.setImageBitmap(Funcoes.decodeFrom64(infracaoDetalhe.getFoto()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume()
    {
        super.onResume();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void onDetalheRegistroEnviadoClick(View v)
    {
        if (frameRegistroEnviado.getVisibility() == View.VISIBLE) frameRegistroEnviado.setVisibility(View.GONE);
        else {
            if (!txtAreaSituacaoRegistro.getText().toString().equals(""))
                frameRegistroEnviado.setVisibility(View.VISIBLE);
        }
    }

    public void onDetalheInfracaoValidadaClick(View v)
    {
        if (frameInfracaoValidada.getVisibility() == View.VISIBLE) frameInfracaoValidada.setVisibility(View.GONE);
        else {
            if (!txtAreaSituacaoInfracao.getText().toString().equals(""))
                frameInfracaoValidada.setVisibility(View.VISIBLE);
        }
    }

    public void onDetalheInfracaoAcaoEducativaClick(View v)
    {
        if (frameAcaoEducativa.getVisibility() == View.VISIBLE) frameAcaoEducativa.setVisibility(View.GONE);
        else {
            if (!txtAreaSituacaoAcaoEducativa.getText().toString().equals(""))
                frameAcaoEducativa.setVisibility(View.VISIBLE);
        }
    }

    /*public void colocaTextoAjustaAltura(TextView tv, String texto)
    {
        tv.setText(texto);
        int height_in_pixels = tv.getLineCount() * tv.getLineHeight(); //approx height text
        height_in_pixels = (int) (height_in_pixels * getResources().getDisplayMetrics().density);

        // Ajusta a altura do text view
        tv.setHeight(height_in_pixels);

        // Se o ajuste de altura acima não funcionar, tente assim:
        //ViewGroup.LayoutParams params = tv.getLayoutParams();
        //params.height = height_in_pixels;
        //tv.setLayoutParams(params);
    }*/
}
