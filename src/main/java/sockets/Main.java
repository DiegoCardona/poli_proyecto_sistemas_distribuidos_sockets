package sockets;


public class Main {
    /**
     *
     * @param args 0 = server|client, 1 = DB Name, 2 = DB user name, 3 = DB user password.
     */
    public static void main(String[] args) {
        if ("server".equals(args[0])) {
            final Server server = new Server(args);
            server.listen();
        } else {
            final Client client = new Client();
            client.sending();
        }
    }
}
