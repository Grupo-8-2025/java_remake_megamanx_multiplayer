package com.tp2.megamanx;

// Importações do LibGDX para manipulação de mapas, camadas, objetos e renderização
import com.badlogic.gdx.maps.tiled.TiledMap;                       // Representa o mapa Tiled
import com.badlogic.gdx.maps.tiled.TmxMapLoader;                   // Carrega mapas TMX
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer; // Renderiza mapas ortogonais
import com.badlogic.gdx.maps.MapObject;                            // Objeto genérico de camada
import com.badlogic.gdx.maps.MapLayer;                             // Camada do mapa
import com.badlogic.gdx.maps.objects.RectangleMapObject;           // Objeto retangular da camada
import com.badlogic.gdx.math.Rectangle;                            // Retângulo para colisão
import com.badlogic.gdx.utils.Array;                               // Array dinâmico do LibGDX
import com.badlogic.gdx.graphics.OrthographicCamera;               // Câmera ortográfica

/**
 * Classe Mapa gerencia o carregamento, renderização e colisores do mapa do jogo.
 * Utiliza mapas no formato TMX (Tiled) e extrai camadas de colisão para plataformas e paredes.
 * Permite renderização eficiente e acesso rápido aos retângulos de colisão para o sistema de física.
 */
public class Mapa {

    // Componentes principais do mapa
    private OrthographicCamera camera;                  // Câmera usada para renderizar o mapa
    private OrthogonalTiledMapRenderer renderizadorMapa; // Renderizador do mapa Tiled
    private TiledMap mapa;                              // Instância do mapa carregado

    // Arrays de retângulos para diferentes tipos de colisão
    private Array<Rectangle> retangulosColisao;             // Todos os retângulos de colisão
    private Array<Rectangle> retangulosColisaoChao;         // Retângulos de colisão do chão/plataformas
    private Array<Rectangle> retangulosColisaoParedeDireita;// Retângulos de colisão das paredes à direita
    private Array<Rectangle> retangulosColisaoParedeEsquerda;// Retângulos de colisão das paredes à esquerda
    private Rectangle colisaoRectanglePinguim;              // Retângulo especial para colisão do chefe Penguin
    private float escala = 3.8f;                            // Fator de escala do mapa (ajusta tamanho dos tiles)

    /**
     * Construtor do Mapa
     * Carrega o mapa TMX, inicializa a câmera e extrai todas as camadas de colisão
     * @param caminho Caminho do arquivo TMX
     * @param largura Largura da viewport/câmera
     * @param altura Altura da viewport/câmera
     */
    public Mapa(String caminho, int largura, int altura) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, largura, altura);

        this.mapa = new TmxMapLoader().load(caminho); // Carrega o mapa TMX
        this.renderizadorMapa = new OrthogonalTiledMapRenderer(mapa, escala); // Renderizador com escala
        
        // Inicializa arrays de colisores
        retangulosColisaoChao = new Array<>();
        retangulosColisaoParedeDireita = new Array<>();
        retangulosColisaoParedeEsquerda = new Array<>();
        retangulosColisao = new Array<>();

        // Carrega colisores de cada camada do mapa
        carregarColisores("chao", 1);
        carregarColisores("vertical-direita", 2);
        carregarColisores("vertical-esquerda", 3);
        carregarColisores("pinguim", 4);
    }

    /**
     * Carrega os retângulos de colisão de uma camada específica do mapa
     * @param nomeCamada Nome da camada no Tiled
     * @param tipo Tipo de camada (1: chão, 2: parede direita, 3: parede esquerda, 4: chefe)
     */
    private void carregarColisores(String nomeCamada, int tipo) {
        
        MapLayer camada = mapa.getLayers().get(nomeCamada);
        if (camada == null) {
            System.out.println("camada de colisão não encontrada: " + nomeCamada);
            return;
        }

        switch (tipo) {
            case 1: // Chão/plataformas
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);
                        retangulosColisaoChao.add(rect);
                    }
                }
                break;
            case 2: // Paredes à direita
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);
                        retangulosColisaoParedeDireita.add(rect);
                    }
                }
                break;
            case 3: // Paredes à esquerda
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);
                        retangulosColisaoParedeEsquerda.add(rect);
                    }
                }
                break;
            case 4: // Colisor especial do chefe Penguin
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {
                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);
                        colisaoRectanglePinguim = new Rectangle(rect);
                    }
                }
                break;
        }
        
        // Adiciona todos os retângulos carregados ao array geral de colisão
        for (Rectangle rect : retangulosColisaoChao) {
            retangulosColisao.add(rect);
        }
        for (Rectangle rect : retangulosColisaoParedeDireita) {
            retangulosColisao.add(rect);
        }
        for (Rectangle rect : retangulosColisaoParedeEsquerda) {
            retangulosColisao.add(rect);
        }
        if (colisaoRectanglePinguim != null) {
            retangulosColisao.add(colisaoRectanglePinguim);
        }
        System.out.println("Colisores carregados: " + retangulosColisaoChao.size);
    }

    /**
     * Retorna todos os retângulos de colisão do mapa (chão, paredes, chefe)
     * @return Array de retângulos de colisão
     */
    public Array<Rectangle> getRetangulosColisao() {
        return retangulosColisao;
    }

    /**
     * Retorna apenas os retângulos de colisão do chão/plataformas
     * @return Array de retângulos do chão
     */
    public Array<Rectangle> getChaos() {
        return retangulosColisaoChao;
    }

    /**
     * Retorna os retângulos de colisão das paredes à direita
     * @return Array de retângulos das paredes à direita
     */
    public Array<Rectangle> getRetangulosColisaoParedeDireita() {
        return retangulosColisaoParedeDireita;
    }

    /**
     * Retorna os retângulos de colisão das paredes à esquerda
     * @return Array de retângulos das paredes à esquerda
     */
    public Array<Rectangle> getRetangulosColisaoParedeEsquerda() {
        return retangulosColisaoParedeEsquerda;
    }

    /**
     * Retorna o fator de escala do mapa
     * @return Valor float da escala
     */
    public float getEscala() {
        return escala;
    }

    /**
     * Renderiza o mapa na tela usando a câmera fornecida
     * @param camera Câmera ortográfica para visualização
     */
    public void render(OrthographicCamera camera) {
        this.camera = camera;
        this.camera.update();
        renderizadorMapa.setView(this.camera);
        renderizadorMapa.render();
    }

    /**
     * Libera os recursos do mapa e do renderizador
     * Deve ser chamado ao fechar ou trocar de fase para evitar vazamento de memória
     */
    public void dispose() {
        mapa.dispose();
        renderizadorMapa.dispose();
    }
}
