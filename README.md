# Remake MegaManX Multiplayer

Jogo desenvolvido em Java, inspirado no primeiro título da clássica franquia Mega Man X, com foco na recriação de suas principais mecânicas de jogabilidade, como movimentação, combate e progressão de fases. Além disso, o projeto propõe uma expansão da experiência original por meio da implementação de um modo multiplayer.

A aplicação foi desenvolvida como trabalho prático da disciplina Linguagem e Técnicas de Programação II, no curso técnico de informática do CEFET-MG. Para o desenvolvimento, foram utilizadas a biblioteca LibGDX, responsável pelos recursos gráficos e multimídia, e Java RMI, empregado na comunicação em rede entre os jogadores.

## Objetivos
- Praticar desenvolvimento backend em Java
- Aplicar conceitos de Programação Orientada a Objetos no desenvolvimento de jogos
- Desenvolver um jogo do tipo scrolling (plataforma)
- Recriar as principais mecânicas, regras e elementos visuais do jogo original
- Estudar e implementar o padrão de projeto Iterator
- Utilizar Java RMI para comunicação em rede entre jogadores
- Implementar a sincronização de estados do jogo em modo multiplayer

## Funcionalidades
- Movimentação do personagem em plataformas
- Sistema de pulo e dash
- Sistema de tiros e troca de tipos de disparo
- Inimigos e chefão por fase
- Sistema de colisão
- Segunda fase adicionada
- Modo singleplayer
- Modo multiplayer

## Tecnologias Usadas
- Java
- Java RMI
- Gradle
- LibGDX
- Padrão de projeto Iterator
- VS Code

## Como Executar

### Requisitos
- JDK 8 ou superior
- Gradle (opcional, caso não utilize o wrapper incluído no projeto)

### Execução Via terminal
1. Baixe e extraia o arquivo `.zip` do projeto
2. Navegue até o diretório raiz do projeto pelo terminal
3. Execute o comando abaixo:
```
gradlew lwjgl3:run
```

### Execução Via IDE
1. Baixe e extraia o arquivo `.zip` do projeto
2. Abra o projeto em sua IDE de preferência 
3. Aguarde o carregamento e a sincronização das dependências do Gradle
3. Navegue até o seguinte arquivo: `lwjgl3/src/main/java/com/tp2/megamanx/lwjgl3/Lwjgl3Launcher.java`
Execute a classe `Lwjgl3Launcher`

## Como Jogar

### Objetivo
Derrotar o chefão de cada fase.

### Início do Jogo
- **Jogador 1**: Clicar no botão Modo Servidor
- **Jogador 2**: Clicar no botão Modo Cliente
- **Jogador único**: Clicar no botão Modo Singleplayer

### Controles
- **⬆️ Cima**: Subir plataformas
- **⬅️ Esquerda**: Mover-se para a esquerda
- **➡️ Direita**: Mover-se para a direita
- **⏺ Espaço**: Pular
- **Tecla Shift**: Ativar o dash
- **Tecla X**: Atirar
- **Tecla C**: Trocar de tiro

### Dica
Execute o jogo em modo janela, na resolução inicial, para evitar problemas de escala gráfica.

## Telas do Jogo
Capturas de tela mostrando a interface e a jogabilidade do projeto.

### Telas Iniciais
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/af497f8f-bf0d-4e96-8168-7bd73cce434d" />
<br>
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/f8b0b93c-b94a-4ce4-9ed8-a49706baa14b" />

### Telas da Fase 1
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/3e4a7f33-eeb1-49eb-b781-738751cb937a" />
<br>
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/ee75af4b-b847-4f14-9793-540a1e03007d" />

### Telas da Fase 2
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/ce6d6f10-e9a5-45e3-9429-ec77bdf621f5" />
<br>
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/454cc06f-5d7c-4d70-b1eb-8f62028c9cb1" />

### Telas Finais
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/0ca6f220-2ca9-409b-83f3-2a1f7e949ecc" />
<br>
<img width="500" height="331" alt="image" src="https://github.com/user-attachments/assets/dea4abb3-e47b-48e5-982a-8f136dd77429" />

---

## Observação Importante
Este projeto contou com o apoio de inteligências artificiais generativas durante o desenvolvimento, sendo utilizadas para auxiliar na implementação de código, correção de erros de lógica e aprimoramento da documentação.
