
/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public interface MensagemService extends java.rmi.Remote {
 
 //Tentativa de Registro do Cliente, verifica a existencia 
    public Boolean registro(String username, String password)
            throws java.rmi.RemoteException;
 
 //Log In do Utilizador, mudando a variavel Boolean Autenticado daquele Utilizador para True
    public int login(String username, String password)
            throws java.rmi.RemoteException;

//Coloca todos os Utilizadores registrados no servidor na variavel utilizadoresSistema que retona ao cliente
    public String listarUtilizadores()
            throws java.rmi.RemoteException;

//Coloca todos os Grupos registrados no servidor na variavel listaGrupos que retona ao cliente
    public String listarGrupos()
            throws java.rmi.RemoteException;

//Coloca todas as Mensagens Gerais registrados no servidor chatGeral que retona ao cliente
    public String ChatGeral()
            throws java.rmi.RemoteException;

//Log Out do Utilizador, mudando a variavel Boolean Autenticado daquele Utilizador para False
    public void logOut(String username)
            throws java.rmi.RemoteException;

//Recebe a Mensagem do Cliente e manda para o Chat Geral 
    public void MensagemGeral(String user, String mensagem)
            throws java.rmi.RemoteException;

//Recebe do cliente a Mensagem Privada de um utilizador para outro utilizador
    public void MensagemPrivada(String user, String mensagem, String user_destino)
            throws java.rmi.RemoteException;

//Recebe do cliente uma Mensagem para um Grupo e manda para o Grupo Especificado
    public void MensagemGrupo(String user, String mensagem, String nomeGrupo)
            throws java.rmi.RemoteException;

//Recebe do cliente uma Solicitacao para Criar um Grupo e Guarda no Servidor
    public int criarGrupo(String nomeGrupo, String username)
            throws java.rmi.RemoteException;

//Entra no Grupo que o Cliente requisitou
    public void entrarGrupo(String nomeGrupo, String username)
            throws java.rmi.RemoteException;

//Guarda o arquivo na listaArquivos no servidor em Memoria.
    public int guardaArquivo(String utilizadorDestinoArquivo, String caminho) //String utilizadorEnviouArquivo,
            throws java.rmi.RemoteException;

//Cria um Utilizador logado na hastable para criar threads
    public void addListener(MensagemMonitor clientInterface, String username)
            throws java.rmi.RemoteException;

//Remove um Utilizador logado na hastable para criar threads
    public void removeListener(MensagemMonitor clientInterface, String username)
            throws java.rmi.RemoteException;

}
