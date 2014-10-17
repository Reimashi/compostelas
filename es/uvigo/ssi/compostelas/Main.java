package es.uvigo.ssi.compostelas;

import java.security.Security;
import java.util.Scanner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Clase principal y punto de entrada del programa
 */
public class Main {
    /**
     * Nombre de la aplicación
     */
    private static final String APP_NAME = "compostelas";
    
    /**
     * Versión de la aplicación
     */
    private static final String APP_VERSION = "0.1";
    
    /**
     * Punto de entrada del programa
     * @param args Argumentos de la ejecución
     */
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        
        if (args.length > 0) {
            switch (args[0]) {
                case "genkey":
                    if (args.length < 3) Main.cmdMsgInsuficientArguments();
                    else Main.cmdGenkey(args[1], args[2]);
                    break;
                case "new":
                    if (args.length < 3) Main.cmdMsgInsuficientArguments();
                    else Main.cmdNew(args[1], args[2]);
                    break;
                case "stamp":
                    if (args.length < 3) Main.cmdMsgInsuficientArguments();
                    else Main.cmdStamp(args[1], args[2]);
                    break;
                case "check":
                    if (args.length < 2) Main.cmdMsgInsuficientArguments();
                    else Main.cmdCheck(args[1]);
                    break;
                case "help":
                    Main.cmdHelp();
                    break;
                default:
                    Main.cmdMsgCommandNotRecognized();
                    break;
            }
        }
        else {
            Main.cmdMsgInsuficientArguments();
        }
    }
    
    
    /**
     * Muestra un mensage por consola advirtiendo de la falta de argumentos.
     */
    private static void cmdMsgInsuficientArguments () {
        System.out.println("Insufficient arguments. Try \"" + Main.APP_NAME + " help\"");
    }
    
    /**
     * Muestra un mensage por consola advirtiendo de un comando no reconocido.
     */
    private static void cmdMsgCommandNotRecognized () {
        System.out.println("Command not recognized. Try \"" + Main.APP_NAME + " help\"");
    }
    
    /**
     * Crea un archivo de formato .userkeys con información y claves RSA del usuario.
     * @param filepath Ruta del archivo de formato .userkeys
     * @param user Tipo de usuario. (hostel, office)
     */
    private static void cmdGenkey (String user, String type) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Crea un archivo de formato .compostela encriptado y lo firma digitalmente.
     * @param filepath Ruta del archivo de formato .compostela a guardar.
     * @param user Ruta del archivo de claves del usuario que va a firmar.
     */
    private static void cmdNew (String filepath, String user) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Firma digitalmente un archivo de formato .compostela
     * @param filepath Ruta del archivo de formato .compostela
     * @param user Ruta del archivo de claves del usuario que va a firmar.
     */
    private static void cmdStamp (String filepath, String user) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Comando "check" del programa. Comprueba las firmas y muestra información
     * del peregrino.
     * @param filepath Ruta del archivo de formato .compostela
     */
    private static void cmdCheck (String filepath) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Muestra la ayuda del programa
     */
    private static void cmdHelp()  {
	System.out.println(Main.APP_NAME + " (" + Main.APP_VERSION + ")");
	System.out.println();
	System.out.println("Usage mode:");
	System.out.println();
	System.out.println("\tcompostelas <command> ...");
	System.out.println();
	System.out.println("Commands:");
	System.out.println();
	System.out.println("genkey <name> <type>\tGenerate a sealant file for an user with <name> and\n\t\t\tof <type>. <type> values: \"hostel\", \"office\"");
	System.out.println();
	System.out.println("new <file> <user>\tGenerate a compostela <file> signated by the <user>\n\t\t\t(Only \"office\"-type users)");
	System.out.println();
	System.out.println("stamp <file> <user>\tStamp a compostela <file> with the sign of the <user>\n\t\t\t(Only \"hostel\"-type users)");
	System.out.println();
	System.out.println("check <file>\t\tChech the sign's and show information about the pilgrim");
	System.out.println();
	System.out.println("help\t\t\tShow this help");
    }
}
