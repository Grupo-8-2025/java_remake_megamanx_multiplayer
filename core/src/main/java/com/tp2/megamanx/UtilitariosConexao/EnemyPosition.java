package com.tp2.megamanx.UtilitariosConexao;

import java.util.ArrayList;

/* 
 * Classe simples para representar a posição dos inimigos na rede. 
 */
public class EnemyPosition {
    public ArrayList<Float> x = new ArrayList<>();
    public ArrayList<Float> y = new ArrayList<>();
    public ArrayList<Integer> ids = new ArrayList<>(); // ids dos inimigos
    public ArrayList<Integer> types = new ArrayList<>(); // tipo de inimigo (codigo definido no servidor)

    public EnemyPosition() {}
}