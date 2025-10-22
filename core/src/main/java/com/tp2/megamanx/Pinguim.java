package com.tp2.megamanx;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class Pinguim extends Personagem implements Inimigo {

	private int ataqueAtual;
	private ArrayList<Ataque> ataques;
	private ArrayList<Ataque> ataquesAtivos;
	protected Random random;
	protected int quantAcoes;
	protected int determinaAcao;
	protected float duracaoAcao;
	protected boolean podeMover;
	protected boolean podeAtacar;

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
		tipoInimigo = 1;

		criarAtaques();
	}

	public Pinguim(Rectangle rect, float posX, float posY) {
		super( rect, posX, posY, 
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
		tipoInimigo = 1;

		criarAtaques();
	}

	public int tipoInimigo(){
		return tipoInimigo;
	}

	public void criarAtaques(){
		ataqueAtual = 0;
		ataquesAtivos = new ArrayList<>();

		ataques = new ArrayList<>();

		try {
			ataques.add(
				new Ataque(new TextureRegion(TipoAtaque.BOLA_GELO.getTextura(), 
				TipoAtaque.BOLA_GELO.getCordX1(), TipoAtaque.BOLA_GELO.getCordY1(),
				TipoAtaque.BOLA_GELO.getLargura1(), TipoAtaque.BOLA_GELO.getAltura1()), 
				-100, -100, new Vector2(1f, 2.5f), TipoAtaque.BOLA_GELO, 0)
			);

			ataques.add(
				new Ataque(new TextureRegion(TipoAtaque.SOPRO_GELO.getTextura(), 
				TipoAtaque.SOPRO_GELO.getCordX1(), TipoAtaque.SOPRO_GELO.getCordY1(),
				TipoAtaque.SOPRO_GELO.getLargura1(), TipoAtaque.SOPRO_GELO.getAltura1()), 
				-100, -100, new Vector2(1f, 2.5f), TipoAtaque.SOPRO_GELO, 0)
			);
		} catch (Exception e) {

			ataques.add(new Ataque(new Rectangle(0,0,17,16),
			-100, -100, new Vector2(1f, 2.5f),
			TipoAtaque.BOLA_GELO, 0));

			ataques.add(new Ataque(new Rectangle(0,0,15,15),
			-100, -100, new Vector2(1f, 2.5f),
			TipoAtaque.BOLA_GELO, 0));
		}
		

		ataque = ataques.get(0);
	}



	public Rectangle getRect(){
		return corpo.getBoundingRectangle();
	}

    public int getDano() {
		return dano;
	}

	public ArrayList<Ataque> getAtaquesAtivos(){
		return ataquesAtivos;
	}

	public void tomarDano(int dano) {
		vida = vida - dano;
		tomandoDano = true;
		deltaTime = 0f;
	}

	public void setPosXmegaMan(float posXmegaMan) {
		this.posXmegaMan = posXmegaMan;
	}

	@Override
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		region.setRegion(cordX, cordY, largura, altura);

		boolean flipou = region.isFlipX();
		if(paraDireita != flipou) {
			region.flip(true, false);
		}

		corpo.setRegion(region);
	}


	public void atualizar(){
		if(!morreu && Math.abs(posXmegaMan - posX) < 600){
			sofrerGravidade(posY, 1, 0, 602, 16, 43, 
			44, 0, 24, 43, 36);

			delimitarMovimento();

			int acaoAnterior = determinaAcao;
			iterarDeltaTime();
			atualizarAcao();

			tomandoDanoPorAtaque(1, 43, 731, 19, 43, 41, 
			0, 24, 43, 36);
			
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

	private void determinarAcaoMover(){
		duracaoAcao = 3.f;
		podeMover = true;
		podeAtacar = false;
	}

	private void determinarAcaoParado(){
		duracaoAcao = 2f;
		parado(3, 43, 0, 24, 43, 36);
		podeAtacar = false;
		podeMover = false;
	}

	private void determinarAtaqueBolaGelo(){
		duracaoAcao = 3.f;
		ataqueAtual = 0;
		ataque = ataques.get(ataqueAtual);
		podeAtacar = true;
		podeMover = false;
	}

	private void determinarAtaqueSoproGelo(){
		duracaoAcao = 3f;
		ataqueAtual = 1;
		ataque = ataques.get(ataqueAtual);
		podeAtacar = true;
		podeMover = false;
	}

	private void delimitarMovimento(){
		if(posX <= 8050){
			paraDireita = true;
			paraEsquerda = false;
		} else if(posX >= 8670){
			paraEsquerda = true;
			paraDireita = false;
		}
	}

	protected void atualizarAcao() {
		if (deltaTime >= duracaoAcao) {
			determinaAcao = random.nextInt(quantAcoes);
			deltaTime = 0f; 
		}
	}

	protected void moverParaDireita(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		paraDireita = true;
		paraEsquerda = false;
		velX = 5;
		setPosicao(posX + velX, posY);
		animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
	}

	protected void moverParaEsquerda(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		paraDireita = false;
		paraEsquerda = true;
		velX = -5;
		setPosicao(posX + velX, posY);
		animar(posX, qtdFrames, incrementa, cordX, cordY, largura, altura);
	}

	protected void parado(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura){
		velX = 0;            
        animar(qtdFrames, incrementa, cordX, cordY, largura, altura);
	}

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


	@Override
	public void atacar(){
		if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
			setRegion(688, 25, 43, 35);

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

	@Override
	public void morrer(){
		if(vida <= 0){
			morreu = true;
			iterarDeltaTime();
			setRegion(774, 13, 43, 47);

			if (deltaTime >= 5.0f) {
				setPosicao(-500, -500);
			}
		}
	}

}