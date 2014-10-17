package es.uvigo.ssi.compostelas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Compostela implements Serializable {
    private Pilgrim pilgrim;
    private List<Stamp> stamps = new ArrayList<>();
    
    private Compostela() {}
    
    public Compostela (Pilgrim p) {
        this.pilgrim = p;
    }
    
    private void AddStamp (Stamp s) {
        this.stamps.add(s);
    }
}
