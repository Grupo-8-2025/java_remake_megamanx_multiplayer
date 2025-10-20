package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;

public interface Inimigo {
    
    public Rectangle getRect();

    public int getDano();

    public ArrayList<Ataque> getAtaquesAtivos();

    public void setPosXmegaMan(float posX);

    public void tomarDano(int dano);
    
}
