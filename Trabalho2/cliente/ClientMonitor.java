//package servidor;

/**
 *
 * @author Alonso Lima Machado Para o Segundo Trabalho de Mestrado de
 * ComputaÃ§Ã£o DistribuÃ­da e em Nuvem do Mestrado em Engenharia InformÃ¡tica
 * da ESTG/IPP 2019
 */
public interface ClientMonitor extends java.rmi.Remote {

    // Callback que recebe mensagem do servidor para o cliente a referir que o utilizador recebeu um arquivo
    public void existeArquivoListeners(String msg, String dataEnvio) throws java.rmi.RemoteException;

	// Callback que recebe o arquivo do servidor e coloca numa pasta no cliente
    public void receberArquivo(String checksum, java.io.File arquivo, byte[] arquivoData) throws java.rmi.RemoteException;

}
