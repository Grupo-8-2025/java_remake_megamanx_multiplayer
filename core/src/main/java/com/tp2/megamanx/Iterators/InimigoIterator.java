package com.tp2.megamanx.Iterators;

import com.tp2.megamanx.inimigos.Inimigo;

/**
 * Classe InimigoIterator implementa o padrão Iterator para percorrer uma coleção de inimigos
 * Permite navegar através de uma coleção de objetos Inimigo de forma sequencial
 * Implementa a interface Iterator<Inimigo> para fornecer métodos padrão de iteração
 */
public class InimigoIterator implements Iterator<Inimigo>, java.io.Serializable {

    // Atributos da classe
    private InimigoCollection colecao;  // Referência para a coleção de inimigos que será iterada
    private int indexAtual;             // Índice atual da posição do iterator na coleção
        
    /**
     * Construtor que recebe uma coleção de inimigos existente
     * @param colecao A coleção de inimigos que será iterada
     */
    public InimigoIterator(InimigoCollection colecao) {
        this.colecao = colecao;    // Atribui a coleção fornecida
        this.indexAtual = 0;       // Inicializa o índice na primeira posição
    }

    /**
     * Construtor padrão que cria uma nova coleção vazia
     * Útil quando queremos criar um iterator e adicionar inimigos posteriormente
     */
    public InimigoIterator(){
        this.colecao = new InimigoCollection();  // Cria uma nova coleção vazia
        this.indexAtual = 0;                     // Inicializa o índice na primeira posição
    }
        
    /**
     * Verifica se existe um próximo elemento na coleção
     * Implementação obrigatória da interface Iterator
     * @return true se ainda há elementos para iterar, false caso contrário
     */
    @Override
    public boolean hasNext() {
        // Compara o índice atual com o tamanho da coleção
        // Retorna true se o índice ainda não chegou ao final da coleção
        return indexAtual < colecao.tamanho();
    }
        
    /**
     * Retorna o próximo elemento da coleção e avança o índice
     * Implementação obrigatória da interface Iterator
     * @return O próximo objeto Inimigo na coleção
     * @throws RuntimeException se não há mais elementos para iterar
     */
    @Override
    public Inimigo next() {
        // Verifica se ainda há elementos disponíveis
        if (!hasNext()) {
            // Lança exceção se tentar acessar além do final da coleção
            throw new RuntimeException("fim coleção");
        }
        // Retorna o inimigo na posição atual e incrementa o índice (pós-incremento)
        return colecao.obterInimigo(indexAtual++);
    }
        
    /**
     * Reinicia o iterator para o início da coleção
     * Útil para percorrer a mesma coleção múltiplas vezes
     */
    public void reset() {
        indexAtual = 0;  // Retorna o índice para a primeira posição
    }
    
    /**
     * Retorna o índice atual do iterator
     * @return O índice da posição atual na coleção
     */
    public int getIndexAtual() {
        return indexAtual;
    }

    /**
     * Adiciona um novo inimigo à coleção
     * Se a coleção não existir, cria uma nova
     * @param inimigo O inimigo a ser adicionado à coleção
     */
    public void add(Inimigo inimigo) {
        // Verifica se a coleção existe, se não existir cria uma nova
        if (colecao == null) {
            colecao = new InimigoCollection();
        }
        // Adiciona o inimigo à coleção
        colecao.adicionarInimigo(inimigo);
    }

    /**
     * Obtém um inimigo em uma posição específica da coleção
     * Permite acesso direto a qualquer elemento sem alterar o índice atual do iterator
     * @param index O índice do inimigo desejado
     * @return O inimigo na posição especificada
     */
    public Inimigo get(int index){
        return colecao.obterInimigo(index);
    }

    /**
     * Retorna a coleção de inimigos gerenciada por este iterator
     * @return A instância da InimigoCollection
     */
    public InimigoCollection getColecao() {
        return colecao;
    }

    /**
     * Define uma nova coleção para este iterator
     * @param colecao A nova coleção de inimigos a ser gerenciada
     */
    public void setColecao(InimigoCollection colecao) {
        this.colecao = colecao;
    }

    public void clear(){
        colecao.limpar();
    }

    /**
     * Pula para o próximo elemento sem retorná-lo
     * Incrementa o índice atual se ainda há elementos disponíveis
     * Útil quando queremos avançar o iterator sem processar o elemento atual
     */
    public void skipNext() {
        // Verifica se há próximo elemento antes de incrementar
        if (hasNext()) {
            indexAtual++;  // Avança para o próximo índice
        }
    }
}

