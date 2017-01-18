package br.com.trihum.oops.fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import br.com.trihum.oops.DetalheInfracaoActivity;
import br.com.trihum.oops.FotoActivity;
import br.com.trihum.oops.R;
import br.com.trihum.oops.model.InfracaoComDetalhe;
import br.com.trihum.oops.utilities.Globais;
import br.com.trihum.oops.utilities.Utility;
import br.com.trihum.oops.adapter.ListaInfracoesAdapter;
import br.com.trihum.oops.model.Infracao;
import br.com.trihum.oops.utilities.Constantes;
import br.com.trihum.oops.utilities.DownloadImageTask;
import br.com.trihum.oops.utilities.Funcoes;


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
    public static ListaInfracoesAdapter adapter;
    public static List<Object> arrayInfracoes;
    public HashMap<String, String> mapaTipos;
    public HashMap<String, String> mapaSituacoes;
    public FrameLayout frameAlteraSenha;
    public Button btnAlterarSenha;
    private FloatingActionButton fabConfirmaEnvioSenha;
    public ImageButton btnEditarFoto;
    public ImageButton btnEditarNome;
    public TextView perfilNomeCompleto;
    public TextView perfilEmail;
    public ImageView perfilFoto;
    public EditText perfilSenhaAtual;
    public EditText perfilNovaSenha;
    public EditText perfilSenhaRepete;

    public TextView circulo1;
    public TextView circulo2;
    public TextView circulo3;
    public TextView circulo4;
    public TextView circulo5;

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
        btnEditarFoto = (ImageButton)view.findViewById(R.id.btnEditarFoto);
        btnEditarNome = (ImageButton)view.findViewById(R.id.btnEditarNome);
        perfilSenhaAtual = (EditText)view.findViewById(R.id.perfilSenhaAtual);
        perfilNovaSenha = (EditText)view.findViewById(R.id.perfilNovaSenha);
        perfilSenhaRepete = (EditText)view.findViewById(R.id.perfilSenhaRepete);

        circulo1 = (TextView) view.findViewById(R.id.circulo1);
        circulo2 = (TextView) view.findViewById(R.id.circulo2);
        circulo3 = (TextView) view.findViewById(R.id.circulo3);
        circulo4 = (TextView) view.findViewById(R.id.circulo4);
        circulo5 = (TextView) view.findViewById(R.id.circulo5);
        circulo1.setText("0");
        circulo2.setText("0");
        circulo3.setText("0");
        circulo4.setText("0");
        circulo5.setText("0");

        //*************************************************
        // Assegura que os campos de editar senha começam ocultos
        frameAlteraSenha.setVisibility(View.INVISIBLE);

        //*************************************************
        // Exibe ou nao os botões para edição do perfil de acordo com o tipo de login
        if (Globais.tipoLogin == Constantes.TIPO_LOGIN_COMUM)
        {
            btnEditarFoto.setVisibility(View.VISIBLE);
            btnEditarNome.setVisibility(View.VISIBLE);
            btnAlterarSenha.setVisibility(View.VISIBLE);
        }
        else
        {
            btnEditarFoto.setVisibility(View.INVISIBLE);
            btnEditarNome.setVisibility(View.INVISIBLE);
            btnAlterarSenha.setVisibility(View.INVISIBLE);
        }

        //*************************************************
        // Listener para os botoes
        fabTirarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), FotoActivity.class);
                startActivity(i);
            }
        });

        btnAlterarSenha.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                perfilSenhaAtual.setText("");
                perfilNovaSenha.setText("");
                perfilSenhaRepete.setText("");

                btnAlterarSenha.setVisibility(View.INVISIBLE);
                frameAlteraSenha.setVisibility(View.VISIBLE);
            }
        });

        fabConfirmaEnvioSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String senhaAtual = perfilSenhaAtual.getText().toString();
                String senhaNova = perfilNovaSenha.getText().toString();
                String senhaRepete = perfilSenhaRepete.getText().toString();

                if (senhaAtual.equals("") && senhaNova.equals("") && senhaRepete.equals(""))
                {
                    btnAlterarSenha.setVisibility(View.VISIBLE);
                    frameAlteraSenha.setVisibility(View.INVISIBLE);
                    return;
                }

                if (senhaAtual.equals("") || senhaNova.equals("") || senhaRepete.equals(""))
                {
                    Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!senhaNova.equals(senhaRepete))
                {
                    Toast.makeText(getContext(), "As Senhas informadas estão diferentes", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (senhaNova.length()<6)
                {
                    Toast.makeText(getContext(), "A Senha informada precisa ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.getCurrentUser().updatePassword(senhaNova)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Senha atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "Não foi possível atualizar a senha", Toast.LENGTH_SHORT).show();
                                }

                                btnAlterarSenha.setVisibility(View.VISIBLE);
                                frameAlteraSenha.setVisibility(View.INVISIBLE);
                            }
                        });

            }
        });

        btnEditarFoto.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnEditarNome.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abreCaixaEdicao();
            }
        });

        //*************************************************
        // Configura o tabhost
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

                Object obj = adapter.getItemAtPosition(posicao);
                if (obj instanceof Infracao)
                {
                    Infracao infracaoSelecionada = (Infracao) obj;

                    Log.d("OOPS","selecionei = "+infracaoSelecionada.getId());

                    Intent intent =  new Intent(getContext(), DetalheInfracaoActivity.class);
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_ID, infracaoSelecionada.getId());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS, infracaoSelecionada.getStatus());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_STATUS_TEXTO, mapaSituacoes.get(infracaoSelecionada.getStatus()));
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO, infracaoSelecionada.getTipo());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_TIPO_TEXTO, mapaTipos.get(infracaoSelecionada.getTipo()));
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_DATA, infracaoSelecionada.getData());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_HORA, infracaoSelecionada.getHora());
                    //intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_UID, infracaoSelecionada.getUid());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_EMAIL, infracaoSelecionada.getEmail());
                    intent.putExtra(Constantes.INTENT_PARAM_INFRACAO_SELECIONADA_COMENTARIO, infracaoSelecionada.getComentario());
                    startActivity(intent);
                }

            }
        });

        //****************************************
        // Consulta a lista de infracoes
        atualizaListaInfracoes();
        //********************************************************

        //********************************************************
        // Pegando componentes do navigator e atribuindo os dados do perfil
        perfilFoto = (ImageView) view.findViewById(R.id.perfilFoto);
        perfilNomeCompleto = (TextView) view.findViewById(R.id.perfilNomeCompleto);
        perfilEmail = (TextView) view.findViewById(R.id.perfilEmail);


        perfilNomeCompleto.setText(Globais.nomeCompleto);
        perfilEmail.setText(Globais.email);
        if (Globais.fotoPerfil.startsWith("http")) {
            try {
                new DownloadImageTask(perfilFoto,true).execute(Globais.fotoPerfil);
            } catch (Exception e) {
            }

        } else if (Globais.fotoPerfil.startsWith("data")) {
            perfilFoto.setImageBitmap(Funcoes.decodeFrom64toRound(Globais.fotoPerfil));
        }

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

        arrayInfracoes = new ArrayList<Object>();

        // Ver o video em para tentar resolver o problema...
        // https://www.youtube.com/watch?v=30RJYT9tctc
        mDatabase.child("infracoes").orderByChild("email").
                equalTo(Globais.emailLogado).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Infracao infracao = dataSnapshot.getValue(Infracao.class);
                infracao.setId(dataSnapshot.getKey());
                arrayInfracoes.add(infracao);

                // Ordena pelos mais recentes pra cima
                Collections.sort(arrayInfracoes, new Comparator<Object>(){
                    public int compare(Object obj1, Object obj2) {
                        // ## Descending order
                        if (((obj1 instanceof Infracao) && (obj2 instanceof Infracao)) ||
                            ((obj2 instanceof Infracao) && (obj1 instanceof Infracao)))
                        {
                            Infracao infracao1 = (Infracao)obj1;
                            Infracao infracao2 = (Infracao)obj2;
                            String dataHora1 = infracao1.getData()+" "+infracao1.getHora();
                            String dataHora2 = infracao2.getData()+" "+infracao2.getHora();
                            return dataHora2.compareToIgnoreCase(dataHora1); // To compare string values
                        }
                        else if ((obj1 instanceof InfracaoComDetalhe) && (obj2 instanceof Infracao))
                        {
                            InfracaoComDetalhe infracao1 = (InfracaoComDetalhe) obj1;
                            Infracao infracao2 = (Infracao)obj2;
                            String dataHora1 = infracao1.getData()+" "+infracao1.getHora();
                            String dataHora2 = infracao2.getData()+" "+infracao2.getHora();
                            return dataHora2.compareToIgnoreCase(dataHora1); // To compare string values
                        }
                        else if ((obj1 instanceof Infracao) && (obj2 instanceof InfracaoComDetalhe))
                        {
                            Infracao infracao1 = (Infracao) obj1;
                            InfracaoComDetalhe infracao2 = (InfracaoComDetalhe) obj2;
                            String dataHora1 = infracao1.getData()+" "+infracao1.getHora();
                            String dataHora2 = infracao2.getData()+" "+infracao2.getHora();
                            return dataHora2.compareToIgnoreCase(dataHora1); // To compare string values
                        }
                        else return 0;
                    }
                });

                adapter.arrayInfracoes = arrayInfracoes;
                adapter.notifyDataSetChanged();
                atualizaRelatorios();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Infracao infracao = dataSnapshot.getValue(Infracao.class);

                Iterator<Object> iterator = arrayInfracoes.iterator();

                while (iterator.hasNext()) {
                    Object obj = iterator.next();
                    if (obj instanceof Infracao)
                    {
                        Infracao infracao1 = (Infracao)obj;
                        if (infracao1.getId().equals(dataSnapshot.getKey()))
                        {
                            infracao1.copia(infracao);
                            adapter.arrayInfracoes = arrayInfracoes;
                            adapter.notifyDataSetChanged();
                            break;
                        }

                    }
                }
                atualizaRelatorios();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Infracao infracao = dataSnapshot.getValue(Infracao.class);

                Iterator<Object> iterator = arrayInfracoes.iterator();

                while (iterator.hasNext()) {
                    Object obj = iterator.next();
                    if (obj instanceof Infracao)
                    {
                        Infracao infracao1 = (Infracao)obj;
                        if (infracao1.getId().equals(dataSnapshot.getKey()))
                        {
                            iterator.remove();
                            adapter.arrayInfracoes = arrayInfracoes;
                            adapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
                atualizaRelatorios();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void selectImage() {
        final CharSequence[] items = { "Tirar Foto", "Escolher da galeria",
                "Cancelar" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Foto do perfil");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getContext());

                if (items[item].equals("Tirar Foto")) {
                    //userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Escolher da galeria")) {
                    //userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Constantes.REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), Constantes.SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constantes.SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == Constantes.REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());

                bm = Funcoes.getRoundedShape(Funcoes.cropToSquare(bm));
                perfilFoto.setImageBitmap(bm);

                // Salva a nova foto do perfil no Firebase
                // Automaticamente a foto do drawer vai se modificar
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 50, bs);
                String encoded_mini = Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);
                mDatabase.child("usuarios_app/"+ Funcoes.convertEmailInKey(Globais.emailLogado)+"/foto_perfil").setValue("data:image/jpeg;base64,"+encoded_mini);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    private void onCaptureImageResult(Intent data) {

        if (data != null)
        {
            //**************************************************
            // Faz os ajustes necessários de rotação da imagem capturada
            Uri selectedImage = data.getData();
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            ContentResolver content = getContext().getContentResolver();
            Cursor cur = content.query(selectedImage, orientationColumn, null, null, null);
            int orientation = -1;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            switch(orientation) {
                case 90:
                    thumbnail = Funcoes.rotateImage(thumbnail, 90);
                    break;
                case 180:
                    thumbnail = Funcoes.rotateImage(thumbnail, 180);
                    break;
                case 270:
                    thumbnail = Funcoes.rotateImage(thumbnail, 270);
                    break;
                default:
                    break;
            }

            thumbnail = Funcoes.getRoundedShape(Funcoes.cropToSquare(thumbnail));
            perfilFoto.setImageBitmap(thumbnail);

            // Salva a nova foto do perfil no Firebase
            // Automaticamente a foto do drawer vai se modificar
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 50, bs);
            String encoded_mini = Base64.encodeToString(bs.toByteArray(), Base64.DEFAULT);

            Log.d("OOPS","trocando a foto em "+"usuarios_app/"+Funcoes.convertEmailInKey(Globais.emailLogado)+"/foto_perfil");
            mDatabase.child("usuarios_app/"+Funcoes.convertEmailInKey(Globais.emailLogado)+"/foto_perfil").setValue("data:image/jpeg;base64,"+encoded_mini);
        }
    }

    public void abreCaixaEdicao()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Nome completo");
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_input_dialog, (ViewGroup) getView(), false);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String nomeCompleto = input.getText().toString();

                perfilNomeCompleto.setText(nomeCompleto);

                mDatabase.child("usuarios_app/"+Funcoes.convertEmailInKey(Globais.emailLogado)+"/nome_completo").setValue(nomeCompleto);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // TODO: Dica Cris - Fonte: http://stackoverflow.com/questions/4095758/change-color-of-button-in-alert-dialog-android
        AlertDialog a=builder.create();

        a.show();

        Button btp = a.getButton(DialogInterface.BUTTON_POSITIVE);
        //btp.setBackgroundColor(Color.BLUE);
        btp.setTextColor(Color.BLACK);

        Button btn = a.getButton(DialogInterface.BUTTON_NEGATIVE);
        //btn.setBackgroundColor(Color.BLUE);
        btn.setTextColor(Color.BLACK);
    }

    public void atualizaRelatorios()
    {
        int iCirculo1 = 0;
        int iCirculo2 = 0;
        int iCirculo3 = 0;
        int iCirculo4 = 0;
        int iCirculo5 = 0;

        Iterator<Object> iterator = arrayInfracoes.iterator();

        while (iterator.hasNext()) {
            Object obj = iterator.next();
            if (obj instanceof Infracao)
            {
                //Infracao infracao1 = iterator.next();
                Infracao infracao1 = (Infracao)obj;
                if (infracao1.getStatus().equals("05")) iCirculo1++;
                if (infracao1.getStatus().equals("01")) iCirculo2++;
                if (infracao1.getStatus().equals("03")) iCirculo3++;
                if (infracao1.getStatus().equals("04")) iCirculo4++;
                if (infracao1.getStatus().equals("02")) iCirculo5++;
            }
        }

        circulo1.setText(iCirculo1+"");
        circulo2.setText(iCirculo2+"");
        circulo3.setText(iCirculo3+"");
        circulo4.setText(iCirculo4+"");
        circulo5.setText(iCirculo5+"");

    }

    public static void notificaAtualizacaoArray()
    {
        adapter.arrayInfracoes = arrayInfracoes;
        adapter.notifyDataSetChanged();
    }
}
