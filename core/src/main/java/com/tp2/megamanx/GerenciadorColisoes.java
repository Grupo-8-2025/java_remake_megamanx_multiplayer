package com.tp2.megamanx;

// Importações de iteradores personalizados para percorrer inimigos e personagens
import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.Iterators.PersonagemIterator;

import java.util.ArrayList;

// Importações do LibGDX para manipulação de retângulos e arrays
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Classe GerenciadorColisoes centraliza toda a lógica de detecção e tratamento de colisões do jogo.
 * Responsável por verificar colisões entre personagens, inimigos, plataformas, ataques e paredes.
 * Fornece métodos utilitários para diferentes tipos de colisão, facilitando a manutenção e expansão do jogo.
 */
public class GerenciadorColisoes {

    /**
     * Método privado utilitário para verificar se dois retângulos colidem (se sobrepõem)
     * @param corpo1 Retângulo do primeiro objeto
     * @param corpo2 Retângulo do segundo objeto
     * @return true se houver sobreposição, false caso contrário
     */
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

    /**
     * Verifica colisão de um personagem com paredes à direita e à esquerda
     * Atualiza flags de movimento e estado de parede do personagem
     * @param paredesDireita Lista de retângulos representando paredes à direita
     * @param paredesEsquerda Lista de retângulos representando paredes à esquerda
     * @param personagem Personagem a ser verificado
     */
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

    /**
     * Verifica colisão de todos os personagens com plataformas
     * Atualiza estados de estar no ar, na plataforma e velocidade vertical
     * @param plataformas Lista de retângulos das plataformas
     * @param personagens Iterator de personagens a serem verificados
     */
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
    
    /**
     * Verifica colisão de ataques com plataformas
     * Remove ataques que colidem e os marca como inativos
     * @param plataformas Lista de retângulos das plataformas
     * @param ataques Lista de ataques ativos
     */
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

    /**
     * Verifica colisão de ataques de inimigos com o MegaMan
     * Aplica dano ao MegaMan e remove o ataque se houver colisão
     * @param megaMan Instância do MegaMan
     * @param ataques Lista de ataques ativos
     */
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

    /**
     * Verifica colisão de ataques do MegaMan com inimigos
     * Aplica dano ao inimigo e remove o ataque se houver colisão
     * @param inimigo Instância do inimigo
     * @param ataques Lista de ataques ativos do MegaMan
     */
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

    /**
     * Verifica colisão direta entre MegaMan e inimigos
     * Aplica dano por contato ao MegaMan
     * @param megaman Instância do MegaMan
     * @param inimigos Iterator de inimigos ativos
     */
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

