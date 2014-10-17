package es.uvigo.ssi.compostelas.types;

import java.io.Serializable;

public class NIF implements Serializable {
    private static final long serialVersionUID = 8799656478674716638L;
    
    public static final String NIF_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    
    private int number;
    private int letter;
    
    private NIF() {}
    
    public NIF (int number) {
        if (number >= 10000000 && number <= 99999999) {
            this.number = number;
            this.letter = NIF.calculateLetter(number);
        }
        else {
            throw new NumberFormatException("A NIF number must have 8 digits");
        }
    }
    
    public static char calculateLetter(int number) {
        if (number >= 10000000 && number <= 99999999) {
            return NIF_LETTERS.charAt(number % 23);
        }
        else {
            throw new NumberFormatException("A NIF number must have 8 digits");
        }
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.number) + "-" + this.letter;
    }
}
