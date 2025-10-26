package com.tp2.megamanx;

public class PosicaoTiro {
    public float x, y;
    public int id; // id do personagem que disparou o tiro

    public PosicaoTiro() {}

    public PosicaoTiro(float x, float y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
}
