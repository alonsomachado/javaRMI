import java.rmi.*;
import java.rmi.Naming;
import java.io.*;
import javax.rmi.PortableRemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public class MensagemServiceClient extends UnicastRemoteObject implements MensagemMonitor {

    // private static MensagemServiceClient service; //Interface RMI para acesso ao servidor
    public static String enderecoServidor = "rmi://localhost:1099/IRCServer";
    static String username = "", password = "";

    protected MensagemServiceClient() throws RemoteException {
    }

    public static void main(String args[]) throws Exception {

        //Instancia o Security Manager para o RMI
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        // Criando registry do MensagemService
        MensagemService service = (MensagemService) Naming.lookup(enderecoServidor);
        // Criando leitor do Terminal
        DataInputStream din = new DataInputStream(System.in);
        //Construir um objeto dele mesmo
        MensagemServiceClient cliente = new MensagemServiceClient();
        //Flag de configuracao para verificar se o utilizador esta logado
        Boolean loggado = false;
        //Output Stream para Receber Arquivos
        BufferedOutputStream output;

        do {
            while (loggado == false) {
                System.out.println("************* Registrar/Login *************");
                System.out.println("1- Registrar");
                System.out.println("2- Login");

                DataInputStream din2 = new DataInputStream(System.in);
                String line, lina;
                int resposta, regis;

                Integer choice3;

                System.out.print("Escolha: ");
                lina = din.readLine();
                choice3 = new Integer(lina);
                regis = choice3.intValue();

                switch (regis) {
                    case 1: //Registo
                        System.out.println("*******Registrar****************");
                        System.out.print("Username: ");
                        line = din2.readLine();
                        username = new String(line);
                        while (username.length() < 3) {
                            System.out.println("Username deve ter mais de 3 caracteres!");
                            System.out.print("Username: ");
                            line = din2.readLine();
                            username = new String(line);
                        }

                        System.out.print("Password: ");
                        line = din2.readLine();
                        password = new String(line);
                        while (password.length() < 3) {
                            System.out.println("Password deve ter mais de 3 caracteres!");
                            System.out.print("Password: ");
                            line = din2.readLine();
                            password = new String(line);
                        }

                        Boolean registrou = service.registro(username, password);
                        if (registrou == true) {
                            System.out.println("Novo Utilizador Registrado!");
                        }
                        if (registrou == false) {
                            System.out.println("Existe Utilizador Registrado com este Username!");
                        }
                        //service.registo(this.cliente, username, password);
                        //loggado=registrou; //Registrar e Entrar Direto no Sistema

                        break;
                    case 2: //Login
                        System.out.println("*******Login****************");
                        System.out.print("Username: ");
                        line = din2.readLine();
                        username = new String(line);
                        while (username.length() < 3) {
                            System.out.println("Username deve ter mais de 3 caracteres!");
                            System.out.print("Username: ");
                            line = din2.readLine();
                            username = new String(line);
                        }

                        System.out.print("Password: ");
                        line = din2.readLine();
                        password = new String(line);
                        while (password.length() < 3) {
                            System.out.println("Password deve ter mais de 3 caracteres!");
                            System.out.print("Username: ");
                            line = din2.readLine();
                            password = new String(line);
                        }

                        try {
                            resposta = service.login(username, password);
                            if (resposta == 0) {
                                System.out.println("\nPassword errada para este Utilizador!");
                                loggado = false;
                            }
                            if (resposta == 1) {
                                System.out.println("\nLogin feito com Sucesso!");
                                loggado = true;
                            }
                            if (resposta == 2) {
                                System.out.println("\nUtilizador encontra-se Logado em outro Terminal!");
                                loggado = false;
                            }
                            if (resposta == 3) {
                                System.out.println("\nTentativa de Login sem nenhum Utilizador Cadastrado!");
                                loggado = false;
                            }
                            if (resposta == 4) {
                                System.out.println("\nUtilizador Inexistente!");
                                loggado = false;
                            }
                        } catch (Exception e) {
                            System.out.println("\nERRO No Registro: " + e.getMessage());
                        }
                        if (loggado == true) { //Criar no Servidor um Listener deste cliente
                            service.addListener(cliente, username);
                        }

                        break;
                }
            }
            while (loggado != false) { //While do Menu Principal
                menuPrincipal();

                String linha, gruporeader, listaUtilizadores, nomegrupo;
                Integer choice, choice2;
                int value, grupoSwitch;

                System.out.print("Escolha: ");
                linha = din.readLine();
                choice = new Integer(linha);
                value = choice.intValue();

                switch (value) {
                    case 1: //Chat geral com todos os utilizadores
                        System.out.println();
                        System.out.println("********** Chat Geral**********");
                        String chatgeral = service.ChatGeral();
                        System.out.println(chatgeral);
                        System.out.print("Introduza a sua Mensagem para o Chat Geral: ");
                        linha = din.readLine();
                        String mensagem = new String(linha);
                        service.MensagemGeral(username, mensagem);
                        break;
                    case 2: //Lista os utilizadores e depois pede a mensagem privada
                        System.out.println();
                        System.out.println("********** Utilizadores do Sistema **********");
                        listaUtilizadores = service.listarUtilizadores(); //lista todos os utilizadores do sistema
                        System.out.println("Lista de Utilizadores: " + listaUtilizadores);

                        //Mensagem Privada
                        System.out.print("Mensagem: ");
                        linha = din.readLine();
                        String mensagem_privada = new String(linha);

                        System.out.print("Utilizador Destino: ");
                        linha = din.readLine();
                        String utilizadorDestino = new String(linha);

                        //Verifica se o utilizador é igual ao utilizador de destino
                        if ((utilizadorDestino.equals(username))) {
                            System.out.println("Está a tentar enviar uma mensagem para si");
                        } else if (listaUtilizadores.contains(utilizadorDestino) && (!(utilizadorDestino.equals(username)))) { //Verificar se utilizadorDestino Existe antes de mandar
                            service.MensagemPrivada(username, mensagem_privada, utilizadorDestino);
                            System.out.println("Mensagem enviada para: " + utilizadorDestino + " com sucesso");
                        } else {
                            System.out.println("ERRO: Utilizador destino da mensagem privada não existe");
                        }
                        break;
                    case 3: //Lista os Grupos do IRC 
                        System.out.println("********** Grupos **********");
                        String grupos = service.listarGrupos(); //lista os nomes dos grupos
                        System.out.println("----------------- ");
                        System.out.println("Lista de Grupos: " + grupos);
                        System.out.println("----------------- ");
                        Boolean grupowhile = true;

                        while (grupowhile == true) {
                            menuGrupos();
                            System.out.print("Escolha: ");
                            gruporeader = din.readLine();
                            choice2 = new Integer(gruporeader);
                            grupoSwitch = choice2.intValue();
                            switch (grupoSwitch) {
                                case 1: //Mandar Mensagem para um Grupo
                                    System.out.print("Mensagem: ");
                                    linha = din.readLine();
                                    String mensagemGrupo = new String(linha);

                                    System.out.print("Grupo Destino: ");
                                    linha = din.readLine();
                                    String grupoDestino = new String(linha);

                                    //Verificar se grupoDestino Existe antes de mandar
                                    if (grupos.contains(grupoDestino)) {
                                        service.MensagemGrupo(username, mensagemGrupo, grupoDestino);
                                        System.out.println("Mensagem de grupo enviada para: " + grupoDestino + " com sucesso");
                                        break;
                                    } else {
                                        System.out.println("ERRO: Grupo destino da mensagem não existe");
                                        break;
                                    }
                                case 2: //Criar novo Grupo de Chat
                                    System.out.print("Nome do Novo Grupo: ");
                                    linha = din.readLine();
                                    nomegrupo = new String(linha);
                                    Integer res = service.criarGrupo(nomegrupo, username);
                                    if (res == 1) {
                                        System.out.println("Grupo " + nomegrupo + " está sendo criado");
                                    }
                                    if (res == 0) {
                                        System.out.println("O nome do grupo já existe, por favor tente outro nome!");
                                    }
                                    grupowhile = false;
                                    break;
                                case 3: //Entrar em um Grupo de Chat
                                    System.out.print("Nome do Grupo: ");
                                    linha = din.readLine();
                                    nomegrupo = new String(linha);
                                    service.entrarGrupo(nomegrupo, username);
                                    System.out.println("Voce entrou no " + nomegrupo + " Grupo de Chat do IRC ESTG/IPP/CDN_2019");
                                    break;
                                case 4: //Voltar ao menu Principal
                                    grupowhile = false;
                                    break;
                                default:
                                    System.out.println("Numero Invalido! Somente 1 a 3");
                                //break;
                            }
                        }
                        break;
                    case 4: //Enviar arquivo
                        System.out.println("********** Enviar Arquivos **********");
                        System.out.println("Pode ser enviado mesmo para Utilizador Offline");
                        listaUtilizadores = service.listarUtilizadores(); //lista todos os utilizadores do sistema
                        System.out.println("Lista de Utilizadores: " + listaUtilizadores);

                        System.out.print("Utilizador Destino do Arquivo: ");
                        linha = din.readLine();
                        String utilizadorDestinoArquivo = new String(linha);

                        System.out.print("Caminho: ");
                        linha = din.readLine();
                        String caminho = new String(linha);
                        int separador1 = caminho.lastIndexOf("|!|"); // Formato da String utilizadorDestinoArquivo+"|!|"+username+"|*|"+data
                        int separador2 = caminho.lastIndexOf("|*|"); // Formato da String utilizadorDestinoArquivo+"|!|"+username+"|*|"+data
                        //int separador3 = caminho.lastIndexOf("|#|");
                        try {
                            Date data = new Date();
                            DateFormat osLocalizedDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
                            int enviadocomsucesso = service.guardaArquivo(utilizadorDestinoArquivo + "|!|" + username + "|*|" + osLocalizedDateFormat.format(new Date()), caminho); // Formato da String utilizadorDestinoArquivo+"|!|"+username+"|*|"+data
                            if (enviadocomsucesso == 0) {
                                System.out.println("Utilizador nao existe no Servidor");
                            }
                            if (enviadocomsucesso == 1) {
                                System.out.println("Envio de arquivo para o servidor com Sucesso");
                            }
                            if (enviadocomsucesso == 2) {
                                System.out.println("Arquivo nao encontrado");
                            }
                            if (enviadocomsucesso == 3) {
                                System.out.println("Erro na gravacao do Arquivo leitura do BuffReader");
                            }
                            if (enviadocomsucesso == 4) {
                                System.out.println("O sistema nao permite enviar arquivos para si mesmo!! ");
                            }
                        } catch (Exception e) {
                            System.out.println("ERRO Ao enviar Arquivo ao Servidor: " + e.getMessage());
                        }

                        break;

                    case 5: //Log out do sistema
                        service.logOut(username);
                        loggado = false;
                        service.removeListener(cliente, username);
                        System.out.println("Logout terminada com Sucesso");
                        break;

                    case 6: //Sair do sistema
                        service.logOut(username);
                        loggado = false;
                        service.removeListener(cliente, username);
                        System.exit(1);
                    default:
                        System.out.println("Numero Invalido! Somente 1 a 6");
                        System.out.println();
                        break;
                }
            }

        } while (true);

    }

    public static void menuPrincipal() {
        System.out.println("\n");
        System.out.println("********************  Menu Principal IRC ESTG/IPP/CDN_2019 ********************");
        System.out.println("    1 - CHAT GERAL");
        System.out.println("    2 - Listar Utilizadores (Mensagem Privada)");
        System.out.println("    3 - Grupos de Chat do IRC");
        System.out.println("    4 - Enviar Arquivo");
        System.out.println("    5 - Logout");
        System.out.println("    6 - Sair");
        System.out.println();
    }

    public static void menuGrupos() {
        System.out.println("   1 - Mandar Mensagem para um Grupo");
        System.out.println("   2 - Criar um Novo Grupo no IRC");
        System.out.println("   3 - Entrar em um Grupo no IRC");
        System.out.println("   4 - Voltar ao Menu Principal");
        System.out.println();
    }

    @Override
    public void existeMensagemPrivadaListeners(String msg, String userEnvio, Date data) {
        DateFormat osLocalizedDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        System.out.print("\n (Mensagem Privada)" + userEnvio + "-" + osLocalizedDateFormat.format(new Date()) + ":  " + msg);
    }

    @Override
    public void existeMensagemGrupoListeners(String msg, String grupoEnvio, Date data) {
        DateFormat osLocalizedDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        System.out.println("\n (Mensagem de Grupo)" + grupoEnvio + "-" + osLocalizedDateFormat.format(new Date()) + ": " + msg);
    }

    @Override
    public void existeChatGeralListeners(String msg, String userEnvio, Date data) {
        DateFormat osLocalizedDateFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
        System.out.println("\n (Chat Geral)" + userEnvio + "-" + osLocalizedDateFormat.format(new Date()) + ": " + msg);
    }

    @Override
    public void existeArquivoListeners(String arquivo, String data) {
        System.out.println("\nRecebeu um arquivo " + "(" + data + "): " + arquivo);
    }

    @Override
    public void receberArquivo(File arquivo) {
        try {
            File recebido = arquivo;
            InputStream inputStream = new FileInputStream(recebido);
            Boolean pastaCriada = new File("./Download/" + username + "/").mkdirs(); //"C:/Users/alons/Documents/NetBeansProjects/IRC/src/Download/" + username + "/" //TESTAR username + "/"
            OutputStream outputStream = new FileOutputStream("./Download/" + username + "/" + recebido.getName()); //"C:/Users/alons/Documents/NetBeansProjects/IRC/src/Download/" + username + "/" + recebido.getName());
            int bytesRead = -1;
            byte[] buffer = new byte[2048];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            outputStream.close();
            System.out.println(" O ficheiro: " + recebido.getName() + " esta na sua Pasta DOWNLOAD");

        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo a ser Recebido teve problemas: " + ex);
        } catch (IOException ex) {
            System.out.println("Arquivo Recebido com erro " + ex);
        } catch (NullPointerException e) {
            //System.out.println("Arquivo Recebido deu Null Pointer com erro " + e);
        }
    }

}
