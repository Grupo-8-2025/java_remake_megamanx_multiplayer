package com.tp2.megamanx;

import com.badlogic.gdx.graphics.Texture;

/**
 * Enum que representa todos os tipos de ataques do jogo, tanto do MegaMan quanto dos inimigos.
 * Cada tipo possui atributos como dano, velocidade, textura, frames de animação e disponibilidade.
 */
public enum TipoAtaque {
    // Ataques do MegaMan
    TIRO_NORMAL(2, 5, "assets/imagens/MegaMan/tiro_normal.png", 
    1, 15, 0, 0, 15, 15),
    
    TIRO_AZUL(3, 5, "assets/imagens/MegaMan/tiro_azul.png", 
    5, 40, 0, 0, 40, 32),
    // Ataques do chefe e inimigos
    BOLA_GELO(3, 5, "assets/imagens/Fase1/shotgun.png", 
    1, 15, 0, 0, 15, 15),

    SOPRO_GELO(2, 5, "assets/imagens/Fase1/sopro.png", 
    2, 17, 0, 0, 17, 16),
   
    BOLA_NEVE(2, -5, "assets/imagens/Fase1/neve.png", 
    1, 8, 0, 32, 8, 8),

    BOMBA(3, -5, "assets/imagens/Fase2/ataque_vile.png", 
    3, 24, 0, 0, 24, 24),

    CHOQUE(3, -5, "assets/imagens/Fase2/ataque_spark.png", 
    2, 31, 0, 0, 31, 31);
    

    private final int dano;
    private final float velocidade;
    private final String caminhoTextura;

    // Parâmetros de animação 1
    private final int qtdFrames;
    private final int incrementa;
    private final int cordX;
    private final int cordY;
    private final int largura;
    private final int altura;

    private Texture textura;

    private TipoAtaque(int dano, float velocidade, String caminhoTextura, 
            int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        this.dano = dano;
        this.velocidade = velocidade;
        this.caminhoTextura = caminhoTextura;
        this.qtdFrames = qtdFrames;
        this.incrementa = incrementa;
        this.cordX = cordX;
        this.cordY = cordY;
        this.largura = largura;
        this.altura = altura;
    }

    // Métodos getters para todos os atributos do ataque
    public int getDano() {
        return dano;
    }
    public float getVelocidade() {
        return velocidade;
    }
    public int getQtdFrames() {
        return qtdFrames;
    }
    public int getIncrementa() {
        return incrementa;
    }
    public int getCordX() {
        return cordX;
    }
    public int getCordY() {
        return cordY;
    }
    public int getLargura() {
        return largura;
    }
    public int getAltura() {
        return altura;
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