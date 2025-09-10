package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe base para todos os personagens do jogo (jogador e inimigos).
 * Herda de Entidade e adiciona atributos e métodos comuns a personagens,
 * como vida, dano, ataques, movimentação, dano e morte.
 */
public class Personagem extends Entidade {

    // Lista de ataques ativos disparados pelo personagem
    protected ArrayList<Ataque> ataquesAtivos;

    // Ataque atualmente selecionado ou em uso
    protected Ataque ataque;
    // Quantidade de vida do personagem
    protected int vida;
    // Dano causado por ataques do personagem
    protected int dano;
    // Velocidade horizontal
    protected float velX;
    // Velocidade vertical
    protected float velY;
    // Valor da gravidade aplicada ao personagem
    protected float gravidade;
    // Posição X do MegaMan (usado para referência em subclasses)
    protected float posXmegaMan;

    // Flags de estado do personagem
    protected boolean tomandoDano;         // Indica se está tomando dano
    protected boolean naPlataforma;        // Indica se está sobre uma plataforma
    protected boolean naParede;            // Indica se está encostado em uma parede
    protected boolean podeAndarDireita;    // Permite movimento para direita
    protected boolean podeAndarEsquerda;   // Permite movimento para esquerda
    protected boolean noAr;                // Indica se está no ar (pulando/caindo)
    protected boolean morreu;              // Indica se o personagem morreu

    /**
     * Construtor da classe Personagem.
     * Inicializa atributos básicos e flags de estado.
     * @param textura Textura do sprite
     * @param region Região da textura usada
     * @param posX Posição X inicial
     * @param posY Posição Y inicial
     * @param escala Escala do sprite
     * @param vida Vida inicial
     * @param dano Dano causado
     * @param ataque Ataque inicial
     * @param velX Velocidade X inicial
     * @param velY Velocidade Y inicial
     */
    public Personagem(Texture textura, TextureRegion region, float posX, float posY, Vector2 escala, int vida, 
    int dano, Ataque ataque, float velX, float velY) {
        super(textura, region, posX, posY, escala); // Chama construtor da superclasse Entidade
        this.ataque = ataque;
        this.vida = vida;
        this.dano = dano;
        this.velX = velX;
        this.velY = velY;
        gravidade = 0.3f; // Valor padrão da gravidade
        posXmegaMan = 0;
        tomandoDano = false;
        naPlataforma = false;
        naParede = false;
        podeAndarDireita = true;
        podeAndarEsquerda = true;
        noAr = true;
        morreu = false;
        ataquesAtivos = new ArrayList<>(); // Inicializa lista de ataques ativos
    }

    /**
     * Retorna o ataque atualmente selecionado
     */
    public Ataque getAtaque(){
        return ataque;
    }

    /**
     * Retorna a lista de ataques ativos do personagem
     */
    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    /**
     * Retorna a vida atual do personagem
     */
    public int getVida() {
        return vida;
    }

    /**
     * Retorna o dano causado pelo personagem
     */
    public int getDano() {
        return dano;
    }

    /**
     * Retorna a velocidade horizontal
     */
    public float getVelX() {
        return velX;
    }

    /**
     * Define a velocidade horizontal
     */
    public void setVelX(float velX) {
        this.velX = velX;
    }

    /**
     * Retorna a velocidade vertical
     */
    public float getVelY() {
        return velY;
    }

    /**
     * Define a velocidade vertical
     */
    public void setVelY(float velY) {
        this.velY = velY;
    }

    /**
     * Indica se o personagem morreu
     */
    public boolean isMorreu() {
        return morreu;
    }

    /**
     * Indica se o personagem está sobre uma plataforma
     */
    public boolean isNaPlataforma() {
        return naPlataforma;
    }

    /**
     * Define se o personagem está sobre uma plataforma
     */
    public void setNaPlataforma(boolean naPlataforma) {
        this.naPlataforma = naPlataforma;
    }

    /**
     * Define se pode andar para a direita
     */
    public void setPodeAndarDireita(boolean podeAndarDireita) {
        this.podeAndarDireita = podeAndarDireita;
    }

    /**
     * Define a vida do personagem
     */
    public void setVida(int vida){
        this.vida = vida;
    }

    /**
     * Define se pode andar para a esquerda
     */
    public void setPodeAndarEsquerda(boolean podeAndarEsquerda) {
        this.podeAndarEsquerda = podeAndarEsquerda;
    }

    /**
     * Indica se o personagem está encostado em uma parede
     */
    public boolean isNaParede() {
        return naParede;
    }

    /**
     * Define se o personagem está encostado em uma parede
     */
    public void setNaParede(boolean naParede) {
        this.naParede = naParede;
    }

    /**
     * Indica se o personagem está no ar
     */
    public boolean isNoAr() {
        return noAr;
    }

    /**
     * Define se o personagem está no ar
     */
    public void setNoAr(boolean noAr) {
        this.noAr = noAr;
    }
    

    /**
     * Método de movimentação (deve ser sobrescrito nas subclasses)
     */
    public void mover() {}

    /**
     * Método de ataque (deve ser sobrescrito nas subclasses)
     */
    public void atacar(){}

    /**
     * Método de morte (deve ser sobrescrito nas subclasses)
     */
    public void morrer(){}

    /**
     * Aplica dano ao personagem e ativa estado de dano
     * @param dano Valor do dano recebido
     */
    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    /**
     * Controla animação e tempo de invulnerabilidade ao tomar dano por ataque
     * @param qtdFrames Quantidade de frames da animação
     * @param incrementa Incremento do frame
     * @param cordX1 Coordenada X do frame de dano
     * @param cordY1 Coordenada Y do frame de dano
     * @param largura1 Largura do frame de dano
     * @param altura1 Altura do frame de dano
     * @param cordX2 Coordenada X do frame normal
     * @param cordY2 Coordenada Y do frame normal
     * @param largura2 Largura do frame normal
     * @param altura2 Altura do frame normal
     */
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

    /**
     * Aplica gravidade ao personagem e controla animação de queda/pulo
     * @param posicaoY Posição Y do personagem
     * @param qtdFrames Quantidade de frames da animação
     * @param incrementa Incremento do frame
     * @param cordX1 Coordenada X do frame de pulo/queda
     * @param cordY1 Coordenada Y do frame de pulo/queda
     * @param largura1 Largura do frame de pulo/queda
     * @param altura1 Altura do frame de pulo/queda
     * @param cordX2 Coordenada X do frame normal
     * @param cordY2 Coordenada Y do frame normal
     * @param largura2 Largura do frame normal
     * @param altura2 Altura do frame normal
     */
    protected void sofrerGravidade(float posicaoY, int qtdFrames, int incrementa, int cordX1, int cordY1, int largura1, int altura1, int cordX2, int cordY2, int largura2, int altura2) {
        if(noAr && !naPlataforma){
            velY = velY - (gravidade * 0.5f); // Aplica gravidade
            setPosicao(posX, posY + velY);    // Atualiza posição vertical

            animar(posicaoY, qtdFrames, incrementa, cordX1, cordY1, largura1, altura1); // Animação de pulo/queda

            if (naPlataforma) {
                velY = 0;
                noAr = false;
                setRegion(cordX2, cordY2, largura2, altura2); // Volta para frame normal
            }

        }
    }

}
