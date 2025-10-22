package com.tp2.Servidor;

import java.lang.ProcessHandle.Info;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.tp2.megamanx.Jogo;
import com.tp2.megamanx.MegaMan;
import com.tp2.megamanx.Iterators.InimigoIterator;

public class Network {
    static public final int port = 54555;
	private Server server;
	//private JogoServer jogo;
	private JogoServerTest jogoTest;

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
	}

	public Network(JogoServerTest jogoTest) {
		//this.jogo = jogo;
		this.jogoTest = jogoTest;
		startServer();
	}

	private void startServer() {
		try {
			server = new Server();
			registerClasses(server);
			//register(server);
			server.start();
			server.bind(port);
			System.out.println("Network: Servidor iniciado na porta " + port);
			server.addListener(new Listener() {
				public void received (Connection connection, Object object) {
					if (object instanceof MegaMan) {
						MegaMan megaMan = (MegaMan) object;
						//jogo.getMegaMan().mover(teclasPressionadas);
						jogoTest.setMegaMan(megaMan);
					}
				}
			});
		} catch (Exception e) {
			System.out.println("Network: Erro ao iniciar o servidor na porta " + port);
			e.printStackTrace();
			if (server != null) {
				server.stop();
				server = null;
			}
		}
	}

	private void registerClasses(Object network) {
        ((Server) network).getKryo().register(InformacoesServidor.class);
        ((Server) network).getKryo().register(MegaMan.class);
        ((Server) network).getKryo().register(ArrayList.class);  
    }


	public void sendInformacoesServidor(){
		//InformacoesServidor info = jogo.getInfoServidor();
		InformacoesServidor info = jogoTest.getInfoServidor();
		server.sendToAllTCP(info);
	}

	public void sendGameOver(boolean isGameOver){
		server.sendToAllTCP(isGameOver);
	}

	public void enviarSegundoJogadorConectado(MegaMan megaMan){
		server.sendToAllTCP(megaMan);
	}


	public void dispose() {
		if (server != null) {
			server.stop();
			server = null;
			System.out.println("Network: Servidor parado.");
		}
	}

}
