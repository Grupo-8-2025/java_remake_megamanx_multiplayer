package com.tp2.megamanx.UtilitariosConexao;

import com.tp2.megamanx.TipoAtaque;

public class PosicaoTiro implements java.io.Serializable {
    public float x, y;
    public TipoAtaque tipo;
    public boolean paraDireita; // direção do tiro

    public PosicaoTiro() {}

    public PosicaoTiro(float x, float y, TipoAtaque tipo) {
        this(x, y, tipo, true);
    }

    public PosicaoTiro(float x, float y, TipoAtaque tipo, boolean paraDireita) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.paraDireita = paraDireita;
    }
}
