package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Representa una compostela, es decir, un archivo que el peregrino debe portar
 * y que debe ser sellado en todos los albergues por los que pasa en su camino
 * para acreditar su estancia en ellos.
 */
public class Compostela {
    private static final Logger _log = Logger.getLogger(Compostela.class.getName());
    
    private PilgrimEncoded pilgrimEncoded;
    private final List<HostelStamp> stamps;
    
    private Pilgrim pilgrimDecoded = null;
    
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
     * @param s Claves del que firma el sello.
     * @throws es.uvigo.ssi.compostelas.exceptions.DecodeException 
     * @throws es.uvigo.ssi.compostelas.exceptions.EncodeException 
     */
    public void AddStamp (Signer s) throws DecodeException, EncodeException {
        this.stamps.add(HostelStamp.fromCMD(s, this.pilgrimEncoded));
    }
    
    /**
     * Obtiene el objeto de tipo peregrino
     * @return Objeto de tipo peregrino.
     * @throws es.uvigo.ssi.compostelas.exceptions.DecodeException 
     */
    public Pilgrim getPilgrim() throws DecodeException {
        if (this.pilgrimDecoded == null) {
            try {
                Signer soff = Signer.loadFromFile(this.pilgrimEncoded.getSignerName());
                Signer spil = Signer.loadFromFile(this.pilgrimEncoded.getPilgrimSignerName());
                this.pilgrimDecoded = this.pilgrimEncoded.decrypt(soff, spil);
            } catch (FileNotFoundException ex) {
                throw new DecodeException ("Pilgrim can't be decoded. " + ex.getMessage());
            }
        }
        
        return this.pilgrimDecoded;
    }
    
    /**
     * Obtiene una lista de sellos (firma digital) de albergues.
     * @return Lista de sellos digitales.
     */
    public List<HostelStamp> getStamps() {
        return this.stamps;
    }
    
    /**
     * Comprueba que todas las firmas digitales, incluida la de peregrino, son correctas.
     * @return true si correctas; false si algún error.
     * @throws es.uvigo.ssi.compostelas.exceptions.EncodeException
     */
    public boolean check() throws EncodeException {
        try {
            this.getPilgrim();
            
            for (HostelStamp s: this.stamps) {
                if (!s.checkStamp(this.pilgrimEncoded)) {
                    return false;
                }
            }
            
            return true;
        } catch (DecodeException ex) {
            Compostela._log.log(Level.SEVERE, null, ex);
        }
        
        return false;
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
        
        for (Iterator<HostelStamp> it = this.stamps.iterator(); it.hasNext();) {
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
                toret.stamps.add(HostelStamp.fromJSON((JSONObject) i.next()));
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
