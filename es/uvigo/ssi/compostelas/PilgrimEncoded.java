package es.uvigo.ssi.compostelas;

import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.util.Base64;
import org.json.simple.JSONObject;
import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;

public class PilgrimEncoded {
    private static final int KEY_SIZE = 192;
    private static final byte[] IV_BYTES = new byte[] { 
                        0x00, 0x00, 0x00, 0x01, 0x04, 0x05, 0x06, 0x07,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
    
    private final String signerName;
    private final byte[] pilgrimdata;
    private final byte[] encryptkey;
    private final byte[] sign;
    
    private PilgrimEncoded (String signerName, byte[] pilgrimdata, byte[] encryptkey, byte[] sign) {
        this.signerName = signerName;
        this.pilgrimdata = pilgrimdata;
        this.encryptkey = encryptkey;
        this.sign = sign;
    }
    
    public PilgrimEncoded (Pilgrim p, Signer office, Signer pilgrim) throws DecodeException, EncodeException {
        this.signerName = office.getName();
        
        try {
            // Generamos llave para cifrado simetrico
            KeyGenerator generadorAES = KeyGenerator.getInstance("AES", "BC");
            generadorAES.init(PilgrimEncoded.KEY_SIZE);
            Key aesKey = generadorAES.generateKey();
            
            // Ciframos el peregrino con cifrado simetrico AES
            Cipher cipherAes = Cipher.getInstance("AES", "BC");
            IvParameterSpec ivSpec  = new IvParameterSpec(PilgrimEncoded.IV_BYTES);
            cipherAes.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            this.pilgrimdata = cipherAes.doFinal(p.toJSON().toJSONString().getBytes());
            
            // Ciframos la clave asimetricamente RSA
            Cipher cipherRsaPub = Cipher.getInstance("RSA", "BC");
            cipherRsaPub.init(Cipher.ENCRYPT_MODE, office.getKey().getPublic());
            this.encryptkey = cipherRsaPub.doFinal(aesKey.getEncoded());
            
            // Firma digital del peregrino
            byte[] hash = PilgrimEncoded.getSHA1Hash(p.toJSON().toJSONString().getBytes());
            Cipher cipherRsaPri = Cipher.getInstance("RSA", "BC");
            cipherRsaPri.init(Cipher.ENCRYPT_MODE, pilgrim.getKey().getPrivate());
            this.sign = cipherRsaPri.doFinal(hash);
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | 
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new EncodeException("PilgrimEncoded can't calculate the sign. " + ex.getMessage());
        }
    }
    
    public Pilgrim decrypt(Signer s) {
        if (s.getName().equals(this.signerName)) {
            
        }
        
        throw new UnsupportedOperationException();
    }
    
    public String getSignerName() {
        return this.signerName;
    }
    
    public static byte[] getSHA1Hash(PilgrimEncoded pil) throws DecodeException {
        return PilgrimEncoded.getSHA1Hash(pil.pilgrimdata);
    }
    
    private static byte[] getSHA1Hash(byte[] pil) throws DecodeException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA", "BC");
            messageDigest.update(pil);
            return messageDigest.digest();
        } catch (NoSuchProviderException|NoSuchAlgorithmException ex) {
            throw new DecodeException ("PilgrimEnconded can't be hashed. " + ex.getMessage());
        }
    }
    
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("signer", this.signerName);
        jsonObj.put("data", Base64.getEncoder().encode(this.pilgrimdata));
        jsonObj.put("encryptkey", Base64.getEncoder().encode(this.encryptkey));
        jsonObj.put("sign", Base64.getEncoder().encode(this.sign));
        
        return jsonObj;
    }
    
    public static PilgrimEncoded fromJSON (JSONObject ob) {
        return new PilgrimEncoded((String) ob.get("signer"),
                Base64.getDecoder().decode((String) ob.get("data")),
                Base64.getDecoder().decode((String) ob.get("encryptkey")),
                Base64.getDecoder().decode((String) ob.get("sign")));
    }
}
