package es.uvigo.ssi.compostelas.types;

import java.io.Serializable;

/**
 * Clase que representa un numero de identificación fiscal (NIF) Español.
 */
public class NIF implements Serializable {
    private static final long serialVersionUID = 8799656478674716638L;
    
    public static final String NIF_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    
    private int number;
    private int letter;
    
    private NIF() {}
    
    /**
     * Crea un objeto NIF a partir de su numero de documento.
     * @param number Numero de documento
     */
    public NIF (int number) {
        if (number >= 10000000 && number <= 99999999) {
            this.number = number;
            this.letter = NIF.calculateLetter(number);
        }
        else {
            throw new NumberFormatException("A NIF number must have 8 digits");
        }
    }
    
    /**
     * Calcula la letra de verificación de un NIF.
     * @param number Numero de documento
     * @return Letra de verificación
     */
    public static char calculateLetter(int number) {
        if (number >= 10000000 && number <= 99999999) {
            return NIF_LETTERS.charAt(number % 23);
        }
        else {
            throw new NumberFormatException("A NIF number must have 8 digits");
        }
    }
    
    /**
     * Representa un documento NIF como una cadena de caracteres.
     * @return Cadena de caracteres que representa el documento NIF.
     */
    @Override
    public String toString() {
        return String.valueOf(this.number) + "-" + this.letter;
    }
}
