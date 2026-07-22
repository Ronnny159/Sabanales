package domain;

import java.io.Serializable;
import java.time.Instant;

public class Jugador implements Serializable{
    private static final long serialVersionUID=1L;

    private final String nombre;
    private final Instant horaEntrada;
    private Instant horaSalida;
    private boolean activo;

    public Jugador(String nombre){
        this.nombre=nombre;
        this.horaEntrada=Instant.now();
        this.activo=true;
    }

    public String getNombre(){return nombre;}
    public Instant getHoraEntrada(){return horaEntrada;}
    public Instant getHoraSalida(){return horaSalida;}
    public boolean isActivo(){return activo;}

    public void registrarSalida(){
        this.horaSalida=Instant.now();
        this.activo=false;
    }
    @Override
    public String toString(){
        return nombre +(activo?" (ACTIVO)":" (RETIRADO)");
    }
}
