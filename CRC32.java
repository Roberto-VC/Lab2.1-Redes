import java.util.Random;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CRC32 {
    public static long crc32Binary(String data) {
        long polynomial = 0x04C11DB7L;
        long crc = 0xFFFFFFFFL;

        for (int i = 0; i < data.length(); i++) {
            int bit = Character.getNumericValue(data.charAt(i));
            crc ^= (bit << 31);
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x80000000L) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc = (crc << 1);
                }
            }
        }

        return crc & 0xFFFFFFFFL;
    }

    public static int[] binaryStringToList(String binaryString) {
        int[] bitList = new int[binaryString.length()];
        for (int i = 0; i < binaryString.length(); i++) {
            bitList[i] = Integer.parseInt(String.valueOf(binaryString.charAt(i)));
        }
        return bitList;
    }

    public static String listToBinaryString(int[] bitList) {
        StringBuilder binaryString = new StringBuilder();
        for (int bit : bitList) {
            binaryString.append(bit);
        }
        return binaryString.toString();
    }

    public static String generateRandomString(String data) {
        Random random = new Random();
        StringBuilder randomChars = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A'); // Generate random uppercase letter
            randomChars.append(randomChar);
        }

        String secondPart = randomChars.substring(4);

        return data + " " + secondPart;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Que desea hacer?\n1. Mandar información\n2. Recibir información.");
        int a = sc.nextInt();
        int port = 8080;

        if (a == 1) {
            String input_data = "";
            int sizeText = 10;

            for (int i = 0; i < sizeText; i++) {
                input_data += generateRandomString(input_data);
            }

            System.out.println(input_data);

            long crc_value = crc32Binary(input_data);

            String crc_binary_string = String.format("%32s", Long.toBinaryString(crc_value)).replace(' ', '0');

            String transmittedData = input_data + crc_binary_string;

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
                    String[] arrOfStr = transmittedData.split(" ");
                    FileWriter myWriter = new FileWriter("CRC32.txt");
                    myWriter.write("Errors\n");
                    System.out.println(transmittedData);

                    for (String x : arrOfStr) {
                        System.out.println(x);

                        String received_data = x.substring(0,
                                x.length() - 32);

                        String received_crc_part = x
                                .substring(x.length() - 32);
                        long received_crc_value = Long.parseLong(received_crc_part, 2);

                        long calculated_crc = crc32Binary(received_data);

                        if (calculated_crc == received_crc_value) {
                            System.out.println("No se detecto error en los datos recibidos.");
                            myWriter.write("Correct\n");
                        } else {
                            System.out.println("Se detecto un error. La data recibida está corrompida.");
                            myWriter.write("Error\n");
                        }
                    }
                    myWriter.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
