package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.FileNotFoundException;
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
import org.bouncycastle.util.Arrays;
import org.json.simple.JSONObject;

/**
 * Sello digital de una compostela
 */
public class Stamp {
    private static final Logger _log = Logger.getLogger(Stamp.class.getName());
    
    protected final String owner;
    protected final byte[] sign;
    
    protected Stamp (String owner, byte[] sign) {
        this.owner = owner;
        this.sign = sign;
    }
    
    public Stamp (Signer signer, PilgrimEncoded pil) throws DecodeException, EncodeException {
        this.owner = signer.getName();
        this.sign = this.calculateSign(signer, pil.getSign());
    }
    
    /**
     * Comprueba la firma digital de un sello.
     * @param pil Información del peregrino.
     * @return true si es correcta; false si es erronea.
     * @throws DecodeException
     * @throws EncodeException 
     */
    public boolean checkStamp(PilgrimEncoded pil) throws DecodeException, EncodeException {
        try {
            Signer stampsig = Signer.loadFromFile(this.owner);
            return Arrays.areEqual(this.sign, this.calculateSign(stampsig, pil.getSign()));
        } catch (FileNotFoundException ex) {
            Stamp._log.log(Level.SEVERE, null, ex);
            throw new DecodeException("Error while open signer key file to chech a Stamp. " + ex.getMessage());
        }
    }
    
    /**
     * Serializa Stamp en un objeto JSON.
     * @return Objeto Stamp serializado en JSON.
     */
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("signer", this.owner);
        jsonObj.put("sign", Base64.getEncoder().encodeToString(sign));
        
        return jsonObj;
    }
    
    /**
     * Obtiene un objeto de tipo Stamp desde un objeto JSON.
     * @param joc Objeto Stamp serializado en JSON.
     * @return Objeto Stamp des-serializado.
     */
    public static Stamp fromJSON (JSONObject joc) {
        return new Stamp((String) joc.get("signer"), 
                Base64.getDecoder().decode((String) joc.get("sign")));
    }
    
    /**
     * Calcula la firma digital de la compostela para un signer (albergue). A partir del hash?
     * @param signer Usuario que firma la compostela
     * @param hash Datos a firmar
     * @return Firma digital
     */
    private byte[] calculateSign(Signer signer, byte[] hash) throws DecodeException, EncodeException {
        try {
            Cipher cifrador = Cipher.getInstance("RSA", "BC");
            cifrador.init(Cipher.ENCRYPT_MODE, signer.getKey().getPrivate());
            return cifrador.doFinal(hash);
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new EncodeException("Stamp can't calculate the sign. " + ex.getMessage());
        }
    }
}
