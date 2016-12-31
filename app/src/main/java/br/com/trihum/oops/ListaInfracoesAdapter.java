package br.com.trihum.oops;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by raphaelmoraes on 19/12/16.
 */

public class ListaInfracoesAdapter extends BaseAdapter {

    LayoutInflater inflater;
    public List<Infracao> arrayInfracoes;

    public ListaInfracoesAdapter(LayoutInflater inflater)
    {
        this.inflater = inflater;
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

        final Infracao infracao = arrayInfracoes.get(posicao);

        TextView textoStatusInfracao = (TextView)convertView.findViewById(R.id.statusInfracao);
        TextView textoTipoInfracao = (TextView)convertView.findViewById(R.id.tipoInfracao);
        TextView textoEnderecoInfracao = (TextView)convertView.findViewById(R.id.enderecoInfracao);
        TextView textoDataInfracao = (TextView)convertView.findViewById(R.id.dataInfracao);
        ImageView imageFotoInfracao = (ImageView) convertView.findViewById(R.id.fotoInfracao);

        textoStatusInfracao.setText(infracao.status);
        textoTipoInfracao.setText(infracao.tipo);
        textoEnderecoInfracao.setText(infracao.endereco);
        textoDataInfracao.setText(infracao.data);
        imageFotoInfracao.setImageBitmap(Constantes.decodeFrom64toRound(infracao.foto));

        return convertView;
    }
}
