package com.tp2.megamanx.Inimigos;

import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Voador extends Personagem implements Inimigo{

    public Voador(Texture textura) {
        super(
            textura, new TextureRegion(textura, 0, 0, 54, 54), 
            new Vector2(0.3f, 1.2f), // escala
            0, 0, 6, 2,
            new Vector2(0, 0) //posMegaMan
        );
    }


    public Rectangle getRect(){ return corpo.getBoundingRectangle(); }

    public int getDano() { return dano; }

    public ArrayList<Ataque> getAtaquesAtivos(){ return ataquesAtivos; }

    public Vector2 getPosicao(){ return new Vector2(posX, posY); }

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

        velX = 0;

        if(distancia < 600){
            if (posMegaMan.x < posX && podeAndarEsquerda) {
                paraDireita = false; 
                velX = -3;
            } else if (posMegaMan.x > posX && podeAndarDireita) {
                paraDireita = true; 
                velX = 3;
            } 
        } else{
            Random random = new Random();
            int sortearMovimento = random.nextInt(2);
            if(sortearMovimento == 0){
                paraDireita = false; 
                velX = -3;
            }else{
                paraDireita = true; 
                velX = 3;
            }
        }

        if(velX != 0){
            setPosicao(posX + velX, posY);
        }

        animar(posX, 10, 30, 0, 0, 30, 32);
        sofrerGravidade(posX, 2, 30, 239, 0, 30, 32, 0, 0, 30, 32);
    }    

}
