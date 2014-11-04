package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.util.Base64;
import java.util.Date;
import java.util.Scanner;
import org.json.simple.JSONObject;

public class HostelStamp extends Stamp {
    private long creationTime;
    private String place;
    
    private HostelStamp (String owner, byte[] sign) {
        super(owner, sign);
    }
    
    private HostelStamp (Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        super(signer, pil);
    }
    
    public Date getCreationTime() {
        return new Date(this.creationTime);
    }
    
    private void setCreationTime(long timestamp) {
        this.creationTime = timestamp;
    }
    
    public String getCreationPlace() {
        return this.place;
    }
    
    public void setCreationPlace(String place) {
        this.place = place;
    }
    
    public static HostelStamp fromCMD(Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        HostelStamp s = new HostelStamp(signer, pil);
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("=== Form: Hostel stamp ===");
        
        System.out.println("Hostel place:");
        s.setCreationPlace(sc.nextLine().trim());
        
        s.setCreationTime(System.currentTimeMillis());
        
        return s;
    }
    
    @Override
    public JSONObject toJSON() {
        JSONObject st = super.toJSON();
        
        st.put("creationtime", this.creationTime);
        st.put("creationplace", this.place);
        
        return st;
    }
    
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
