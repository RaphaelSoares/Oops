package br.com.trihum.oops.model;

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
    private String comentario_orgao;
    private String msg_orgao;
    private String msg_orgao_01;
    private String msg_orgao_02;
    private String msg_orgao_03;
    private String msg_orgao_04;
    private String msg_orgao_05;

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

    public String getComentario_orgao() {
        return comentario_orgao;
    }

    public void setComentario_orgao(String comentario_orgao) {
        this.comentario_orgao = comentario_orgao;
    }

    public String getMsg_orgao() {
        return msg_orgao;
    }

    public void setMsg_orgao(String msg_orgao) {
        this.msg_orgao = msg_orgao;
    }

    public String getMsg_orgao_01() {return msg_orgao_01;}

    public void setMsg_orgao_01(String msg_orgao_01) {this.msg_orgao_01 = msg_orgao_01;}

    public String getMsg_orgao_02() {return msg_orgao_02;}

    public void setMsg_orgao_02(String msg_orgao_02) {this.msg_orgao_02 = msg_orgao_02;}

    public String getMsg_orgao_03() {return msg_orgao_03;}

    public void setMsg_orgao_03(String msg_orgao_03) {this.msg_orgao_03 = msg_orgao_03;}

    public String getMsg_orgao_04() {return msg_orgao_04;}

    public void setMsg_orgao_04(String msg_orgao_04) {this.msg_orgao_04 = msg_orgao_04;}

    public String getMsg_orgao_05() {return msg_orgao_05;}

    public void setMsg_orgao_05(String msg_orgao_05) {this.msg_orgao_05 = msg_orgao_05;}
}
