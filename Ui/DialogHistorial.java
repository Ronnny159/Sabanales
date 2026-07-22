package Ui;

import domain.CuentaJugador;
import Service.ServicioBillar;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DialogHistorial extends JDialog {
    public DialogHistorial(Frame parent, ServicioBillar servicio, String mesaId) {
        super(parent, " Historial - Mesa " + mesaId, true);
        setLayout(new BorderLayout(10, 10));
        setSize(650, 450);
        setLocationRelativeTo(parent);

        List<CuentaJugador> historial = servicio.getHistorialMesa(mesaId);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        if (historial.isEmpty()) {
            textArea.setText(" No hay historial para esta mesa");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("═".repeat(60)).append("\n");
            sb.append("   HISTORIAL DE MESA ").append(mesaId).append("\n");
            sb.append("═".repeat(60)).append("\n\n");
            
            final BigDecimal[] totalGeneral = new BigDecimal[]{BigDecimal.ZERO};
            
            historial.stream()
                .sorted((a, b) -> b.getFinJuego().compareTo(a.getFinJuego()))
                .forEach(cuenta -> {
                    sb.append("👤 ").append(cuenta.getJugador().getNombre()).append("\n");
                    sb.append("   Inicio: ").append(cuenta.getInicioJuego().toString()).append("\n");
                    sb.append("   Fin:    ").append(cuenta.getFinJuego().toString()).append("\n");
                    sb.append("   Tiempo: ").append(cuenta.getTiempoAcumulado()).append(" min\n");
                    sb.append("   Total pagado: $").append(cuenta.getTotalPagado()).append("\n");
                    
                    totalGeneral[0] = totalGeneral[0].add(cuenta.getTotalPagado());
                    
                    if (!cuenta.getAbonos().isEmpty()) {
                        sb.append("   Abonos:\n");
                        cuenta.getAbonos().forEach(abono -> {
                            sb.append("    • $").append(abono.getMonto())
                              .append(" - ").append(abono.getFecha().toString()).append("\n");
                        });
                    }
                    sb.append("  ").append("─".repeat(50)).append("\n\n");
                });
            
            sb.append("═".repeat(60)).append("\n");
            sb.append("   TOTAL RECAUDADO: $").append(totalGeneral[0]).append("\n");
            sb.append("═".repeat(60));
            
            textArea.setText(sb.toString());
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());

        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.add(btnCerrar);
        add(panelBoton, BorderLayout.SOUTH);
    }
}