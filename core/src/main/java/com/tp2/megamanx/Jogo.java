package com.tp2.megamanx;

import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;

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
        gerador = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        parametro = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametro.size = 24;
        parametro.color = Color.WHITE;
        fonteVida = gerador.generateFont(parametro);
        gerador.dispose();

        somMorte = Gdx.audio.newSound(Gdx.files.internal("sons/megaman-x-death-sound-effect.mp3"));
        somVitoria = Gdx.audio.newSound(Gdx.files.internal("sons/mmx-stage-clear.mp3"));
        somPadrao = Gdx.audio.newSound(Gdx.files.internal("sons/mega-man-x2-snes-music-first-stage-audiotrimmer.mp3"));


        random = new Random();
        posicoesValidas = new ArrayList<>();

        gerenciadorColisoes = new GerenciadorColisoes();
        inimigos = new InimigoIterator();
        personagens = new PersonagemIterator();

        carregaTexturas();
        criaMapa();
        criaPersonagens();

        somPadrao.play(somPadrao.loop());
    }

    private void criaMapa(){
        mapa = new Mapa("maps/Mapa.tmx", 800, 600);
    }

    private void carregaTexturas(){
        TipoAtaque.carregarTodasTexturas();
        texturaMegaMan = new Texture("imagens/MegaMan/megaMan.png");
        texturaPenguin = new Texture("imagens/ChilPenguin/inimigos/Penguin/penguin.png");
        texturaTrower = new Texture("imagens/ChilPenguin/inimigos/now.png");
        texturaJaminger = new Texture("imagens/ChilPenguin/inimigos/jaminger.png");
        texturaFundo = new Texture("fundos/cena-de-pixels-graficos-de-8-bits-com-montanhas.jpg");
    }

    private void criaPersonagens(){
        criarInimigos();
        megaMan = new MegaMan(texturaMegaMan, 330, 2517);

        personagens.add(megaMan);
        personagens.add(penguin);
    }

    private void criarInimigos(){
        penguin = new Pinguim(texturaPenguin, 11635, 3000);
        //penguin = new Pinguim(texturaPenguin, 500, 2517);
        inimigos.add(penguin);
        
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
            int indexPosicao = random.nextInt(posicoesValidas.size());

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

    private void determinarPosicoesValidas(){
    posicoesValidas.clear();
    for(Rectangle plataforma : mapa.getChaos()){
            float posYplataforma = plataforma.y + plataforma.height;
            float posXplataforma = plataforma.x + plataforma.width;
            posicoesValidas.add(new Vector2(posXplataforma, posYplataforma));
    }
    System.out.println("Posições válidas encontradas: " + posicoesValidas.size());
}

    @Override
    public void render() {
        cameraFoco.set(megaMan.getPosX() + megaMan.getCorpo().getBoundingRectangle().width, 
        megaMan.getPosY() + (megaMan.getCorpo().getBoundingRectangle().height/2));
        camera.position.set(cameraFoco, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined); 
        
        Gdx.gl.glClearColor(255f, 255f, 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(!megaMan.isMorreu()){
            ataquesPersonagens();
            atualizarPersonagens();
            colisoes();
        }

        desenhaItens();
        super.render();
    }

    

    private void atualizarPersonagens(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            personagem.mover();
            personagem.atacar();
            personagem.morrer();
            if(penguin.getVida() <= 0){
                somVitoria.play();
                setScreen(new TelaVitoria(this));
            }
            if (megaMan.isMorreu() && !gameOver) {
                //megaMan.setPosicao(0, 0);
                somMorte.play();
                vidasMegaMan--;
                if (vidasMegaMan > 0) {
                    //megaMan = new MegaMan(texturaMegaMan, 330, 2517);
                    dispose();
                    criaObjetosJogo();
                    gameOver = false;
                    //megaMan.setPosicao(330, 2517);
                    //megaMan.setVida(16);
                    //personagens.add(megaMan);
                } else {
                    gameOver = true;
                    setScreen(new TelaGameOver(this));
                }
            }
            megaMan.confereMortePorQueda();
        }
        personagens.reset();

        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            inimigo.setPosXmegaMan(megaMan.getPosX());
        }
        inimigos.reset();

        penguin.atualizar();
    }

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
    private void desenhaItens(){
    
        batch.begin();

        batch.draw(texturaFundo, camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight );

        batch.end();

        mapa.render(camera);

        batch.begin();

        desenharEntidades();

        fonteVida.draw(batch, "Vida MegaMan: " + megaMan.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 20);

        fonteVida.draw(batch, "Vida Penguin: " + penguin.getVida(), camera.position.x - camera.viewportWidth / 2 + 20, camera.position.y + camera.viewportHeight / 2 - 50);

        desenharVida();
        batch.end();
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);
        // for (Rectangle r : mapa.getChaos()) {
        //     shapeRenderer.rect(r.x, r.y, r.width, r.height);
        // }
        
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
    }

    private void desenharAtaques(){
        personagens.reset();
        while (personagens.hasNext()) {
            Personagem personagem = personagens.next();
            for(int i=0; i < personagem.getAtaquesAtivos().size(); i++){
                personagem.getAtaquesAtivos().get(i).draw(batch);;
            }
        }
        personagens.reset();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void reset(){
        vidasMegaMan = 3;
        gameOver = false;
        dispose();
        create();
    }


    @Override
    public void dispose() {
        if(mapa != null) {
            mapa.dispose();
        }
        if(batch != null) {
            batch.dispose();
        }
        
        somMorte.pause();
        somMorte.dispose();
        somPadrao.pause();
        somPadrao.dispose();
        somVitoria.pause();
        somVitoria.dispose();

        fonteVida.dispose();

        //TipoAtaque.disposeTodasTexturas();
        texturaMegaMan.dispose();
        texturaPenguin.dispose();
        texturaTrower.dispose();
        texturaJaminger.dispose();

        super.dispose();
    }

}