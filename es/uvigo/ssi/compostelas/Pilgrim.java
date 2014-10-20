package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.types.NIF;
import java.io.Serializable;
import java.util.Date;
import java.util.Scanner;

/**
 * Representa la información de un peregrino
 */
public class Pilgrim implements Serializable {
    private String name;
    private NIF dni;
    private String address;
    private String motivations;
    private long creationTime;
    private String creationPlace;
    
    private Pilgrim() {}
    
    /**
     * Obtiene el nombre del peregrino.
     * @return 
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Obtiene el documento NIF del peregrino.
     * @return 
     */
    public NIF getNIF() {
        return this.dni;
    }
    
    /**
     * Obtiene la dirección del peregrino.
     * @return 
     */
    public String getAddress() {
        return this.address;
    }
    
    /**
     * Obtiene las motivaciones del peregrino para emprender el viaje.
     * @return 
     */
    public String getMotivations() {
        return this.motivations;
    }
    
    /**
     * Obtiene la fecha en la que se creó el peregrino.
     * @return 
     */
    public Date getCreationTime() {
        return new Date(this.creationTime);
    }
    
    /**
     * Obtiene el nombre de la entidad que creó el peregrino.
     * @return 
     */
    public String getCreationPlace() {
        return this.creationPlace;
    }
    
    /**
     * Crea un objeto peregrino recolectando la información por la linea de comandos.
     * @return Objeto peregrino creado.
     */
    public static Pilgrim fromCMD() {
        Pilgrim pl = new Pilgrim();
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== Form: Pilgrim registration ===");
        
        System.out.println("Name (Lastname, Firstname):");
        pl.name = sc.nextLine().trim();
        
        System.out.println("NIF (Only number):");
        boolean dnit = false;
        while (!dnit) {
            try {
                int dninum = Integer.parseInt(sc.nextLine().trim());
                pl.dni = new NIF(dninum);
                dnit = true;
            }
            catch (NumberFormatException en) {
                System.err.println("The number has introduced is invalid (Must have 8 digits).");
            }
        }
        
        System.out.println("Address:");
        pl.address = sc.nextLine().trim();
        
        System.out.println("Motivations:");
        pl.motivations = sc.nextLine().trim();
        
        System.out.println("Certification office name:");
        pl.creationPlace = sc.nextLine().trim();
        
        pl.creationTime = System.currentTimeMillis();
        
        return pl;
    }
    
    /**
     * Convierte a cadena de caracteres la información de un peregrino.
     * @return Cadena de caracteres con la información del peregrino.
     */
    @Override
    public String toString() {
        return "=== Pilgrim Information ===" + System.lineSeparator() +
                "Name: " + this.getName() + System.lineSeparator() +
                "NIF: " + this.getNIF().toString() + System.lineSeparator() +
                "Address: " + this.getAddress() + System.lineSeparator() +
                "Motivations: " + this.getMotivations() + System.lineSeparator() +
                "==========================" +
                "Certification office: " + this.getCreationPlace() + System.lineSeparator() +
                "Creation time: " + this.getCreationTime().toString() + System.lineSeparator() +
                "==========================";
    }
}
