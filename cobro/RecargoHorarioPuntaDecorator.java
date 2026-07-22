package cobro;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;


public class RecargoHorarioPuntaDecorator implements PoliticaCosto {
    private final PoliticaCosto wrapped;
    private final Instant inicioUso;
    private final BigDecimal porCentajeExtra;

    public RecargoHorarioPuntaDecorator(PoliticaCosto wrapped,Instant inicioUso,BigDecimal porCentajeExtra){
        this.wrapped=wrapped;
        this.inicioUso=inicioUso;
        this.porCentajeExtra=porCentajeExtra;
    }

    @Override
    public BigDecimal calcular(Duration tiempoUsado){
        BigDecimal base=wrapped.calcular(tiempoUsado);
        LocalTime horaInicio=LocalTime.ofInstant(inicioUso, ZoneOffset.UTC);
        if(horaInicio.isAfter(LocalTime.of(18,0)) && horaInicio.isBefore(LocalTime.of(22,0))){
            return base.multiply(BigDecimal.ONE.add(porCentajeExtra));
        }
        return base;
    }
}
