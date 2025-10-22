/*package com.tp2.Servidor;

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

public class JogoServer {

    //Server server;
    private Network network;
    private InformacoesServidor info = new InformacoesServidor();
    /* 
    private Texture texturaMegaMan;
    private Texture texturaPenguin;
    private Texture texturaTrower;
    private Texture texturaJaminger;
    private Texture texturaFundo;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector2 cameraFoco;
    private Viewport viewport;
    private BitmapFont fonteVida;
    private FreeTypeFontGenerator gerador;
    private FreeTypeFontGenerator.FreeTypeFontParameter parametro;

    private ArrayList<Vector2> posicoesValidas;
    private Random random;

    private Mapa mapa;

    private GerenciadorColisoes gerenciadorColisoes;
    private InimigoIterator inimigos;
    private PersonagemIterator personagens;

    private MegaMan megaMan;
    private int vidasMegaMan = 3;
    private boolean gameOver = false;
    private Pinguim penguin;

    private Sound somMorte, somVitoria, somPadrao;

    private ShapeRenderer shapeRenderer;

    private String nomeJogador, nomeHost;

    private JogoCliente jogoCliente;
    

    public JogoServer() {
        network = new Network(this);
        /* 
        server = new Server();
        Network.register(server);
        try {
            server.bind(Network.port);
            server.start();
            System.out.println("Servidor iniciado na porta " + Network.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void criaObjetosJogo(){
        info.vidasMegaMan = 3;
        info.gameOver = false;

        info.batch = new SpriteBatch();
        info.camera = new OrthographicCamera();
        info.cameraFoco = new Vector2();
        info.camera.setToOrtho(false, 800, 600);
        info.viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        info.shapeRenderer = new ShapeRenderer();
        info.gerador = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        info.parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
        info.parametro.size = 24;
        info.parametro.color = Color.WHITE;
        info.fonteVida = info.gerador.generateFont(info.parametro);
        info.gerador.dispose();

        info.somMorte = Gdx.audio.newSound(Gdx.files.internal("sons/megaman-x-death-sound-effect.mp3"));
        info.somVitoria = Gdx.audio.newSound(Gdx.files.internal("sons/mmx-stage-clear.mp3"));
        info.somPadrao = Gdx.audio.newSound(Gdx.files.internal("sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));


        info.random = new Random();
        info.posicoesValidas = new ArrayList<>();

        info.gerenciadorColisoes = new GerenciadorColisoes();
        info.inimigos = new InimigoIterator();
        info.personagens = new PersonagemIterator();

        carregaTexturas();
        criaMapa();
        criaPersonagens();

        info.somPadrao.play(info.somPadrao.loop());
        info.nomeJogador = new String();
        network = new Network(this);
    }

    public void setNomeJogador(String nome){
        this.info.nomeJogador = nome;
    }

    public void setNomeHost(String host){
        this.info.nomeHost = host;
    }

    private void criaMapa(){
        info.mapa = new Mapa("maps/Mapa.tmx", 800, 600);
    }

    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();
        info.texturaMegaMan = new Texture("imagens/MegaMan/megaMan.png");
        info.texturaPenguin = new Texture("imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        info.texturaTrower = new Texture("imagens/ChilPenguin/inimigos/now.png");
        info.texturaJaminger = new Texture("imagens/ChilPenguin/inimigos/jaminger.png");
        info.texturaFundo = new Texture("fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg");
    }

    private void criaPersonagens(){
        criarInimigos();
        info.megaMan = new MegaMan(info.texturaMegaMan, 330, 2517);

        info.personagens.add(info.megaMan);
        info.personagens.add(info.penguin);
    }

    private void criarInimigos(){
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

    @Override
    public void render() {
        info.cameraFoco.set(info.megaMan.getPosX() + info.megaMan.getCorpo().getBoundingRectangle().width, 
        info.megaMan.getPosY() + (info.megaMan.getCorpo().getBoundingRectangle().height/2));
        info.camera.position.set(info.cameraFoco, 0);
        info.camera.update();
        info.batch.setProjectionMatrix(info.camera.combined);
        
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!info.megaMan.isMorreu()){
            ataquesPersonagens();
            atualizarPersonagens();
            colisoes();
        }

        desenhaItens();
        super.render();
    }

    private void atualizarPersonagens(){
        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            personagem.mover();
            personagem.atacar();
            personagem.morrer();
            if(info.penguin.getVida() <= 0){
                info.somVitoria.play();
                setScreen(new TelaVitoria(this));
            }
            if (info.megaMan.isMorreu() && !info.gameOver) {
                //megaMan.setPosicao(0, 0);
                info.somMorte.play();
                info.vidasMegaMan--;
                if (info.vidasMegaMan > 0) {
                    //megaMan = new MegaMan(texturaMegaMan, 330, 2517);
                    dispose();
                    criaObjetosJogo();
                    info.gameOver = false;
                    network.sendGameOver(false);
                    //megaMan.setPosicao(330, 2517);
                    //megaMan.setVida(16);
                    //personagens.add(megaMan);
                } else {
                    info.gameOver = true;
                    network.sendGameOver(true);
                    setScreen(new TelaGameOver(this));
                    
                }
            }
            info.megaMan.confereMortePorQueda();
        }
        info.personagens.reset();

        info.inimigos.reset();
        while (info.inimigos.hasNext()) {
            Inimigo inimigo = info.inimigos.next();
            inimigo.setPosXmegaMan(info.megaMan.getPosX());
        }
        info.inimigos.reset();

        info.penguin.atualizar();
    }

    private void ataquesPersonagens(){
        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).disparar();
            }
        }
        info.personagens.reset();
    }

    public InimigoIterator getInimigos() {
        return info.inimigos;
    }

    public MegaMan getMegaMan() {
        return info.megaMan;
    }

    private void colisoes() {

        info.gerenciadorColisoes.colisaoPersonagensPlataformas(info.mapa.getRetangulosColisao(), info.personagens);

        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            info.gerenciadorColisoes.colisaoPersonagemParedes(
                info.mapa.getRetangulosColisaoParedeDireita(),
                info.mapa.getRetangulosColisaoParedeEsquerda(),
                personagem
            );
        }
        info.personagens.reset();

        info.gerenciadorColisoes.colisaoMegaManInimigos(info.megaMan, info.inimigos);

        info.inimigos.reset();
        while (info.inimigos.hasNext()) {
            Inimigo inimigo = info.inimigos.next();
            info.gerenciadorColisoes.colisaoAtaquesMegaman(info.megaMan, inimigo.getAtaquesAtivos());
            info.gerenciadorColisoes.colisaoAtaquesMegamanInimigos(inimigo, info.megaMan.getAtaquesAtivos());
        }
        info.inimigos.reset();

        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            info.gerenciadorColisoes.colisaoAtaquesPlataformas(info.mapa.getRetangulosColisao(), personagem.getAtaquesAtivos());
        }
        info.personagens.reset();
    }

    private void desenharVida() {
        float larguraBarra = 20;
        float alturaBarra = 200;
        float margem = 30;
        float borda = 4;

        float vidaMaxMegaMan = 16f;
        float vidaAtualMegaMan = info.megaMan.getVida();
        float proporcaoMegaMan = Math.max(vidaAtualMegaMan / vidaMaxMegaMan, 0);

        float barraMegaManX = info.camera.position.x - info.camera.viewportWidth / 2 + margem;
        float barraMegaManY = info.camera.position.y - alturaBarra / 2;

        info.shapeRenderer.setProjectionMatrix(info.camera.combined);
        info.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        info.shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
        info.shapeRenderer.rect(barraMegaManX - borda, barraMegaManY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

        info.shapeRenderer.setColor(Color.DARK_GRAY);
        info.shapeRenderer.rect(barraMegaManX, barraMegaManY, larguraBarra, alturaBarra);

        info.shapeRenderer.setColor(Color.YELLOW);
        info.shapeRenderer.rect(barraMegaManX, barraMegaManY, larguraBarra, alturaBarra * proporcaoMegaMan);

        info.shapeRenderer.end();

        float distancia = info.megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
            info.penguin.getCorpo().getBoundingRectangle().getCenter(new Vector2())
        );
        if (distancia < 600) {
            float vidaMaxPenguin = 32f;
            float vidaAtualPenguin = info.penguin.getVida();
            float proporcaoPenguin = Math.max(vidaAtualPenguin / vidaMaxPenguin, 0);

            float barraPenguinX = info.camera.position.x + info.camera.viewportWidth / 2 - margem - larguraBarra;
            float barraPenguinY = info.camera.position.y - alturaBarra / 2;

            info.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            info.shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
            info.shapeRenderer.rect(barraPenguinX - borda, barraPenguinY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

            info.shapeRenderer.setColor(Color.DARK_GRAY);
            info.shapeRenderer.rect(barraPenguinX, barraPenguinY, larguraBarra, alturaBarra);

            info.shapeRenderer.setColor(Color.RED);
            info.shapeRenderer.rect(barraPenguinX, barraPenguinY, larguraBarra, alturaBarra * proporcaoPenguin);

            info.shapeRenderer.end();
        }
    }

    private void desenhaItens(){

        info.camera.update();
        info.batch.setProjectionMatrix(info.camera.combined);
    
        info.batch.begin();

        info.batch.draw(info.texturaFundo, info.camera.position.x - info.camera.viewportWidth / 2, info.camera.position.y - info.camera.viewportHeight / 2, info.camera.viewportWidth, info.camera.viewportHeight );

        info.batch.end();

        info.mapa.render(info.camera);

        info.batch.setShader(null);
        info.batch.setProjectionMatrix(info.camera.combined);

        info.batch.begin();

        if (info.megaMan.getVida() <= 5) {
            info.fonteVida.setColor(Color.RED);
        }else{
            info.fonteVida.setColor(Color.BLUE);
        }
        info.fonteVida.draw(info.batch, "Vida do " + info.nomeJogador + ": "+ info.megaMan.getVida(), info.megaMan.getPosX() - 300, info.megaMan.getPosY() + 300);
        //System.err.println(megaMan.getPosY() + " , "+ megaMan.getPosX());

        info.fonteVida.setColor(Color.ORANGE);
        info.fonteVida.draw(info.batch, "Vida Penguin: " + info.penguin.getVida(), info.megaMan.getPosX() + 280, info.megaMan.getPosY() + 300);

        desenharEntidades();
        desenharVida();
        info.batch.end();
        
        info.shapeRenderer.setProjectionMatrix(info.camera.combined);
        info.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        info.shapeRenderer.setColor(Color.RED);
        // for (Rectangle r : mapa.getChaos()) {
        //     shapeRenderer.rect(r.x, r.y, r.width, r.height);
        // }
        
        info.shapeRenderer.end();
    }

    private void desenharEntidades(){
        desenharAtaques();
        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            personagem.draw(info.batch);
        }
        info.personagens.reset();
    }

    private void desenharAtaques(){
        info.personagens.reset();
        while (info.personagens.hasNext()) {
            Personagem personagem = info.personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).draw(info.batch);;
            }
        }
        info.personagens.reset();
    }

    @Override
    public void resize(int width, int height) {
        info.viewport.update(width, height);
    }

    public void reset(){
        info.vidasMegaMan = 3;
        info.gameOver = false;
        dispose();
        create();
    }


    @Override
    public void dispose() {
        if(info.mapa != null) {
            info.mapa.dispose();
        }
        if(info.batch != null) {
            info.batch.dispose();
        }
        
        info.somMorte.pause();
        info.somMorte.dispose();
        info.somPadrao.pause();
        info.somPadrao.dispose();
        info.somVitoria.pause();
        info.somVitoria.dispose();

        info.fonteVida.dispose();

        //TipoAtaque.disposeTodasTexturas();
        info.texturaMegaMan.dispose();
        info.texturaPenguin.dispose();
        info.texturaTrower.dispose();
        info.texturaJaminger.dispose();

        super.dispose();
    }


    public static void main(String[] args) {
        new JogoServer();
    }
}*/
