package calcolomediaserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class CalcoloMediaServer {
    public static final int SERVER_PORT=54321;
    
    public static void main(String[] args) {
        DatagramSocket socket = null;

        try {
            // Crea un socket UDP
            socket = new DatagramSocket(SERVER_PORT);

            byte[] receiveData = new byte[1024];

            while (true) {
                // Crea un pacchetto Datagram per ricevere i dati
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // Riceve i dati dal client
                socket.receive(receivePacket);

                // Ottiene i dati dal pacchetto ricevuto
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Messaggio ricevuto dal client: " + message);

                // Calcola la media, oppure scrive un messaggio di errore
                double averageValue = 0.0;
                double weightsSum = 0.0;
                String response = null;
                String[] values = message.split(" ");
                if(values.length%2 != 0 || values.length<2)
                    response = "ERRORE: Formato del messaggio non corretto!";
                else {
                    try {
                        for(int i=0; i<values.length; i+=2) {
                            double value = Double.parseDouble(values[i]);
                            int weight = Integer.parseInt(values[i+1]);
                            averageValue += value * weight;
                            weightsSum += weight;
                        }
                    } catch (NumberFormatException e) {
                        response = "ERRORE: Formato del messaggio non corretto!";
                    } 
                }
                
                // Ottiene l'indirizzo IP e la porta del client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                // Converte la risposta in un array di byte
                if(response == null)
                    response = String.format("%.2f", averageValue / weightsSum);
                byte[] sendData = response.getBytes();

                // Crea un pacchetto Datagram e invia la risposta al client
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                socket.send(sendPacket);
            }
        } catch (IOException e) {
            System.out.println("ERRORE: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
