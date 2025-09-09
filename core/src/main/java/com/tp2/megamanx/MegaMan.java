package com.tp2.megamanx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class MegaMan extends Personagem { 

	private int ataqueAtual;
	private ArrayList<Ataque> ataques;
	private ArrayList<Ataque> ataquesAtivos;
	private float tempoInvulneravel = 0f;
	private final float TEMPORECUO = 4f;

	private boolean apertouRight;
	private boolean apertouLeft;
	private boolean apertouUp;
	private boolean apertouDown;
	private boolean apertouX;
	private boolean apertouShift;

	private boolean naEscada;

	private boolean colidiuInimigo;
	private boolean jaTomouDano;
	private boolean tomandoDano;

	private boolean ganhouJogo;


	public MegaMan(Texture textura, float posX, float posY) {
		super(textura, new TextureRegion(textura, 0, 0, 34, 46), posX, posY, 
		new Vector2(0.03f, 1.5f), 16, 0, null, 0, 0);
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

		criarAtaques();
	}

	public void criarAtaques(){
		ataqueAtual = 0;
		ataquesAtivos = new ArrayList<>();

		ataques = new ArrayList<>();

		ataques.add(
			new Ataque(new TextureRegion(TipoAtaque.TIRO_NORMAL.getTextura(), 
			TipoAtaque.TIRO_NORMAL.getCordX1(), TipoAtaque.TIRO_NORMAL.getCordY1(),
			TipoAtaque.TIRO_NORMAL.getLargura1(), TipoAtaque.TIRO_NORMAL.getAltura1()), 
			-100, -100, new Vector2(0.5f, 1.5f), TipoAtaque.TIRO_NORMAL, 0)
		);

		ataques.add(
			new Ataque(new TextureRegion(TipoAtaque.TIRO_AZUL.getTextura(), 
			TipoAtaque.TIRO_AZUL.getCordX1(), TipoAtaque.TIRO_AZUL.getCordY1(),
			TipoAtaque.TIRO_AZUL.getLargura1(), TipoAtaque.TIRO_AZUL.getAltura1()), 
			-100, -100, new Vector2(0.5f, 1f), TipoAtaque.TIRO_AZUL, 0)
		);

		ataque = ataques.get(0);
	}


	public ArrayList<Ataque> getAtaquesAtivos(){
		return ataquesAtivos;
	}

	public boolean isNaEscada() {
		return naEscada;
	}

	public void setNaEscada(boolean naEscada) {
		this.naEscada = naEscada;
	}

	public boolean isColidiuInimigo() {
		return colidiuInimigo;
	}

	public void setColidiuInimigo(boolean colidiuInimigo) {
		this.colidiuInimigo = colidiuInimigo;
	}

	public boolean isJaTomouDano() {
		return jaTomouDano;
	}

	public void setJaTomouDano(boolean jaTomouDano) {
		this.jaTomouDano = jaTomouDano;
	}

	public boolean isTomandoDano() {
		return tomandoDano;
	}

	public void setTomandoDano(boolean tomandoDano) {
		this.tomandoDano = tomandoDano;
	}

	public boolean isGanhouJogo() {
		return ganhouJogo;
	}

	public void setGanhouJogo(boolean ganhouJogo) {
		this.ganhouJogo = ganhouJogo;
	}

	@Override
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		region.setRegion(cordX, cordY, largura, altura);

		boolean flipou = region.isFlipX();
		if(paraEsquerda != flipou) {
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

	public void confereMortePorQueda(){
		if (getPosX() <= -100) {
			vida--;
		}if(getPosY() <= 0){
			vida--;
		}
	}

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

    	//System.out.println(posX + ", " + posY);
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

	/*
	private void subirEscada() {
		if(testarTecla(Input.Keys.UP) && naEscada){
            apertouUp = true;
            velY = 5;
            posY = posY + velY;
            setPosicao(posX, posY);

            animar(posY, 4, 20, 1395, 0, 20, 50); 
        }else{
            if(apertouUp){
				if(testarTecla(Input.Keys.UP)){
					apertouX = true;
					setRegion(1538, 0, 26, 50);
				}else{
					setRegion(1538, 0, 26, 50);
				}
                apertouUp = false;
            }
        }
	}

	private void descerEscada() {
		if(testarTecla(Input.Keys.DOWN) && naEscada){
            apertouDown = true;
            velY = -5;
            posY = posY + velY;
            setPosicao(posX, posY);

            animar(posY, 4, 20, 1395, 0, 20, 50); 
        }else{
            if(apertouDown){
                if(testarTecla(Input.Keys.UP)){
					apertouX = true;
					setRegion(1475, 0, 21, 50);
				}else{
					setRegion(1475, 0, 21, 50);
				}
                apertouDown = false;
            }
        }
	}*/

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


	public void mudarAtaque(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.C)){
			ataqueAtual++;
			if (ataqueAtual == 2) {
				ataqueAtual = 0;
			}
			this.ataque = ataques.get(ataqueAtual);
		}
	}

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