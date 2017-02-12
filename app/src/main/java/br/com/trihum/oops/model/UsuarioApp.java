package br.com.trihum.oops.model;

/**
 * Created by raphaelmoraes on 01/01/17.
 */

public class UsuarioApp {

    public String nome_completo;
    public String email;
    public String foto_perfil;
    public String grupo;
    public String token;

    public UsuarioApp()
    {
        // Default constructor required for calls to DataSnapshot.getValue(UsuarioApp.class)
    }

    public UsuarioApp(String nomeCompleto, String email, String foto_perfil){
        this.nome_completo = nomeCompleto;
        this.email = email;
        this.foto_perfil = foto_perfil;
        this.grupo = "1";
        this.token = "";
    }
}
