/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import modelo.Emoticon;
import modelo.PacoteCliente;
import modelo.PacoteServidor;
import modelo.Usuario;
import view.ChatForm;
import view.LoginForm;

/**
 *
 * @author Hardware
 */
public class ClienteMain extends Cliente {

    private ChatForm chatView;
    private LoginForm loginView;
    StyledDocument doc;
    SimpleAttributeSet minhaFonte;
    SimpleAttributeSet outraFonte;
    SimpleAttributeSet alertaFonte;
    private boolean connected;
    private List<Usuario> usuariosVisualizandoTela;
    private List<Usuario> usuariosEscrevendo;
    private boolean mensagemRecebida;

    private boolean focus;
    private boolean digitando;

    private KeyStroke keyStroke;
    private static final String enter = "ENTER";

    private Action enviar = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent ae) {
            chatView.jButtonEnviar.doClick();
        }
    };

    public ClienteMain() {
        super();
        loginView = new LoginForm();
        chatView = new ChatForm();
        loginView.setVisible(true);
        carregarTelaLogin();
        connected = false;
        carregarFonte();
        carregarTelaChat();
    }

    private void carregarTelaLogin() {
        loginView.jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fazerLogin();
            }
        });

        loginView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                loginView.dispose();
            }
        });
    }

    private void carregarTelaChat() {
        usuariosVisualizandoTela = new ArrayList<>();
        chatView.jButtonEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });

        chatView.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                chatView.dispose();
                deslogar();
            }
        });

        keyStroke = KeyStroke.getKeyStroke(enter);
        Object actionKey = chatView.jTextPaneMesagem.getInputMap(
                JComponent.WHEN_FOCUSED).get(keyStroke);
        chatView.jTextPaneMesagem.getActionMap().put(actionKey, enviar);
        chatView.jTextPaneMesagem.getStyledDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                campoMensagemAlterado();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                campoMensagemAlterado();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                campoMensagemAlterado();
            }

        });

        chatView.addWindowFocusListener(new WindowFocusListener() {

            @Override
            public void windowGainedFocus(WindowEvent e) {
                focus = true;
                atualizarFocus();
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                focus = false;
                atualizarFocus();
            }
        });

    }

    private void fazerLogin() {
        enderecoServidor = loginView.jTextFieldEndereco.getText();
        porta = Integer.parseInt(loginView.jTextFieldPorta.getText());
        String nome = loginView.jTextFieldUsuario.getText();
        usuario = new Usuario();
        usuario.setNome(nome);
        if (start()) {
            loginView.setVisible(false);
            chatView.setVisible(true);
            chatView.jLabelUsuario.setText(usuario.getNome());
            connected = true;
        }
    }

    private void enviarMensagem() {
        if (connected) {
            if (!chatView.jTextPaneMesagem.getText().equals("")) {
                PacoteCliente pacote = gerarPacoteCliente(chatView.jTextPaneMesagem.getText());
                enviarPacote(pacote);
                chatView.jTextPaneMesagem.setText("");
            }
        }
    }

    private void deslogar() {
        if (connected) {
            enviarPacote(gerarPacoteClienteDeslogar());
        }
    }

    private void campoMensagemAlterado() {
        if (chatView.jTextPaneMesagem.getText().length() > 0) {
            if (digitando == false) {
                digitando = true;
                enviarPacote(gerarPacoteClienteIsDigitando());
            }
        } else {
            if (digitando == true) {
                digitando = false;
                enviarPacote(gerarPacoteClienteIsDigitando());
            }
        }
    }

    private void atualizarFocus() {
        if (mensagemRecebida) {
            enviarPacote(gerarPacoteClienteIsVisualizando(false));
            mensagemRecebida = false;
            return;
        }
        enviarPacote(gerarPacoteClienteIsVisualizando());
    }

    @Override
    protected void conexaoFalhou() {
        connected = false;
    }

    @Override
    protected void processarPacoteRecebido(PacoteServidor pacoteServidor) {
        usuariosVisualizandoTela = pacoteServidor.getUsuariosVisualizando();
        usuariosEscrevendo = pacoteServidor.getUsuariosDigitando();
        atualizarTela();

        if (!pacoteServidor.getMensagem().equals("")) {
            if (pacoteServidor.getUsuario().getNome().equals(usuario.getNome())) {
                escreverTexto(pacoteServidor, minhaFonte);
            } else if (pacoteServidor.getUsuario().getNome().equals(PacoteServidor.SERVIDOR_REMETENTE)) {
                escreverTexto(pacoteServidor, alertaFonte);
            } else {
                escreverTexto(pacoteServidor, outraFonte);
            }
        }
    }

    @Override
    //deve alterar para pegar a data do servidor
    protected void alerta(String mensagem) {
        escreverTexto(new Date(), PacoteServidor.SERVIDOR_REMETENTE, mensagem, alertaFonte);
    }

    private void escreverTexto(PacoteServidor pacote, SimpleAttributeSet fonte) {
        escreverTexto(pacote.getData(), pacote.getUsuario().getNome(), pacote.getMensagem(), fonte);
    }

    private void escreverTexto(Date data, String usuario, String mensagem, SimpleAttributeSet fonte) {
        escreverPrefixoMensagem(data, usuario, fonte);
        String restoMensagem = processarMensagem(mensagem);
        escreverMensagem(restoMensagem, true);
        chatView.jTextPaneChat.setCaretPosition(doc.getLength() - 1);

        if (!chatView.isActive()) {
            chatView.toFront();
            chatView.toBack();
        }
    }

    private void escreverPrefixoMensagem(Date data, String usuario, SimpleAttributeSet fonte) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String dataFormatada = sdf.format(data);
        try {
            doc.insertString(doc.getLength(), "(" + dataFormatada + ")" + usuario + ": ", fonte);
        } catch (BadLocationException ex) {
            
        }
    }
    
    private void escreverMensagem(String mensagem) {
        escreverMensagem(mensagem, false);
    }

    private void escreverMensagem(String mensagem, boolean pulaLinha) {
        try {
            
            if (pulaLinha) {
                doc.insertString(doc.getLength(), mensagem + "\n", null);
            } else {
                doc.insertString(doc.getLength(), mensagem, null);
            }
            
        } catch (BadLocationException ex) {
            
        }

    }
    
    private void escreverEmoticon(String key) {        
        if(Emoticon.isValid(key)){
            String url = Emoticon.emoticons.get(key);
            try {
                doc.insertString(doc.getLength(), "mensagem ignorada", getEmoticonImagem(url));
            } catch (BadLocationException ex) {
                
            }
        }
    }
    
    private String processarMensagem(String mensagem){
        for(String chave : Emoticon.emoticons.keySet()){
            boolean contemEmoticon = mensagem.contains(chave);
            if(contemEmoticon){
                int posicaoInicialEmoticon = mensagem.indexOf(chave);
                String prefixoMensagem = mensagem.substring(0, posicaoInicialEmoticon);
                escreverMensagem(prefixoMensagem);
                escreverEmoticon(chave);
                mensagem = mensagem.substring(posicaoInicialEmoticon+chave.length());
            }
        }
        return mensagem;
    }

//    private void processarMensagem(String mensagem) {
//        boolean lendoEmoticon = false;
//        String texto = "";
//        String emoticonTexto = "";
//        for (int i = 0; i < mensagem.length(); i++) {
//            Character caracter = mensagem.charAt(i);
//            if (caracter.equals(':') || caracter.equals(';') || lendoEmoticon) {
//                lendoEmoticon = true;
//                emoticonTexto += caracter;
//                Emoticon emoticon = new Emoticon(emoticonTexto);
//                if (emoticon.isValid()) {
//                    try {
//                        doc.insertString(doc.getLength(), texto, null);
//                        doc.insertString(doc.getLength(), mensagem, getEmoticonImagem(emoticon));
//                        emoticonTexto = "";
//                        texto = "";
//                        lendoEmoticon = false;
//                    } catch (BadLocationException ex) {
//                    }
//                }
//            } else {
//                texto += caracter;
//            }
//        }
//        try {
//            doc.insertString(doc.getLength(), texto + emoticonTexto + "\n", null);
//        } catch (BadLocationException ex) {
//            Logger.getLogger(ClienteMain.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    private Style getEmoticonImagem(String path) {
        ClassLoader cl = getClass().getClassLoader();
        URL url = cl.getResource(path);
        ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit().createImage(url));
        StyleContext context = new StyleContext();
        Style labelStyle = context.getStyle(StyleContext.DEFAULT_STYLE);
        JLabel label = new JLabel(icon);
        label.setAlignmentY(5);
        StyleConstants.setComponent(labelStyle, label);
        return labelStyle;
    }

    private void mostrarEscrevendo() {
        String usuarioEscrevendo = "";
        for (Usuario u : usuariosEscrevendo) {
            if (!u.getNome().equals(usuario.getNome())) {
                usuarioEscrevendo = u.getNome() + " está escrevendo...";
            }
        }
        chatView.jLabelEscrevendo.setText(usuarioEscrevendo);
    }

    private Color gerarCor() {
        int R = (int) (Math.random() * 256);
        int G = (int) (Math.random() * 256);
        int B = (int) (Math.random() * 256);
        return new Color(R, G, B);
    }

    private void carregarFonte() {
        doc = chatView.jTextPaneChat.getStyledDocument();
        minhaFonte = new SimpleAttributeSet();
        StyleConstants.setForeground(minhaFonte, Color.BLUE);
        outraFonte = new SimpleAttributeSet();
        StyleConstants.setForeground(outraFonte, Color.RED);
        alertaFonte = new SimpleAttributeSet();
        StyleConstants.setForeground(alertaFonte, Color.ORANGE);
    }

    private void mostrarUsuariosVisualizandoTela() {
        String texto = "";
        if (usuariosVisualizandoTela.size() > 0) {
            texto += "(";
            for (Usuario u : usuariosVisualizandoTela) {
                texto += u.getNome();
                texto += ", ";
            }
            texto = texto.substring(0, texto.length() - 2);
            texto += ")";
        }
        chatView.jLabelUsuarios.setText(texto);
    }

    private void atualizarTela() {
        mostrarUsuariosVisualizandoTela();
        mostrarEscrevendo();
    }

    private PacoteCliente gerarPacoteCliente(String mensagem) {
        PacoteCliente pacote = new PacoteCliente();
        pacote.setData(new Date());
        pacote.setDeslogar(false);
        pacote.setDigitando(digitando);
        pacote.setMensagem(mensagem);
        pacote.setUsuario(usuario);
        pacote.setVisualizando(focus);
        return pacote;
    }

    private PacoteCliente gerarPacoteClienteDeslogar() {
        PacoteCliente pacote = new PacoteCliente();
        pacote.setData(new Date());
        pacote.setDeslogar(true);
        pacote.setDigitando(false);
        pacote.setMensagem("");
        pacote.setUsuario(usuario);
        pacote.setVisualizando(false);
        return pacote;
    }

    private PacoteCliente gerarPacoteClienteIsDigitando() {
        PacoteCliente pacote = gerarPacoteCliente("");
        pacote.setDigitando(digitando);
        return pacote;
    }

    private PacoteCliente gerarPacoteClienteIsVisualizando(boolean isVisualizando) {
        PacoteCliente pacote = gerarPacoteCliente("");
        pacote.setVisualizando(isVisualizando);
        return pacote;
    }

    private PacoteCliente gerarPacoteClienteIsVisualizando() {
        return gerarPacoteClienteIsVisualizando(focus);
    }

    public static void main(String[] args) {
        new ClienteMain();
    }
}
