def crc32_binary(data):
    polynomial = 0x04C11DB7
    crc = 0xFFFFFFFF

    for bit in data:
        crc ^= (int(bit) << 31)
        for _ in range(8):
            crc = (crc << 1) ^ polynomial if crc & 0x80000000 else crc << 1

    return crc & 0xFFFFFFFF

def binary_string_to_list(binary_string):
    return [int(bit) for bit in binary_string]

def list_to_binary_string(bit_list):
    return "".join(str(bit) for bit in bit_list)

if __name__ == "__main__":
    # Input binary data as a string of 0s and 1s
    input_data = "110101111"

    # Convert binary input string to a list of integers (0s and 1s)
    data_bits = binary_string_to_list(input_data)

    # Calculate CRC-32 value for the input data
    crc_value = crc32_binary(data_bits)

    # Convert the CRC-32 value to a binary string representation
    crc_binary_string = format(crc_value, '032b')

    # Append the CRC-32 value to the data to simulate transmission
    transmitted_data = input_data + crc_binary_string

    # Introduce an error in the transmitted data by flipping a bit
    transmitted_data_with_error = transmitted_data
    print(transmitted_data_with_error)

    # Extract the received data (excluding the appended CRC value)
    received_data = transmitted_data_with_error[:-32]

    # Convert the received data to a list of integers (0s and 1s)
    received_bits = binary_string_to_list(received_data)

    # Calculate the CRC-32 value for the received data
    received_crc_value = crc32_binary(received_bits)

    # Compare the calculated CRC-32 value with the transmitted CRC value
    if received_crc_value == int(transmitted_data_with_error[-32:], 2):
        print("Error not detected: Data is likely intact.")
    else:
        print("Error detected: Data may have been corrupted during transmission.")
