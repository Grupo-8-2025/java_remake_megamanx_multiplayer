package com.tp2.megamanx;

// Importações do LibGDX para entrada, gráficos e vetores
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Classe MegaMan representa o personagem principal jogável do jogo.
 * Herda de Personagem e implementa toda a lógica de movimento, ataque, dano, animação e troca de armas.
 */
public class MegaMan extends Personagem { 

    // Controle de ataques e armas
    private int ataqueAtual;                        // Índice do ataque/arma selecionada
    private ArrayList<Ataque> ataques;              // Lista de todos os ataques disponíveis
    private ArrayList<Ataque> ataquesAtivos;        // Lista de ataques ativos na tela
    private float tempoInvulneravel = 0f;           // Tempo restante de invulnerabilidade após tomar dano
    private final float TEMPORECUO = 4f;            // Duração da invulnerabilidade após dano

    // Flags de controle de input e estado
    private boolean apertouRight;
    private boolean apertouLeft;
    private boolean apertouUp;
    private boolean apertouDown;
    private boolean apertouX;
    private boolean apertouShift;

    private boolean naEscada;                       // Indica se MegaMan está em uma escada
    private boolean colidiuInimigo;                 // Indica se colidiu com um inimigo
    private boolean jaTomouDano;                    // Indica se já tomou dano recentemente
    private boolean tomandoDano;                    // Indica se está no estado de tomar dano
    private boolean ganhouJogo;                     // Indica se o jogador venceu

    /**
     * Construtor do MegaMan
     * Inicializa atributos, flags e ataques disponíveis
     * @param textura Textura do sprite do MegaMan
     * @param posX Posição X inicial
     * @param posY Posição Y inicial
     */
    public MegaMan(Texture textura, float posX, float posY) {
        super(textura, new TextureRegion(textura, 0, 0, 34, 46), posX, posY, 
        new Vector2(0.03f, 1.5f), 16, 0, null, 0, 0);
        // Inicializa flags de input e estado
        apertouRight = false;
        apertouLeft = false;
        apertouUp = false;
        apertouDown = false;
        apertouX = false;
        apertouShift = false;
        naEscada = false;
        naParede = false;
        podeAndarDireita = true;
        podeAndarEsquerda = true;
        colidiuInimigo = false;
        jaTomouDano = false;
        tomandoDano = false;
        ganhouJogo = false;
        paraDireita = true;
        paraEsquerda = false;
        criarAtaques(); // Inicializa lista de ataques disponíveis
    }

    /**
     * Cria e inicializa os ataques disponíveis do MegaMan
     * Adiciona ataques à lista e define o ataque inicial
     */
    public void criarAtaques(){
        ataqueAtual = 0;
        ataquesAtivos = new ArrayList<>();
        ataques = new ArrayList<>();
        // Tiro normal
        ataques.add(
            new Ataque(new TextureRegion(TipoAtaque.TIRO_NORMAL.getTextura(), 
            TipoAtaque.TIRO_NORMAL.getCordX1(), TipoAtaque.TIRO_NORMAL.getCordY1(),
            TipoAtaque.TIRO_NORMAL.getLargura1(), TipoAtaque.TIRO_NORMAL.getAltura1()), 
            -100, -100, new Vector2(0.5f, 1.5f), TipoAtaque.TIRO_NORMAL, 0)
        );
        // Tiro azul
        ataques.add(
            new Ataque(new TextureRegion(TipoAtaque.TIRO_AZUL.getTextura(), 
            TipoAtaque.TIRO_AZUL.getCordX1(), TipoAtaque.TIRO_AZUL.getCordY1(),
            TipoAtaque.TIRO_AZUL.getLargura1(), TipoAtaque.TIRO_AZUL.getAltura1()), 
            -100, -100, new Vector2(0.5f, 1f), TipoAtaque.TIRO_AZUL, 0)
        );
        ataque = ataques.get(0); // Ataque inicial
    }

    /**
     * Retorna a lista de ataques ativos do MegaMan
     * @return Lista de ataques ativos
     */
    public ArrayList<Ataque> getAtaquesAtivos(){
        return ataquesAtivos;
    }

    // Getters e setters para flags de estado
    public boolean isNaEscada() { return naEscada; }
    public void setNaEscada(boolean naEscada) { this.naEscada = naEscada; }
    public boolean isColidiuInimigo() { return colidiuInimigo; }
    public void setColidiuInimigo(boolean colidiuInimigo) { this.colidiuInimigo = colidiuInimigo; }
    public boolean isJaTomouDano() { return jaTomouDano; }
    public void setJaTomouDano(boolean jaTomouDano) { this.jaTomouDano = jaTomouDano; }
    public boolean isTomandoDano() { return tomandoDano; }
    public void setTomandoDano(boolean tomandoDano) { this.tomandoDano = tomandoDano; }
    public boolean isGanhouJogo() { return ganhouJogo; }
    public void setGanhouJogo(boolean ganhouJogo) { this.ganhouJogo = ganhouJogo; }

    /**
     * Sobrescreve o método setRegion para controlar o flip horizontal do sprite
     * @param cordX Coordenada X do frame
     * @param cordY Coordenada Y do frame
     * @param largura Largura do frame
     * @param altura Altura do frame
     */
    @Override
    protected void setRegion(int cordX, int cordY, int largura, int altura) {
        region.setRegion(cordX, cordY, largura, altura);
        boolean flipou = region.isFlipX();
        if(paraEsquerda != flipou) {
            region.flip(true, false);
        }
        corpo.setRegion(region);
    }

    /**
     * Testa se uma tecla está pressionada
     * @param tecla Código da tecla (Input.Keys)
     * @return true se pressionada, false caso contrário
     */
    public boolean testarTecla(int tecla) {
        if(Gdx.input.isKeyPressed(tecla)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Verifica se MegaMan morreu por queda (fora do mapa)
     * Decrementa vida se sair dos limites
     */
    public void confereMortePorQueda(){
        if (getPosX() <= -100) {
            vida--;
        }if(getPosY() <= 0){
            vida--;
        }
    }

    /**
     * Lógica principal de movimento do MegaMan
     * Chama métodos de movimentação, pulo, dash, parede e controla invulnerabilidade
     */
    @Override
    public void mover() {
        paradoAtirando();
        moverParaDireita();
        moverParaEsquerda();
        pular();
        subirParede();
        descerParede();
        dashParaDireita();
        dashParaEsquerda();
        if (tempoInvulneravel > 0) {
            tempoInvulneravel -= deltaTime;
        }
        tomandoDanoPorAtaque(3, 32, 2302, 0, 32, 50, 0, 16, 34, 34);
    }

    /**
     * Animação de MegaMan parado atirando
     * Controla o frame de tiro parado
     */
    private void paradoAtirando(){
        if(testarTecla(Input.Keys.X)){
            apertouX = true;
            setRegion(1254, 16, 30, 34);            
        }else{
            if(apertouX){
                setRegion(0, 16, 34, 34); 
                apertouX = false;
            }
        }
    }

    /**
     * Movimento para a direita, incluindo animação e controle de tiro
     */
    private void moverParaDireita(){
        if(podeAndarDireita){
            if(testarTecla(Input.Keys.RIGHT)){
                paraDireita = true;
                paraEsquerda = false;
                apertouRight = true;
                velX = 5;
                posX = posX + velX;
                setPosicao(posX, posY);
                if(testarTecla(Input.Keys.X)){
                    animar(posX, 11, 38, 374, 14, 38, 36);
                    apertouX = true;
                }else{
                    animar(posX, 11, 34, 0, 16, 34, 34);
                    apertouX = false;
                }
            }else{
                if(apertouRight){
                    setRegion(0, 16, 34, 34); 
                    apertouRight = false;
                }
            }
        }
    }

    /**
     * Movimento para a esquerda, incluindo animação e controle de tiro
     */
    private void moverParaEsquerda() {
        if(podeAndarEsquerda){
            if(testarTecla(Input.Keys.LEFT)){
                paraDireita = false;
                paraEsquerda = true;
                apertouLeft = true;
                velX = -5;
                posX = posX + velX;
                setPosicao(posX, posY);
                if(testarTecla(Input.Keys.X)){
                    animar(posX, 11, 38, 374, 14, 38, 36);
                    apertouX = true;
                }else{
                    animar(posX, 11, 34, 0, 16, 34, 34);
                    apertouX = false;
                }
            }else{
                if(apertouLeft){
                    setRegion(0, 16, 34, 34); 
                    apertouLeft = false;
                }
            }
        }
    }

    /**
     * Lógica de pulo do MegaMan
     * Permite pular apenas se não estiver no ar
     * Aplica animação de pulo com ou sem tiro
     */
    private void pular() {
        if(testarTecla(Input.Keys.SPACE) && !noAr){
            noAr = true;
            naPlataforma = false;
            velY = 5;
            posY = posY + velY;            
            setPosicao(posX, posY);
        }
        if(testarTecla(Input.Keys.X)){
            apertouX = true;
            sofrerGravidade(posY, 7, 36, 1002, 0, 36, 50, 1218, 0, 36, 50);
        }else{
            apertouX = false;
            sofrerGravidade(posY, 7, 30, 792, 0, 30, 50, 966, 0, 30, 50);
        }
    }

    // Métodos de escada comentados (não utilizados)

    /**
     * Permite subir paredes se estiver no ar e encostado na parede
     */
    private void subirParede() {
        if (isNaParede() && isNoAr() && Gdx.input.isKeyPressed(Input.Keys.UP)) {
            apertouUp = true;
            velY = 3;
            posY += velY;
            setPosicao(posX, posY);
            setRegion(1475, 0, 21, 50);
        }else{
            if(apertouUp){
                setRegion(0, 16, 34, 34); 
                apertouUp = false;
            }
        }
    }

    private void descerParede() {}

    /**
     * Dash para a direita (movimento rápido)
     */
    private void dashParaDireita() {
        if(testarTecla(Input.Keys.RIGHT) && testarTecla(Input.Keys.SHIFT_LEFT)){
            paraDireita = true;
            paraEsquerda = false;
            apertouRight = true;
            apertouShift = true;
            velX = 5;
            posX = posX + velX;
            setPosicao(posX, posY);
            animar(posX, 1, 49, 1890, 19, 49, 31);
        }else{
            if(apertouRight && apertouShift){
                setRegion(0, 16, 34, 34); 
                apertouRight = false;
                apertouShift = false;
            }
        }
    }

    /**
     * Dash para a esquerda (movimento rápido)
     */
    private void dashParaEsquerda(){
        if(testarTecla(Input.Keys.LEFT) && testarTecla(Input.Keys.SHIFT_LEFT)){
            paraDireita = false;
            paraEsquerda = true;
            apertouLeft = true;
            apertouShift = true;
            velX = -5;
            posX = posX + velX;
            setPosicao(posX, posY);
            animar(posX, 1, 49, 1890, 19, 49, 31);
        }else{
            if(apertouLeft && apertouShift){
                setRegion(0, 16, 34, 34); 
                apertouLeft = false;
                apertouShift = false;
            }
        }
    }

    /**
     * Aplica dano ao MegaMan por contato com inimigo
     * Ativa animação de dano e aplica recuo
     * @param dano Valor do dano recebido
     */
    public void tomarDanoPorContato(float dano) {
        if (tempoInvulneravel <= 0) {
            vida -= dano;
            animar(11, 33, 1939, 0, 33, 50);
            if (paraDireita) {
                setPosicao(posX - 25, posY);
            } else if (paraEsquerda) {
                setPosicao(posX + 25, posY);
            }
            tempoInvulneravel = TEMPORECUO; 
        }
    }

    /**
     * Lógica de ataque do MegaMan
     * Cria novo ataque se a tecla X for pressionada e permite troca de arma
     */
    @Override
    public void atacar() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && !tomandoDano) {
            float posXataque = 0;
            float posYataque = corpo.getY();
            int velocidadeAtaque = 0;
            if(paraDireita){
                posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
                velocidadeAtaque = 5;
            }else if(paraEsquerda){
                posXataque = corpo.getX();
                velocidadeAtaque = -5;
            }
            Ataque novoAtaque = new Ataque(
                new TextureRegion(ataque.getTipo().getTextura(),
                ataque.getTipo().getCordX1(), ataque.getTipo().getCordY1(),
                ataque.getTipo().getLargura1(), ataque.getTipo().getAltura1()),
                posXataque, posYataque, new Vector2(0.3f, 1.2f), 
                ataque.getTipo(), velocidadeAtaque
            );
            novoAtaque.setColidiu(false);
            novoAtaque.setPodeDisparar(true);
            ataquesAtivos.add(novoAtaque);
        }
        mudarAtaque();    
    }

    /**
     * Permite trocar de arma pressionando a tecla C
     * Alterna entre os ataques disponíveis
     */
    public void mudarAtaque(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            ataqueAtual++;
            if (ataqueAtual == 2) {
                ataqueAtual = 0;
            }
            this.ataque = ataques.get(ataqueAtual);
        }
    }

    /**
     * Lógica de morte do MegaMan
     * Ativa animação de morte e reseta posição após tempo
     */
    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            iterarDeltaTime();
            setRegion(2398, 0, 35, 50);
            if (deltaTime >= 5.0f) {
                setPosicao(0, 0);
            }
        }
    }
}