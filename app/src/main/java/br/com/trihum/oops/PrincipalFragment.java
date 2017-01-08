package br.com.trihum.oops;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrincipalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrincipalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrincipalFragment extends Fragment {

    private TabHost tabHost;
    private FloatingActionButton fabTirarFoto;
    private ListView listaInfracoes;
    public ListaInfracoesAdapter adapter;
    public List<Infracao> arrayInfracoes;
    public HashMap<String, String> mapaTipos;
    public HashMap<String, String> mapaSituacoes;
    public FrameLayout frameAlteraSenha;
    public Button btnAlterarSenha;
    private FloatingActionButton fabConfirmaEnvioSenha;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PrincipalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrincipalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrincipalFragment newInstance(String param1, String param2) {
        PrincipalFragment fragment = new PrincipalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_principal, container, false);

        //*************************************************
        // Componentes da tela
        listaInfracoes = (ListView)view.findViewById(R.id.listaInfracoes);
        fabTirarFoto = (FloatingActionButton) view.findViewById(R.id.fabTirarFoto);
        tabHost = (TabHost)view.findViewById(R.id.tab_host_principal);
        frameAlteraSenha = (FrameLayout)view.findViewById(R.id.frame_altera_senha);
        btnAlterarSenha = (Button)view.findViewById(R.id.btnAlterarSenha);
        fabConfirmaEnvioSenha = (FloatingActionButton) view.findViewById(R.id.fabConfirmaEnvioSenha);

        fabTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getContext(), FotoActivity.class);
                startActivity(i);
                //Log.d("TESTE","(Fragment) Tira foto!");

            }
        });

        btnAlterarSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAlterarSenha.setVisibility(View.INVISIBLE);
                frameAlteraSenha.setVisibility(View.VISIBLE);
            }
        });

        fabConfirmaEnvioSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAlterarSenha.setVisibility(View.VISIBLE);
                frameAlteraSenha.setVisibility(View.INVISIBLE);
            }
        });


        tabHost.setup();
        this.setNewTab(view.getContext(), tabHost, "lista", R.string.tab_title_1, R.drawable.ic_aba_lista, R.id.tab1);
        this.setNewTab(view.getContext(), tabHost, "metricas", R.string.tab_title_2, R.drawable.ic_aba_metricas, R.id.tab2);
        this.setNewTab(view.getContext(), tabHost, "perfil", R.string.tab_title_3, R.drawable.ic_aba_perfil, R.id.tab3);


        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //****************************************
        // Consulta a lista de tipos e situacoes
        consultaListaTipos();
        consultaListaSituacoes();
        //****************************************

        adapter = new ListaInfracoesAdapter(inflater, mapaTipos, mapaSituacoes);
        listaInfracoes.setAdapter(adapter);

        listaInfracoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int posicao,
                                    long id) {

                Infracao infracaoSelecionada = (Infracao) adapter.getItemAtPosition(posicao);

                /*Intent intent = new Intent(ListaOfertasActivity.this, OfertaDetalheActivity.class);
                intent.putExtra(Constantes.INTENT_PARAM_OFERTA_SELECIONADA, ofertaSelecionada);
                intent.putExtra(Constantes.INTENT_PARAM_ID_USUARIO, idUsuario);
                startActivity(intent);*/

                Log.d("OOPS","selecionei = "+infracaoSelecionada.getId());

            }
        });

        //****************************************
        // Consulta a lista de infracoes
        atualizaListaInfracoes();
        //********************************************************

        return view;
    }

    private void setNewTab(Context context, TabHost tabHost, String tag, int title, int icon, int contentID ){
        TabHost.TabSpec tabSpec = tabHost.newTabSpec(tag);
        tabSpec.setIndicator(getTabIndicator(tabHost.getContext(), title, icon)); // new function to inject our own tab layout
        tabSpec.setContent(contentID);
        tabHost.addTab(tabSpec);
    }

    private View getTabIndicator(Context context, int title, int icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        ImageView iv = (ImageView) view.findViewById(R.id.imageView);
        iv.setImageResource(icon);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }*/

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void consultaListaTipos()
    {
        mapaTipos=new HashMap<String, String>();

        mDatabase.child("tipos_infracao").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    mapaTipos.put(postSnapshot.getKey(),postSnapshot.child("tipo").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void consultaListaSituacoes()
    {
        mapaSituacoes=new HashMap<String, String>();

        mDatabase.child("situacoes_app").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    mapaSituacoes.put(postSnapshot.getKey(),postSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void atualizaListaInfracoes()
    {
        //addValueEventListener

        arrayInfracoes = new ArrayList<Infracao>();

        // Ver o video em para tentar resolver o problema...
        // https://www.youtube.com/watch?v=30RJYT9tctc
        mDatabase.child("infracoes").orderByChild("uid").
                equalTo(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Infracao infracao = dataSnapshot.getValue(Infracao.class);
                infracao.setId(dataSnapshot.getKey());
                arrayInfracoes.add(infracao);
                adapter.arrayInfracoes = arrayInfracoes;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Infracao infracao = dataSnapshot.getValue(Infracao.class);

                Iterator<Infracao> iterator = arrayInfracoes.iterator();

                while (iterator.hasNext()) {
                    Infracao infracao1 = iterator.next();
                    if (infracao1.getId().equals(dataSnapshot.getKey()))
                    {
                        infracao1.copia(infracao);
                        adapter.arrayInfracoes = arrayInfracoes;
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
