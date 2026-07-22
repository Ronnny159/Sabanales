package Ui;

import Service.ServicioBillar;
import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SabanalesFrame extends JFrame {
    private final ServicioBillar servicio;
    private final PanelMesas panelMesas;
    private final PanelControl panelControl;

    public SabanalesFrame(ServicioBillar servicio) {
        this.servicio = servicio;
        setTitle("Sabanales - Sistema de Billar");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        panelMesas = new PanelMesas(servicio);
        panelControl = new PanelControl(servicio, panelMesas);

        servicio.setObservadorUI(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                SwingUtilities.invokeLater(() -> {
                    panelMesas.actualizarMesas();
                    panelControl.refrescar();
                });
            }
        });

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(0, 102, 204));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("🏆 SABANALES BILLAR");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panelSuperior.add(lblTitulo, BorderLayout.WEST);

        JLabel lblFecha = new JLabel(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        lblFecha.setFont(new Font("Arial", Font.PLAIN, 14));
        lblFecha.setForeground(Color.WHITE);
        panelSuperior.add(lblFecha, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);
        add(panelControl, BorderLayout.WEST);
        add(new JScrollPane(panelMesas), BorderLayout.CENTER);

        // Panel inferior con información
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBackground(Color.LIGHT_GRAY);
        JLabel lblInfo = new JLabel("🔹 Seleccione una mesa y agregue jugadores | Doble clic en jugador para ver cuenta");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInferior.add(lblInfo);
        add(panelInferior, BorderLayout.SOUTH);

        panelMesas.actualizarMesas();
    }

    @Override
    public void dispose() {
        servicio.guardarEstado();
        super.dispose();
    }
}