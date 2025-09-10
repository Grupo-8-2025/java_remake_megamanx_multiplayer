package com.tp2.megamanx.Iterators;

/**
 * Interface IterableCollection define um contrato para coleções que podem ser iteradas
 * Implementa o padrão Iterator de forma estendida, permitindo criar múltiplos iterators
 * para a mesma coleção.
 * 
 * Esta interface estende Iterator<T>, o que significa que uma coleção iterável
 * também funciona como um iterator, mas pode criar novos iterators quando necessário.
 * 
 * @param <T> O tipo genérico dos elementos contidos na coleção
 */
public interface IterableCollection<T> extends Iterator<T>{
    
    /**
     * Método que cria e retorna um novo iterator para esta coleção
     * Permite que múltiplos iterators trabalhem independentemente na mesma coleção
     * 
     * Este método é fundamental para o padrão Iterator pois permite:
     * - Criação de múltiplos pontos de iteração simultâneos
     * - Iteração independente sem interferência entre diferentes iterators
     * - Reutilização da coleção com novos iterators após conclusão de iterações anteriores
     * 
     * @return Uma nova instância de Iterator<T> configurada para iterar esta coleção
     */
    public Iterator<T> iterableCreate();
}
