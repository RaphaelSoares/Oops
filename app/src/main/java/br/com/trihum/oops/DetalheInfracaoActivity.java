package br.com.trihum.oops;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
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
    boolean infracaoEhOffline;
    boolean imagemAmpliada;

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
        infracaoEhOffline = intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_OFFLINE).equals("1");
        infracaoSelecionada = new Infracao();
        infracaoSelecionada.setId(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_ID));
        infracaoSelecionada.setStatus(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS));
        infracaoSelecionada.setTipo(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO));
        infracaoSelecionada.setData(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_DATA));
        infracaoSelecionada.setHora(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_HORA));
        infracaoSelecionada.setEmail(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_EMAIL));
        infracaoSelecionada.setComentario(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_COMENTARIO));

        barraRegistroDetalheInfracao.setText(Globais.mapaTipos.get(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO)));

        if (infracaoSelecionada.getStatus().equals("00"))
        {
            btnSituacaoRegistro.setText("Registro Não enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
            //btnSituacaoInfracao.setText(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("01"))
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
            btnSituacaoInfracao.setText(Globais.mapaSituacoes.get(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS)));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("04"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoInfracao.setText(Globais.mapaSituacoes.get(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS)));
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_naovalidacao_registro_infracao_selector));
        }
        else if (infracaoSelecionada.getStatus().equals("05"))
        {
            btnSituacaoRegistro.setText("Registro Enviado");
            btnSituacaoInfracao.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoInfracao.setText("Infração Validada");
            btnSituacaoAcaoEducativa.setBackground(Funcoes.getDrawable(this,R.drawable.botao_validacao_registro_infracao_selector));
            btnSituacaoAcaoEducativa.setText(Globais.mapaSituacoes.get(intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS)));
        }

        registroDataInfracao.setText(Funcoes.dataDiaMesAno(infracaoSelecionada.getData()));
        registroComentarioInfracao.setText(infracaoSelecionada.getComentario());

        txtAreaSituacaoRegistro.setText("");
        txtAreaSituacaoInfracao.setText("");
        txtAreaSituacaoAcaoEducativa.setText("");

        if (!infracaoEhOffline)
        {
            mDatabase.child("detalhes_infracoes/"+infracaoSelecionada.getId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    InfracaoDetalhe infracaoDetalhe = dataSnapshot.getValue(InfracaoDetalhe.class);

                    if (infracaoDetalhe == null) return;

                    progressBarFoto.setVisibility(View.INVISIBLE);

                    registroEnderecoInfracao.setText(infracaoDetalhe.getEndereco());

                    String comentarioOrgao = (infracaoDetalhe.getComentario_orgao()!=null)?infracaoDetalhe.getComentario_orgao():"";
                    String msgOrgao = (infracaoDetalhe.getMsg_orgao()!=null)?infracaoDetalhe.getMsg_orgao():"";
                    String msgOrgao01 = (infracaoDetalhe.getMsg_orgao_01()!=null)?infracaoDetalhe.getMsg_orgao_01():"";
                    String msgOrgao02 = (infracaoDetalhe.getMsg_orgao_02()!=null)?infracaoDetalhe.getMsg_orgao_02():"";
                    String msgOrgao03 = (infracaoDetalhe.getMsg_orgao_03()!=null)?infracaoDetalhe.getMsg_orgao_03():"";
                    String msgOrgao04 = (infracaoDetalhe.getMsg_orgao_04()!=null)?infracaoDetalhe.getMsg_orgao_04():"";
                    String msgOrgao05 = (infracaoDetalhe.getMsg_orgao_05()!=null)?infracaoDetalhe.getMsg_orgao_05():"";

                    if (infracaoSelecionada.getStatus().equals("01"))
                    {
                        String conteudoTexto = (msgOrgao01.equals(""))?Globais.mensagemPadraoRegistroRecebido:"MENSAGEM: " + msgOrgao01 + "\n\n" + Globais.mensagemPadraoRegistroRecebido;
                        txtAreaSituacaoRegistro.setText(conteudoTexto);
                    }
                    if (infracaoSelecionada.getStatus().equals("02"))
                    {
                        String conteudoTexto = (msgOrgao02.equals(""))?comentarioOrgao:"MENSAGEM: " + msgOrgao02 + "\n\n" + comentarioOrgao;
                        txtAreaSituacaoRegistro.setText(conteudoTexto);
                    }
                    else if (infracaoSelecionada.getStatus().equals("03"))
                    {
                        String conteudoTexto = (msgOrgao03.equals(""))?comentarioOrgao:"MENSAGEM: " + msgOrgao03 + "\n\n" + comentarioOrgao;
                        txtAreaSituacaoInfracao.setText(conteudoTexto);
                    }
                    else if (infracaoSelecionada.getStatus().equals("04"))
                    {
                        String conteudoTexto = (msgOrgao04.equals(""))?comentarioOrgao:"MENSAGEM: " + msgOrgao04 + "\n\n" + comentarioOrgao;
                        txtAreaSituacaoInfracao.setText(conteudoTexto);
                    }
                    else
                    {
                        String conteudoTexto = (msgOrgao05.equals(""))?comentarioOrgao:"MENSAGEM: " + msgOrgao05 + "\n\n" + comentarioOrgao;
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
        else // Se é infração offline, a foto já está no objeto
        {
            progressBarFoto.setVisibility(View.INVISIBLE);
            registroEnderecoInfracao.setText("");

            String foto_offline = intent.getStringExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_FOTO_OFFLINE);
            if (foto_offline!=null && foto_offline.length()>0)
            {
                imageFotoInfracao.setImageBitmap(Funcoes.decodeFrom64(foto_offline));
            }

        }

        imagemAmpliada = false;
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

    public void onImagemClick(View v)
    {
        ViewGroup.LayoutParams lp = imageFotoInfracao.getLayoutParams();

        if (!imagemAmpliada)
        {
            lp.height = lp.height * 2;
        }
        else
        {
            lp.height = lp.height / 2;
        }
        imagemAmpliada = !imagemAmpliada;

        imageFotoInfracao.setLayoutParams(lp);

    }
}
