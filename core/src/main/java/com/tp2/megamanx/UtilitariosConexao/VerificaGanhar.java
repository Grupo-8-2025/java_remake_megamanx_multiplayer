package com.tp2.megamanx.UtilitariosConexao;

public class VerificaGanhar implements java.io.Serializable {
    public boolean ganhou =false;

    public VerificaGanhar() {}

    public VerificaGanhar(boolean ganhou) { 
        this.ganhou = ganhou; 
    }

    public void setGanhou(boolean ganhou) { 
        this.ganhou = ganhou; 
    }

}