package com.tp2.megamanx.inimigos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

public class Trower extends Personagem implements Inimigo{

    public Trower(Texture textura, Ataque ataque) {

        super(textura, new TextureRegion(textura, 0, 0, 35, 58), 
        new Vector2(0.5f, 2.0f), 0, 0, 6, 3, ataque);

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
    public void mover(){
        Vector2 posicao = new Vector2(posX, posY);
        float distancia = posMegaMan.dst(posicao); 
        if(distancia < 600){
            if (posMegaMan.x < posX && podeAndarEsquerda) {
                paraDireita = false; 
                velX = -3;
            } else if (posMegaMan.x > posX && podeAndarDireita) {
                paraDireita = true; 
                velX = 3;
            } 
            setPosicao(posX + velX, posY);
            animar(posX, 7, 35, 0, 0, 35, 58);
        }
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
                        ataqueAtual.getTipo().getCordX1(), 
                        ataqueAtual.getTipo().getCordY1(),
                        ataqueAtual.getTipo().getLargura1(), 
                        ataqueAtual.getTipo().getAltura1()),
                    new Vector2(0.3f, 1.2f), posXataque, posYataque, 
                    ataqueAtual.getTipo(), velocidadeAtaque
                );
                novoAtaque.setColidiu(false);
                novoAtaque.setPodeDisparar(true);
                ataquesAtivos.add(novoAtaque);
                deltaTime = 0f;
            }
        }
    }

    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            iterarDeltaTime();
            setRegion(27, 0, 27, 75);
            setPosicao(-500, -500);
        }
    }

}