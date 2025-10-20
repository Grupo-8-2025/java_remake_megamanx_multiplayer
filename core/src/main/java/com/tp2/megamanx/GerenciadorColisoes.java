package com.tp2.megamanx;

import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class GerenciadorColisoes {

    private boolean colisaoCorpos(Rectangle corpo1, Rectangle corpo2){
        if (corpo1.overlaps(corpo2)) {
            return true;
        }else return false;
    }
    
    /*
    public void colisaoMegaManPlataforma(Array<Rectangle> plataformas, MegaMan megaMan){
        Rectangle corpoMegaman = megaMan.getCorpo().getBoundingRectangle();
        boolean colidiuDireita = false;
        boolean colidiuEsquerda = false;
        for(Rectangle plataforma : plataformas){
            if(colisaoCorpos(corpoMegaman, plataforma)){

                if(plataforma.height > 50 && plataforma.width > 100){
                    if(corpoMegaman.y + corpoMegaman.height > plataforma.y + plataforma.height){
                        if(corpoMegaman.x + corpoMegaman.width > plataforma.x 
                        && corpoMegaman.x < plataforma.x){
                            colidiuDireita = true;
                            megaMan.setPodeAndarDireita(false);
                            megaMan.setRegion(0, 16, 34, 34);
                        }

                        if(corpoMegaman.x < plataforma.x + plataforma.width 
                        && corpoMegaman.x + corpoMegaman.width > plataforma.x + plataforma.width){
                            colidiuEsquerda = true;
                            megaMan.setPodeAndarEsquerda(false);
                            megaMan.setRegion(0, 16, 34, 34);
                        }
                    }
                    break;
                }
                
            }
            
        }
        if(!colidiuDireita) megaMan.setPodeAndarDireita(true);
        if(!colidiuEsquerda) megaMan.setPodeAndarEsquerda(true);
    }*/

    // public void colisaoMegaManParedes(Array<Rectangle> paredesDireita, Array<Rectangle> paredesEsquerda, MegaMan megaMan) {
    //     Rectangle corpo = megaMan.getCorpo().getBoundingRectangle();
    //     boolean colidiuDireita = false;
    //     boolean colidiuEsquerda = false;

    //     for (Rectangle parede : paredesDireita) {
    //         if (corpo.overlaps(parede)) {
    //             colidiuDireita = true;
    //             megaMan.setNaParede(true);
    //             megaMan.setPodeAndarDireita(false);                
    //         }
    //     }

    //     for (Rectangle parede : paredesEsquerda) {
    //         if (corpo.overlaps(parede)) {
    //             colidiuEsquerda = true;
    //             megaMan.setNaParede(true);
    //             megaMan.setPodeAndarEsquerda(false);
    //         }
    //     }

    //     if (!colidiuDireita && !colidiuEsquerda) {
    //         megaMan.setNaParede(false);
    //         megaMan.setPodeAndarDireita(true);
    //         megaMan.setPodeAndarEsquerda(true);
    //     }
    // }

    public void colisaoPersonagemParedes(Array<Rectangle> paredesDireita, Array<Rectangle> paredesEsquerda, Personagem personagem) {
        Rectangle corpo = personagem.getCorpo().getBoundingRectangle();
        boolean colidiuDireita = false;
        boolean colidiuEsquerda = false;

        for (Rectangle parede : paredesDireita) {
            if (corpo.overlaps(parede)) {
                colidiuDireita = true;
                personagem.setNaParede(true);
                personagem.setPodeAndarDireita(false);
            }
        }

        for (Rectangle parede : paredesEsquerda) {
            if (corpo.overlaps(parede)) {
                colidiuEsquerda = true;
                personagem.setNaParede(true);
                personagem.setPodeAndarEsquerda(false);
            }
        }

        if (!colidiuDireita && !colidiuEsquerda) {
            personagem.setNaParede(false);
            personagem.setPodeAndarDireita(true);
            personagem.setPodeAndarEsquerda(true);
        }
    }

    public void colisaoPersonagensPlataformas(Array<Rectangle> plataformas, PersonagemIterator personagens){
        personagens.reset();
        while (personagens.hasNext()) {
            boolean colidiu = false;
            Personagem personagem = personagens.next();
            Rectangle corpo = personagem.getCorpo().getBoundingRectangle();
            for(Rectangle plataforma : plataformas){
                if(colisaoCorpos(corpo, plataforma)){

                    float posTopoPlataforma = plataforma.y + plataforma.height;
                    if(personagem.getVelY() <= 0 && corpo.y < posTopoPlataforma){
                        personagem.setNaPlataforma(true);
                        personagem.setNoAr(false);
                        personagem.setPosicao(personagem.getPosX(), personagem.getPosY());
                        personagem.setVelY(0);
                        colidiu = true;
                    }

                    float posTopoPersonagem = corpo.y + corpo.height;
                    if(posTopoPersonagem < plataforma.y + plataforma.height){
                        personagem.setNaPlataforma(false);
                        personagem.setNoAr(true);
                        personagem.setPosicao(personagem.getPosX(), personagem.getPosY());
                    }

                    break;
                }
            }
            if(!colidiu){
                personagem.setNaPlataforma(false);
                personagem.setNoAr(true);
            }
        }
        personagens.reset();
    } 
    
    
    public void colisaoAtaquesPlataformas(Array<Rectangle> plataformas, ArrayList<Ataque> ataques){
        for(Rectangle plataforma : plataformas){
            for(int i=0; i<ataques.size(); i++){
                Ataque ataque = ataques.get(i);
                Rectangle corpoAtaque = ataque.getCorpo().getBoundingRectangle();
                if (colisaoCorpos(plataforma, corpoAtaque)) {
                    ataque.setColidiu(true);
                    ataque.setPodeDisparar(false);
                    ataque.setPosicao(-100, -100);
                    ataques.remove(ataque);
                }
            }
        }
    }

    public void colisaoAtaquesMegaman(MegaMan megaMan, ArrayList<Ataque> ataques) {
        Rectangle corpoMegaman = megaMan.getCorpo().getBoundingRectangle();
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            Rectangle corpoAtaque = ataque.getCorpo().getBoundingRectangle();

            if(colisaoCorpos(corpoAtaque, corpoMegaman)){
                ataque.setColidiu(true);
                ataque.setPodeDisparar(false);
                ataque.setPosicao(-100, -100);
                megaMan.tomarDano(ataque.getTipo().getDano());
                System.out.println("Personagem e ataque");
                ataques.remove(i);
                break; 
            }

        } 
    }

    public void colisaoAtaquesMegamanInimigos(Inimigo inimigo, ArrayList<Ataque> ataques){
        Rectangle corpoPersonagem = inimigo.getRect();
        for (int i = 0; i < ataques.size(); i++) {
            Ataque ataque = ataques.get(i);
            Rectangle corpoAtaque = ataque.getCorpo().getBoundingRectangle();

            if(colisaoCorpos(corpoAtaque, corpoPersonagem)){
                ataque.setColidiu(true);
                ataque.setPodeDisparar(false);
                ataque.setPosicao(-100, -100);
                inimigo.tomarDano(ataque.getTipo().getDano());
                ataques.remove(i);
                break; 
            }

        } 
    }

    public void colisaoMegaManInimigos(MegaMan megaman, InimigoIterator inimigos){
        Rectangle corpoMegaman = megaman.getCorpo().getBoundingRectangle();
        inimigos.reset();
        while (inimigos.hasNext()) {
            Inimigo inimigo = inimigos.next();
            Rectangle corpoInimigo = inimigo.getRect();

            if (colisaoCorpos(corpoMegaman, corpoInimigo)) {
                megaman.tomarDanoPorContato(inimigo.getDano());
            }
        }
    }

}

