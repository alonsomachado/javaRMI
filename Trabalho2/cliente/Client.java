//package servidor;

import java.rmi.*;
import java.rmi.Naming;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.rmi.PortableRemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


/**
 *
 * @author Alonso Lima Machado Para o Segundo Trabalho de Mestrado de
 * ComputaÃ§Ã£o DistribuÃ­da e em Nuvem do Mestrado em Engenharia InformÃ¡tica
 * da ESTG/IPP 2019
 */
public class Client extends UnicastRemoteObject implements ClientMonitor {

    // private static Client service; //Interface RMI para acesso ao servidor
    public static String enderecoServidor = "rmi://localhost:1099/Arquivo";
	public static String enderecoServidorDocker ="rmi://servidor:1099/Arquivo";
	
    private static final long serialVersionUID = 4L;            //Serial version uid

    protected Client() throws RemoteException {
    }

    public static void main(String args[]) throws Exception {
		
		if (args.length != 1)
	    throw new RuntimeException("Syntax:" + " Client <hostname>");


        //Instancia o Security Manager para o RMI
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Criando registry do Servidor
        //Servidor service = (Servidor) Naming.lookup(enderecoServidor);
		Servidor service = (Servidor) Naming.lookup("rmi://"+args[0]+":1099/Arquivo");
        // Criando leitor do Terminal
        DataInputStream din = new DataInputStream(System.in);
        String line;
        //Construir um objeto dele mesmo
        Client cliente = new Client();
        //Output Stream para Receber Arquivos
        BufferedOutputStream output;

       
        System.out.println("Executando Cliente! " );
        
        service.addListener(cliente);
		int i = 0;
		int j = 0;
        while (true) {
            
			
			for(i=0;i < 999990;i++){
				for(j=0;j < 9999;j++){
					if (i > 999980 & j > 9997) {
						//System.out.println("Requisitando Arquivo! " );
						//service.guardaArquivo();
						i = 1;
						j = 1;
					}
				}
			}
            
            /*
			service.removeListener(cliente, username);
			System.exit(1);
             */
        }

    }
	

    @Override
    public void existeArquivoListeners(String checksum, String data) {
        System.out.println("\nRecebeu um arquivo " + "(" + data + ") MD5 Recebido: " + checksum);
    }

    @Override
    public void receberArquivo(String md5Recebido, java.io.File recebido, byte[] arquivoData) {
		//Boolean pastaCriada = new File("..\\clientdata\\").mkdirs();
        try {
            
            String checksum = "";
            OutputStream outputStream = new FileOutputStream("/clientdata/" + recebido.getName());
            outputStream.write(arquivoData);
            byte[] b = Files.readAllBytes(Paths.get("/clientdata/" + recebido.getName()) );
            byte[] hash = MessageDigest.getInstance("MD5").digest(b);
            checksum = Arrays.toString(hash);
            String md5p = hex(hash);
            System.out.println("HASH CHECKSUM: " + checksum + " MD5: " + md5p);

            System.out.println((md5p == null ? md5Recebido == null : md5p.equals(md5Recebido)) ? "CHECKSUM e MD5 do Arquivo CORRETO!" : "Arquivo Com Erro"); //Verificar Checksum
            

            outputStream.flush();
            outputStream.close();
            System.out.println(" O arquivo: " + recebido.getName() + " esta na sua Pasta CLIENTDATA conforme especificacao do Trabalho");

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo a ser Recebido teve problemas na Transferencia: " + ex);
        } catch (IOException ex) {
            System.out.println("Arquivo Recebido com erro IO: " + ex);
        } catch (NullPointerException e) {
            //System.out.println("Arquivo Recebido deu Null Pointer com erro " + e);
        } catch (NoSuchAlgorithmException ex) { //Nunca ocorre para MD5 mas e obrigatorio para tirar o warning/error no Netbeans
            System.out.println("Erro ao Realizar MD5! " + ex); 
        }

    }

    public static String hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

  
}
