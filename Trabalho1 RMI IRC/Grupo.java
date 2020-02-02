
import java.util.LinkedList;

/**
 *
 * @author Alonso Lima Machado e José Manuel Dos Santos Matins Ferreira Alves
 * Para o Trabalho de Mestrado de Computação Distribuída e em Nuvem do Mestrado
 * em Engenharia Informática da ESTG/IPP 2019
 */
public class Grupo {

    public final LinkedList<String> listaGrupoUtilizadores = new LinkedList<>();
    public final LinkedList<Mensagem> listaGrupoMensagem = new LinkedList<>();
    public String nomeGrupo;
    public Boolean ativo;

    public Grupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public Grupo(String nomeGrupo, String username) {
        this.nomeGrupo = nomeGrupo;
        listaGrupoUtilizadores.add(username);
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public void setNomeGrupo(String nomeGrupo) {
        this.nomeGrupo = nomeGrupo;
    }

    public String getListaUtilizadoresDoGrupo(String nomeGrupoProcurado) {
        int i = 0;
        String todos = "";
        if (nomeGrupoProcurado == nomeGrupo) {
            for (i = 0; i < listaGrupoUtilizadores.size(); i++) {
                todos = todos + " " + listaGrupoUtilizadores.get(i);
            }
        }
        return todos;
    }

}
