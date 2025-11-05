package com.tp2.megamanx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ataque extends Entidade /*implements java.io.Serializable*/ {

	private TipoAtaque tipo;           
	private float velocidade;          
	private boolean colidiu;           
	private boolean podeMovimentar;      

	public Ataque(TextureRegion region, Vector2 escala, float posX, float posY, TipoAtaque tipo, float velocidade, boolean direcaoPersonagem){
		super(tipo.getTextura(), region, escala, posX, posY);
		
		this.tipo = tipo;
		this.velocidade = velocidade;
		this.colidiu = false;                         
		this.podeMovimentar = true; 

		this.paraDireita = direcaoPersonagem;                      
	}


	public TipoAtaque getTipo() {
		return tipo;
	}

	public void setColidiu(boolean colidiu) {
		this.colidiu = colidiu;
	}

	public void setPodeMovimentar(boolean podeMovimentar) {
		this.podeMovimentar = podeMovimentar;
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


	public void disparar() {
		if(!colidiu && podeMovimentar){
			setPosicao(posX + velocidade, posY);
			setRegion(tipo.getCordX(), tipo.getCordY(), tipo.getLargura(), tipo.getAltura());
			
			animar(posX, tipo.getQtdFrames(), tipo.getIncrementa(), tipo.getCordX(), 
			tipo.getCordY(), tipo.getLargura(), tipo.getAltura());
		}
	}

}