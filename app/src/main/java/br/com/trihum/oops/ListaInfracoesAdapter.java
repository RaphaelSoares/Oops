package br.com.trihum.oops;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

/**
 * Created by raphaelmoraes on 19/12/16.
 */

public class ListaInfracoesAdapter extends BaseAdapter {

    LayoutInflater inflater;
    public List<Infracao> arrayInfracoes;
    private DatabaseReference mDatabase;
    HashMap<String, String> mapaTipos;
    HashMap<String, String> mapaSituacoes;

    public ListaInfracoesAdapter(LayoutInflater inflater, HashMap<String, String> mapaTipos, HashMap<String, String> mapaSituacoes)
    {
        this.inflater = inflater;
        this.mapaTipos = mapaTipos;
        this.mapaSituacoes = mapaSituacoes;
        //****************************************
        // Objetos Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public int getCount() {
        if (arrayInfracoes == null) return 0;
        return arrayInfracoes.size();
    }

    @Override
    public Object getItem(int posicao) {
        return arrayInfracoes.get(posicao);
    }

    @Override
    public long getItemId(int posicao) {
        return posicao;
    }

    @Override
    public View getView(int posicao, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.adapter_lista_infracoes, null);
        Context context = parent.getContext();

        final Infracao infracao = arrayInfracoes.get(posicao);

        TextView textoStatusInfracao = (TextView)convertView.findViewById(R.id.statusInfracao);
        TextView textoTipoInfracao = (TextView)convertView.findViewById(R.id.tipoInfracao);
        TextView textoDataInfracao = (TextView)convertView.findViewById(R.id.dataInfracao);
        final TextView textoEnderecoInfracao = (TextView)convertView.findViewById(R.id.enderecoInfracao);
        final ImageView imageFotoInfracao = (ImageView) convertView.findViewById(R.id.fotoInfracao);

        textoStatusInfracao.setText(mapaSituacoes.get(infracao.getStatus()));
        if (infracao.getStatus().equals("01") || infracao.getStatus().equals("02"))
        {
            textoStatusInfracao.setBackgroundResource(R.drawable.titulo_lista_situacao_1);
            textoStatusInfracao.setTextColor(ContextCompat.getColor(context, R.color.corVerdeTitulo));
        }
        else if (infracao.getStatus().equals("03"))
        {
            textoStatusInfracao.setBackgroundResource(R.drawable.titulo_lista_situacao_3);
            textoStatusInfracao.setTextColor(ContextCompat.getColor(context, R.color.corVerdeTitulo));
        }
        else if (infracao.getStatus().equals("04"))
        {
            textoStatusInfracao.setBackgroundResource(R.drawable.titulo_lista_situacao_2);
            textoStatusInfracao.setTextColor(ContextCompat.getColor(context, R.color.corLaranjaTitulo));
        }
        else if (infracao.getStatus().equals("05"))
        {
            textoStatusInfracao.setBackgroundResource(R.drawable.titulo_lista_situacao_4);
            textoStatusInfracao.setTextColor(ContextCompat.getColor(context, R.color.corVerdeTitulo));
        }

        textoTipoInfracao.setText(mapaTipos.get(infracao.getTipo()));
        textoDataInfracao.setText(Constantes.dataDiaMesAno(infracao.getData()));

        mDatabase.child("detalhes_infracoes/"+infracao.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                InfracaoDetalhe infracaoDetalhe = dataSnapshot.getValue(InfracaoDetalhe.class);

                textoEnderecoInfracao.setText(infracaoDetalhe.getEndereco());

                if (infracaoDetalhe.getFoto_mini()!=null && infracaoDetalhe.getFoto_mini().length()>0)
                {
                    imageFotoInfracao.setImageBitmap(Constantes.decodeFrom64toRound(infracaoDetalhe.getFoto_mini()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return convertView;
    }
}
