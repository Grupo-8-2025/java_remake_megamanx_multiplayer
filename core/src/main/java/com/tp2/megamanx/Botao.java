package com.tp2.megamanx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.Gdx;

public class Botao {
    
    private Rectangle bounds;
    private String texto;
    private Color corFundo;
    private Color corBorda;
    private Color corTexto;

    public Botao(float x, float y, float w, float h, String texto, Color corFundo, Color corBorda, Color corTexto) {
        this.bounds = new Rectangle(x, y, w, h);
        this.texto = texto;
        this.corFundo = corFundo;
        this.corBorda = corBorda;
        this.corTexto = corTexto;
    }

    public void desenhar(ShapeRenderer shape, SpriteBatch batch, BitmapFont font) {
        
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(corFundo);
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Line);
        shape.setColor(corBorda);
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shape.end();

        batch.begin();
        font.setColor(corTexto);
        font.draw(batch, texto, bounds.x + 20, bounds.y + bounds.height/2 + 10);
        batch.end();
    }

    public boolean foiClicado() {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            return bounds.contains(x, y);
        }
        return false;
    }

}