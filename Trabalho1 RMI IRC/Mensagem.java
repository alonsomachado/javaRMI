import java.util.Date;

/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public class Mensagem {

    String mensagem, username, destino,quemleu;
    Date data;
    Boolean lida;
    int lidaGrupo;
    

    public Mensagem(String username, String mensagem, String destino) {
        this.mensagem = mensagem;
        this.username = username;
        this.destino = destino;
        this.data = new Date();
        this.lida = false;
        this.lidaGrupo=0;
		this.quemleu="";
    }

    public Mensagem(String username, String mensagem) {
        this.mensagem = mensagem;
        this.username = username;
        this.data = new Date();
        this.lida = false;
        this.lidaGrupo=0;
		this.quemleu="";
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean leu) {
        this.lida = leu;
    }

    public int getLidaGrupo() {
        return lidaGrupo;
    }

    public String getQuemleu() {
        return quemleu;
    }
    
    public void addLida(String username){
        this.lidaGrupo=lidaGrupo+1;
        if(!quemleu.contains(username)){
        this.quemleu=quemleu+" "+username;
        }
    }

}
