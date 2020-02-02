
/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado em Engenharia Informática da ESTG/IPP 2019
 */
public interface MensagemMonitor extends java.rmi.Remote {

// Callback que recebe mensagem do servidor para o cliente a referir que o utilizador recebeu uma mensagem privada
    public void existeMensagemPrivadaListeners(String msg, String userEnvio, java.util.Date data) throws java.rmi.RemoteException;

// Callback que recebe mensagem do servidor para o cliente a referir que o utilizador recebeu uma mensagem de Grupo
    public void existeMensagemGrupoListeners(String msg, String grupoEnvio, java.util.Date data) throws java.rmi.RemoteException;

// Callback que recebe mensagem do servidor para o cliente a referir que o utilizador recebeu uma mensagem no Chat Geral (Com todos os utlizadores Online e Offline)
    public void existeChatGeralListeners(String msg, String userEnvio, java.util.Date data) throws java.rmi.RemoteException;

 // Callback que recebe mensagem do servidor para o cliente a referir que o utilizador recebeu um arquivo
    public void existeArquivoListeners(String msg, String dataEnvio) throws java.rmi.RemoteException;
	
// Callback que recebe o arquivo do servidor e coloca numa pasta no cliente
    public void receberArquivo(java.io.File arquivo) throws java.rmi.RemoteException;
    
}
