package Ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DialogJugadores extends JDialog {
    private JTextField[] camposNombres;
    private boolean confirmado = false;
    private List<String> jugadores;

    public DialogJugadores(Frame parent, String mesaId, int activosActuales) {
        super(parent, "Agregar Jugadores - Mesa " + mesaId, true);
        setLayout(new BorderLayout(10, 10));
        setSize(400, 350);
        setLocationRelativeTo(parent);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int disponibles = 5 - activosActuales;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = new JLabel("Ingrese los nombres de los jugadores (máximo " + disponibles + "):");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(lblTitulo, gbc);

        camposNombres = new JTextField[5];
        for (int i = 0; i < 5; i++) {
            gbc.gridy = i + 1;
            gbc.gridwidth = 1;
            gbc.gridx = 0;
            JLabel lbl = new JLabel("Jugador " + (i + 1) + ":");
            lbl.setEnabled(i < disponibles);
            panel.add(lbl, gbc);
            
            gbc.gridx = 1;
            camposNombres[i] = new JTextField(15);
            camposNombres[i].setEnabled(i < disponibles);
            if (i < disponibles) {
                camposNombres[i].setToolTipText("Ingrese el nombre del jugador " + (i + 1));
            } else {
                camposNombres[i].setToolTipText("Máximo " + disponibles + " jugadores");
                camposNombres[i].setBackground(Color.LIGHT_GRAY);
            }
            panel.add(camposNombres[i], gbc);
        }

        add(panel, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnOk = new JButton("Agregar Jugadores");
        btnOk.addActionListener(e -> {
            jugadores = new ArrayList<>();
            for (int i = 0; i < disponibles; i++) {
                String nombre = camposNombres[i].getText().trim();
                if (!nombre.isEmpty()) {
                    jugadores.add(nombre);
                }
            }
            
            if (jugadores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese al menos un jugador");
                return;
            }
            
            confirmado = true;
            dispose();
        });

        JButton btnCancel = new JButton("Cancelar");
        btnCancel.addActionListener(e -> dispose());

        panelBotones.add(btnOk);
        panelBotones.add(btnCancel);
        add(panelBotones, BorderLayout.SOUTH);
    }

    public boolean isConfirmado() { return confirmado; }
    public List<String> getJugadores() { return jugadores; }
}