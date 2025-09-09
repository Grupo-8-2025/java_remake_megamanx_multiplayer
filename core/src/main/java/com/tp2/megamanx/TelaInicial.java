package com.tp2.megamanx;

//import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class TelaInicial implements Screen {

    private Texture imgFundoMegaManX;
    private Texture imgCarregando;
    private FitViewport portTela;
    private SpriteBatch spriteBatch;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontParameter fontParameter;
    private BitmapFont font;
    private boolean apertarEnter = false;
    private float tempo = 0;
    private String nomeJogador = "";
    private boolean digitandoNome = true;
    private Jogo jogo;

    
    public TelaInicial(Jogo jogo){
        this.jogo = jogo;
    }

    private void create() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 20;
        font = fontGenerator.generateFont(fontParameter);

        spriteBatch = new SpriteBatch();
        portTela = new FitViewport(800, 500);
        imgFundoMegaManX = new Texture("imagens/TelaInicial/Mega_Man_X_Logo.png");
        //imgCarregando = new Texture("winXPLoading.jpeg");
    }

    void desenharTelaInicial(BitmapFont font, SpriteBatch spriteBatch, Texture imgFundoMegaManX, float worldHeight) {
        spriteBatch.draw(imgFundoMegaManX, 0, worldHeight - 250, 450, 220);
        font.setColor(Color.SKY);

        font.draw(spriteBatch, "MEGA MAN X", 10, worldHeight - 300);
        font.draw(spriteBatch, "APERTE ENTER PARA COMECAR", 10, worldHeight - 320);
        font.draw(spriteBatch, "Digite seu nome:", 10, worldHeight - 350);
        font.draw(spriteBatch, nomeJogador + (digitandoNome ? "|" : ""), 10, worldHeight - 370);
    }

    @Override
    public void show (){
        create();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        portTela.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        spriteBatch.begin();

        float worldWidth = portTela.getWorldWidth();
        float worldHeight = portTela.getWorldHeight();

        if (!digitandoNome) {
            jogo.setScreen(null);
            
        } else {
            desenharTelaInicial(font, spriteBatch, imgFundoMegaManX, worldHeight);
        }

        spriteBatch.end();
    }

    private void verificaDigitacaoNome() {
        if (digitandoNome) {
            for (int i = 0; i < 26; i++) {
                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A + i)) {
                    nomeJogador += (char) ('A' + i);
                }
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACKSPACE) && nomeJogador.length() > 0) {
                nomeJogador = nomeJogador.substring(0, nomeJogador.length() - 1);
            }
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) && nomeJogador.length() > 0) {
                digitandoNome = false;
            }
        }
    }

    @Override
	public void render (float delta){
        if (digitandoNome) {
            verificaDigitacaoNome();
        }
        draw();
    }

	public void resize(int width, int height) {
        portTela.update(width, height);
    }

	public void pause (){}

	public void resume (){}

	public void hide (){}

	@Override
    public void dispose() {
        spriteBatch.dispose();
        imgFundoMegaManX.dispose();
        fontGenerator.dispose();
    }

}
