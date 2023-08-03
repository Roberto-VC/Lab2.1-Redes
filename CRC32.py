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
    menu = int(input("¿Que desea hacer?\n 1. Mandar Información.\n2. Recibir Información"))
    if menu == 1:
        input_data = input("Ingrese los digitos a enviar: ")

        data_bits = binary_string_to_list(input_data)

        crc_value = crc32_binary(data_bits)

        crc_binary_string = format(crc_value, '032b')

        transmitted_data = input_data + crc_binary_string
        print(transmitted_data)

    
    elif menu == 2:
        transmitted_data_with_error = input("Ingrese data que recibir: ")

        received_data = transmitted_data_with_error[:-32]

        received_bits = binary_string_to_list(received_data)

        received_crc_value = crc32_binary(received_bits)

        if received_crc_value == int(transmitted_data_with_error[-32:], 2):
            print("Error no encontrado. Todo esta bien..")
        else:
            print("Se detecto un error. Data Corrompida")
