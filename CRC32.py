import socket
import random

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

def apply_error(data):
    for j in data:
        random_number = random.randint(0, 100)
        if random_number <= 1:
            if j == '0':
                j = '1'
            else:
                j = '0'

def send_data(data):
    HOST = 'localhost'
    PORT = 8080

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.connect((HOST, PORT))
        s.sendall(data.encode())

def listen_for_data():
    HOST = 'localhost'
    PORT = 8080

    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()

        print("Listening for incoming connections...")
        conn, addr = s.accept()
        with conn:
            print('Connected by', addr)
            data = conn.recv(1024).decode()
            print('Received data:', data)
            return data

if __name__ == "__main__":
    menu = int(input("¿Que desea hacer?\n 1. Mandar Información.\n2. Recibir Información"))
    if menu == 1:
        input_data = input("Ingrese los digitos a enviar: ")

        data_bits = binary_string_to_list(input_data)

        crc_value = crc32_binary(data_bits)

        crc_binary_string = format(crc_value, '032b')

        transmitted_data = input_data + crc_binary_string

        data_error = apply_error(transmitted_data)

        send_data(transmitted_data)

        print(transmitted_data)

    elif menu == 2:
        received_data_with_error = listen_for_data()

        received_data = received_data_with_error[:-32]

        received_bits = binary_string_to_list(received_data)

        received_crc_value = crc32_binary(received_bits)

        if received_crc_value == int(received_data_with_error[-32:], 2):
            print("No error detected. Data is intact.")
        else:
            print("Error detected. Data is corrupted.")
