package com.tp2.megamanx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Entidade {

	protected Texture textura;
	protected TextureRegion region;
	protected float posX;
	protected float posY;
	protected Sprite corpo;
	protected float deltaTime;
	
	protected boolean paraEsquerda;
	protected boolean paraDireita;
	private Rectangle contactArea;

	Entidade(Texture textura, TextureRegion region, float posX, float posY, Vector2 escala){
		this.textura = textura;
		this.region = region;
		this.posX = posX;
		this.posY = posY;
		deltaTime = 0f;
		paraEsquerda = true;
		paraDireita = false;

		this.corpo = new Sprite(textura);
        this.corpo.setRegion(region);
        this.corpo.setScale(escala.x, escala.y);
        this.corpo.setPosition(posX, posY);
        this.corpo.setOrigin(this.corpo.getBoundingRectangle().width/2, this.corpo.getBoundingRectangle().height/2);
	}

	Entidade(Rectangle contactArea, float posX, float posY, Vector2 escala){
		this.contactArea = contactArea;
		this.posX = posX;
		this.posY = posY;
		deltaTime = 0f;
		paraEsquerda = true;
		paraDireita = false;

		this.corpo = new Sprite(textura);
        this.corpo.setRegion(region);
        this.corpo.setScale(escala.x, escala.y);
        this.corpo.setPosition(posX, posY);
        this.corpo.setOrigin(this.corpo.getBoundingRectangle().width/2, this.corpo.getBoundingRectangle().height/2);
	}
	
	public Rectangle getRect(){
    	return contactArea;
	}

	public void setRect(int cordX, int cordY, int largura, int altura){
		contactArea.set(cordX, cordY, largura, altura);
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public Sprite getCorpo() {
		return corpo;
	}

	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		region.setRegion(cordX, cordY, largura, altura);

		boolean flipou = region.isFlipX();
		if(paraDireita != flipou) {
			region.flip(true, false);
		}

		corpo.setRegion(region);
	}

	public void setPosicao(float posX, float posY) {
		setPosX(posX);
        setPosY(posY);
        corpo.setPosition(posX, posY);
	}


	public void draw(SpriteBatch batch) {
		corpo.draw(batch);
	}

	protected void iterarDeltaTime() {
		deltaTime += Gdx.graphics.getDeltaTime();
	}

	protected void animar(float posicao, int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		int x = (int) posicao / 50 % qtdFrames;
        x = x * incrementa;
        setRegion(cordX + x, cordY, largura, altura); 
	}

	public void animar(int qtdFrames, int incrementa, int cordX, int cordY, int largura, int altura) {
		iterarDeltaTime();
		int frame = (int)(deltaTime / 0.25f) % qtdFrames; 
        int x = frame * incrementa; 
        setRegion(cordX + x, cordY, largura, altura); 
	}

}
