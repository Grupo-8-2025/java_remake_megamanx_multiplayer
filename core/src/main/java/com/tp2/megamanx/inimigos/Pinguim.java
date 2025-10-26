package com.tp2.megamanx.inimigos;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.TipoAtaque;

public class Pinguim extends Boss {

    private int indexAtaqueAtual;
    private ArrayList<Ataque> tiposAtaque;

    public Pinguim(Texture textura) {
        
        super(textura, new TextureRegion(textura, 602, 16, 43, 44), 
        new Vector2(0.1f, 2.0f), 11635, 3000, 32, 4);    
        
        criarAtaques(); 
    }

    public void criarAtaques(){
        tiposAtaque = new ArrayList<>();
        indexAtaqueAtual = 0;

        tiposAtaque.add(
            new Ataque(
                new TextureRegion(
                    TipoAtaque.BOLA_GELO.getTextura(),  
                    TipoAtaque.BOLA_GELO.getCordX1(), 
                    TipoAtaque.BOLA_GELO.getCordY1(),
                    TipoAtaque.BOLA_GELO.getLargura1(), 
                    TipoAtaque.BOLA_GELO.getAltura1()), 
                new Vector2(0.5f, 1.5f), -100, -100, 
                TipoAtaque.BOLA_GELO, 0
            )
        );

        tiposAtaque.add(
            new Ataque(
                new TextureRegion(
                    TipoAtaque.SOPRO_GELO.getTextura(),  
                    TipoAtaque.SOPRO_GELO.getCordX1(), 
                    TipoAtaque.SOPRO_GELO.getCordY1(),
                    TipoAtaque.SOPRO_GELO.getLargura1(), 
                    TipoAtaque.SOPRO_GELO.getAltura1()), 
                new Vector2(0.5f, 1.5f), -100, -100, 
                TipoAtaque.SOPRO_GELO, 0
            )
        );

        ataqueAtual = tiposAtaque.get(0); // Ataque inicial
    }

    /* 
    @Override
    protected void setRegion(int cordX, int cordY, int largura, int altura) {
        region.setRegion(cordX, cordY, largura, altura);

        boolean flipou = region.isFlipX();
        if(paraDireita != flipou) {
            region.flip(true, false);
        }

        corpo.setRegion(region);
    }
    */

    public void atualizar(){
        if(!morreu && Math.abs(posMegaMan.x - posX) < 600){
            sofrerGravidade(posY, 1, 0, 602, 16, 43, 
            44, 0, 24, 43, 36); 

            delimitarMovimento(8050, 8670); 

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
                        determinarAtaqueBolaGelo();
                    }else if(determinaAcao == 3){
                        determinarAtaqueSoproGelo();
                    }
                } 
            }

        }
    }

    private void determinarAtaqueBolaGelo(){
        duracaoAcao = 3.f;
        indexAtaqueAtual = 0;
        ataqueAtual = tiposAtaque.get(indexAtaqueAtual);
        podeAtacar = true;
        podeMover = false;
    }

    private void determinarAtaqueSoproGelo(){
        duracaoAcao = 3f;
        indexAtaqueAtual = 1;
        ataqueAtual = tiposAtaque.get(indexAtaqueAtual);
        podeAtacar = true;
        podeMover = false;
    }

    @Override
    public void mover() {
        if (podeMover) {
            if (posMegaMan.x < posX) {
                moverParaEsquerda(1, 43, 301, 31, 43, 29);
            } else if (posMegaMan.x > posX) {
                moverParaDireita(1, 43, 301, 31, 43, 29);
            }
        } else {
            if (posMegaMan.x < posX) {
                paraDireita = false;
            } else if (posMegaMan.x > posX) {
                paraDireita = true;
            }
            setRegion(0, 24, 43, 36); 
        }
    }

    @Override
    public void atacar(){
        if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
            setRegion(688, 25, 43, 35); // Frame de ataque

            float posXataque = 0;
            float posYataque = corpo.getY() + 15f;

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
                    ataqueAtual.getTipo().getCordX1(), ataqueAtual.getTipo().getCordY1(),
                    ataqueAtual.getTipo().getLargura1(), ataqueAtual.getTipo().getAltura1()),
                new Vector2(1f, 2.5f), posXataque, posYataque, 
                ataqueAtual.getTipo(), velocidadeAtaque
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
            setRegion(774, 13, 43, 47); 
            if (deltaTime >= 5.0f) {
                setPosicao(-500, -500); 
            }
        }
    }

}