package sockets;


public class Main {
    /**
     * For Server
     * @param args 0 = server, 1 = DB Name, 2 = DB user name, 3 = DB user password.
     *
     * For Client
     * @param args 0 = client, 1 = HOST Address.
     */
    public static void main(String[] args) {
        if ("server".equals(args[0])) {
            final Server server = new Server(args);
            server.listen();
        } else {
            final Client client = new Client(args[1]);
            client.sending();
        }
    }
}
