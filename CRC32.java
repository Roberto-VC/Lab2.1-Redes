import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CRC32 {
    private static final int POLYNOMIAL = 0x04C11DB7;

    private static int crc32Binary(int[] data) {
        int crc = 0xFFFFFFFF;

        for (int bit : data) {
            crc ^= (bit << 31);
            for (int i = 0; i < 8; i++) {
                crc = ((crc & 0x80000000) != 0) ? ((crc << 1) ^ POLYNOMIAL) : (crc << 1);
            }
        }

        return crc & 0xFFFFFFFF;
    }

    private static int[] binaryStringToIntArray(String binaryString) {
        int[] data = new int[binaryString.length()];
        for (int i = 0; i < binaryString.length(); i++) {
            data[i] = Integer.parseInt(binaryString.substring(i, i + 1));
        }
        return data;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Que desea hacer?\n1. Mandar información\n2. Recibir información.");
        int a = sc.nextInt();
        int port = 8080;

        if (a == 1) {
            System.out.println("Ingrese la información a mandar.");
            sc.nextLine();
            String inputBinaryData = sc.nextLine();

            int[] inputData = binaryStringToIntArray(inputBinaryData);

            int crcValue = crc32Binary(inputData);

            String crcBinaryString = String.format("%32s", Integer.toBinaryString(crcValue)).replace(' ', '0');

            String transmittedData = inputBinaryData + crcBinaryString;

            try (Socket socket = new Socket("localhost", port)) {
                socket.getOutputStream().write(transmittedData.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(transmittedData);
        } else if (a == 2) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Socket listener started. Waiting for incoming connections...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection established with " + clientSocket.getInetAddress());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String transmittedData = reader.readLine();

                    String receivedData = transmittedData.substring(0, transmittedData.length() - 32);
                    int[] receivedDataArray = binaryStringToIntArray(receivedData);
                    int receivedCrcValue = crc32Binary(receivedDataArray);

                    if (receivedCrcValue == Integer.parseInt(transmittedData.substring(transmittedData.length() - 32),
                            2)) {
                        System.out.println("No se detecto error en los datos recibidos.");
                    } else {
                        System.out.println("Se detecto un error. La data recibida está corrompida.");
                    }

                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
