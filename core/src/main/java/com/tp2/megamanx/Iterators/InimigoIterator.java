package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.Inimigo;

public class InimigoIterator implements Iterator<Inimigo> {

    private InimigoCollection colecao;
    private int indexAtual;
        
    public InimigoIterator(InimigoCollection colecao) {
        this.colecao = colecao;
        this.indexAtual = 0;
    }

    public InimigoIterator(){
        this.colecao = new InimigoCollection();
        this.indexAtual = 0;
    }
        
    @Override
    public boolean hasNext() {
        return indexAtual < colecao.tamanho();
    }
        
    @Override
    public Inimigo next() {
        if (!hasNext()) {
            throw new RuntimeException("fim coleção");
        }
        return colecao.obterInimigo(indexAtual++);
    }
        
    public void reset() {
        indexAtual = 0;
    }
    
    public int getIndexAtual() {
        return indexAtual;
    }

    public void add(Inimigo inimigo) {
        if (colecao == null) {
            colecao = new InimigoCollection();
        }
        colecao.adicionarInimigo(inimigo);
    }

    public Inimigo get(int index){
        return colecao.obterInimigo(index);
    }

    public InimigoCollection getColecao() {
        return colecao;
    }

    public void setColecao(InimigoCollection colecao) {
        this.colecao = colecao;
    }

    public void skipNext() {
        if (hasNext()) {
            indexAtual++;
        }
    }
}

