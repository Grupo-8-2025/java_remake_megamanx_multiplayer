package com.tp2.megamanx.Iterators;

/**
 * Interface Iterator implementa o padrão de design Iterator (Comportamental)
 * 
 * O padrão Iterator fornece uma maneira de acessar sequencialmente os elementos
 * de uma coleção sem expor sua representação interna (lista, array, etc.).
 * 
 * Esta interface define o contrato básico que todos os iterators devem seguir,
 * permitindo navegação uniforme através de diferentes tipos de coleções.
 * 
 * Vantagens do padrão Iterator:
 * - Acesso sequencial sem conhecer a estrutura interna da coleção
 * - Múltiplos iterators podem trabalhar na mesma coleção simultaneamente
 * - Suporte a diferentes algoritmos de travessia na mesma estrutura
 * - Separação de responsabilidades entre coleção e navegação
 * 
 * @param <T> O tipo genérico dos elementos que serão iterados
 */
public interface Iterator<T> {
    
    /**
     * Verifica se existem mais elementos na coleção para serem iterados
     * 
     * Este método é fundamental para controle de fluxo em loops de iteração,
     * permitindo verificar se é seguro chamar next() sem causar exceções.
     * 
     * Tipicamente usado em estruturas como:
     * while(iterator.hasNext()) {
     *     T elemento = iterator.next();
     *     // processar elemento
     * }
     * 
     * @return true se ainda há elementos para serem retornados, false caso contrário
     */
    public boolean hasNext();
    
    /**
     * Retorna o próximo elemento da coleção e avança a posição do iterator
     * 
     * Este método realiza duas operações importantes:
     * 1. Retorna o elemento atual da iteração
     * 2. Avança o iterator para a próxima posição
     * 
     * Deve ser chamado apenas após verificar hasNext() para evitar exceções
     * ao tentar acessar elementos além do final da coleção.
     * 
     * @return O próximo elemento da coleção do tipo T
     * @throws RuntimeException (ou subclasse) se não houver mais elementos disponíveis
     */
    public T next();
}
