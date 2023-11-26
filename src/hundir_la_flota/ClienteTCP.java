package hundir_la_flota;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClienteTCP {
    private Socket socketCliente = null;
    private BufferedReader entrada = null;
    private PrintWriter salida = null;

    public ClienteTCP(String ip, int puerto) {
        try {
            socketCliente = new Socket(ip, puerto);
            System.out.println("Conexión establecida: " + socketCliente);
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
        } catch (IOException e) {
            System.err.printf("Imposible conectar con ip:%s / puerto:%d", ip, puerto);
            System.exit(-1);
        }
    }

    public void closeClienteTCP() {
        try {
            salida.close();
            entrada.close();
            socketCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-> Cliente Terminado");
    }

    public void enviarMsg(String linea) {
        salida.println(linea);
    }

    public String recibirMsg() {
        String msg = "";
        try {
            msg = entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public static void main(String[] args) throws IOException {
        ClienteTCP canal = new ClienteTCP("localhost", 5555);
        Scanner scanner = new Scanner(System.in);
        System.out.println("¡Bienvenido al juego Hundir la Flota!");
        System.out.println("Esperando a que el servidor inicie el juego...");

        canal.recibirMsg(); // Espera hasta que el servidor inicie el juego

        System.out.println("¡El juego ha comenzado! Ingresa las coordenadas para disparar.");
        System.out.println("Por ejemplo, A3 representa la posición en la columna A, fila 3.");

        String linea;
        do {
            System.out.println("Esperando tu turno...");
            linea = scanner.nextLine();
            canal.enviarMsg(linea);

            String respuesta = canal.recibirMsg();
            System.out.println("Respuesta del servidor: " + respuesta);

            if (respuesta.equals("Ganaste")) {
                System.out.println("¡Felicidades! Has hundido todos los barcos. ¡Ganaste!");
                break;
            } else if (respuesta.equals("Agua") || respuesta.equals("Tocado")) {
                // Muestra un mensaje según la respuesta del servidor
                System.out.println(respuesta);
            }

            System.out.println("Esperando el turno del servidor...");

        } while (!linea.equals("Adiós"));

        System.out.println("Gracias por jugar. ¡Hasta luego!");
        canal.closeClienteTCP();
        scanner.close();
    }
}
