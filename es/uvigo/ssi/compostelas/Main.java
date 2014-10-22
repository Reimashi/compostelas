package es.uvigo.ssi.compostelas;

import es.uvigo.ssi.compostelas.exceptions.DecodeException;
import es.uvigo.ssi.compostelas.exceptions.EncodeException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.simple.parser.ParseException;

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
    
    private static final Logger _log = Logger.getLogger(Main.class.getName());
    
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
                    if (args.length < 4) Main.cmdMsgInsuficientArguments();
                    else Main.cmdNew(args[1], args[2], args[3]);
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
    private static void cmdNew (String filepath, String user, String piluser) {
        
        try {
            Signer usign = Signer.loadFromFile(user);
            Signer pilusign = Signer.loadFromFile(piluser);
            Pilgrim pil = Pilgrim.fromCMD();
            Compostela comp = new Compostela(pil, usign, pilusign);
            comp.SaveFile(filepath);
        } catch (FileNotFoundException ex) {
            System.out.println("<ERROR> The user key file can't be opened.");
            Main._log.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("<ERROR> Output file can't be written. Check directory permissions.");
            Main._log.log(Level.SEVERE, null, ex);
        } catch (DecodeException | EncodeException ex) {
            System.out.println("<ERROR> Compostela can't be encrypted or signed.");
            Main._log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Firma digitalmente un archivo de formato .compostela
     * @param filepath Ruta del archivo de formato .compostela
     * @param user Ruta del archivo de claves del usuario que va a firmar.
     */
    private static void cmdStamp (String filepath, String user) {
        try {
            Compostela comp = Compostela.loadFromFile(filepath);
            Signer signer = Signer.loadFromFile(user);
            comp.AddStamp(signer);
            comp.SaveFile(filepath);
        } catch (IOException ex) {
            System.out.println("<ERROR> The user key file can't be opened.");
            Main._log.log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            System.out.println("<ERROR> JSON Parser can't understand the compostela file.");
            Main._log.log(Level.SEVERE, null, ex);
        } catch (DecodeException|EncodeException ex) {
            System.out.println("<ERROR> The program can't sign the compostela.");
            Main._log.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Comando "check" del programa. Comprueba las firmas y muestra información
     * del peregrino.
     * @param filepath Ruta del archivo de formato .compostela
     */
    private static void cmdCheck (String filepath) {
        
        try {
            Compostela comp = Compostela.loadFromFile(filepath);
            if (comp.CheckStamps()) {
                System.out.println("Signs are correct. File integrity is OK!");
                try {
                    System.out.println(comp.getPilgrim().toString());
                } catch (DecodeException ex) {
                    System.out.println("Pilgrim info can't be decoded.");
                    Main._log.log(Level.SEVERE, null, ex);
                }
            }
            else {
                System.out.println("Some sign appears to be incorrect. File integrity is BAD!");
            }
        } catch (IOException ex) {
            System.out.println("Compostela file can't be opened.");
            Main._log.log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            System.out.println("<ERROR> JSON Parser can't understand the compostela file.");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
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
	System.out.println("genkey <name>\n\n\tGenerate a sealant file for an user with <name>.\n");
	System.out.println();
	System.out.println("new <file> <office> <pilgrim>\n\n\tGenerate a compostela <file> signated by the <office> and <pilgrim>.\n");
	System.out.println();
	System.out.println("stamp <file> <user>\n\n\tStamp a compostela <file> with the sign of the <user>.\n");
	System.out.println();
	System.out.println("check <file>\n\n\tChech the sign's and show information about the pilgrim.\n");
	System.out.println();
	System.out.println("help\n\n\tShow this help.");
    }
}
