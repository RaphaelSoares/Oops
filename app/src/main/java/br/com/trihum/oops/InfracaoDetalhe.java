package br.com.trihum.oops;

/**
 * Created by raphaelmoraes on 03/01/17.
 */

public class InfracaoDetalhe {

    // Dados detalhe infracao
    private String foto;
    private String foto_mini;
    private String endereco;
    private double latitude;
    private double longitude;
    private String placa;
    private String placa_ident;
    private String identifica_infracao;

    public InfracaoDetalhe()
    {

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
}
