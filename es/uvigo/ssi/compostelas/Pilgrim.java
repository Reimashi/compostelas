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
    
    public String getName() {
        return this.name;
    }
    
    public NIF getDNI() {
        return this.dni;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public String getMotivations() {
        return this.motivations;
    }
    
    public Date getCreationTime() {
        return new Date(this.creationTime);
    }
    
    public String getCreationPlace() {
        return this.creationPlace;
    }
    
    /**
     * Crea un objeto peregrino recolectando la información por la linea de comandos
     * @return 
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
}
