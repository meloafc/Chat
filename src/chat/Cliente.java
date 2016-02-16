package chat;

import java.net.*;
import java.io.*;
import modelo.PacoteCliente;
import modelo.PacoteServidor;
import modelo.Usuario;

public abstract class Cliente {

    protected ObjectInputStream sInput;
    protected ObjectOutputStream sOutput;
    protected Socket socket;
    protected String enderecoServidor;
    protected Usuario usuario;
    protected int porta;

    public boolean start() {
        try {
            socket = new Socket(enderecoServidor, porta);
        }
        catch (Exception ec) {
            alerta("Error ao se conectar com o servidor:" + ec);
            return false;
        }

        String msg = "Conexão aceita " + socket.getInetAddress() + ":" + socket.getPort();
        alerta(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            alerta("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        //cria uma thread para ouvir o servidor
        new ListenFromServer().start();
        //primeiro objeto enviado para o servido é o Usuario. Os outros sao Pacotes
        try {
            sOutput.writeObject(usuario);
        } catch (IOException eIO) {
            alerta("Exceção ao fazer login : " + eIO);
            disconnect();
            return false;
        }
        // success we inform the caller that it worked
        return true;
    }

    protected abstract void conexaoFalhou();

    protected abstract void processarPacoteRecebido(PacoteServidor pacoteServidor);
    
    protected abstract void alerta(String mensagem);

    void enviarPacote(PacoteCliente pacoteCliente) {
        try {
            sOutput.writeObject(pacoteCliente);
        } catch (IOException e) {
            alerta("Exceção ao enviar pacote para o servidor : " + e);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect not much to do in the catch clause
     */
    private void disconnect() {
        try {
            if (sInput != null) {
                sInput.close();
            }
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (sOutput != null) {
                sOutput.close();
            }
        } catch (Exception e) {
        } // not much else I can do
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
        } // not much else I can do

        conexaoFalhou();
    }

    /*
     * a class that waits for the message from the server and append them to the JTextArea
     * if we have a GUI or simply System.out.println() it in console mode
     */
    class ListenFromServer extends Thread {

        public void run() {
            while (true) {
                try {
                    PacoteServidor pacoteServidor = (PacoteServidor) sInput.readObject();
                    processarPacoteRecebido(pacoteServidor);
                } catch (IOException e) {
                    alerta("Servidor fechou a conexão: " + e);                  
                    conexaoFalhou();
                    break;
                } // can't happen with a String object but need the catch anyhow
                catch (ClassNotFoundException e2) {
                }
            }
        }
    }
}
