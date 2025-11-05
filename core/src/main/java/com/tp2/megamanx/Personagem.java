package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Personagem extends Entidade {

    protected int vida;
    protected int dano;
    protected float velX;
    protected float velY;
    protected Ataque ataqueAtual;
    protected ArrayList<Ataque> ataquesAtivos;
    protected Vector2 posMegaMan;
    protected float deltaTime; 

    protected boolean tomandoDano;         
    protected boolean naPlataforma;  
    protected boolean noAr;      
    protected boolean podeAndarDireita;    
    protected boolean podeAndarEsquerda;                   
    protected boolean morreu;        


    public Personagem(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, int dano, Ataque ataque, Vector2 posMegaMan) {
        
        super(textura, region, escala, posX, posY); 

        this.vida = vida;
        this.dano = dano;
        this.velX = 0;
        this.velY = 0;
        this.ataqueAtual = ataque;
        ataquesAtivos = new ArrayList<>();
        this.posMegaMan = posMegaMan;
        deltaTime = 0f;

        inicializarAtributosBooleanos();
    }

    public Personagem(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, int dano, Vector2 posMegaMan) {
        
        super(textura, region, escala, posX, posY); 

        this.vida = vida;
        this.dano = dano;
        this.velX = 0;
        this.velY = 0;
        this.ataqueAtual = null;
        ataquesAtivos = new ArrayList<>();
        this.posMegaMan = posMegaMan;

        inicializarAtributosBooleanos();
    }

    private void inicializarAtributosBooleanos(){
        tomandoDano = false;
        naPlataforma = false;
        noAr = true;
        podeAndarDireita = true;
        podeAndarEsquerda = true; 
        morreu = false;
    }


    public Ataque getAtaque(){ return ataqueAtual; }

    public ArrayList<Ataque> getAtaquesAtivos(){ return ataquesAtivos; }

    public int getVida() { return vida; }

    public void setVida(int vida){
        this.vida = vida;
    }

    public int getDano() { return dano; }

    public float getVelX() { return velX; }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() { return velY; }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(float deltaTime) {
        this.deltaTime = deltaTime;
    }

    public boolean isNaPlataforma() { return naPlataforma; }

    public void setNaPlataforma(boolean naPlataforma) {
        this.naPlataforma = naPlataforma;
    }

    public boolean isNoAr() { return noAr; }

    public void setNoAr(boolean noAr) {
        this.noAr = noAr;
    }

    public void setPodeAndarDireita(boolean podeAndarDireita) {
        this.podeAndarDireita = podeAndarDireita;
    }

    public void setPodeAndarEsquerda(boolean podeAndarEsquerda) {
        this.podeAndarEsquerda = podeAndarEsquerda;
    }

    public boolean isMorreu() { return morreu; }

    public void setMorreu(boolean morreu){
        this.morreu = morreu;
    }

    
    protected void iterarDeltaTime() {
		deltaTime += Gdx.graphics.getDeltaTime();
	}

    public void mover() {}

    public void atacar(){}

    public void morrer(){
        if(vida <= 0 || (posMegaMan.x > posX + 450)){
            morreu = true; // não influencia no jogo para personagens que não sejam Boss ou MegaMan
            setPosicao(-500, -500);
        }
    }

    
    public void tomandoDanoPorAtaque(int qtdFrames, int incrementa, int cordX1, int cordY1, int largura1, int altura1, int cordX2, int cordY2, int largura2, int altura2){
        if (tomandoDano) {
            iterarDeltaTime();
            animar(qtdFrames, incrementa, cordX1, cordY1, largura1, altura1);
            
            if (deltaTime >= 2.0f) {
                tomandoDano = false;
                setRegion(cordX2, cordY2, largura2, altura2); 
            }
        }
    }

    protected void sofrerGravidade(float posicaoY, int qtdFrames, int incrementa, int cordX1, int cordY1, int largura1, int altura1, int cordX2, int cordY2, int largura2, int altura2) {
        if(noAr && !naPlataforma){
            velY = velY - (0.3f * 0.5f); 
            setPosicao(posX, posY + velY);    

            animar(posicaoY, qtdFrames, incrementa, cordX1, cordY1, largura1, altura1); 

            if (naPlataforma) {
                velY = 0;
                noAr = false;
                setRegion(cordX2, cordY2, largura2, altura2); 
            }

        }
    }

    public void animar(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		iterarDeltaTime();
		int frame = (int)(deltaTime / 0.25f) % qtdFrames; 
        int x = frame * incrementa; 
        setRegion(cordX + x, cordY, largura, altura); 
	}

}