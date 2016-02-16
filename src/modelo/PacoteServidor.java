/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Hardware
 */
public class PacoteServidor implements Serializable{    
    
    public static final String SERVIDOR_REMETENTE = "servidor";
    
    private Date data;
    private Usuario usuario;
    private String mensagem;
    private List<Usuario> usuariosOnline;
    private List<Usuario> usuariosDigitando;
    private List<Usuario> usuariosVisualizando;       

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<Usuario> getUsuariosOnline() {
        return usuariosOnline;
    }

    public void setUsuariosOnline(List<Usuario> usuariosOnline) {
        this.usuariosOnline = usuariosOnline;
    }

    public List<Usuario> getUsuariosDigitando() {
        return usuariosDigitando;
    }

    public void setUsuariosDigitando(List<Usuario> usuariosDigitando) {
        this.usuariosDigitando = usuariosDigitando;
    }

    public List<Usuario> getUsuariosVisualizando() {
        return usuariosVisualizando;
    }

    public void setUsuariosVisualizando(List<Usuario> usuariosVisualizando) {
        this.usuariosVisualizando = usuariosVisualizando;
    }

    @Override
    public String toString() {
        return "PacoteServidor{" + "data=" + data + ", usuario=" + usuario + ", mensagem=" + mensagem + ", usuariosOnline=" + usuariosOnline + ", usuariosDigitando=" + usuariosDigitando + ", usuariosVisualizando=" + usuariosVisualizando + '}';
    }
    
    
}
