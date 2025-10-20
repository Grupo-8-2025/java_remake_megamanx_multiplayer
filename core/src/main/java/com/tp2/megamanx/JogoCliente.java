package com.tp2.megamanx;

import com.esotericsoftware.kryonet.Client;
import com.tp2.Servidor.Network;

public class JogoCliente {
    Client client;
    String nomeHost = "";

    public JogoCliente(String nomeHost) {

        this.nomeHost = nomeHost;
        client = new Client();
        Network.register(client);
        try {
            client.start();
            client.connect(5000, nomeHost, Network.port);
            System.out.println("Conectado ao servidor na porta " + Network.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //new JogoCliente();
    }
}
