package es.uvigo.ssi.compostelas.exceptions;

/**
 * Excepción general producida al decodificar algo.
 */
public class DecodeException extends Exception {
    public DecodeException(String message) {
        super(message);
    }
}
