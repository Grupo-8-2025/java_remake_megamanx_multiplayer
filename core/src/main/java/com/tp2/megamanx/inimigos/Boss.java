package com.tp2.megamanx.inimigos;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.Personagem;

public class Boss extends Personagem implements Inimigo {

    protected Random random;
    protected int quantAcoes;
    protected int determinaAcao;
    protected float duracaoAcao;
    protected boolean podeMover;
    protected boolean podeAtacar;

    public Boss(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, 
                int dano, Ataque ataque){

        super(textura, region, escala, posX, posY, vida, dano, ataque);
        
        random = new Random();
        quantAcoes = 4;
        determinaAcao = 0;
        duracaoAcao = 0;
        podeMover = false;
        podeAtacar = false;
        paraDireita = false;
    }

    public Boss(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, 
                int dano){

        super(textura, region, escala, posX, posY, vida, dano);
        
        random = new Random();
        quantAcoes = 4;
        determinaAcao = 0;
        duracaoAcao = 0;
        podeMover = false;
        podeAtacar = false;
        paraDireita = false;
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


    protected void determinarAcaoMover(){
        duracaoAcao = 3.f;
        podeMover = true;
        podeAtacar = false;
    }

    protected void determinarAcaoParado(){
        duracaoAcao = 2f;
        podeAtacar = false;
        podeMover = false;
    }

    protected void delimitarMovimento(float posXlimite1, float posXlimite2){
        if(posX <= posXlimite1){
            paraDireita = true;
        } else if(posX >= posXlimite2){
            paraDireita = false;
        }
    }

    protected void atualizarAcao() {
        if (deltaTime >= duracaoAcao) {
            determinaAcao = random.nextInt(quantAcoes);
            deltaTime = 0f; 
        }
    }

    protected void moverParaDireita(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        paraDireita = true;
        velX = 5;
        setPosicao(posX + velX, posY);
        animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
    }

    protected void moverParaEsquerda(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        paraDireita = false;
        velX = -5;
        setPosicao(posX + velX, posY);
        animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
    }

    protected void parado(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura){
        if(!podeAtacar && !podeMover){
            velX = 0;            
            animar(qtdFrames, incrementa, cordX, cordY, largura, altura);
        } 
    }
    
}
