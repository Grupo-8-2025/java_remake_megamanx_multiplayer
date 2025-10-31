package com.tp2.megamanx;


/*
 * Classe simples para representar a posição do jogador na rede.
 */
public class PlayerPosition {
    public float x, y;
    public int id; // 0 para player1, 1 para player2
    // animation / region info so remote can mirror animation
    public int regionX;
    public int regionY;
    public int regionW;
    public int regionH;
    public boolean paraDireita;

    public PlayerPosition() {}

    public PlayerPosition(float x, float y, int id) {
        this.x = x;
        this.y = y;
        this.id = id;
    }
    
}