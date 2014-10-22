package es.uvigo.ssi.compostelas;

import java.security.MessageDigest;
import java.security.NoSuchProviderException;
import java.util.Base64;
import org.json.simple.JSONObject;
import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.IOException;
import java.io.StringReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.json.simple.parser.JSONParser;

/**
 * Representa los datos encriptados de un peregrino. (Ver clase Pilgrim)
 */
public class PilgrimEncoded {
    private static final String CRYPTO_PROVIDER = "BC";
    private static final int KEY_SIZE = 128;
    private static final byte[] IV_BYTES = new byte[] { 
                        0x00, 0x00, 0x00, 0x01, 0x04, 0x05, 0x06, 0x07,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01 };
    
    private static final Logger _log = Logger.getLogger(PilgrimEncoded.class.getName());
    
    private final String signerName;
    private final String pilSignerName;
    private final byte[] pilgrimdata;
    private final byte[] encryptkey;
    private final byte[] sign;
    
    private PilgrimEncoded (String signerName, String pilSignerName, byte[] pilgrimdata, byte[] encryptkey, byte[] sign) {
        this.signerName = signerName;
        this.pilSignerName = pilSignerName;
        this.pilgrimdata = pilgrimdata;
        this.encryptkey = encryptkey;
        this.sign = sign;
    }
    
    public PilgrimEncoded (Pilgrim p, Signer office, Signer pilgrim) throws DecodeException, EncodeException {
        this.signerName = office.getName();
        this.pilSignerName = pilgrim.getName();
        
        try {
            // Generamos llave para cifrado simetrico
            KeyGenerator generadorAES = KeyGenerator.getInstance("AES", CRYPTO_PROVIDER);
            generadorAES.init(PilgrimEncoded.KEY_SIZE);
            Key aesKey = generadorAES.generateKey();
            
            // Ciframos el peregrino con cifrado simetrico AES
            Cipher cipherAes = Cipher.getInstance("AES", CRYPTO_PROVIDER);
            IvParameterSpec ivSpec  = new IvParameterSpec(PilgrimEncoded.IV_BYTES);
            cipherAes.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
            this.pilgrimdata = cipherAes.doFinal(p.toJSON().toJSONString().getBytes());
            
            // Ciframos la clave asimetricamente RSA
            Cipher cipherRsaPub = Cipher.getInstance("RSA", CRYPTO_PROVIDER);
            cipherRsaPub.init(Cipher.ENCRYPT_MODE, office.getKey().getPublic());
            this.encryptkey = cipherRsaPub.doFinal(aesKey.getEncoded());
            
            // Firma digital del peregrino
            byte[] hash = PilgrimEncoded.getSHA1Hash(p.toJSON().toJSONString().getBytes());
            Cipher cipherRsaPri = Cipher.getInstance("RSA", CRYPTO_PROVIDER);
            cipherRsaPri.init(Cipher.ENCRYPT_MODE, pilgrim.getKey().getPrivate());
            this.sign = cipherRsaPri.doFinal(hash);
            
        } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | InvalidKeyException | 
                InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
            PilgrimEncoded._log.log(Level.SEVERE, null, ex);
            throw new EncodeException("PilgrimEncoded can't calculate the sign. " + ex.getMessage());
        }
    }
    
    /**
     * Desencripta un objeto PilgrimEncoded en un objeto Pilgrim.
     * @param office Claves de la oficina que ha emitido la información.
     * @param pilgrim Claves del peregrino.
     * @return Objeto peregrino desencriptado.
     * @throws DecodeException 
     */
    public Pilgrim decrypt(Signer office, Signer pilgrim) throws DecodeException {
        if (office.getName().equals(this.signerName) &&
                pilgrim.getName().equals(this.pilSignerName)) {
            try {
                // Desciframos la clave asimetricamente RSA
                Cipher cipherRsaPriv = Cipher.getInstance("RSA", CRYPTO_PROVIDER);
                cipherRsaPriv.init(Cipher.DECRYPT_MODE, office.getKey().getPrivate());
                byte[] simKey = cipherRsaPriv.doFinal(this.encryptkey);
                
                // Obtenemos llave para descifrado simetrico
                Key aesKey = new SecretKeySpec(simKey, 0, simKey.length, "AES");
                
                // Desciframos el peregrino con cifrado simetrico AES
                Cipher cipherAes = Cipher.getInstance("AES", CRYPTO_PROVIDER);
                IvParameterSpec ivSpec  = new IvParameterSpec(PilgrimEncoded.IV_BYTES);
                cipherAes.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
                String jsonpil = new String(cipherAes.doFinal(this.pilgrimdata));
                
                // Convertimos los datos en un objeto Pilgrim
                StringReader sr = new StringReader(jsonpil);
                JSONParser parser = new JSONParser();
                Pilgrim pildecrypt = Pilgrim.fromJSON((JSONObject) parser.parse(sr));
            
                // Comprobamos firma digital del peregrino
                byte[] hash = PilgrimEncoded.getSHA1Hash(pildecrypt.toJSON().toJSONString().getBytes());
                Cipher cipherRsaPub = Cipher.getInstance("RSA", CRYPTO_PROVIDER);
                cipherRsaPub.init(Cipher.DECRYPT_MODE, pilgrim.getKey().getPublic());
                byte[] decryptedkey = cipherRsaPub.doFinal(this.sign);
                if (!Arrays.equals(hash, decryptedkey)) {
                    throw new DecodeException("Pilgrim sign is incorrect. Pilgrim info could has been manipulated!!.");
                }
                
                return pildecrypt;
            } 
            catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException | 
                    InvalidKeyException | IllegalBlockSizeException | BadPaddingException | 
                    IOException | InvalidAlgorithmParameterException ex) {
                PilgrimEncoded._log.log(Level.SEVERE, null, ex);
                throw new DecodeException("Unexpected error while decrypt PilgrimEncoded.");
            }
            catch (ParseException | org.json.simple.parser.ParseException ex) {
                PilgrimEncoded._log.log(Level.SEVERE, null, ex);
                throw new DecodeException("Decoded info is corrupted.");
            }
        }
        
        throw new DecodeException("Signers name don't match with the saved.");
    }
    
    /**
     * Obtiene el nombre de la oficina emisora de la información del peregrino.
     * @return Nombre de la oficina emisora.
     */
    public String getSignerName() {
        return this.signerName;
    }
    
    /**
     * Obtiene el nombre del peregrino que ha firmado.
     * @return Nombre del peregrino.
     */
    public String getPilgrimSignerName() {
        return this.pilSignerName;
    }
    
    /**
     * Obtiene la firma digital del peregrino.
     * @return Firma digital en formato binario.
     */
    public byte[] getSign() {
        return this.sign;
    }
    
    /**
     * Obtiene el hash SHA1 de los datos de un objeto PilgrimEncoded
     * @param pil Objeto a hashear.
     * @return Función de hasheo.
     * @throws DecodeException 
     */
    public static byte[] getSHA1Hash(PilgrimEncoded pil) throws DecodeException {
        return PilgrimEncoded.getSHA1Hash(pil.pilgrimdata);
    }
    
    // Función auxiliar. Obtiene el hash SHA1 de un array de bytes
    private static byte[] getSHA1Hash(byte[] pil) throws DecodeException {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA", "BC");
            messageDigest.update(pil);
            return messageDigest.digest();
        } catch (NoSuchProviderException|NoSuchAlgorithmException ex) {
            throw new DecodeException ("PilgrimEnconded can't be hashed. " + ex.getMessage());
        }
    }
    
    /**
     * Serializa PilgrimEncoded en un objeto JSON.
     * @return Objeto PilgrimEncoded serializado en JSON.
     */
    public JSONObject toJSON() {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("signer", this.signerName);
        jsonObj.put("pilgrim-signer", this.pilSignerName);
        jsonObj.put("data", Base64.getEncoder().encodeToString(this.pilgrimdata));
        jsonObj.put("encryptkey", Base64.getEncoder().encodeToString(this.encryptkey));
        jsonObj.put("sign", Base64.getEncoder().encodeToString(this.sign));
        
        return jsonObj;
    }
    
    /**
     * Obtiene un objeto de tipo PilgrimEncoded desde un objeto JSON.
     * @param ob Objeto PilgrimEncoded serializado en JSON.
     * @return Objeto PilgrimEncoded des-serializado.
     */
    public static PilgrimEncoded fromJSON (JSONObject ob) {
        return new PilgrimEncoded((String) ob.get("signer"),
                (String) ob.get("pilgrim-signer"),
                Base64.getDecoder().decode((String) ob.get("data")),
                Base64.getDecoder().decode((String) ob.get("encryptkey")),
                Base64.getDecoder().decode((String) ob.get("sign")));
    }
}
