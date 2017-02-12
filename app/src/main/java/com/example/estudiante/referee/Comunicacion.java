package com.example.estudiante.referee;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Observable;
/** 
 * * Created by estudiante on 11/02/17. 
 * */

public class Comunicacion extends Observable implements Runnable {
    private static Comunicacion ref;
    public static final String android = "10.0.2.2";
    public static final String miIp = "228.5.6.7";
    public static final int puerto = 5000;

    private MulticastSocket multi;
    private boolean corre;
    private boolean conec;
    private boolean reset;


    private Comunicacion() {
        corre = true;
        conec = true;
        reset = false;
    }

    public static Comunicacion getInstance() {
        if (ref == null) {
            ref = new Comunicacion();
            Thread corredor = new Thread(ref);
            corredor.start();
        }
        return ref;
    }

    @Override
    public void run() {
        while (corre) {
            if (conec) {
                if (reset) {
                    if (multi != null) {
                        multi.close();
                    }
                    reset = false;
                }
                conec = !intento();
            } else {
                if (multi != null) {
                    DatagramPacket paquete = recibir();
                    if (paquete != null) {
                        String mensaje = new String(paquete.getData(), 0, paquete.getLength());
                        setChanged();
                        notifyObservers(mensaje);
                        clearChanged();
                    }
                }
            }
        }
        multi.close();
    }

    public boolean intento() {
        multi = new MulticastSocket();
        try {
            setChanged();
            notifyObservers("Connection started");
            clearChanged();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void enviar(final String mensaje, final String destino, final int puerto) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (multi != null) {
                    try {
                        InetAddress net = InetAddress.getByName(destino);
                        multi.joinGroup(net);
                        byte[] data = mensaje.getBytes();
                        DatagramPacket packet = new DatagramPacket(data, data.length, net, puerto);
                        System.out.println("Sending data to " + net.getHostAddress() + ":" + puerto);
                        multi.send(packet);
                        System.out.println("Data was sent");
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    setChanged();
                    notifyObservers("Not connected");
                    clearChanged();
                }
            }
        }).start();

    }

    public DatagramPacket recibir() {
        byte[] tamaño = new byte[1024];
        DatagramPacket packet = new DatagramPacket(tamaño, tamaño.length);
        try {
            multi.receive(packet);
            System.out.println("Data received from " + packet.getAddress() + ":" + packet.getPort());
            return packet;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
