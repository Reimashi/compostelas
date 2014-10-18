package es.uvigo.ssi.compostelas;

import java.io.IOException;
import java.security.Security;
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
     * Página web de la aplicación
     */
    private static final String APP_WEB = "https://github.com/Reimashi/compostelas";
    
    /**
     * Punto de entrada del programa
     * @param args Argumentos de la ejecución
     */
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        
        if (args.length > 0) {
            switch (args[0]) {
                case "genkey":
                    if (args.length < 2) Main.cmdMsgInsuficientArguments();
                    else Main.cmdGenkey(args[1]);
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
     * @param user Tipo de usuario. (hostel, office, pilgrim)
     */
    private static void cmdGenkey (String user) {
        Signer s = Signer.createSigner(user);
        
        try {
            s.SaveFile();
            System.out.println("Sign files created succesfully!");
        }
        catch (IOException e) {
            System.out.println("An error has ocurred while " + Main.APP_NAME + " try save the sign files.");
        }
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
	System.out.println(Main.APP_NAME + " (" + Main.APP_VERSION + ") <" + Main.APP_WEB + ">");
	System.out.println();
	System.out.println("Usage mode:");
	System.out.println();
	System.out.println("\tcompostelas <command> ...");
	System.out.println();
	System.out.println("Commands:");
	System.out.println();
	System.out.println("genkey <name>\t\tGenerate a sealant file for an user with <name>.");
	System.out.println();
	System.out.println("new <file> <user>\tGenerate a compostela <file> signated by the <user>.");
	System.out.println();
	System.out.println("stamp <file> <user>\tStamp a compostela <file> with the sign of the <user>.");
	System.out.println();
	System.out.println("check <file>\t\tChech the sign's and show information about the pilgrim");
	System.out.println();
	System.out.println("help\t\t\tShow this help");
    }
}
