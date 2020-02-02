
import java.rmi.*;
import java.io.*;
import java.util.Date;
import java.rmi.server.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.registry.LocateRegistry;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public class MensagemServiceServer extends UnicastRemoteObject implements MensagemService, Runnable {

    private static MensagemServiceServer rmi;
    public static final Vector<User> listaUser = new Vector<>(); //Lista de Utilizadores Registrados
    public static final Hashtable<String, MensagemMonitor> listaUserInterface = new Hashtable<>(); //Lista de Utilizadores Online Ativos
    public static final Hashtable<String, Boolean> listaUserInterfaceTemThread = new Hashtable<>();
    public static final Vector<Grupo> listaGrupo = new Vector<>();
    public final Vector<Mensagem> listaMensagem = new Vector<>();
    public final Vector<Mensagem> listaMensagemPrivada = new Vector<>();
    public final Hashtable<String, File> listaArquivos = new Hashtable<>();
    public volatile boolean registroGlobal; //Retorno do Registro para o Cliente
    public volatile int loginGlobal; //Retorno do Login para o Cliente
    public volatile int guardaArquivoGlobal; //Retorno do Guardar Arquivo para o Cliente
    public volatile String listaGruposGlobal;
    public volatile String utilizadoresSistemaGlobal;
    public volatile String chatGeralGlobal;
    public volatile Integer respostaCriaGrupo;

    public MensagemServiceServer() throws RemoteException {

    }

    public static void criaPadrao() {
        User user1 = new User("teste", "teste", false);
        User user2 = new User("teste2", "teste2", false);
        User user3 = new User("111", "111", false);
        User user4 = new User("222", "222", false);
        User user5 = new User("333", "333", false);
        listaUser.add(user1);
        listaUser.add(user2);
        listaUser.add(user3);
        listaUser.add(user4);
        listaUser.add(user5);
        Grupo Grupo1 = new Grupo("teste");
        Grupo Grupo2 = new Grupo("futebol");
        listaGrupo.add(Grupo1);
        listaGrupo.add(Grupo2);
    }

    @Override
    public synchronized Boolean registro(String username, String password) { //Tentativa de Registro do Cliente, verifica a existencia 

        Thread registroThread = new Thread(() -> {
            Boolean registroDaThread = false;
            try {
                int i = 0, al = 0, flag = 0;

                User novo = new User(username, password, false);
                if (listaUser.isEmpty()) {//Primeiro Utilizador do Sistema 
                    System.out.println("Primeiro Utilizador Registado.  Utilizador: " + username + " Password: " + password + " Estado: " + novo.getAutenticado());
                    listaUser.add(novo);
                    flag = 1;
                    registroDaThread = true;
                    //return true;
                }
                for (i = 0; i < listaUser.size(); i++) {
                    if (!(listaUser.get(i).getUsername().equals(username))) { // Se percorreu a lista inteira e não achou Utilizador com esse Username
                        al++;
                        if (al == listaUser.size()) {
                            listaUser.add(novo);
                            System.out.println("Utilizador Registado.  Utilizador: " + username + " Password: " + password + " Estado: " + novo.getAutenticado());
                            registroDaThread = true;
                            flag = 1;
                            //return true;
                        }
                    }
                }
                if (flag == 0) { // Percorreu a lista e existe Utilizador com esse Username (Nao criou Utilizador novo) Retorna false
                    registroDaThread = false;
                }
                //System.out.println("Variavel Registro agora: " + registro);
            } catch (Exception e) {
                System.out.println("Erro na Thread Registo: " + e);
            }
            //synchronized (this) {
            registroGlobal = registroDaThread;
            //}

        });
        registroThread.start();
        try {
            registroThread.sleep(100);
            registroThread.join();
            registroThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Registo Join/Interrupt: " + ex);
        }
        return registroGlobal;

    }

    @Override
    public int login(String username, String password) {
        //public synchronized int login(String username, String password) { //Log In do Utilizador, mudando a variavel Boolean Autenticado daquele Utilizador para True

        Thread loginThread = new Thread(() -> {

            int loginDaThread = 0; //Variavel local da trhead
            int i = 0, j = 0;

            if (listaUser.isEmpty()) {//Primeiro Utilizador do Sistema
                loginDaThread = 3;
                System.out.println("Tentativa de Login sem nenhum Utilizador Cadastrado! Username: " + username + " Password: " + password);

            }

            for (i = 0; i < listaUser.size(); i++) {
                if (!(listaUser.get(i).getUsername().equals(username))) { // Se percorreu a lista inteira e não achou Utilizador com esse Username
                    j++;
                    if ((j == listaUser.size())) {
                        loginDaThread = 4;
                        System.out.println("Tentativa de Login com Utilizador Inexistente: " + username);
                        break;
                    }
                } else {
                    if ((listaUser.get(i).getUsername().equals(username)) && (listaUser.get(i).getPassword().equals(password))) { // Se o username e senha passadas conferem faz LOGIN
                        if (listaUser.get(i).getAutenticado().equals(false)) {
                            listaUser.get(i).setAutenticado(true);
                            loginDaThread = 1;
                            System.out.println("Login! Utilizador: " + username + " Password: " + password + " Estado: " + listaUser.get(i).getAutenticado());
                            break;
                        } else { //Login em mais de um terminal para o mesmo utilizador
                            loginDaThread = 2;
                            System.out.println("Tentativa de Login com Utilizador online em outro terminal: " + username);
                            break;
                        }
                    }

                    if ((listaUser.get(i).getUsername().equals(username)) && !(listaUser.get(i).getPassword().equals(password))) { // Se a senha passada não confere com a senha o Username MENSAGEM ERRO
                        loginDaThread = 0;
                        System.out.println("Tentativa de Login com Password Errada para Utilizador: " + username);
                        break;
                    }
                }

            }
            synchronized (this) {
                loginGlobal = loginDaThread;
            }
            //Imprimir todos os utilizadores registrados no sistema
            /*for (User mUser : listaUser) {
             System.out.println("Utilizador registados : " + mUser.getUsername() + " Pass registados: " + mUser.getPassword());
             }*/
            //retorno = this.flag;
        });
        loginThread.start();

        try {
            loginThread.join();
            loginThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Login Join/Interrupt: " + ex);
        }
        return loginGlobal;

    }

    @Override
    public synchronized void logOut(String username) { //Log Out do Utilizador, mudando a variavel Boolean Autenticado daquele Utilizador para False

        Thread logOutThread = new Thread(() -> {
            int i = 0;

            for (i = 0; i < listaUser.size(); i++) {
                if (listaUser.get(i).getUsername().equals(username)) {
                    listaUser.get(i).setAutenticado(false);
                    Date dataLogOut = new Date();
                    listaUser.get(i).setDataUltimaSessao(dataLogOut);
                }
            }
            System.out.println("Lista de Utilizadores apos operacao de logout: ");
            for (User mUser : listaUser) {
                System.out.println("Logout: " + " Username: " + mUser.getUsername() + " Estado  : " + mUser.getAutenticado());
            }
        });
        logOutThread.start();
        try {
            //thread.sleep(300);
            logOutThread.join();
            logOutThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread LogOut Join/Interrupt: " + ex);
        }
    }

    @Override
    public void MensagemGeral(String username, String mensagem) { //Recebe a Mensagem do Cliente e manda para o Chat Geral 

        Thread msgGeralThread = new Thread(() -> {
            System.out.println("Mensagem Geral recebeu: " + mensagem + "Username: " + username);

            Mensagem nova = new Mensagem(username, mensagem); //Username do Utilizador que escreveu a mensagem
            listaMensagem.add(nova);
            //Imprimir todos as Mensagens do Chat Geral registrados no servidor
            /*System.out.println("Lista de Chat Geral ");
             for (Mensagem mMsg : listaMensagem) {
             System.out.println(" Username: " + mMsg.getUsername() + " Mensagem  : " + mMsg.getMensagem());
             }*/
        });
        msgGeralThread.start();
        msgGeralThread.interrupt();

    }

    @Override
    public void MensagemPrivada(String userEnvio, String mensagem, String userDestino) { //Recebe do cliente a Mensagem Privada de um utilizador para outro utilizador
        Thread msgPrivadaThread = new Thread(() -> {
            System.out.println("Mensagem Privada recebeu: " + mensagem + " Utilizador Envio: " + userEnvio + " Utilizador Destino: " + userDestino);

            Mensagem nova = new Mensagem(userEnvio, mensagem, userDestino);  //Cria a mensagem privada no Construtor de Mensagens
            listaMensagemPrivada.add(nova);
            //Imprimir todos as Mensagens do Chat Privado registrados no servidor
            /*System.out.println("Lista de Mensagens Privadas (1 Para 1) ");
             for (Mensagem mMsg : listaMensagemPrivada) {
             System.out.println(" Mensagem: " + mMsg.getMensagem() + " Utilizador Envio: " + mMsg.getUsername() + " Utilizador Destino: " + mMsg.getDestino());
             }*/
        });
        msgPrivadaThread.start();
        msgPrivadaThread.interrupt();
    }

    @Override
    public void MensagemGrupo(String userEnvio, String mensagem, String nomeGrupo) { //Recebe do cliente uma Mensagem para um Grupo e manda para o Grupo Especificado 
        Thread msgGrupoThread = new Thread(() -> {
            System.out.println("Grupo: " + nomeGrupo + " Mensagem: " + mensagem + " do Utilizador: " + userEnvio);

            Mensagem nova = new Mensagem(userEnvio, mensagem, nomeGrupo); //Cria a mensagem para o Grupo
            int i, j;

            for (i = 0; i < listaGrupo.size(); i++) {
                if (listaGrupo.get(i).getNomeGrupo().equals(nomeGrupo)) {
                    listaGrupo.get(i).listaGrupoMensagem.add(nova); //Envia a mensagem para o Grupo
                }
            }
            //Imprimir todos as Mensagens de Grupo de todos os Grupos registrados no servidor
            /*System.out.println("Lista de Mensagens nos Grupos ");
             for (i = 0; i < listaGrupo.size(); i++) {
             for (j = 0; j < listaGrupo.get(i).listaGrupoMensagem.size(); j++) {
             System.out.println(" Grupo: " + listaGrupo.get(i).getNomeGrupo() + " Utilizador Envio: " + listaGrupo.get(i).listaGrupoMensagem.get(j).getUsername() + " Mensagem: " + listaGrupo.get(i).listaGrupoMensagem.get(j).getMensagem());
             }
             }*/
        });
        msgGrupoThread.start();
        msgGrupoThread.interrupt();
    }

    @Override
    public int criarGrupo(String nomeGrupo, String username) { //Recebe do cliente uma Solicitacao para Criar um Grupo e Guarda no Servidor

        Thread criaGrupoThread = new Thread(() -> {
            int resposta = 0;
            int aux = 0;
            for (Grupo a : listaGrupo) {
                if (a.nomeGrupo.equals(nomeGrupo)) {
                    System.out.println("Este grupo já existe, tente outro nome para o grupo");
                    resposta = 0;
                    break;
                } else {
                    aux++;
                    if (aux == listaGrupo.size()) {
                        Grupo novoGrupo = new Grupo(nomeGrupo, username);
                        listaGrupo.add(novoGrupo);
                        resposta = 1;
                        break;
                    }
                }
            }
            synchronized (this) {
                respostaCriaGrupo = resposta;
            }
            /*Grupo novoGrupo = new Grupo(nomeGrupo, username);
             listaGrupo.add(novoGrupo);*/
        });
        criaGrupoThread.start();
        try {
            criaGrupoThread.sleep(300);
            criaGrupoThread.join();
            criaGrupoThread.interrupt();
        } catch (ConcurrentModificationException ex) {
            System.out.println("ConcurrentModificationException error: " + ex);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException error: " + e);
        } catch (InterruptedException exx) {
            System.out.println("InterruptedException" + exx);
        }
        return respostaCriaGrupo;

    }

    @Override
    public void entrarGrupo(String nomeGrupoEntrar, String username) { //Entra no Grupo que o Cliente requisitou
        Thread thread = new Thread(() -> {
            for (Grupo mGrupo : listaGrupo) {
                //System.out.println("Grupo: "+mGrupo.getNomeGrupo() + " Quero Entrar: "+nomeGrupoEntrar);
                if (mGrupo.getNomeGrupo().equals(nomeGrupoEntrar)) {
                    mGrupo.listaGrupoUtilizadores.add(username);
                    System.out.println("Adicionou " + username + " no Grupo : " + mGrupo.getNomeGrupo());
                }
            }
        });
        thread.start();
        thread.interrupt();
    }

    @Override
    public synchronized String listarGrupos() { //Coloca todos os Grupos registrados no servidor na variavel listaGrupos que retona ao cliente
        Thread listaGrupoThread = new Thread(() -> {
            //String listaGrupos = "\n";
            listaGruposGlobal = "\n";

            for (Grupo mGroup : listaGrupo) {
                //listaGrupos = listaGrupos + mGroup.getNomeGrupo() + " \n"; 
                listaGruposGlobal = listaGruposGlobal + mGroup.getNomeGrupo() + " \n";
            }
        });
        listaGrupoThread.start();
        try {
            listaGrupoThread.join();
            listaGrupoThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Listar Grupos Join/Interrupt: " + ex);
        }
        //return listaGrupos;
        return listaGruposGlobal;
    }

    @Override
    public synchronized String ChatGeral() { //Coloca todas as Mensagens Gerais registrados no servidor chatGeral que retona ao cliente
        Thread listaChatGeralThread = new Thread(() -> {
            //String chatGeral = "\n";

            chatGeralGlobal = "\n";
            Boolean estado;
            for (Mensagem mMsg : listaMensagem) {
                // System.out.println("Utilizadores: " + mMsg.getUsername() + "    " + " Estado: " + mMsg.getAutenticado());
                //chatGeral = chatGeral + mMsg.getUsername() + " Mensagem: " + mMsg.getMensagem() + "\n";
                chatGeralGlobal = chatGeralGlobal + mMsg.getUsername() + " Mensagem: " + mMsg.getMensagem() + "\n";
            }
        });
        listaChatGeralThread.start();
        try {
            listaChatGeralThread.join();
            listaChatGeralThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Listar Mensagens do Chat Geral Join/Interrupt: " + ex);
        }
        //return geral;
        return chatGeralGlobal;
    }

    @Override

    public synchronized String listarUtilizadores() { //Coloca todos os Utilizadores registrados no servidor na variavel utilizadoresSistema que retona ao cliente
        Thread listaUtilizadoresThread = new Thread(() -> {
            //String utilizadoresSistema = "\n";
            utilizadoresSistemaGlobal = "\n";
            Boolean estado;
            for (User mUser : listaUser) {
                // System.out.println("Utilizadores: " + mUser.getUsername() + "    " + " Estado: " + mUser.getAutenticado());
                //utilizadoresSistema = utilizadoresSistema + mUser.getUsername() + " Online: " + mUser.getAutenticado() + " \n";
                utilizadoresSistemaGlobal = utilizadoresSistemaGlobal + mUser.getUsername() + " Online: " + mUser.getAutenticado() + " \n";

            }

        });
        listaUtilizadoresThread.start();
        try {
            listaUtilizadoresThread.join();
            listaUtilizadoresThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Listar Utilizadores Registrados Join/Interrupt: " + ex);
        }
        //return utilizadoresSistema;
        return utilizadoresSistemaGlobal;
    }

    @Override
    public int guardaArquivo(String recebe, String caminho) { //Guarda o arquivo na listaArquivos no servidor em Memoria.

        Thread guardaArquivoThread = new Thread(() -> {
            File arq = new File(caminho);
            int i = 0, flag = 0;
            int separador1 = recebe.lastIndexOf("|!|");
            int separador2 = recebe.lastIndexOf("|*|"); // Formato da String utilizadorDestinoArquivo+"|!|"+username+"|*|"+data
            String utilizadorDestinoArquivo = recebe.substring(0, separador1);
            //System.out.println("Utilizador Destino do Arquivo: " + utilizadorDestinoArquivo);
            String utilizadorEnviandoArquivo = recebe.substring(separador1 + 3, separador2);
            //System.out.println("Utilizador Enviando o Arquivo: " + utilizadorEnviandoArquivo);
            for (i = 0; i < listaUser.size(); i++) {
                if (listaUser.get(i).getUsername().equals(utilizadorDestinoArquivo)) {
                    guardaArquivoGlobal = 1;
                    listaArquivos.put(recebe, arq);
                }
            }
            if (utilizadorDestinoArquivo == utilizadorEnviandoArquivo) {
                guardaArquivoGlobal = 4;
            }
            try {
                BufferedReader br = new BufferedReader(new FileReader(arq));
            } catch (FileNotFoundException ex) {
                System.out.println("Erro ao procurar arquivo: " + caminho + " Erro: " + ex);
                guardaArquivoGlobal = 2;
            } catch (NullPointerException e) {
                guardaArquivoGlobal = 3;
            }

            //enviouArquivos.put(utilizadorDestinoArquivo, utilizadorEnviandoArquivo);
            for (i = 0; i < listaArquivos.size(); i++) {
                try {
                    //File mFile = listaArquivos.get(utilizadorDestinoArquivo);
                    System.out.println("Arquivo na memoria do servidor: " + listaArquivos.get(recebe).getName() + " -- De:" + utilizadorEnviandoArquivo + " Para:" + utilizadorDestinoArquivo);
                } catch (NullPointerException e) {
                    System.out.println("Erro ao percorrer a lista de arquivos. Erro: " + e);
                }
            }
        });
        guardaArquivoThread.start();
        try {
            //thread.sleep(400);
            guardaArquivoThread.join();
            guardaArquivoThread.interrupt();
        } catch (InterruptedException ex) {
            System.out.println("Erro na Thread Listar Grupos Join/Interrupt: " + ex);
        }
        return guardaArquivoGlobal;

    }

    private static void utilizadorAtivoThread() { //Thread de Cada Utilizador

        //K = Username do Utilizador e V= Listener daquele Utilizador
        try {
            Set<String> keyset = listaUserInterface.keySet();

            for (String utilizador : keyset) {
                //System.out.println("Testando: " + utilizador);
                if (listaUserInterfaceTemThread.get(utilizador) == false) {

                    Thread userThread = new Thread(rmi, utilizador);
                    userThread.start();
                    //System.out.println("Startou a Thread do Utilizador X: " + utilizador);
                    listaUserInterfaceTemThread.put(utilizador, true);
                }
            }
        } catch (ConcurrentModificationException eConcorrencia) {
            //System.out.println("Erro ao percorrer a Lista Por ter alterado. Erro:" + eConcorrencia);
        } catch (NullPointerException eNullPointer) {
            //System.out.println("Erro Null Pointer percorrer a Lista Por ter alterado. Erro:" + eNullPointer);
        }
    }

    @Override
    public void run() {
        //System.out.println("RUNNNN FOREST RUNNN: ");
        for (;;) {
            try {
                Thread.sleep(2000);

            } catch (InterruptedException eInterup) {
                System.out.println(eInterup.getMessage());
            } catch (Exception e) {
                //interrupt(); //Mata a Thread
            }

            MensagemPrivadaListeners();
            MensagemGrupoListeners();
            ChatGeralListeners();
            ArquivoListeners();

        }
    }

    private void ArquivoListeners() {

        try {
            String utilizador = Thread.currentThread().getName();
            MensagemMonitor listenerUtilizador = listaUserInterface.get(utilizador);

            try {
                Set<String> keyset = listaArquivos.keySet();
                for (String chave : keyset) {
                    int separador1 = chave.lastIndexOf("|!|");
                    int separador2 = chave.lastIndexOf("|*|"); // Formato da String utilizadorDestinoArquivo+"|!|"+username+"|*|"+caminho
                    String utilizadorDestinoArquivo = chave.substring(0, separador1);
                    //System.out.println("Utilizador Destino do Arquivo: " + utilizadorDestinoArquivo);
                    String utilizadorEnviandoArquivo = chave.substring(separador1 + 3, separador2);
                    String dataEnvio = chave.substring(separador2 + 3);
                    //System.out.println("Utilizador Enviando o Arquivo: " + utilizadorEnviandoArquivo);
                    if (utilizador.equals(utilizadorDestinoArquivo)) { //Enquanto tem arquivos para receber
                        //if ((listaArquivos.containsKey(chave))) {
                        //String utilizadorEnviandoArquivo = enviouArquivos.remove(chave);
                        String msg;

                        File arquivo = listaArquivos.remove(chave); //Remove o arquivo da Lista em Memória

                        msg = "do Utilizador: " + utilizadorEnviandoArquivo;

                        listenerUtilizador.existeArquivoListeners(msg, dataEnvio);
                        listenerUtilizador.receberArquivo(arquivo); //Tenta enviar ao cliente

                        //}
                    }
                }
            } catch (RemoteException e) {
                System.out.println("ERRO no Listener, Removido" + e);
                removeListener(listenerUtilizador, utilizador);

            } catch (NullPointerException e) {
                System.out.println("ERRO Null Pointer Listener Arquivo, Removido" + e);
                removeListener(listenerUtilizador, utilizador);

            }
            //System.out.println("Key : " + k + ", Value : " + v);

        } catch (Exception e) {

            System.out.println("Exception: " + e);

        }
    }

    private void ChatGeralListeners() {

        try {

            String utilizador = Thread.currentThread().getName();
            MensagemMonitor listenerUtilizador = listaUserInterface.get(utilizador);
            try {
                for (Mensagem mMsg : listaMensagem) {
                    //System.out.println(" Mensagem: " + mMsg.getMensagem()+ "|| User Envio: " + mMsg.getUsername() + " User Destino: " + mMsg.getDestino());
                    if ((mMsg.getLida().equals(false)) && !(mMsg.getQuemleu().contains(utilizador))) {
                        //msg = msg + mMsg.getUsername() +" Online: " + mMsg.getAutenticado() + " \n";
                        String msg;
                        msg = mMsg.getMensagem();
                        listenerUtilizador.existeChatGeralListeners(msg, mMsg.getUsername(), mMsg.getData());
                        mMsg.addLida(utilizador);
                        if ((mMsg.getLidaGrupo()) == (listaUser.size())) {
                            mMsg.setLida(true);
                        }
                    }

                }
            } catch (RemoteException e) {
                System.out.println("ERRO no Listener, Removido" + e);
                removeListener(listenerUtilizador, utilizador);

            }
            //System.out.println("Key : " + k + ", Value : " + v);

        } catch (Exception e) {

            System.out.println("Exception: " + e);

        }
    }

    private void MensagemPrivadaListeners() {

        try {

            String utilizador = Thread.currentThread().getName();
            MensagemMonitor listenerUtilizador = listaUserInterface.get(utilizador);

            try {
                for (Mensagem mMsg : listaMensagemPrivada) {
                    //System.out.println(" Mensagem: " + mMsg.getMensagem()+ "|| User Envio: " + mMsg.getUsername() + " User Destino: " + mMsg.getDestino());
                    if ((mMsg.getLida().equals(false)) && (mMsg.getDestino().equals(utilizador))) {
                        //msg = msg + mMsg.getUsername() +" Online: " + mMsg.getAutenticado() + " \n";
                        String msg, userEnvio;
                        msg = mMsg.getMensagem();
                        userEnvio = mMsg.getUsername();
                        Date data = mMsg.getData();
                        listenerUtilizador.existeMensagemPrivadaListeners(msg, userEnvio, data);
                        mMsg.setLida(true);
                    }

                }
            } catch (RemoteException e) {
                System.out.println("ERRO no Listener, Removido" + e);
                removeListener(listenerUtilizador, utilizador);

            }

        } catch (Exception e) {

            System.out.println("Exception: " + e);

        }
    }

    private void MensagemGrupoListeners() {   //String username,MensagemClientInterface listener) {

        try {

            String utilizador = Thread.currentThread().getName();
            MensagemMonitor listenerUtilizador = listaUserInterface.get(utilizador);

            for (Grupo mGrupo : listaGrupo) {
                //System.out.println(" Mensagem: " + mMsg.getMensagem()+ "|| User Envio: " + mMsg.getUsername() + " User Destino: " + mMsg.getDestino());

                for (Mensagem mMsg : mGrupo.listaGrupoMensagem) {

                    for (String mUserString : mGrupo.listaGrupoUtilizadores) {
                        //System.out.println("mUserString: " + mUserString + " e Usuario Ouvindo: " + k);
                        //System.out.println("Utilizadores do Grupo: " + mGrupo.getListaUtilizadoresDoGrupo(mGrupo.getNomeGrupo()));
                        if (mGrupo.getListaUtilizadoresDoGrupo(mGrupo.getNomeGrupo()).contains(utilizador) && mMsg.getDestino().equals(mGrupo.getNomeGrupo())) {
                            if ((mMsg.getLida() == false) && !(mMsg.getQuemleu().contains(utilizador))) {//quem recebeu  //(mUserString == username) &&

                                System.out.println("Esta procurando na lista de mensagensssss : " + mMsg.getMensagem());
                                String msg = mMsg.getMensagem();
                                String grupoEnvio = mMsg.getDestino();
                                Date data = mMsg.getData();
                                try {
                                    listenerUtilizador.existeMensagemGrupoListeners(msg, grupoEnvio, data);
                                } catch (RemoteException ex) {
                                    System.out.println(ex);
                                }
                                mMsg.addLida(utilizador);
                                if ((mMsg.getLidaGrupo()) == (mGrupo.listaGrupoUtilizadores.size())) {
                                    mMsg.setLida(true);
                                }
                            }
                        }
                    }

                }

            }

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    @Override
    public void addListener(MensagemMonitor clientInterface, String username) {
        listaUserInterface.put(username, clientInterface);
        listaUserInterfaceTemThread.put(username, false);
        //System.out.println("Entrou um UserListener Novo!" + clientInterface);
    }

    @Override
    public void removeListener(MensagemMonitor clientInterface, String username
    ) {
        listaUserInterface.remove(username);
        listaUserInterfaceTemThread.remove(username);
        //System.out.println("Removeu um UserListener" + clientInterface);
    }

    public static void main(String args[]) throws Exception {

        criaPadrao();

        System.setSecurityManager(
                new RMISecurityManager());
        try {
            rmi = new MensagemServiceServer();

            LocateRegistry.createRegistry(1099);
            System.out.println("Registry criado");

            Naming.rebind("//localhost:1099/IRCServer", rmi);

            System.out.println("Servidor Bindando na Porta 1099");

            System.out.println("Esperando Clientes....");

            while (true) {

                utilizadorAtivoThread();
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
