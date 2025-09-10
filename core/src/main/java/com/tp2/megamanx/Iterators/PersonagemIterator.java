package com.tp2.megamanx.Iterators;

// Importa a classe Personagem para trabalhar com objetos do tipo Personagem
import com.tp2.megamanx.Personagem;

/**
 * Classe PersonagemIterator implementa o padrão Iterator para percorrer uma coleção de personagens
 * Permite navegar através de uma coleção de objetos Personagem de forma sequencial
 * Implementa a interface Iterator<Personagem> para fornecer métodos padrão de iteração
 * 
 * Esta classe trabalha em conjunto com PersonagemCollection para permitir:
 * - Iteração segura através de personagens
 * - Múltiplos iterators independentes na mesma coleção
 * - Operações de adição e acesso direto aos elementos
 */
public class PersonagemIterator implements Iterator<Personagem> {

    // Atributos da classe
    private PersonagemCollection colecao;  // Referência para a coleção de personagens que será iterada
    private int indexAtual;                // Índice atual da posição do iterator na coleção
        
    /**
     * Construtor que recebe uma coleção de personagens existente
     * Permite criar um iterator para uma coleção já populada
     * @param colecao A coleção de personagens que será iterada
     */
    public PersonagemIterator(PersonagemCollection colecao) {
        this.colecao = colecao;    // Atribui a coleção fornecida
        this.indexAtual = 0;       // Inicializa o índice na primeira posição
    }

    /**
     * Construtor padrão que cria uma nova coleção vazia
     * Útil quando queremos criar um iterator e adicionar personagens posteriormente
     * Inicializa uma nova PersonagemCollection internamente
     */
    public PersonagemIterator(){
        this.colecao = new PersonagemCollection();  // Cria uma nova coleção vazia
        this.indexAtual = 0;                        // Inicializa o índice na primeira posição
    }
        
    /**
     * Verifica se existe um próximo elemento na coleção
     * Implementação obrigatória da interface Iterator
     * @return true se ainda há personagens para iterar, false caso contrário
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
     * @return O próximo objeto Personagem na coleção
     * @throws RuntimeException se não há mais elementos para iterar
     */
    @Override
    public Personagem next() {
        // Verifica se ainda há elementos disponíveis
        if (!hasNext()) {
            // Lança exceção se tentar acessar além do final da coleção
            throw new RuntimeException("fim coleção");
        }
        // Retorna o personagem na posição atual e incrementa o índice (pós-incremento)
        return colecao.obterPersonagem(indexAtual++);
    }
        
    /**
     * Reinicia o iterator para o início da coleção
     * Permite reutilizar o mesmo iterator para percorrer a coleção novamente
     * Útil para múltiplas iterações na mesma coleção
     */
    public void reset() {
        indexAtual = 0;  // Retorna o índice para a primeira posição
    }
    
    /**
     * Retorna o índice atual do iterator
     * Útil para debugging ou para saber a posição atual da iteração
     * @return O índice da posição atual na coleção
     */
    public int getIndexAtual() {
        return indexAtual;
    }

    /**
     * Adiciona um novo personagem à coleção gerenciada por este iterator
     * Se a coleção não existir, cria uma nova automaticamente
     * @param personagem O personagem a ser adicionado à coleção
     */
    public void add(Personagem personagem) {
        // Verifica se a coleção existe, se não existir cria uma nova
        if (colecao == null) {
            colecao = new PersonagemCollection();
        }
        // Adiciona o personagem à coleção usando o método da PersonagemCollection
        colecao.adicionarPersonagem(personagem);
    }

    /**
     * Obtém um personagem em uma posição específica da coleção
     * Permite acesso direto a qualquer elemento sem alterar o índice atual do iterator
     * @param index O índice do personagem desejado
     * @return O personagem na posição especificada
     * @throws IndexOutOfBoundsException se o índice for inválido (delegado pela PersonagemCollection)
     */
    public Personagem get(int index){
        return colecao.obterPersonagem(index);
    }

    /**
     * Retorna a coleção de personagens gerenciada por este iterator
     * Permite acesso direto à coleção subjacente
     * @return A instância da PersonagemCollection
     */
    public PersonagemCollection getColecao() {
        return colecao;
    }

    /**
     * Define uma nova coleção para este iterator
     * Substitui a coleção atual e reinicia o índice para o início
     * @param colecao A nova coleção de personagens a ser gerenciada
     */
    public void setColecao(PersonagemCollection colecao) {
        this.colecao = colecao;
    }

    /**
     * Pula para o próximo elemento sem retorná-lo
     * Incrementa o índice atual se ainda há elementos disponíveis
     * Útil quando queremos avançar o iterator sem processar o elemento atual
     */
    public void skipNext() {
        // Verifica se há próximo elemento antes de incrementar
        if (hasNext()) {
            indexAtual++;  // Avança para o próximo índice sem retornar o elemento
        }
    }
}


