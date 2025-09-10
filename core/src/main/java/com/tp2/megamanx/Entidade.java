package com.tp2.megamanx;

// Importações do LibGDX para funcionalidades gráficas e matemáticas
import com.badlogic.gdx.Gdx;                        // Acesso a funcionalidades do sistema (tempo, gráficos)
import com.badlogic.gdx.graphics.Texture;           // Para carregar e gerenciar texturas
import com.badlogic.gdx.graphics.g2d.Sprite;        // Para renderizar texturas com transformações
import com.badlogic.gdx.graphics.g2d.SpriteBatch;   // Para desenhar sprites em lote (batch rendering)
import com.badlogic.gdx.graphics.g2d.TextureRegion; // Para trabalhar com regiões específicas de texturas
import com.badlogic.gdx.math.Vector2;               // Para vetores 2D (usado para escala)

/**
 * Classe abstrata Entidade representa a base para todos os objetos visuais do jogo
 * 
 * Esta classe fornece funcionalidades fundamentais para:
 * - Posicionamento de objetos na tela
 * - Renderização de sprites com texturas
 * - Sistema de animação baseado em sprite sheets
 * - Controle de direção (esquerda/direita)
 * - Gerenciamento de tempo para animações
 * 
 * Serve como classe pai para personagens, inimigos, ataques e outros elementos visuais
 */
public class Entidade {

	// Atributos relacionados à textura e renderização
	protected Texture textura;          // Textura completa da entidade (sprite sheet)
	protected TextureRegion region;     // Região específica da textura a ser exibida (frame atual)
	protected float posX;                // Posição X (horizontal) da entidade no mundo do jogo
	protected float posY;                // Posição Y (vertical) da entidade no mundo do jogo
	protected Sprite corpo;              // Sprite que representa visualmente a entidade
	protected float deltaTime;           // Acumulador de tempo para controle de animações
	
	// Atributos de controle de direção
	protected boolean paraEsquerda;      // Flag indicando se a entidade está virada para a esquerda
	protected boolean paraDireita;       // Flag indicando se a entidade está virada para a direita

	/**
	 * Construtor da classe Entidade
	 * Inicializa uma entidade com textura, posição e escala específicas
	 * 
	 * @param textura Textura completa da entidade (sprite sheet)
	 * @param region Região inicial da textura a ser exibida
	 * @param posX Posição inicial X da entidade
	 * @param posY Posição inicial Y da entidade
	 * @param escala Vetor 2D contendo os fatores de escala (x, y) para redimensionamento
	 */
	Entidade(Texture textura, TextureRegion region, float posX, float posY, Vector2 escala){
		// Inicializa os atributos básicos da entidade
		this.textura = textura;
		this.region = region;
		this.posX = posX;
		this.posY = posY;
		
		// Inicializa o contador de tempo para animações
		deltaTime = 0f;
		
		// Define direção padrão (virada para a esquerda)
		paraEsquerda = true;
		paraDireita = false;

		// Cria e configura o sprite que representa visualmente a entidade
		this.corpo = new Sprite(textura);                    // Cria sprite com a textura
        this.corpo.setRegion(region);                        // Define a região inicial a ser exibida
        this.corpo.setScale(escala.x, escala.y);            // Aplica a escala de redimensionamento
        this.corpo.setPosition(posX, posY);                  // Define a posição inicial
        // Define o ponto de origem do sprite no centro para rotações e transformações
        this.corpo.setOrigin(this.corpo.getBoundingRectangle().width/2, this.corpo.getBoundingRectangle().height/2);
	}
	

	/**
	 * Retorna a posição X atual da entidade
	 * @return Coordenada X (horizontal) da entidade
	 */
	public float getPosX() {
		return posX;
	}

	/**
	 * Define uma nova posição X para a entidade
	 * Atualiza apenas o atributo interno, não move o sprite visualmente
	 * @param posX Nova coordenada X
	 */
	public void setPosX(float posX) {
		this.posX = posX;
	}

	/**
	 * Retorna a posição Y atual da entidade
	 * @return Coordenada Y (vertical) da entidade
	 */
	public float getPosY() {
		return posY;
	}

	/**
	 * Define uma nova posição Y para a entidade
	 * Atualiza apenas o atributo interno, não move o sprite visualmente
	 * @param posY Nova coordenada Y
	 */
	public void setPosY(float posY) {
		this.posY = posY;
	}

	/**
	 * Retorna o sprite que representa visualmente a entidade
	 * @return Objeto Sprite usado para renderização
	 */
	public Sprite getCorpo() {
		return corpo;
	}

	/**
	 * Método protegido para definir uma região específica da textura
	 * Controla qual parte da sprite sheet será exibida e gerencia o espelhamento
	 * 
	 * @param cordX Coordenada X inicial da região na textura
	 * @param cordY Coordenada Y inicial da região na textura
	 * @param largura Largura da região a ser extraída
	 * @param altura Altura da região a ser extraída
	 */
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		// Define a região da textura que será exibida
		region.setRegion(cordX, cordY, largura, altura);

		// Verifica se a região está atualmente espelhada horizontalmente
		boolean flipou = region.isFlipX();
		
		// Se a direção atual (paraDireita) não corresponde ao estado de flip
		if(paraDireita != flipou) {
			// Espelha a região horizontalmente para corresponder à direção
			region.flip(true, false);
		}

		// Atualiza o sprite com a nova região
		corpo.setRegion(region);
	}

	/**
	 * Define uma nova posição para a entidade
	 * Atualiza tanto os atributos internos quanto a posição visual do sprite
	 * 
	 * @param posX Nova coordenada X
	 * @param posY Nova coordenada Y
	 */
	public void setPosicao(float posX, float posY) {
		// Atualiza as coordenadas internas
		setPosX(posX);
        setPosY(posY);
        // Move o sprite visualmente para a nova posição
        corpo.setPosition(posX, posY);
	}


	/**
	 * Renderiza a entidade na tela usando o SpriteBatch fornecido
	 * @param batch SpriteBatch ativo para desenhar o sprite
	 */
	public void draw(SpriteBatch batch) {
		corpo.draw(batch);
	}

	/**
	 * Método protegido para atualizar o contador de tempo da animação
	 * Adiciona o tempo decorrido desde o último frame ao deltaTime
	 */
	protected void iterarDeltaTime() {
		deltaTime += Gdx.graphics.getDeltaTime();
	}

	/**
	 * Método de animação baseado em posição
	 * Calcula o frame da animação baseado em uma posição específica
	 * 
	 * @param posicao Posição usada para calcular o frame (geralmente posição X)
	 * @param qtdFrames Número total de frames na animação
	 * @param incrementa Incremento em pixels entre frames na sprite sheet
	 * @param cordX Coordenada X inicial da animação na textura
	 * @param cordY Coordenada Y inicial da animação na textura
	 * @param largura Largura de cada frame
	 * @param altura Altura de cada frame
	 */
	protected void animar(float posicao, int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		// Calcula qual frame exibir baseado na posição
		// Divide por 50 para criar uma animação suave e usa módulo para repetir
		int x = (int) posicao / 50 % qtdFrames;
        // Multiplica pelo incremento para obter a posição correta na sprite sheet
        x = x * incrementa;
        // Define a região da textura correspondente ao frame calculado
        setRegion(cordX + x, cordY, largura, altura); 
	}

	/**
	 * Método de animação baseado em tempo
	 * Calcula o frame da animação baseado no tempo decorrido
	 * 
	 * @param qtdFrames Número total de frames na animação
	 * @param incrementa Incremento em pixels entre frames na sprite sheet
	 * @param cordX Coordenada X inicial da animação na textura
	 * @param cordY Coordenada Y inicial da animação na textura
	 * @param largura Largura de cada frame
	 * @param altura Altura de cada frame
	 */
	public void animar(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		// Atualiza o contador de tempo
		iterarDeltaTime();
		
		// Calcula qual frame exibir baseado no tempo
		// Muda de frame a cada 0.25 segundos (4 FPS de animação)
		int frame = (int)(deltaTime / 0.25f) % qtdFrames; 
        // Multiplica pelo incremento para obter a posição correta na sprite sheet
        int x = frame * incrementa; 
        // Define a região da textura correspondente ao frame calculado
        setRegion(cordX + x, cordY, largura, altura); 
	}

}
