package com.tp2.megamanx;

import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;
import com.tp2.megamanx.Inimigos.Hogamer;
import com.tp2.megamanx.Inimigos.Inimigo;
import com.tp2.megamanx.Inimigos.Pinguim;
import com.tp2.megamanx.Inimigos.Trower;
import com.tp2.megamanx.Inimigos.Vile;
import com.tp2.megamanx.Inimigos.Voador;
import com.tp2.megamanx.Inimigos.Walking;
import com.tp2.megamanx.Inimigos.Spark;
import com.tp2.megamanx.NetworkManager.*;

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Method;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.audio.Sound;
import com.tp2.megamanx.UtilitariosConexao.*;

public class Jogo extends Game /*implements java.io.Serializable*/ {

    private boolean jogoIniciado = false; 
    private boolean objetosCriados = false; 

    private Texture texturaMegaMan;
    private Texture texturaMegaMan2;
    private Texture texturaVoador;
    private Texture texturaHogamer;

    private Texture texturaPenguin;
    private Texture texturaTrower;

    private Texture texturaWalking; 
    private Texture texturaVile;
    private Texture texturaSpark;

    private Texture texturaFundo;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Vector2 cameraFoco;
    private Viewport viewport;
    private FreeTypeFontGenerator gerador;
    private FreeTypeFontGenerator.FreeTypeFontParameter parametro;
    private BitmapFont fonteVida;
    private ShapeRenderer shapeRenderer;

    private Sound somMorte, somVitoria, somPadrao; 
    
    private int vidasMegaMan = 3;
    public boolean fase2Ativada = false;
    private boolean megaManPodeSofrerDanoContato = true;
    public final GerenciadorFases gerenciadorFases = new GerenciadorFases(1);

    private Random random;
    private ArrayList<Vector2> posicoesValidas;
    private Mapa mapaFase1, mapaFase2;                          

    private GerenciadorColisoes gerenciadorColisoes; 

    public InimigoIterator inimigos;                 
    private PersonagemIterator personagens;          

    private MegaMan megaMan;          
    private MegaMan remoteMegaMan; 
    public Pinguim penguin;         
    private Vile vile;
    private Spark spark;

    private NetworkManager networkManager; 
    private final ArrayList<PosicaoTiro> tirosRemotosPendentes = new ArrayList<>();
    private final ArrayList<PosicaoTiro> tirosRemotosParaDesenhar = new ArrayList<>();
    public boolean isServer = true;      
    private boolean isMultiplayer = false; 
    public InimigoIterator remoteInimigos;
    private Map<Integer, Inimigo> remoteEnemyMap = new HashMap<>();
    private Map<Integer, Integer> remoteEnemyLastVida = new HashMap<>();
    private Set<Inimigo> remotePlaceholders = new HashSet<>();
    private java.util.Set<Ataque> sentAttacks = new java.util.HashSet<>();

    @Override
    public void create() {
        setScreen(new TelaInicial(this));
    }

    public void iniciarJogo() {
        if (objetosCriados) return; 
        objetosCriados = true;
        criaObjetosJogo();
        setJogoIniciado(true);
    }

    private void criaObjetosJogo(){
        iniciarComponentes();
        iniciarSons();

        carregaTexturas();
        if (texturaWalking == null) {
            texturaWalking = new Texture("imagens/Fase2/walking.png"); 
        }
        if (texturaVile == null) {
            texturaVile = new Texture("imagens/Fase2/vile.png");
        }
        if (texturaSpark == null) {
            texturaSpark = new Texture("imagens/Fase2/spark.png");
        }

        random = new Random();                       
        posicoesValidas = new ArrayList<>();    
        criaMapa();

        gerenciadorColisoes = new GerenciadorColisoes(); 

        inimigos = new InimigoIterator();                
        personagens = new PersonagemIterator(); 
        criarPersonagens(fase2Ativada);

        if (isMultiplayer && networkManager == null && isServer) {
            iniciaMultiplayer(); 
        }

        if (isMultiplayer && networkManager == null && !isServer) {
            iniciaMultiplayer(); 
        }
    }

    private void verificaGerenciadorFases(){
        if (gerenciadorFases.getFaseAtual() == 2) {
            fase2Ativada = true;
        } else {
            fase2Ativada = false;
        }
    }

    private void iniciarComponentes(){
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);
        cameraFoco = new Vector2();
        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        gerador = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametro.size = 24;
        parametro.color = Color.WHITE;
        fonteVida = gerador.generateFont(parametro);
        gerador.dispose();

        shapeRenderer = new ShapeRenderer();
    }

    private void iniciarSons(){
        somMorte = Gdx.audio.newSound(Gdx.files.internal("assets/sons/megaman-x-death-sound-effect.mp3"));
        somVitoria = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mmx-stage-clear.mp3"));
        somPadrao = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));
        somPadrao.play(somPadrao.loop()); 
    }

    private void criaMapa(){
        mapaFase1 = new Mapa("maps/MapaFase1.tmx", 800, 600);
        mapaFase2 = new Mapa("maps/MapaFase2.tmx", 800, 600);
    }

    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();    
        if (texturaFundo == null) {
            texturaFundo = new Texture("fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg");
        }
        if (texturaMegaMan == null) {
            texturaMegaMan = new Texture("imagens/MegaMan/mega_man.png"); 
        }
        if (texturaMegaMan2 == null) {
            texturaMegaMan2 = new Texture("imagens/MegaMan/mega_man_green.png");
        }
        if (texturaVoador == null) {
            texturaVoador = new Texture("imagens/Fase1/bee.png");    
        }
        if (texturaHogamer == null) {
            texturaHogamer = new Texture("imagens/Fase1/hogamer.png");
        }
        if (texturaPenguin == null) {
            texturaPenguin = new Texture("imagens/Fase1/penguin.png");
        }
        if (texturaTrower == null) {
            texturaTrower = new Texture("imagens/Fase1/now.png"); 
        }
        if (texturaVile == null) {
            texturaVile = new Texture("imagens/Fase2/vile.png");
        }
        if (texturaSpark == null) {
            texturaSpark = new Texture("imagens/Fase2/spark.png");
        }
    }

    private void iniciaMultiplayer() {
        if (!isMultiplayer) return;
        // Se já existe, descarte para recriar com a flag atual de isServer
        if (networkManager != null) {
            networkManager.dispose();
        }
        //System.out.println("iniciaMultiplayer: isServer=" + isServer);
        networkManager = new NetworkManager(this, isServer); // Inicializa/recria o gerenciador de rede
    }

    private void criarPersonagens(boolean fase2Ativada){
        criarInimigos(fase2Ativada);

        if(isMultiplayer && !isServer){
            megaMan = new MegaMan(texturaMegaMan2,  330, 3000);
            //megaMan = new MegaMan(texturaMegaMan2,  330, 2517);
        }else{
            megaMan = new MegaMan(texturaMegaMan,  330, 3000);
            //megaMan = new MegaMan(texturaMegaMan,  330, 2517); 
        }
        personagens.add(megaMan);      
        
        if(!fase2Ativada){
            if (penguin != null) personagens.add(penguin); 
        }                     

        if (fase2Ativada) {
            if (vile != null) personagens.add(vile); 
            if (spark != null) personagens.add(spark);                    
        }
    }

    private void criarInimigos(boolean fase2Ativada){
        if (isMultiplayer && isServer) {
            criarBosses();
            criarNaoBosses();   
        } else if (!isMultiplayer) {
            criarBosses();
            criarNaoBosses(); 
        } 
    }

    private void criarBosses(){
        if(!fase2Ativada){
            penguin = new Pinguim(texturaPenguin); 
            inimigos.add(penguin);
        }else if (fase2Ativada) {
            criarVile();
            criarSpark();
        }
    }

    private void criarNaoBosses(){
        determinarPosicoesValidas();

        int indexPosicaoAnterior = -1;
        int inimigosCriados = 0;
        int tentativas = 0;

        while (inimigosCriados < 15 && tentativas < 100) {
            tentativas++;
            int indexPosicao = random.nextInt(posicoesValidas.size());

            if (indexPosicaoAnterior == -1 || Math.abs(posicoesValidas.get(indexPosicao).x - posicoesValidas.get(indexPosicaoAnterior).x) > 350) {
                int sortearPersonagem = random.nextInt(3);

                if (sortearPersonagem == 0) {
                    if (!fase2Ativada) {
                        criarTrower(indexPosicao);
                    } else {
                        criarWalking(indexPosicao);
                    }
                } else if (sortearPersonagem == 1) {
                    criarVoador(indexPosicao);
                } else {
                    criarHogamer(indexPosicao);
                }

                indexPosicaoAnterior = indexPosicao;
                inimigosCriados++;
            }
        }

    }
    
    private void determinarPosicoesValidas(){
        if (!fase2Ativada) {
            posicoesValidas.clear();

            for(int i=0; i<30; i++){
                for(Rectangle plataforma : mapaFase1.getChaos()){
                    float posYplataforma = plataforma.y + plataforma.height + 150;
                    float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                    posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
                }
            }

        } else {
            posicoesValidas.clear();

            for(int i=0; i<30; i++){
                for(Rectangle plataforma : mapaFase1.getChaos()){
                    float posYplataforma = plataforma.y + plataforma.height + 150;
                    float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                    posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
                }
            }

        }
    }

     private void criarVile(){
        Ataque ataqueVile = new Ataque(
            new TextureRegion(
                TipoAtaque.BOMBA.getTextura(), 
		        TipoAtaque.BOMBA.getCordX(), 
                TipoAtaque.BOMBA.getCordY(),
		        TipoAtaque.BOMBA.getLargura(), 
                TipoAtaque.BOMBA.getAltura()), 
		    new Vector2(0.05f, 0.5f), 0, 0, 
            TipoAtaque.BOMBA, -5, false
        );
        vile = new Vile(texturaVile, ataqueVile);
        inimigos.add(vile);
    }

    private void criarSpark(){
        Ataque ataqueSpark = new Ataque(
            new TextureRegion(
                TipoAtaque.CHOQUE.getTextura(), 
		        TipoAtaque.CHOQUE.getCordX(), 
                TipoAtaque.CHOQUE.getCordY(),
		        TipoAtaque.CHOQUE.getLargura(), 
                TipoAtaque.CHOQUE.getAltura()), 
		    new Vector2(0.05f, 0.5f), 0, 0, 
            TipoAtaque.CHOQUE, -5, false
        );
        spark = new Spark(texturaSpark, ataqueSpark);
        inimigos.add(spark);
    }

    private void criarTrower(int indexPosicao){
        Trower trower = new Trower(texturaTrower);

        float posX = posicoesValidas.get(indexPosicao).x - trower.getCorpo().getBoundingRectangle().width;
        float posY = posicoesValidas.get(indexPosicao).y;
        trower.setPosicao(posX, posY);

        inimigos.add(trower);
        personagens.add(trower);
    }

    private void criarWalking(int indexPosicao){
        Walking walking = new Walking(texturaWalking);

        float posX = posicoesValidas.get(indexPosicao).x + walking.getCorpo().getBoundingRectangle().width;
        float posY = posicoesValidas.get(indexPosicao).y;
        walking.setPosicao(posX, posY);

        inimigos.add(walking);
        personagens.add(walking);
    }

    private void criarVoador(int indexPosicao){
        Voador voador = new Voador(texturaVoador);

        float posX = posicoesValidas.get(indexPosicao).x - voador.getCorpo().getBoundingRectangle().width;
        float posY = posicoesValidas.get(indexPosicao).y;
        voador.setPosicao(posX, posY);

        inimigos.add(voador);
        personagens.add(voador);
    }

    private void criarHogamer(int indexPosicao){
        Hogamer hogamer = new Hogamer(texturaHogamer);

        float posX = posicoesValidas.get(indexPosicao).x + hogamer.getCorpo().getBoundingRectangle().width;
        float posY = posicoesValidas.get(indexPosicao).y + hogamer.getCorpo().getBoundingRectangle().height;
        hogamer.setPosicao(posX, posY);

        inimigos.add(hogamer);
        personagens.add(hogamer);
    }

    @Override
    public void render() {
        if (jogoIniciado) {
            verificaGerenciadorFases();
           
            if (!objetosCriados) {
                objetosCriados = true;
                criaObjetosJogo();
            }
            
            criarRemoteMegaMan();

            atualizarCamera();

            controleFases();

            partePrincipalDoJogo();

            desenhaItens(); 
        }
        super.render(); 
    }

    private void criarRemoteMegaMan(){
        if (isMultiplayer && remoteMegaMan == null) {
            if (isServer) {
                remoteMegaMan = new MegaMan(texturaMegaMan2, 0, 0);
            }else{
                remoteMegaMan = new MegaMan(texturaMegaMan, 0, 0); 
            }
        }
    }

    private void atualizarCamera(){
        cameraFoco.set(megaMan.getPosX() + megaMan.getCorpo().getBoundingRectangle().width, 
        megaMan.getPosY() + (megaMan.getCorpo().getBoundingRectangle().height/2));
        camera.position.set(cameraFoco, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined); 
        
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void controleFases(){
        irParaSegundaFase();
        ganharJogo();
        perderJogo();
    }

    private void irParaSegundaFase(){
        if (!fase2Ativada) {
            if (penguin != null && penguin.getVida() <= 0) {
                megaManPodeSofrerDanoContato = false;
                if(penguin.isMorreu() && penguin.getDeltaTime() > 3.5f){
                    penguin.setPosicao(-500, -500);
                    gerenciadorFases.setFaseAtual(2);
                    if (networkManager != null) {
                        networkManager.sendGerenciadorFases(gerenciadorFases);
                    }
                    fase2Ativada = true;
                    megaManPodeSofrerDanoContato = true;
                    iniciarSegundaFase(fase2Ativada);
                }
            }
        }
    }

    public void iniciarSegundaFase(boolean notifyNetwork){
        penguin = null;
        megaMan = null;
        remoteMegaMan = null;

        inimigos.clear();
        personagens.clear();

        criarPersonagens(fase2Ativada);
    }

    public void ganharJogo(){
        if(fase2Ativada){
            if(spark != null && spark.getVida() <= 0){
                if (networkManager != null) {
                    networkManager.sendGanhouJogo(new VerificaGanhar(true));
                }
                megaManPodeSofrerDanoContato = false;
                if(spark.isMorreu() && spark.getDeltaTime() > 3.5f){
                    spark.setPosicao(-500, -500);
                    gerenciadorFases.setFaseAtual(1);
                    if (networkManager != null) {
                        networkManager.sendGerenciadorFases(gerenciadorFases);
                    }
                    fase2Ativada = false;
                    megaManPodeSofrerDanoContato = true;
                    somVitoria.play();
                    setScreen(new TelaVitoria(this));   
                }
            }
        }
    }

    private void perderJogo(){
        if ((megaMan.isMorreu()) && vidasMegaMan <= 0) {
            if(megaMan.getDeltaTime() > 3.5f){
                gerenciadorFases.setFaseAtual(1);
                if (networkManager != null) {
                    networkManager.sendGerenciadorFases(gerenciadorFases);
                }
                fase2Ativada = false;
                somMorte.play();
                setScreen(new TelaGameOver(this));
            }
        }
    }

    private void partePrincipalDoJogo(){
        if(vidasMegaMan > 0){
            atualizarEntidades();
            
            colisoes();

            if(isMultiplayer && isServer){
                networkManager.sendInimigos(inimigos);
            }


            if (isMultiplayer && !isServer && networkManager != null && megaMan != null) {
                try {
                    ArrayList<Ataque> ataques = megaMan.getAtaquesAtivos();
                    ArrayList<Ataque> ataquesParaRemover = new ArrayList<>();
                    percorrerInimigosRemotos(ataques, ataquesParaRemover);
                    removerAtaquesQueAtingiram(ataquesParaRemover, ataques);
                } catch (Throwable t) {
                    Gdx.app.error("Network", "Erro ao detectar hits cliente: " + t.getMessage());
                }
            }

            sincronizarPersonagens();
        } 
    }

    private void atualizarEntidades(){
        if(megaMan.isMorreu() && megaMan.getDeltaTime() > 3.5f){
            vidasMegaMan--;
            megaMan.setVida(16);
            megaMan.setPosicao(330, 2517);
            megaMan.setMorreu(false);
            megaMan.setDeltaTime(0);
            criarNaoBosses();
        }

        atualizarBosses();
        atualizarPersonagens();
        atualizarInimigos();
    }

    private void atualizarBosses(){
        if(!fase2Ativada){
            if (penguin != null) penguin.atualizar();
        } 

        if (fase2Ativada) {
            if (vile != null) vile.atualizar();
            if (spark != null) spark.atualizar();

            if (vile != null && vile.getVida() <= 0) {
                vile.morrer();
            }
            if (spark != null && spark.getVida() <= 0) {
                spark.morrer();
            }
        }
    }

    private void atualizarPersonagens(){
        megaMan.confereMortePorQueda(); 

        atualizarAtaquesPersonagens();
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.mover();
            personagem.atacar();
            personagem.morrer();
        }
        personagens.reset();
    }

    private void atualizarAtaquesPersonagens(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).disparar();
            }
        }
        personagens.reset();
    }

    private void atualizarInimigos(){
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();

            if (remotePlaceholders.contains(inimigo)) {
                continue;
            }

            if(remoteMegaMan != null){
                Vector2 posInimigo = inimigo.getPosicao();
                Vector2 posRemoteMegaMan = new Vector2(remoteMegaMan.getPosX(), remoteMegaMan.getPosY());
                Vector2 posMegaMan = new Vector2(megaMan.getPosX(), megaMan.getPosY());

                float distanciaRemoteInimigo = posRemoteMegaMan.dst(posInimigo);
                float distanciaMegaManInimigo = posMegaMan.dst(posInimigo); 
                if(distanciaRemoteInimigo < distanciaMegaManInimigo){
                    inimigo.setPosicaoMegaMan(new Vector2(remoteMegaMan.getPosX(), remoteMegaMan.getPosY()));
                }else{  
                    inimigo.setPosicaoMegaMan(new Vector2(megaMan.getPosX(), megaMan.getPosY()));
                }
            }else{
                inimigo.setPosicaoMegaMan(new Vector2(megaMan.getPosX(), megaMan.getPosY()));
            }
            
        }
        inimigos.reset();
    }

    private void colisoes() {
        Mapa tipoMapa = fase2Ativada ? mapaFase2 : mapaFase1;

        colisoesPersonagensComPlataformas(tipoMapa);    
        colisoesAtaquesComPlataformas(tipoMapa);
        colisoesPersonagensComAtaques();
        if(megaManPodeSofrerDanoContato){
            gerenciadorColisoes.colisaoMegaManInimigos(megaMan, inimigos);
        }
    }

    private void colisoesPersonagensComPlataformas(Mapa tipoMapa){
        gerenciadorColisoes.colisaoPersonagensPlataformas(tipoMapa.getRetangulosColisao(), personagens);

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            gerenciadorColisoes.colisaoPersonagemParedes(
                tipoMapa.getRetangulosColisaoParedeDireita(),
                tipoMapa.getRetangulosColisaoParedeEsquerda(),
                personagem
            );
        }
        personagens.reset();
    }

    private void colisoesAtaquesComPlataformas(Mapa tipoMapa){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            gerenciadorColisoes.colisaoAtaquesPlataformas(tipoMapa.getRetangulosColisao(), personagem.getAtaquesAtivos());
        }
        personagens.reset();
    }

    private void colisoesPersonagensComAtaques(){
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            gerenciadorColisoes.colisaoAtaquesMegaman(megaMan, inimigo.getAtaquesAtivos());
            gerenciadorColisoes.colisaoAtaquesMegamanInimigos(inimigo, megaMan.getAtaquesAtivos());
        }
        inimigos.reset();
    }

    private void percorrerInimigosRemotos(ArrayList<Ataque> ataques, ArrayList<Ataque> ataquesParaRemover){
        for (Map.Entry<Integer, Inimigo> e : remoteEnemyMap.entrySet()) {
            int enemyId = e.getKey();
            Inimigo inimigoLocal = e.getValue();

            if (inimigoLocal == null) continue;
        
            Rectangle rectInimigo;
            try {
                rectInimigo = inimigoLocal.getRect();
            } catch (Throwable t) { continue; }
            if (rectInimigo == null) continue;

            verificaColisaoAtaquesDoClienteComInimigos(ataques, ataquesParaRemover, rectInimigo, enemyId);
        }
    }

    private void verificaColisaoAtaquesDoClienteComInimigos(ArrayList<Ataque> ataques, ArrayList<Ataque> ataquesParaRemover, Rectangle rectInimigo, int enemyId){
        for (Ataque ataque : ataques) {
            if (ataque == null) continue;
            try {
                Rectangle rectAtaque = null;
                rectAtaque = pegarRectangleAtaqueDoCliente(ataque);

                if (rectAtaque == null) continue;
                if (rectAtaque.overlaps(rectInimigo)) {
                    notificarServidorQueInimigoAtingido(ataque, enemyId);
                    ataquesParaRemover.add(ataque);
                    break; 
                }
            } catch (Throwable ignored) {}
        }
    }

    private Rectangle pegarRectangleAtaqueDoCliente(Ataque ataque){
        Rectangle rectAtaque = null;
        try {
            Method mRect = null;
            try {
                mRect = ataque.getClass().getMethod("getRect");
            } catch (NoSuchMethodException ignore) {
                try {
                    Object corpo = ataque.getClass().getMethod("getCorpo").invoke(ataque);
                    if (corpo != null) {
                        try {
                            rectAtaque = (Rectangle) corpo.getClass().getMethod("getBoundingRectangle").invoke(corpo);
                        } catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }

            if (mRect != null) {
                Object r = mRect.invoke(ataque);
                if (r instanceof Rectangle) rectAtaque = (Rectangle) r;
            }
        } catch (Throwable ignore) {}
        return rectAtaque;
    }

    private void notificarServidorQueInimigoAtingido(Ataque ataque, int enemyId){
        // damage: tentar obter método getDano() em Ataque, senão usa 1
        int damage = 1;
        try {
            java.lang.reflect.Method mDano = ataque.getClass().getMethod("getDano");
            Object d = mDano.invoke(ataque);
            if (d instanceof Number) damage = ((Number) d).intValue();
        } catch (Throwable ignored) {}

        // envia hit para o servidor (o servidor chamará inimigo.tomarDano())
        networkManager.sendEnemyHit(enemyId, damage);
    }

    private void removerAtaquesQueAtingiram(ArrayList<Ataque> ataquesParaRemover, ArrayList<Ataque> ataques){
        if (!ataquesParaRemover.isEmpty()) {
            for (Ataque ar : ataquesParaRemover) {
                try { 
                    ataques.remove(ar); 
                } catch (Throwable ignored) {}
            }
        }
    } 

    private void sincronizarPersonagens(){
        if (isMultiplayer && networkManager != null) {
            // Enviar posição + estado de animação do jogador para o outro personagem
            PlayerPosition pp = new PlayerPosition(megaMan.getPosX(), megaMan.getPosY(), isServer ? 0 : 1);
            try {
                pp.regionX = megaMan.getRegionX();
                pp.regionY = megaMan.getRegionY();
                pp.regionW = megaMan.getRegionWidth();
                pp.regionH = megaMan.getRegionHeight();
                pp.paraDireita = megaMan.isParaDireita();
            } catch (Throwable ignored) {}
            networkManager.sendPlayerPosition(pp);

            // Envia posições dos inimigos se for servidor
            if (isServer) {
                networkManager.sendEnemyPositions();
            }
        }
    }

    private void desenhaItens(){
        Mapa tipoMapa = fase2Ativada ? mapaFase2 : mapaFase1;
       
        batch.begin();
        batch.draw(texturaFundo, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight );
        batch.end();

        tipoMapa.render(camera);

        batch.begin();

        desenharEntidades(); 
       
        fonteVida.draw(batch, "Vida MegaMan: " + megaMan.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 20);
        
        if(!fase2Ativada && (penguin != null)){
            fonteVida.draw(batch, "Vida Penguin: " + penguin.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 50);
        }
        else if(fase2Ativada && (spark != null)){
            fonteVida.draw(batch, "Vida Spark: " + spark.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 50);
        }
        
        desenharVidas();

        batch.end();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.end();
    }

    private void desenharEntidades(){
        desenharAtaques(); 

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.draw(batch);
        }
        personagens.reset();

        if (remoteMegaMan != null && (remoteMegaMan.getPosX() != 0 || remoteMegaMan.getPosY() != 0)) {
            remoteMegaMan.draw(batch);
        }
    }

    private void desenharAtaques(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            for (int i = 0; i < personagem.getAtaquesAtivos().size(); i++) {
                desenhaTiros(personagem.getAtaquesAtivos().get(i));
            }
        }
        personagens.reset();

        if (!tirosRemotosPendentes.isEmpty()) {
            for (int i = 0; i < tirosRemotosPendentes.size(); i++) {
                PosicaoTiro tiro = tirosRemotosPendentes.get(i);
                if (tiro != null) {
                    tirosRemotosParaDesenhar.add(new PosicaoTiro(tiro.x, tiro.y, tiro.tipo, tiro.paraDireita));
                }
            }
            tirosRemotosPendentes.clear();
        }

        for (int i = 0; i < tirosRemotosParaDesenhar.size(); i++) {
            desenhaTiroRemoto(tirosRemotosParaDesenhar.get(i));
        }

        tirosRemotosParaDesenhar.clear();
    }

    private void desenharVidas() {
        float larguraBarra = 20;
        float alturaBarra = 200;
        float margem = 30;
        float borda = 4;

        desenhaVidaMegaMan(larguraBarra, alturaBarra, margem, borda);

        InformacoesBoss informacoesBoss = new InformacoesBoss();
        determinarInformacoesBoss(informacoesBoss);
        desenharVidaBoss(informacoesBoss, larguraBarra, alturaBarra, margem, borda);
    }

    private void desenhaVidaMegaMan(float larguraBarra, float alturaBarra, float margem, float borda){
        float vidaMaxMegaMan = 16f;
        float vidaAtualMegaMan = megaMan.getVida();
        float proporcaoMegaMan = Math.max(vidaAtualMegaMan / vidaMaxMegaMan, 0);

        float barraMegaManX = camera.position.x - camera.viewportWidth / 2 + margem;
        float barraMegaManY = camera.position.y - alturaBarra / 2;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
        shapeRenderer.rect(barraMegaManX - borda, barraMegaManY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(barraMegaManX, barraMegaManY, larguraBarra, alturaBarra);

        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(barraMegaManX, barraMegaManY, larguraBarra, alturaBarra * proporcaoMegaMan);

        shapeRenderer.end();
    }

    class InformacoesBoss {
        float distancia = 0;
        float vidaMaxBoss = 0;
        float vidaAtualBoss = 0;    
    }

    public void determinarInformacoesBoss(InformacoesBoss informacoesBoss){
        if(!fase2Ativada && (penguin != null)){
            informacoesBoss.distancia = megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
                penguin.getCorpo().getBoundingRectangle().getCenter(new Vector2())
            ); 
            informacoesBoss.vidaMaxBoss = 32f;
            informacoesBoss.vidaAtualBoss = penguin.getVida();
        }
        else if(fase2Ativada && (spark != null)){
            informacoesBoss.distancia = megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
                spark.getCorpo().getBoundingRectangle().getCenter(new Vector2())
            ); 
            informacoesBoss.vidaMaxBoss = 40f;
            informacoesBoss.vidaAtualBoss = spark.getVida();
        }
    }

    private void desenharVidaBoss(InformacoesBoss informacoesBoss, float larguraBarra, float alturaBarra, float margem, float borda){
        if (informacoesBoss.distancia < 600) {
            float proporcaoBoss = Math.max(informacoesBoss.vidaAtualBoss / informacoesBoss.vidaMaxBoss, 0);

            float barraBossX = camera.position.x + camera.viewportWidth / 2 - margem - larguraBarra;
            float barraBossY = camera.position.y - alturaBarra / 2;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
            shapeRenderer.rect(barraBossX - borda, barraBossY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(barraBossX, barraBossY, larguraBarra, alturaBarra);

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(barraBossX, barraBossY, larguraBarra, alturaBarra * proporcaoBoss);

            shapeRenderer.end();
        }
    }
   
    public void setJogoIniciado(boolean iniciado) {
        this.jogoIniciado = iniciado;
    }

    public void setSegundaFaseAtivada(boolean fase2Ativada) {
        this.fase2Ativada = fase2Ativada;
    }

    public void setFase(int fase) {
        gerenciadorFases.setFaseAtual(fase);
    }

    public void reset(){
        vidasMegaMan = 3;
        gerenciadorFases.setFaseAtual(1);
        if (networkManager != null) {
            networkManager.sendGerenciadorFases(gerenciadorFases);
        }
        fase2Ativada = false;
        megaManPodeSofrerDanoContato = true;

        megaMan = null;
        remoteMegaMan = null;
        penguin = null;
        vile = null;
        spark = null;

        if (personagens != null) {
            personagens.clear();
        }

        if (inimigos != null) {
            inimigos.clear();
        }

        if (remoteInimigos != null) {
            remoteInimigos.clear();
        }

        remoteEnemyMap.clear();
        remoteEnemyLastVida.clear();
        remotePlaceholders.clear();
        sentAttacks.clear();

        tirosRemotosPendentes.clear();
        tirosRemotosParaDesenhar.clear();

        objetosCriados = false;
    }

        @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
    }

    @Override
    public void dispose() {
        if(batch != null) {
            batch.dispose();
        }
        if (fonteVida != null) {
            fonteVida.dispose();
        }
        if (networkManager != null) {
            networkManager.dispose();
        }
        disposeMapas();
        disposeSons();
        disposeTexturas();
        super.dispose();
    }

    private void disposeMapas(){
        if(mapaFase1 != null) {
            mapaFase1.dispose();
        }
        if(mapaFase2 != null) {
            mapaFase2.dispose();
        }
    }

    private void disposeSons() {
        if (somMorte != null) {
            somMorte.stop();
            somMorte.dispose();
        }
        if (somVitoria != null) {
            somVitoria.stop();
            somVitoria.dispose();
        }
        if (somPadrao != null) {
            somPadrao.stop();
            somPadrao.dispose();
        }
    }


    private void disposeTexturas(){
        TipoAtaque.disposeTodasTexturas();
        if (texturaMegaMan != null) {
            texturaMegaMan.dispose();
        }
        if (texturaVoador != null) {
            texturaVoador.dispose();
        }
        if (texturaHogamer != null) {
            texturaHogamer.dispose();
        }
        if (texturaFundo != null){
            texturaFundo.dispose();
        }
        if (texturaPenguin != null) {
            texturaPenguin.dispose();
        }
        if (texturaTrower != null) {
            texturaTrower.dispose();
        }
        if (texturaWalking != null) {
            texturaWalking.dispose();
        }
        if (texturaVile != null) {
            texturaVile.dispose();
        }
        if (texturaSpark != null) {
            texturaSpark.dispose();
        }
    }


    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
        if (networkManager != null) {
            networkManager.dispose();
            networkManager = new NetworkManager(this, this.isServer);
        }
    }

    public boolean getIsServer (){
        return isServer;
    }

    public void setIsMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;

        if (this.isMultiplayer && objetosCriados && networkManager == null) {
            iniciaMultiplayer();
        }

        if (!this.isMultiplayer && networkManager != null) {
            networkManager.dispose();
            networkManager = null;
        }
    }

    public void updateRemotePlayer(PlayerPosition pos) {
        if (remoteMegaMan != null) {
            try {
                remoteMegaMan.setPosicao(pos.x, pos.y);

                try { 
                    remoteMegaMan.setParaDireita(pos.paraDireita); 
                } catch (Throwable ignored) {}

                try { 
                    remoteMegaMan.setRegion(pos.regionX, pos.regionY, pos.regionW, pos.regionH); 
                } catch (Throwable ignored) {}

            } catch (Throwable ignored) {
                try { 
                    remoteMegaMan.setPosicao(pos.x, pos.y); 
                } catch (Throwable ignored2) {}
            }
        }
    }

    public EnemyPosition getEnemyPositions() {
        EnemyPosition pos = new EnemyPosition(); 

        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            pos.x.add(((Personagem) inimigo).getPosX()); 
            pos.y.add(((Personagem) inimigo).getPosY());
            pos.ids.add(inimigo.hashCode()); 
            
            int tipo = 0; 
            try {
                if (inimigo instanceof Trower) tipo = 0;
                else if (inimigo instanceof Voador) tipo = 1;
                else if (inimigo instanceof Hogamer) tipo = 2;
                else if (inimigo instanceof Walking) tipo = 3;
                else if (inimigo instanceof Vile) tipo = 4;
                else if (inimigo instanceof Spark) tipo = 5;
                else if (inimigo instanceof Pinguim) tipo = 6;
            } catch (Throwable ignored) { tipo = 0; }

            pos.types.add(tipo);
        }
        inimigos.reset(); 

        return pos;
    }

    public void updateEnemies(EnemyPosition pos) {
        if (!isServer) {
            for (int i = 0; i < pos.ids.size(); i++) {
                int id = pos.ids.get(i);
                float x = pos.x.get(i); float y = pos.y.get(i);
                Inimigo inimigoLocal = remoteEnemyMap.get(id);

                if (inimigoLocal == null) {
                    int tipo = 0;
                    try { 
                        tipo = pos.types.get(i); 
                    } catch (Throwable ignored) { 
                        tipo = 0; 
                    }

                    Inimigo placeholder = null;
                    try {
                        placeholder = getPlaceHolderInimigo(tipo);
                    } catch (Throwable t) {
                        Gdx.app.error("Network", "Falha ao instanciar o inimigo de espaço reservado: " + t.getMessage());
                        try { if (texturaVoador == null) carregaTexturas(); } catch (Throwable ignored) {}
                    }

                    colocaInimiogoNoCliente(placeholder, id, x, y);
                } else {
                    ((Personagem) inimigoLocal).setPosicao(x, y);
                }

            }
        }
    }

    private Inimigo getPlaceHolderInimigo(int tipo){
        Inimigo placeholder;
        switch (tipo) {
            case 0: { 
                placeholder = new Trower(texturaTrower);
                break;
            }
            case 1: { 
                if (texturaVoador == null) carregaTexturas();
                placeholder = new Voador(texturaVoador);
                break;
            }
            case 2: { 
                if (texturaHogamer == null) carregaTexturas();
                placeholder = new Hogamer(texturaHogamer);
                break;
            }
            case 3: { 
                if (texturaWalking == null) carregaTexturas();
                placeholder = new Walking(texturaWalking);
                break;
            }
            case 4: { 
                if (texturaVile == null) carregaTexturas();
                Ataque ataqueVile = new Ataque(
                    new TextureRegion(
                        TipoAtaque.BOMBA.getTextura(),
                        TipoAtaque.BOMBA.getCordX(), TipoAtaque.BOMBA.getCordY(),
                        TipoAtaque.BOMBA.getLargura(), TipoAtaque.BOMBA.getAltura()
                    ),
                    new Vector2(0.05f, 0.5f), 0, 0,
                    TipoAtaque.BOMBA, -5, false
                );
                placeholder = new Vile(texturaVile, ataqueVile);
                break;
            }
            case 5: { 
                if (texturaSpark == null) carregaTexturas();
                Ataque ataqueSpark = new Ataque(
                    new TextureRegion(
                        TipoAtaque.CHOQUE.getTextura(),
                        TipoAtaque.CHOQUE.getCordX(), TipoAtaque.CHOQUE.getCordY(),
                        TipoAtaque.CHOQUE.getLargura(), TipoAtaque.CHOQUE.getAltura()
                    ),
                    new Vector2(0.05f, 0.5f), 0, 0,
                    TipoAtaque.CHOQUE, -5, false
                );
                placeholder = new Spark(texturaSpark, ataqueSpark);
                break;
            }
            case 6: { 
                if (texturaPenguin == null) carregaTexturas();
                placeholder = new Pinguim(texturaPenguin);
                break;
            }
            default: { 
                if (texturaVoador == null) carregaTexturas();
                placeholder = new Voador(texturaVoador);
                break;
            }
        }
        return placeholder;
    }

    private void colocaInimiogoNoCliente(Inimigo placeholder, int id, float x, float y){
        if (placeholder != null) {
            try { 
                ((Personagem) placeholder).setPosicao(x, y); 
            } catch (Throwable ignored) {}
            
            inimigos.add(placeholder);
            try {
                personagens.add((Personagem) placeholder); 
            } catch (Throwable t) {}
            remotePlaceholders.add(placeholder); 

            remoteEnemyMap.put(id, placeholder);
            
            try { 
                remoteEnemyLastVida.put(id, ((Personagem) placeholder).getVida()); 
            } catch (Throwable ignored) {}
        }
    }

    public void updatePinguinState(PinguinState state) {
        if (state == null) return;

        if (!isServer) {
            try {
                if (texturaPenguin == null) carregaTexturas(); 

                if (penguin == null) {
                    penguin = new Pinguim(texturaPenguin);

                    try { 
                        inimigos.add(penguin); 
                    } catch (Throwable ignored) {}

                    try { 
                        personagens.add(penguin); 
                    } catch (Throwable ignored) {}
                }
                
                try { 
                    penguin.setPosicao(state.x, state.y); 
                } catch (Throwable ignored) {}

                try { 
                    penguin.setVida(state.vida); 
                } catch (Throwable ignored) {}
            } catch (Throwable t) {
                Gdx.app.error("Network", "updatePinguinState() falhou: " + t.getMessage());
            }
        }
    }

    public void updateSparkState(SparkState state) {
        if (state == null) return;

        if (!isServer) {
            try {
                if (texturaSpark == null) carregaTexturas(); 

                    if (spark == null) {
                        Ataque ataqueSpark = new Ataque(
                        new TextureRegion(
                            TipoAtaque.CHOQUE.getTextura(),
                            TipoAtaque.CHOQUE.getCordX(), TipoAtaque.CHOQUE.getCordY(),
                            TipoAtaque.CHOQUE.getLargura(), TipoAtaque.CHOQUE.getAltura()
                        ),
                        new Vector2(0.05f, 0.5f), 0, 0,
                        TipoAtaque.CHOQUE, -5, false
                    );
                    spark = new Spark(texturaSpark, ataqueSpark);

                    try { 
                        inimigos.add(spark); 
                    } catch (Throwable ignored) {}

                    try { 
                        personagens.add(spark); 
                    } catch (Throwable ignored) {}
                }
                
                try { 
                    spark.setPosicao(state.x, state.y); 
                } catch (Throwable ignored) {}

                try { 
                    spark.setVida(state.vida); 
                } catch (Throwable ignored) {}
            } catch (Throwable t) {
                Gdx.app.error("Network", "updateSparkState() falhou: " + t.getMessage());
            }
        }
    }

    public void updateVileState(VileState state) {
        if (state == null) return;

        if (!isServer) {
            try {
                if (texturaVile == null) carregaTexturas(); 

                    if (vile == null) {

                        Ataque ataqueVile = new Ataque(
                        new TextureRegion(
                            TipoAtaque.BOMBA.getTextura(),
                            TipoAtaque.BOMBA.getCordX(), TipoAtaque.BOMBA.getCordY(),
                            TipoAtaque.BOMBA.getLargura(), TipoAtaque.BOMBA.getAltura()
                        ),
                        new Vector2(0.05f, 0.5f), 0, 0,
                        TipoAtaque.BOMBA, -5, false
                    );
                    vile = new Vile(texturaVile, ataqueVile);

                    try { 
                        inimigos.add(vile); 
                    } catch (Throwable ignored) {}

                    try { 
                        personagens.add(vile); 
                    } catch (Throwable ignored) {}
                }
                
                try { 
                    vile.setPosicao(state.x, state.y); 
                } catch (Throwable ignored) {}

                try { 
                    vile.setVida(state.vida); 
                } catch (Throwable ignored) {}
            } catch (Throwable t) {
                Gdx.app.error("Network", "updateVileState() falhou: " + t.getMessage());
            }
        }
    }

    public void applyEnemyHit(int enemyId, int damage) {
        if (!isServer) return;

        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            if (inimigo.hashCode() == enemyId) {
                try {
                    inimigo.tomarDano(damage);
                } catch (Throwable t) {
                    Gdx.app.error("Game", "applyEnemyHit() falhou: " + t.getMessage());
                }

                try {
                    inimigos.reset();
                } catch (Throwable ignored) {}
                
                break;
            }
        }
        inimigos.reset();

        if (networkManager != null) {
            networkManager.sendEnemyPositions();
        }
    }

    public void desenhaTiros(Ataque ataque) {
        if (ataque == null) return;

        ataque.draw(batch);

        if (isMultiplayer && networkManager != null) {
            networkManager.sendTiroPositions(
                ataque.getPosX(),
                ataque.getPosY(),
                ataque.getTipo(),
                ataque.isParaDireita()
            );
        }
    }
    public void desenhaTirosRemotos(PosicaoTiro posicaoTiro) {
        if (posicaoTiro == null) return;

        tirosRemotosPendentes.add(new PosicaoTiro(
            posicaoTiro.x,
            posicaoTiro.y,
            posicaoTiro.tipo,
            posicaoTiro.paraDireita
        ));
    }

    private void desenhaTiroRemoto(PosicaoTiro posicaoTiro) {
        if (posicaoTiro == null) {
            return;
        }

        try{
            TipoAtaque tipoAtaque = posicaoTiro.tipo;
            Ataque ataque = new Ataque(
                new TextureRegion(
                    tipoAtaque.getTextura(),
                    tipoAtaque.getCordX(), tipoAtaque.getCordY(),
                    tipoAtaque.getLargura(), tipoAtaque.getAltura()
                ),
                new Vector2(0.3f, 1.2f), posicaoTiro.x, posicaoTiro.y,
                tipoAtaque, 0, posicaoTiro.paraDireita
            );
            ataque.draw(batch);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

}