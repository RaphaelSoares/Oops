package br.com.trihum.oops.model;

/**
 * Created by raphaelmoraes on 18/01/17.
 */

public class InfracaoComDetalhe {

    // Dados tabela infracao
    private String id;
    private String status;
    private String tipo;
    private String data;
    private String hora;
    private String comentario;
    private String email;

    // Dados detalhe infracao
    private String foto;
    private String foto_mini;
    private String endereco;
    private double latitude;
    private double longitude;
    private String placa;
    private String placa_ident;
    private String identifica_infracao;
    private String comentario_orgao;

    public InfracaoComDetalhe()
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

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFoto_mini() {
        return foto_mini;
    }

    public void setFoto_mini(String foto_mini) {
        this.foto_mini = foto_mini;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getPlaca_ident() {
        return placa_ident;
    }

    public void setPlaca_ident(String placa_ident) {
        this.placa_ident = placa_ident;
    }

    public String getIdentifica_infracao() {
        return identifica_infracao;
    }

    public void setIdentifica_infracao(String identifica_infracao) {
        this.identifica_infracao = identifica_infracao;
    }

    public String getComentario_orgao() {
        return comentario_orgao;
    }

    public void setComentario_orgao(String comentario_orgao) {
        this.comentario_orgao = comentario_orgao;
    }
}
