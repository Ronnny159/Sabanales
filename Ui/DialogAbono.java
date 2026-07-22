package Ui;

import domain.CuentaJugador;
import Service.ServicioBillar;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogAbono extends JDialog {
    private JTextField txtMonto;
    private boolean confirmado = false;

    public DialogAbono(Frame parent, ServicioBillar servicio, String mesaId, String nombreJugador) {
        super(parent, " Abono - " + nombreJugador, true);
        setLayout(new BorderLayout(10, 10));
        setSize(350, 180);
        setLocationRelativeTo(parent);

        CuentaJugador cuenta = servicio.getJugadoresActivos(mesaId).get(nombreJugador);
        BigDecimal deudaActual = servicio.calcularDeudaJugador(mesaId, nombreJugador);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Deuda actual:"), gbc);
        gbc.gridx = 1;
        JLabel lblDeuda = new JLabel("$" + deudaActual.toString());
        lblDeuda.setFont(new Font("Arial", Font.BOLD, 14));
        if (deudaActual.compareTo(BigDecimal.ZERO) > 0) {
            lblDeuda.setForeground(Color.RED);
        } else {
            lblDeuda.setForeground(Color.GREEN);
        }
        panel.add(lblDeuda, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Monto a abonar ($):"), gbc);
        
        gbc.gridx = 1;
        txtMonto = new JTextField(10);
        txtMonto.setToolTipText("Ingrese el monto a pagar");
        panel.add(txtMonto, gbc);

        add(panel, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnOk = new JButton(" Registrar Abono");
        btnOk.addActionListener(e -> {
            try {
                BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
                if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0");
                    return;
                }
                servicio.registrarAbono(mesaId, nombreJugador, monto);
                confirmado = true;
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, " Ingrese un monto válido (ej: 10.50)");
            }
        });

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());

        panelBotones.add(btnOk);
        panelBotones.add(btnCancel);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public boolean isConfirmado() { return confirmado; }
}