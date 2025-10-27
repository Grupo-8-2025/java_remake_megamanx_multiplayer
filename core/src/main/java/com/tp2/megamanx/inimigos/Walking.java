package com.tp2.megamanx.inimigos;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

public class Walking extends Personagem implements Inimigo{

    public Walking(Texture textura) {
        super(textura, new TextureRegion(textura, 0, 0, 54, 54), 
        new Vector2(0.3f, 1.0f), 0, 0, 6, 2);
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

        animar(posX, 8, 54, 0, 0, 54, 54);
        sofrerGravidade(posX, 1, 54, 108, 0, 54, 54, 0, 0, 54, 54);
    }

}
