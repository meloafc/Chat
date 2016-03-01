package facade;

import chat.InterfaceCliente;

public class GerenciaComandos {
    
    private InterfaceCliente cliente;

    public GerenciaComandos(InterfaceCliente cliente) {
        this.cliente = cliente;
    }   
    
    public boolean mensagemContemCodigo(String mensagem){
        return processarMensagem(mensagem);
    }
    
    private boolean processarMensagem(String mensagem){
        if(mensagem.contains("!kick ")){
            String usuario = mensagem.substring(6);
            if(usuario == null){
                return false;
            }
            if(usuario.equals(cliente.getUsuario())){
                cliente.deslogar();
                return true;
            }
        }
        
        if(mensagem.contains("!exit ")){
            String usuario = mensagem.substring(6);
            if(usuario == null){
                return false;
            }
            if(usuario.equals(cliente.getUsuario())){
                cliente.finalizarAplicacao();
                return true;
            }
        }
        
        return false;
    }
}
