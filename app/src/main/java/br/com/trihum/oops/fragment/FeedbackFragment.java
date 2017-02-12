package br.com.trihum.oops.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.trihum.oops.PrincipalActivity;
import br.com.trihum.oops.R;
import br.com.trihum.oops.model.FeedbackUsuario;
import br.com.trihum.oops.model.Questionario;
import br.com.trihum.oops.utilities.Constantes;
import br.com.trihum.oops.utilities.Funcoes;
import br.com.trihum.oops.utilities.Globais;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedbackFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment {


    public TextView txtPergunta1;
    public TextView txtPergunta2;
    public TextView txtPergunta3;
    public EditText editPergunta1;
    public EditText editPergunta2;
    public EditText editPergunta3;
    public RadioGroup rgGrupo1;
    public RadioGroup rgGrupo2;
    public RadioGroup rgGrupo3;
    public Button btnFeedbackEnviar;
    public EditText txtArea;

    public String questionario_ativo;

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

    public FeedbackFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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
        View view = inflater.inflate(R.layout.fragment_feedback, container, false);

        //*************************************************
        // Componentes da tela
        txtPergunta1 = (TextView) view.findViewById(R.id.txtPergunta1);
        txtPergunta2 = (TextView) view.findViewById(R.id.txtPergunta2);
        txtPergunta3 = (TextView) view.findViewById(R.id.txtPergunta3);
        editPergunta1 = (EditText) view.findViewById(R.id.editPergunta1);
        editPergunta2 = (EditText) view.findViewById(R.id.editPergunta2);
        editPergunta3 = (EditText) view.findViewById(R.id.editPergunta3);
        rgGrupo1 = (RadioGroup) view.findViewById(R.id.rgGrupo1);
        rgGrupo2 = (RadioGroup) view.findViewById(R.id.rgGrupo2);
        rgGrupo3 = (RadioGroup) view.findViewById(R.id.rgGrupo3);
        txtArea = (EditText) view.findViewById(R.id.txtArea);
        btnFeedbackEnviar = (Button)view.findViewById(R.id.btnFeedbackEnviar);

        //****************************************
        // Objetos Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnFeedbackEnviar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarFeedback();
            }
        });

        // Retira todas as questoes da tela
        definePergunta1("",0);
        definePergunta2("",0);
        definePergunta3("",0);
        txtArea.setText("");

        obterQuestionarioAtivo();

        //********************************************************
        return view;
    }

    public void obterQuestionarioAtivo()
    {
        questionario_ativo = "";
        mDatabase.child("controles/questionario_ativo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                questionario_ativo = dataSnapshot.getValue(String.class);
                Log.d("OOPS","questionario_ativo = ("+questionario_ativo+")");

                if (!questionario_ativo.equals(""))
                {
                    montaQuestionario();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void montaQuestionario()
    {
        mDatabase.child("questionarios/"+questionario_ativo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Questionario questionario = dataSnapshot.getValue(Questionario.class);
                if (questionario == null) return;

                definePergunta1(questionario.getPergunta1(),questionario.getTipo1());
                definePergunta2(questionario.getPergunta2(),questionario.getTipo2());
                definePergunta3(questionario.getPergunta3(),questionario.getTipo3());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void definePergunta1(String pergunta, int tipo)
    {
        if (pergunta.equals(""))
        {
            txtPergunta1.setVisibility(View.GONE);
            editPergunta1.setVisibility(View.GONE);
            rgGrupo1.setVisibility(View.GONE);
            editPergunta1.setText("");
            rgGrupo1.clearCheck();
        }
        else
        {
            txtPergunta1.setVisibility(View.VISIBLE);
            txtPergunta1.setText(pergunta);
            if (tipo == 1) // tipo 1 = editText
            {
                editPergunta1.setVisibility(View.VISIBLE);
                editPergunta1.setText("");
                rgGrupo1.setVisibility(View.GONE);
            }
            else if (tipo == 2)  // tipo 2 = radio
            {
                editPergunta1.setVisibility(View.GONE);
                rgGrupo1.setVisibility(View.VISIBLE);
                rgGrupo1.clearCheck();
            }
        }
    }

    public void definePergunta2(String pergunta, int tipo)
    {
        if (pergunta.equals(""))
        {
            txtPergunta2.setVisibility(View.GONE);
            editPergunta2.setVisibility(View.GONE);
            rgGrupo2.setVisibility(View.GONE);
            editPergunta2.setText("");
            rgGrupo2.clearCheck();
        }
        else
        {
            txtPergunta2.setVisibility(View.VISIBLE);
            txtPergunta2.setText(pergunta);
            if (tipo == 1) // tipo 1 = editText
            {
                editPergunta2.setVisibility(View.VISIBLE);
                editPergunta2.setText("");
                rgGrupo2.setVisibility(View.GONE);
            }
            else if (tipo == 2)  // tipo 2 = radio
            {
                editPergunta2.setVisibility(View.GONE);
                rgGrupo2.setVisibility(View.VISIBLE);
                rgGrupo2.clearCheck();
            }
        }
    }

    public void definePergunta3(String pergunta, int tipo)
    {
        if (pergunta.equals(""))
        {
            txtPergunta3.setVisibility(View.GONE);
            editPergunta3.setVisibility(View.GONE);
            rgGrupo3.setVisibility(View.GONE);
            editPergunta3.setText("");
            rgGrupo3.clearCheck();
        }
        else
        {
            txtPergunta3.setVisibility(View.VISIBLE);
            txtPergunta3.setText(pergunta);
            if (tipo == 1) // tipo 1 = editText
            {
                editPergunta3.setVisibility(View.VISIBLE);
                editPergunta3.setText("");
                rgGrupo3.setVisibility(View.GONE);
            }
            else if (tipo == 2)  // tipo 2 = radio
            {
                editPergunta3.setVisibility(View.GONE);
                rgGrupo3.setVisibility(View.VISIBLE);
                rgGrupo3.clearCheck();
            }
        }
    }

    public void enviarFeedback()
    {
        FeedbackUsuario feedbackUsuario = new FeedbackUsuario();

        // Campo obrigatório
        if (rgGrupo1.getVisibility()==View.VISIBLE) feedbackUsuario.setResposta1(""+(rgGrupo1.indexOfChild(getActivity().findViewById(rgGrupo1.getCheckedRadioButtonId()))+1));
        else feedbackUsuario.setResposta1(editPergunta1.getText().toString());

        if (rgGrupo2.getVisibility()==View.VISIBLE) feedbackUsuario.setResposta2(""+(rgGrupo2.indexOfChild(getActivity().findViewById(rgGrupo2.getCheckedRadioButtonId()))+1));
        else feedbackUsuario.setResposta2(editPergunta2.getText().toString());

        if (rgGrupo3.getVisibility()==View.VISIBLE) feedbackUsuario.setResposta3(""+(rgGrupo3.indexOfChild(getActivity().findViewById(rgGrupo3.getCheckedRadioButtonId()))+1));
        else feedbackUsuario.setResposta3(editPergunta3.getText().toString());

        // Campo opcional
        feedbackUsuario.setRespostaTexto(txtArea.getText().toString());

        if ((!feedbackUsuario.getResposta1().isEmpty() && !feedbackUsuario.getResposta1().equals("0"))
                && (!feedbackUsuario.getResposta2().isEmpty() && !feedbackUsuario.getResposta2().equals("0"))
                && (!feedbackUsuario.getResposta3().isEmpty() && !feedbackUsuario.getResposta3().equals("0"))){

            //Log.d("OOPS","feedbackUsuario.getResposta1() = "+feedbackUsuario.getResposta1());
            //Log.d("OOPS","feedbackUsuario.getResposta2() = "+feedbackUsuario.getResposta2());
            //Log.d("OOPS","feedbackUsuario.getResposta3() = "+feedbackUsuario.getResposta3());

            mDatabase.child("feedback_usuarios/"+questionario_ativo).child(Funcoes.convertEmailInKey(Globais.emailLogado)).setValue(feedbackUsuario);
            ((PrincipalActivity)getActivity()).exibeFragmentPrincipal();
        } else {

            Toast.makeText(getContext(), "Por favor, preencha os campos do formulário.", Toast.LENGTH_SHORT).show();
            return;
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onEnviarFeedbackClick(View v){

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

}
