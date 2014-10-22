package es.uvigo.ssi.compostelas.types;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Clase que representa un número de identificación fiscal (NIF) Español.
 */
public class NIF implements Serializable {
    private static final long serialVersionUID = 8799656478674716638L;
    
    private static final String NIF_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    
    private final int number;
    private final char letter;
    
    /**
     * Crea un objeto NIF a partir de su número de documento.
     * @param number Número de documento.
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
     * Obtiene el número del documento NIF.
     * @return Número del documento.
     */
    public int getNumber() {
        return this.number;
    }
    
    /**
     * Obtiene la letra del documento NIF.
     * @return Carácter de verificación del documento.
     */
    public char getLetter() {
        return this.letter;
    }
    
    /**
     * Calcula la letra de verificación de un NIF.
     * @param number Número de documento
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
     * Obtiene un objeto NIF a partir de su representación textual.
     * FIXME: Comprobar las letras en las representaciones complejas.
     * @param str Cadena de caracteres con la representación textual.
     * @return Objeto NIF creado.
     * @throws java.text.ParseException 
     */
    public static NIF getNIF(String str) throws ParseException {
        if (str.length() == 8) {
            return new NIF(Integer.getInteger(str));
        }
        else if (str.length() == 9) {
            return new NIF(Integer.getInteger(str.substring(0,8)));
        }
        else if (str.length() == 10) {
            return new NIF(Integer.getInteger(str.substring(0,8)));
        }
        else {
            throw new ParseException("getNIF can't parse the textual representation of NIF", 0);
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
