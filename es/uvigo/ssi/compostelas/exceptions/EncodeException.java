package es.uvigo.ssi.compostelas.exceptions;

/**
 * Excepción general producida al codificar algo.
 */
public class EncodeException extends Exception {
    public EncodeException(String message) {
        super(message);
    }
}
