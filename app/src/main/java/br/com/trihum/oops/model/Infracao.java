package br.com.trihum.oops.model;

/**
 * Created by raphaelmoraes on 19/12/16.
 */

public class Infracao {

    // Dados tabela infracao
    private String id;
    private String status;
    private String tipo;
    private String data;
    private String hora;
    //private String uid;
    private String comentario;
    private String email;

    public Infracao()
    {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    /*public String getUid() {
        return uid;
    }*/

    /*public void setUid(String uid) {
        this.uid = uid;
    }*/

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public void copia(Infracao infracaoOrigem)
    {
        status = infracaoOrigem.getStatus();
        tipo = infracaoOrigem.getTipo();
        data = infracaoOrigem.getData();
        hora = infracaoOrigem.getHora();
        //uid = infracaoOrigem.getUid();
        comentario = infracaoOrigem.getComentario();
        email = infracaoOrigem.getEmail();
    }
}
