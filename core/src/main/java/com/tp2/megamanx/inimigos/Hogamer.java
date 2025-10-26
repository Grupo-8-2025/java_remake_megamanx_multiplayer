package com.tp2.megamanx.inimigos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

public class Hogamer extends Personagem implements Inimigo{

    public Hogamer(Texture textura) {
        super(textura, new TextureRegion(textura, 0, 0, 37, 37), 
        new Vector2(0.3f, 1.0f), 0, 0, 5, 3);
    }

    public Rectangle getRect(){
        return corpo.getBoundingRectangle();
    }

    public int getDano() {
        return dano;
    }

    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    public void setPosicaoMegaMan(Vector2 posMegaMan) {
        this.posMegaMan = posMegaMan;
    }

    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    @Override
    public void mover() {
        Vector2 posicao = new Vector2(posX, posY);
        float distancia = posMegaMan.dst(posicao); 
        if(distancia < 600){
            paraDireita = false; 
            velX = -3; 
            setPosicao(posX + velX, posY);
            animar(posX, 3, 37, 0, 0, 37, 37);
        }
        sofrerGravidade(posY, 3, 37, 0, 0, 37, 37, 0, 0, 37, 37);
    }

    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            setPosicao(-500, -500);
        }else if(posMegaMan.x > posX + 300){
            morreu = true;
            setPosicao(-500, -500);
        }
    }

}

