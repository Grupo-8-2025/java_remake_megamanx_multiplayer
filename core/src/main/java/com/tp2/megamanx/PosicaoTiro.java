package com.tp2.megamanx;

public class PosicaoTiro {
    public float x, y;
    public int id; // id do personagem que disparou o tiro
    public int tipo; // TipoAtaque ordinal
    public boolean paraDireita; // direção do tiro

    public PosicaoTiro() {}

    public PosicaoTiro(float x, float y, int id) {
        this(x, y, id, 0, true);
    }

    public PosicaoTiro(float x, float y, int id, int tipo, boolean paraDireita) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.tipo = tipo;
        this.paraDireita = paraDireita;
    }
}
