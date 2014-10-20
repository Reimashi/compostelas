package es.uvigo.ssi.compostelas;

import java.io.Serializable;

/**
 * Sello digital de una compostela
 */
public class Stamp implements Serializable {
    private String owner;
    private byte[] sign;
    
    private Stamp() {}
    
    public Stamp (Signer signer, Pilgrim pil) {
        this.owner = signer.getName();
        this.sign = this.calculateSign(signer, pil);
    }
    
    /**
     * Calcula la firma digital de la compostela para un signer (albergue). A partir del hash?
     * @param signer Usuario que firma la compostela
     * @param pil Información del peregrino
     * @return Firma digital
     */
    private byte[] calculateSign(Signer signer, Pilgrim pil) {
        throw new UnsupportedOperationException();
    }
}
