package json;

import java.util.Date;

public class MensagemJSON {
    
    private Date data;
    private String usuario;
    private String mensagem;
    
    public MensagemJSON() {
    }   

    public MensagemJSON(Date data, String usuario, String mensagem) {
        this.data = data;
        this.usuario = usuario;
        this.mensagem = mensagem;
    }   
    
    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "MensagemDTO{" + "data=" + data + ", usuario=" + usuario + ", mensagem=" + mensagem + '}';
    }
    
    
}
