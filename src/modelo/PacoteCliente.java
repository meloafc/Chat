/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modelo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Hardware
 */
public class PacoteCliente implements Serializable{
    
    private Date data;
    private Usuario usuario;
    private String mensagem;
    private boolean deslogar;
    private boolean digitando;
    private boolean visualizando;

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

    public boolean isDeslogar() {
        return deslogar;
    }

    public void setDeslogar(boolean deslogar) {
        this.deslogar = deslogar;
    }

    public boolean isDigitando() {
        return digitando;
    }

    public void setDigitando(boolean digitando) {
        this.digitando = digitando;
    }

    public boolean isVisualizando() {
        return visualizando;
    }

    public void setVisualizando(boolean visualizando) {
        this.visualizando = visualizando;
    }

    @Override
    public String toString() {
        return "PacoteCliente{" + "data=" + data + ", usuario=" + usuario + ", mensagem=" + mensagem + ", deslogar=" + deslogar + ", digitando=" + digitando + ", visualizando=" + visualizando + '}';
    }
    
    

}
