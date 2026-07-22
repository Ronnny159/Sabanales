package domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import cobro.PoliticaCosto;

public class MesaBillar implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private EstadoMesa estado;
    private final Map<String, CuentaJugador> cuentasActivas;
    private final List<CuentaJugador> historialCuentas;
    private transient PropertyChangeSupport soporte;

    public MesaBillar(String id) {
        this.id = id;
        this.estado = EstadoMesa.LIBRE;
        this.cuentasActivas = new HashMap<>();
        this.historialCuentas = new ArrayList<>();
        this.soporte = new PropertyChangeSupport(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.soporte = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener listener) {
        soporte.addPropertyChangeListener(listener);
    }

    public void removeObserver(PropertyChangeListener listener) {
        soporte.removePropertyChangeListener(listener);
    }

    private void notificarCambio(String propiedad, Object viejo, Object nuevo) {
        soporte.firePropertyChange(propiedad, viejo, nuevo);
    }

    public void agregarJugadores(List<String> nombres) {
        if (this.estado == EstadoMesa.MANTENIMIENTO) {
            throw new IllegalStateException("Mesa en mantenimiento");
        }
        if (nombres.size() > 5) {
            throw new IllegalArgumentException("Máximo 5 jugadores por mesa");
        }

        long activos = cuentasActivas.values().stream()
                .filter(c -> !c.isCerrada())
                .count();
        
        if (activos + nombres.size() > 5) {
            throw new IllegalStateException("Ya hay " + activos + " jugadores activos. Máximo 5");
        }

        for (String nombre : nombres) {
            if (cuentasActivas.containsKey(nombre)) {
                throw new IllegalArgumentException("El jugador " + nombre + " ya está en la mesa");
            }
            Jugador jugador = new Jugador(nombre);
            CuentaJugador cuenta = new CuentaJugador(jugador);
            cuentasActivas.put(nombre, cuenta);
        }

        if (this.estado != EstadoMesa.OCUPADA) {
            EstadoMesa viejo = this.estado;
            this.estado = EstadoMesa.OCUPADA;
            notificarCambio("estado", viejo, this.estado);
        }
        notificarCambio("jugadores", null, nombres);
    }

    public CuentaJugador retirarJugador(String nombre, Instant horaSalida) {
        CuentaJugador cuenta = cuentasActivas.get(nombre);
        if (cuenta == null) {
            throw new IllegalArgumentException("Jugador no encontrado: " + nombre);
        }
        if (cuenta.isCerrada()) {
            throw new IllegalStateException("La cuenta ya está cerrada");
        }

        Duration tiempoJugado = Duration.between(cuenta.getInicioJuego(), horaSalida);
        cuenta.agregarTiempo(tiempoJugado);
        cuenta.cerrarCuenta();

        historialCuentas.add(cuenta);
        cuentasActivas.remove(nombre);

        if (cuentasActivas.isEmpty()) {
            EstadoMesa viejo = this.estado;
            this.estado = EstadoMesa.LIBRE;
            notificarCambio("estado", viejo, this.estado);
        }

        notificarCambio("jugador_retirado", null, nombre);
        return cuenta;
    }

    public void registrarAbono(String nombre, BigDecimal monto) {
        CuentaJugador cuenta = cuentasActivas.get(nombre);
        if (cuenta == null) {
            throw new IllegalArgumentException("Jugador no encontrado: " + nombre);
        }
        if (cuenta.isCerrada()) {
            throw new IllegalStateException("La cuenta ya está cerrada");
        }
        cuenta.registrarAbono(monto);
        notificarCambio("abono", null, nombre + ":$" + monto);
    }

    public BigDecimal calcularDeudaJugador(String nombre, PoliticaCosto politica) {
        CuentaJugador cuenta = cuentasActivas.get(nombre);
        if (cuenta == null) {
            throw new IllegalArgumentException("Jugador no encontrado: " + nombre);
        }
        return cuenta.calcularDeuda(politica);
    }

    public List<String> getJugadoresActivos() {
        return cuentasActivas.keySet().stream()
                .filter(n -> !cuentasActivas.get(n).isCerrada())
                .collect(Collectors.toList());
    }

    public List<CuentaJugador> getHistorial() {
        return new ArrayList<>(historialCuentas);
    }

    public Map<String, CuentaJugador> getCuentasActivas() {
        return new HashMap<>(cuentasActivas);
    }

    public List<CuentaJugador> cerrarMesa(Instant horaCierre) {
        List<CuentaJugador> cuentasCerradas = new ArrayList<>();
        for (String nombre : new ArrayList<>(cuentasActivas.keySet())) {
            CuentaJugador cuenta = retirarJugador(nombre, horaCierre);
            cuentasCerradas.add(cuenta);
        }
        this.estado = EstadoMesa.LIBRE;
        notificarCambio("estado", EstadoMesa.OCUPADA, EstadoMesa.LIBRE);
        return cuentasCerradas;
    }

    public void ocupar(String cliente, Instant inicio) {
        // Redirigir al nuevo sistema: crear jugador único
        List<String> jugadores = List.of(cliente);
        agregarJugadores(jugadores);
    }

    public IntervaloTiempo liberar(Instant fin) {
        cerrarMesa(fin);
        return new IntervaloTiempo(fin.minusSeconds(1), fin);
    }

    public void ponerMantenimiento() {
        if (this.estado == EstadoMesa.OCUPADA) {
            throw new IllegalStateException("No se puede poner en mantenimiento si está ocupada");
        }
        EstadoMesa viejo = this.estado;
        this.estado = EstadoMesa.MANTENIMIENTO;
        notificarCambio("estado", viejo, this.estado);
    }

    public void reparar() {
        if (this.estado != EstadoMesa.MANTENIMIENTO) {
            throw new IllegalStateException("No está en mantenimiento");
        }
        EstadoMesa viejo = this.estado;
        this.estado = EstadoMesa.LIBRE;
        notificarCambio("estado", viejo, this.estado);
    }

    public String getId() { return id; }
    public EstadoMesa getEstado() { return estado; }
    public String getClienteActual() {
        // Para compatibilidad
        return cuentasActivas.isEmpty() ? null : 
               String.join(", ", cuentasActivas.keySet());
    }
    public IntervaloTiempo getIntervaloActual() {
        if (cuentasActivas.isEmpty()) return null;
        CuentaJugador primera = cuentasActivas.values().iterator().next();
        return new IntervaloTiempo(primera.getInicioJuego(), 
                                   primera.getFinJuego() != null ? primera.getFinJuego() : Instant.now());
    }
    public int getCantidadJugadores() {
        return (int) cuentasActivas.values().stream()
                .filter(c -> !c.isCerrada())
                .count();
    }
}