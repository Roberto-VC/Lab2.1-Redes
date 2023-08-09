import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Hamming {

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

        return Integer.parseInt(Integer.toString(res), 2);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int port = 8080;
        System.out.println("¿Que desea hacer?");
        System.out.println("1. Mandar Información.");
        System.out.println("2. Recibir Información");
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

                    int correction = detectError(receivedData,
                            (int) Math.round(Math.log(receivedData.length()) / Math.log(2)));
                    if (correction == 0) {
                        System.out.println("No error in the received message!");
                    } else {
                        System.out.println("Error detected. Bit at position " + correction + " is incorrect.");
                    }

                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
