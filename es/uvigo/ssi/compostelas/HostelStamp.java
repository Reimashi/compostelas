package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import org.json.simple.JSONObject;

/**
 * Sello digital de una compostela emitido por un hostal
 */
public class HostelStamp extends Stamp {
    private long creationTime;
    private String place;
    
    private HostelStamp (String owner, byte[] sign) {
        super(owner, sign);
    }
    
    private HostelStamp (Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        super(signer, pil);
    }
    
    /**
     * Obtine la fecha en la que fue emitida la compostela.
     * @return 
     */
    public Date getCreationTime() {
        return new Date(this.creationTime);
    }
    
    private void setCreationTime(long timestamp) {
        this.creationTime = timestamp;
    }
    
    /**
     * Obtiene el lugar en el que se firmó la compostela.
     * @return 
     */
    public String getCreationPlace() {
        return this.place;
    }
    
    public void setCreationPlace(String place) {
        this.place = place;
    }
    
    /**
     * Crea un HostelStamp obteniendo la información desde la consola.
     * @param signer Información de cifrado del peregrino.
     * @param pil Información codificada del peregrino.
     * @return Sello emitido por un hostal.
     * @throws DecodeException
     * @throws EncodeException 
     */
    public static HostelStamp fromCMD(Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        HostelStamp s = new HostelStamp(signer, pil);
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== Form: Hostel stamp ===");
        
        System.out.println("Hostel place:");
        s.setCreationPlace(sc.nextLine().trim());
        
        s.setCreationTime(System.currentTimeMillis());
        
        return s;
    }
    
    /**
     * Serializa HotelStamp en un objeto JSON.
     * @return Objeto HotelStamp serializado en JSON.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject st = super.toJSON();
        
        st.put("creationtime", this.creationTime);
        st.put("creationplace", this.place);
        
        return st;
    }
    
    /**
     * Obtiene un objeto de tipo HotelStamp desde un objeto JSON.
     * @param json Objeto HotelStamp serializado en JSON.
     * @return Objeto HotelStamp des-serializado.
     */
    public static HostelStamp fromJSON(JSONObject json) {
        HostelStamp hs = new HostelStamp((String) json.get("signer"), 
                Base64.getDecoder().decode((String) json.get("sign")));
        hs.setCreationTime((Long) json.get("creationtime"));
        hs.setCreationPlace((String) json.get("creationplace"));
        return hs;
    }
    
    @Override
    public String toString() {
        StringBuilder lines = new StringBuilder();
        
        lines.append("=== Hostel Information ===").append(System.lineSeparator());
        lines.append("Creation place: ").append(this.getCreationPlace()).append(System.lineSeparator());
        lines.append("Creation time: ").append(this.getCreationTime().toString()).append(System.lineSeparator());
        lines.append("==========================");
        
        return lines.toString();
    }
}
