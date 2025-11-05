package com.tp2.megamanx.Inimigos;

import com.tp2.megamanx.Ataque;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Vile extends Boss{

    public Vile(Texture textura, Ataque ataque) {
        super(
            textura, new TextureRegion(textura, 0, 0, 33, 45), 
            new Vector2(0.5f, 2.0f), // escala
            8320, 220, 25, 3, 
            ataque
        );
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


    public void atualizar(){
        if(!morreu && Math.abs(posMegaMan.x - posX) < 600){

            sofrerGravidade(posY, 1, 0, 53, 0, 43, 45, 0, 0, 43, 45); 
            
            tomandoDanoPorAtaque(1, 33, 0, 0, 43, 45, 
            0, 0, 43, 45); // Tem que ver isso

            delimitarMovimento(5200, 6200); 

            int acaoAnterior = determinaAcao;
            iterarDeltaTime(); 
            atualizarAcao(); 
            
            if(!noAr){
                if(determinaAcao != acaoAnterior){
                    if(determinaAcao == 0){
                        determinarAcaoMover();
                    }else if(determinaAcao == 1){
                        determinarAcaoParado();
                    }else if(determinaAcao == 2){
                        determinarAcaoAtaque();
                    } 
                } else {
                    atualizarAcao();
                }
            }
            
        }
    }

    @Override
    public void mover() {
        if (podeMover) {
            if (posMegaMan.x < posX) {
                moverParaEsquerda(2, 53, 106, 0, 53, 45);
            } else if (posMegaMan.x > posX) {
                moverParaDireita(2, 53, 106, 0, 53, 45);
            }
        } else {
            if (posMegaMan.x < posX) {
                paraDireita = false;
            } else if (posMegaMan.x > posX) {
                paraDireita = true;
            }
            setRegion(0, 0, 33, 45); 
        }
        parado(2, 53, 0, 0, 53, 45);
    }

    @Override
    public void atacar(){
        if(podeAtacar &&  deltaTime <= 0f && !tomandoDano){
            setRegion(53, 0, 43, 45); // Frame de ataque

            float posXataque = 0;
            float posYataque = corpo.getY() + 10f;

            int velocidadeAtaque = 0;
            if(posMegaMan.x > posX){
                paraDireita = true;
                velocidadeAtaque = 5;
                posXataque = corpo.getX() + corpo.getBoundingRectangle().width;
            }else if(posMegaMan.x < posX){
                paraDireita = false;
                velocidadeAtaque = -5;
                posXataque = corpo.getX();
            }

            Ataque novoAtaque = new Ataque(
                new TextureRegion(
                    ataqueAtual.getTipo().getTextura(),
                    ataqueAtual.getTipo().getCordX(), ataqueAtual.getTipo().getCordY(),
                    ataqueAtual.getTipo().getLargura(), ataqueAtual.getTipo().getAltura()),
                new Vector2(1f, 2.5f), posXataque, posYataque, 
                ataqueAtual.getTipo(), velocidadeAtaque, paraDireita
            );

            novoAtaque.setColidiu(false);
            novoAtaque.setPodeMovimentar(true);
            ataquesAtivos.add(novoAtaque);
        }
    }

    @Override
    public void morrer(){
        if(vida <= 0){
            morreu = true;
            setRegion(0, 0, 43, 45); 
            setPosicao(-500, -500); 
        }
    }

}
