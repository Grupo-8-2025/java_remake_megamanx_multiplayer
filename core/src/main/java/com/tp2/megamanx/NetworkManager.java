package com.tp2.megamanx;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.tp2.megamanx.Iterators.InimigoIterator;
import com.tp2.megamanx.UtilitariosConexao.EnemyPosition;
import com.tp2.megamanx.UtilitariosConexao.GerenciadorFases;
import com.tp2.megamanx.UtilitariosConexao.PlayerPosition;
import com.tp2.megamanx.UtilitariosConexao.PosicaoTiro;
import com.tp2.megamanx.UtilitariosConexao.VerificaGanhar;
import com.tp2.megamanx.Inimigos.Pinguim;
import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.StringWriter;
import java.io.PrintWriter;

public class NetworkManager {

    private Server server; 
    private Client client; 
    private boolean isServer; 
    private Jogo jogo;

    public NetworkManager(Jogo jogo, boolean isServer) {
        this.jogo = jogo;
        this.isServer = isServer;

        if (this.isServer) { 
            startServer();
        } else { 
            startClient();
        }
    }

    private void startServer() {
        boolean bound = false;
        int basePort = 54555;

        for (int i = 0; i < 10 && !bound; i++) {
            int tcpPort = basePort + i;
            int udpPort = tcpPort + 222; 
            try {
                server = new Server();   
                registerClasses(server); 
                server.start();         
                server.bind(tcpPort, udpPort);
                bound = true;
                Gdx.app.log("Network", "Server bound to ports TCP: " + tcpPort + ", UDP: " + udpPort);
            } catch (Exception e) {
                Gdx.app.log("Network", "Failed to bind to ports TCP: " + tcpPort + ", UDP: " + udpPort + ", trying next...");
                if (server != null) {
                    server.stop();
                    server = null;
                }
            }
        }
        
        if (!bound) {
            Gdx.app.error("Network", "Failed to start server on any port");
            server = null;
        }

        if (server != null) { 
            server.addListener(new Listener() { 
                @Override
                public void connected(Connection connection) {
                    try {
                        int clients = server.getConnections().length; 
                        Gdx.app.log("Network", "Client connected. total clients=" + clients);
                        if (clients > 1) {
                            ConnectionRejected rej = new ConnectionRejected("Nao foi possivel conectar, a sala ja tem 2 jogadores!");

                            try {
                                connection.sendTCP(rej);
                            } catch (Throwable t) {
                                Gdx.app.error("Network", "Failed to send ConnectionRejected: " + t.getMessage());
                            }
                            connection.close();
                            Gdx.app.log("Network", "Rejected connection because room is full");
                        }
                    } catch (Throwable t) {
                        Gdx.app.error("Network", "Error in connected handler: " + t.getMessage());
                    }
                }

                @Override
                public void disconnected(Connection connection) {
                    Gdx.app.log("Network", "Client disconnected: " + connection.getRemoteAddressTCP());
                }

                @Override
                public void received(Connection connection, Object object) { 
                    if (object instanceof PlayerPosition) {
                        PlayerPosition pos = (PlayerPosition) object; 
                        jogo.updateRemotePlayer(pos); 
                    } else if (object instanceof EnemyHit) {
                        EnemyHit hit = (EnemyHit) object;
                        jogo.applyEnemyHit(hit.id, hit.damage);
                    
                    }
                    else if (object instanceof PosicaoTiro) {
                        PosicaoTiro posicaoTiro = (PosicaoTiro) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.desenhaTirosRemotos(posicaoTiro);
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    else if (object instanceof GerenciadorFases) {
                        GerenciadorFases gerenciador = (GerenciadorFases) object;
                        try{
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.gerenciadorFases.setFaseAtual(gerenciador.getFaseAtual());
                                        jogo.iniciarSegundaFase(true);
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        }catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    else if (object instanceof VerificaGanhar) {
                        VerificaGanhar verificaGanhar = (VerificaGanhar) object;
                        try{
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.setScreen(new TelaVitoria(jogo));
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        }catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void startClient() {
        client = new Client(1048576, 1048576); // Aumentar buffers para 1MB para evitar overflow
        registerClasses(client);
        client.start();
        boolean connected = false;
        int basePort = 54555;
        for (int i = 0; i < 10 && !connected; i++) {
            int tcpPort = basePort + i;
            int udpPort = tcpPort + 222;
            try {
                client.connect(5000, "127.0.0.1", tcpPort, udpPort);
                connected = true;
                Gdx.app.log("Network", "Client connected to ports TCP: " + tcpPort + ", UDP: " + udpPort);
            } catch (Exception e) {
                Gdx.app.log("Network", "Failed to connect to ports TCP: " + tcpPort + ", UDP: " + udpPort + ", trying next...");
            }
        }
        if (!connected) {
            Gdx.app.error("Network", "Failed to connect to server on any port");
            client = null; 
        }

        if (client != null) {
            client.addListener(new Listener() { 
                public void received(Connection connection, Object object) { 
                    
                    if (object instanceof PlayerPosition) { 
                        PlayerPosition pos = (PlayerPosition) object;
                        jogo.updateRemotePlayer(pos); 
                    } 
                    
                    else if (object instanceof EnemyPosition) { 
                        EnemyPosition pos = (EnemyPosition) object; 
                        jogo.updateEnemies(pos); 
                    } 
                    
                    else if (object instanceof PinguinState) { 
                        PinguinState st = (PinguinState) object;
                        jogo.updatePinguinState(st);
                    } 
                    
                    else if (object instanceof SparkState) { 
                        SparkState st = (SparkState) object;
                        jogo.updateSparkState(st);
                    } 
                    
                    else if (object instanceof VileState) { 
                        VileState st = (VileState) object;
                        jogo.updateVileState(st);
                    } 
                    
                    else if (object instanceof ConnectionRejected) {
                        final ConnectionRejected cr = (ConnectionRejected) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.setScreen(new TelaSalaCheia(jogo, cr.message));
                                    } catch (Throwable t) {
                                        Gdx.app.error("Network", "Failed to show rejection screen on main thread: " + t.getMessage());
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to post rejection screen runnable: " + t.getMessage());
                        }
                        try { client.close(); } catch (Throwable ignored) {}
                        client = null;
                    }

                    else if (object instanceof PosicaoTiro) {
                        PosicaoTiro posicaoTiro = (PosicaoTiro) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.desenhaTirosRemotos(posicaoTiro);
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }
                    
                    else if (object instanceof GerenciadorFases) {
                        GerenciadorFases gerenciador = (GerenciadorFases) object;
                        try{
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.gerenciadorFases.setFaseAtual(gerenciador.getFaseAtual());
                                        jogo.iniciarSegundaFase(true);
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        }catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }

                    else if (object instanceof VerificaGanhar) {
                        VerificaGanhar verificaGanhar = (VerificaGanhar) object;
                        try{
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.setScreen(new TelaVitoria(jogo));
                                    } catch (Throwable t) {
                                        t.printStackTrace();
                                    }
                                }
                            });
                        }catch (Throwable t) {
                            t.printStackTrace();
                        }
                    }

                }
            });
        }
    }


    private static class NullSerializer<T> extends Serializer<T> {
        @Override
        public void write(com.esotericsoftware.kryo.Kryo kryo, Output output, T object) {}
        @Override
        public T read(com.esotericsoftware.kryo.Kryo kryo, Input input, Class<T> type) {
            return null;
        }
    }

    private void registerClasses(Object network) {
        com.esotericsoftware.kryo.Kryo kryo;
        if (network instanceof Server) {
            kryo = ((Server) network).getKryo();
        } else {
            kryo = ((Client) network).getKryo();
        }

        try { kryo.setRegistrationRequired(false); } catch (Throwable ignored) {}

        kryo.register(PlayerPosition.class);
        kryo.register(EnemyPosition.class);
        kryo.register(PosicaoTiro.class); 
        kryo.register(PinguinState.class);
        kryo.register(VileState.class);
        kryo.register(SparkState.class);
        kryo.register(EnemyHit.class);
        kryo.register(ConnectionRejected.class);
        kryo.register(GerenciadorFases.class);
        kryo.register(VerificaGanhar.class);
        kryo.register(Ataque.class);

        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.Float.class);
        kryo.register(java.lang.Integer.class);
        kryo.register(float[].class);
        kryo.register(int[].class);
    }

    private void configureKryo(com.esotericsoftware.kryo.Kryo kryo) {
        try {
            Class<?> stdClass = Class.forName("org.objenesis.strategy.StdInstantiatorStrategy");
            Object stdInstance = stdClass.getDeclaredConstructor().newInstance();

            Class<?> defaultStratClass = Class.forName("com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy");
            java.lang.reflect.Constructor<?> ctor = defaultStratClass.getConstructor(stdClass.getInterfaces().length > 0 ? stdClass.getInterfaces()[0] : stdClass);
            Object defaultStrategyInstance;

            try {
                defaultStrategyInstance = ctor.newInstance(stdInstance);
            } catch (Exception nsme) {
                java.lang.reflect.Constructor<?>[] ctors = defaultStratClass.getConstructors();
                if (ctors.length > 0) {
                    defaultStrategyInstance = ctors[0].newInstance(stdInstance);
                } else {
                    throw new RuntimeException("No suitable constructor found for DefaultInstantiatorStrategy");
                }
            }

            java.lang.reflect.Method setMethod = null;
            for (java.lang.reflect.Method m : kryo.getClass().getMethods()) {
                if ("setInstantiatorStrategy".equals(m.getName()) && m.getParameterCount() == 1) {
                    setMethod = m;
                    break;
                }
            }
            if (setMethod != null) {
                setMethod.invoke(kryo, defaultStrategyInstance);
                Gdx.app.log("Network", "Kryo instantiator strategy set via reflection to DefaultInstantiatorStrategy (Objenesis)");
            } else {
                Gdx.app.log("Network", "Kryo.setInstantiatorStrategy method not found via reflection");
            }

        } catch (ClassNotFoundException cnf) {
            Gdx.app.log("Network", "Objenesis / Kryo util classes not found on classpath; Kryo may require no-arg constructors for some classes.");
        } catch (Throwable t) {
            Gdx.app.error("Network", "Failed to set Kryo instantiator strategy via reflection: " + t.getMessage());
            Gdx.app.error("Network", exceptionToString(t));
        }

        try {
            kryo.setRegistrationRequired(false);
        } catch (Throwable ignored) {}
    }

    private void registerGdxGraphicClasses(com.esotericsoftware.kryo.Kryo kryo) {
        String[] classes = new String[] {
            "com.badlogic.gdx.graphics.Color",
            "com.badlogic.gdx.graphics.GL20",
            "com.badlogic.gdx.graphics.Pixmap",
            "com.badlogic.gdx.graphics.Pixmap$Format",
            "com.badlogic.gdx.graphics.Texture",
            "com.badlogic.gdx.graphics.Texture$TextureFilter",
            "com.badlogic.gdx.graphics.Texture$TextureWrap",
            "com.badlogic.gdx.graphics.TextureData",
            "com.badlogic.gdx.graphics.glutils.FileTextureData",
            "com.badlogic.gdx.graphics.glutils.ShapeRenderer",
            "com.badlogic.gdx.graphics.glutils.ShaderProgram",
            "com.badlogic.gdx.graphics.Mesh",
            "com.badlogic.gdx.graphics.VertexAttribute",
            "com.badlogic.gdx.graphics.VertexAttributes",
            "com.badlogic.gdx.graphics.FrameBuffer",
            "com.badlogic.gdx.graphics.FPSLogger",
            "com.badlogic.gdx.graphics.Pixmap$Blending",
            "com.badlogic.gdx.graphics.Pixmap$Filter",
            "com.badlogic.gdx.graphics.g2d.TextureAtlas", 
            "com.badlogic.gdx.Files$FileType"
        };

        for (String name : classes) {
            tryRegisterIfPresent(kryo, name);
        }

        String[] g2d = new String[] {
            "com.badlogic.gdx.graphics.g2d.TextureRegion",
            "com.badlogic.gdx.graphics.g2d.SpriteBatch",
            "com.badlogic.gdx.graphics.g2d.BitmapFont",
            "com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator",
            "com.badlogic.gdx.graphics.g2d.TextureAtlas$TextureAtlasData"
        };

        for (String name : g2d) {
            tryRegisterIfPresent(kryo, name);
        }
    }

    private void tryRegisterIfPresent(com.esotericsoftware.kryo.Kryo kryo, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            kryo.register(clazz);
            Gdx.app.log("Network", "Kryo registered class: " + className);
        } catch (ClassNotFoundException e) {
            Gdx.app.log("Network", "Class not present, skipping Kryo register: " + className);
        } catch (Throwable t) {
            Gdx.app.error("Network", "Failed to register class via reflection: " + className + " -> " + t.getMessage());
            Gdx.app.error("Network", exceptionToString(t));
        }
    }

    private String exceptionToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public void sendPlayerPosition(PlayerPosition pos) {
        if (pos == null) return;
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
    
    public void sendInimigos(InimigoIterator inimigos){
        if (isServer && server != null) {
            EnemyPosition pos = jogo.getEnemyPositions();
            server.sendToAllTCP(pos);
        }
    }

    public void sendTiroPositions(float x, float y, TipoAtaque tipo, boolean paraDireita) {
        PosicaoTiro pos = new PosicaoTiro(x, y, tipo, paraDireita);
        if (isServer && server != null) {
            server.sendToAllTCP(pos);
        } else if (!isServer && client != null) {
            client.sendTCP(pos);
        }
    }

    public void sendGerenciadorFases(GerenciadorFases gerenciadorFases) {
        if (gerenciadorFases == null) return;
        if (isServer && server != null) {
            server.sendToAllTCP(gerenciadorFases);
        } else if (!isServer && client != null) {
            client.sendTCP(gerenciadorFases);
        }
    }

    public void sendGanhouJogo(VerificaGanhar verifica) {
        if(verifica == null) return;
        if (isServer && server != null) {
            server.sendToAllTCP(verifica);
        } else if (!isServer && client != null) {
            client.sendTCP(verifica);
        }
    }

    public void sendAtaque(Ataque ataque) {
        if (ataque == null) return;
        if (isServer && server != null) {
            server.sendToAllTCP(ataque);
        } else if (!isServer && client != null) {
            client.sendTCP(ataque);
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

    
    public static class PinguinState {
        public float x;
        public float y;
        public int vida;
        public PinguinState() {}
        public PinguinState(float x, float y, int vida) { this.x = x; this.y = y; this.vida = vida; }
    }

    public static class SparkState {
        public float x;
        public float y;
        public int vida;
        public SparkState() {}
        public SparkState(float x, float y, int vida) { this.x = x; this.y = y; this.vida = vida; }
    }

    public static class VileState {
        public float x;
        public float y;
        public int vida;
        public VileState() {}
        public VileState(float x, float y, int vida) { this.x = x; this.y = y; this.vida = vida; }
    }

	public static class EnemyHit {
		public int id;
		public int damage;
		public EnemyHit() {}
		public EnemyHit(int id, int damage) { this.id = id; this.damage = damage; }
	}

    public static class ConnectionRejected {
        public String message;
        public ConnectionRejected() {}
        public ConnectionRejected(String message) { this.message = message; }
    }

	public void sendEnemyHit(int enemyId, int damage) {
		if (!isServer && client != null) {
			EnemyHit hit = new EnemyHit(enemyId, damage);
			try {
				client.sendTCP(hit);
			} catch (Throwable t) {
				Gdx.app.error("Network", "Failed to send EnemyHit: " + t.getMessage());
			}

		} else if (isServer && server != null) {

			jogo.applyEnemyHit(enemyId, damage);

			server.sendToAllTCP(jogo.getEnemyPositions());
		}
	}   
    
}