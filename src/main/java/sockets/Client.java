package sockets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private String HOST;
    private static final int PORT=9001;

    public Client (final String hostAddress) {
        HOST = hostAddress;
    }

    public void sending() {
        DataInputStream in;
        DataOutputStream out;
        Scanner scanner;
        try {
            scanner= new Scanner(System.in);
            System.out.println("Para salir, en cualquier momento ingrese 'quit' o 'salir'");
            while (true) {
                Socket sc = new Socket(HOST,PORT);
                System.out.print("Ingresa el número de telefono a consultar: ");
                String numero=scanner.next();
                if ("quit".equals(numero) || "salir".equals(numero)) {
                    break;
                }
                System.out.println("numero ingresado:" + numero);
                out = new DataOutputStream(sc.getOutputStream());
                out.writeUTF(numero);
                in = new DataInputStream(sc.getInputStream());
                System.out.println(in.readUTF());
                sc.close();
            }
            scanner.close();
        }catch(Exception e) {
            System.out.print("cliente falló :"+ e.getMessage());
        }

    }

}
