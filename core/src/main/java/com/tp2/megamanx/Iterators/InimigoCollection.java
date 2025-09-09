package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.Inimigo;

public class InimigoCollection implements IterableCollection<Inimigo> {
    
    private Inimigo[] inimigos;
    private int size;
    private int indexAtual;
    
    public InimigoCollection() {
        this.inimigos = new Inimigo[10];
        this.size = 0;
        this.indexAtual = 0;
    }
    
    public InimigoCollection(int capacity) {
        this.inimigos = new Inimigo[capacity];
        this.size = 0;
        this.indexAtual = 0;
    }
    
    
    public void adicionarInimigo(Inimigo inimigo) {
        if (size >= inimigos.length) {
            redimensionarArray();
        }
        inimigos[size++] = inimigo;
    }
    
    public boolean removerInimigo(int index) {
        if (index < 0 || index >= size) {
            return false;
        }
        
        for (int i = index; i < size - 1; i++) {
            inimigos[i] = inimigos[i + 1];
        }
        
        inimigos[--size] = null;
        
        if (indexAtual > size) {
            indexAtual = size;
        }
        
        return true;
    }
    
    public boolean removerInimigo(Inimigo inimigo) {
        for (int i = 0; i < size; i++) {
            if (inimigos[i] == inimigo) {
                return removerInimigo(i);
            }
        }
        return false;
    }
    
    public Inimigo obterInimigo(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index invalido: " + index);
        }
        return inimigos[index];
    }
    
    public int tamanho() {
        return size;
    }
    
    public boolean estaVazia() {
        return size == 0;
    }
    
    public void limpar() {
        for (int i = 0; i < size; i++) {
            inimigos[i] = null;
        }
        size = 0;
        indexAtual = 0;
    }
    
    public void reiniciarIteracao() {
        indexAtual = 0;
    }
    
    private void redimensionarArray() {
        Inimigo[] novoArray = new Inimigo[inimigos.length * 2];
        System.arraycopy(inimigos, 0, novoArray, 0, size);
        inimigos = novoArray;
    }
    
    @Override
    public boolean hasNext() {
        return indexAtual < size;
    }
    
    @Override
    public Inimigo next() {
        if (!hasNext()) {
            throw new RuntimeException("fim do array");
        }
        return inimigos[indexAtual++];
    }
    
    @Override
    public Iterator<Inimigo> iterableCreate() {
        return new InimigoIterator(this);
    }
    
}
