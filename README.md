# Remake MegaManX

Jogo desenvolvido em Java inspirado no primeiro título da clássica franquia Mega Man X, com o objetivo de reproduzir suas principais mecânicas de jogabilidade e expandi-las com um modo multiplayer. Este projeto foi desenvolvido como trabalho prático da disciplina Linguagem e Técnicas de Programação 2 no curso técnico de informática do CEFET-MG, utilizando a biblioteca LibGDX para recursos gráficos e multimídia e Java RMI para comunicação em rede.

---

## Funcionalidades
- Movimentação do personagem em plataformas
- Sistema de pulo e dash
- Sistema de tiros e troca de tipos de disparo
- Inimigos e chefão por fase
- Sistema de colisão
- Execução do jogo em modo janela
- Segunda fase adicionada
- Modo singleplayer
- Modo multiplayer

## Tecnologias usadas
- Java (JDK 8 ou superior)
- Java RMI
- Gradle
- LibGDX
- VS Code

---

## Como executar

### Pelo Terminal
1. Baixe e extraia o arquivo zip do projeto
2. Copie a pasta principal do projeto para o diretório desejado
3. Abra o terminal na raiz onde está o projeto
4. Compile e execute utilizando o comando:
```bash
gradlew lwjgl3:run
```

### Pela sua IDE
1. Baixe e extraia o arquivo zip do projeto
2. Copie a pasta principal do projeto para o diretório desejado
3. Abra o projeto na sua IDE de preferência
4. Navegue até o arquivo:
```
lwjgl3/src/main/java/com/tp2/megamanx/lwjgl3/Lwjgl3Launcher.java
```
5. Clique com o botão direito no arquivo e selecione Run ou Executar

---

## Como Jogar

### Objetivo
Derrotar o chefão de cada fase

### Início do jogo
- **Jogador 1**: clicar no botão Modo Servidor
- **Jogador 2**: clicar no botão Modo Cliente
- **Jogador único**: clicar no botão Modo Singleplayer

### Controles
- **⬆️ Cima**: Subir plataformas
- **⬅️ Esquerda**: Mover-se para a esquerda
- **➡️ Direita**: Mover-se para a direita
- **⏺ Espaço**: Pular
- **Tecla Shift**: Ativar o dash
- **Tecla X**: Atirar
- **Tecla C**: Trocar de tiro

### Dicas
- Execute o jogo somente em modo janela
- Utilize a resolução recomendada de 800x500 pixels
- Não utilize o modo tela cheia, pois pode ocorrer erro de escala gráfica

---

## Observação importante
Este projeto utilizou inteligências artificiais generativas como apoio no desenvolvimento, sendo empregadas para auxiliar na elaboração dos códigos, correção de erros de lógica e de bugs, bem como na documentação do projeto.
