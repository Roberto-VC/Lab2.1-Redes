import math
import random
import socket

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
 
def apply_error(data, error):
    for j in data:
        random_number = random.randint(0, 100)
        if random_number <= error:
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

    data_base = ['11110000', '10101010', '10001000']
    errors = [1, 5, 10, 15, 20, 25]

    for error in errors:
        for data in data_base:
            r = calcRedundantBits(len(data))
            arr = posRedundantBits(data, r)
            arr = calcParityBits(arr, r)
            arr_error = apply_error(arr, error)
            send_data(arr_error)
            print("Mensaje en Hamming " + arr)

elif menu == 2:
    received_data = listen_for_data()
    correction = detectError(received_data, round(math.log2(len(received_data))))
    if correction == 0:
        print("No error in the message!")
    else:
        print("Error at position", len(received_data) - correction + 1, "from the left")