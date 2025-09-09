package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Jaminger extends Personagem implements Inimigo{

    public Jaminger(Texture textura, float posX, float posY, Ataque ataque, float velX, float velY) {
		super(textura, new TextureRegion(textura, 390, 0, 39, 75), posX, posY, new Vector2(0.3f, 1.5f), 
        3, 2, ataque, velX, velY);
	}


    public Rectangle getRect(){
		return corpo.getBoundingRectangle();
	}

    public int getDano() {
		return dano;
	}

	public ArrayList<Ataque> getAtaquesAtivos(){
		return ataquesAtivos;
	}

	public void setPosXmegaMan(float posXmegaMan) {
		this.posXmegaMan = posXmegaMan;
	}

	public void tomarDano(int dano) {
		vida = vida - dano;
		tomandoDano = true;
		deltaTime = 0f;
	}


	@Override
	public void mover(){
		sofrerGravidade(posY, 1, 39, 0, 0, 39, 75, 0, 0, 39, 75);
		animar(10, 39, 189, 0, 39, 75);
	}

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
