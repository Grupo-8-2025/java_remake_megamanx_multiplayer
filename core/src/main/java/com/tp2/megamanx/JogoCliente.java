package com.tp2.megamanx;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.tp2.Servidor.Network;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Connection;

public class JogoCliente {
    Client client;
    String nomeHost = "";
    MegaMan megaManLocal;

    public JogoCliente(String nomeHost) {

        this.nomeHost = nomeHost;
        client = new Client();
        registerClasses(client); // Registra as classes que serão enviadas
        try {
            client.start();
            client.connect(5000, nomeHost, 54555, 54777);
            System.out.println("Conectado ao servidor na porta " + 54555);
            client.addListener(new Listener() { 
                public void received(Connection connection, Object object) {
                    Gdx.app.postRunnable(() -> {
                        if (object instanceof MegaMan) {
                            megaManLocal = (MegaMan) object;  
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerClasses(Object network) {
         // Registra as classes que serão enviadas se a instância for um cliente
        ((Client) network).getKryo().register(MegaMan.class);
        //((Client) network).getKryo().register(int.class);
        //((Client) network).getKryo().register(PosicaoTiro.class);
        //((Client) network).getKryo().register(ArrayList.class);
    
    }

    public void enviarMegaMan(MegaMan megaMan) {
        if (client != null) {
            client.sendTCP(megaMan);
        }
    }

    public void dispose() {
        if (client != null) {
            client.stop();
        }
    }
    /* 
    public static void main(String[] args) {
        //new JogoCliente();
    }
    */
}
