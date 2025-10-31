package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.Personagem;

public class PersonagemIterator implements Iterator<Personagem> {

    private PersonagemCollection colecao;
    private int indexAtual;
        
    public PersonagemIterator(PersonagemCollection colecao) {
        this.colecao = colecao;
        this.indexAtual = 0;
    }

    public PersonagemIterator(){
        this.colecao = new PersonagemCollection();
        this.indexAtual = 0;
    }


    public int getIndexAtual() {
        return indexAtual;
    }

    public Personagem get(int index){
        return colecao.obterPersonagem(index);
    }

    public PersonagemCollection getColecao() {
        return colecao;
    }

    public void setColecao(PersonagemCollection colecao) {
        this.colecao = colecao;
    }

    
    public void reset() {
        indexAtual = 0;
    }

    public void add(Personagem personagem) {
        if (colecao == null) {
            colecao = new PersonagemCollection();
        }
        colecao.adicionarPersonagem(personagem);
    }

    public void clear(){
        colecao.limpar();
    }

    public void skipNext() {
        if (hasNext()) {
            indexAtual++;
        }
    }

        
    @Override
    public boolean hasNext() {
        return indexAtual < colecao.tamanho();
    }
        
    @Override
    public Personagem next() {
        if (!hasNext()) {
            throw new RuntimeException("fim coleção");
        }
        return colecao.obterPersonagem(indexAtual++);
    }

}


