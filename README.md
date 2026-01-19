# 🎮 Remake MegaManX Multiplayer 🎮

Bem-vindo ao repositório **Remake MegaManX Multiplayer**! Este projeto foi desenvolvido como trabalho prático da matéria Linguagem e Técnicas de Programação 2 no curso técnico de informática do CEFET-MG.
O jogo desenvolvido é baseado no primeiro jogo da clássica série de jogos **Mega Man X**. Ele representa uma evolução do remake feito em [Remake MegaManX](https://github.com/Grupo-8-2025/remake_megamanx), com melhorias no código, adição de segunda fase e modo multiplayer, utilizando **Java RMI**. 

---

## 🚀 Tecnologias Utilizadas

- ☕ **Java** JDK 8 ou superior
- 🔁 **Java RMI** (importado e utilizado no próprio código)
- 📦 **Gradle** (ou utilize o Gradle Wrapper incluso no projeto)
- 🎮 **LibGDX** (já configurado, não é necessário instalar manualmente)
- 🧠 **IDE recomendada:** IntelliJ IDEA, Visual Studio Code ou qualquer IDE que suporte Java e Gradle

---

## 🏗️ Como Compilar

✔️ Passo 1: Abrir o terminal na raiz do projeto

✔️ Passo 2: Compilar usando o Gradle Wrapper

## 🎯 Como Rodar o Jogo

### ✅ Pelo Terminal

**No Windows (Prompt de Comando ou PowerShell depois de acessar a pasta root do projeto):**

```bash
gradlew lwjgl3:run
```

### ✅ Pela sua IDE

1. Abra o projeto na sua IDE de preferência (**IntelliJ IDEA**, **VS Code**, **Eclipse**, etc.)
2. Navegue até o arquivo:

```
lwjgl3/src/main/java/com/tp2/megamanx/lwjgl3/Lwjgl3Launcher.java
```

3. Clique com o botão direito no arquivo e selecione:
   - **"Run"** (IntelliJ IDEA, VS Code) ou **"Executar"** (dependendo do idioma da sua IDE)

### ⚠️ Importante

- Execute o jogo **apenas em modo janela**, na resolução recomendada de **800x500 pixels**. **Não utilize o modo tela cheia**, pois pode gerar erros de escala

---

## 🕹️Como Jogar?🕹️

### 🎯 Objetivo
Derrotar o chefão de cada fase

### 🔰 Inicío do Jogo
- **Jogador 1**: clicar no botão **Modo Servidor**
- **Jogador 2**: clicar no botão **Modo Cliente**
- **Jogador Único**: clicar no botão **Modo Singleplayer**

### 🎮 Controles
- **⬆️ Cima**: Subir plataformas
- **⬅️ Esquerda**: Mover-se para a esquerda
- **➡️ Direita**: Mover-se para a direita
- **⏺ Espaço**: Pular
- **Shift**: Ativar o dash
- **X**: Atirar
- **C**: Trocar de tiro
