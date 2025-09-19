package com.tp2.megamanx;


/*
 * Gerencia a comunicação de rede usando KryoNet.
 */
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

public class NetworkManager {
    private Server server; // Servidor KryoNet
    private Client client; // Cliente KryoNet
    private boolean isServer; // True se este é o servidor, false se é o cliente
    private Jogo jogo;

    public NetworkManager(Jogo jogo, boolean isServer) {
        this.jogo = jogo;
        this.isServer = isServer;

        if (isServer) { // O jogo abre como Servidor
            startServer();
        } else { // Abre como Cliente
            startClient();
        }
    }

    private void startServer() {
        server = new Server();
        registerClasses(server); // Registra as classes que serão enviadas
        server.start(); // Inicia o servidor
        try {
            server.bind(54555, 54777); // Portas padrão do KryoNet
        } catch (Exception e) {
            Gdx.app.error("Network", "Failed to start server", e); //Mostra o erro no log
            server = null; // Disabilita o servidor se falhar
        }

        if (server != null) { // Se o servidor iniciou corretamente
            server.addListener(new Listener() { // Adiciona um listener para receber mensagens
                public void received(Connection connection, Object object) { // Quando uma mensagem é recebida
                    if (object instanceof PlayerPosition) { // Se receber a posição do jogador
                        PlayerPosition pos = (PlayerPosition) object; // Atualiza a posição do jogador remoto
                        jogo.updateRemotePlayer(pos); // Atualiza a posição do jogador remoto
                    }
                }
            });
        }
    }

    private void startClient() {
        client = new Client();
        registerClasses(client);
        client.start();
        try {
            client.connect(5000, "127.0.0.1", 54555, 54777); // Conecta ao servidor local
        } catch (Exception e) {
            Gdx.app.error("Network", "Failed to connect to server", e); //Mostra o erro no log
            client = null; //Disabilita o cliente se falhar
        }

        if (client != null) {
            client.addListener(new Listener() { // Adiciona um listener para receber mensagens
                public void received(Connection connection, Object object) { // Quando uma mensagem é recebida
                    if (object instanceof PlayerPosition) { // Se receber a posição do jogador
                        PlayerPosition pos = (PlayerPosition) object; // Atualiza a posição do jogador remoto
                        jogo.updateRemotePlayer(pos); // Atualiza a posição do jogador remoto
                    } else if (object instanceof EnemyPosition) { // Se receber a posição dos inimigos
                        EnemyPosition pos = (EnemyPosition) object; // Atualiza a posição dos inimigos
                        jogo.updateEnemies(pos); // Atualiza a posição dos inimigos
                    }
                }
            });
        }
    }

    /* Registra as classes que serão enviadas */
    private void registerClasses(Object network) {
        if (network instanceof Server) { // Registra as classes que serão enviadas se a instância for um servidor
            ((Server) network).getKryo().register(PlayerPosition.class);
            ((Server) network).getKryo().register(EnemyPosition.class);
            ((Server) network).getKryo().register(ArrayList.class);  
        } else if (network instanceof Client) { // Registra as classes que serão enviadas se a instância for um cliente
            ((Client) network).getKryo().register(PlayerPosition.class);
            ((Client) network).getKryo().register(EnemyPosition.class);
            ((Client) network).getKryo().register(ArrayList.class);
        }
    }

    // Envia a posição do jogador para o outro jogador
    public void sendPlayerPosition(float x, float y, int id) { // id = 0 para player1, 1 para player2
        PlayerPosition pos = new PlayerPosition(x, y, id); // Cria o objeto de posição
        if (isServer && server != null) { // Se for o servidor
            server.sendToAllTCP(pos); // Envia para todos os clientes conectados
        } else if (!isServer && client != null) { // se for o cliente
            client.sendTCP(pos); // Envia para o servidor
        }
    }

    public void sendEnemyPositions() {
        if (isServer && server != null) { // Se for o servidor
            EnemyPosition pos = jogo.getEnemyPositions(); // Pega a posição dos inimigos
            server.sendToAllTCP(pos); // Envia para todos os clientes conectados
        }
    }

    // Encerra a conexão de rede
    public void dispose() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }
}