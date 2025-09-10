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

/**
 * Tela exibida quando o jogador perde o jogo (Game Over).
 * Permite reiniciar o jogo ou voltar para a tela inicial.
 */
public class TelaGameOver implements Screen {
    // Referência para o jogo principal
    private Jogo jogo;
    // Responsável por desenhar sprites na tela
    private SpriteBatch batch;
    // Responsável por desenhar formas geométricas (usado nos botões)
    private ShapeRenderer shapeRenderer;
    // Imagem de fundo da tela de Game Over
    private Texture imgFundo;
    // Fonte para desenhar textos
    private BitmapFont font;
    // Gerador de fonte TrueType
    private FreeTypeFontGenerator fontGenerator;
    // Parâmetros para geração da fonte
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    // Botão para jogar novamente
    private Botao botaoJogarNovamente, botaoInicio;

    /**
     * Construtor da tela de Game Over.
     * Inicializa recursos gráficos, fonte e botões.
     * @param jogo Referência para o jogo principal
     */
    public TelaGameOver(Jogo jogo) {
        this.jogo = jogo;
        batch = new SpriteBatch(); // Inicializa batch para desenhar sprites
        shapeRenderer = new ShapeRenderer(); // Inicializa shapeRenderer para desenhar botões
        imgFundo = new Texture("imagens/TelaInicial/Mega_Man_X_Logo.png"); // Imagem de fundo

        // Configuração da fonte personalizada
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 24;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        // Inicializa botões com posição, tamanho, texto e cores
        botaoJogarNovamente = new Botao(300, 250, 280, 40, "Jogar Novamente", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
        botaoInicio = new Botao(300, 140, 280, 40, "Voltar para o Início", Color.DARK_GRAY, Color.BLUE, Color.WHITE);
    }

    /**
     * Método chamado a cada frame para desenhar e atualizar a tela.
     * @param delta Tempo desde o último frame
     */
    @Override
    public void render(float delta) {
        // Limpa a tela com cor preta
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Desenha imagem de fundo e texto "GAME OVER"
        batch.begin();
        batch.draw(imgFundo, 0, 300);
        font.draw(batch, "GAME OVER", 350, 400);
        batch.end();

        // Desenha botões na tela
        botaoJogarNovamente.desenhar(shapeRenderer, batch, font);
        botaoInicio.desenhar(shapeRenderer, batch, font);

        // Verifica se o botão "Jogar Novamente" foi clicado
        if (botaoJogarNovamente.foiClicado()) {
            jogo.reset(); // Reinicia o jogo
            jogo.setScreen(null); // Volta para o jogo
        }
        // Verifica se o botão "Voltar para o Início" foi clicado
        if (botaoInicio.foiClicado()) {
            jogo.reset(); // Reinicia o jogo
            jogo.setScreen(new TelaInicial(jogo)); // Vai para tela inicial
        }
    }

    // Métodos obrigatórios da interface Screen (não utilizados nesta tela)
    @Override public void resize(int width, int height) {}
    @Override public void show() {}
    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}

    /**
     * Libera recursos gráficos utilizados pela tela
     */
    @Override
    public void dispose() {
        batch.dispose();
        imgFundo.dispose();
        shapeRenderer.dispose();
        font.dispose();
        fontGenerator.dispose();
    }
}