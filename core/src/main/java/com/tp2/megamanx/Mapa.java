package com.tp2.megamanx;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Mapa {

    private OrthographicCamera camera;
    private OrthogonalTiledMapRenderer renderizadorMapa;
    private TiledMap mapa;
    private Array<Rectangle> retangulosColisao;
    private Array<Rectangle> retangulosColisaoChao;
    private Array<Rectangle> retangulosColisaoParedeDireita;
    private Array<Rectangle> retangulosColisaoParedeEsquerda;
    private Rectangle colisaoRectanglePinguim;
    private float escala = 3.8f;

    public Mapa(String caminho, int largura, int altura) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, largura, altura);

        this.mapa = new TmxMapLoader().load(caminho);
        this.renderizadorMapa = new OrthogonalTiledMapRenderer(mapa, escala);
        
        retangulosColisaoChao = new Array<>();
        retangulosColisaoParedeDireita = new Array<>();
        retangulosColisaoParedeEsquerda = new Array<>();
        retangulosColisao = new Array<>();

        carregarColisores("chao", 1);
        carregarColisores("vertical-direita", 2);
        carregarColisores("vertical-esquerda", 3);
        carregarColisores("pinguim", 4);
    }

    private void carregarColisores(String nomeCamada, int tipo) {
        
        MapLayer camada = mapa.getLayers().get(nomeCamada);
        if (camada == null) {
            System.out.println("camada de colisão não encontrada: " + nomeCamada);
            return;
        }

        switch (tipo) {
            case 1:
                
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {

                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                    rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);

                    retangulosColisaoChao.add(rect);
                    }
                }
                break;
            case 2:
                
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {

                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                    rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);

                    retangulosColisaoParedeDireita.add(rect);
                    }
                }
                
                break;

            case 3:
                
                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {

                    Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                    rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);

                    retangulosColisaoParedeEsquerda.add(rect);
                    }
                }
                
                break;
                
            case 4:

                for (MapObject objeto : camada.getObjects()) {
                    if (objeto instanceof RectangleMapObject) {

                        Rectangle rect = ((RectangleMapObject) objeto).getRectangle();
                        rect.set( rect.x * escala, rect.y * escala, rect.width * escala, rect.height * escala);

                        colisaoRectanglePinguim = new Rectangle(rect);
                    }
                }
                break;
        }
        
        
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

        //retangulosColisao.add(colisaoRectanglePinguim);

        System.out.println("Colisores carregados: " + retangulosColisaoChao.size);
    }

    public Array<Rectangle> getRetangulosColisao() {
        return retangulosColisao;
    }

    public Array<Rectangle> getChaos() {
        return retangulosColisaoChao;
    }

    public Array<Rectangle> getRetangulosColisaoParedeDireita() {
        return retangulosColisaoParedeDireita;
    }

    public Array<Rectangle> getRetangulosColisaoParedeEsquerda() {
        return retangulosColisaoParedeEsquerda;
    }

    public float getEscala() {
        return escala;
    }

    public void render(OrthographicCamera camera) {
        this.camera = camera;
        this.camera.update();
        renderizadorMapa.setView(this.camera);
        renderizadorMapa.render();
    }

    public void dispose() {
        mapa.dispose();
        renderizadorMapa.dispose();
    }
}
