import math
import random
import socket

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

def calcRedundantBits(m): 
    for x in range(m):
        if(2**x >= m + x + 1):
            return x
 
def posRedundantBits(data, r):
    j = 0
    k = 1
    m = len(data)
    res = ''

    for i in range(1, m + r+1):
        if(i == 2**j):
            res = res + '0'
            j += 1
        else:
            res = res + data[-1 * k]
            k += 1

    return res[::-1]
 
 
def calcParityBits(arr, r):
    n = len(arr)
 

    for i in range(r):
        val = 0
        for j in range(1, n + 1):
            if(j & (2**i) == (2**i)):
                val = val ^ int(arr[-1 * j])

        arr = arr[:n-(2**i)] + str(val) + arr[n-(2**i)+1:]
    return arr

def decodeHamming(encoded_message):
    r = calcRedundantBits(len(encoded_message))
    encoded_message = encoded_message[::-1]  

    error_positions = []
    for i in range(r):
        val = 0
        for j in range(1, len(encoded_message) + 1):
            if j & (2**i) == (2**i):
                val = val ^ int(encoded_message[j - 1])

        if val != 0:
            error_positions.append(2**i)

    if error_positions:
        corrected_message = list(encoded_message)
        for pos in error_positions:
            corrected_message[pos - 1] = str(int(corrected_message[pos - 1]) ^ 1)
        corrected_message = ''.join(corrected_message)
    else:
        corrected_message = encoded_message

    decoded_message = ''
    j = 0
    for i in range(1, len(corrected_message) + 1):
        if i != 2**j:
            decoded_message += corrected_message[i - 1]
        else:
            j += 1

    return decoded_message[::-1]
 
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
        print(data)
        s.sendall(data.encode())

def detectError(arr, nr):
    n = len(arr)
    res = 0
 
    for i in range(nr):
        val = 0
        for j in range(1, n + 1):
            if(j & (2**i) == (2**i)):
                val = val ^ int(arr[-1 * j])
 

        res = res + val*(10**i)
 
    return int(str(res), 2)
 
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

menu = int(input("¿Que desea hacer?\n1. Mandar Información.\n2. Recibir Información\n"))
if menu == 1:
    data = input("Escribir mensaje a enviar: ")

    bin = str_to_binary(data)

    text = ""
    for x in range(len(bin)):

        r = calcRedundantBits(len(bin[x]))
        
        arr = posRedundantBits(bin[x], r)
        
        arr = calcParityBits(arr, r)
        


        arr_error = apply_error(arr, 100)
        text += arr_error
        if bin[x] != bin[-1]:
            text += " "

    print(text)
    send_data(text)
        
    print("Mensaje en Hamming " + text) 
        

elif menu == 2:
    received_data = listen_for_data()
    correction = detectError(received_data, round(math.log2(len(received_data))))
    if correction == 0:
        print("No error in the message!")
    else:
        print("Error at position", len(received_data) - correction + 1, "from the left")
