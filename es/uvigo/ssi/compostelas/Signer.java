package es.uvigo.ssi.compostelas;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Signer {
    private static final Logger _log = Logger.getLogger(Signer.class.getName());
    
    private String name;
    
    private PublicKey publicSign;
    private PrivateKey privateSign;
    
    public static Signer fromCMD(String user) {
        Signer storet = new Signer();
        
        storet.name = user;
        
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
