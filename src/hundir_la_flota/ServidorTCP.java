package hundir_la_flota;

import java.io.*;
import java.net.*;

public class ServidorTCP {
    private Socket socketCliente;
    private ServerSocket socketServidor;
    private BufferedReader entrada;
    private PrintWriter salida;
    private char[][] tablero;

    public ServidorTCP(int puerto) {
        this.socketCliente = null;
        this.socketServidor = null;
        this.entrada = null;
        this.salida = null;
        this.tablero = new char[10][10];
        inicializarTablero();

        try {
            socketServidor = new ServerSocket(puerto);
            System.out.println("Esperando conexión...");
            socketCliente = socketServidor.accept();
            System.out.println("Conexión aceptada: " + socketCliente);
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
        } catch (IOException e) {
            System.out.println("No puede escuchar en el puerto: " + puerto);
            System.exit(-1);
        }
    }

    private void inicializarTablero() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tablero[i][j] = ' ';
            }
        }
        // Coloca los barcos en posiciones fijas para demostrar el concepto
        tablero[0][0] = 'B';
        tablero[1][0] = 'B';
        tablero[2][0] = 'B';
        tablero[3][0] = 'B';
        tablero[4][0] = 'B';
        tablero[5][0] = 'B';
        tablero[6][0] = 'B';
        tablero[7][0] = 'B';
        tablero[8][0] = 'B';
        tablero[9][0] = 'B';
    }

    public void mostrarTablero() {
        System.out.println("Tablero actual:");
        System.out.println("  A B C D E F G H I J");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < 10; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void enviarTablero() {
        // Enviar la representación del tablero al cliente
        StringBuilder tableroStr = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tableroStr.append(tablero[i][j]).append(" ");
            }
            tableroStr.append("\n");
        }
        salida.println(tableroStr.toString());
    }

    public String verificarCoordenada(String coordenada) {
        int fila = coordenada.charAt(0) - 'A';
        int columna = Integer.parseInt(coordenada.substring(1));

        if (tablero[fila][columna] == 'B') {
            tablero[fila][columna] = 'X';  // Marcar como acertado
            return "¡Tocado!";
        } else {
            return "¡Agua!";
        }
    }

    public void closeServidorTCP() {
        try {
            salida.close();
            entrada.close();
            socketCliente.close();
            socketServidor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("-> Servidor Terminado");
    }

    public void enviarMsg(String respuesta) {
        salida.println(respuesta);
    }

    public String recibirMsg() {
        String linea = "";
        try {
            linea = entrada.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return linea;
    }

    public static void main(String[] args) throws IOException {
        ServidorTCP servidor = new ServidorTCP(5555);
        jugarComoServidor(servidor);
        servidor.closeServidorTCP();
    }

    private static void jugarComoServidor(ServidorTCP servidor) {
        System.out.println("¡El cliente se ha conectado! Esperando que vea el tablero...");

        servidor.mostrarTablero(); // Mostrar el tablero al cliente
        servidor.enviarTablero(); // Enviar el tablero al cliente
        servidor.enviarMsg("El juego ha comenzado. ¡Buena suerte!");

        System.out.println("Tablero enviado al cliente. Esperando coordenadas...");

        do {
            String coordenada = servidor.recibirMsg(); // Recibir coordenada del cliente
            System.out.println("Coordenada recibida del cliente: " + coordenada);

            String respuesta = servidor.verificarCoordenada(coordenada);
            servidor.enviarMsg(respuesta); // Enviar respuesta al cliente
            System.out.println("Respuesta enviada al cliente: " + respuesta);

            if (respuesta.equals("¡Tocado!")) {
                System.out.println("El cliente ha acertado. Esperando siguiente movimiento...");
            } else if (respuesta.equals("¡Agua!")) {
                System.out.println("El cliente ha fallado. Esperando siguiente movimiento...");
            }

        } while (!servidor.verificarCoordenada("").equals("¡Ganaste!")); // Puedes ajustar la condición de salida según tus necesidades

        System.out.println("¡El cliente ha ganado!"); 
    }
}
