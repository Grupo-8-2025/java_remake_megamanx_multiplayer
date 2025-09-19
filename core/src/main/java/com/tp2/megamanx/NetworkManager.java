package com.tp2.megamanx;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

public class NetworkManager {
    private Server server;
    private Client client;
    private boolean isServer;
    private Jogo jogo;

    public NetworkManager(Jogo jogo, boolean isServer) {
        this.jogo = jogo;
        this.isServer = isServer;

        if (isServer) {
            startServer();
        } else {
            startClient();
        }
    }

    private void startServer() {
        server = new Server();
        registerClasses(server);
        server.start();
        try {
            server.bind(54555, 54777);
        } catch (Exception e) {
            Gdx.app.error("Network", "Failed to start server", e);
            server = null; // Disable server if bind fails
        }

        if (server != null) {
            server.addListener(new Listener() {
                public void received(Connection connection, Object object) {
                    if (object instanceof PlayerPosition) {
                        PlayerPosition pos = (PlayerPosition) object;
                        jogo.updateRemotePlayer(pos);
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
            client.connect(5000, "127.0.0.1", 54555, 54777);
        } catch (Exception e) {
            Gdx.app.error("Network", "Failed to connect to server", e);
            client = null; // Disable client if connect fails
        }

        if (client != null) {
            client.addListener(new Listener() {
                public void received(Connection connection, Object object) {
                    if (object instanceof PlayerPosition) {
                        PlayerPosition pos = (PlayerPosition) object;
                        jogo.updateRemotePlayer(pos);
                    } else if (object instanceof EnemyPosition) {
                        EnemyPosition pos = (EnemyPosition) object;
                        jogo.updateEnemies(pos);
                    }
                }
            });
        }
    }
    
    private void registerClasses(Object network) {
        if (network instanceof Server) {
            ((Server) network).getKryo().register(PlayerPosition.class);
            ((Server) network).getKryo().register(EnemyPosition.class);
            ((Server) network).getKryo().register(ArrayList.class);  // Added registration for ArrayList
        } else if (network instanceof Client) {
            ((Client) network).getKryo().register(PlayerPosition.class);
            ((Client) network).getKryo().register(EnemyPosition.class);
            ((Client) network).getKryo().register(ArrayList.class);  // Added registration for ArrayList
        }
    }

    public void sendPlayerPosition(float x, float y, int id) {
        PlayerPosition pos = new PlayerPosition(x, y, id);
        if (isServer && server != null) {
            server.sendToAllTCP(pos);
        } else if (!isServer && client != null) {
            client.sendTCP(pos);
        }
    }

    public void sendEnemyPositions() {
        if (isServer && server != null) {
            EnemyPosition pos = jogo.getEnemyPositions();
            server.sendToAllTCP(pos);
        }
    }

    public void dispose() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.stop();
        }
    }
}