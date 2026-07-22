package domain;

import java.time.Duration;
import java.time.Instant;
import java.io.Serializable;

public record IntervaloTiempo(Instant inicio, Instant fin) implements Serializable{
    private static final long serialVersionUID = 1L;

    public Duration duracion(){
        return Duration.between(inicio, fin);
    }

    public boolean seSolapaCon(IntervaloTiempo otro){
        return this.inicio.isBefore(otro.fin) && otro.inicio.isAfter(this.fin);
    }
}