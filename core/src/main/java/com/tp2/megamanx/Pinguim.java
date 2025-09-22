package com.tp2.megamanx;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

/**
 * Classe que representa o chefe Pinguim (Chill Penguin), inimigo principal da fase.
 * Herda de Personagem e implementa a interface Inimigo.
 * Possui lógica de IA para ações aleatórias, ataques e movimentação.
 */
public class Pinguim extends Personagem implements Inimigo {

    // Índice do ataque atual selecionado
    private int ataqueAtual;
    // Lista de todos os ataques disponíveis do Pinguim
    private ArrayList<Ataque> ataques;
    // Lista de ataques ativos disparados pelo Pinguim
    private ArrayList<Ataque> ataquesAtivos;
    // Gerador de números aleatórios para IA
    protected Random random;
    // Quantidade de ações possíveis (mover, parar, atacar, etc)
    protected int quantAcoes;
    // Índice da ação atualmente determinada
    protected int determinaAcao;
    // Duração da ação atual
    protected float duracaoAcao;
    // Flags de controle de IA
    protected boolean podeMover;
    protected boolean podeAtacar;

    /**
     * Construtor do Pinguim.
     * Inicializa atributos, ataques e flags de IA.
     * @param textura Textura do sprite do Pinguim
     * @param posX Posição X inicial
     * @param posY Posição Y inicial
     */
    public Pinguim(Texture textura, float posX, float posY) {
        super(textura, new TextureRegion(textura, 602, 16, 43, 44), posX, posY, 
        new Vector2(0.1f, 2.0f), 32, 4, null,  0, 0);    
        random = new Random();
        quantAcoes = 4;
        determinaAcao = 0;
        duracaoAcao = 0;
        podeMover = false;
        podeAtacar = false;
        paraEsquerda = true;
        paraDireita = false;
        noAr = true;
        criarAtaques(); // Inicializa lista de ataques
    }

    /**
     * Cria e inicializa os ataques disponíveis do Pinguim
     * Adiciona ataques à lista e define o ataque inicial
     */
    public void criarAtaques(){
        ataqueAtual = 0;
        ataquesAtivos = new ArrayList<>();
        ataques = new ArrayList<>();
        // Bola de gelo
        ataques.add(
            new Ataque(new TextureRegion(TipoAtaque.BOLA_GELO.getTextura(), 
            TipoAtaque.BOLA_GELO.getCordX1(), TipoAtaque.BOLA_GELO.getCordY1(),
            TipoAtaque.BOLA_GELO.getLargura1(), TipoAtaque.BOLA_GELO.getAltura1()), 
            -100, -100, new Vector2(1f, 2.5f), TipoAtaque.BOLA_GELO, 0)
        );
        // Sopro de gelo
        ataques.add(
            new Ataque(new TextureRegion(TipoAtaque.SOPRO_GELO.getTextura(), 
            TipoAtaque.SOPRO_GELO.getCordX1(), TipoAtaque.SOPRO_GELO.getCordY1(),
            TipoAtaque.SOPRO_GELO.getLargura1(), TipoAtaque.SOPRO_GELO.getAltura1()), 
            -100, -100, new Vector2(1f, 2.5f), TipoAtaque.SOPRO_GELO, 0)
        );
        ataque = ataques.get(0); // Ataque inicial
    }

    /**
     * Retorna o retângulo de colisão do corpo do Pinguim
     */
    public Rectangle getRect(){
        return corpo.getBoundingRectangle();
    }

    /**
     * Retorna o dano causado pelo Pinguim
     */
    public int getDano() {
        return dano;
    }

    /**
     * Retorna a lista de ataques ativos do Pinguim
     */
    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    /**
     * Aplica dano ao Pinguim e ativa estado de dano
     * @param dano Valor do dano recebido
     */
    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    /**
     * Define a posição X do MegaMan (usado para IA do chefe)
     * @param posXmegaMan Posição X do MegaMan
     */
    public void setPosXmegaMan(float posXmegaMan) {
        this.posXmegaMan = posXmegaMan;
    }

    /**
     * Atualiza a região do sprite, controlando o flip horizontal
     * @param cordX Coordenada X do frame
     * @param cordY Coordenada Y do frame
     * @param largura Largura do frame
     * @param altura Altura do frame
     */
    @Override
    protected void setRegion(int cordX, int cordY, int largura, int altura) {
        region.setRegion(cordX, cordY, largura, altura);

        boolean flipou = region.isFlipX();
        if(paraDireita != flipou) {
            region.flip(true, false);
        }

        corpo.setRegion(region);
    }

    /**
     * Atualiza o estado do Pinguim a cada frame.
     * Controla gravidade, ações, animações, IA e ataques.
     */
    public void atualizar(){
        if(!morreu && Math.abs(posXmegaMan - posX) < 600){
            sofrerGravidade(posY, 1, 0, 602, 16, 43, 
            44, 0, 24, 43, 36); // Aplica gravidade e animação

            delimitarMovimento(); // Limita área de movimento do chefe

            int acaoAnterior = determinaAcao;
            iterarDeltaTime(); // Atualiza deltaTime
            atualizarAcao(); // Sorteia nova ação se necessário

            tomandoDanoPorAtaque(1, 43, 731, 19, 43, 41, 
            0, 24, 43, 36); // Animação de dano
            
            if(!noAr){
                if(determinaAcao != acaoAnterior){
                    if(determinaAcao == 0){
                        determinarAcaoMover();
                    }else if(determinaAcao == 1){
                        determinarAcaoParado();
                    }else if(determinaAcao == 2){
                        determinarAtaqueBolaGelo();
                    }else if(determinaAcao == 3){
                        determinarAtaqueSoproGelo();
                    }
                } 
            }

        }
    }

    /**
     * Define ação de mover (andar) para o chefe
     */
    private void determinarAcaoMover(){
        duracaoAcao = 3.f;
        podeMover = true;
        podeAtacar = false;
    }

    /**
     * Define ação de ficar parado
     */
    private void determinarAcaoParado(){
        duracaoAcao = 2f;
        parado(3, 43, 0, 24, 43, 36);
        podeAtacar = false;
        podeMover = false;
    }

    /**
     * Define ação de ataque Bola de Gelo
     */
    private void determinarAtaqueBolaGelo(){
        duracaoAcao = 3.f;
        ataqueAtual = 0;
        ataque = ataques.get(ataqueAtual);
        podeAtacar = true;
        podeMover = false;
    }

    /**
     * Define ação de ataque Sopro de Gelo
     */
    private void determinarAtaqueSoproGelo(){
        duracaoAcao = 3f;
        ataqueAtual = 1;
        ataque = ataques.get(ataqueAtual);
        podeAtacar = true;
        podeMover = false;
    }

    /**
     * Limita a área de movimento do chefe no cenário
     */
    private void delimitarMovimento(){
        if(posX <= 8050){
            paraDireita = true;
            paraEsquerda = false;
        } else if(posX >= 8670){
            paraEsquerda = true;
            paraDireita = false;
        }
    }

    /**
     * Atualiza a ação do chefe sorteando uma nova após o tempo de duração
     */
    protected void atualizarAcao() {
        if (deltaTime >= duracaoAcao) {
            determinaAcao = random.nextInt(quantAcoes);
            deltaTime = 0f; 
        }
    }

    /**
     * Move o chefe para a direita, atualizando posição e animação
     */
    protected void moverParaDireita(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        paraDireita = true;
        paraEsquerda = false;
        velX = 5;
        setPosicao(posX + velX, posY);
        animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
    }

    /**
     * Move o chefe para a esquerda, atualizando posição e animação
     */
    protected void moverParaEsquerda(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
        paraDireita = false;
        paraEsquerda = true;
        velX = -5;
        setPosicao(posX + velX, posY);
        animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
    }

    /**
     * Animação de chefe parado
     */
    protected void parado(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura){
        velX = 0;            
        animar(qtdFrames, incrementa, cordX, cordY, largura, altura);
    }

    /**
     * Lógica de movimentação do chefe, perseguindo o MegaMan se podeMover for true
     */
    @Override
    public void mover() {
        if (podeMover) {
            if (posXmegaMan < posX) {
                moverParaEsquerda(1, 43, 301, 31, 43, 29);
            } else if (posXmegaMan > posX) {
                moverParaDireita(1, 43, 301, 31, 43, 29);
            }
        } else {
            if (posXmegaMan < posX) {
                paraDireita = false;
                paraEsquerda = true;
            } else if (posXmegaMan > posX) {
                paraDireita = true;
                paraEsquerda = false;
            }
            setRegion(0, 24, 43, 36); 
        }
    }

    /**
     * Lógica de ataque do chefe, disparando ataque na direção do MegaMan
     */
    @Override
    public void atacar(){
        if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
            setRegion(688, 25, 43, 35); // Frame de ataque

            float posXataque = 0;
            float posYataque = corpo.getY() + 15f;

            int velocidadeAtaque = 0;
            if(posXmegaMan > posX){
                paraDireita = true;
                paraEsquerda = false;
                velocidadeAtaque = 5;
                posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
            }else if(posXmegaMan < posX){
                paraDireita = false;
                paraEsquerda = true;
                velocidadeAtaque = -5;
                posXataque = corpo.getX();
            }

            Ataque novoAtaque = new Ataque(
                new TextureRegion(ataque.getTipo().getTextura(),
                ataque.getTipo().getCordX1(), ataque.getTipo().getCordY1(),
                ataque.getTipo().getLargura1(), ataque.getTipo().getAltura1()),
                posXataque, posYataque, new Vector2(1f, 2.5f), 
                ataque.getTipo(), velocidadeAtaque
            );

            novoAtaque.setColidiu(false);
            novoAtaque.setPodeDisparar(true);
            ataquesAtivos.add(novoAtaque);
        }
    }

    /**
     * Lógica de morte do chefe, animação e remoção do cenário
     */
    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            iterarDeltaTime();
            setRegion(774, 13, 43, 47); // Frame de morte

            //if (deltaTime >= 5.0f) {
                setPosicao(-500, -500); // Remove do cenário
            //}
        }
    }

}