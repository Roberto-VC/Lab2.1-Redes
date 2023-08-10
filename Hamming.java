import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Hamming {
    public static List<String> strToBinary(String string) {
        List<String> binaryList = new ArrayList<>();
        for (char ch : string.toCharArray()) {
            String binaryString = Integer.toBinaryString(ch);
            binaryList.add(String.format("%8s", binaryString).replace(' ', '0'));
        }
        return binaryList;
    }

    public static String binaryToDecimal(String binary) {
        int decimal = 0;
        int length = binary.length();
        for (int i = 0; i < length; i++) {
            int digit = binary.charAt(length - i - 1) - '0';
            decimal += digit * Math.pow(2, i);
        }
        System.out.println(decimal);
        String letter = Character.toString((char) decimal);
        return letter;
    }

    public static int calcRedundantBits(int m) {
        for (int x = 0; x < m; x++) {
            if (Math.pow(2, x) >= m + x + 1) {
                return x;
            }
        }
        return -1;
    }

    public static String posRedundantBits(String data, int r) {
        int j = 0;
        int k = 1;
        int m = data.length();
        StringBuilder res = new StringBuilder();

        for (int i = 1; i <= m + r; i++) {
            if (i == Math.pow(2, j)) {
                res.append('0');
                j++;
            } else {
                res.append(data.charAt(m - k));
                k++;
            }
        }

        return res.reverse().toString();
    }

    public static String calcParityBits(String arr, int r) {
        int n = arr.length();
        for (int i = 0; i < r; i++) {
            int val = 0;
            for (int j = 1; j <= n; j++) {
                if ((j & (1 << i)) == (1 << i)) {
                    val ^= Integer.parseInt(arr.charAt(n - j) + "");
                }
            }
            arr = arr.substring(0, n - (int) Math.pow(2, i)) + val + arr.substring(n - (int) Math.pow(2, i) + 1);
        }
        return arr;
    }

    public static int detectError(String arr, int nr) {
        int n = arr.length();
        int res = 0;

        for (int i = 0; i < nr; i++) {
            int val = 0;
            for (int j = 1; j <= n; j++) {
                if ((j & (1 << i)) == (1 << i)) {
                    val ^= Integer.parseInt(arr.charAt(n - j) + "");
                }
            }
            res += val * Math.pow(10, i);
        }
        System.out.println(res);
        return Integer.parseInt(Integer.toString(res), 2);
    }

    public static String decodeHamming(String encodedMessage) {
        int r = calcRedundantBits(encodedMessage.length());
        StringBuilder encodedMessageBuilder = new StringBuilder(encodedMessage).reverse();

        List<Integer> errorPositions = new ArrayList<>();
        for (int i = 0; i < r; i++) {
            int val = 0;
            for (int j = 1; j <= encodedMessageBuilder.length(); j++) {
                if ((j & (1 << i)) == (1 << i)) {
                    val ^= Integer.parseInt(String.valueOf(encodedMessageBuilder.charAt(j - 1)));
                }
            }

            if (val != 0) {
                errorPositions.add(1 << i);
            }
        }

        if (!errorPositions.isEmpty()) {
            char[] correctedMessage = encodedMessageBuilder.toString().toCharArray();
            for (int pos : errorPositions) {
                correctedMessage[pos - 1] = (char) ('0' + ('1' - correctedMessage[pos - 1]));
            }
            encodedMessageBuilder = new StringBuilder(String.valueOf(correctedMessage));
        }

        StringBuilder decodedMessageBuilder = new StringBuilder();
        int j = 0;
        for (int i = 1; i <= encodedMessageBuilder.length(); i++) {
            if (i != (1 << j)) {
                decodedMessageBuilder.append(encodedMessageBuilder.charAt(i - 1));
            } else {
                j++;
            }
        }

        return decodedMessageBuilder.reverse().toString();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int port = 8080;
        System.out.println("¿Que desea hacer?");
        System.out.println("1. Mandar Información.");
        System.out.println("2. Recibir Información");
        String inputString = "Hello, World!";
        List<String> binaryList = strToBinary(inputString);

        int menu = sc.nextInt();

        if (menu == 1) {
            System.out.print("Escribir mensaje a enviar: ");
            String data = sc.next();

            int r = calcRedundantBits(data.length());

            String arr = posRedundantBits(data, r);

            arr = calcParityBits(arr, r);

            try (Socket socket = new Socket("localhost", port)) {
                socket.getOutputStream().write(arr.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Mensaje en Hamming: " + arr);
        } else if (menu == 2) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Hamming Code listener started. Waiting for incoming connections...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connection established with " + clientSocket.getInetAddress());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String receivedData = reader.readLine();

                    String[] arrOfStr = receivedData.split(" ");
                    FileWriter myWriter = new FileWriter("Hamming.txt");
                    myWriter.write("Errors\n");
                    for (String a : arrOfStr) {

                        int correction = detectError(a,
                                (int) Math.round(Math.log(a.length()) / Math.log(2)));
                        if (correction == 0) {
                            System.out.println("No error in the received message!");
                            String decimalValue = binaryToDecimal(decodeHamming(a));
                            System.out.println("Decimal: " + decimalValue);
                            myWriter.write("Correct\n");

                        } else {
                            System.out.println("Error detected. Bit at position " + correction + " is incorrect.");
                            String decimalValue = binaryToDecimal(decodeHamming(a));
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
