package br.com.trihum.oops.utilities;

/**
 * Created by raphaelmoraes on 19/12/16.
 */

public class Constantes {

    public static final int TIPO_LOGIN_COMUM = 0;
    public static final int TIPO_LOGIN_REDE_SOCIAL = 1;

    public static final int PROVEDOR_LOGIN_COMUM = 0;
    public static final int PROVEDOR_LOGIN_GOOGLE = 1;
    public static final int PROVEDOR_LOGIN_FACEBOOK = 2;

    public static final int REQUEST_CAMERA = 0;
    public static final int SELECT_FILE = 1;

    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_OFFLINE = "infracaoSelecionada_offline";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_ID = "infracaoSelecionada_id";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_STATUS = "infracaoSelecionada_status";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_TIPO = "infracaoSelecionada_tipo";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_DATA = "infracaoSelecionada_data";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_HORA = "infracaoSelecionada_hora";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_EMAIL = "infracaoSelecionada_email";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_COMENTARIO = "infracaoSelecionada_comentario";
    public static final String INTENT_PARAM_INFRACAO_SELECIONADA_FOTO_OFFLINE = "infracaoSelecionada_foto_offline";

    public static final String SHARED_PREFERENCES_NAME 					= "MinhasConfiguracoes";
    public static final String SHARED_PREFERENCES_KEY_EXIBE_TUTORIAL    = "exibeTutorial";
    public static final String SHARED_PREFERENCES_KEY_INFRACOES_OFFLINE = "infracoesOffline";

    public static final int RC_SIGN_IN = 9001;
    public static final int RC_CADASTRO = 1000;
    public static final int RC_CADASTRO_SUCESSO = 1;
    public static final int RC_CADASTRO_FALHA = 0;
}
