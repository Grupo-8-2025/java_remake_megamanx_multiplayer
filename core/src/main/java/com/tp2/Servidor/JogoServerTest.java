package com.tp2.Servidor;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryonet.Server;
import com.tp2.megamanx.Ataque;
import com.tp2.megamanx.GerenciadorColisoes;
import com.tp2.megamanx.Inimigo;
import com.tp2.megamanx.Jaminger;
import com.tp2.megamanx.JogoCliente;
import com.tp2.megamanx.Mapa;
import com.tp2.megamanx.MegaMan;
import com.tp2.megamanx.Personagem;
import com.tp2.megamanx.Pinguim;
import com.tp2.megamanx.TelaGameOver;
import com.tp2.megamanx.TelaVitoria;
import com.tp2.megamanx.TipoAtaque;
import com.tp2.megamanx.Trower;
import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;

public class JogoServerTest {

    //Server server;
    private Network network;
    private InformacoesServidor info = new InformacoesServidor();
    private PersonagemIterator personagens;
    private Pinguim penguin;
    private MegaMan megaMan;

    private static volatile boolean headlessInitialized;
    private static HeadlessApplication headlessApplication;

    public JogoServerTest() {
        network = new Network(this);
    }

    private static void ensureHeadlessEnvironment() {
        if (headlessInitialized) {
            return;
        }
        synchronized (JogoServerTest.class) {
            if (headlessInitialized) {
                return;
            }
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            headlessApplication = new HeadlessApplication(new ApplicationAdapter() {}, config);
            headlessInitialized = true;
        }
    }

    private void criaObjetosJogo(){
        info.random = new Random();
        info.posicoesValidas = new ArrayList<>();
        info.inimigos = new InimigoIterator();
        //network = new Network(this);
        criaPersonagens();
    }

    private void criaPersonagens(){
        criarInimigos();
        info.personagens.add(info.penguin);
    }

    private void criarInimigos(){
        // Remove texture loading as it requires graphics context
        //info.texturaPenguin = new Texture("assets/imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        //info.texturaJaminger = new Texture("assets/imagens/ChilPenguin/inimigos/Jaminger/jaminger.png"); // Add missing texture
        //info.texturaTrower = new Texture("assets/imagens/ChilPenguin/inimigos/Trower/trower.png"); // Add missing texture
        // Load TipoAtaque textures (adjust paths)
        //TipoAtaque.BOLA_NEVE.setTextura(new Texture("assets/imagens/ataques/bola_neve.png"));
        //TipoAtaque.DISCO.setTextura(new Texture("assets/imagens/ataques/disco.png"));

        //info.texturaPenguin = new Texture("assets/imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        info.penguin = new Pinguim(new Rectangle(602, 16, 43, 44), 11635, 3000);
        //info.penguin = new Pinguim(info.texturaPenguin, 500, 2517);
        info.inimigos.add(info.penguin);

        Ataque ataqueTrower = new Ataque(new Rectangle(0, 32, 8, 8), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.BOLA_NEVE, -5);

        Ataque ataqueJaminger = new Ataque(new Rectangle(0, 0, 15, 15), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.DISCO, -5);
            
        determinarPosicoesValidas();

        info.personagens = new PersonagemIterator();
        
        int indexPosicaoAnterior = -1;
        for(int i=0; i<15; i++){
            int indexPosicao = info.random.nextInt(info.posicoesValidas.size());

            if(indexPosicaoAnterior == -1 || Math.abs(info.posicoesValidas.get(indexPosicao).x - info.posicoesValidas.get(indexPosicaoAnterior).x) > 800){
                int sortearPersonagem = info.random.nextInt(2);
                if(sortearPersonagem == 0){
                    Jaminger jaminger = new Jaminger(new Rectangle(390, 0, 39, 75), 0, 
                    0, ataqueJaminger, 0, 5);

                    float posX = info.posicoesValidas.get(indexPosicao).x + jaminger.getContactArea().width;
                    float posY = info.posicoesValidas.get(indexPosicao).y;
                    jaminger.setPosicao(posX, posY);

                    info.inimigos.add(jaminger);
                    info.personagens.add(jaminger);
                }else{
                    Trower trower = new Trower(new Rectangle(0, 0, 35, 58), 0, 
                    0, ataqueTrower, 0, 5);

                    float posX = info.posicoesValidas.get(indexPosicao).x - trower.getContactArea().width;
                    float posY = info.posicoesValidas.get(indexPosicao).y;
                    
                    trower.setPosicao(posX, posY);
                    info.inimigos.add(trower);
                    info.personagens.add(trower);
                }
            }
            indexPosicaoAnterior = indexPosicao;
        }
        
    }

    public InformacoesServidor getInfoServidor() {
        return info;
    }

    /* */
    private void determinarPosicoesValidas(){
        info.posicoesValidas = new ArrayList<Vector2>();
        /* 
        info.mapa = new Mapa("maps/Mapa.tmx", 800, 600, true);
        info.posicoesValidas.clear();
        for(Rectangle plataforma : info.mapa.getChaos()){
                float posYplataforma = plataforma.y + plataforma.height;
                float posXplataforma = plataforma.x + plataforma.width;
                info.posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
        }*/
        info.posicoesValidas.add(new Vector2(100, 50));
        info.posicoesValidas.add(new Vector2(300, 50));
        info.posicoesValidas.add(new Vector2(500, 50));
        info.posicoesValidas.add(new Vector2(700, 50));
        info.posicoesValidas.add(new Vector2(900, 50));
        System.out.println("Posições válidas encontradas: " + info.posicoesValidas.size());
    }

    public void update(float delta) {
        network.sendInformacoesServidor();
    }

    public void reset(){
        info.gameOver = false;
        dispose();
    }

    public void dispose() {
        network.dispose();
    }

    public InimigoIterator getInimigos() {
        return info.inimigos;
    }

    public MegaMan getMegaMan() {
        return megaMan;
    }

    public void setMegaMan(MegaMan megaMan) {
        this.megaMan = megaMan;
    }

    public static void main(String[] args) {
        JogoServerTest server = new JogoServerTest();
        server.criaObjetosJogo();
        float delta = 1 / 60f; // 60 FPS
        while (true) {
            server.update(delta);
            try {
                Thread.sleep((long) (delta * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
