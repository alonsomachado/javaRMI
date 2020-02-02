//package servidor;

import java.rmi.*;
import java.io.*;
import java.rmi.server.*;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ConcurrentModificationException;
import java.util.Random;
import java.util.Vector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * @author Alonso Lima Machado Para o Segundo Trabalho de Mestrado de
 * ComputaÃ§Ã£o DistribuÃ­da e em Nuvem do Mestrado em Engenharia InformÃ¡tica
 * da ESTG/IPP 2019
 */
public class ServidorImpl extends UnicastRemoteObject implements Servidor, Runnable {

    private static final long serialVersionUID = 4L;            //Serial version uid
    private static ServidorImpl rmi;
	public static String enderecoServidor = "//localhost:1099/Arquivo"; //Endereco ip deste servidor
	public static String enderecoServidorDocker = "//servidor:1099/Arquivo"; //Endereco ip no docker servidor (Melhor pegar em tempo de execucao com argv[0])

    public static final Vector<ClientMonitor> listaUserInterface = new Vector<ClientMonitor>(); //Lista de Utilizadores Online Ativos
    public final Vector<File> listaArquivos = new Vector<File>();

    public ServidorImpl() throws RemoteException {

    }
    //Cria o arquivo no Servidor na pasta serverdata, coloca o arquivo memória e envia posteriormente
    @Override
    public void guardaArquivo() {
        
        String utilizador = Thread.currentThread().getName();
		if(listaUserInterface.isEmpty()==false){ //Caso exista Listener na lista cria arquivo 
			String nomeRandom = "";
			String md5 = "";
			Date data = new Date();
			DateFormat osLocalizedDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
			int i;
			Random ra = new Random();
			do{
			nomeRandom = nomeRandom + ra.nextInt(999999);
			}while(nomeRandom.length() < 30);
			//Boolean pastaCriada = new File("..\\serverdata\\").mkdirs();
			File arq = new File("/serverdata/" + nomeRandom +".txt");
			FileWriter arquivoNovo;
			try {
				
				arquivoNovo = new FileWriter(arq);
				arquivoNovo.write("Arquivo Criado em Java para o Trabalho de CDN do Mestrado do ESTG! Aluno Alonso Machado! \n");
				arquivoNovo.write("Trabalho 2 Computacao Distribuida e Nuvem com Docker e Rede e Volume! \n");
				arquivoNovo.write(nomeRandom); //Escreve dentro do arquivo o nome dele totalmente randomico
				listaArquivos.add(arq);
				arquivoNovo.close();
				synchronized(this){		
					md5 = calculaMD5(arq);
					System.out.println("MD5 CHECKSUM: "+ md5);
					}
			} catch (FileNotFoundException ex) {
				System.out.println("Erro ao criar arquivo randomico: " + nomeRandom + " Erro: " + ex);

			} catch (NullPointerException e) {
				System.out.println("Erro NullPointer ao criar arquivo randomico: " + e);
			} catch (IOException ex) {
				System.out.println("Erro IOException ao criar arquivo randomico: " + ex);
			}
			
			//Manda para o Cliente Que está esperando o arquivo
			
			for (i = 0; i < listaUserInterface.size(); i++) {
				if (utilizador == null ? listaUserInterface.get(i).toString() == null : utilizador.equals(listaUserInterface.get(i).toString())) {
					ClientMonitor listenerUtilizadorArq = listaUserInterface.get(i);
					
					File arquivo = listaArquivos.lastElement(); //Pega o ultimo arquivo da Lista em memoria
					try {
						byte[] arquivoData = Files.readAllBytes(Paths.get("/serverdata/" + arquivo.getName()) );
						listenerUtilizadorArq.existeArquivoListeners(md5, osLocalizedDateFormat.format(new Date()));
						listenerUtilizadorArq.receberArquivo(md5, arquivo, arquivoData); //Tenta enviar ao cliente
						listaArquivos.remove(arquivo); //Remove o ultimo arquivo da Lista em memoria
					} catch (IOException e) {
						System.out.println("ERRO no Listener Arquivo, Removido" + e);
						removeListener(listenerUtilizadorArq);

					} catch (NullPointerException e) {
						System.out.println("ERRO Null Pointer Listener Arquivo, Removido" + e);
						removeListener(listenerUtilizadorArq);
					}
				}
			}
			System.out.println(" O arquivo: " + arq.getName() + " esta na sua Pasta SERVERDATA conforme especificacao do Trabalho");
		}
    }

    /**
     * JAVADOC
     */
    @Override
    public void run() {
        //Variavel de Controle para Thread parar de gerar e enviar arquivos;
		boolean continua=false;
		if(listaUserInterface.isEmpty()==false){ //True se estiver vazio False caso tenha elementos
			continua=true;
		}
        while(continua!=false) {
            try {
                Thread.sleep(5000); //Tempo em milisegundos (5000 5 segundos)

            } catch (InterruptedException eInterup) {
                System.out.println(eInterup.getMessage());
				//Thread.interrupt();
            } catch (Exception e) {
                //Thread.interrupt(); //Mata a Thread
            }

            guardaArquivo();
			if(listaUserInterface.isEmpty()==true){ //True se estiver vazio False caso tenha elementos
				continua=false;
			}

        }
    }

    @Override
    public void addListener(ClientMonitor clientInterface) {
        listaUserInterface.add(clientInterface);
        String clientex = clientInterface.toString();
        try {
            Thread userThread = new Thread(rmi, clientex);
            System.out.println("Startando a thread para o CLIENTE: " + clientex);
            userThread.start();
        } catch (ConcurrentModificationException eConcorrencia) {
            //System.out.println("Erro ao percorrer a Lista Por ter alterado. Erro:" + eConcorrencia);
        } catch (NullPointerException eNullPointer) {
            //System.out.println("Erro Null Pointer percorrer a Lista Por ter alterado. Erro:" + eNullPointer);
        }
        //System.out.println("Entrou um UserListener Novo!" + clientInterface);
    }

    @Override
    public void removeListener(ClientMonitor clientInterface) {
        int i;
        String clientex = clientInterface.toString();
        for (i = 0; i < listaUserInterface.size(); i++) {
            if (listaUserInterface.get(i).equals(clientInterface)) {
                System.out.println("Removeu o Listener: " + clientex);
                listaUserInterface.remove(i);
			}
		}
		System.out.println("--- Listagem dos Listeners Ativos neste servidor agora: ");
		if(listaUserInterface.isEmpty()==true) System.out.println(" Nenhum Listeners Ativo no servidor agora! ");
		for (i = 0; i < listaUserInterface.size(); i++) {
			System.out.println(i+" UserListener Existente: " + listaUserInterface.get(i).toString() );
		}

    }

    public static String hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    public static String calculaMD5(File f) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(f));
                byte[] buf = new byte[8192];
                for (int nBytes = is.read(buf, 0, buf.length); nBytes > 0; nBytes = is.read(buf, 0, buf.length)) {
                    md.update(buf, 0, nBytes);
                }
            } catch (IOException ex) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex2) {
                    }
                }
            }
            byte[] digest = md.digest();
            return hex(digest);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Erro ao Realizar MD5! " + ex);
        }
        return null;
    }
    
    public static void main(String args[]) throws Exception {

        System.setSecurityManager(new RMISecurityManager());
        try {
            rmi = new ServidorImpl();

            LocateRegistry.createRegistry(1099);
            System.out.println("Registry criado");
			
			if (args.length != 1)
			throw new RuntimeException("Syntax:" + " ServidorImpl <hostname>");
			Naming.rebind("//"+args[0]+":1099/Arquivo", rmi);
			System.out.println("Servidor Bindando na Porta 1099");
			System.out.println("Envia Arquivo Randomicamente Criado para cada cliente conectado a cada 5 Segundos....");
			System.out.println("Esperando Clientes....");
			int i = 0;
            while (true) {

                i++;
                if (i > 999) {
                    i = 1;
                }
            }
		} catch (java.rmi.UnknownHostException uhe) {
            System.out.println("Erro java.rmi.UnknownHostException.");
        } catch (RemoteException re) {
            System.out.println("Erro ao iniciar servico RMI: " + re);
        } catch (MalformedURLException mURLe) {
            System.out.println("Erro ao instaciar na URL //localhost:1099/IRCServer" + mURLe);
        }
    }
}

