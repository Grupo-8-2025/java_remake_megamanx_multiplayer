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

public class TelaGameOver implements Screen {
    private Jogo jogo;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Texture imgFundo;
    // Fonte para desenhar textos
    private BitmapFont font;
    // Gerador de fonte TrueType
    private FreeTypeFontGenerator fontGenerator;
    // Parâmetros para geração da fonte
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private Botao botaoJogarNovamente, botaoInicio;

    public TelaGameOver(Jogo jogo) {
        this.jogo = jogo;
        batch = new SpriteBatch(); 
        shapeRenderer = new ShapeRenderer(); 
        imgFundo = new Texture("assets/imagens/Mega_Man_X_Logo.png");

        // Configuração da fonte personalizada
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        botaoJogarNovamente = new Botao(300, 250, 280, 40, "Jogar Novamente", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
        botaoInicio = new Botao(300, 140, 280, 40, "Voltar para o Início", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(imgFundo, 0, 300);
        font.draw(batch, "GAME OVER", 350, 400);
        batch.end();

        botaoJogarNovamente.desenhar(shapeRenderer, batch, font);
        botaoInicio.desenhar(shapeRenderer, batch, font);

        if (botaoJogarNovamente.foiClicado()) {
            jogo.reset();
            jogo.setSegundaFaseAtivada(false);
            jogo.setJogoIniciado(true);
            jogo.setScreen(null);
        }

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