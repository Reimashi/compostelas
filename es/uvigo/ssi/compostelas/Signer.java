package es.uvigo.ssi.compostelas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

/**
 * Representa un par de claves publica/privada e información sobre su dueño.
 */
public class Signer {
    private static final Logger _log = Logger.getLogger(Signer.class.getName());
    
    private static final String PRIVATE_KEY_EXTENSION = ".pem";
    
    private final String name;
    
    private PublicKey publicSign;
    private PrivateKey privateSign;
    
    public Signer (String name) {
        this.name = name;
    }
    
    /**
     * Obtiene el nombre del dueño de las claves.
     * @return Nombre del dueño de las claves.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Obtiene el par de claves.
     * @return Par de claves publica/privada.
     */
    public KeyPair getKey() {
        return new KeyPair(this.publicSign, this.privateSign);
    }
    
    /**
     * Guarda las claves en un archivo de formato PEM (Con nombre igual al dueño)
     * @throws IOException 
     */
    public void SaveFile() throws IOException {
        
        FileWriter fw = new FileWriter(this.name + Signer.PRIVATE_KEY_EXTENSION);
        
        try (JcaPEMWriter pemWriter = new JcaPEMWriter(fw)) {
            pemWriter.writeObject(this.privateSign);
            pemWriter.writeObject(this.publicSign);
        }
        catch (IOException e) {
            // TODO: 
            throw e;
        }
    }
    
    /**
     * Carga las claves en un archivo de formato PEM (Con nombre igual al dueño)
     * @param path Nombre del dueño / Ruta del archivo ("Dueño.pem")
     * @return Objeto Signer correspondiente.
     * @throws FileNotFoundException 
     */
    public static Signer loadFromFile(String path) throws FileNotFoundException {
        String privKeyFilename = path + Signer.PRIVATE_KEY_EXTENSION;
        File privKeyFile = new File(privKeyFilename);
        
        if (!privKeyFile.exists() && !privKeyFile.isFile()) {
            throw new FileNotFoundException("Sign file key <" + privKeyFilename + "> not found.");
        } 
        else {
            Signer sign = new Signer(path); // FIXME: Path en lugar del nombre
            
            try (FileInputStream privFis = new FileInputStream(privKeyFile); 
                    InputStreamReader privDis = new InputStreamReader(privFis)) {
                
                PEMParser parser = new PEMParser(privDis);
                Object privatekey = parser.readObject();
                
                if (privatekey instanceof PEMEncryptedKeyPair) {
                    privatekey = ((PEMEncryptedKeyPair)privatekey).decryptKeyPair(new JcePEMDecryptorProviderBuilder().build("".toCharArray()));
                }
                
                PEMKeyPair pair = (PEMKeyPair) privatekey;

                // Get the encoded objects ready for conversion to Java objects
                byte[] encodedPublicKey = pair.getPublicKeyInfo().getEncoded();
                byte[] encodedPrivateKey = pair.getPrivateKeyInfo().getEncoded();
                
                // Now convert to Java objects
                KeyFactory keyFactory = KeyFactory.getInstance( "RSA");

                X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
                sign.publicSign = keyFactory.generatePublic(publicKeySpec);

                PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
                sign.privateSign = keyFactory.generatePrivate(privateKeySpec);
            }
            catch (IOException|NoSuchAlgorithmException|InvalidKeySpecException e) {
            }
            
            return sign;
        }
    }
    
    /**
     * Genera un nuevo objeto Signer.
     * @param user Nombre del dueño.
     * @return Nuevo objeto Signer generado.
     */
    public static Signer createSigner(String user) {
        Signer storet = new Signer(user);
        
        try {
            KeyPairGenerator rsaGenerator;
            rsaGenerator = KeyPairGenerator.getInstance("RSA", "BC");
            rsaGenerator.initialize(512);
        
            KeyPair clavesRSA = rsaGenerator.generateKeyPair();
            storet.privateSign = clavesRSA.getPrivate();
            storet.publicSign = clavesRSA.getPublic();
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            _log.log(Level.SEVERE, null, ex);
            throw new UnsupportedOperationException("BouncyCastle can't be loaded. " + 
                    ex.getMessage());
        }
        
        return storet;
    }
}
