package domain;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.time.Instant;

public class MesaBillar implements Serializable{
    private static final long serialVersionUID = 1L;

    private final String id;
    private EstadoMesa estado;
    private IntervaloTiempo intervaloActual;
    private String clienteActual;
    private transient PropertyChangeSupport soporte;

    public MesaBillar(String id){
        this.id=id;
        this.estado=EstadoMesa.LIBRE;
        this.soporte=new PropertyChangeSupport(this);
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException{
        in.defaultReadObject();
        this.soporte = new PropertyChangeSupport(this);
    }

    public void addObserver(PropertyChangeListener listener){
        soporte.addPropertyChangeListener(listener);
    }

    public void removeObserver(PropertyChangeListener listener){
        soporte.removePropertyChangeListener(listener);
    }

    public void notificarCambio(String propiedad, Object viejo, Object nuevo){
        soporte.firePropertyChange(propiedad, viejo, nuevo);
    }

    public void ocupar(String cliente, Instant inicio){
        if(this.estado != EstadoMesa.LIBRE && this.estado != EstadoMesa.RESERVADA){
            throw new IllegalStateException("Mesa no disponible para ocupar");
        }
        EstadoMesa viejo = this.estado;
        this.estado=EstadoMesa.OCUPADA;
        this.clienteActual=cliente;
        this.intervaloActual=new IntervaloTiempo(inicio,null);
        notificarCambio("estado", viejo, this.estado);
        notificarCambio("cliente", null, cliente);
    }

    public IntervaloTiempo liberar(Instant fin){
        if(this.estado != EstadoMesa.OCUPADA){
            throw new IllegalStateException("La mesa no esta ocupada");
        }
        IntervaloTiempo completo=new IntervaloTiempo(this.intervaloActual.inicio(), fin);
        EstadoMesa viejo= this.estado;
        this.estado=EstadoMesa.LIBRE;
        String clienteViejo=this.clienteActual;
        this.clienteActual=null;
        this.intervaloActual=null;
        notificarCambio("estado", viejo, this.estado);
        notificarCambio("cliente", clienteViejo, null);
        return completo;
    }

    public void reservar(String cliente,IntervaloTiempo intervalo){
        if(this.estado == EstadoMesa.OCUPADA || this.estado == EstadoMesa.MANTENIMIENTO){
            throw new IllegalStateException("No se puede reservar ahora");
        }
        EstadoMesa viejo = this.estado;
        this.estado=EstadoMesa.RESERVADA;
        this.clienteActual=cliente;
        this.intervaloActual=intervalo;
        notificarCambio("estado", viejo, this.estado);
        notificarCambio("cliente", null, cliente);
    }

    public void cancelarReservar(){
        if(this.estado != EstadoMesa.RESERVADA){
            throw new IllegalStateException("No hay reserva activa");
        }
        EstadoMesa viejo = this.estado;
        this.estado=EstadoMesa.LIBRE;
        String clienteViejo = this.clienteActual;
        this.clienteActual=null;
        this.intervaloActual=null;
        notificarCambio("estado", viejo, this.estado);
        notificarCambio("cliente", clienteViejo, null);
    }

    public void ponerMantenimiento(){
        EstadoMesa viejo=this.estado;
        this.estado=EstadoMesa.MANTENIMIENTO;
        this.clienteActual=null;
        this.intervaloActual=null;
        notificarCambio("estado", viejo, this.estado);
        notificarCambio("cliente", null, null);
    }

    public void reparar(){
        if(this.estado != EstadoMesa.MANTENIMIENTO){
            throw new IllegalStateException("No esta en mantenimiento");
        }
        EstadoMesa viejo = this.estado;
        this.estado=EstadoMesa.LIBRE;
        notificarCambio("estado", viejo, this.estado);
    }

    public String getId(){return id;}
    public EstadoMesa getEstado(){return estado;}
    public IntervaloTiempo getIntervaloActual(){return intervaloActual;}
    public String getClienteActual(){return clienteActual;}
}