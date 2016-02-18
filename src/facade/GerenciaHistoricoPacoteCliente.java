package facade;

import java.util.ArrayList;
import java.util.List;
import modelo.PacoteCliente;
import modelo.Usuario;

public class GerenciaHistoricoPacoteCliente {
    
    private Usuario usuario;
    private List<PacoteCliente> mensagensDigitadas;
    private int posicao;
    private String mensagemAtual;
    
    public GerenciaHistoricoPacoteCliente(Usuario usuario){
        this.usuario = usuario;
        mensagensDigitadas = new ArrayList<>();
        posicao = 0;
        mensagemAtual = "";
    }
    
    public void resetarPosicao(){
        posicao = mensagensDigitadas.size();
    }
    
    public String getMensagemAnterior(){
        posicao--;
        if(posicao >= 0){
            String mensagem = mensagensDigitadas.get(posicao).getMensagem();        
            return mensagem;
        } else {
            posicao = 0;
            if(mensagensDigitadas.size() != 0){
                return mensagensDigitadas.get(posicao).getMensagem(); 
            } else {
                return "";
            }
        }
        
    }
    
    public String getProximaMensagem(){
        posicao++;
        if(posicao < mensagensDigitadas.size()){
            String mensagem = mensagensDigitadas.get(posicao).getMensagem();        
            return mensagem;
        } else {
            resetarPosicao();
            return "";
        }
        
    }
    
    public void addMensagem(PacoteCliente pacote){        
        mensagensDigitadas.add(pacote);
        resetarPosicao();
    }
}
