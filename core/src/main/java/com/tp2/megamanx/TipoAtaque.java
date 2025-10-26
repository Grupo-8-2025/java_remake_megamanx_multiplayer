package com.tp2.megamanx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Enum que representa todos os tipos de ataques do jogo, tanto do MegaMan quanto dos inimigos.
 * Cada tipo possui atributos como dano, velocidade, textura, frames de animação e disponibilidade.
 */
public enum TipoAtaque {
    // Ataques do MegaMan
    TIRO_NORMAL(2, 5, "assets/imagens/MegaMan/tiro_normal.png", 
    1, 15, 0, 0, 15, 15, 3, 15, 15, 0, 15, 15),
    
    TIRO_AZUL(3, 5, "assets/imagens/MegaMan/tiro_azul.png", 
    5, 40, 0, 0, 40, 32, 5, 40, 200, 0, 40, 32),
    // Ataques do chefe e inimigos
    BOLA_GELO(3, 5, "assets/imagens/Fase1/shotgun.png", 
    1, 15, 0, 0, 15, 15, 1, 0, 0, 0, 0, 0),

    SOPRO_GELO(2, 5, "assets/imagens/Fase1/sopro.png", 
    2, 17, 0, 0, 17, 16, 2, 17, 0, 0, 17, 16),
   
    BOLA_NEVE(2, -5, "assets/imagens/Fase1/neve.png", 
    1, 8, 0, 32, 8, 8, 3, 40, 40, 0, 40, 40),
    
    DISCO(3, -5, "assets/imagens/Fase1/disco.png", 
    1, 15, 0, 0, 15, 15, 1, 15, 15, 0, 15, 15),

    BOMBA(3, -5, "assets/imagens/Fase2/ataque_vile.png", 
    1, 15, 0, 0, 15, 15, 1, 15, 15, 0, 15, 15),

    CHOQUE(3, -5, "assets/imagens/Fase2/ataque_spark.png", 
    1, 15, 0, 0, 15, 15, 1, 15, 15, 0, 15, 15);
    

    private final int dano;
    private final float velocidade;
    private final String caminhoTextura;

    // Parâmetros de animação 1
    private final int qtdFrames1;
    private final int incrementa1;
    private final int cordX1;
    private final int cordY1;
    private final int largura1;
    private final int altura1;

    // Parâmetros de animação 2
    private final int qtdFrames2;
    private final int incrementa2;
    private final int cordX2;
    private final int cordY2;
    private final int largura2;
    private final int altura2;

    private Texture textura;

    private TipoAtaque(int dano, float velocidade, String caminhoTextura, 
            int qtdFrames1, int incrementa1, int cordX1, int cordY1, int largura1, int altura1, 
            int qtdFrames2, int incrementa2, int cordX2, int cordY2, int largura2, int altura2) {
        this.dano = dano;
        this.velocidade = velocidade;
        this.caminhoTextura = caminhoTextura;
        this.qtdFrames1 = qtdFrames1;
        this.incrementa1 = incrementa1;
        this.cordX1 = cordX1;
        this.cordY1 = cordY1;
        this.largura1 = largura1;
        this.altura1 = altura1;
        this.qtdFrames2 = qtdFrames2;
        this.incrementa2 = incrementa2;
        this.cordX2 = cordX2;
        this.cordY2 = cordY2;
        this.largura2 = largura2;
        this.altura2 = altura2;
    }

    // Métodos getters para todos os atributos do ataque
    public int getDano() {
        return dano;
    }
    public float getVelocidade() {
        return velocidade;
    }
    public int getQtdFrames1() {
        return qtdFrames1;
    }
    public int getIncrementa1() {
        return incrementa1;
    }
    public int getCordX1() {
        return cordX1;
    }
    public int getCordY1() {
        return cordY1;
    }
    public int getLargura1() {
        return largura1;
    }
    public int getAltura1() {
        return altura1;
    }
    public int getQtdFrames2() {
        return qtdFrames2;
    }
    public int getIncrementa2() {
        return incrementa2;
    }
    public int getCordX2() {
        return cordX2;
    }
    public int getCordY2() {
        return cordY2;
    }
    public int getLargura2() {
        return largura2;
    }
    public int getAltura2() {
        return altura2;
    }
    public Texture getTextura() {
        return textura;
    }

    /**
     * Carrega a textura do ataque se ainda não estiver carregada
     */
    private void carregarTextura() {
        if (textura == null) {
            this.textura = new Texture(caminhoTextura);
        }
    }

    /**
     * Libera a textura da memória
     */
    private void disposeTextura(){
        this.textura.dispose();
    }

    /**
     * Carrega todas as texturas de todos os tipos de ataque
     */
    public static void carregarTodasTexturas() {
        for (TipoAtaque ataque : values()) {
            ataque.carregarTextura();
        }
    }

    /**
     * Libera todas as texturas de todos os tipos de ataque
     */
    public static void disposeTodasTexturas(){
        for (TipoAtaque ataque : values()) {
            ataque.disposeTextura();
        }
    }

}