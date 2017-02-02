package br.com.trihum.oops.utilities;

import java.util.HashMap;

/**
 * Created by raphaelmoraes on 14/01/17.
 */

public class Globais {
    public static boolean conectado;

    public static String nomeCompleto;
    public static String fotoPerfil;
    public static String email;
    public static int tipoLogin; // 0 - comun, 1 - rede social
    public static int provedorLogin;

    public static String emailLogado;

    public static HashMap<String, String> mapaSituacoes;
    public static HashMap<String, String> mapaTipos;
    public static HashMap<String, String> mapaOrgaos;
    public static String mensagemPadraoRegistroRecebido;

    public static String grupo;
}
