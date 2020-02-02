//package servidor;


/**
 *
 * @author Alonso Lima Machado Para o Segundo Trabalho de Mestrado de
 * ComputaÃ§Ã£o DistribuÃ­da e em Nuvem do Mestrado em Engenharia InformÃ¡tica
 * da ESTG/IPP 2019
 */
public interface Servidor extends java.rmi.Remote {

//Guarda o arquivo na listaArquivos no servidor em Memoria.
    public void guardaArquivo() //String utilizadorEnviouArquivo,
            throws java.rmi.RemoteException;

//Cria um Utilizador logado na hastable para criar threads
    public void addListener(ClientMonitor clientInterface)
            throws java.rmi.RemoteException;

//Remove um Utilizador logado na hastable para criar threads
    public void removeListener(ClientMonitor clientInterface)
            throws java.rmi.RemoteException;

}
