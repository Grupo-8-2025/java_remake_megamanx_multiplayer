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

import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

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

public class Jogo extends Game {

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
    private boolean somPadraoTocando = false;    

    
    private int vidasMegaMan = 3;
    private boolean fase2Ativada = false;

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

        carregaTexturasFase1();

        somMorte = Gdx.audio.newSound(Gdx.files.internal("assets/sons/megaman-x-death-sound-effect.mp3"));
        somVitoria = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mmx-stage-clear.mp3"));
        somPadrao = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));

        somPadrao.play(somPadrao.loop()); // Inicia a música de fundo em loop
        somPadraoTocando = true;

        random = new Random();                       
        posicoesValidas = new ArrayList<>();    
        criaMapa();      

        gerenciadorColisoes = new GerenciadorColisoes(); 

        inimigos = new InimigoIterator();                
        personagens = new PersonagemIterator(); 
        
        criaPersonagens(fase2Ativada);

        if (isMultiplayer && networkManager == null && isServer) {
            iniciaMultiplayer(); 
        }

        if (isMultiplayer && networkManager == null && !isServer) {
            iniciaMultiplayer(); 
        }
    }

    private void criaMapa(){
        mapaFase1 = new Mapa("maps/MapaFase1.tmx", 800, 600);
        mapaFase2 = new Mapa("maps/MapaFase2.tmx", 800, 600);
    }

    private void carregaTexturasFase1(){
        TipoAtaque.carregarTodasTexturas();    
        texturaFundo = new Texture("fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg");                         
        texturaMegaMan = new Texture("imagens/MegaMan/mega_man.png"); 
        texturaMegaMan2 = new Texture("imagens/MegaMan/mega_man_green.png");
        texturaVoador = new Texture("imagens/Fase1/bee.png");    
        texturaHogamer = new Texture("imagens/Fase1/hogamer.png");
        texturaPenguin = new Texture("imagens/Fase1/penguin.png");
        texturaTrower = new Texture("imagens/Fase1/now.png"); 
    }

    private void carregarTexturasFase2(){
        if (texturaWalking == null) {
            texturaWalking = new Texture("imagens/Fase2/walking.png"); 
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
        System.out.println("iniciaMultiplayer: isServer=" + isServer);
        networkManager = new NetworkManager(this, isServer); // Inicializa/recria o gerenciador de rede
    }

    private void criaPersonagens(boolean fase2Ativada){
        criarInimigos(fase2Ativada);

        if(isMultiplayer && !isServer){
            megaMan = new MegaMan(texturaMegaMan2,  330, 2517);
        }else{
            megaMan = new MegaMan(texturaMegaMan,  330, 2517); 
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
        if (isServer) {
            if(!fase2Ativada){
                penguin = new Pinguim(texturaPenguin); 
                inimigos.add(penguin);
            }else if (fase2Ativada) {
                criarVile();
                criarSpark();
            }
                
            determinarPosicoesValidas(); 

            // Talvez melhorar essa lógica
            int indexPosicaoAnterior = -1;
            for(int i=0; i<15; i++){
                int indexPosicao = random.nextInt(posicoesValidas.size());

                // Garante espaçamento mínimo entre inimigos
                if(indexPosicaoAnterior == -1 || Math.abs(posicoesValidas.get(indexPosicao).x - posicoesValidas.get(indexPosicaoAnterior).x) > 350){
                    int sortearPersonagem = random.nextInt(3);
                    if(sortearPersonagem == 0){
                        if(!fase2Ativada){
                            criarTrower(indexPosicao);
                        }
                        if(fase2Ativada){
                            criarWalking(indexPosicao);
                        }
                    }
                    if(sortearPersonagem == 1){
                        criarVoador(indexPosicao);
                    }
                    if(sortearPersonagem == 2){
                        criarHogamer(indexPosicao);
                    }
                } else {
                    //indexPosicao = random.nextInt(posicoesValidas.size());
                }

                indexPosicaoAnterior = indexPosicao;
            }

        }
    }

    private void determinarPosicoesValidas(){
        if (!fase2Ativada) {
            posicoesValidas.clear();
            Random random = new Random();

            for(int i=0; i<30; i++){
                for(Rectangle plataforma : mapaFase1.getChaos()){
                    float posYplataforma = plataforma.y + plataforma.height + 150;
                    float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                    posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
                }
            }

        } else {
            posicoesValidas.clear();
            Random random = new Random();

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
            // Se a tela inicial marcou o jogo como iniciado mas os objetos ainda não foram criados,
            // cria-os agora para evitar NullPointerException (texturas/objetos nulos).
            if (!objetosCriados) {
                objetosCriados = true;
                criaObjetosJogo();
            }
            
            if (isMultiplayer && remoteMegaMan == null) {
                if (isServer) {
                    remoteMegaMan = new MegaMan(texturaMegaMan2, 0, 0);
                }else{
                    remoteMegaMan = new MegaMan(texturaMegaMan, 0, 0); 
                }
            }

            cameraFoco.set(megaMan.getPosX() + megaMan.getCorpo().getBoundingRectangle().width, 
            megaMan.getPosY() + (megaMan.getCorpo().getBoundingRectangle().height/2));
            camera.position.set(cameraFoco, 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined); 
            
            Gdx.gl.glClearColor(255f, 255f, 255f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            controleFases();

            if(vidasMegaMan > 0){
                atualizarEntidades();
                networkManager.sendInimigos(inimigos);
                //networkManager.sendPinguin(penguin);
                colisoes();

                // NEW: no cliente, detecta colisões entre ataques locais do jogador e os placeholders
                // e envia um EnemyHit para o servidor solicitando que ele chame tomarDano(...) no inimigo autoritativo.
                if (isMultiplayer && !isServer && networkManager != null && megaMan != null) {
                    try {
                        // colete ataques e envie hits para inimigos atingidos; remover ataques que acertarem
                        ArrayList<Ataque> ataques = megaMan.getAtaquesAtivos();
                        // protegemos contra concorrência e modificações durante iteração
                        ArrayList<Ataque> ataquesParaRemover = new ArrayList<>();
                        for (Map.Entry<Integer, Inimigo> e : remoteEnemyMap.entrySet()) {
                            int enemyId = e.getKey();
                            Inimigo inimigoLocal = e.getValue();
                            if (inimigoLocal == null) continue;
                            Rectangle rectInimigo;
                            try {
                                rectInimigo = inimigoLocal.getRect();
                            } catch (Throwable t) {
                                continue;
                            }
                            if (rectInimigo == null) continue;

                            // checa cada ataque do jogador
                            for (Ataque a : ataques) {
                                if (a == null) continue;
                                try {
                                    // tenta obter bounding rectangle do ataque — método pode variar, usamos try/catch
                                    Rectangle rectAtaque = null;
                                    try {
                                        // tentativa comum: Ataque.getRect() ou getCorpo().getBoundingRectangle()
                                        java.lang.reflect.Method mRect = null;
                                        try {
                                            mRect = a.getClass().getMethod("getRect");
                                        } catch (NoSuchMethodException ignore) {
                                            // tentar getCorpo().getBoundingRectangle via reflexão
                                            try {
                                                Object corpo = a.getClass().getMethod("getCorpo").invoke(a);
                                                if (corpo != null) {
                                                    try {
                                                        rectAtaque = (Rectangle) corpo.getClass().getMethod("getBoundingRectangle").invoke(corpo);
                                                    } catch (Throwable ignored) {}
                                                }
                                            } catch (Throwable ignored) {}
                                        }
                                        if (mRect != null) {
                                            Object r = mRect.invoke(a);
                                            if (r instanceof Rectangle) rectAtaque = (Rectangle) r;
                                        }
                                    } catch (Throwable ignore) {}

                                    if (rectAtaque == null) continue;
                                    if (rectAtaque.overlaps(rectInimigo)) {
                                        // damage: tentar obter método getDano() em Ataque, senão usar 1
                                        int damage = 1;
                                        try {
                                            java.lang.reflect.Method mDano = a.getClass().getMethod("getDano");
                                            Object d = mDano.invoke(a);
                                            if (d instanceof Number) damage = ((Number) d).intValue();
                                        } catch (Throwable ignored) {}

                                        // envia hit para o servidor (o servidor chamará inimigo.tomarDano)
                                        networkManager.sendEnemyHit(enemyId, damage);

                                        // marca ataque para remoção local (visual)
                                        ataquesParaRemover.add(a);
                                        break; // passa para próximo inimigo
                                    }
                                } catch (Throwable ignored) {
                                    // não interromper fluxo por causa de reflexões
                                }
                            }
                        }
                        // remove ataques que acertaram (se o Ataque suporta remoção direta)
                        if (!ataquesParaRemover.isEmpty()) {
                            for (Ataque ar : ataquesParaRemover) {
                                try { ataques.remove(ar); } catch (Throwable ignored) {}
                            }
                        }
                    } catch (Throwable t) {
                        Gdx.app.error("Network", "Erro ao detectar hits cliente: " + t.getMessage());
                    }
                }
            }

            // Envia a posição do jogador local e atualiza a posição do jogador remoto
            if (isMultiplayer && networkManager != null) {
                // Enviar posição + estado de animação do jogador para o outro peer
                PlayerPosition pp = new PlayerPosition(megaMan.getPosX(), megaMan.getPosY(), isServer ? 0 : 1);
                try {
                    pp.regionX = megaMan.getRegionX();
                    pp.regionY = megaMan.getRegionY();
                    pp.regionW = megaMan.getRegionWidth();
                    pp.regionH = megaMan.getRegionHeight();
                    pp.paraDireita = megaMan.isParaDireita();
                } catch (Throwable ignored) {}
                networkManager.sendPlayerPosition(pp);
                if (isServer) {
                    networkManager.sendEnemyPositions(); // Envia posições dos inimigos se for servidor
                    
                }
                // Envia ataques recém-criados para o peer (evita reenvio múltiplo usando sentAttacks)
                try {
                    java.util.Set<Ataque> currentAttacks = new java.util.HashSet<>();
                    personagens.reset();
                    while (personagens.hasNext()) {
                        Personagem p = personagens.next();
                        for (Ataque a : p.getAtaquesAtivos()) {
                            if (a == null) continue;
                            currentAttacks.add(a);
                            if (!sentAttacks.contains(a)) {
                                try {
                                    int tipo = 0;
                                    try { if (a.getTipo() != null) tipo = a.getTipo().ordinal(); } catch (Throwable ignored) {}
                                    networkManager.sendTiroPositions(a.getPosX(), a.getPosY(), p.hashCode(), tipo, a.isParaDireita());
                                    sentAttacks.add(a);
                                } catch (Throwable t) {
                                    Gdx.app.error("Network", "Failed to send tiro: " + t.getMessage());
                                }
                            }
                        }
                    }
                    personagens.reset();
                    // cleanup: remove ataques que já foram removidos localmente
                    sentAttacks.retainAll(currentAttacks);
                } catch (Throwable ignored) {}
            }
            System.out.println(megaMan.getPosX() + " - " + megaMan.getPosY());

            mutaSomFundo(); 
            desenhaItens(); 
        }
        super.render(); 
    }

    private void mutaSomFundo(){
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M) && Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT)){ // Tecla M para mutar/desmutar
            if(somPadraoTocando){ 
                somPadrao.pause();
                somPadraoTocando = false; 
            } else {
                somPadrao.play(somPadrao.loop());
                somPadraoTocando = true; 
            }
        }
    }

    private void controleFases(){
        if (!fase2Ativada) {
            if (penguin != null && penguin.getVida() <= 0) {
                if(penguin.isMorreu() && penguin.getDeltaTime() > 3.5f){
                    penguin.setPosicao(-500, -500);
                    fase2Ativada = true;
                    iniciarSegundaFase(true);
                }
            }
        }
        
        if(fase2Ativada){
            if(spark != null && spark.getVida() <= 0){
                if(spark.isMorreu() && spark.getDeltaTime() > 3.5f){
                    spark.setPosicao(-500, -500);
                    fase2Ativada = false;
                    somVitoria.play();
                    setScreen(new TelaVitoria(this));   
                }
            }
        }
        
        if ((megaMan.isMorreu()) && vidasMegaMan <= 0) {
            if(megaMan.getDeltaTime() > 3.5f){
                fase2Ativada = false; 
                somMorte.play();
                setScreen(new TelaGameOver(this));
            }
        }
    }

    public void iniciarSegundaFase(boolean notifyNetwork){
        penguin = null;
        megaMan = null;
        remoteMegaMan = null;

        fase2Ativada = true;

        carregarTexturasFase2();

        inimigos.clear();
        personagens.clear();

        criaPersonagens(fase2Ativada);

        if (notifyNetwork && networkManager != null) {
            try {
                networkManager.sendPhaseChange(true);
            } catch (Throwable t) {
                Gdx.app.error("Network", "Failed to send PhaseChange: " + t.getMessage());
            }
        }
        
    }

    private void atualizarEntidades(){
        if(vidasMegaMan > 0){
            if(megaMan.isMorreu() && megaMan.getDeltaTime() > 3.5f){
                vidasMegaMan--;
                megaMan.setVida(16);
                megaMan.setPosicao(330, 2517);
                megaMan.setMorreu(false);
            }
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

            if(vile != null){
                if(vile.isMorreu() && vile.getDeltaTime() > 3.5f){
                    vile.setPosicao(-500, -500);
                }
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

        gerenciadorColisoes.colisaoMegaManInimigos(megaMan, inimigos);

        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            gerenciadorColisoes.colisaoAtaquesMegaman(megaMan, inimigo.getAtaquesAtivos());
            gerenciadorColisoes.colisaoAtaquesMegamanInimigos(inimigo, megaMan.getAtaquesAtivos());
        }
        inimigos.reset();

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            gerenciadorColisoes.colisaoAtaquesPlataformas(tipoMapa.getRetangulosColisao(), personagem.getAtaquesAtivos());
        }
        personagens.reset();
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
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).draw(batch);
            }
        }
        personagens.reset();
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

    // Define se esta instância do jogo é o servidor
    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
        // Se o NetworkManager já foi criado, recrie-o com a nova configuração
        if (networkManager != null) {
            networkManager.dispose();
            networkManager = new NetworkManager(this, this.isServer);
        }
    }

    public boolean getIsServer (){
        return isServer;
    }

    // Define se o jogo está em modo multiplayer
    public void setIsMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;

        // Se ativou multiplayer depois dos objetos já terem sido criados, inicializa a rede
        if (this.isMultiplayer && objetosCriados && networkManager == null) {
            iniciaMultiplayer();
        }

        // Se desativou multiplayer, descarte o gerenciador de rede
        if (!this.isMultiplayer && networkManager != null) {
            networkManager.dispose();
            networkManager = null;
        }
    }

    public void setSegundaFaseAtivada(boolean fase2Ativada) {
        this.fase2Ativada = fase2Ativada;
    }

    public void reset(){
        vidasMegaMan = 3;
        fase2Ativada = false;
        objetosCriados = false; // permitir recriar depois do reset
        dispose();
        criaObjetosJogo();
        objetosCriados = true;
    }


    public void updateRemotePlayer(PlayerPosition pos) {
        if (remoteMegaMan != null) {
            try {
                remoteMegaMan.setPosicao(pos.x, pos.y);
                // aplicar orientação antes de setRegion para que flip funcione corretamente
                try { remoteMegaMan.setParaDireita(pos.paraDireita); } catch (Throwable ignored) {}
                try { remoteMegaMan.setRegion(pos.regionX, pos.regionY, pos.regionW, pos.regionH); } catch (Throwable ignored) {}
            } catch (Throwable ignored) {
                // fallback: apenas posicionar
                try { remoteMegaMan.setPosicao(pos.x, pos.y); } catch (Throwable ignored2) {}
            }
        }
    }

    public EnemyPosition getEnemyPositions() {
        EnemyPosition pos = new EnemyPosition(); // Inicializa listas vazias
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            pos.x.add(((Personagem) inimigo).getPosX()); // Adiciona posição X
            pos.y.add(((Personagem) inimigo).getPosY()); // Adiciona posição Y
            pos.ids.add(inimigo.hashCode()); // Adiciona ID
            // Determina tipo do inimigo para que o cliente crie o mesmo tipo
            int tipo = 0; // default -> Trower/Voador mapping
            try {
                if (inimigo instanceof Trower) tipo = 0;
                else if (inimigo instanceof Voador) tipo = 1;
                else if (inimigo instanceof Hogamer) tipo = 2;
                else if (inimigo instanceof Walking) tipo = 3;
                else if (inimigo instanceof Vile) tipo = 4;
                else if (inimigo instanceof Spark) tipo = 5;
                else if (inimigo instanceof Pinguim) tipo = 6;
                else tipo = 0;
            } catch (Throwable ignored) { tipo = 0; }
            pos.types.add(tipo);
        }
        inimigos.reset(); 
        return pos;
    }

    public void updateEnemies(EnemyPosition pos) {
        if (!isServer) { // Apenas o cliente atualiza as posições dos inimigos
            for (int i = 0; i < pos.ids.size(); i++) {
                int id = pos.ids.get(i);
                float x = pos.x.get(i);
                float y = pos.y.get(i);
                Inimigo local = remoteEnemyMap.get(id);
                if (local == null) {
                    // Cria placeholder do mesmo tipo enviado pelo servidor
                    int tipo = 0;
                    try { tipo = pos.types.get(i); } catch (Throwable ignored) { tipo = 0; }
                    Inimigo placeholder = null;
                    try {
                        // Garante que texturas estejam carregadas no cliente
                        if (texturaFundo == null || texturaMegaMan == null) carregaTexturasFase1();
                        switch (tipo) {
                            case 0: { // Trower
                                placeholder = new com.tp2.megamanx.Inimigos.Trower(texturaTrower);
                                break;
                            }
                            case 1: { // Voador
                                if (texturaVoador == null) carregaTexturasFase1();
                                placeholder = new com.tp2.megamanx.Inimigos.Voador(texturaVoador);
                                break;
                            }
                            case 2: { // Hogamer
                                if (texturaHogamer == null) carregaTexturasFase1();
                                placeholder = new com.tp2.megamanx.Inimigos.Hogamer(texturaHogamer);
                                break;
                            }
                            case 3: { // Walking (Fase2)
                                if (texturaWalking == null) carregarTexturasFase2();
                                placeholder = new com.tp2.megamanx.Inimigos.Walking(texturaWalking);
                                break;
                            }
                            case 4: { // Vile (boss)
                                if (texturaVile == null) carregarTexturasFase2();
                                Ataque ataqueVile = new Ataque(
                                    new com.badlogic.gdx.graphics.g2d.TextureRegion(
                                        TipoAtaque.BOMBA.getTextura(),
                                        TipoAtaque.BOMBA.getCordX(), TipoAtaque.BOMBA.getCordY(),
                                        TipoAtaque.BOMBA.getLargura(), TipoAtaque.BOMBA.getAltura()
                                    ),
                                    new com.badlogic.gdx.math.Vector2(0.05f, 0.5f), 0, 0,
                                    TipoAtaque.BOMBA, -5, false
                                );
                                placeholder = new com.tp2.megamanx.Inimigos.Vile(texturaVile, ataqueVile);
                                break;
                            }
                            case 5: { // Spark (boss)
                                if (texturaSpark == null) carregarTexturasFase2();
                                Ataque ataqueSpark = new Ataque(
                                    new com.badlogic.gdx.graphics.g2d.TextureRegion(
                                        TipoAtaque.CHOQUE.getTextura(),
                                        TipoAtaque.CHOQUE.getCordX(), TipoAtaque.CHOQUE.getCordY(),
                                        TipoAtaque.CHOQUE.getLargura(), TipoAtaque.CHOQUE.getAltura()
                                    ),
                                    new com.badlogic.gdx.math.Vector2(0.05f, 0.5f), 0, 0,
                                    TipoAtaque.CHOQUE, -5, false
                                );
                                placeholder = new com.tp2.megamanx.Inimigos.Spark(texturaSpark, ataqueSpark);
                                break;
                            }
                            case 6: { // Pinguim: o estado do pinguim é enviado separadamente (updatePinguinState), pular aqui
                                placeholder = null;
                                break;
                            }
                            default: {
                                if (texturaVoador == null) carregaTexturasFase1();
                                placeholder = new com.tp2.megamanx.Inimigos.Voador(texturaVoador);
                                break;
                            }
                        }
                    } catch (Throwable t) {
                        Gdx.app.error("Network", "Failed to instantiate placeholder enemy: " + t.getMessage());
                        try { if (texturaVoador == null) carregaTexturasFase1(); } catch (Throwable ignored) {}
                    }

                    if (placeholder != null) {
                        // placeholder is created as a concrete Personagem subclass (which also implements Inimigo)
                        try { ((Personagem) placeholder).setPosicao(x, y); } catch (Throwable ignored) {}
                        inimigos.add(placeholder);
                        try { personagens.add((Personagem) placeholder); } catch (Throwable t) { /* fallback: ignore if not castable */ }
                        remoteEnemyMap.put(id, placeholder);
                        remotePlaceholders.add(placeholder); // marcar como controlado pelo servidor
                        try { remoteEnemyLastVida.put(id, ((Personagem) placeholder).getVida()); } catch (Throwable ignored) {}
                    }
                } else {
                    ((Personagem) local).setPosicao(x, y);
                }
            }
        }
    }

    // NEW: atualiza ou cria o Pinguim local a partir do DTO recebido pela rede
    public void updatePinguinState(com.tp2.megamanx.NetworkManager.PinguinState state) {
        if (state == null) return;
        // não sobrescrever estado do servidor local; aplicar apenas no cliente
        if (!isServer) {
            try {
                if (texturaPenguin == null) {
                    carregaTexturasFase1(); // garante que textura exista no cliente
                }
                if (penguin == null) {
                    penguin = new Pinguim(texturaPenguin);
                    // evita duplicar nas coleções
                    try { inimigos.add(penguin); } catch (Throwable ignored) {}
                    try { personagens.add(penguin); } catch (Throwable ignored) {}
                }
                try { penguin.setPosicao(state.x, state.y); } catch (Throwable ignored) {}
                try { penguin.setVida(state.vida); } catch (Throwable ignored) {}
            } catch (Throwable t) {
                Gdx.app.error("Network", "updatePinguinState failed: " + t.getMessage());
            }
        }
    }

    // NEW: cria/recebe um tiro vindo pela rede e o adiciona ao personagem correto (ou ao jogo)
    public void handleIncomingTiro(com.tp2.megamanx.PosicaoTiro pt) {
        if (pt == null) return;
        try {
            // garante texturas carregadas
            TipoAtaque.carregarTodasTexturas();
            TipoAtaque tipo = TipoAtaque.values()[Math.max(0, Math.min(pt.tipo, TipoAtaque.values().length - 1))];
            com.badlogic.gdx.graphics.g2d.TextureRegion region = new com.badlogic.gdx.graphics.g2d.TextureRegion(
                tipo.getTextura(), tipo.getCordX(), tipo.getCordY(), tipo.getLargura(), tipo.getAltura()
            );
            com.badlogic.gdx.math.Vector2 escala = new com.badlogic.gdx.math.Vector2(0.3f, 1.2f);
            Ataque novo = new Ataque(region, escala, pt.x, pt.y, tipo, 0, pt.paraDireita);
            novo.setColidiu(false);

            // tenta anexar ao personagem que disparou, se existir
            boolean attached = false;
            personagens.reset();
            while (personagens.hasNext()) {
                Personagem p = personagens.next();
                if (p.hashCode() == pt.id) {
                    try { p.getAtaquesAtivos().add(novo); attached = true; } catch (Throwable ignored) {}
                    break;
                }
            }
            personagens.reset();

            if (!attached) {
                // se não encontrou o emissor, tenta anexar ao remoteMegaMan (player remoto) ou ao player local
                try {
                    if (remoteMegaMan != null) {
                        remoteMegaMan.getAtaquesAtivos().add(novo);
                    } else if (megaMan != null) {
                        megaMan.getAtaquesAtivos().add(novo);
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            Gdx.app.error("Network", "handleIncomingTiro failed: " + t.getMessage());
        }
    }

    // NEW: chamado pelo servidor quando receber EnemyHit — aplica dano no servidor (autoridade)
    public void applyEnemyHit(int enemyId, int damage) {
        if (!isServer) return;
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            if (inimigo.hashCode() == enemyId) {
                try {
                    inimigo.tomarDano(damage);
                } catch (Throwable t) {
                    Gdx.app.error("Game", "Failed applyEnemyHit (tomarDano): " + t.getMessage());
                }

                // se morreu, remover placeholder mappings caso exista (segurança)
                try {
                    // supondo que inimigo.morrer() marque estado; se já morreu, limpe map/set
                    // verifica via reflexão/try para evitar NPEs
                    // (implementação concreta pode fornecer isDead/getVida)
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


    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
    }

    @Override
    public void dispose() {
        if(mapaFase1 != null) {
            mapaFase1.dispose();
        }
        if(mapaFase2 != null) {
            mapaFase2.dispose();
        }
        if(batch != null) {
            batch.dispose();
        }
        
        if (somMorte != null) {
            somMorte.pause();
            somMorte.dispose();
        }
        if (somPadrao != null) {
            somPadrao.pause();
            somPadrao.dispose();
        }
        if (somVitoria != null) {
            somVitoria.pause();
            somVitoria.dispose();
        }
        if (fonteVida != null) {
            fonteVida.dispose();
        }
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
        if (networkManager != null) {
            networkManager.dispose();
        }
        super.dispose();
    }

}
