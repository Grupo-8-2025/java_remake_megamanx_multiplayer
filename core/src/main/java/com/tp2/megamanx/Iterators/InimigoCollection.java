package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.Inimigo;

// Classe que representa uma coleção de inimigos, permitindo adicionar, remover e iterar sobre eles
public class InimigoCollection implements IterableCollection<Inimigo> {
    
    private Inimigo[] inimigos;
    private int size;
    private int indexAtual;
    
    // Construtor padrão com capacidade inicial de 10
    public InimigoCollection() {
        this.inimigos = new Inimigo[10];
        this.size = 0;
        this.indexAtual = 0;
    }
    
    // Construtor com capacidade especificada
    public InimigoCollection(int capacity) {
        this.inimigos = new Inimigo[capacity];
        this.size = 0;
        this.indexAtual = 0;
    }
    
    
    // Adiciona um inimigo à coleção, redimensionando o array se necessário
    public void adicionarInimigo(Inimigo inimigo) {
        if (size >= inimigos.length) {
            redimensionarArray();
        }
        inimigos[size++] = inimigo;
    }
    
    // Remove o inimigo no índice especificado, deslocando os elementos subsequentes
    public boolean removerInimigo(int index) {
        if (index < 0 || index >= size) {
            return false;
        }
        
        // Desloca os elementos para a esquerda para preencher o espaço vazio
        for (int i = index; i < size - 1; i++) {
            inimigos[i] = inimigos[i + 1];
        }
        
        inimigos[--size] = null;
        
        if (indexAtual > size) {
            indexAtual = size;
        }
        
        return true;
    }
    
    // Remove o inimigo específico da coleção
    public boolean removerInimigo(Inimigo inimigo) {
        for (int i = 0; i < size; i++) {
            if (inimigos[i] == inimigo) {
                return removerInimigo(i);
            }
        }
        return false;
    }
    
    // Retorna o inimigo no índice especificado, lançando exceção se inválido
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
    
    // Remove todos os inimigos da coleção e redefine o estado
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
    
    // Dobra o tamanho do array interno quando necessário para acomodar mais inimigos
    private void redimensionarArray() {
        Inimigo[] novoArray = new Inimigo[inimigos.length * 2];
        System.arraycopy(inimigos, 0, novoArray, 0, size);
        inimigos = novoArray;
    }
    
    // Verifica se há mais inimigos para iterar
    @Override
    public boolean hasNext() {
        return indexAtual < size;
    }
    
    // Retorna o próximo inimigo na iteração e avança o índice
    @Override
    public Inimigo next() {
        if (!hasNext()) {
            throw new RuntimeException("fim do array");
        }
        return inimigos[indexAtual++];
    }
    
    // Cria um iterador para percorrer a coleção de inimigos
    @Override
    public Iterator<Inimigo> iterableCreate() {
        return new InimigoIterator(this);
    }
    
}
