package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe que representa o inimigo Trower.
 * Herda de Personagem e implementa a interface Inimigo.
 * Possui lógica de ataque à distância e movimentação simples.
 */
public class Trower extends Personagem implements Inimigo{

    /**
     * Construtor do Trower.
     * Inicializa atributos do inimigo, incluindo textura, posição, ataque e velocidades.
     * @param textura Textura do sprite do inimigo
     * @param posX Posição X inicial
     * @param posY Posição Y inicial
     * @param ataque Ataque utilizado pelo inimigo
     * @param velX Velocidade X inicial
     * @param velY Velocidade Y inicial
     */
    public Trower(Texture textura, float posX, float posY, Ataque ataque, float velX, float velY) {
        super(textura, new TextureRegion(textura, 0, 0, 35, 58), posX, posY, new Vector2(0.5f, 2.0f), 
        6, 3, ataque, velX, velY);
    }

    /**
     * Retorna o retângulo de colisão do corpo do inimigo
     */
    public Rectangle getRect(){
        return corpo.getBoundingRectangle();
    }

    /**
     * Retorna o dano causado pelo inimigo
     */
    public int getDano() {
        return dano;
    }

    /**
     * Retorna a lista de ataques ativos do inimigo
     */
    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    /**
     * Define a posição X do MegaMan (usado para IA do inimigo)
     * @param posXmegaMan Posição X do MegaMan
     */
    public void setPosXmegaMan(float posXmegaMan) {
        this.posXmegaMan = posXmegaMan;
    }

    /**
     * Aplica dano ao inimigo e ativa estado de dano
     * @param dano Valor do dano recebido
     */
    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    /**
     * Lógica de movimentação do inimigo.
     * Aplica gravidade e animação de movimento.
     */
    @Override
    public void mover(){
        sofrerGravidade(posY, 1, 35, 0, 0, 35, 58, 0, 0, 35, 58);
        animar(7, 35, 0, 0, 35, 58);
    }

    /**
     * Lógica de ataque do inimigo.
     * Dispara projétil se o MegaMan estiver próximo e respeitando o tempo de recarga.
     */
    @Override
    public void atacar(){
        if(Math.abs(posXmegaMan - posX) < 600){
            iterarDeltaTime();
            if(deltaTime >= 8f){
                Rectangle rect = corpo.getBoundingRectangle();
                float posXataque = rect.x - rect.width - 10;
                float posYataque = rect.y + rect.height/1.5f;
                Ataque novoAtaque = new Ataque(
                    new TextureRegion(ataque.getTipo().getTextura(),
                    ataque.getTipo().getCordX1(), ataque.getTipo().getCordY1(),
                    ataque.getTipo().getLargura1(), ataque.getTipo().getAltura1()),
                    posXataque, posYataque, new Vector2(0.2f, 1f), 
                    ataque.getTipo(), ataque.getTipo().getVelocidade()
                );
                novoAtaque.setColidiu(false);
                novoAtaque.setPodeDisparar(true);
                ataquesAtivos.add(novoAtaque);
                deltaTime = 0f;
            }
        }
    }
    
    /**
     * Lógica de morte do inimigo.
     * Ativa animação de morte e remove do cenário.
     */
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