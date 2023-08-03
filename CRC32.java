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

    private static String intArrayToBinaryString(int[] data) {
        StringBuilder binaryStringBuilder = new StringBuilder();
        for (int bit : data) {
            binaryStringBuilder.append(bit);
        }
        return binaryStringBuilder.toString();
    }

    public static void main(String[] args) {
        // Input binary data as a string of 0s and 1s
        String inputBinaryData = "110101111";

        // Convert binary input string to an array of integers (0s and 1s)
        int[] inputData = binaryStringToIntArray(inputBinaryData);

        // Calculate CRC-32 value for the input data
        int crcValue = crc32Binary(inputData);

        // Convert the CRC-32 value to a binary string representation
        String crcBinaryString = String.format("%32s", Integer.toBinaryString(crcValue)).replace(' ', '0');

        // Append the CRC-32 value to the data to simulate transmission
        String transmittedData = inputBinaryData + crcBinaryString;

        // Introduce an error in the transmitted data by flipping a bit
        String transmittedDataWithError = transmittedData.substring(0, 5)
                + (transmittedData.charAt(5) == '0' ? '1' : '0') + transmittedData.substring(6);

        // Extract the received data (excluding the appended CRC value)
        String receivedData = transmittedDataWithError.substring(0, transmittedDataWithError.length() - 32);

        // Convert the received data to an array of integers (0s and 1s)
        int[] receivedDataArray = binaryStringToIntArray(receivedData);

        // Calculate the CRC-32 value for the received data
        int receivedCrcValue = crc32Binary(receivedDataArray);

        // Compare the calculated CRC-32 value with the transmitted CRC value
        if (receivedCrcValue == Integer
                .parseInt(transmittedDataWithError.substring(transmittedDataWithError.length() - 32), 2)) {
            System.out.println("Error not detected: Data is likely intact.");
        } else {
            System.out.println("Error detected: Data may have been corrupted during transmission.");
        }
    }
}
