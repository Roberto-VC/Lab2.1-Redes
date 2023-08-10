import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class CRC32 {
    private static final int POLYNOMIAL = 0x04C11DB7;

    public static int crc32Binary(String data) {
        int polynomial = 0x04C11DB7;
        int crc = 0xFFFFFFFF;

        for (char bit : data.toCharArray()) {
            crc ^= (Integer.parseInt(String.valueOf(bit)) << 31);
            for (int i = 0; i < 8; i++) {
                crc = (crc << 1) ^ ((crc & 0x80000000) != 0 ? polynomial : 0);
            }
        }

        return crc & 0xFFFFFFFF;
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

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("¿Que desea hacer?\n1. Mandar información\n2. Recibir información.");
        int a = sc.nextInt();
        int port = 8080;

        if (a == 1) {
            System.out.println("Ingrese la información a mandar.");
            sc.nextLine();
            String inputBinaryData = sc.nextLine();

            int[] data_bits = binaryStringToList(inputBinaryData);

            int crc_value = crc32Binary(listToBinaryString(data_bits));

            String crc_binary_string = String.format("%32s", Integer.toBinaryString(crc_value)).replace(' ', '0');

            String transmittedData = inputBinaryData + crc_binary_string;

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

                        int crcLength = 32;
                        String received_data = x.substring(0,
                                x.length() - crcLength);

                        int received_crc_value = Integer.parseInt(x.substring(x.length() - 32), 2);

                        int[] received_bits = binaryStringToList(received_data);
                        int calculated_crc = crc32Binary(listToBinaryString(received_bits));

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
