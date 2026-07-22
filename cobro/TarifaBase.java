package cobro;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class TarifaBase implements PoliticaCosto {
    private final BigDecimal precioPorHora;

    public TarifaBase(BigDecimal precioPorHora){
        this.precioPorHora=precioPorHora;
    }
    
    @Override
    public BigDecimal calcular(Duration tiempo){
        double horas=tiempo.toMinutes()/60.0;
        return precioPorHora.multiply(BigDecimal.valueOf(horas))
            .setScale(2,RoundingMode.HALF_UP);
    }
}
