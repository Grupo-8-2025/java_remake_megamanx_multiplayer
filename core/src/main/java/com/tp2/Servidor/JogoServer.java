package com.tp2.Servidor;

import com.esotericsoftware.kryonet.Server;

public class JogoServer {

    Server server;

    public JogoServer() {
        server = new Server();
        Network.register(server);
        try {
            server.bind(Network.port);
            server.start();
            System.out.println("Servidor iniciado na porta " + Network.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JogoServer();
    }
}
