package br.com.trihum.oops.model;

/**
 * Created by raphaelmoraes on 12/01/17.
 */

public class Questionario {

    private String pergunta1;
    private String pergunta2;
    private String pergunta3;
    private int tipo1;
    private int tipo2;
    private int tipo3;

    public Questionario()
    {

    }

    public String getPergunta1() {
        return pergunta1;
    }

    public void setPergunta1(String pergunta1) {
        this.pergunta1 = pergunta1;
    }

    public String getPergunta2() {
        return pergunta2;
    }

    public void setPergunta2(String pergunta2) {
        this.pergunta2 = pergunta2;
    }

    public String getPergunta3() {
        return pergunta3;
    }

    public void setPergunta3(String pergunta3) {
        this.pergunta3 = pergunta3;
    }

    public int getTipo1() {
        return tipo1;
    }

    public void setTipo1(int tipo1) {
        this.tipo1 = tipo1;
    }

    public int getTipo2() {
        return tipo2;
    }

    public void setTipo2(int tipo2) {
        this.tipo2 = tipo2;
    }

    public int getTipo3() {
        return tipo3;
    }

    public void setTipo3(int tipo3) {
        this.tipo3 = tipo3;
    }
}
