package com.tp2.megamanx;

// Importações dos iteradores personalizados para gerenciamento de inimigos e personagens
import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;

import java.util.Random;
import java.util.ArrayList;

// Importações do framework LibGDX para desenvolvimento de jogos
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

/**
 * Classe principal do jogo Mega Man X
 * Estende a classe Game do LibGDX e gerencia toda a lógica do jogo
 * Responsável por controlar o loop principal, renderização, colisões e estados do jogo
 */
public class Jogo extends Game {

    private boolean jogoIniciado = false; // Flag para indicar se o jogo foi iniciado
    private boolean objetosCriados = false; // Flag para indicar se os objetos do jogo foram criados

    // Texturas dos personagens principais e elementos visuais
    private Texture texturaMegaMan;      // Textura do personagem jogável Mega Man
    private Texture texturaPenguin;      // Textura do chefe Penguin
    private Texture texturaTrower;       // Textura do inimigo Trower
    private Texture texturaJaminger;     // Textura do inimigo Jaminger
    private Texture texturaFundo;        // Textura do fundo do jogo

    // Componentes de renderização e câmera
    private SpriteBatch batch;                    // Responsável por desenhar texturas na tela
    private OrthographicCamera camera;            // Câmera ortográfica para visualização 2D
    private Vector2 cameraFoco;                   // Ponto de foco da câmera (segue o Mega Man)
    private Viewport viewport;                    // Gerencia diferentes resoluções de tela
    
    // Componentes de fonte e texto
    private BitmapFont fonteVida;                 // Fonte para exibir informações de vida
    private FreeTypeFontGenerator gerador;        // Gerador de fontes TTF
    private FreeTypeFontGenerator.FreeTypeFontParameter parametro; // Parâmetros da fonte

    // Geração aleatória de posições para inimigos
    private ArrayList<Vector2> posicoesValidas;   // Lista de posições válidas para spawn de inimigos
    private Random random;                        // Gerador de números aleatórios

    // Mapa do jogo
    private Mapa mapa;                           // Instância do mapa carregado do arquivo TMX

    // Gerenciadores e coleções de entidades
    private GerenciadorColisoes gerenciadorColisoes; // Gerencia todas as colisões do jogo
    private InimigoIterator inimigos;                 // Iterator para percorrer lista de inimigos
    private PersonagemIterator personagens;           // Iterator para percorrer lista de personagens

    // Personagens principais
    private MegaMan megaMan;          // Personagem principal jogável
    private MegaMan remoteMegaMan; // Personagem do segundo jogador (multiplayer)
    private int vidasMegaMan = 3;     // Número de vidas restantes do Mega Man
    private boolean gameOver = false; // Flag indicando se o jogo terminou
    private Pinguim penguin;          // Chefe principal do jogo

    // Efeitos sonoros
    private Sound somMorte, somVitoria, somPadrao; // Sons de morte, vitória e música de fundo
    private boolean somPadraoTocando = false;      // Estado do som de fundo

    // Renderização de formas geométricas (barras de vida)
    private ShapeRenderer shapeRenderer;

    // Gerenciamento de rede
    private NetworkManager networkManager; // Gerencia a comunicação em rede
    private boolean isServer = false; // Indica se esta instância é o servidor
    private boolean isMultiplayer = false; // Indica se o jogo está em modo multiplayer
    private String[] args; // Argumentos de linha de comando

    // Segunda Fase
    private boolean segundaFaseAtivada = false; // Flag para indicar se a segunda fase foi ativada
    private Pinguim penguin2; // Segundo chefe Penguin na segunda fase



    /**
     * Método chamado na inicialização do jogo
     * Configura a tela inicial e cria todos os objetos necessários
     */
    @Override
    public void create() {
        setScreen(new TelaInicial(this)); // Exibe tela inicial
    }

    /**
     * Atualiza a posição do jogador remoto (segundo jogador) no modo multiplayer
     * @param pos Objeto contendo a nova posição e ID do jogador
     */
    /* 
    public void setArgs(String[] args) { // Puxa os Argumentos da Linha de Comando
        this.args = args;
        if (args != null && args.length > 0) { // Verifica se os argumentos são válidos
            if ("server".equals(args[0])) { // Verifica se o argumento é "server"
                isServer = true;// O jogo abre como Servidor
                isMultiplayer = true; // Ativa modo multiplayer
            } else if ("client".equals(args[0])) { // Verifica se o argumento é "client"
                isServer = false; // O jogo abre como Cliente
                isMultiplayer = true; // Ativa modo multiplayer
            } else {
                isServer = true; // O jogo abre como Servidor
                isMultiplayer = false; // Modo single player
            }
        } else {
            isServer = true; // O jogo abre como Servidor
            isMultiplayer = false; // Modo single player
        }
    }
    */

    /**
     * Método responsável por criar e inicializar todos os objetos do jogo
     * Configura câmera, renderizadores, fontes, sons e entidades
     */
    private void criaObjetosJogo(){
        batch = new SpriteBatch();                    // Cria o SpriteBatch para desenhar texturas
        camera = new OrthographicCamera();            // Cria câmera ortográfica para visão 2D
        cameraFoco = new Vector2();                   // Inicializa o ponto de foco da câmera
        camera.setToOrtho(false, 800, 600);           // Configura câmera com resolução 800x600
        viewport = new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Viewport adaptável
        shapeRenderer = new ShapeRenderer();          // Renderizador para formas geométricas
        
        // Configuração da fonte para exibir textos
        gerador = new FreeTypeFontGenerator(Gdx.files.internal("assets/GAMERIA.ttf")); // Carrega arquivo de fonte
        parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();        // Cria parâmetros da fonte
        parametro.size = 24;                          // Define tamanho da fonte
        parametro.color = Color.WHITE;                // Define cor branca para a fonte
        fonteVida = gerador.generateFont(parametro);  // Gera a fonte com os parâmetros definidos
        gerador.dispose();                            // Libera recursos do gerador

        // Carregamento dos efeitos sonoros
        somMorte = Gdx.audio.newSound(Gdx.files.internal("assets/sons/megaman-x-death-sound-effect.mp3"));
        somVitoria = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mmx-stage-clear.mp3"));
        somPadrao = Gdx.audio.newSound(Gdx.files.internal("assets/sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));

        random = new Random();                        // Gerador de números aleatórios
        posicoesValidas = new ArrayList<>();          // Lista para armazenar posições válidas

        gerenciadorColisoes = new GerenciadorColisoes(); // Gerenciador de colisões
        inimigos = new InimigoIterator();                 // Iterator para inimigos
        personagens = new PersonagemIterator();           // Iterator para personagens

        carregaTexturas();  // Carrega todas as texturas necessárias
        criaMapa();         // Cria e configura o mapa do jogo
        criaPersonagens(segundaFaseAtivada);  // Cria todos os personagens do jogo

        somPadrao.play(somPadrao.loop()); // Inicia a música de fundo em loop
        somPadraoTocando = true;
    }

    private void iniciaMultiplayer() {
        if (isMultiplayer) {
            networkManager = new NetworkManager(this, isServer); // Inicializa o gerenciador de rede
        }
    }

    /**
     * Cria e configura o mapa do jogo
     * Carrega o arquivo TMX do mapa com as dimensões especificadas
     */
    private void criaMapa(){
        mapa = new Mapa("assets/maps/Mapa.tmx", 800, 600);
    }

    /**
     * Carrega todas as texturas necessárias para o jogo
     * Inclui texturas dos personagens, inimigos e elementos visuais
     */
    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();                                           // Carrega texturas dos ataques
        texturaMegaMan = new Texture("assets/imagens/MegaMan/megaMan.png");                 // Textura do Mega Man
        texturaPenguin = new Texture("assets/imagens/ChilPenguin/inimigos/Penguin/penguin.png"); // Textura do Penguin
        texturaTrower = new Texture("assets/imagens/ChilPenguin/inimigos/now.png");         // Textura do Trower
        texturaJaminger = new Texture("assets/imagens/ChilPenguin/inimigos/jaminger.png");  // Textura do Jaminger
        texturaFundo = new Texture("assets/fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg"); // Fundo do jogo
    }

    /**
     * Cria todos os personagens do jogo, incluindo MegaMan e inimigos
     */
    private void criaPersonagens(boolean segundaFase){
        criarInimigos(segundaFase);
        megaMan = new MegaMan(texturaMegaMan, 330, 2517); // Cria o MegaMan na posição inicial
        personagens.add(megaMan);                         // Adiciona MegaMan à lista de personagens
        personagens.add(penguin);                         // Adiciona o chefe Penguin
        if (segundaFaseAtivada) {
            personagens.add(penguin2);                    // Adiciona o segundo chefe Penguin na segunda fase
            
        }
    }

    /**
     * Cria e posiciona todos os inimigos do jogo
     * Sorteia posições válidas e alterna entre tipos de inimigos
     */
    private void criarInimigos(boolean segundaFase){
        penguin = new Pinguim(texturaPenguin, 11635, 3000); // Cria o chefe Penguin
        inimigos.add(penguin);
        if (segundaFase) {
            penguin2 = new Pinguim(texturaPenguin, 5855, 900); // Cria o segundo chefe Penguin
            inimigos.add(penguin2); 
        }
        
        // Cria ataques padrões para inimigos
        Ataque ataqueTrower = new Ataque(new TextureRegion(TipoAtaque.BOLA_NEVE.getTextura(), 
		TipoAtaque.BOLA_NEVE.getCordX1(), TipoAtaque.BOLA_NEVE.getCordY1(),
		TipoAtaque.BOLA_NEVE.getLargura1(), TipoAtaque.BOLA_NEVE.getAltura1()), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.BOLA_NEVE, -5);

        Ataque ataqueJaminger = new Ataque(new TextureRegion(TipoAtaque.DISCO.getTextura(), 
		TipoAtaque.DISCO.getCordX1(), TipoAtaque.DISCO.getCordY1(),
		TipoAtaque.DISCO.getLargura1(), TipoAtaque.DISCO.getAltura1()), 
		0, 0, new Vector2(0.05f, 0.5f), TipoAtaque.DISCO, -5);
            
        determinarPosicoesValidas(); // Calcula posições válidas para spawn

        int indexPosicaoAnterior = -1;
        for(int i=0; i<15; i++){
            int indexPosicao = random.nextInt(posicoesValidas.size());

            // Garante espaçamento mínimo entre inimigos
            if(indexPosicaoAnterior == -1 || Math.abs(posicoesValidas.get(indexPosicao).x - posicoesValidas.get(indexPosicaoAnterior).x) > 800){
                int sortearPersonagem = random.nextInt(2);
                if(sortearPersonagem == 0){
                    Jaminger jaminger = new Jaminger(texturaJaminger, 0, 
                    0, ataqueJaminger, 0, 5);

                    float posX = posicoesValidas.get(indexPosicao).x + jaminger.getCorpo().getBoundingRectangle().width;
                    float posY = posicoesValidas.get(indexPosicao).y;
                    jaminger.setPosicao(posX, posY);

                    inimigos.add(jaminger);
                    personagens.add(jaminger);
                }else{
                    Trower trower = new Trower(texturaTrower, 0, 
                    0, ataqueTrower, 0, 5);

                    float posX = posicoesValidas.get(indexPosicao).x - trower.getCorpo().getBoundingRectangle().width;
                    float posY = posicoesValidas.get(indexPosicao).y;
                    
                    trower.setPosicao(posX, posY);
                    inimigos.add(trower);
                    personagens.add(trower);
                }
            }
            indexPosicaoAnterior = indexPosicao;
        }
        
    }

    /**
     * Determina todas as posições válidas para spawn de inimigos com base nas plataformas do mapa
     */
    private void determinarPosicoesValidas(){
        posicoesValidas.clear();
        for(Rectangle plataforma : mapa.getChaos()){
                float posYplataforma = plataforma.y + plataforma.height;
                float posXplataforma = plataforma.x + plataforma.width;
                posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
        }
        System.out.println("Posições válidas encontradas: " + posicoesValidas.size());
    }

    /**
     * Loop principal de renderização do jogo
     * Atualiza câmera, limpa tela, processa lógica de jogo e desenha elementos
     */
    @Override
    public void render() {
        if (jogoIniciado) {
            if (objetosCriados == false) {
                criaObjetosJogo(); // Cria os objetos do jogo se ainda não foram criados
                objetosCriados = true; // Marca que os objetos foram criados
                if (isMultiplayer && remoteMegaMan == null) {
                    remoteMegaMan = new MegaMan(texturaMegaMan, 0, 0); // Inicializa o personagem remoto após carregar texturas
                }
            }
            // Atualiza o foco da câmera para seguir o MegaMan
            cameraFoco.set(megaMan.getPosX() + megaMan.getCorpo().getBoundingRectangle().width, 
            megaMan.getPosY() + (megaMan.getCorpo().getBoundingRectangle().height/2));
            camera.position.set(cameraFoco, 0);
            camera.update();
            batch.setProjectionMatrix(camera.combined); 
            
            // Limpa a tela com cor branca
            Gdx.gl.glClearColor(255f, 255f, 255f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Se o MegaMan não morreu, processa lógica de ataques, atualização e colisões
            if(!megaMan.isMorreu()){
                ataquesPersonagens();
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

            mutaSomFundo(); // Verifica input para mutar/desmutar som de fundo
            desenhaItens(); // Desenha todos os elementos visuais
            //System.out.println("Posicao X MegaMan: " + megaMan.getPosX() + " | Posicao Y MegaMan: " + megaMan.getPosY());
        }
        super.render(); // Chama o método render da classe pai
    }

    /* Muta o som de fundo */
    private void mutaSomFundo(){
        if(Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.M) && Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.SHIFT_LEFT)){ // Tecla M para mutar/desmutar
            if(somPadraoTocando){ // Se o som está tocando, pausa
                somPadrao.pause();
                somPadraoTocando = false; // Atualiza estado para pausado
            } else {
                somPadrao.play(somPadrao.loop());
                somPadraoTocando = true; // Atualiza estado para tocando
            }
        }
    }

    /**
     * Atualiza todos os personagens do jogo
     * Move, executa ataques, verifica mortes e troca de telas
     */
    private void atualizarPersonagens(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.mover();
            personagem.atacar();
            personagem.morrer();
            // Se o chefe morreu, exibe tela de vitória
            if(penguin.getVida() <= 0 && !segundaFaseAtivada){
                segundaFaseAtivada = true; // Marca que a segunda fase foi ativada
                dispose();
                criaObjetosJogo(); // Reinicia o jogo para a segunda fase
            } 
            
            // Se o chefe morreu na segunda fase, exibe tela de vitória
            else if(penguin.getVida() <= 0 && segundaFaseAtivada){
                if(!gameOver){
                    somVitoria.play();
                    gameOver = true;
                    segundaFaseAtivada = false; // Reseta a flag para futuras partidas
                    setScreen(new TelaVitoria(this));
                }
            }
            // Se o MegaMan morreu, processa vidas e game over
            if (megaMan.isMorreu() && !gameOver) {
                somMorte.play();
                vidasMegaMan--;
                if (vidasMegaMan > 0) {
                    dispose();
                    criaObjetosJogo();
                    gameOver = false;
                } else {
                    gameOver = true;
                    segundaFaseAtivada = false; // Reseta a flag para futuras partidas
                    setScreen(new TelaGameOver(this));
                }
            }
            megaMan.confereMortePorQueda(); // Verifica se MegaMan caiu do mapa
        }
        personagens.reset();

        // Atualiza posição do MegaMan para todos os inimigos
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            inimigo.setPosXmegaMan(megaMan.getPosX());
        }
        inimigos.reset();

        penguin.atualizar(); // Atualiza lógica do chefe
        if (segundaFaseAtivada) {
            penguin2.atualizar(); // Atualiza lógica do segundo chefe na segunda fase
        }else if (segundaFaseAtivada && penguin2.getVida() <= 0) {
            penguin2.morrer(); // Se o segundo chefe morreu, processa morte
        }
    }

    /**
     * Atualiza e dispara todos os ataques ativos dos personagens
     */
    private void ataquesPersonagens(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).disparar();
            }
        }
        personagens.reset();
    }

    /**
     * Gerencia todas as colisões do jogo (personagens, plataformas, ataques, inimigos)
     */
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

    /**
     * Desenha as barras de vida do MegaMan e do chefe Penguin na tela
     * Inclui lógica para exibir barra do chefe apenas quando próximo
     */
    private void desenharVida() {
        float larguraBarra = 20;
        float alturaBarra = 200;
        float margem = 30;
        float borda = 4;

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

        // Exibe barra de vida do chefe apenas se estiver próximo do MegaMan
        float distancia = megaMan.getCorpo().getBoundingRectangle().getCenter(new Vector2()).dst(
            penguin.getCorpo().getBoundingRectangle().getCenter(new Vector2())
        );
        if (distancia < 600) {
            float vidaMaxPenguin = 32f;
            float vidaAtualPenguin = penguin.getVida();
            float proporcaoPenguin = Math.max(vidaAtualPenguin / vidaMaxPenguin, 0);

            float barraPenguinX = camera.position.x + camera.viewportWidth / 2 - margem - larguraBarra;
            float barraPenguinY = camera.position.y - alturaBarra / 2;

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0, 0.1f, 0.5f, 1));
            shapeRenderer.rect(barraPenguinX - borda, barraPenguinY - borda, larguraBarra + 2 * borda, alturaBarra + 2 * borda);

            shapeRenderer.setColor(Color.DARK_GRAY);
            shapeRenderer.rect(barraPenguinX, barraPenguinY, larguraBarra, alturaBarra);

            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rect(barraPenguinX, barraPenguinY, larguraBarra, alturaBarra * proporcaoPenguin);

            shapeRenderer.end();
        }
    }

    /**
     * Desenha todos os elementos visuais do jogo (fundo, entidades, HUD)
     */
    private void desenhaItens(){
        batch.begin();
        // Desenha o fundo do jogo
        batch.draw(texturaFundo, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight );
        batch.end();

        // Renderiza o mapa
        mapa.render(camera);

        batch.begin();
        desenharEntidades(); // Desenha personagens e ataques
        // Exibe informações de vida no topo da tela
        fonteVida.draw(batch, "Vida MegaMan: " + megaMan.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 20);
        fonteVida.draw(batch, "Vida Penguin: " + penguin.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 50);
        desenharVida(); // Desenha barras de vida
        batch.end();
        
        // Opcional: desenha retângulos de colisão para debug
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        // for (Rectangle r : mapa.getChaos()) {
        //     shapeRenderer.rect(r.x, r.y, r.width, r.height);
        // }
        shapeRenderer.end();
    }

    /**
     * Desenha todos os personagens e ataques ativos na tela
     */
    private void desenharEntidades(){
        desenharAtaques(); // Desenha todos os ataques ativos
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.draw(batch);
        }
        personagens.reset();

        // Desenha o jogador remoto (segundo jogador) se estiver ativo
        if (remoteMegaMan != null && (remoteMegaMan.getPosX() != 0 || remoteMegaMan.getPosY() != 0)) {
            remoteMegaMan.draw(batch);
        }
    }

    /**
     * Desenha todos os ataques ativos de todos os personagens
     */
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
    
    /*
     * Setters
     */

    // Inicia o jogo, chamado pela tela inicial
    public void setJogoIniciado(boolean iniciado) {
        this.jogoIniciado = iniciado;
    }

    // Define se esta instância do jogo é o servidor
    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    // Define se o jogo está em modo multiplayer
    public void setIsMultiplayer(boolean isMultiplayer) {
        this.isMultiplayer = isMultiplayer;
        if (isMultiplayer && networkManager == null) {
            iniciaMultiplayer(); // Inicializa o modo multiplayer se ativado
        }
    }

    public void setSegundaFaseAtivada(boolean ativada) {
        this.segundaFaseAtivada = ativada;
    }

    /**
     * Atualiza o viewport quando a janela é redimensionada
     * @param width Nova largura da janela
     * @param height Nova altura da janela
     */
    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height);
        }
    }

    /**
     * Reinicia o jogo, restaurando vidas e resetando todos os objetos
     */
    public void reset(){
        vidasMegaMan = 3;
        gameOver = false;
        dispose();
        create();
    }

    /**
     * Atualiza a posição do jogador remoto (segundo jogador) no modo multiplayer
     * @param pos Objeto contendo a nova posição e ID do jogador
     */
    public void updateRemotePlayer(PlayerPosition pos) {
        if (remoteMegaMan != null) {
            remoteMegaMan.setPosicao(pos.x, pos.y);
        }
    }

    /**
     * Obtém as posições atuais de todos os inimigos para envio em rede
     * @return Objeto contendo listas de posições X, Y e IDs dos inimigos
     */
    public EnemyPosition getEnemyPositions() {
        EnemyPosition pos = new EnemyPosition(); // Inicializa listas vazias
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            pos.x.add(((Personagem) inimigo).getPosX()); // Adiciona posição X
            pos.y.add(((Personagem) inimigo).getPosY()); // Adiciona posição Y
            pos.ids.add(inimigo.hashCode()); // Adiciona ID
        }
        inimigos.reset(); // Reinicia o iterator
        return pos;
    }

    /**
     * Atualiza as posições dos inimigos com base nos dados recebidos em rede
     * @param pos Objeto contendo listas de posições X, Y e IDs dos inimigos
     */
    public void updateEnemies(EnemyPosition pos) {
        if (!isServer) { // Apenas o cliente atualiza as posições dos inimigos
            int i = 0;
            inimigos.reset(); // Reinicia o iterator
            while (inimigos.hasNext() && i < pos.x.size()) { // Enquanto houver inimigos e posições
                Inimigo inimigo = inimigos.next(); // Pega o próximo inimigo
                ((Personagem) inimigo).setPosicao(pos.x.get(i), pos.y.get(i)); // Atualiza posição
                i++;
            }
            inimigos.reset(); // Reinicia o iterator
        }
    }

    /**
     * Libera todos os recursos gráficos e sonoros utilizados pelo jogo
     * Evita vazamento de memória ao fechar ou reiniciar o jogo
     */
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

        //TipoAtaque.disposeTodasTexturas();
        if (texturaMegaMan != null) {
            texturaMegaMan.dispose();
        }
        if (texturaPenguin != null) {
            texturaPenguin.dispose();
        }
        if (texturaTrower != null) {
            texturaTrower.dispose();
        }
        if (texturaJaminger != null) {
            texturaJaminger.dispose();
        }

        // Libera o gerenciador de rede, se existir
        if (networkManager != null) {
            networkManager.dispose();
        }

        super.dispose();
    }

}