import socket
import random

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

if __name__ == "__main__":
    menu = int(input("¿Que desea hacer?\n 1. Mandar Información.\n2. Recibir Información"))
    if menu == 1:
        input_data = input("Ingrese el mensaje a enviar: ")
        input_data = '''Persona 5[a] is a 2016 role-playing video game developed by P-Studio and published by Atlus. It is the sixth installment in the Persona series, itself a part of the larger Megami Tensei franchise. It was released for the PlayStation 3 and PlayStation 4 game consoles in Japan in September 2016, and worldwide in April 2017. It was published by Atlus in Japan and North America, and by Deep Silver in PAL territories. An enhanced version featuring new content, Persona 5 Royal,[b] was released for the PlayStation 4 in Japan in October 2019, and worldwide in March 2020. It was published by Atlus in Japan and worldwide by its parent company Sega. Persona 5 Royal was later released for the Nintendo Switch, PlayStation 5, Windows, Xbox One, and Xbox Series X/S game consoles in October 2022.

It takes place in modern-day Tokyo and follows a high school student known by the pseudonym Joker who transfers to a new school after he is falsely accused of assault, and put on probation. Over the course of a school year, he and other students awaken to a special power, becoming a group of secret vigilantes known as the Phantom Thieves of Hearts. They explore the Metaverse, a supernatural realm born from humanity's subconscious desires, to steal malevolent intent from the hearts of adults. As with previous games in the series, the party battles enemies known as Shadows using physical manifestations of their psyche known as Personas. The game incorporates role-playing and dungeon crawling elements alongside social simulation scenarios.

Persona 5 was developed by P-Studio, an internal development division within Atlus led at the time by game director and producer Katsura Hashino. Along with Hashino, returning staff from earlier Persona games included character designer Shigenori Soejima and music composer Shoji Meguro. Preparatory work began during the development of Persona 4, with full development beginning after the release of Catherine in 2011. First announced in 2013, Persona 5 was delayed from its original late 2014 release date due to the game not being fully finished. Its themes revolve around attaining freedom from the limitations of modern society. While the story was strongly inspired by picaresque fiction, the party's Personas were based on outlaws and rebels from literature.

Persona 5 won several awards and has been cited as one of the greatest role-playing video games of all time, with praise given to its visual presentation, gameplay, story, and music. Persona 5 (including Royal) had sold over 7.2 million units by April 2023, making it the best-selling entry in the Megami Tensei franchise.

Several pieces of related media have also been released, including four spin-off games—Persona 5: Dancing in Starlight, Persona 5 Strikers, and the upcoming Persona 5 Tactica and Persona 5: The Phantom X—as well as manga and anime adaptations. The game's cast has also made appearances in other games, such as Joker appearing as a playable character in the 2018 crossover fighting game Super Smash Bros. Ultimate.'''
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
