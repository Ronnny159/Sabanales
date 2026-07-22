import Service.ServicioBillar;
import Ui.SabanalesFrame;
import javax.swing.*;
import java.math.BigDecimal;
import java.time.Clock;

public class SabanalesApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ServicioBillar servicio = new ServicioBillar(Clock.systemDefaultZone(), BigDecimal.valueOf(5.0));

            // Crear mesas de ejemplo
            String[] mesasEjemplo = {"Mesa 1", "Mesa 2", "Mesa 3", "Mesa 4", "Mesa 5"};
            for (String id : mesasEjemplo) {
                try {
                    servicio.crearMesa(id);
                } catch (IllegalArgumentException ignored) {}
            }

            SabanalesFrame frame = new SabanalesFrame(servicio);
            frame.setVisible(true);
        });
    }
}
