package chatservidor;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import javax.swing.JOptionPane;

/**
 * Clase en la que se maneja la comunicación del lado del servidor.
 */
public class Servidor extends Thread {
    /** Socket servidor que escucha cuando los clientes se conectan para incluirlos en el chat. */
    private ServerSocket serverSocket;
    /** Lista de todos los hilos de comunicación, para cada cliente se instancia uno de estos hilos */
    public LinkedList<HiloCliente> clientes;
    /** Almacena la ventana que gestiona la interfaz gráfica del servidor. */
    private final VentanaS ventana;
    /** Almacena el puerto que el servidor usará para escuchar. */
    private final String puerto;
    /**
     * Correlativo para diferenciar a los múltiples clientes que se conectan, si se
     * conectaran, por ejemplo, dos usuarios con el mismo nombre, se podrían
     * diferenciar
     * por este correlativo.
     */
    public static int correlativo;

    /**
     * Constructor del servidor.
     * @param puerto
     * @param ventana
     */
    public Servidor(String puerto, VentanaS ventana) {
        correlativo = 0;
        this.puerto = puerto;
        this.ventana = ventana;
        clientes = new LinkedList<>();
        this.start();
    }

    /**
     * Método que escucha permenentemente en espera de conexiones de nuevos clientes.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(Integer.valueOf(puerto));
            ventana.addServidorIniciado();
            while (true) {
                HiloCliente h;
                Socket socket;
                socket = serverSocket.accept();
                System.out.println("Nueva conexion entrante: " + socket);
                h = new HiloCliente(socket, this);
                h.start();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ventana, "El servidor no se ha podido iniciar,\n"
                    + "puede que haya ingresado un puerto incorrecto.\n"
                    + "Esta aplicación se cerrará.");
            System.exit(0);
        }
    }

    /**
     * Devuelve una lista con los id de todos los clientes conectados.
     * @return lista de clientes
     */
    public LinkedList<String> getUsuariosConectados() {
        LinkedList<String> usuariosConectados = new LinkedList<>();
        clientes.stream().forEach(c -> usuariosConectados.add(c.getIdentificador()));
        return usuariosConectados;
    }

    /**
     * Método que agrega una linea al log de la interfaz gráfica del servidor.
     * @param texto
     */
    public void agregarLog(String texto) {
        ventana.agregarLog(texto);
    }
}
