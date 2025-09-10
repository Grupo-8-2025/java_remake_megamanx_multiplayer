package com.tp2.megamanx;

// Importações do LibGDX para funcionalidades de interface gráfica
import com.badlogic.gdx.graphics.g2d.BitmapFont;     // Para renderização de texto
import com.badlogic.gdx.graphics.g2d.SpriteBatch;    // Para desenhar texturas e texto
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; // Para desenhar formas geométricas
import com.badlogic.gdx.graphics.Color;              // Para definir cores
import com.badlogic.gdx.math.Rectangle;              // Para definir área retangular do botão
import com.badlogic.gdx.Gdx;                         // Para acesso a funcionalidades do sistema (input, gráficos)

/**
 * Classe Botao representa um elemento de interface gráfica clicável
 * 
 * Esta classe implementa um botão simples com:
 * - Área retangular clicável
 * - Texto personalizado
 * - Cores configuráveis para fundo, borda e texto
 * - Detecção de cliques do mouse/touch
 * - Renderização visual completa
 */
public class Botao {
    // Atributos do botão
    private Rectangle bounds;    // Define a área retangular do botão (posição e dimensões)
    private String texto;        // Texto a ser exibido no botão
    private Color corFundo;      // Cor de preenchimento do fundo do botão
    private Color corBorda;      // Cor da borda/contorno do botão
    private Color corTexto;      // Cor do texto exibido no botão

    /**
     * Construtor da classe Botao
     * Inicializa um botão com posição, dimensões, texto e cores personalizadas
     * 
     * @param x Posição X (horizontal) do canto inferior esquerdo do botão
     * @param y Posição Y (vertical) do canto inferior esquerdo do botão
     * @param w Largura do botão em pixels
     * @param h Altura do botão em pixels
     * @param texto Texto a ser exibido no botão
     * @param corFundo Cor de preenchimento do fundo
     * @param corBorda Cor da borda do botão
     * @param corTexto Cor do texto do botão
     */
    public Botao(float x, float y, float w, float h, String texto, Color corFundo, Color corBorda, Color corTexto) {
        // Cria o retângulo que define a área clicável do botão
        this.bounds = new Rectangle(x, y, w, h);
        // Armazena o texto a ser exibido
        this.texto = texto;
        // Define as cores para os diferentes elementos visuais
        this.corFundo = corFundo;
        this.corBorda = corBorda;
        this.corTexto = corTexto;
    }

    /**
     * Método responsável por renderizar o botão na tela
     * Desenha o fundo, borda e texto do botão usando os renderizadores fornecidos
     * 
     * @param shape ShapeRenderer para desenhar formas geométricas (fundo e borda)
     * @param batch SpriteBatch para desenhar o texto
     * @param font BitmapFont para definir a fonte do texto
     */
    public void desenhar(ShapeRenderer shape, SpriteBatch batch, BitmapFont font) {
        
        // === DESENHO DO FUNDO ===
        // Inicia o modo de renderização preenchida (formas sólidas)
        shape.begin(ShapeRenderer.ShapeType.Filled);
        // Define a cor do fundo
        shape.setColor(corFundo);
        // Desenha um retângulo preenchido nas dimensões do botão
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        // Finaliza o modo de renderização preenchida
        shape.end();

        // === DESENHO DA BORDA ===
        // Inicia o modo de renderização de linhas (contornos)
        shape.begin(ShapeRenderer.ShapeType.Line);
        // Define a cor da borda
        shape.setColor(corBorda);
        // Desenha um retângulo vazado (apenas contorno) nas dimensões do botão
        shape.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        // Finaliza o modo de renderização de linhas
        shape.end();

        // === DESENHO DO TEXTO ===
        // Inicia o batch para renderização de texto/texturas
        batch.begin();
        // Obtém a largura da região da fonte (não utilizada atualmente - poderia ser removida)
        // float textoLargura = font.getRegion().getRegionWidth();
        // Define a cor do texto
        font.setColor(corTexto);
        // Desenha o texto do botão
        // Posição: x + margem de 20px, y centrado verticalmente + ajuste de 10px
        font.draw(batch, texto, bounds.x + 20, bounds.y + bounds.height/2 + 10);
        // Finaliza o batch
        batch.end();
    }

    /**
     * Método que verifica se o botão foi clicado
     * Detecta se houve um clique/toque na área do botão no frame atual
     * 
     * @return true se o botão foi clicado neste frame, false caso contrário
     */
    public boolean foiClicado() {
        // Verifica se houve um toque/clique que acabou de acontecer neste frame
        if (Gdx.input.justTouched()) {
            // Obtém a coordenada X do clique/toque
            float x = Gdx.input.getX();
            
            // Obtém a coordenada Y do clique/toque
            // Nota: LibGDX usa sistema de coordenadas com Y=0 no topo da tela,
            // mas o sistema de desenho usa Y=0 na parte inferior
            // Por isso subtrai-se a posição Y da altura total da tela
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            
            // Verifica se as coordenadas do clique estão dentro da área do botão
            return bounds.contains(x, y);
        }
        
        // Retorna false se não houve clique neste frame
        return false;
    }
}