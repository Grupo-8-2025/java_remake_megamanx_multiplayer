package com.tp2.megamanx.inimigos;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

public class Trower extends Personagem implements Inimigo{

    public Trower(Texture textura, Ataque ataque) {

        super(textura, new TextureRegion(textura, 0, 0, 35, 58), 
        new Vector2(0.4f, 1.3f), 0, 0, 6, 2, ataque);

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

        animar(posX, 7, 35, 0, 0, 35, 58);
        sofrerGravidade(posY, 1, 35, 0, 0, 35, 58, 0, 0, 35, 58);
    }


    @Override
    public void atacar(){
        if(Math.abs(posMegaMan.x - posX) < 600){
            iterarDeltaTime();
            if(deltaTime >= 5f){
                float posXataque = 0, posYataque = 0;
                float velocidadeAtaque = 0;

                if(paraDireita){
                    posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
                    velocidadeAtaque = 5;
                }else if(!paraDireita){
                    posXataque = corpo.getX();
                    velocidadeAtaque = -5;
                }

                Ataque novoAtaque = new Ataque(
                    new TextureRegion(
                        ataqueAtual.getTipo().getTextura(),
                        ataqueAtual.getTipo().getCordX(), 
                        ataqueAtual.getTipo().getCordY(),
                        ataqueAtual.getTipo().getLargura(), 
                        ataqueAtual.getTipo().getAltura()),
                    new Vector2(0.3f, 1.2f), posXataque, posYataque, 
                    ataqueAtual.getTipo(), velocidadeAtaque, paraDireita
                );
                novoAtaque.setColidiu(false);
                novoAtaque.setPodeDisparar(true);
                ataquesAtivos.add(novoAtaque);
                deltaTime = 0f;
            }
        }
    }

}