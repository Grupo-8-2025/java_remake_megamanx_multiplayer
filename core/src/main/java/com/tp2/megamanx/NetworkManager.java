package com.tp2.megamanx;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.tp2.megamanx.Iterators.InimigoIterator;
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
            int udpPort = tcpPort + 222; // 54777 - 54555 = 222
            try {
                server = new Server();   // Aumentar buffers para 1MB para evitar overflow
                registerClasses(server); // Registra as classes que serão enviadas
                server.start();          // Inicia o servidor
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
            server = null; // Disabilita o servidor se falhar
        }

        if (server != null) { // Se o servidor iniciou corretamente
            server.addListener(new Listener() { // Adiciona um listener para receber mensagens
                @Override
                public void connected(Connection connection) {
                    try {
                        int clients = server.getConnections().length; // número de clientes conectados
                        Gdx.app.log("Network", "Client connected. total clients=" + clients);
                        // Permitimos apenas 1 cliente além do servidor (total de players = 2)
                        if (clients > 1) {
                            // Rejeita a nova conexão com uma mensagem amigável e fecha a conexão
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
                public void received(Connection connection, Object object) { // Quando uma mensagem é recebida
                    if (object instanceof PlayerPosition) { // Se receber a posição do jogador
                        PlayerPosition pos = (PlayerPosition) object; // Atualiza a posição do jogador remoto
                        jogo.updateRemotePlayer(pos); // Atualiza a posição do jogador remoto
                    } else if (object instanceof EnemyHit) {
                        EnemyHit hit = (EnemyHit) object;
                        // Delegar ao jogo para aplicar o dano (servidor é autoritativo)
                        jogo.applyEnemyHit(hit.id, hit.damage);
                    } else if (object instanceof PhaseChange) {
                        PhaseChange pc = (PhaseChange) object;
                        // Aplicar mudança de fase no jogo (agendar na thread principal)
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (pc.segundaFase) jogo.iniciarSegundaFase(false);
                                    } catch (Throwable t) {
                                        Gdx.app.error("Network", "Failed to apply PhaseChange on server: " + t.getMessage());
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to post PhaseChange runnable on server: " + t.getMessage());
                        }
                    }
                    else if (object instanceof PosicaoTiro) {
                        // Recebeu tiro de um cliente: aplicar no jogo (agendando na thread principal)
                        final PosicaoTiro pt = (PosicaoTiro) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.handleIncomingTiro(pt);
                                    } catch (Throwable t) {
                                        Gdx.app.error("Network", "Failed to handle incoming tiro on server: " + t.getMessage());
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to post PosicaoTiro runnable on server: " + t.getMessage());
                        }
                        // Repassa para os demais clientes (exceto o remetente)
                        try {
                            if (server != null) server.sendToAllExceptTCP(connection.getID(), pt);
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to forward PosicaoTiro to other clients: " + t.getMessage());
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
                    } else if (object instanceof PinguinState) { // Recebe estado leve do pinguim
                        PinguinState st = (PinguinState) object;
                        // Atualiza/Cria pinguim local sem depender de desserializar o objeto original
                        jogo.updatePinguinState(st);
                    } else if (object instanceof PhaseChange) {
                        final PhaseChange pc = (PhaseChange) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (pc.segundaFase) jogo.iniciarSegundaFase(false);
                                    } catch (Throwable t) {
                                        Gdx.app.error("Network", "Failed to apply PhaseChange on client: " + t.getMessage());
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to post PhaseChange runnable on client: " + t.getMessage());
                        }
                    } else if (object instanceof ConnectionRejected) {
                        final ConnectionRejected cr = (ConnectionRejected) object;
                        // NÃO criar objetos gráficos na thread do listener (causa crash GL)
                        // Agendamos na thread principal do LibGDX
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
                        final PosicaoTiro pt = (PosicaoTiro) object;
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        jogo.handleIncomingTiro(pt);
                                    } catch (Throwable t) {
                                        Gdx.app.error("Network", "Failed to handle incoming tiro on client: " + t.getMessage());
                                    }
                                }
                            });
                        } catch (Throwable t) {
                            Gdx.app.error("Network", "Failed to post PosicaoTiro runnable on client: " + t.getMessage());
                        }
                    }
                    // outros tipos leves (PosicaoTiro, etc.) podem ser tratados aqui
                }
            });
        }
    }

    // ADDED: NullSerializer para evitar serializar recursos/problemáticas nativas (retorna null ao desserializar)
    private static class NullSerializer<T> extends Serializer<T> {
        @Override
        public void write(com.esotericsoftware.kryo.Kryo kryo, Output output, T object) {
            // intentionally write nothing
        }
        @Override
        public T read(com.esotericsoftware.kryo.Kryo kryo, Input input, Class<T> type) {
            return null;
        }
    }

    /* Registra as classes que serão enviadas */
    private void registerClasses(Object network) {
        // Register only light-weight DTOs and basic Java types. Do NOT register LibGDX engine classes
        // or game object classes that hold Textures/Sounds/etc. This avoids Kryo registration id mismatches.
        com.esotericsoftware.kryo.Kryo kryo;
        if (network instanceof Server) {
            kryo = ((Server) network).getKryo();
        } else {
            kryo = ((Client) network).getKryo();
        }

        try { kryo.setRegistrationRequired(false); } catch (Throwable ignored) {}

        // DTOs used by the networking layer
        kryo.register(PlayerPosition.class);
        kryo.register(EnemyPosition.class);
        kryo.register(PosicaoTiro.class); // if used
        kryo.register(PinguinState.class);
        kryo.register(EnemyHit.class);
    kryo.register(PhaseChange.class);
    kryo.register(ConnectionRejected.class);

        // Basic collections/primitives used inside DTOs
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.lang.Float.class);
        kryo.register(java.lang.Integer.class);
        kryo.register(float[].class);
        kryo.register(int[].class);

        // If you later add new DTOs, register them here (both client and server must register same DTOs).
    }

    // ADDED: tenta configurar DefaultInstantiatorStrategy com StdInstantiatorStrategy (Objenesis) via reflection.
    private void configureKryo(com.esotericsoftware.kryo.Kryo kryo) {
        try {
            // Cria instância de org.objenesis.strategy.StdInstantiatorStrategy via reflection
            Class<?> stdClass = Class.forName("org.objenesis.strategy.StdInstantiatorStrategy");
            Object stdInstance = stdClass.getDeclaredConstructor().newInstance();

            // Cria instância de com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy via reflection,
            // passando o StdInstantiatorStrategy no construtor.
            Class<?> defaultStratClass = Class.forName("com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy");
            java.lang.reflect.Constructor<?> ctor = defaultStratClass.getConstructor(stdClass.getInterfaces().length > 0 ? stdClass.getInterfaces()[0] : stdClass);
            Object defaultStrategyInstance;
            try {
                // Tenta com o tipo concreto do parâmetro (caso seja exatamente org.objenesis.strategy.InstantiatorStrategy)
                defaultStrategyInstance = ctor.newInstance(stdInstance);
            } catch (Exception nsme) {
                // Fallback: tentar achar qualquer construtor e invocar
                java.lang.reflect.Constructor<?>[] ctors = defaultStratClass.getConstructors();
                if (ctors.length > 0) {
                    defaultStrategyInstance = ctors[0].newInstance(stdInstance);
                } else {
                    throw new RuntimeException("No suitable constructor found for DefaultInstantiatorStrategy");
                }
            }

            // Invoca kryo.setInstantiatorStrategy(...) por reflexão (procura método com 1 parâmetro)
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
            // Fallback: não exigir registro estrito
            kryo.setRegistrationRequired(false);
        } catch (Throwable ignored) {}
    }

    // Novo: registra um conjunto amplo de classes do pacote com.badlogic.gdx.graphics usando reflection
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
            "com.badlogic.gdx.graphics.g2d.TextureAtlas", // g2d often used; safe to try
            // inner enum for Files
            "com.badlogic.gdx.Files$FileType"
        };
        for (String name : classes) {
            tryRegisterIfPresent(kryo, name);
        }
        // também tentar registrar classes comuns do subpackage g2d (se desejar ampliar)
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

    // Helper: tenta registrar uma classe no Kryo somente se ela existir no classpath
    private void tryRegisterIfPresent(com.esotericsoftware.kryo.Kryo kryo, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            kryo.register(clazz);
            Gdx.app.log("Network", "Kryo registered class: " + className);
        } catch (ClassNotFoundException e) {
            // Classe não presente no core (normal). Ignorar.
            Gdx.app.log("Network", "Class not present, skipping Kryo register: " + className);
        } catch (Throwable t) {
            // Qualquer outro problema no registro: log e continue
            Gdx.app.error("Network", "Failed to register class via reflection: " + className + " -> " + t.getMessage());
            Gdx.app.error("Network", exceptionToString(t));
        }
    }

    // ADDED: helper para converter stacktrace em String
    private String exceptionToString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    // Envia a posição do jogador (incluindo dados de animação) para o outro jogador
    public void sendPlayerPosition(PlayerPosition pos) {
        if (pos == null) return;
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
    
    // Envia apenas EnemyPosition (evita enviar InimigoIterator)
    public void sendInimigos(InimigoIterator inimigos){
        if (isServer && server != null) {
            EnemyPosition pos = jogo.getEnemyPositions();
            server.sendToAllTCP(pos);
        }
    }

    // Envia apenas estado leve do pinguim (posição + vida)
    public void sendPinguin(Pinguim penguin) {
        if (isServer && server != null) {
            if (penguin == null) return;
            PinguinState st = new PinguinState(penguin.getPosX(), penguin.getPosY(), penguin.getVida());
            server.sendToAllTCP(st);
        }
    }

    public void sendTiroPositions(float x, float y, int id, int tipo, boolean paraDireita) {
        PosicaoTiro pos = new PosicaoTiro(x, y, id, tipo, paraDireita); // Cria o objeto de posição do tiro
        if (isServer && server != null) { // Se for o servidor
            server.sendToAllTCP(pos); // Envia para todos os clientes conectados
        } else if (!isServer && client != null) { // se for o cliente
            client.sendTCP(pos); // Envia para o servidor
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

    // NEW: lightweight DTO para enviar estado do Pinguim (evita serializar Texture/recursos)
    public static class PinguinState {
        public float x;
        public float y;
        public int vida;
        public PinguinState() {}
        public PinguinState(float x, float y, int vida) { this.x = x; this.y = y; this.vida = vida; }
    }

	// NEW DTO: cliente -> servidor indica que um inimigo recebeu dano
	public static class EnemyHit {
		public int id;
		public int damage;
		public EnemyHit() {}
		public EnemyHit(int id, int damage) { this.id = id; this.damage = damage; }
	}

    // DTO: indica mudança de fase (segundo nivel ativado/desativado)
    public static class PhaseChange {
        public boolean segundaFase;
        public PhaseChange() {}
        public PhaseChange(boolean segundaFase) { this.segundaFase = segundaFase; }
    }

    // DTO: servidor -> cliente informa que a conexão foi recusada
    public static class ConnectionRejected {
        public String message;
        public ConnectionRejected() {}
        public ConnectionRejected(String message) { this.message = message; }
    }

	// Envia evento de hit (cliente -> servidor)
	public void sendEnemyHit(int enemyId, int damage) {
		if (!isServer && client != null) {
			EnemyHit hit = new EnemyHit(enemyId, damage);
			try {
				client.sendTCP(hit);
			} catch (Throwable t) {
				Gdx.app.error("Network", "Failed to send EnemyHit: " + t.getMessage());
			}

		} else if (isServer && server != null) {
			// Se estiver rodando em modo servidor local (diagnóstico), processa direto
			jogo.applyEnemyHit(enemyId, damage);
			// e envia atualização de inimigos para clientes
			server.sendToAllTCP(jogo.getEnemyPositions());
		}
	}

        // Envia evento de mudança de fase (servidor->cliente ou cliente->servidor)
        public void sendPhaseChange(boolean segundaFase) {
            PhaseChange pc = new PhaseChange(segundaFase);
            if (isServer && server != null) {
                server.sendToAllTCP(pc);
            } else if (!isServer && client != null) {
                client.sendTCP(pc);
            }
        }
}