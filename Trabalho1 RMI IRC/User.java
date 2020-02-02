
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public class User {

    String username, password;
    Boolean autenticado;
    Date dataUltimaSessao;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.autenticado = false;
        this.dataUltimaSessao = new Date();
    }

    public User(String username, String password, Boolean autenticado) {
        this.username = username;
        this.password = password;
        this.autenticado = autenticado;
        this.dataUltimaSessao = new Date();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }

    public Date getDataUltimaSessao() {
        return dataUltimaSessao;
    }

    public void setDataUltimaSessao(Date datalogout) {
        this.dataUltimaSessao = datalogout;
    }

}
