package com.tp2.megamanx;

// Importação de ArrayList para gerenciar múltiplos ataques ativos
import java.util.ArrayList;

// Importação do LibGDX para manipulação de retângulos (usado para colisão)
import com.badlogic.gdx.math.Rectangle;

/**
 * Interface Inimigo define o contrato para todos os inimigos do jogo.
 * 
 * Permite que diferentes tipos de inimigos sejam tratados de forma polimórfica,
 * garantindo que todos implementem métodos essenciais para interação, colisão e combate.
 */
public interface Inimigo {
    
    /**
     * Retorna o retângulo de colisão do inimigo
     * Usado para detectar colisões com outros objetos (MegaMan, ataques, plataformas)
     * @return Rectangle representando a área ocupada pelo inimigo
     */
    public Rectangle getRect();

    /**
     * Retorna o valor de dano que o inimigo causa ao MegaMan por contato
     * @return Valor inteiro do dano
     */
    public int getDano();

    /**
     * Retorna a lista de ataques ativos do inimigo
     * Permite que o sistema de colisão verifique projéteis disparados pelo inimigo
     * @return Lista de objetos Ataque ativos
     */
    public ArrayList<Ataque> getAtaquesAtivos();

    /**
     * Atualiza a posição X do MegaMan para que o inimigo possa reagir (ex: mirar, perseguir)
     * @param posX Posição X atual do MegaMan
     */
    public void setPosXmegaMan(float posX);

    /**
     * Aplica dano ao inimigo (ex: quando atingido por um ataque do MegaMan)
     * @param dano Quantidade de dano a ser subtraída da vida do inimigo
     */
    public void tomarDano(int dano);
    
}
