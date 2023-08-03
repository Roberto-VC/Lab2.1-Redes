import java.util.Scanner;

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
        if (a == 1) {
            System.out.println("Ingrese la información a mandar.");
            String size = sc.nextLine();
            String inputBinaryData = size;

            // Convert binary input string to an array of integers (0s and 1s)
            int[] inputData = binaryStringToIntArray(inputBinaryData);

            // Calculate CRC-32 value for the input data
            int crcValue = crc32Binary(inputData);

            // Convert the CRC-32 value to a binary string representation
            String crcBinaryString = String.format("%32s", Integer.toBinaryString(crcValue)).replace(' ', '0');

            // Append the CRC-32 value to the data to simulate transmission
            String transmittedData = inputBinaryData + crcBinaryString;
            System.out.println(transmittedData);
        } else if (a == 2) {
            // Introduce an error in the transmitted data by flipping a bit
            System.out.println("Ingrese la información a recibir.");
            String transmittedData = sc.nextLine();

            // Extract the received data (excluding the appended CRC value)
            String receivedData = transmittedData.substring(0, transmittedData.length() - 32);

            // Convert the received data to an array of integers (0s and 1s)
            int[] receivedDataArray = binaryStringToIntArray(receivedData);

            // Calculate the CRC-32 value for the received data
            int receivedCrcValue = crc32Binary(receivedDataArray);

            // Compare the calculated CRC-32 value with the transmitted CRC value
            if (receivedCrcValue == Integer
                    .parseInt(transmittedData.substring(transmittedData.length() - 32), 2)) {
                System.out.println("No se detecto error");
            } else {
                System.out.println("Se detecto un error. Data esta corrompida.");
            }
        }
    }
}
