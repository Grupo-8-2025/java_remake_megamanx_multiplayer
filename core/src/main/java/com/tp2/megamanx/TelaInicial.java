package com.tp2.megamanx;

import java.lang.reflect.Array;
import java.util.ArrayList;

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

    //Inicia variaveis do jogo
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
    private boolean modoSelecionado = false;

    //Cria a tela inicial
    public TelaInicial(Jogo jogo){
        this.jogo = jogo;
    }

    //Cria os elementos da tela inicial
    private void create() {
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("assets/GAMERIA2.ttf"));
        fontParameter = new FreeTypeFontParameter();
        fontParameter.size = 20;
        font = fontGenerator.generateFont(fontParameter);

        spriteBatch = new SpriteBatch();
        portTela = new FitViewport(800, 500);//Define o tamanho da tela
        imgFundoMegaManX = new Texture("assets/imagens/TelaInicial/Mega_Man_X_Logo.png");
        //imgCarregando = new Texture("winXPLoading.jpeg");
    }

    // Desenha a tela inicial
    void desenharTelaInicial(BitmapFont font, SpriteBatch spriteBatch, Texture imgFundoMegaManX, float worldHeight) {
        spriteBatch.draw(imgFundoMegaManX, 0, worldHeight - 250, 450, 220);
        font.setColor(Color.SKY);

        font.draw(spriteBatch, "MEGA MAN X", 10, worldHeight - 300);
        font.draw(spriteBatch, "APERTE ENTER PARA COMECAR", 10, worldHeight - 320);
        font.draw(spriteBatch, "Digite seu nome:", 10, worldHeight - 350);
        // (digitandoNome ? "|" : "") faz com que o cursor (|), caso digitandoNome for verdade, pisque enquanto o jogador digita o nome
        font.draw(spriteBatch, nomeJogador + (digitandoNome ? "|" : ""), 10, worldHeight - 370);
    }

    // Desenha a tela de seleção de modo
    public void desenharTelaSelecaoModo(BitmapFont font, SpriteBatch spriteBatch, float worldHeight) {
        font.setColor(Color.SKY);
        font.draw(spriteBatch, "SELECIONE O MODO DE JOGO", 10, worldHeight - 300);
        font.draw(spriteBatch, "1 - Modo Singleplayer", 10, worldHeight - 320);
        font.draw(spriteBatch, "2 - Modo Multiplayer (Servidor)", 10, worldHeight - 340);
        font.draw(spriteBatch, "3 - Modo Multiplayer (Cliente)", 10, worldHeight - 360);
    }

    // Verifica se o jogador escolheu o modo de jogo
    public void verificaSelecaoModo() {
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_1)) {
            jogo.setIsMultiplayer(false);
            digitandoNome = false; // Sai da tela inicial
            modoSelecionado = true;
            jogo.setJogoIniciado(true);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_2)) {
            System.out.println("Apertou 2");
            jogo.setIsMultiplayer(true);
            jogo.setIsServer(true);
            digitandoNome = false; // Sai da tela inicial
            modoSelecionado = true;
            jogo.setJogoIniciado(true);
        } else if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.NUM_3)) {
            jogo.setIsMultiplayer(true);
            jogo.setIsServer(false);
            digitandoNome = false; // Sai da tela inicial
            modoSelecionado = true;
            jogo.setJogoIniciado(true);
        }
        
    }

    // Mostra a tela inicial
    @Override
    public void show (){
        create();
    }

    // Desenha a tela
    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        portTela.apply();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        spriteBatch.begin(); // <-- Comeca a desenhar

        float worldWidth = portTela.getWorldWidth();
        float worldHeight = portTela.getWorldHeight();

        /*--------------------------------------------------------------
        *
        * Verifica se o jogo ja colocou o nome e apertou enter
        *                                   
        *--------------------------------------------------------------
        */

        // Se apertar enter, para de desenhar a tela inicial
        if (!digitandoNome) {
            desenharTelaSelecaoModo(font, spriteBatch, worldHeight);
            if (!modoSelecionado) {
                verificaSelecaoModo();
            } else {
                jogo.setScreen(null);
            }  
        } 
        // Se nao, desenha a tela inicial
        else {
            desenharTelaInicial(font, spriteBatch, imgFundoMegaManX, worldHeight);
        }

        spriteBatch.end();// <-- Termina de desenhar
    }

    /*--------------------------------------------------------------
    *
    * Funcao que verifica a digitacao do nome do jogador
    *
    *--------------------------------------------------------------
    */
    private void verificaDigitacaoNome() {
        // Verifica se o jogador esta digitando o nome
        // Se o jogador está digitando o nome
        if (digitandoNome) {
            // Verifica se alguma tecla de A a Z foi pressionada
            for (int i = 0; i < 26; i++) {
                if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.A + i)) {
                    // Adiciona a letra correspondente ao nome do jogador
                    nomeJogador += (char) ('A' + i);
                }
            }
            // Verifica se a tecla BACKSPACE foi pressionada e remove o último caractere do nome
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.BACKSPACE) && nomeJogador.length() > 0) {
            nomeJogador = nomeJogador.substring(0, nomeJogador.length() - 1);
            }
            // Verifica se a tecla ENTER foi pressionada e o nome não está vazio, finalizando a digitação
            if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ENTER) && nomeJogador.length() > 0) {
            digitandoNome = false;
            }
        }
    }

    // Renderiza a tela
    @Override
	public void render (float delta){
        if (digitandoNome) {
            verificaDigitacaoNome();
        }
        draw();
    }

    // Redimensiona a tela
	public void resize(int width, int height) {
        portTela.update(width, height);
    }

	public void pause (){}

	public void resume (){}

	public void hide (){}

    // Descarrega os recursos
	@Override
    public void dispose() {
        spriteBatch.dispose();
        imgFundoMegaManX.dispose();
        fontGenerator.dispose();
    }

}
