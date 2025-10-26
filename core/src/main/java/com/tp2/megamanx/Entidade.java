package com.tp2.megamanx;

import com.badlogic.gdx.Gdx;                        
import com.badlogic.gdx.graphics.Texture;           
import com.badlogic.gdx.graphics.g2d.Sprite;        
import com.badlogic.gdx.graphics.g2d.SpriteBatch;   
import com.badlogic.gdx.graphics.g2d.TextureRegion; 
import com.badlogic.gdx.math.Vector2;               

public class Entidade {

	protected Texture textura;          
	protected TextureRegion region;     
	protected float posX;              
	protected float posY;                
	protected Sprite corpo;              
	protected float deltaTime;           
	protected boolean paraDireita;       

	Entidade(Texture textura, TextureRegion region, Vector2 escala, float posX, float posY){
		this.textura = textura;
		this.region = region;
		this.posX = posX;
		this.posY = posY;

		this.corpo = new Sprite(textura);                    
        this.corpo.setRegion(region);                        
        this.corpo.setScale(escala.x, escala.y);            
        this.corpo.setPosition(posX, posY);                  
        this.corpo.setOrigin(this.corpo.getBoundingRectangle().width/2, this.corpo.getBoundingRectangle().height/2);
		
		deltaTime = 0f;
		paraDireita = false;
	}

	Entidade(Texture textura, TextureRegion region, Vector2 escala){
		this.textura = textura;
		this.region = region;
		this.posX = 0;
		this.posY = 0;

		this.corpo = new Sprite(textura);                    
        this.corpo.setRegion(region);                        
        this.corpo.setScale(escala.x, escala.y);            
        this.corpo.setPosition(posX, posY);                  
        this.corpo.setOrigin(this.corpo.getBoundingRectangle().width/2, this.corpo.getBoundingRectangle().height/2);
		
		deltaTime = 0f;
		paraDireita = false;
	}
	
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
		corpo.setPosition(this.posX, this.posY);
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
		corpo.setPosition(this.posX, this.posY);
	}

	public Sprite getCorpo() {
		return corpo;
	}

	protected void setRegion(int cordX, int cordY, int largura, int altura) {
		region.setRegion(cordX, cordY, largura, altura);
		boolean flipou = region.isFlipX();

		if(paraDireita == flipou) {
			region.flip(true, false);
		}

		corpo.setRegion(region);
	}

	/* 
	protected void setRegion(int cordX, int cordY, int largura, int altura) {
        region.setRegion(cordX, cordY, largura, altura);

        boolean flipou = region.isFlipX();
        if(paraDireita != flipou) {
            region.flip(true, false);
        }

        corpo.setRegion(region);
    }
	*/

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
