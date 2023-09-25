package sockets;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Server {
    // Variables de Base de datos
    private final String DB_HOST = "localhost";
    private final String DB_PORT = "3306";
    private String DB_NAME;
    private String DB_USERNAME;
    private String DB_USERPASS;
    private Connection connection = null;

    // Variables de Socket
    private final int PORT = 9001;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private ServerSocket server;
    private Socket socket;

    public Server(String[] args) {
        DB_NAME = args[1];
        DB_USERNAME = args[2];
        DB_USERPASS = args[3];
    }

    public void listen() {
        try {
            this.connectToDatabase();
            this.server = new ServerSocket(this.PORT);
            System.out.println("Servidor en linea.");
            // Mantiene la conexión abierta para futuros requests
            while (true) {
                this.socket = this.server.accept();
                System.out.println("Cliente conectado.");

                this.dataInputStream = new DataInputStream(socket.getInputStream());
                this.dataOutputStream = new DataOutputStream(socket.getOutputStream());

                System.out.println("Esperando input del cliente...");
                String message = null;
                try {
                    message = dataInputStream.readUTF();
                    System.out.println("Data recibida del cliente: " + message);
                } catch (EOFException eofException) {
                    System.out.println("Cliente cerró la conexión. Reestableciendo socket.");
                }

                if (message != null) {
                    System.out.println("Datos enviados desde el cliente");
                    System.out.println(message);

                    // Process the message and send a response
                    String response = this.getAccount(message);
                    System.out.println("Respuesta al cliente: " + response);
                    dataOutputStream.writeUTF(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getAccount(final String phoneNumber) {

        try {
            final ResultSet resultSet = this.queryDatabaseByPhoneNumber(phoneNumber);
            if (resultSet.next()) {
                return String.format(
                        "Teléfono: %s, Tipo de telefono: %s, nombre: %s, Dirección: %s, Ciudad: %s",
                        resultSet.getString("dir_tel"),
                        resultSet.getString("dir_tipo_tel"),
                        resultSet.getString("dir_nombre"),
                        resultSet.getString("dir_direccion"),
                        resultSet.getString("ciud_nombre"));
            } else {
                return "Número de teléfono no existe en la base de datos";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultSet queryDatabaseByPhoneNumber(final String phoneNumber) throws SQLException {
        // Si no hay una conexión establecida entonces se crea.
        if (null == this.connection) {
            this.connectToDatabase();
        }

        final String query = String.format("SELECT * FROM Personas AS p LEFT JOIN Ciudades AS c ON (p.dir_ciud_id = c.ciud_id) WHERE p.dir_tel = '%s';", phoneNumber);
        try (java.sql.Statement statement = connection.createStatement()) {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void connectToDatabase() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                    "jdbc:mariadb://" + this.DB_HOST + ":" + this.DB_PORT + "/" + this.DB_NAME,
                    this.DB_USERNAME,
                    this.DB_USERPASS
            );
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}