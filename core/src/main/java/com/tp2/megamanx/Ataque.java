package com.tp2.megamanx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Ataque extends Entidade {

	private TipoAtaque tipo;           
	private float velocidade;          

	private boolean colidiu;           
	private boolean podeDisparar;      
	private boolean podeDefinirPosicao; 
	private boolean disparou;          

	public Ataque(TextureRegion region, Vector2 escala, float posX, float posY, TipoAtaque tipo, float velocidade, boolean direcaoPersonagem){
		super(tipo.getTextura(), region, escala, posX, posY);
		
		this.tipo = tipo;
		this.velocidade = velocidade;

		this.colidiu = false;                         
		this.podeDisparar = true;                    
		this.podeDefinirPosicao = true;               
		this.disparou = false;  
		this.paraDireita = direcaoPersonagem;                      
	}

	public boolean isDisparou() {
		return disparou;
	}

	public void setDisparou(boolean disparou) {
		this.disparou = disparou;
	}

	public boolean isColidiu() {
		return colidiu;
	}

	public void setColidiu(boolean colidiu) {
		this.colidiu = colidiu;
	}

	public TipoAtaque getTipo() {
		return tipo;
	}

	public boolean isPodeDisparar() {
		return podeDisparar;
	}

	public void setPodeDisparar(boolean podeDisparar) {
		this.podeDisparar = podeDisparar;
	}

	public boolean isPodeDefinirPosicao() {
		return podeDefinirPosicao;
	}

	public void setPodeDefinirPosicao(boolean podeDefinirPosicao) {
		this.podeDefinirPosicao = podeDefinirPosicao;
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

	/* 
	@Override
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		// Define a região da textura a ser utilizada
		region.setRegion(cordX, cordY, largura, altura);

		// Verifica se a região está atualmente espelhada horizontalmente
		boolean flipou = region.isFlipX();
		
		// Se o estado de direção (paraEsquerda) não corresponde ao flip atual
		if(paraDireita == flipou) {
			// Espelha a região horizontalmente para corresponder à direção
			region.flip(true, false);
		}

		// Atualiza o corpo (sprite) com a nova região
		corpo.setRegion(region);
	}*/

	public void disparar() {
		// Verifica se o ataque não colidiu e pode ser disparado
		if(!colidiu && podeDisparar){
			// Move o ataque horizontalmente pela velocidade definida
			setPosicao(posX + velocidade, posY);
			
			// Atualiza a região da textura para o frame atual da animação
			setRegion(tipo.getCordX(), tipo.getCordY(), tipo.getLargura(), tipo.getAltura());
			
			// Executa a animação do ataque usando os parâmetros do tipo
			animar(tipo.getQtdFrames(), tipo.getIncrementa(), tipo.getCordX(), 
			tipo.getCordY(), tipo.getLargura(), tipo.getAltura());
		}
	}

}