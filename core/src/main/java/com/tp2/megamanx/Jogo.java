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
    //private boolean objetosCriados = false; 

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

    private Mapa mapa;                           

    private GerenciadorColisoes gerenciadorColisoes; 
    private InimigoIterator inimigos;                 
    private PersonagemIterator personagens;          

    private MegaMan megaMan;          
    private MegaMan remoteMegaMan;
    private int vidasMegaMan = 3;     
    //private boolean gameOver = false; 
    private Pinguim penguin;         

    // Segunda Fase
    private boolean segundaFaseAtivada = false; 
    private Vile vile;
    private Spark spark;

    private Sound somMorte, somVitoria, somPadrao; 
    private boolean somPadraoTocando = false;      

    private ShapeRenderer shapeRenderer;

    // Gerenciamento de rede
    private NetworkManager networkManager;  // Gerencia a comunicação em rede
    private boolean isServer = false;       // Indica se esta instância é o servidor
    private boolean isMultiplayer = false;  // Indica se o jogo está em modo multiplayer
    

    @Override
    public void create() {
        setScreen(new TelaInicial(this)); 
        criaObjetosJogo();
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

        carregaTexturas();  
        criaMapa();         
        criaPersonagens(segundaFaseAtivada);  

        somPadrao.play(somPadrao.loop()); // Inicia a música de fundo em loop
        somPadraoTocando = true;
        if (isMultiplayer && networkManager == null) {
            iniciaMultiplayer(); // Inicializa o modo multiplayer se ativado
        }
    }

    private void iniciaMultiplayer() {
        if (isMultiplayer) {
            System.out.println(isServer);
            networkManager = new NetworkManager(this, isServer); // Inicializa o gerenciador de rede
        }
    }

    private void criaMapa(){
        mapa = new Mapa("assets/maps/Mapa.tmx", 800, 600);
    }

    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();    
        texturaFundo = new Texture("assets/fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg"); 
                                 
        texturaMegaMan = new Texture("assets/imagens/MegaMan/mega_man.png"); 
        texturaVoador = new Texture("assets/imagens/Fase1/bee.png");    
        texturaHogamer = new Texture("assets/imagens/Fase2/hogamer.png");

        texturaPenguin = new Texture("assets/imagens/Fase1/penguin.png");
        texturaTrower = new Texture("assets/imagens/Fase1/now.png"); 
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
        megaMan = new MegaMan(texturaMegaMan,  330, 2517); 
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
        if(!segundaFaseAtivada){
            penguin = new Pinguim(texturaPenguin); 
            inimigos.add(penguin);
        }else if (segundaFaseAtivada) {
            criarVile();
            criarSpark();
        }
            
        determinarPosicoesValidas(); 

        int indexPosicaoAnterior = -1;
        for(int i=0; i<20; i++){
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
        
    }

    private void determinarPosicoesValidas(){
        posicoesValidas.clear();
        Random random = new Random();
        for(int i=0; i<10; i++){
            for(Rectangle plataforma : mapa.getChaos()){
                float posYplataforma = plataforma.y + plataforma.height + 150;
                float posXplataforma = random.nextFloat() * ((plataforma.x + plataforma.width) - plataforma.x) + plataforma.x;
                posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
            }
        }
    }

    @Override
    public void render() {
        if (jogoIniciado) {

            //if (objetosCriados == false) {
               // criaObjetosJogo(); 
               // objetosCriados = true; 
                if (isMultiplayer && remoteMegaMan == null) {
                    remoteMegaMan = new MegaMan(texturaMegaMan, 0, 0); 
                }
           // }

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
                colisoes();
            }

            // Envia a posição do jogador local e atualiza a posição do jogador remoto
            if (isMultiplayer && networkManager != null) {
                networkManager.sendPlayerPosition(megaMan.getPosX(), megaMan.getPosY(), isServer ? 0 : 1); // ID 0 para servidor, 1 para cliente
                if (isServer) {
                    networkManager.sendEnemyPositions(); // Envia posições dos inimigos se for servidor
                }
            }

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
        if(!segundaFaseAtivada){
            if(penguin != null & penguin.getVida() <= 0){
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
        
        if (megaMan.isMorreu() && vidasMegaMan <= 0) {
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

        if(megaMan.isMorreu() && vidasMegaMan > 0){
            vidasMegaMan--;
            megaMan.setVida(16);
            megaMan.setPosicao(330, 2517);
            megaMan.setMorreu(false);
            criarInimigos(segundaFaseAtivada);
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

        gerenciadorColisoes.colisaoPersonagensPlataformas(mapa.getRetangulosColisao(), personagens);

        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            gerenciadorColisoes.colisaoPersonagemParedes(
                mapa.getRetangulosColisaoParedeDireita(),
                mapa.getRetangulosColisaoParedeEsquerda(),
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
            gerenciadorColisoes.colisaoAtaquesPlataformas(mapa.getRetangulosColisao(), personagem.getAtaquesAtivos());
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
        batch.begin();
        batch.draw(texturaFundo, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight );
        batch.end();
        mapa.render(camera);

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
    }

    public boolean getIsServer (){
        return isServer;
    }

    // Define se o jogo está em modo multiplayer
    public void setIsMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
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
        dispose();
        //create();
        criaObjetosJogo();
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
            int numEnemies = Math.min(inimigos.getColecao().tamanho(), pos.x.size());
            for (int i = 0; i < numEnemies; i++) {
                Inimigo inimigo = inimigos.get(i);
                ((Personagem) inimigo).setPosicao(pos.x.get(i), pos.y.get(i));
            }
        }
    }

    @Override
    public void dispose() {
        if(mapa != null) {
            mapa.dispose();
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
