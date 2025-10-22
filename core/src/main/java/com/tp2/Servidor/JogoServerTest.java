package com.tp2.Servidor;

import java.util.ArrayList;
import java.util.Random;

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

public class JogoServerTest {

    //Server server;
    private Network network;
    private InformacoesServidor info = new InformacoesServidor();
    private PersonagemIterator personagens;
    private Pinguim penguin;
    private MegaMan megaMan;

    public JogoServerTest() {
        network = new Network(this);
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
        personagens.add(info.penguin);
    }

    private void criarInimigos(){
        //info.texturaPenguin = new Texture("assets/imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        //info.texturaJaminger = new Texture("assets/imagens/ChilPenguin/inimigos/Jaminger/jaminger.png"); // Add missing texture
        //info.texturaTrower = new Texture("assets/imagens/ChilPenguin/inimigos/Trower/trower.png"); // Add missing texture
        // Load TipoAtaque textures (adjust paths)
        //TipoAtaque.BOLA_NEVE.setTextura(new Texture("assets/imagens/ataques/bola_neve.png"));
        //TipoAtaque.DISCO.setTextura(new Texture("assets/imagens/ataques/disco.png"));

        //info.texturaPenguin = new Texture("assets/imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        info.penguin = new Pinguim(info.texturaPenguin, 11635, 3000);
        //info.penguin = new Pinguim(info.texturaPenguin, 500, 2517);
        info.inimigos.add(info.penguin);

        Ataque ataqueTrower = new Ataque(new TextureRegion(TipoAtaque.BOLA_NEVE.getTextura(),
		TipoAtaque.BOLA_NEVE.getCordX1(), TipoAtaque.BOLA_NEVE.getCordY1(),
		TipoAtaque.BOLA_NEVE.getLargura1(), TipoAtaque.BOLA_NEVE.getAltura1()), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.BOLA_NEVE, -5);

        Ataque ataqueJaminger = new Ataque(new TextureRegion(TipoAtaque.DISCO.getTextura(), 
		TipoAtaque.DISCO.getCordX1(), TipoAtaque.DISCO.getCordY1(),
		TipoAtaque.DISCO.getLargura1(), TipoAtaque.DISCO.getAltura1()), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.DISCO, -5);
            
        determinarPosicoesValidas();

        int indexPosicaoAnterior = -1;
        for(int i=0; i<15; i++){
            int indexPosicao = info.random.nextInt(info.posicoesValidas.size());

            if(indexPosicaoAnterior == -1 || Math.abs(info.posicoesValidas.get(indexPosicao).x - info.posicoesValidas.get(indexPosicaoAnterior).x) > 800){
                int sortearPersonagem = info.random.nextInt(2);
                if(sortearPersonagem == 0){
                    Jaminger jaminger = new Jaminger(info.texturaJaminger, 0, 
                    0, ataqueJaminger, 0, 5);

                    float posX = info.posicoesValidas.get(indexPosicao).x + jaminger.getCorpo().getBoundingRectangle().width;
                    float posY = info.posicoesValidas.get(indexPosicao).y;
                    jaminger.setPosicao(posX, posY);

                    info.inimigos.add(jaminger);
                    info.personagens.add(jaminger);
                }else{
                    Trower trower = new Trower(info.texturaTrower, 0, 
                    0, ataqueTrower, 0, 5);

                    float posX = info.posicoesValidas.get(indexPosicao).x - trower.getCorpo().getBoundingRectangle().width;
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

    private void determinarPosicoesValidas(){
    info.posicoesValidas.clear();
    for(Rectangle plataforma : info.mapa.getChaos()){
            float posYplataforma = plataforma.y + plataforma.height;
            float posXplataforma = plataforma.x + plataforma.width;
            info.posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
    }
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
        // Simple game loop for server (adjust delta as needed)
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
