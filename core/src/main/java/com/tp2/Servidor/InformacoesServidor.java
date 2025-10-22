package com.tp2.Servidor;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.tp2.megamanx.GerenciadorColisoes;
import com.tp2.megamanx.Mapa;
import com.tp2.megamanx.MegaMan;
import com.tp2.megamanx.Pinguim;
import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;

public class InformacoesServidor {
    public Texture texturaMegaMan;
    public Texture texturaPenguin;
    public Texture texturaTrower;
    public Texture texturaJaminger;
    public Texture texturaFundo;

    public SpriteBatch batch;
    public OrthographicCamera camera;
    public Vector2 cameraFoco;
    public Viewport viewport;
    public BitmapFont fonteVida;
    public FreeTypeFontGenerator gerador;
    public FreeTypeFontGenerator.FreeTypeFontParameter parametro;

    public ArrayList<Vector2> posicoesValidas;
    public Random random;

    public Mapa mapa;

    public GerenciadorColisoes gerenciadorColisoes;
    public InimigoIterator inimigos;
    public PersonagemIterator personagens;

    public MegaMan megaMan;
    public int vidasMegaMan;
    public boolean gameOver;
    public Pinguim penguin;

    public Sound somMorte, somVitoria, somPadrao;

    public ShapeRenderer shapeRenderer;

    public String nomeJogador, nomeHost;

}
