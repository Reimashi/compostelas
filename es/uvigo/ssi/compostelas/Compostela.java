package es.uvigo.ssi.compostelas;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Compostela implements Serializable {
    private transient Signer signerPilgrim;
    private final Pilgrim pilgrim;
    private final List<Stamp> stamps = new ArrayList<>();
    
    private Compostela() {
        this.pilgrim = null;
    }
    
    /**
     * Crea un objeto Compostela
     * @param p Información del peregrino
     * @param s Usuario que firma y encripta peregrino
     */
    public Compostela (Pilgrim p, Signer s) {
        this.pilgrim = p;
        this.signerPilgrim = s;
    }
    
    /**
     * Añade un sello (firma digital) de un albergue.
     * @param s 
     */
    public void AddStamp (Stamp s) {
        this.stamps.add(s);
    }
    
    /**
     * Obtiene el objeto de tipo peregrino
     * @return 
     */
    public Pilgrim getPilgrim() {
        return this.pilgrim;
    }
    
    /**
     * Obtiene una lista de sellos (firma digital) de albergues.
     * @return 
     */
    public List<Stamp> getStamps() {
        return this.stamps;
    }
    
    /**
     * Comprueba que todas las firmas digitales, incluida la de peregrino, son correctas.
     * @return TRUE si correctas. FALSE si algún error.
     */
    public boolean CheckStamps() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Guarda un archivo de tipo .compostela
     * @param path Ruta del fichero de salida, incluido el nombre del archivo.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void SaveFile (String path) throws FileNotFoundException, IOException {
        try (FileOutputStream fos = new FileOutputStream(path);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                XMLEncoder encoder = new XMLEncoder(bos)) {
            encoder.writeObject(this);
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } 
        catch (IOException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }
    
    /**
     * Carga un archivo .compostela en un objeto de tipo Compostela
     * @param path Ruta del archivo de origen
     * @return Objeto compostela
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static Compostela loadFromFile (String path) throws FileNotFoundException, IOException {
        Compostela toret = null;
        
        try (FileInputStream fis = new FileInputStream(path);
                BufferedInputStream bis = new BufferedInputStream(fis);
                XMLDecoder decoder = new XMLDecoder(bis)) {
            toret = (Compostela) decoder.readObject();
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } 
        catch (IOException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return toret;
    }
}
