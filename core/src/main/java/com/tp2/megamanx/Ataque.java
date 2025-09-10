package com.tp2.megamanx;

// Importações do LibGDX para trabalhar com texturas e vetores
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Classe Ataque representa um projétil ou ataque no jogo
 * Estende a classe Entidade, herdando funcionalidades básicas como posição, textura e animação
 * 
 * Esta classe gerencia:
 * - Tipos diferentes de ataques (através do enum TipoAtaque)
 * - Movimento e velocidade dos projéteis
 * - Estados do ataque (disponível, colidiu, pode disparar, etc.)
 * - Animação e renderização dos ataques
 */
public class Ataque extends Entidade {

	// Atributos da classe Ataque
	private TipoAtaque tipo;           // Tipo do ataque (define características como textura, dano, etc.)
	private float velocidade;          // Velocidade de movimento do projétil

	// Flags de controle de estado do ataque
	private boolean disponivel;        // Indica se o ataque está disponível para uso
	private boolean isMegaMan;         // Indica se o ataque pertence ao MegaMan (true) ou a um inimigo (false)
	private boolean colidiu;           // Indica se o ataque já colidiu com algo
	private boolean podeDisparar;      // Indica se o ataque pode ser disparado
	private boolean podeDefinirPosicao; // Indica se a posição do ataque pode ser alterada
	private boolean disparou;          // Indica se o ataque já foi disparado

	/**
	 * Construtor da classe Ataque
	 * Inicializa um novo ataque com todas as propriedades necessárias
	 * 
	 * @param region Região da textura que representa visualmente o ataque
	 * @param posX Posição inicial X do ataque
	 * @param posY Posição inicial Y do ataque
	 * @param escala Escala de renderização do ataque (largura e altura)
	 * @param tipo Tipo do ataque (define características específicas)
	 * @param velocidade Velocidade de movimento do projétil
	 */
	Ataque(TextureRegion region, float posX, float posY, Vector2 escala, TipoAtaque tipo, float velocidade){
		// Chama o construtor da classe pai (Entidade) com os parâmetros de renderização
		super(tipo.getTextura(), region, posX, posY, escala);
		
		// Inicializa o tipo do ataque
		this.tipo = tipo;
		
		// Inicializa as flags de estado baseadas no tipo do ataque
		this.disponivel = tipo.isDisponivel();        // Disponibilidade inicial do tipo
		this.isMegaMan = tipo.isMegaMan();            // Se pertence ao MegaMan ou inimigo
		
		// Inicializa flags de controle com valores padrão
		this.colidiu = false;                         // Ainda não colidiu
		this.podeDisparar = false;                    // Inicialmente não pode disparar
		this.podeDefinirPosicao = true;               // Pode definir posição inicial
		this.disparou = false;                        // Ainda não foi disparado
		
		// Define a velocidade de movimento
		this.velocidade = velocidade;
	}


	/**
	 * Verifica se o ataque já foi disparado
	 * @return true se o ataque foi disparado, false caso contrário
	 */
	public boolean isDisparou() {
		return disparou;
	}

	/**
	 * Define se o ataque foi disparado
	 * @param disparou true para marcar como disparado, false caso contrário
	 */
	public void setDisparou(boolean disparou) {
		this.disparou = disparou;
	}

	/**
	 * Verifica se o ataque está disponível para uso
	 * @return true se disponível, false caso contrário
	 */
	public boolean isDisponivel() {
		return disponivel;
	}

	/**
	 * Define a disponibilidade do ataque
	 * @param disponivel true para disponível, false para indisponível
	 */
	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}

	/**
	 * Verifica se o ataque pertence ao MegaMan
	 * @return true se é do MegaMan, false se é de um inimigo
	 */
	public boolean isMegaMan() {
		return isMegaMan;
	}

	/**
	 * Define se o ataque pertence ao MegaMan ou a um inimigo
	 * @param isMegaMan true para ataques do MegaMan, false para ataques de inimigos
	 */
	public void setMegaMan(boolean isMegaMan) {
		this.isMegaMan = isMegaMan;
	}

	/**
	 * Verifica se o ataque já colidiu com algo
	 * @return true se colidiu, false caso contrário
	 */
	public boolean isColidiu() {
		return colidiu;
	}

	/**
	 * Define se o ataque colidiu com algo
	 * Quando true, geralmente impede que o ataque continue se movendo
	 * @param colidiu true se colidiu, false caso contrário
	 */
	public void setColidiu(boolean colidiu) {
		this.colidiu = colidiu;
	}

	/**
	 * Retorna o tipo do ataque
	 * @return O enum TipoAtaque que define as características deste ataque
	 */
	public TipoAtaque getTipo() {
		return tipo;
	}

	/**
	 * Verifica se o ataque pode ser disparado
	 * @return true se pode disparar, false caso contrário
	 */
	public boolean isPodeDisparar() {
		return podeDisparar;
	}

	/**
	 * Define se o ataque pode ser disparado
	 * Controla quando o projétil está pronto para ser lançado
	 * @param podeDisparar true para permitir disparo, false para impedir
	 */
	public void setPodeDisparar(boolean podeDisparar) {
		this.podeDisparar = podeDisparar;
	}

	/**
	 * Verifica se a posição do ataque pode ser definida
	 * @return true se pode definir posição, false caso contrário
	 */
	public boolean isPodeDefinirPosicao() {
		return podeDefinirPosicao;
	}

	/**
	 * Define se a posição do ataque pode ser alterada
	 * Útil para controlar quando o ataque pode ser reposicionado
	 * @param podeDefinirPosicao true para permitir alteração de posição, false para fixar posição
	 */
	public void setPodeDefinirPosicao(boolean podeDefinirPosicao) {
		this.podeDefinirPosicao = podeDefinirPosicao;
	}

	/**
	 * Sobrescreve o método setRegion da classe pai para definir a região da textura
	 * Controla qual parte da textura será exibida e lida com o espelhamento (flip)
	 * 
	 * @param cordX Coordenada X inicial da região na textura
	 * @param cordY Coordenada Y inicial da região na textura  
	 * @param largura Largura da região a ser extraída
	 * @param altura Altura da região a ser extraída
	 */
	@Override
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		// Define a região da textura a ser utilizada
		region.setRegion(cordX, cordY, largura, altura);

		// Verifica se a região está atualmente espelhada horizontalmente
		boolean flipou = region.isFlipX();
		
		// Se o estado de direção (paraEsquerda) não corresponde ao flip atual
		if(paraEsquerda != flipou) {
			// Espelha a região horizontalmente para corresponder à direção
			region.flip(true, false);
		}

		// Atualiza o corpo (sprite) com a nova região
		corpo.setRegion(region);
	}

	/**
	 * Método que controla o movimento e animação do ataque quando disparado
	 * Move o projétil horizontalmente e atualiza sua animação
	 * Só executa se o ataque não colidiu e tem permissão para disparar
	 */
	public void disparar() {
		// Verifica se o ataque não colidiu e pode ser disparado
		if(!colidiu && podeDisparar){
			// Move o ataque horizontalmente pela velocidade definida
			setPosicao(posX + velocidade, posY);
			
			// Atualiza a região da textura para o frame atual da animação
			setRegion(tipo.getCordX1(), tipo.getCordY1(), tipo.getLargura1(), tipo.getAltura1());
			
			// Executa a animação do ataque usando os parâmetros do tipo
			animar(tipo.getQtdFrames1(), tipo.getIncrementa1(), tipo.getCordX1(), 
			tipo.getCordY1(), tipo.getLargura1(), tipo.getAltura1());
		}
	}

}