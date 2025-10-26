package com.tp2.megamanx.Iterators;

// Importa a classe Personagem para trabalhar com objetos do tipo Personagem
import com.tp2.megamanx.Personagem;

/**
 * Classe PersonagemCollection implementa uma coleção iterável de personagens
 * 
 * Esta classe combina as funcionalidades de:
 * - Uma coleção (armazenamento e gerenciamento de personagens)
 * - Um iterator (capacidade de percorrer os elementos)
 * - Uma factory de iterators (criação de novos iterators independentes)
 * 
 * Implementa o padrão Iterator através da interface IterableCollection<Personagem>
 * Utiliza um array dinâmico interno que se redimensiona automaticamente conforme necessário
 */
public class PersonagemCollection implements IterableCollection<Personagem> {
    
    // Atributos da coleção
    private Personagem[] personagens;  // Array interno que armazena os objetos Personagem
    private int size;                   // Número atual de personagens na coleção
    private int indexAtual;             // Índice atual para iteração (usado pelos métodos hasNext/next)
    
    /**
     * Construtor padrão que cria uma coleção com capacidade inicial de 10 personagens
     * Inicializa todos os contadores em zero
     */
    public PersonagemCollection() {
        this.personagens = new Personagem[10];  // Cria array com capacidade inicial de 10
        this.size = 0;                          // Inicializa contador de elementos
        this.indexAtual = 0;                    // Inicializa índice de iteração
    }
    
    /**
     * Construtor que permite especificar a capacidade inicial da coleção
     * Útil quando se conhece antecipadamente o número aproximado de personagens
     * @param capacity Capacidade inicial do array interno
     */
    public PersonagemCollection(int capacity) {
        this.personagens = new Personagem[capacity];  // Cria array com capacidade personalizada
        this.size = 0;                                // Inicializa contador de elementos
        this.indexAtual = 0;                          // Inicializa índice de iteração
    }
    
    
    /**
     * Adiciona um novo personagem à coleção
     * Se o array estiver cheio, automaticamente redimensiona para comportar mais elementos
     * @param personagem O personagem a ser adicionado à coleção
     */
    public void adicionarPersonagem(Personagem personagem) {
        // Verifica se o array está cheio e precisa ser redimensionado
        if (size >= personagens.length) {
            redimensionarArray();  // Dobra o tamanho do array
        }
        // Adiciona o personagem na próxima posição disponível e incrementa o contador
        personagens[size++] = personagem;
    }
    
    /**
     * Remove um personagem da coleção baseado no seu índice
     * Reorganiza o array para preencher o espaço vazio
     * @param index Índice do personagem a ser removido
     * @return true se a remoção foi bem-sucedida, false se o índice for inválido
     */
    public boolean removerPersonagem(int index) {
        // Valida se o índice está dentro dos limites válidos
        if (index < 0 || index >= size) {
            return false;  // Índice inválido
        }
        
        // Move todos os elementos posteriores uma posição para frente
        // Isso elimina o "buraco" deixado pela remoção
        for (int i = index; i < size - 1; i++) {
            personagens[i] = personagens[i + 1];
        }
        
        // Remove a referência do último elemento e decrementa o tamanho
        personagens[--size] = null;
        
        // Ajusta o índice de iteração se necessário
        // Evita que o iterator aponte para uma posição inválida
        if (indexAtual > size) {
            indexAtual = size;
        }
        
        return true;  // Remoção bem-sucedida
    }
    
    /**
     * Remove um personagem específico da coleção
     * Busca o personagem no array e remove usando o método de remoção por índice
     * @param personagem O objeto personagem a ser removido
     * @return true se o personagem foi encontrado e removido, false caso contrário
     */
    public boolean removerPersonagem(Personagem personagem) {
        // Percorre o array procurando pelo personagem específico
        for (int i = 0; i < size; i++) {
            // Usa comparação de referência (==) para encontrar o objeto exato
            if (personagens[i] == personagem) {
                // Delega a remoção para o método que remove por índice
                return removerPersonagem(i);
            }
        }
        return false;  // Personagem não encontrado
    }
    
    /**
     * Obtém um personagem em uma posição específica da coleção
     * Permite acesso direto a qualquer elemento sem afetar a iteração
     * @param index Índice do personagem desejado
     * @return O personagem na posição especificada
     * @throws IndexOutOfBoundsException se o índice for inválido
     */
    public Personagem obterPersonagem(int index) {
        // Valida os limites do índice antes de acessar o array
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index invalido: " + index);
        }
        return personagens[index];  // Retorna o personagem na posição solicitada
    }
    
    /**
     * Retorna o número atual de personagens na coleção
     * @return Quantidade de personagens armazenados
     */
    public int tamanho() {
        return size;
    }
    
    /**
     * Verifica se a coleção está vazia
     * @return true se não há personagens na coleção, false caso contrário
     */
    public boolean estaVazia() {
        return size == 0;
    }
    
    /**
     * Remove todos os personagens da coleção
     * Limpa todas as referências e reinicia os contadores
     * Útil para reinicializar a coleção sem criar uma nova instância
     */
    public void limpar() {
        // Remove todas as referências para permitir garbage collection
        for (int i = 0; i < size; i++) {
            personagens[i] = null;
        }
        // Reinicia os contadores
        size = 0;
        indexAtual = 0;
    }
    
    /**
     * Reinicia a iteração para o início da coleção
     * Permite reutilizar o mesmo objeto para múltiplas iterações
     */
    public void reiniciarIteracao() {
        indexAtual = 0;  // Volta o índice para o início
    }
    
    /**
     * Método privado que dobra o tamanho do array interno
     * Chamado automaticamente quando o array atual fica cheio
     * Utiliza System.arraycopy para eficiência na cópia dos elementos
     */
    private void redimensionarArray() {
        // Cria um novo array com o dobro da capacidade atual
        Personagem[] novoArray = new Personagem[personagens.length * 2];
        // Copia todos os elementos do array antigo para o novo
        // System.arraycopy é mais eficiente que um loop manual
        System.arraycopy(personagens, 0, novoArray, 0, size);
        // Substitui a referência do array antigo pelo novo
        personagens = novoArray;
    }
    
    /**
     * Implementação do método hasNext() da interface Iterator
     * Verifica se ainda existem personagens para serem iterados
     * @return true se há mais elementos, false caso contrário
     */
    @Override
    public boolean hasNext() {
        // Compara o índice atual com o tamanho da coleção
        return indexAtual < size;
    }
    
    /**
     * Implementação do método next() da interface Iterator
     * Retorna o próximo personagem e avança o índice de iteração
     * @return O próximo personagem na sequência
     * @throws RuntimeException se não há mais elementos para iterar
     */
    @Override
    public Personagem next() {
        // Verifica se ainda há elementos disponíveis
        if (!hasNext()) {
            throw new RuntimeException("fim do array");
        }
        // Retorna o personagem atual e incrementa o índice (pós-incremento)
        return personagens[indexAtual++];
    }
    
    /**
     * Implementação do método iterableCreate() da interface IterableCollection
     * Cria um novo iterator independente para esta coleção
     * Permite múltiplas iterações simultâneas sem interferência
     * @return Nova instância de PersonagemIterator configurada para esta coleção
     */
    @Override
    public Iterator<Personagem> iterableCreate() {
        // Cria um novo PersonagemIterator passando esta coleção como parâmetro
        return new PersonagemIterator(this);
    }
    
}

