package Ui;

import domain.CuentaJugador;
import domain.EstadoMesa;
import domain.MesaBillar;
import Service.ServicioBillar;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PanelControl extends JPanel {
    private final ServicioBillar servicio;
    private final PanelMesas panelMesas;
    private JComboBox<String> cmbMesas;
    private DefaultListModel<String> modelJugadores;
    private JList<String> listJugadores;
    private JLabel lblInfoMesa;

    public PanelControl(ServicioBillar servicio, PanelMesas panelMesas) {
        this.servicio = servicio;
        this.panelMesas = panelMesas;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Control de Mesas"));
        setPreferredSize(new Dimension(350, 500));

        JPanel panelSuperior = new JPanel(new GridBagLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panelSuperior.add(new JLabel("Mesa:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cmbMesas = new JComboBox<>();
        cmbMesas.setPreferredSize(new Dimension(120, 25));
        cmbMesas.addActionListener(e -> actualizarListaJugadores());
        panelSuperior.add(cmbMesas, gbc);

        gbc.gridx = 3;
        gbc.gridwidth = 1;
        JButton btnAgregar = new JButton(" Agregar");
        btnAgregar.setToolTipText("Agregar jugadores a la mesa");
        btnAgregar.addActionListener(this::agregarJugadores);
        panelSuperior.add(btnAgregar, gbc);

        gbc.gridx = 4;
        JButton btnCerrar = new JButton(" Cerrar");
        btnCerrar.setToolTipText("Cerrar mesa (todos los jugadores)");
        btnCerrar.addActionListener(this::cerrarMesa);
        panelSuperior.add(btnCerrar, gbc);

        gbc.gridx = 5;
        JButton btnHistorial = new JButton(" Historial");
        btnHistorial.setToolTipText("Ver historial de la mesa");
        btnHistorial.addActionListener(this::verHistorial);
        panelSuperior.add(btnHistorial, gbc);

        add(panelSuperior, BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBorder(BorderFactory.createTitledBorder("Jugadores Activos"));

        modelJugadores = new DefaultListModel<>();
        listJugadores = new JList<>(modelJugadores);
        listJugadores.setFont(new Font("Monospaced", Font.PLAIN, 12));
        listJugadores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listJugadores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    verCuentaJugador();
                }
            }
        });

        JScrollPane scrollJugadores = new JScrollPane(listJugadores);
        scrollJugadores.setPreferredSize(new Dimension(300, 150));
        panelCentral.add(scrollJugadores, BorderLayout.CENTER);

        lblInfoMesa = new JLabel("Seleccione una mesa");
        lblInfoMesa.setFont(new Font("Arial", Font.PLAIN, 11));
        lblInfoMesa.setForeground(Color.GRAY);
        panelCentral.add(lblInfoMesa, BorderLayout.NORTH);

        JPanel panelAcciones = new JPanel(new GridLayout(1, 3, 5, 5));
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnCuenta = new JButton(" Cuenta");
        btnCuenta.setToolTipText("Ver cuenta del jugador seleccionado");
        btnCuenta.addActionListener(e -> verCuentaJugador());
        panelAcciones.add(btnCuenta);

        JButton btnAbonar = new JButton(" Abonar");
        btnAbonar.setToolTipText("Registrar un abono para el jugador");
        btnAbonar.addActionListener(e -> abonarJugador());
        panelAcciones.add(btnAbonar);

        JButton btnRetirar = new JButton(" Retirar");
        btnRetirar.setToolTipText("Retirar jugador de la mesa");
        btnRetirar.addActionListener(e -> retirarJugador());
        panelAcciones.add(btnRetirar);

        panelCentral.add(panelAcciones, BorderLayout.SOUTH);
        add(panelCentral, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Mantenimiento"));

        JButton btnMantenimiento = new JButton(" Mantenimiento");
        btnMantenimiento.addActionListener(this::ponerMantenimiento);
        panelInferior.add(btnMantenimiento);

        JButton btnReparar = new JButton(" Reparar");
        btnReparar.addActionListener(this::repararMesa);
        panelInferior.add(btnReparar);

        JButton btnEliminar = new JButton(" Eliminar Mesa");
        btnEliminar.addActionListener(this::eliminarMesa);
        panelInferior.add(btnEliminar);

        add(panelInferior, BorderLayout.SOUTH);

        actualizarListaMesas();
    }

    private void actualizarListaMesas() {
        cmbMesas.removeAllItems();
        for (String id : servicio.getMesas().keySet()) {
            cmbMesas.addItem(id);
        }
        actualizarListaJugadores();
        panelMesas.actualizarMesas();
    }

    private void actualizarListaJugadores() {
        modelJugadores.clear();
        String mesaId = getMesaSeleccionada();
        if (mesaId != null) {
            Map<String, CuentaJugador> jugadores = servicio.getJugadoresActivos(mesaId);
            if (jugadores.isEmpty()) {
                modelJugadores.addElement("(No hay jugadores)");
                lblInfoMesa.setText("Mesa " + mesaId + " - Libre");
                lblInfoMesa.setForeground(Color.GREEN);
            } else {
                for (String nombre : jugadores.keySet()) {
                    CuentaJugador cuenta = jugadores.get(nombre);
                    if (!cuenta.isCerrada()) {
                        String tiempo = String.format("%.1f min", cuenta.getTiempoAcumulado().doubleValue());
                        modelJugadores.addElement(String.format("%-15s $%-8s %s", 
                            nombre, cuenta.getTotalPagado(), tiempo));
                    }
                }
                MesaBillar mesa = servicio.obtenerMesa(mesaId);
                lblInfoMesa.setText("Mesa " + mesaId + " - " + jugadores.size() + " jugadores");
                lblInfoMesa.setForeground(Color.RED);
            }
        } else {
            lblInfoMesa.setText("Seleccione una mesa");
            lblInfoMesa.setForeground(Color.GRAY);
        }
    }

    private String getMesaSeleccionada() {
        return (String) cmbMesas.getSelectedItem();
    }

    private String getJugadorSeleccionado() {
        String seleccion = listJugadores.getSelectedValue();
        if (seleccion != null && !seleccion.equals("(No hay jugadores)")) {
            return seleccion.split("\\s+")[0]; 
        }
        return null;
    }

    private void agregarJugadores(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        MesaBillar mesa = servicio.obtenerMesa(mesaId);
        if (mesa.getEstado() == EstadoMesa.MANTENIMIENTO) {
            JOptionPane.showMessageDialog(this, "Mesa en mantenimiento");
            return;
        }

        int activos = (int) mesa.getCuentasActivas().values().stream()
                .filter(c -> !c.isCerrada())
                .count();
        
        if (activos >= 5) {
            JOptionPane.showMessageDialog(this, "Mesa completa (máximo 5 jugadores)");
            return;
        }

        DialogJugadores dialog = new DialogJugadores(
            (Frame) SwingUtilities.getWindowAncestor(this),
            mesaId, activos
        );
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            List<String> nombres = dialog.getJugadores();
            try {
                servicio.agregarJugadoresAMesa(mesaId, nombres);
                actualizarListaJugadores();
                panelMesas.actualizarMesas();
                JOptionPane.showMessageDialog(this, 
                    "Numero: " + nombres.size() + " jugadores agregados a " + mesaId);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
            }
        }
    }

    private void verCuentaJugador() {
        String mesaId = getMesaSeleccionada();
        String jugador = getJugadorSeleccionado();
        if (mesaId == null || jugador == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador de la lista");
            return;
        }

        DialogCuenta dialog = new DialogCuenta(
            (Frame) SwingUtilities.getWindowAncestor(this),
            servicio, mesaId, jugador
        );
        dialog.setVisible(true);
        actualizarListaJugadores();
        panelMesas.actualizarMesas();
    }

    private void abonarJugador() {
        String mesaId = getMesaSeleccionada();
        String jugador = getJugadorSeleccionado();
        if (mesaId == null || jugador == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador de la lista");
            return;
        }

        DialogAbono dialog = new DialogAbono(
            (Frame) SwingUtilities.getWindowAncestor(this),
            servicio, mesaId, jugador
        );
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            actualizarListaJugadores();
            panelMesas.actualizarMesas();
        }
    }

    private void retirarJugador() {
        String mesaId = getMesaSeleccionada();
        String jugador = getJugadorSeleccionado();
        if (mesaId == null || jugador == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un jugador de la lista");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Retirar a " + jugador + " de la mesa " + mesaId + "?",
            "Confirmar retiro",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                CuentaJugador cuenta = servicio.retirarJugador(mesaId, jugador);
                BigDecimal deuda = servicio.calcularDeudaJugador(mesaId, jugador);
                JOptionPane.showMessageDialog(this,
                    "Jugador retirado:\n" +
                    "Tiempo: " + cuenta.getTiempoAcumulado() + " min\n" +
                    "Total pagado: $" + cuenta.getTotalPagado() + "\n" +
                    "Saldo pendiente: $" + deuda
                );
                actualizarListaJugadores();
                panelMesas.actualizarMesas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
            }
        }
    }

    private void cerrarMesa(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Cerrar mesa " + mesaId + "? (Todos los jugadores se retirarán)",
            "Cerrar mesa",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                List<CuentaJugador> cuentas = servicio.cerrarMesaCompleta(mesaId);
                StringBuilder sb = new StringBuilder(" Mesa cerrada:\n");
                BigDecimal totalGeneral = BigDecimal.ZERO;
                for (CuentaJugador c : cuentas) {
                    sb.append("• ").append(c.getJugador().getNombre())
                      .append(" - ").append(c.getTiempoAcumulado())
                      .append(" min - Pagado: $").append(c.getTotalPagado()).append("\n");
                    totalGeneral = totalGeneral.add(c.getTotalPagado());
                }
                sb.append("\nTotal recaudado: $").append(totalGeneral);
                JOptionPane.showMessageDialog(this, sb.toString());
                actualizarListaJugadores();
                panelMesas.actualizarMesas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
            }
        }
    }

    private void verHistorial(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        DialogHistorial dialog = new DialogHistorial(
            (Frame) SwingUtilities.getWindowAncestor(this),
            servicio, mesaId
        );
        dialog.setVisible(true);
    }

    private void ponerMantenimiento(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        try {
            servicio.ponerMantenimiento(mesaId);
            actualizarListaJugadores();
            panelMesas.actualizarMesas();
            JOptionPane.showMessageDialog(this, " Mesa " + mesaId + " en mantenimiento");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
        }
    }

    private void repararMesa(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        try {
            servicio.repararMesa(mesaId);
            actualizarListaJugadores();
            panelMesas.actualizarMesas();
            JOptionPane.showMessageDialog(this, " Mesa " + mesaId + " reparada");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
        }
    }

    private void eliminarMesa(ActionEvent e) {
        String mesaId = getMesaSeleccionada();
        if (mesaId == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Eliminar la mesa " + mesaId + " permanentemente?",
            "Eliminar mesa",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                servicio.eliminarMesa(mesaId);
                actualizarListaMesas();
                panelMesas.actualizarMesas();
                JOptionPane.showMessageDialog(this, " Mesa " + mesaId + " eliminada");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, " Error: " + ex.getMessage());
            }
        }
    }

    public void refrescar() {
        actualizarListaMesas();
    }
}