package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.json.simple.JSONObject;

/**
 * Sello digital de una compostela
 */
public class Stamp implements Serializable {
    private final String owner;
    private final byte[] sign;
    
    private Stamp (String owner, byte[] sign) {
        this.owner = owner;
        this.sign = sign;
    }
    
    public Stamp (Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        this.owner = signer.getName();
        this.sign = this.calculateSign(signer, pil);
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("signer", this.owner);
        jsonObj.put("sign", Base64.getEncoder().encode(this.sign));
        
        return jsonObj;
    }
    
    public static Stamp fromJSON (JSONObject joc) {
        return new Stamp((String) joc.get("signer"), 
                Base64.getDecoder().decode((String) joc.get("sign")));
    }
    
    /**
     * Calcula la firma digital de la compostela para un signer (albergue). A partir del hash?
     * @param signer Usuario que firma la compostela
     * @param pil Información del peregrino
     * @return Firma digital
     */
    private byte[] calculateSign(Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        try {
            byte[] hash = PilgrimEncoded.getSHA1Hash(pil);
            
            Cipher cifrador = Cipher.getInstance("RSA", "BC");
            cifrador.init(Cipher.ENCRYPT_MODE, signer.getKey().getPrivate());
            return cifrador.doFinal(hash);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new EncodeException("Stamp can't calculate the sign. " + ex.getMessage());
        }
    }
}
