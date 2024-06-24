package edu.escuelaing.arsw.ASE.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConcurrentTestWebApp {
    private static String path = "src/main/resources/index.html";
    private static String pathSearch = "src/main/resources/";
    private static String exceededPath = "src/main/resources/exceeded.txt";

    private static final int max_clients = 3;
    // Counter to track the number of connected clients
    private static int clientCount = 0;

    public static void main(String[] args) throws IOException {
        // List for keeping threads
        List<Ejecutable> threads = new ArrayList<>();
        ServerSocket serverSocket = null;

        // Create a server socket 
        try {
            serverSocket = new ServerSocket(45000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 45000.");
            System.exit(1);
        }

        // Start max_clients number of threads
        for (int i = 0; i < max_clients; i++) {
            Ejecutable hilo = new Ejecutable(serverSocket);
            threads.add(hilo);
            threads.get(i).start();
        }

        // Wait for all threads to finish execution
        for (Ejecutable i : threads) {
            try {
                i.join();
            } catch (InterruptedException e) {
                System.err.println("Thread interrupted: " + e.getMessage());
            }
        }
    }

    // Thread class to handle client connections
    private static class Ejecutable extends Thread {
        private ServerSocket serverSocket;
        private Socket clientSocket;

        public Ejecutable(ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // Synchronized block to safely increment client count
                    synchronized (ConcurrentTestWebApp.class) {
                        if (clientCount >= max_clients) {
                            sendExceededResponse(serverSocket.accept());
                            continue;
                        }
                        clientCount++;
                    }

                    // Accept client connection and handle it
                    clientSocket = serverSocket.accept();
                    handleClient(clientSocket);

                } catch (IOException e) {
                    System.err.println("Accept failed: " + e.getMessage());
                }

                    // Synchronized block to safely decrement client count
                    synchronized (ConcurrentTestWebApp.class) {
                        clientCount--;

                }
            }
        }

        /**
         * Sends a response indicating the maximum number of clients has been exceeded.
         *
         * @param clientSocket Socket connected to the client
         * @throws IOException if an I/O error occurs while sending the response
         */
        private void sendExceededResponse(Socket clientSocket) throws IOException {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String response = new String(Files.readAllBytes(Paths.get(exceededPath)));

            out.println("HTTP/1.1 503 Service Unavailable");
            out.println("Content-Type: text/plain");
            out.println();
            out.println(response);

            out.close();
            clientSocket.close();
        }

        /**
         * Handles the client request.
         *
         * @param clientSocket Socket connected to the client
         * @throws IOException if an I/O error occurs while reading or writing
         */
        private void handleClient(Socket clientSocket) throws IOException {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            StringBuilder request = new StringBuilder();

            // Read the HTTP request from the client
            while ((inputLine = in.readLine()) != null) {
                request.append(inputLine).append("\n");
                if (!in.ready()) {
                    break;
                }
            }

            // Extract the requested path from the HTTP request
            String requestLine = request.toString().split(" ")[1];
            System.out.println("Received: " + requestLine);

            String response;
            if (requestLine.startsWith("/search")) {
                String file = pathSearch + requestLine.split("=")[1];

                // Handle image files (png, jpg, jpeg) with base64 encoding
                if (file.endsWith(".png") || file.endsWith(".jpg") || file.endsWith(".jpeg")) {
                    byte[] imageData = getImageContent(file);
                    String base64Image = Base64.getEncoder().encodeToString(imageData);

                    String htmlResponse = "<!DOCTYPE html>\r\n"
                            + "<html>\r\n"
                            + "    <head>\r\n"
                            + "        <title>Image</title>\r\n"
                            + "    </head>\r\n"
                            + "    <body>\r\n"
                            + "         <center><img src=\"data:image/jpeg;base64," + base64Image + "\" alt=\"image\"></center>\r\n"
                            + "    </body>\r\n"
                            + "</html>";

                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println();
                    out.println(htmlResponse);
                } else {
                    response = getFileContent(file);

                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: " + getContentType(file));
                    out.println();
                    out.println(response);
                }
            } else {
                // Serve the default index.html
                response = getFile();
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type: " + getContentType(path));
                out.println();
                out.println(response);
            }

            out.close();
            in.close();
            clientSocket.close();
        }

        /**
         * Determines the content type based on the file extension.
         *
         * @param fileName Name of the file
         * @return Content type as a string
         */
        private String getContentType(String fileName) {
            if (fileName.endsWith(".html")) {
                return "text/html";
            } else if (fileName.endsWith(".js")) {
                return "application/javascript";
            } else if (fileName.endsWith(".css")) {
                return "text/css";
            } else if (fileName.endsWith(".txt")) {
                return "text/plain";
            } else if (fileName.endsWith(".png")) {
                return "image/png";
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                return "image/jpeg";
            } else {
                return "text/plain";
            }
        }

        /**
         * Reads the content of an image file into a byte array.
         *
         * @param file Path to the image file
         * @return Byte array containing image data
         * @throws IOException if an I/O error occurs while reading the file
         */
        private byte[] getImageContent(String file) throws IOException {
            Path filePath = Paths.get(file);
            return Files.readAllBytes(filePath);
        }

        /**
         * Reads the content of a text-based file into a string.
         *
         * @param file Path to the file
         * @return Content of the file as a string
         * @throws IOException if an I/O error occurs while reading the file
         */
        private String getFileContent(String file) throws IOException {
            try {
                return new String(Files.readAllBytes(Paths.get(file)));
            } catch (IOException e) {
                return "File not found";
            }
        }

        /**
         * Reads the content of the default index.html file into a string.
         *
         * @return Content of the index.html file as a string
         * @throws IOException if an I/O error occurs while reading the file
         */
        private String getFile() throws IOException {
            return new String(Files.readAllBytes(Paths.get(path)));
        }
    }
}
