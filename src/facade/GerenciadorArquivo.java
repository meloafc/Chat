package facade;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import json.MensagemJSON;
import modelo.PacoteServidor;

public class GerenciadorArquivo {

    public static void escreverArquivo(List<PacoteServidor> mensagens) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String data = sdf.format(new Date());
        File arquivo = new File("C:\\Users\\Hardware\\Desktop\\" + data + ".txt");

        if (!arquivo.exists()) {
            arquivo.createNewFile();
        }

        sdf = new SimpleDateFormat("HH:mm:ss");

        FileWriter fw = new FileWriter(arquivo, true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (PacoteServidor pacote : mensagens) {
            if (!pacote.getMensagem().equals("")) {
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

    public static void gerarJson(List<PacoteServidor> mensagens) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String data = sdf.format(new Date());

        Gson gson = new Gson();

        List<MensagemJSON> listaJSON = new ArrayList<>();
        for (PacoteServidor mensagem : mensagens) {
            MensagemJSON mensagemJSON = new MensagemJSON();
            mensagemJSON.setData(mensagem.getData());
            mensagemJSON.setMensagem(mensagem.getMensagem());
            mensagemJSON.setUsuario(mensagem.getUsuario().getNome());
            listaJSON.add(mensagemJSON);
        }

        String json = gson.toJson(listaJSON);

        try {
            FileWriter writer = new FileWriter("C:\\Users\\Hardware\\Desktop\\" + data + ".json");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void escreverArquivo(String mensagem) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String data = sdf.format(new Date());
        File arquivo = new File("C:\\Users\\Hardware\\Desktop\\" + data + ".txt");

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
        }
    }
}
