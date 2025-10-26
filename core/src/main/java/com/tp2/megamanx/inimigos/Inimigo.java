package com.tp2.megamanx.inimigos;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.tp2.megamanx.Ataque;

public interface Inimigo {

    public Rectangle getRect();

    public int getDano();

    public ArrayList<Ataque> getAtaquesAtivos();

    public void setPosicaoMegaMan(Vector2 posMegaMan);

    public void tomarDano(int dano);
    
}
