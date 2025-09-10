package com.tp2.megamanx;

// Importação de ArrayList para gerenciar ataques ativos
import java.util.ArrayList;

// Importações do LibGDX para manipulação gráfica e física
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe Jaminger representa um inimigo específico do jogo, herdando de Personagem e implementando Inimigo.
 * 
 * Possui comportamento próprio de movimento, ataque e morte, além de gerenciar seus ataques ativos.
 * Utiliza herança para reaproveitar lógica de personagem e interface para garantir integração com o sistema de colisão.
 */
public class Jaminger extends Personagem implements Inimigo{

    /**
     * Construtor do Jaminger
     * Inicializa o inimigo com textura, posição, ataque e velocidades
     * 
     * @param textura Textura do sprite do Jaminger
     * @param posX Posição X inicial
     * @param posY Posição Y inicial
     * @param ataque Ataque padrão do Jaminger
     * @param velX Velocidade horizontal
     * @param velY Velocidade vertical
     */
    public Jaminger(Texture textura, float posX, float posY, Ataque ataque, float velX, float velY) {
		super(textura, new TextureRegion(textura, 390, 0, 39, 75), posX, posY, new Vector2(0.3f, 1.5f), 
        3, 2, ataque, velX, velY);
	}

    /**
     * Retorna o retângulo de colisão do Jaminger
     * Usado para detecção de colisão com MegaMan, ataques, plataformas, etc.
     * @return Rectangle representando a área ocupada pelo Jaminger
     */
    public Rectangle getRect(){
		return corpo.getBoundingRectangle();
	}

    /**
     * Retorna o valor de dano que o Jaminger causa ao MegaMan por contato
     * @return Valor inteiro do dano
     */
    public int getDano() {
		return dano;
	}

    /**
     * Retorna a lista de ataques ativos do Jaminger
     * Permite que o sistema de colisão verifique projéteis disparados
     * @return Lista de objetos Ataque ativos
     */
	public ArrayList<Ataque> getAtaquesAtivos(){
		return ataquesAtivos;
	}

    /**
     * Atualiza a posição X do MegaMan para que o Jaminger possa reagir (ex: mirar, atacar)
     * @param posXmegaMan Posição X atual do MegaMan
     */
	public void setPosXmegaMan(float posXmegaMan) {
		this.posXmegaMan = posXmegaMan;
	}

    /**
     * Aplica dano ao Jaminger (ex: quando atingido por um ataque do MegaMan)
     * @param dano Quantidade de dano a ser subtraída da vida
     */
	public void tomarDano(int dano) {
		vida = vida - dano;
		tomandoDano = true;
		deltaTime = 0f;
	}


	/**
	 * Comportamento de movimento do Jaminger
	 * Aplica gravidade e executa animação de movimento
	 * Sobrescreve o método mover() da classe Personagem
	 */
	@Override
	public void mover(){
		sofrerGravidade(posY, 1, 39, 0, 0, 39, 75, 0, 0, 39, 75);
		animar(10, 39, 189, 0, 39, 75);
	}

	/**
	 * Comportamento de ataque do Jaminger
	 * Dispara projéteis se o MegaMan estiver a menos de 600px de distância
	 * Controla o tempo entre ataques usando deltaTime
	 * Sobrescreve o método atacar() da classe Personagem
	 */
	@Override
	public void atacar(){
		if(Math.abs(posXmegaMan - posX) < 600){
			iterarDeltaTime();
			
			if(deltaTime >= 8f){
				Rectangle rect = corpo.getBoundingRectangle();
				float posXataque = rect.x - rect.width - 10;
				float posYataque = rect.y + rect.height/1.5f;

				Ataque novoAtaque = new Ataque(
					new TextureRegion(ataque.getTipo().getTextura(),
					ataque.getTipo().getCordX1(), ataque.getTipo().getCordY1(),
					ataque.getTipo().getLargura1(), ataque.getTipo().getAltura1()),
					posXataque, posYataque, new Vector2(1.5f, 2.5f), 
					ataque.getTipo(), ataque.getTipo().getVelocidade()
				);

				novoAtaque.setColidiu(false);
				novoAtaque.setPodeDisparar(true);
				ataquesAtivos.add(novoAtaque);
				deltaTime = 0f;
			}

		}
	}
	
	/**
	 * Comportamento de morte do Jaminger
	 * Marca como morto, executa animação de morte e remove da tela
	 * Sobrescreve o método morrer() da classe Personagem
	 */
	@Override
	public void morrer(){
		if(vida <= 0){
			morreu = true;
			iterarDeltaTime();
			setRegion(27, 0, 27, 75);
			setPosicao(-500, -500);
		}
	}

}
