package facade;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.PacoteServidor;

public class GerenciadorArquivo {

    public static void escreverArquivo(List<PacoteServidor> mensagens) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String data = sdf.format(new Date());
        File arquivo = new File("C:\\Users\\Hardware\\Desktop\\"+data+".txt");
        
        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }
        
        sdf = new SimpleDateFormat("HH:mm:ss");

        FileWriter fw = new FileWriter(arquivo, true);
        BufferedWriter bw = new BufferedWriter(fw);
        for(PacoteServidor pacote : mensagens){
            if(!pacote.getMensagem().equals("")){
                String mensagem;
                mensagem = "(";
                mensagem += sdf.format(pacote.getData());
                mensagem += ")";
                mensagem += pacote.getUsuario().getNome();
                mensagem += ": ";
                mensagem += pacote.getMensagem();
                
                bw.write(mensagem);
                bw.newLine();
            }
        }        
        
        bw.close();
        fw.close();
    }
    
    private static void escreverArquivo(String mensagem) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String data = sdf.format(new Date());
        File arquivo = new File("C:\\Users\\Hardware\\Desktop\\"+data+".txt");
        
        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }       

        FileWriter fw = new FileWriter(arquivo, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(mensagem);
        bw.newLine();
        bw.close();
        fw.close();
    }
    
    public static void main(String[] args) {
        try {
            escreverArquivo("teste");
        } catch (IOException ex) {
            Logger.getLogger(GerenciadorArquivo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
