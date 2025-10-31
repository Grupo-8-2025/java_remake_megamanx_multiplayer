package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.Personagem;

public class PersonagemCollection implements IterableCollection<Personagem> {
    
    private Personagem[] personagens;
    private int size;
    private int indexAtual;
    
    public PersonagemCollection() {
        this.personagens = new Personagem[10];
        this.size = 0;
        this.indexAtual = 0;
    }
    
    public PersonagemCollection(int capacity) {
        this.personagens = new Personagem[capacity];
        this.size = 0;
        this.indexAtual = 0;
    }


    public int tamanho() {
        return size;
    }
    
    public boolean estaVazia() {
        return size == 0;
    }
    
    
    public void adicionarPersonagem(Personagem personagem) {
        if (size >= personagens.length) {
            redimensionarArray();
        }
        personagens[size++] = personagem;
    }
    
    public boolean removerPersonagem(int index) {
        if (index < 0 || index >= size) {
            return false;
        }
        
        for (int i = index; i < size - 1; i++) {
            personagens[i] = personagens[i + 1];
        }
        
        personagens[--size] = null;
        
        if (indexAtual > size) {
            indexAtual = size;
        }
        
        return true;
    }
    
    public boolean removerPersonagem(Personagem personagem) {
        for (int i = 0; i < size; i++) {
            if (personagens[i] == personagem) {
                return removerPersonagem(i);
            }
        }
        return false;
    }
    
    public Personagem obterPersonagem(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index invalido: " + index);
        }
        return personagens[index];
    }

    public void limpar() {
        for (int i = 0; i < size; i++) {
            personagens[i] = null;
        }
        size = 0;
        indexAtual = 0;
    }

    private void redimensionarArray() {
        Personagem[] novoArray = new Personagem[personagens.length * 2];
        System.arraycopy(personagens, 0, novoArray, 0, size);
        personagens = novoArray;
    }

    public void reiniciarIteracao() {
        indexAtual = 0;
    }

    
    @Override
    public boolean hasNext() {
        return indexAtual < size;
    }
    
    @Override
    public Personagem next() {
        if (!hasNext()) {
            throw new RuntimeException("Fim da coleção");
        }
        return personagens[indexAtual++];
    }
    
    @Override
    public Iterator<Personagem> iterableCreate() {
        return new PersonagemIterator(this);
    }
    
}

