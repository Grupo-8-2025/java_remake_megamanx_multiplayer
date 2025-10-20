package com.tp2.megamanx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TelaOpcaoMultiplayer implements Screen {
    private Jogo jogo;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture imgFundo;
    private BitmapFont font;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private Botao botaoJogarSozinho, botaoJogarEmLAN;

    public TelaOpcaoMultiplayer(Jogo jogo) {
        this.jogo = jogo;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        imgFundo = new Texture("imagens/TelaInicial/Mega_Man_X_Logo.png");

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        botaoJogarSozinho = new Botao(300, 250, 280, 40, "Jogar Sozinho", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
        botaoJogarEmLAN = new Botao(300, 140, 280, 40, "Jogar em LAN", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(imgFundo, 0, 300);
        font.draw(batch, "SEJA BEM VINDO AO JOGO!", 350, 400);
        batch.end();

        botaoJogarSozinho.desenhar(shapeRenderer, batch, font);
        botaoJogarEmLAN.desenhar(shapeRenderer, batch, font);

        if (botaoJogarSozinho.foiClicado()) {
            jogo.setScreen(new TelaInicial(jogo));
        }
        if (botaoJogarEmLAN.foiClicado()) {
            jogo.setScreen(new TelaConfiguracaoConexao(jogo));
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override
    public void dispose() {
        batch.dispose();
        imgFundo.dispose();
        shapeRenderer.dispose();
        font.dispose();
        fontGenerator.dispose();
    }
}