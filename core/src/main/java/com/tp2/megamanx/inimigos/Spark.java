package com.tp2.megamanx.inimigos;

import com.tp2.megamanx.Ataque;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Spark extends Boss {

    public Spark(Texture textura, Ataque ataque) {
        
        super(textura, new TextureRegion(textura, 0, 0, 56, 64), 
        new Vector2(0.2f, 2.0f), 14300, 1700, 40, 6, ataque);

    }

    public void atualizar(){
        if(!morreu && Math.abs(posMegaMan.x - posX) < 600){
            sofrerGravidade(posY, 1, 0, 0, 0, 56, 64, 0, 0, 56, 64); 

            delimitarMovimento(10600, 11855); 

            int acaoAnterior = determinaAcao;
            iterarDeltaTime(); 
            atualizarAcao(); 

            tomandoDanoPorAtaque(3, 56, 0, 0, 56, 64, 
            0, 0, 56, 64); 
            
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
                moverParaEsquerda(2, 88, 294, 0, 88, 64);
            } else if (posMegaMan.x > posX) {
                moverParaDireita(2, 88, 294, 0, 88, 64);
            }
        } else {
            if (posMegaMan.x < posX) {
                paraDireita = false;
            } else if (posMegaMan.x > posX) {
                paraDireita = true;
            }
            setRegion(0, 0, 56, 64); 
        }
        parado(3, 56, 0, 0, 56, 64);
    }

    @Override
    public void atacar(){
        if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
            animar(2, 63, 168, 0, 63, 64);

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
            setRegion(0, 0, 56, 64); 
            if (deltaTime >= 3.0f) {
                setPosicao(-500, -500); 
            }
        }
    }

}

