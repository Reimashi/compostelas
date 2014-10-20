package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.types.NIF;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 * Representa la información de un peregrino
 */
public class Pilgrim {
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
    
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("name", this.name);
        jsonObj.put("nif", this.dni);
        jsonObj.put("address", this.address);
        jsonObj.put("motivations", this.motivations);
        jsonObj.put("creationtime", this.creationTime);
        jsonObj.put("creationplace", this.creationPlace);
        
        return jsonObj;
    }
    
    /**
     * Convierte a cadena de caracteres la información de un peregrino.
     * @return Cadena de caracteres con la información del peregrino.
     */
    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        
        lines.append("=== Pilgrim Information ===").append(System.lineSeparator());
        lines.append("Name: ").append(this.getName()).append(System.lineSeparator());
        lines.append("NIF: ").append(this.getNIF().toString()).append(System.lineSeparator());
        lines.append("Address: ").append(this.getAddress()).append(System.lineSeparator());
        lines.append("Motivations: ").append(this.getMotivations()).append(System.lineSeparator());
        lines.append("==========================");
        lines.append("Certification office: ").append(this.getCreationPlace()).append(System.lineSeparator());
        lines.append("Creation time: ").append(this.getCreationTime().toString()).append(System.lineSeparator());
        lines.append("==========================");
        
        return lines.toString();
    }
}
