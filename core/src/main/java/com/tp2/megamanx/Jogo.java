package com.tp2.megamanx;

import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;
import com.tp2.megamanx.inimigos.Hogamer;
import com.tp2.megamanx.inimigos.Inimigo;
import com.tp2.megamanx.inimigos.Pinguim;
import com.tp2.megamanx.inimigos.Trower;
import com.tp2.megamanx.inimigos.Vile;
import com.tp2.megamanx.inimigos.Voador;
import com.tp2.megamanx.inimigos.Walking;
import com.tp2.megamanx.inimigos.Spark;

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
import com.tp2.megamanx.Ataque;
import java.util.Iterator;

public class Jogo extends Game {

    private boolean jogoIniciado = false; 
    private boolean objetosCriados = false; // nova flag para evitar criação duplicada

    // Texturas das duas fases
    private Texture texturaMegaMan;      
    private Texture texturaVoador; 
    
    // Texturas da Fase 1
    private Texture texturaPenguin;            
    private Texture texturaTrower;
    private Texture texturaFundo;       

    // Texturas da Fase 2
    private Texture texturaWalking; 
    private Texture texturaHogamer;
    private Texture texturaVile;
    private Texture texturaSpark;  

    // Componentes de renderização e câmera
    private SpriteBatch batch;                    // Responsável por desenhar texturas na tela
    private OrthographicCamera camera;            // Câmera ortográfica para visualização 2D
    private Vector2 cameraFoco;                   // Ponto de foco da câmera (segue o Mega Man)
    private Viewport viewport;                    // Gerencia diferentes resoluções de tela
    
    // Componentes de fonte e texto
    private BitmapFont fonteVida;                                   // Fonte para exibir informações de vida
    private FreeTypeFontGenerator gerador;                          // Gerador de fontes TTF
    private FreeTypeFontGenerator.FreeTypeFontParameter parametro;  // Parâmetros da fonte

    private ArrayList<Vector2> posicoesValidas;   
    private Random random;                        

    private Mapa mapa, mapaSegundaFase;                           

    private GerenciadorColisoes gerenciadorColisoes; 
    public InimigoIterator inimigos;                 
    private PersonagemIterator personagens;          

    private MegaMan megaMan;          
    private MegaMan remoteMegaMan;
    private int vidasMegaMan = 3;     
    //private boolean gameOver = false; 
    public Pinguim penguin;         

    // Segunda Fase
    private boolean segundaFaseAtivada = false; 
    private Vile vile;
    private Spark spark;

    private Sound somMorte, somVitoria, somPadrao; 
    private boolean somPadraoTocando = false;      

    private ShapeRenderer shapeRenderer;

    // Gerenciamento de rede
    private NetworkManager networkManager;  // Gerencia a comunicação em rede
    public boolean isServer = true;       // Indica se esta instância é o servidor
    private boolean isMultiplayer = false;  // Indica se o jogo está em modo multiplayer
    private Texture texturaMegaMan2;
    public InimigoIterator remoteInimigos;
    private PersonagemIterator remotePersonagens;
    
    // NEW: map para relacionar id (do servidor) -> inimigo local placeholder
    private Map<Integer, Inimigo> remoteEnemyMap = new HashMap<>();
    // NEW: vida conhecida anteriormente para detectar diminuições locais
    private Map<Integer, Integer> remoteEnemyLastVida = new HashMap<>();

    // NEW: conjunto de placeholders remotos (clientes) para que não rodem AI local
    private Set<Inimigo> remotePlaceholders = new HashSet<>();

    @Override
    public void create() {
        // apenas mostra a tela inicial; não cria objetos do jogo aqui
        setScreen(new TelaInicial(this));
        // criaObjetosJogo(); <- removido para aguardar callback da TelaInicial
    }

    // método público que a TelaInicial deve chamar quando terminar (todos os botões clicados)
    public void iniciarJogo() {
        if (objetosCriados) return; // já criado
        objetosCriados = true;
        criaObjetosJogo();
        // marca o jogo como iniciado para que o loop de render comece a atualizar/desenhar
        setJogoIniciado(true);
    }

    private void criaObjetosJogo(){
        batch = new SpriteBatch();                    
        camera = new OrthographicCamera();            
        cameraFoco = new Vector2();                   
        camera.setToOrtho(false, 800, 600);           
        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); 
        shapeRenderer = new ShapeRenderer();          
        
        gerador = new FreeTypeFontGenerator(Gdx.files.internal("assets/GAMERIA.ttf")); 
        parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();        
        parametro.size = 24;                         
        parametro.color = Color.WHITE;               
        fonteVida = gerador.generateFont(parametro);  
        gerador.dispose();                            

        somMorte = Gdx.audio.newSound(Gdx.files.internal("assets/sons/megaman-x-death-sound-effect.mp3"));
        somVitoria = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mmx-stage-clear.mp3"));
        somPadrao = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));

        random = new Random();                       
        posicoesValidas = new ArrayList<>();          

        gerenciadorColisoes = new GerenciadorColisoes(); 
        inimigos = new InimigoIterator();                
        personagens = new PersonagemIterator();           

        if (isMultiplayer && networkManager == null && isServer) {
            iniciaMultiplayer(); // Inicializa o modo multiplayer se ativado
        }

        carregaTexturas();  
        criaMapa();         
        criaPersonagens(segundaFaseAtivada);  

        somPadrao.play(somPadrao.loop()); // Inicia a música de fundo em loop
        somPadraoTocando = true;
        if (isMultiplayer && networkManager == null && !isServer) {
            iniciaMultiplayer(); // Inicializa o modo multiplayer se ativado
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

    private void criaMapa(){
        mapa = new Mapa("assets/maps/Mapa.tmx", 800, 600);
        mapaSegundaFase = new Mapa("assets/maps/MapaSegundaFase.tmx", 800, 600);
    }

    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();    
        texturaFundo = new Texture("assets/fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg"); 
                                 
        texturaMegaMan = new Texture("assets/imagens/MegaMan/mega_man.png"); 
        texturaVoador = new Texture("assets/imagens/Fase1/bee.png");    
        texturaHogamer = new Texture("assets/imagens/Fase2/hogamer.png");

        texturaPenguin = new Texture("assets/imagens/Fase1/penguin.png");
        texturaTrower = new Texture("assets/imagens/Fase1/now.png"); 

        texturaMegaMan2 = new Texture("assets/imagens/MegaMan/mega_man_green.png");
    }

    private void carregarTexturasFase2(){
        if (texturaWalking == null) {
            texturaWalking = new Texture("assets/imagens/Fase2/walking.png"); 
        }
        if (texturaVile == null) {
            texturaVile = new Texture("assets/imagens/Fase2/vile.png");
        }
        if (texturaSpark == null) {
            texturaSpark = new Texture("assets/imagens/Fase2/spark.png");
        }
    }

    private void criaPersonagens(boolean segundaFaseAtivada){

        criarInimigos(segundaFaseAtivada);

        if(isMultiplayer && !isServer){
            megaMan = new MegaMan(texturaMegaMan2,  330, 2517);
        }else{
            megaMan = new MegaMan(texturaMegaMan,  330, 2517); 
        }
        personagens.add(megaMan);      
        
        if(!segundaFaseAtivada){
            if (penguin != null) personagens.add(penguin); 
        }                     

        if (segundaFaseAtivada) {
            if (vile != null) personagens.add(vile); 
            if (spark != null) personagens.add(spark);                    
        }
    }

    private void criarTrower(int indexPosicao){
        Ataque ataqueTrower = new Ataque(
            new TextureRegion(
                TipoAtaque.BOLA_NEVE.getTextura(), 
		        TipoAtaque.BOLA_NEVE.getCordX(), 
                TipoAtaque.BOLA_NEVE.getCordY(),
		        TipoAtaque.BOLA_NEVE.getLargura(), 
                TipoAtaque.BOLA_NEVE.getAltura()
            ), 
		    new Vector2(0.05f, 0.5f), 0, 0, 
            TipoAtaque.BOLA_NEVE, -5, false
        );

        Trower trower = new Trower(texturaTrower, ataqueTrower);

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

    private void criarInimigos(boolean segundaFaseAtivada){
        if (isServer) {
            if(!segundaFaseAtivada){
                penguin = new Pinguim(texturaPenguin); 
                inimigos.add(penguin);
            }else if (segundaFaseAtivada) {
                criarVile();
                criarSpark();
            }
                
            determinarPosicoesValidas(); 

            int indexPosicaoAnterior = -1;
            for(int i=0; i<15; i++){
                int indexPosicao = random.nextInt(posicoesValidas.size());

                // Garante espaçamento mínimo entre inimigos
                if(indexPosicaoAnterior == -1 || Math.abs(posicoesValidas.get(indexPosicao).x - posicoesValidas.get(indexPosicaoAnterior).x) > 350){
                    int sortearPersonagem = random.nextInt(3);
                    if(sortearPersonagem == 0){
                        if(!segundaFaseAtivada){
                            criarTrower(indexPosicao);
                        }
                        if(segundaFaseAtivada){
                            criarWalking(indexPosicao);
                        }
                    }
                    if(sortearPersonagem == 1){
                        criarVoador(indexPosicao);
                    }
                    if(sortearPersonagem == 2){
                        criarHogamer(indexPosicao);
                    }
                }
                indexPosicaoAnterior = indexPosicao;
            }
            networkManager.sendInimigos(inimigos);
            networkManager.sendPinguin(penguin);
        }
    }

    private void determinarPosicoesValidas(){
        if (!segundaFaseAtivada) {
            posicoesValidas.clear();
            Random random = new Random();
            for(int i=0; i<10; i++){
                for(Rectangle plataforma : mapa.getChaos()){
                    float posYplataforma = plataforma.y + plataforma.height + 150;
                    float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                    posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
                }
            }
        }else{
            posicoesValidas.clear();
            Random random = new Random();
            for(int i=0; i<10; i++){
                for(Rectangle plataforma : mapaSegundaFase.getChaos()){
                    float posYplataforma = plataforma.y + plataforma.height + 150;
                    float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                    posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
                }
            }
        }
        
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
                atualizarPersonagens();
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
                networkManager.sendPlayerPosition(megaMan.getPosX(), megaMan.getPosY(), isServer ? 0 : 1); // ID 0 para servidor, 1 para cliente
                if (isServer) {
                    networkManager.sendEnemyPositions(); // Envia posições dos inimigos se for servidor
                    
                }
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
        // Proteção: verificar primeiro se penguin != null e usar && (short-circuit)
        if (!segundaFaseAtivada) {
            if (penguin != null && penguin.getVida() <= 0) {
                iniciarSegundaFase();
            }
        }
        
        if(segundaFaseAtivada){
            if(spark != null){
                if(spark.getVida() <= 0){
                    somVitoria.play();
                    segundaFaseAtivada = false; 
                    jogoIniciado = false;
                    setScreen(new TelaVitoria(this));
                }
            }
        }
        
        if ((megaMan.isMorreu()) && vidasMegaMan <= 0) {
            somMorte.play();
            segundaFaseAtivada = false; 
            jogoIniciado = false;
            setScreen(new TelaGameOver(this));
            return;
        }
    }

    private void iniciarSegundaFase(){
        penguin = null;
        megaMan = null;
        remoteMegaMan = null;

        
        segundaFaseAtivada = true;

        carregarTexturasFase2();

        inimigos.clear();
        personagens.clear();

        criaPersonagens(segundaFaseAtivada);
     }

    private void atualizarPersonagens(){
        ataquesPersonagens();

        megaMan.confereMortePorQueda(); 

        if((megaMan.isMorreu()) && vidasMegaMan > 0){
            vidasMegaMan--;
            megaMan.setVida(16);
            megaMan.setPosicao(330, 2517);
            megaMan.setMorreu(false);
            //criarInimigos(segundaFaseAtivada);
        }

        if(vidasMegaMan <= 0){
            megaMan.setMorreu(true);
        }

        if(!segundaFaseAtivada){
            if (penguin != null) penguin.atualizar();
        } 

        if (segundaFaseAtivada) {
            if (vile != null) vile.atualizar();
            if (spark != null) spark.atualizar();

            if (vile != null && vile.getVida() <= 0) {
                vile.morrer();
            }
            if (spark != null && spark.getVida() <= 0) {
                spark.morrer();
            }
        }

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.mover();
            personagem.atacar();
            personagem.morrer();
        }
        personagens.reset();

        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            // Skip calling AI-follow for remote placeholders controlled by server
            if (remotePlaceholders.contains(inimigo)) {
                continue;
            }
            inimigo.setPosicaoMegaMan(new Vector2(megaMan.getPosX(), megaMan.getPosY()));
        }
        inimigos.reset();

    }

    private void ataquesPersonagens(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).disparar();
                //personagem.getAtaquesAtivos().remove(personagem.getAtaquesAtivos().get(i));
            }
        }
        personagens.reset();
    }

    private void colisoes() {

        Mapa mapaAux = segundaFaseAtivada ? mapaSegundaFase : mapa;

        gerenciadorColisoes.colisaoPersonagensPlataformas(mapaAux.getRetangulosColisao(), personagens);

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            gerenciadorColisoes.colisaoPersonagemParedes(
                mapaAux.getRetangulosColisaoParedeDireita(),
                mapaAux.getRetangulosColisaoParedeEsquerda(),
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
            gerenciadorColisoes.colisaoAtaquesPlataformas(mapaAux.getRetangulosColisao(), personagem.getAtaquesAtivos());
        }
        personagens.reset();
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

    private void desenharVidas() {
        float larguraBarra = 20;
        float alturaBarra = 200;
        float margem = 30;
        float borda = 4;

        desenhaVidaMegaMan(larguraBarra, alturaBarra, margem, borda);

        float distancia = 0;
        float vidaMaxBoss = 0;
        float vidaAtualBoss = 0;

        if(!segundaFaseAtivada && (penguin != null)){
            distancia = megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
                penguin.getCorpo().getBoundingRectangle().getCenter(new Vector2())
            ); 
            vidaMaxBoss = 32f;
            vidaAtualBoss = penguin.getVida();
        }
        else if(segundaFaseAtivada && (spark != null)){
            distancia = megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
                spark.getCorpo().getBoundingRectangle().getCenter(new Vector2())
            ); 
            vidaMaxBoss = 40f;
            vidaAtualBoss = spark.getVida();
        }

        if (distancia < 600) {
            float proporcaoPenguin = Math.max(vidaAtualBoss / vidaMaxBoss, 0);

            float barraBossX = camera.position.x + camera.viewportWidth / 2 - margem - larguraBarra;
            float barraBossY = camera.position.y - alturaBarra / 2;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
            shapeRenderer.rect(barraBossX - borda, barraBossY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(barraBossX, barraBossY, larguraBarra, alturaBarra);

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(barraBossX, barraBossY, larguraBarra, alturaBarra * proporcaoPenguin);

            shapeRenderer.end();
        }

    }

    private void desenhaItens(){
        Mapa mapaAux = segundaFaseAtivada ? mapaSegundaFase : mapa;
        batch.begin();
        batch.draw(texturaFundo, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight );
        batch.end();
        mapaAux.render(camera);

        batch.begin();
        desenharEntidades(); 
       
        fonteVida.draw(batch, "Vida MegaMan: " + megaMan.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 20);
        
        if(!segundaFaseAtivada && (penguin != null)){
            fonteVida.draw(batch, "Vida Penguin: " + penguin.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 50);
        }
        else if(segundaFaseAtivada && (spark != null)){
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

    public void setSegundaFaseAtivada(boolean ativada) {
        this.segundaFaseAtivada = ativada;
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
    }

    public void reset(){
        vidasMegaMan = 3;
        segundaFaseAtivada = false;
        objetosCriados = false; // permitir recriar depois do reset
        dispose();
        //create();
        // Opcional: abrir novamente a tela inicial ou recriar imediatamente:
        // setScreen(new TelaInicial(this)); // se quiser voltar à tela inicial
        // ou recriar diretamente:
        criaObjetosJogo();
        objetosCriados = true;
    }

    public void updateRemotePlayer(PlayerPosition pos) {
        if (remoteMegaMan != null) {
            remoteMegaMan.setPosicao(pos.x, pos.y);
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
                    // Cria placeholder – usamos Voador como genérico visual
                    if (texturaVoador == null) carregaTexturas();
                    Voador placeholder = new Voador(texturaVoador);
                    placeholder.setPosicao(x, y);
                    inimigos.add(placeholder);
                    personagens.add(placeholder);
                    remoteEnemyMap.put(id, placeholder);
                    remotePlaceholders.add(placeholder); // marcar como controlado pelo servidor
                    // inicializar vida conhecida (se a classe tiver)
                    try { remoteEnemyLastVida.put(id, placeholder.getVida()); } catch (Throwable ignored) {}
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
                    carregaTexturas(); // garante que textura exista no cliente
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
    public void dispose() {
        if(mapa != null) {
            mapa.dispose();
        }
        if(mapaSegundaFase != null) {
            mapaSegundaFase.dispose();
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
