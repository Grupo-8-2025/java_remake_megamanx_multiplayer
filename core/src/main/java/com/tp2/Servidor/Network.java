package com.tp2.Servidor;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {
    static public final int port = 54555;

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
	}

}
