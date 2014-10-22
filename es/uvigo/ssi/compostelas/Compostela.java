package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Compostela implements Serializable {
    private PilgrimEncoded pilgrimEncoded;
    private final List<Stamp> stamps;
    
    private Compostela() {
        this.stamps = new ArrayList<>();
    }
    
    /**
     * Crea un objeto Compostela
     * @param p Información del peregrino
     * @param office Claves de la oficinal del peregrino
     * @param pilgrim Claves del peregrino
     * @throws es.uvigo.ssi.compostelas.exceptions.DecodeException
     * @throws es.uvigo.ssi.compostelas.exceptions.EncodeException
     */
    public Compostela (Pilgrim p, Signer office, Signer pilgrim) throws DecodeException, EncodeException {
        this.pilgrimEncoded = new PilgrimEncoded(p, office, pilgrim);
        this.stamps = new ArrayList<>();
    }
    
    /**
     * Añade un sello (firma digital) de un albergue.
     * @param s 
     * @throws es.uvigo.ssi.compostelas.exceptions.DecodeException 
     * @throws es.uvigo.ssi.compostelas.exceptions.EncodeException 
     */
    public void AddStamp (Signer s) throws DecodeException, EncodeException {
        this.stamps.add(new Stamp(s, this.pilgrimEncoded));
    }
    
    /**
     * Obtiene el objeto de tipo peregrino
     * @return 
     * @throws es.uvigo.ssi.compostelas.exceptions.DecodeException 
     */
    public Pilgrim getPilgrim() throws DecodeException {
        try {
            Signer s = Signer.loadFromFile(this.pilgrimEncoded.getSignerName());
            return this.pilgrimEncoded.decrypt(s);
        } catch (FileNotFoundException ex) {
            throw new DecodeException ("Pilgrim can't be decoded. " + ex.getMessage());
        }
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
    public void SaveFile (String path) throws IOException {
        JSONObject jsonObj = new JSONObject();
        
        jsonObj.put("pilgrim", this.pilgrimEncoded.toJSON());
        
        JSONArray jsonStamps = new JSONArray();
        
        for (Iterator<Stamp> it = this.stamps.iterator(); it.hasNext();) {
            jsonStamps.add(it.next().toJSON());
        }
        
        jsonObj.put("stamps", jsonStamps);
        
        try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObj.toJSONString());
            file.flush();
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
     * @throws org.json.simple.parser.ParseException 
     */
    public static Compostela loadFromFile (String path) throws IOException, ParseException {
        Compostela toret = new Compostela();
        
        try (FileReader reader = new FileReader(path)) {
            JSONParser parser = new JSONParser();
            JSONObject jsonRoot = (JSONObject) parser.parse(reader);
            
            toret.pilgrimEncoded = PilgrimEncoded.fromJSON((JSONObject) jsonRoot.get("pilgrim"));
            
            JSONArray stamps = (JSONArray) jsonRoot.get("stamps");

            Iterator i = stamps.iterator();
            while (i.hasNext()) {
                toret.stamps.add(Stamp.fromJSON((JSONObject) i.next()));
            }
        } 
        catch (FileNotFoundException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        } 
        catch (IOException | ParseException ex) {
            Logger.getLogger(Compostela.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return toret;
    }
}
