package Ui;

import domain.CuentaJugador;
import Service.ServicioBillar;
import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogCuenta extends JDialog {
    private final ServicioBillar servicio;
    private final String mesaId;
    private final String nombreJugador;
    private JLabel lblTiempo;
    private JLabel lblPagado;
    private JLabel lblDeuda;
    private JButton btnAbonar;
    private JButton btnRetirar;

    public DialogCuenta(Frame parent, ServicioBillar servicio, String mesaId, String nombreJugador) {
        super(parent, " Cuenta de " + nombreJugador, true);
        this.servicio = servicio;
        this.mesaId = mesaId;
        this.nombreJugador = nombreJugador;

        setLayout(new BorderLayout(10, 10));
        setSize(400, 350);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Información del jugador
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Jugador:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(nombreJugador), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Mesa:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(mesaId), gbc);

        // Tiempo jugado
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel(" Tiempo jugado:"), gbc);
        gbc.gridx = 1;
        CuentaJugador cuenta = servicio.getJugadoresActivos(mesaId).get(nombreJugador);
        if (cuenta == null) {
            JOptionPane.showMessageDialog(this, "El jugador ya no está activo");
            dispose();
            return;
        }
        lblTiempo = new JLabel(cuenta.getTiempoAcumulado() + " minutos");
        panel.add(lblTiempo, gbc);

        // Total pagado
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel(" Total pagado:"), gbc);
        gbc.gridx = 1;
        lblPagado = new JLabel("$" + cuenta.getTotalPagado().toString());
        lblPagado.setFont(new Font("Arial", Font.BOLD, 14));
        lblPagado.setForeground(new Color(0, 150, 0));
        panel.add(lblPagado, gbc);

        // Deuda actual
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel(" Deuda actual:"), gbc);
        gbc.gridx = 1;
        BigDecimal deuda = servicio.calcularDeudaJugador(mesaId, nombreJugador);
        lblDeuda = new JLabel("$" + deuda.toString());
        lblDeuda.setFont(new Font("Arial", Font.BOLD, 16));
        if (deuda.compareTo(BigDecimal.ZERO) > 0) {
            lblDeuda.setForeground(Color.RED);
        } else {
            lblDeuda.setForeground(Color.GREEN);
        }
        panel.add(lblDeuda, gbc);

        // Mostrar abonos realizados
        if (!cuenta.getAbonos().isEmpty()) {
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            JTextArea txtAbonos = new JTextArea(3, 20);
            txtAbonos.setEditable(false);
            txtAbonos.setFont(new Font("Monospaced", Font.PLAIN, 10));
            StringBuilder sb = new StringBuilder("Abonos realizados:\n");
            cuenta.getAbonos().forEach(a -> {
                sb.append("  • $").append(a.getMonto())
                  .append(" - ").append(a.getFecha().toString()).append("\n");
            });
            txtAbonos.setText(sb.toString());
            JScrollPane scrollAbonos = new JScrollPane(txtAbonos);
            panel.add(scrollAbonos, gbc);
        }

        add(panel, BorderLayout.CENTER);

        // Botones de acción
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnAbonar = new JButton(" Abonar");
        btnAbonar.addActionListener(e -> {
            DialogAbono dialog = new DialogAbono(
                (Frame) SwingUtilities.getWindowAncestor(this),
                servicio, mesaId, nombreJugador
            );
            dialog.setVisible(true);
            if (dialog.isConfirmado()) {
                actualizarDatos();
                JOptionPane.showMessageDialog(this, " Abono registrado con éxito");
            }
        });

        btnRetirar = new JButton(" Retirar Jugador");
        btnRetirar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de retirar a " + nombreJugador + "?",
                "Confirmar retiro",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    CuentaJugador cuentaRetirada = servicio.retirarJugador(mesaId, nombreJugador);
                    BigDecimal deudaFinal = servicio.calcularDeudaJugador(mesaId, nombreJugador);
                    JOptionPane.showMessageDialog(this,
                        "Jugador retirado.\n" +
                        "Tiempo total: " + cuentaRetirada.getTiempoAcumulado() + " min\n" +
                        "Total pagado: $" + cuentaRetirada.getTotalPagado() + "\n" +
                        "Saldo pendiente: $" + deudaFinal
                    );
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
                }
            }
        });

        panelBotones.add(btnAbonar);
        panelBotones.add(btnRetirar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void actualizarDatos() {
        CuentaJugador cuenta = servicio.getJugadoresActivos(mesaId).get(nombreJugador);
        if (cuenta != null && !cuenta.isCerrada()) {
            lblTiempo.setText(cuenta.getTiempoAcumulado() + " minutos");
            lblPagado.setText("$" + cuenta.getTotalPagado().toString());
            BigDecimal deuda = servicio.calcularDeudaJugador(mesaId, nombreJugador);
            lblDeuda.setText("$" + deuda.toString());
            if (deuda.compareTo(BigDecimal.ZERO) > 0) {
                lblDeuda.setForeground(Color.RED);
            } else {
                lblDeuda.setForeground(Color.GREEN);
            }
        }
    }
}