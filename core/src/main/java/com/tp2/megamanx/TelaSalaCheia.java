package com.tp2.megamanx;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class TelaSalaCheia implements Screen {
    private Jogo jogo;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture imgFundo;
    private BitmapFont font;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private Botao botaoInicio;
    private String message;

    public TelaSalaCheia(Jogo jogo, String message) {
        this.jogo = jogo;
        this.message = message == null ? "Nao foi possivel conectar, a sala ja tem 2 jogadores!" : message;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        imgFundo = new Texture("assets/imagens/mega_man_logo.png");

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        botaoInicio = new Botao(300, 140, 280, 40, "Voltar para o Início", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(imgFundo, 0, 300);
        font.draw(batch, "", 0, 0);
        float x = 150;
        float y = 420;
        font.draw(batch, message, x, y);
        batch.end();

        botaoInicio.desenhar(shapeRenderer, batch, font);

        if (botaoInicio.foiClicado()) {
            jogo.reset();
            jogo.setSegundaFaseAtivada(false);
            jogo.setScreen(new TelaInicial(jogo));
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
