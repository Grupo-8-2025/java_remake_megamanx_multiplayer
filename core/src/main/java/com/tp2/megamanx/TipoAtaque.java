package com.tp2.megamanx;

import com.badlogic.gdx.graphics.Texture;

public enum TipoAtaque {
    TIRO_NORMAL(2, "assets/imagens/MegaMan/tiro_normal.png", 
    1, 15, 0, 0, 15, 15),
    
    TIRO_AZUL(3, "assets/imagens/MegaMan/tiro_azul.png", 
    5, 40, 0, 0, 40, 32),

    BOLA_GELO(3, "assets/imagens/Fase1/shotgun.png", 
    1, 15, 0, 0, 15, 15),

    SOPRO_GELO(2, "assets/imagens/Fase1/sopro.png", 
    2, 17, 0, 0, 17, 16),

    BOMBA(3, "assets/imagens/Fase2/ataque_vile.png", 
    3, 24, 0, 0, 24, 24),

    CHOQUE(3, "assets/imagens/Fase2/ataque_spark.png", 
    2, 31, 0, 0, 31, 31);
    

    private final int dano;
    private final String caminhoTextura;

    private final int qtdFrames;
    private final int incrementa;
    private final int cordX;
    private final int cordY;
    private final int largura;
    private final int altura;

    private Texture textura;

    private TipoAtaque(int dano, String caminhoTextura, 
            int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        this.dano = dano;
        this.caminhoTextura = caminhoTextura;
        this.qtdFrames = qtdFrames;
        this.incrementa = incrementa;
        this.cordX = cordX;
        this.cordY = cordY;
        this.largura = largura;
        this.altura = altura;
    }

    public int getDano() {
        return dano;
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


    private void carregarTextura() {
        if (textura == null) {
            this.textura = new Texture(caminhoTextura);
        }
    }

    private void disposeTextura(){
        this.textura.dispose();
    }

    public static void carregarTodasTexturas() {
        for (TipoAtaque ataque : values()) {
            ataque.carregarTextura();
        }
    }

    public static void disposeTodasTexturas(){
        for (TipoAtaque ataque : values()) {
            ataque.disposeTextura();
        }
    }

}