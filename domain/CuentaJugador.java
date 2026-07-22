package domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import cobro.PoliticaCosto;

public class CuentaJugador implements Serializable {
    private static final long serialVersionUID=1L;

    private Jugador jugador = null;
    private Instant inicioJuego;
    private Instant finJuego;
    private BigDecimal tiempoAcumulado;
    private BigDecimal totalPagado;
    private List<Abono> abonos;
    private boolean cerrada;

    public CuentaJugador(Jugador jugador) {
        this.jugador=jugador;
        this.inicioJuego=Instant.now();
        this.tiempoAcumulado=BigDecimal.ZERO;
        this.totalPagado=BigDecimal.ZERO;
        this.abonos=new ArrayList<>();
        this.cerrada=false;
    }
    
    public void agregarTiempo(Duration duracion){
        if(!cerrada){
            double minutos=duracion.toMinutes();
            this.tiempoAcumulado=this.tiempoAcumulado.add(BigDecimal.valueOf(minutos));
            this.finJuego=Instant.now();
        }
    }

    public void registrarAbono(BigDecimal monto){
        if(!cerrada){
            this.totalPagado=this.totalPagado.add(monto);
            this.abonos.add(new Abono(monto, Instant.now()));
        }
    }

    public BigDecimal calcularDeuda(PoliticaCosto politica){
        if(cerrada) return BigDecimal.ZERO;

        Duration tiempoJugado = Duration.ofMinutes(tiempoAcumulado.longValue());
        BigDecimal costoTotal = politica.calcular(tiempoJugado);
        return costoTotal.subtract(totalPagado).max(BigDecimal.ZERO);
    }

    public void cerrarCuenta() {
        this.cerrada = true;
        this.finJuego = Instant.now();
        jugador.registrarSalida();
    }

    public Jugador getJugador() { return jugador; }
    public Instant getInicioJuego() { return inicioJuego; }
    public Instant getFinJuego() { return finJuego; }
    public BigDecimal getTiempoAcumulado() { return tiempoAcumulado; }
    public BigDecimal getTotalPagado() { return totalPagado; }
    public List<Abono> getAbonos() { return new ArrayList<>(abonos); }
    public boolean isCerrada() { return cerrada; }

    public static class Abono implements Serializable {
        private static final long serialVersionUID = 1L;
        private final BigDecimal monto;
        private final Instant fecha;

        public Abono(BigDecimal monto, Instant fecha) {
            this.monto = monto;
            this.fecha = fecha;
        }

        public BigDecimal getMonto() { return monto; }
        public Instant getFecha() { return fecha; }
    }
}
