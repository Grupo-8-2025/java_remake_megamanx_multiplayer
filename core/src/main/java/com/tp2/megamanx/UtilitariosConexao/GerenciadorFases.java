package com.tp2.megamanx.UtilitariosConexao;

public class GerenciadorFases implements java.io.Serializable {
    public GerenciadorFases(){}
    
    public int faseAtual = 1;

    public int getFaseAtual() {
        return faseAtual;
    }
    public void setFaseAtual(int faseAtual) {
        this.faseAtual = faseAtual;
    }
    public GerenciadorFases(int faseAtual) {
        this.faseAtual = faseAtual;
    }
}
