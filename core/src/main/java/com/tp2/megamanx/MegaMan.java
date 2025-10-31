package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class MegaMan extends Personagem { 

    private int indexAtaqueAtual;                   
    private ArrayList<Ataque> tiposAtaque;              
    private float tempoRestanteDeInvulnerabilidade = 0f;           
    private final float tempoInvulneravel = 3.5f;            

    private boolean apertouRight;
    private boolean apertouLeft;
    private boolean apertouUp;
    private boolean apertouX;
    private boolean apertouShift;
    private boolean naParede;                            
 

    public MegaMan(Texture textura, float posX, float posY) {
        super(
            textura, new TextureRegion(textura, 0, 0, 34, 46), 
            new Vector2(0.03f, 1.5f), // escala
            posX, posY, 16, 0,
            null
        );

        inicializarAtributosBooleanos();
        criarAtaques(); 
    }

    private void inicializarAtributosBooleanos(){
        apertouRight = false;
        apertouLeft = false;
        apertouUp = false;
        apertouX = false;
        apertouShift = false;
        naParede = false;

        podeAndarDireita = true;
        podeAndarEsquerda = true;
        paraDireita = true;
    }

    public void criarAtaques(){
        ataquesAtivos = new ArrayList<>();
        tiposAtaque = new ArrayList<>();
        indexAtaqueAtual = 0;

        tiposAtaque.add(
            new Ataque(
                new TextureRegion(
                    TipoAtaque.TIRO_NORMAL.getTextura(), 
                    TipoAtaque.TIRO_NORMAL.getCordX(), 
                    TipoAtaque.TIRO_NORMAL.getCordY(),
                    TipoAtaque.TIRO_NORMAL.getLargura(), 
                    TipoAtaque.TIRO_NORMAL.getAltura()),
                new Vector2(0.5f, 1.5f), -100, -100, 
                TipoAtaque.TIRO_NORMAL, 0, paraDireita
            )
        );

        tiposAtaque.add(
            new Ataque(
                new TextureRegion(
                    TipoAtaque.TIRO_AZUL.getTextura(), 
                    TipoAtaque.TIRO_AZUL.getCordX(), 
                    TipoAtaque.TIRO_AZUL.getCordY(),
                    TipoAtaque.TIRO_AZUL.getLargura(), 
                    TipoAtaque.TIRO_AZUL.getAltura()),
                new Vector2(0.5f, 1.5f), -100, -100, 
                TipoAtaque.TIRO_AZUL, 0, paraDireita
            )
        );
        
        ataqueAtual = tiposAtaque.get(0); 
    }


    public ArrayList<Ataque> getAtaquesAtivos(){ return ataquesAtivos; }

    public void setNaParede(boolean naParede) {
        this.naParede = naParede;
    }
    
    @Override
    protected void setRegion(int cordX, int cordY, int largura, int altura) {
		region.setRegion(cordX, cordY, largura, altura);
		boolean flipou = region.isFlipX();

		if(paraDireita == flipou) {
			region.flip(true, false);
		}

		corpo.setRegion(region);
	}


    public boolean testarTecla(int tecla) {
        if(Gdx.input.isKeyPressed(tecla)){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void mover() {
        paradoAtirando();
        moverParaDireita();
        moverParaEsquerda();
        pular();
        subirParede();
        dashParaDireita();
        dashParaEsquerda();

        if (tempoRestanteDeInvulnerabilidade > 0) {
            tempoRestanteDeInvulnerabilidade -= deltaTime;
        }

        tomandoDanoPorAtaque(3, 32, 2302, 0, 32, 50, 0, 16, 34, 34);
    }

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

    private void moverParaDireita(){
        if(podeAndarDireita){
            if(testarTecla(Input.Keys.RIGHT)){
                paraDireita = true;
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

    private void moverParaEsquerda() {
        if(podeAndarEsquerda){
            if(testarTecla(Input.Keys.LEFT)){
                paraDireita = false;
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

    private void pular() {
        if(testarTecla(Input.Keys.SPACE) && !noAr){
            noAr = true;
            naPlataforma = false;
            velY = 8;
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

    private void subirParede() {
        if (naParede && isNoAr() && Gdx.input.isKeyPressed(Input.Keys.UP)) {
            apertouUp = true;
            velY = 3;
            posY += velY;
            setPosicao(posX, posY);
            setRegion(863, 0, 21, 50); // Mexer nisso
        }else{
            if(apertouUp){
                setRegion(0, 16, 34, 34); 
                apertouUp = false;
            }
        }
    }

    // Tirar do jogo por bug
    private void dashParaDireita() {
        if(testarTecla(Input.Keys.RIGHT) && testarTecla(Input.Keys.SHIFT_LEFT)){
            paraDireita = true;
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

    // Tirar do jogo por bug
    private void dashParaEsquerda(){
        if(testarTecla(Input.Keys.LEFT) && testarTecla(Input.Keys.SHIFT_LEFT)){
            paraDireita = false;
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

    @Override
    public void atacar() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.X) && !tomandoDano) {
            float posXataque = 0;
            float posYataque = corpo.getY();
            int velocidadeAtaque = 0;
            if(paraDireita){
                posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
                velocidadeAtaque = 5;
            }else if(!paraDireita){
                posXataque = corpo.getX();
                velocidadeAtaque = -5;
            }

            Ataque novoAtaque = new Ataque(
                new TextureRegion(
                    ataqueAtual.getTipo().getTextura(),
                    ataqueAtual.getTipo().getCordX(), 
                    ataqueAtual.getTipo().getCordY(),
                    ataqueAtual.getTipo().getLargura(), 
                    ataqueAtual.getTipo().getAltura()),
                new Vector2(0.3f, 1.2f), posXataque, posYataque, 
                ataqueAtual.getTipo(), velocidadeAtaque, paraDireita
            );
            
            novoAtaque.setColidiu(false);
            novoAtaque.setPodeMovimentar(true);
            ataquesAtivos.add(novoAtaque);
        }
        mudarAtaque();    
    }

    public void mudarAtaque(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
            indexAtaqueAtual++;
            if (indexAtaqueAtual == 2) {
                indexAtaqueAtual = 0;
            }
            this.ataqueAtual = tiposAtaque.get(indexAtaqueAtual);
        }
    }

    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            setRegion(2398, 0, 35, 50);
            iterarDeltaTime();
            //if (deltaTime >= 3.5f) {
                //setPodeVoltarInicio(true);
            //}
        }
    }

    public void confereMortePorQueda(){
        if (getPosX() <= -100) {
            vida--;
        }if(getPosY() <= 0){
            vida--;
        }
    }

    public void tomarDano(int dano) {
        vida = vida - dano;
        tomandoDano = true;
        deltaTime = 0f;
    }

    public void tomarDanoPorContato(float dano) {
        if (tempoRestanteDeInvulnerabilidade <= 0) {
            vida -= dano;
            animar(11, 33, 1939, 0, 33, 50);
            if (paraDireita) {
                setPosicao(posX - 25, posY);
            } else if (!paraDireita) {
                setPosicao(posX + 25, posY);
            }
            tempoRestanteDeInvulnerabilidade = tempoInvulneravel; 
        }
    }

}