package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Personagem extends Entidade {

    protected ArrayList<Ataque> ataquesAtivos;
    protected Ataque ataqueAtual;
    protected int vida;
    protected int dano;
    protected float velX;
    protected float velY;

    protected Vector2 posMegaMan;

    protected boolean tomandoDano;         
    protected boolean naPlataforma;        
    protected boolean naParede;            
    protected boolean podeAndarDireita;    
    protected boolean podeAndarEsquerda;   
    protected boolean noAr;                
    protected boolean morreu;              


    public Personagem(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, int dano, Ataque ataque) {
        
        super(textura, region, escala, posX, posY); 

        this.ataqueAtual = ataque;
        this.vida = vida;
        this.dano = dano;
        this.velX = 0;
        this.velY = 0;
        posMegaMan = new Vector2();
        ataquesAtivos = new ArrayList<>();
        inicializarAtributosBooleanos();
    }

    public Personagem(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY, int vida, int dano) {
        
        super(textura, region, escala, posX, posY); 

        this.ataqueAtual = null;
        this.vida = vida;
        this.dano = dano;
        this.velX = 0;
        this.velY = 0;
        posMegaMan = new Vector2();
        ataquesAtivos = new ArrayList<>();
        inicializarAtributosBooleanos();
    }

    private void inicializarAtributosBooleanos(){
        tomandoDano = false;
        naPlataforma = false;
        naParede = false;
        podeAndarDireita = true;
        podeAndarEsquerda = true;
        noAr = true; 
        morreu = false;
    }

    public Ataque getAtaque(){
        return ataqueAtual;
    }

    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    public int getVida() {
        return vida;
    }

    public int getDano() {
        return dano;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }

    public boolean isMorreu() {
        return morreu;
    }

    public boolean isNaPlataforma() {
        return naPlataforma;
    }

    public void setNaPlataforma(boolean naPlataforma) {
        this.naPlataforma = naPlataforma;
    }

    public void setPodeAndarDireita(boolean podeAndarDireita) {
        this.podeAndarDireita = podeAndarDireita;
    }

    public void setVida(int vida){
        this.vida = vida;
    }

    public void setPodeAndarEsquerda(boolean podeAndarEsquerda) {
        this.podeAndarEsquerda = podeAndarEsquerda;
    }

    public boolean isNaParede() {
        return naParede;
    }

    public void setNaParede(boolean naParede) {
        this.naParede = naParede;
    }

    public boolean isNoAr() {
        return noAr;
    }

    public void setNoAr(boolean noAr) {
        this.noAr = noAr;
    }
    

    public void mover() {}

    public void atacar(){}

    public void morrer(){}

    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    public void tomandoDanoPorAtaque(int qtdFrames, int incrementa, int cordX1, int cordY1, int largura1, int altura1, int cordX2, int cordY2, int largura2, int altura2){
        if (tomandoDano) {
            iterarDeltaTime();
            animar(qtdFrames, incrementa, cordX1, cordY1, largura1, altura1);
            
            if (deltaTime >= 3.0f) {
                tomandoDano = false;
                setRegion(cordX2, cordY2, largura2, altura2); 
            }
        }
    }

    protected void sofrerGravidade(float posicaoY, int qtdFrames, int incrementa, int cordX1, int cordY1, int largura1, int altura1, int cordX2, int cordY2, int largura2, int altura2) {
        if(noAr && !naPlataforma){
            velY = velY - (0.3f * 0.5f); // Aplica gravidade
            setPosicao(posX, posY + velY);    

            animar(posicaoY, qtdFrames, incrementa, cordX1, cordY1, largura1, altura1); // Animação de queda

            if (naPlataforma) {
                velY = 0;
                noAr = false;
                setRegion(cordX2, cordY2, largura2, altura2); 
            }

        }
    }

}
