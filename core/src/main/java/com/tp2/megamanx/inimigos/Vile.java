package com.tp2.megamanx.inimigos;

import com.tp2.megamanx.Ataque;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Vile extends Boss {

    public Vile(Texture textura, Ataque ataque) {
        
        super(textura, new TextureRegion(textura, 0, 0, 33, 45), 
        new Vector2(0.5f, 2.0f), 300, 500, 20, 3, ataque);

        ataquesAtivos = new ArrayList<>();
    }

    public void atualizar(){
        if(!morreu && Math.abs(posMegaMan.x - posX) < 600){
            sofrerGravidade(posY, 1, 0, 53, 0, 53, 45, 0, 0, 53, 45); 

            delimitarMovimento(185, 630); 

            int acaoAnterior = determinaAcao;
            iterarDeltaTime(); 
            atualizarAcao(); 

            tomandoDanoPorAtaque(1, 43, 731, 19, 43, 41, 
            0, 24, 43, 36); 
            
            if(!noAr){
                if(determinaAcao != acaoAnterior){
                    if(determinaAcao == 0){
                        determinarAcaoMover();
                    }else if(determinaAcao == 1){
                        determinarAcaoParado();
                    }else if(determinaAcao == 2){
                        determinarAcaoAtaque();
                    } 
                }
            }
        }
    }

    private void determinarAcaoAtaque(){
        duracaoAcao = 2f;
        podeAtacar = true;
        podeMover = false;
    }

    @Override
    public void mover() {
        if (podeMover) {
            if (posMegaMan.x < posX) {
                moverParaEsquerda(2, 53, 106, 0, 53, 45);
            } else if (posMegaMan.x > posX) {
                moverParaDireita(2, 53, 106, 0, 53, 45);
            }
        } else {
            if (posMegaMan.x < posX) {
                paraDireita = false;
            } else if (posMegaMan.x > posX) {
                paraDireita = true;
            }
            setRegion(0, 0, 33, 45); 
        }
        parado(2, 53, 0, 0, 53, 45);
    }

    @Override
    public void atacar(){
        if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
            setRegion(53, 0, 53, 45); // Frame de ataque

            float posXataque = 0;
            float posYataque = corpo.getY() + 10f;

            int velocidadeAtaque = 0;
            if(posMegaMan.x > posX){
                paraDireita = true;
                velocidadeAtaque = 5;
                posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
            }else if(posMegaMan.x < posX){
                paraDireita = false;
                velocidadeAtaque = -5;
                posXataque = corpo.getX();
            }

            Ataque novoAtaque = new Ataque(
                new TextureRegion(
                    ataqueAtual.getTipo().getTextura(),
                    ataqueAtual.getTipo().getCordX(), ataqueAtual.getTipo().getCordY(),
                    ataqueAtual.getTipo().getLargura(), ataqueAtual.getTipo().getAltura()),
                new Vector2(1f, 2.5f), posXataque, posYataque, 
                ataqueAtual.getTipo(), velocidadeAtaque, paraDireita
            );

            novoAtaque.setColidiu(false);
            novoAtaque.setPodeDisparar(true);
            ataquesAtivos.add(novoAtaque);
        }
    }


    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            iterarDeltaTime();
            setRegion(0, 0, 33, 45); 
            if (deltaTime >= 3.0f) {
                setPosicao(-500, -500); 
            }
        }
    }

}
