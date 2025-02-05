package chatservidor;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

/**
 * Clase para los hilos de los clientes
 */
public class HiloCliente extends Thread {
    /** Socket que se utiliza para comunicarse con el cliente. */
    private final Socket socket;
    /** Stream con el que se envían objetos al servidor. */
    private ObjectOutputStream objectOutputStream;
    /** Stream con el que se reciben objetos del servidor. */
    private ObjectInputStream objectInputStream;
    /** Servidor al que pertenece este hilo. */
    private final Servidor server;
    /** Identificador único del cliente con el que este hilo se comunica. */
    private String identificador;
    /** Almacena true cuando esta escuchando */
    private boolean escuchando;

    /**
     * Constructor de la clase hilo cliente.
     * @param socket
     * @param server
     */
    public HiloCliente(Socket socket, Servidor server) {
        this.server = server;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.err.println("Error en la inicialización del ObjectOutputStream y el ObjectInputStream");
        }
    }

    /**
     * Método encargado de cerrar el socket con el que se esta comunicando.
     */
    public void desconectar() {
        try {
            socket.close();
            escuchando = false;
        } catch (IOException ex) {
            System.err.println("Error al cerrar el socket de comunicación con el cliente.");
        }
    }

    /**
     * Corre el ciclo infinito.
     */
    public void run() {
        try {
            escuchar();
        } catch (Exception ex) {
            System.err.println("Error al llamar al método readLine del hilo del cliente.");
        }
        desconectar();
    }

    /**
     * Método que escucha todo lo que es enviado por el cliente que se comunica con él.
     */
    public void escuchar() {
        escuchando = true;
        while (escuchando) {
            try {
                Object aux = objectInputStream.readObject();
                if (aux instanceof LinkedList) {
                    ejecutar((LinkedList<String>) aux);
                }
            } catch (Exception e) {
                System.err.println("Error al leer lo enviado por el cliente.");
            }
        }
    }

    /**
     * Método que realiza determinadas acciones dependiendo de lo que el socket haya
     * recibido y lo que este le envie el método, en él se manejan una serie de códigos.
     * @param lista
     */
    public void ejecutar(LinkedList<String> lista) {
        String tipo = lista.get(0);
        switch (tipo) {
            case "SOLICITUD_CONEXION":
                confirmarConexion(lista.get(1));
                break;
            case "SOLICITUD_DESCONEXION":
                confirmarDesConexion();
                break;
            case "MENSAJE":
                String destinatario = lista.get(2);
                server.clientes
                        .stream()
                        .filter(h -> (destinatario.equals(h.getIdentificador())))
                        .forEach((h) -> h.enviarMensaje(lista));
                break;
            default:
                break;
        }
    }

    /**
     * Método para enviar un mensaje al cliente atraves del socket.
     * @param lista
     */
    private void enviarMensaje(LinkedList<String> lista) {
        try {
            objectOutputStream.writeObject(lista);
        } catch (Exception e) {
            System.err.println("Error al enviar el objeto al cliente.");
        }
    }

    /**
     * Método que notifica a los clientes conectados que hay un nuevo cliente para que lo agreguen a sus contactos.
     * @param identificador
     */
    private void confirmarConexion(String identificador) {
        Servidor.correlativo++;
        this.identificador = Servidor.correlativo + " - " + identificador;
        LinkedList<String> lista = new LinkedList<>();
        lista.add("CONEXION_ACEPTADA");
        lista.add(this.identificador);
        lista.addAll(server.getUsuariosConectados());
        enviarMensaje(lista);
        server.agregarLog("\nNuevo cliente: " + this.identificador);
        LinkedList<String> auxLista = new LinkedList<>();
        auxLista.add("NUEVO_USUARIO_CONECTADO");
        auxLista.add(this.identificador);
        server.clientes
                .stream()
                .forEach(cliente -> cliente.enviarMensaje(auxLista));
        server.clientes.add(this);
    }

    /**
     * Devuelve el id único del cliente dentro del chat.
     * @return id del cliente
     */
    public String getIdentificador() {
        return identificador;
    }

    /**
     * Método que informa a los clientes de que un cliente se ha desconectado
     */
    private void confirmarDesConexion() {
        LinkedList<String> auxLista = new LinkedList<>();
        auxLista.add("USUARIO_DESCONECTADO");
        auxLista.add(this.identificador);
        server.agregarLog("\nEl cliente \"" + this.identificador + "\" se ha desconectado.");
        this.desconectar();
        for (int i = 0; i < server.clientes.size(); i++) {
            if (server.clientes.get(i).equals(this)) {
                server.clientes.remove(i);
                break;
            }
        }
        server.clientes
                .stream()
                .forEach(h -> h.enviarMensaje(auxLista));
    }
}