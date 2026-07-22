package Service;

import cobro.*;
import domain.*;
import Persistencia.RepositorioArchivoPlano;

import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.*;

public class ServicioBillar {
    private final Map<String, MesaBillar> mesas;
    private final RepositorioArchivoPlano repositorio;
    private final Clock reloj;
    private final PoliticaCosto politicaCostoBase;
    private final BigDecimal precioBase;

    // Observadores
    private final PropertyChangeListener observadorPersistencia;
    private final PropertyChangeListener observadorActualizacionUI;

    public ServicioBillar(Clock reloj, BigDecimal precioBasePorHora) {
        this.reloj = reloj;
        this.precioBase = precioBasePorHora;
        this.repositorio = new RepositorioArchivoPlano();
        this.mesas = repositorio.carga();
        this.politicaCostoBase = new TarifaBase(precioBasePorHora);

        this.observadorPersistencia = evt -> {
            System.out.println("[Persistencia] Cambio en mesa " + ((MesaBillar) evt.getSource()).getId());
            repositorio.guardar(mesas);
        };

        this.observadorActualizacionUI = evt -> {
            // Se conectará con la UI cuando esté disponible
        };

        mesas.values().forEach(this::registrarObservadores);
    }

    private void registrarObservadores(MesaBillar mesa) {
        mesa.addObserver(observadorPersistencia);
        mesa.addObserver(observadorActualizacionUI);
    }

    public void setObservadorUI(PropertyChangeListener observadorUI) {
        mesas.values().forEach(mesa -> {
            mesa.removeObserver(this.observadorActualizacionUI);
            mesa.addObserver(observadorUI);
        });
    }


    public void agregarJugadoresAMesa(String mesaId, List<String> nombres) {
        MesaBillar mesa = obtenerMesa(mesaId);
        mesa.agregarJugadores(nombres);
        repositorio.guardar(mesas);
    }

    public CuentaJugador retirarJugador(String mesaId, String nombre) {
        MesaBillar mesa = obtenerMesa(mesaId);
        CuentaJugador cuenta = mesa.retirarJugador(nombre, reloj.instant());
        
        PoliticaCosto politicaConRecargos = crearPoliticaConRecargos(cuenta.getInicioJuego());
        BigDecimal deuda = cuenta.calcularDeuda(politicaConRecargos);
        
        repositorio.guardar(mesas);
        return cuenta;
    }

    public void registrarAbono(String mesaId, String nombre, BigDecimal monto) {
        MesaBillar mesa = obtenerMesa(mesaId);
        mesa.registrarAbono(nombre, monto);
        repositorio.guardar(mesas);
    }

    public BigDecimal calcularDeudaJugador(String mesaId, String nombre) {
        MesaBillar mesa = obtenerMesa(mesaId);
        PoliticaCosto politicaConRecargos = crearPoliticaConRecargos(reloj.instant());
        return mesa.calcularDeudaJugador(nombre, politicaConRecargos);
    }

    public List<CuentaJugador> cerrarMesaCompleta(String mesaId) {
        MesaBillar mesa = obtenerMesa(mesaId);
        List<CuentaJugador> cuentas = mesa.cerrarMesa(reloj.instant());
        repositorio.guardar(mesas);
        return cuentas;
    }

    public List<CuentaJugador> getHistorialMesa(String mesaId) {
        MesaBillar mesa = obtenerMesa(mesaId);
        return mesa.getHistorial();
    }

    public Map<String, CuentaJugador> getJugadoresActivos(String mesaId) {
        MesaBillar mesa = obtenerMesa(mesaId);
        return mesa.getCuentasActivas();
    }

    private PoliticaCosto crearPoliticaConRecargos(Instant inicio) {
        return new RecargoHorarioPuntaDecorator(
                new RecargFinDeSemanaDecorator(politicaCostoBase, inicio, BigDecimal.valueOf(0.15)),
                inicio, BigDecimal.valueOf(0.20)
        );
    }

    public void crearMesa(String id) {
        if (mesas.containsKey(id)) {
            throw new IllegalArgumentException("La mesa ya existe");
        }
        MesaBillar nueva = new MesaBillar(id);
        registrarObservadores(nueva);
        mesas.put(id, nueva);
        repositorio.guardar(mesas);
    }

    public void eliminarMesa(String id) {
        if (!mesas.containsKey(id)) {
            throw new IllegalArgumentException("La mesa no existe");
        }
        mesas.remove(id);
        repositorio.guardar(mesas);
    }

    public void ocuparMesa(String id, String cliente) {
        MesaBillar mesa = obtenerMesa(id);
        mesa.ocupar(cliente, reloj.instant());
    }

    public BigDecimal liberarMesa(String id) {
        MesaBillar mesa = obtenerMesa(id);
        IntervaloTiempo usado = mesa.liberar(reloj.instant());
        PoliticaCosto politicaConRecargos = crearPoliticaConRecargos(usado.inicio());
        BigDecimal total = politicaConRecargos.calcular(usado.duracion());
        return total;
    }

    public void ponerMantenimiento(String id) {
        MesaBillar mesa = obtenerMesa(id);
        mesa.ponerMantenimiento();
    }

    public void repararMesa(String id) {
        MesaBillar mesa = obtenerMesa(id);
        mesa.reparar();
    }

    public Map<String, MesaBillar> getMesas() {
        return new HashMap<>(mesas);
    }

    public MesaBillar obtenerMesa(String id) {
        MesaBillar m = mesas.get(id);
        if (m == null) throw new IllegalArgumentException("Mesa no existe");
        return m;
    }

    public Clock getReloj() { return reloj; }
    public BigDecimal getPrecioBase() { return precioBase; }

    public void guardarEstado() {
        repositorio.guardar(mesas);
    }

    public String getResumenMesa(String id) {
        MesaBillar mesa = obtenerMesa(id);
        StringBuilder sb = new StringBuilder();
        sb.append("Mesa ").append(id).append(" - ");
        sb.append(mesa.getEstado()).append("\n");
        
        Map<String, CuentaJugador> activos = mesa.getCuentasActivas();
        if (!activos.isEmpty()) {
            sb.append("Jugadores activos:\n");
            for (Map.Entry<String, CuentaJugador> entry : activos.entrySet()) {
                CuentaJugador cuenta = entry.getValue();
                if (!cuenta.isCerrada()) {
                    sb.append("  • ").append(entry.getKey())
                      .append(" - Tiempo: ").append(cuenta.getTiempoAcumulado())
                      .append(" min - Pagado: $").append(cuenta.getTotalPagado())
                      .append("\n");
                }
            }
        }
        
        List<CuentaJugador> historial = mesa.getHistorial();
        if (!historial.isEmpty()) {
            sb.append("Historial reciente:\n");
            historial.stream()
                .sorted((a, b) -> b.getFinJuego().compareTo(a.getFinJuego()))
                .limit(3)
                .forEach(c -> {
                    sb.append("  • ").append(c.getJugador().getNombre())
                      .append(" - ").append(c.getTiempoAcumulado())
                      .append(" min - Total: $").append(c.getTotalPagado())
                      .append("\n");
                });
        }
        return sb.toString();
    }
}
