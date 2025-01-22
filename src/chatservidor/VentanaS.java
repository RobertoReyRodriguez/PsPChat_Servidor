package chatservidor;

import java.awt.*;
import javax.swing.*;

/**
 * Clase que gestiona la interfaz gráfica del servidor
 */
public class VentanaS extends JFrame {
    private final String DEFAULT_PORT = "10101";
    private final Servidor servidor;
    
    private JScrollPane jScrollPane1;
    private JTextArea txtClientes;

    public VentanaS() {
        initComponents();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String puerto = getPuerto();
        servidor = new Servidor(puerto, this);
    }

    private void initComponents() {

        jScrollPane1 = new JScrollPane();
        txtClientes = new JTextArea();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor");

        jScrollPane1.setBorder(BorderFactory.createTitledBorder("Log del Servidor"));

        txtClientes.setEditable(false);
        txtClientes.setColumns(20);
        txtClientes.setRows(5);
        jScrollPane1.setViewportView(txtClientes);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 284, Short.MAX_VALUE)
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                                .addContainerGap()));

        pack();
    }

    /**
     * @param args 
     */
    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaS().setVisible(true);
            }
        });
    }

    /**
     * Método que agrega una línea de texto al log.
     * @param texto
     */
    public void agregarLog(String texto) {
        txtClientes.append(texto);
    }

    /**
     * Método para que el usuario ingrese el puerto
     * @return string del puerto
     */
    private String getPuerto() {
        String p = DEFAULT_PORT;
        JTextField puerto = new JTextField(20);
        puerto.setText(p);
        JPanel myPanel = new JPanel();
        myPanel.setLayout(new GridLayout(2, 1));
        myPanel.add(new JLabel("Puerto de la conexión:"));
        myPanel.add(puerto);
        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Configuraciones de la comunicación", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            p = puerto.getText();
        } else {
            System.exit(0);
        }
        return p;
    }

    /**
     * Método que agrega un mensaje de confirmación al log cuando el servidor está
     * corriendo correctamente.
     */
    public void addServidorIniciado() {
        txtClientes.setText("Inicializando el servidor... [Ok].");
    }
}
