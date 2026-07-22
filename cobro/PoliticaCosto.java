package cobro;

import java.math.BigDecimal;
import java.time.Duration;

@FunctionalInterface
public interface PoliticaCosto {
    BigDecimal calcular(Duration tiempoUsado);
}
