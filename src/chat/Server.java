package chat;

import java.io.*;
import java.net.*;
import java.util.*;
import modelo.PacoteCliente;
import modelo.PacoteServidor;
import modelo.Usuario;

public class Server {

    private static int uniqueId;
    private long contadorPacotesEnviados;
    private long contadorPacotesRecebidos;
    private ArrayList<ClientThread> clientes;
    private List<Usuario> usuariosDigitando;
    private List<Usuario> usuariosVisualizando;
    private int porta;
    private boolean online;
    private List<PacoteServidor> mensagens;
    private ServidorMain tela;

    public Server(int porta) {
        this.porta = porta;
        clientes = new ArrayList<>();
        usuariosDigitando = new ArrayList<>();
        usuariosVisualizando = new ArrayList<>();
        mensagens = new ArrayList<>();
        contadorPacotesEnviados = 0;
        contadorPacotesRecebidos = 0;
        tela = null;
    }

    public void start() throws IOException {
        online = true;
        ServerSocket serverSocket = new ServerSocket(porta);

        // loop infinito esperando nova conexão
        while (online) {
            Socket socket = serverSocket.accept();
            if (!online) {
                break;
            }
            ClientThread t = new ClientThread(socket);
            clientes.add(t);
            t.start();
        }
        serverSocket.close();
        for (int i = 0; i < clientes.size(); ++i) {
            ClientThread tc = clientes.get(i);
            try {
                tc.sInput.close();
                tc.sOutput.close();
                tc.socket.close();
            } catch (IOException ioE) {
                // not much I can do
            }
        }
    }

    protected void stop() {
        online = false;
        // connect to myself as Client to exit statement 
        // Socket socket = serverSocket.accept();
        try {
            new Socket("localhost", porta);
        } catch (Exception e) {
            // nothing I can really do
        }
    }

    private void display(String msg) {
        System.out.println(msg);
    }

    /*
     *  enviando Pacote para clientes conectados
     */
    private synchronized void broadcast(PacoteServidor pacoteServidor) {
        if(!pacoteServidor.getMensagem().equals("")){
            mensagens.add(pacoteServidor);
        }
        
        //loop de tras pra frente para caso precise remover um usuario desconectado;
        for (int i = clientes.size(); --i >= 0;) {
            ClientThread ct = clientes.get(i);

            //tenta enviar pacote para o usuario, removendo da lista se falhar.
            if (!ct.gerarPacote(pacoteServidor)) {
                clientes.remove(i);
                display("Cliente desconectado " + ct.usuario.getNome() + " removido da lista.");
            } else {
                pacoteEnviadoComSucesso();
            }
        }
    }

    synchronized void deslogar(int id) {
        // scan the array list until we found the Id
        for (int i = 0; i < clientes.size(); ++i) {
            ClientThread ct = clientes.get(i);
            // found it
            if (ct.id == id) {
                clientes.remove(i);
                return;
            }
        }
    }
    
    private void desconectarUsuario(Usuario usuario){
        removerUsuarioDigitando(usuario);
        removerUsuarioVisualizando(usuario);
    }

    /**
     * Uma instancia dessa thread para cada usuario conectado
     */
    class ClientThread extends Thread {

        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        Usuario usuario;
        PacoteCliente ultimoPacoteRecebidoCliente;

        ClientThread(Socket socket) {
            id = ++uniqueId;
            this.socket = socket;
            display("Thread trying to create Object Input/Output Streams");
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                usuario = (Usuario) sInput.readObject();
                display(usuario.getNome() + " conectado.");
                broadcast(gerarPacoteServidorRemetente(usuario.getNome() + " entrou!"));
                
                atualizarTela();
                
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            } // have to catch ClassNotFoundException
            // but I read a String, I am sure it will work
            catch (ClassNotFoundException e) {
            }
        }

        public void run() {
            boolean keepGoing = true;
            while (keepGoing) {
                try {
                    ultimoPacoteRecebidoCliente = (PacoteCliente) sInput.readObject();           
                    System.out.println(ultimoPacoteRecebidoCliente);
                } catch (IOException e) {
                    display(usuario.getNome() + " Exception reading Streams: " + e);
                    broadcast(gerarPacoteServidorRemetente(usuario.getNome() + " saiu!"));
                    break;
                } catch (ClassNotFoundException e2) {
                    break;
                }

                if (ultimoPacoteRecebidoCliente.isDeslogar()) {
                    //atualizar listas quando usuario deslogar
                    keepGoing = false;
                    display(usuario.getNome() + " saiu!");
                    broadcast(gerarPacoteServidorRemetente(usuario.getNome() + " saiu!"));
                } else {
                    pacoteRecebidoComSucesso();
                    atualizarTela();
                    lerPacoteRecebidoCliente(ultimoPacoteRecebidoCliente);
                    broadcast(gerarPacoteServidor(ultimoPacoteRecebidoCliente));
                }
            }
            deslogar(id);
            atualizarTela();
            close();
        }

        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            };
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }

        private boolean gerarPacote(PacoteServidor pacoteServidor) {
            if (!socket.isConnected()) {
                close();
                return false;
            }
            try {
                sOutput.writeObject(pacoteServidor);
            } catch (IOException e) {
                display("Error ao enviar mensagem para " + usuario.getNome());
                display(e.toString());
            }
            return true;
        }
    }

    private void pacoteRecebidoComSucesso() {
        contadorPacotesRecebidos++;
    }

    private void pacoteEnviadoComSucesso() {
        contadorPacotesEnviados++;
    }

    private void lerPacoteRecebidoCliente(PacoteCliente pacoteCliente) {
        Usuario usuario = pacoteCliente.getUsuario();

        if (pacoteCliente.isDigitando()) {
            adicionarUsuarioDigitando(usuario);
        } else {
            removerUsuarioDigitando(usuario);
        }

        if (pacoteCliente.isVisualizando()) {
            adicionarUsuarioVisualizando(usuario);
        } else {
            removerUsuarioVisualizando(usuario);
        }
    }

    private void adicionarUsuarioDigitando(Usuario usuario) {
        boolean contemUsuario = false;
        for (Usuario u : usuariosDigitando) {
            if (u.getNome().equals(usuario.getNome())) {
                contemUsuario = true;
                break;
            }
        }

        if (!contemUsuario) {
            usuariosDigitando.add(usuario);
        }
    }

    private void removerUsuarioDigitando(Usuario usuario) {
        int posicao = -1;
        for (int i = 0; i < usuariosDigitando.size(); i++) {
            Usuario u = usuariosDigitando.get(i);
            if (u.getNome().equals(usuario.getNome())) {
                posicao = i;
                break;
            }
        }
        if (posicao != -1) {
            usuariosDigitando.remove(posicao);
        }
    }

    private void adicionarUsuarioVisualizando(Usuario usuario) {
        boolean contemUsuario = false;
        for (Usuario u : usuariosVisualizando) {
            if (u.getNome().equals(usuario.getNome())) {
                contemUsuario = true;
                break;
            }
        }

        if (!contemUsuario) {
            usuariosVisualizando.add(usuario);
        }
    }

    private void removerUsuarioVisualizando(Usuario usuario) {
        int posicao = -1;
        for (int i = 0; i < usuariosVisualizando.size(); i++) {
            Usuario u = usuariosVisualizando.get(i);
            if (u.getNome().equals(usuario.getNome())) {
                posicao = i;
                break;
            }
        }
        if (posicao != -1) {
            usuariosVisualizando.remove(posicao);
        }
    }

    private PacoteServidor gerarPacoteServidor(PacoteCliente pacoteCliente) {
        PacoteServidor pacoteServidor = gerarPacoteServidorRemetente("");
        pacoteServidor.setMensagem(pacoteCliente.getMensagem());
        pacoteServidor.setUsuario(pacoteCliente.getUsuario());
        return pacoteServidor;
    }

    private PacoteServidor gerarPacoteServidorRemetente(String mensagem) {
        Usuario servidor = new Usuario();
        servidor.setNome(PacoteServidor.SERVIDOR_REMETENTE);
        PacoteServidor pacoteServidor = new PacoteServidor();
        pacoteServidor.setMensagem(mensagem);
        pacoteServidor.setUsuario(servidor);
        pacoteServidor.setData(new Date());
        pacoteServidor.setUsuariosDigitando(cloneList(usuariosDigitando));
        pacoteServidor.setUsuariosOnline(getUsuariosOnline());
        pacoteServidor.setUsuariosVisualizando(cloneList(usuariosVisualizando));
        return pacoteServidor;
    }
    
    private void calcularPalavras(){
        
    }
    
    private void calcularCaracteres(){
        
    }

    private List<Usuario> getUsuariosOnline() {
        List<Usuario> usuarios = new ArrayList<>();
        for (int i = 0; i < clientes.size(); ++i) {
            ClientThread cliente = clientes.get(i);
            usuarios.add(cliente.usuario);
        }
        return usuarios;
    }

    public String getEndereco() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
    
    private void atualizarTela(){
        if(tela != null){
            tela.atualizarTela();
        }
    }

    public <T extends Object> List<T> cloneList(List<T> list) {
        return ((List<T>) ((ArrayList<T>) list).clone());
    }

    public ServidorMain getTela() {
        return tela;
    }

    public void setTela(ServidorMain tela) {
        this.tela = tela;
    }
    
    public long getQuantidadeUsuariosOnline(){
        return getUsuariosOnline().size();
    }

    public long getContadorPacotesEnviados() {
        return contadorPacotesEnviados;
    }

    public long getContadorPacotesRecebidos() {
        return contadorPacotesRecebidos;
    }   

    public List<PacoteServidor> getMensagens() {
        return mensagens;
    }   
    
    public static void main(String[] args) {
        int portNumber = 5556;
        Server server = new Server(portNumber);
        try {
            server.start();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
