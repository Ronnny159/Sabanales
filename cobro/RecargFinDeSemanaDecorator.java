package cobro;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

public class RecargFinDeSemanaDecorator implements PoliticaCosto {
private final PoliticaCosto wrapped;
    private final Instant inicioUso;
    private final BigDecimal porCentajeExtra;

    public RecargFinDeSemanaDecorator(PoliticaCosto wrapped,Instant inicioUso,BigDecimal porCentajeExtra){
        this.wrapped=wrapped;
        this.inicioUso=inicioUso;
        this.porCentajeExtra=porCentajeExtra;
    }
    
    @Override
    public BigDecimal calcular(Duration tiempoUsado){
        BigDecimal base=wrapped.calcular(tiempoUsado);
        DayOfWeek dia = DayOfWeek.from(inicioUso.atZone(ZoneOffset.UTC));
        if(dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY){
            return base.multiply(BigDecimal.ONE.add(porCentajeExtra));
        }
        return base;
    }
}
