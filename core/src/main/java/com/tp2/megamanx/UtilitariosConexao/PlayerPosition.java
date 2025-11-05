package com.tp2.megamanx.UtilitariosConexao;

public class PlayerPosition {
    public float x;
    public float y;
    public int side; // já existia (isServer ? 0 : 1)
    public int playerId; // novo
    public int regionX, regionY, regionW, regionH;
    public boolean paraDireita;
    public PlayerPosition() {}
    public PlayerPosition(float x, float y, int side) { this.x = x; this.y = y; this.side = side; }
}