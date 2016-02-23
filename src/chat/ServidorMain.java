/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import facade.GerenciadorArquivo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import view.ServerForm;

/**
 *
 * @author Hardware
 */
public class ServidorMain {

    private Server servidor;
    private ServerForm tela;
    private boolean online;

    public ServidorMain() {
        tela = new ServerForm();
        carregarTela();
        carregarBotoes();
        tela.setVisible(true);        
    }

    private void carregarTela() {
        atualizarBotoes();
    }

    public void atualizarTela() {
        if (servidor != null) {
            long usuarios = servidor.getQuantidadeUsuariosOnline();
            long pacotesRecebidos = servidor.getContadorPacotesRecebidos();
            long pacotesEnviados = servidor.getContadorPacotesEnviados();
            long totalPacote = servidor.getContadorPacotesRecebidos() + servidor.getContadorPacotesEnviados();
            tela.jLabelUsuarios.setText(String.valueOf(usuarios));
            tela.jLabelPacotesRecebidos.setText(String.valueOf(pacotesRecebidos));
            tela.jLabelPacotesEnviados.setText(String.valueOf(pacotesEnviados));
            tela.jLabelPacotesTotais.setText(String.valueOf(totalPacote));
            try {
                tela.jLabelEndereco.setText(servidor.getEndereco());
            } catch (UnknownHostException ex) {
                tela.jLabelEndereco.setText("");
            }
        }
    }

    private void atualizarBotoes() {
        tela.jButtonIniciar.setEnabled(!online);
        tela.jTextFieldPorta.setEnabled(!online);
        tela.jButtonParar.setEnabled(online);        
    }

    private void carregarBotoes() {
        tela.jButtonIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarServidor();
                atualizarBotoes();
            }
        });

        tela.jButtonParar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pararServidor();
                atualizarBotoes();
            }
        });
    }

    private void iniciarServidor() {
        new ServidorThread().start();

    }

    private void pararServidor() {
        if (servidor != null && online) {
            guardarHistorioMensagens();
            servidor.stop();
        }
    }
    
    private void guardarHistorioMensagens(){
        try {
            GerenciadorArquivo.escreverArquivo(servidor.getMensagens());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void limparTela() {
        tela.jLabelUsuarios.setText("0");
        tela.jLabelPacotesRecebidos.setText("0");
        tela.jLabelPacotesEnviados.setText("0");
        tela.jLabelPacotesTotais.setText("0");
        tela.jLabelEndereco.setText("");
    }

    private class ServidorThread extends Thread {

        @Override
        public void run() {
            limparTela();
            int porta = Integer.parseInt(tela.jTextFieldPorta.getText());
            servidor = new Server(porta);
            servidor.setTela(ServidorMain.this);
            atualizarTela();
            try {
                online = true;
                atualizarBotoes();
                servidor.start();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            online = false;
            servidor = null;
            limparTela();
            atualizarBotoes();
        }
    }

    public static void main(String[] args) {
        new ServidorMain();
    }
}
