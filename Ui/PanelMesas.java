package Ui;

import domain.EstadoMesa;
import domain.MesaBillar;
import Service.ServicioBillar;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PanelMesas extends JPanel {
    private final ServicioBillar servicio;
    private JPanel panelMesasGrid;

    public PanelMesas(ServicioBillar servicio) {
        this.servicio = servicio;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Mesas de Billar"));

        panelMesasGrid = new JPanel(new GridLayout(0, 3, 15, 15));
        panelMesasGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelMesasGrid, BorderLayout.CENTER);

        actualizarMesas();
    }

    public void actualizarMesas() {
        panelMesasGrid.removeAll();
        Map<String, MesaBillar> mesas = servicio.getMesas();

        if (mesas.isEmpty()) {
            JLabel lblVacio = new JLabel("No hay mesas registradas", SwingConstants.CENTER);
            lblVacio.setFont(new Font("Arial", Font.BOLD, 16));
            panelMesasGrid.add(lblVacio);
        } else {
            for (MesaBillar mesa : mesas.values()) {
                panelMesasGrid.add(crearPanelMesa(mesa));
            }
        }

        panelMesasGrid.revalidate();
        panelMesasGrid.repaint();
    }

    private JPanel crearPanelMesa(MesaBillar mesa) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getColorPorEstado(mesa.getEstado()), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);

        // Cabecera: ID y estado
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelNorte.setOpaque(false);
        
        JLabel lblId = new JLabel(mesa.getId());
        lblId.setFont(new Font("Arial", Font.BOLD, 16));
        panelNorte.add(lblId);
        
        JLabel lblEstado = new JLabel(mesa.getEstado().toString());
        lblEstado.setFont(new Font("Arial", Font.PLAIN, 10));
        lblEstado.setForeground(getColorPorEstado(mesa.getEstado()));
        panelNorte.add(lblEstado);
        
        panel.add(panelNorte, BorderLayout.NORTH);

        // Centro: Información de jugadores
        JPanel panelCentro = new JPanel(new GridLayout(0, 1, 2, 2));
        panelCentro.setOpaque(false);
        
        int jugadores = mesa.getCantidadJugadores();
        if (jugadores > 0) {
            JLabel lblJugadores = new JLabel("👤 " + jugadores + " jugadores");
            lblJugadores.setFont(new Font("Arial", Font.BOLD, 12));
            panelCentro.add(lblJugadores);
            
            Map<String, ?> cuentas = mesa.getCuentasActivas();
            for (String nombre : cuentas.keySet()) {
                JLabel lblNombre = new JLabel("  • " + nombre);
                lblNombre.setFont(new Font("Arial", Font.PLAIN, 10));
                panelCentro.add(lblNombre);
            }
        } else if (mesa.getEstado() == EstadoMesa.MANTENIMIENTO) {
            JLabel lblMantenimiento = new JLabel("🔧 Mantenimiento");
            lblMantenimiento.setFont(new Font("Arial", Font.BOLD, 12));
            lblMantenimiento.setForeground(Color.GRAY);
            panelCentro.add(lblMantenimiento);
        } else {
            JLabel lblLibre = new JLabel("✅ Disponible");
            lblLibre.setFont(new Font("Arial", Font.BOLD, 12));
            lblLibre.setForeground(Color.GREEN);
            panelCentro.add(lblLibre);
        }
        
        panel.add(panelCentro, BorderLayout.CENTER);

        // Tooltip con información detallada
        StringBuilder tooltip = new StringBuilder();
        tooltip.append("Mesa: ").append(mesa.getId()).append("\n");
        tooltip.append("Estado: ").append(mesa.getEstado()).append("\n");
        tooltip.append("Jugadores: ").append(mesa.getCantidadJugadores()).append("\n");
        Map<String, ?> cuentas = mesa.getCuentasActivas();
        for (String nombre : cuentas.keySet()) {
            tooltip.append("  • ").append(nombre).append("\n");
        }
        panel.setToolTipText(tooltip.toString());

        return panel;
    }

    private Color getColorPorEstado(EstadoMesa estado) {
        return switch (estado) {
            case LIBRE -> new Color(0, 150, 0);
            case OCUPADA -> new Color(200, 0, 0);
            case MANTENIMIENTO -> Color.GRAY;
            case LIMPIEZA -> new Color(0, 150, 200);
        };
    }
}