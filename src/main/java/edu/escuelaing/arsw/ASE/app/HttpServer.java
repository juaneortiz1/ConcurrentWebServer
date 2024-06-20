package edu.escuelaing.arsw.ASE.app;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpServer {
private static String path = "src/main/resources/index.html";
    private static String getFile()throws IOException{

        String content = new String(Files.readAllBytes(Paths.get(path)));
        return content;
    }
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        for (int i = 0; 1 < 3 ; i++){

        }
    }





}
class Execution extends Thread{
    ServerSocket serverSocket;
    Socket clientSocket;

    public Socket getClientSocket() {
        return clientSocket;
    }
    public Execution(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
    public void run(){

        clientSocket = null;
        System.out.println("Listo para recibir ...");
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        PrintWriter out;

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        String inputLine, outputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + getFile()
                + inputLine;

        out.println(outputLine);

        out.close();

        in.close();

        clientSocket.close();

        serverSocket.close();
    }
}