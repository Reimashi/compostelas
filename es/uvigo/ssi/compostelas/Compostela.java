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
    private Pilgrim pilgrim;
    private List<Stamp> stamps = new ArrayList<>();
    
    private Compostela() {}
    
    public Compostela (Pilgrim p) {
        this.pilgrim = p;
    }
    
    public void AddStamp (Stamp s) {
        this.stamps.add(s);
    }
    
    public Pilgrim getPilgrim() {
        return this.pilgrim;
    }
    
    public List<Stamp> getStamps() {
        return this.stamps;
    }
    
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
