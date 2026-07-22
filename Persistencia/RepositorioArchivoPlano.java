package Persistencia;

import domain.MesaBillar;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RepositorioArchivoPlano {
    private static final String ARCHIVO = "billar_state.ser";

    @SuppressWarnings("unchecked")
    public Map<String, MesaBillar> carga(){
        File file = new File(ARCHIVO);
        if(!file.exists()){
            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<String, MesaBillar>) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.err.println("Error al cargar, iniciando vacio: " + e.getMessage());
            return new HashMap<>();
        }
    }

    public void guardar(Map<String, MesaBillar> mesas){
        try(ObjectOutputStream oss = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oss.writeObject(mesas);
        } catch (IOException e) {
            System.err.println("Error al guardar: "+ e.getMessage());
        }
    }
}
