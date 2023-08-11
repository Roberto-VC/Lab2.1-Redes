import socket
import random
import string

def str_to_binary(string):
    binary_list = []
    for char in string:
        binary_list.append(bin(ord(char))[2:].zfill(8))  
    return binary_list
def BinaryToDecimal(binary):
        
    binary1 = binary
    decimal, i, n = 0, 0, 0
    while(binary != 0):
        dec = binary % 10
        decimal = decimal + dec * pow(2, i)
        binary = binary//10
        i += 1
    return (decimal)   

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

def apply_error(bits, rate):
    byte = ''
    for x in range(len(bits)):
        y = random.randint(0,rate)
        if y == 0:
            if bits[x] == '0':
                byte += '1'
            else:
                byte += '0'
        else: 
            byte += bits[x]
    return byte


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

def generate_random_string(text):
    random_chars = random.choices(string.ascii_uppercase, k=8)
    first_part = ''.join(random_chars[:4])
    result = f"{text} {first_part}"
    return result


if __name__ == "__main__":
    menu = int(input("¿Que desea hacer?\n 1. Mandar Información.\n2. Recibir Información"))
    if menu == 1:
        input_data = input("Ingrese el mensaje a enviar: ")
        input_data = ''
        sizeText = 10
        for i in sizeText:
            input_data += generate_random_string(input_data)
        print(input_data)
        
        bin = str_to_binary(input_data)

        text = ""
        for x in range(len(bin)):

            data_bits = binary_string_to_list(bin[x])

            crc_value = crc32_binary(data_bits)

            crc_binary_string = format(crc_value, '032b')

            transmitted_data = bin[x] + crc_binary_string


            data_error = apply_error(transmitted_data, 100)

            print(data_error)

            text += data_error
            if bin[x] != bin[-1]:
                text += " "

        send_data(text)


    elif menu == 2:
        received_data_with_error = listen_for_data()

        received_data = received_data_with_error[:-32]

        received_bits = binary_string_to_list(received_data)

        received_crc_value = crc32_binary(received_bits)

        if received_crc_value == int(received_data_with_error[-32:], 2):
            print("No error detected. Data is intact.")
        else:
            print("Error detected. Data is corrupted.")
