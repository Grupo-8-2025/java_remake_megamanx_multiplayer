package com.tp2.megamanx.Inimigos;

import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Boss extends Personagem implements Inimigo {

    protected int quantAcoes;
    protected int determinaAcao;
    protected float duracaoAcao;
    protected boolean podeMover;
    protected boolean podeAtacar;
    protected Random random;

    public Boss(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, 
                int dano, Ataque ataque){

        super(textura, region, escala, posX, posY, vida, dano, ataque, new Vector2(0, 0));
        
        quantAcoes = 4; // Tem que ver isso
        determinaAcao = 0;
        duracaoAcao = 0;
        podeMover = false;
        podeAtacar = false;
        random = new Random();

        paraDireita = false;
    }

    public Boss(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, 
                int dano){

        super(textura, region, escala, posX, posY, vida, dano, new Vector2(0, 0));
        
        quantAcoes = 3; 
        determinaAcao = 0;
        duracaoAcao = 0;
        podeMover = false;
        podeAtacar = false;
        random = new Random();

        paraDireita = false;
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


    protected void atualizarAcao() {
        if (deltaTime >= duracaoAcao) {
            determinaAcao = random.nextInt(quantAcoes);
            deltaTime = 0f; 
        }
    }

    protected void determinarAcaoMover(){
        duracaoAcao = 3.0f;
        podeMover = true;
        podeAtacar = false;
    }

    protected void determinarAcaoParado(){
        duracaoAcao = 1.0f;
        podeAtacar = false;
        podeMover = false;
    }

    protected void determinarAcaoAtaque(){
        duracaoAcao = 2f;
        podeAtacar = true;
        podeMover = false;
    }

    protected void delimitarMovimento(float posXlimite1, float posXlimite2){
        if(posX <= posXlimite1){
            paraDireita = true;
            podeMover = false;
        } else if(posX >= posXlimite2){
            paraDireita = false;
            podeMover = false;
        }
    }

    protected void moverParaDireita(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        if(podeAndarDireita){
            paraDireita = true;
            velX = 5;
            setPosicao(posX + velX, posY);
            animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
        }
    }

    protected void moverParaEsquerda(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        if(podeAndarEsquerda){
            paraDireita = false;
            velX = -5;
            setPosicao(posX + velX, posY);
            animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
        }
    }

    protected void parado(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura){
        if(!podeAtacar && !podeMover){
            velX = 0;            
            animar(qtdFrames, incrementa, cordX, cordY, largura, altura);
        } 
    }
    
}
