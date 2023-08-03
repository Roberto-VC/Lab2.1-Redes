import math
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
 
menu = int(input("¿Que desea hacer?\n1. Mandar Información.\n2. Recibir Información\n"))
if menu == 1:
    data = input("Escribir mensaje a enviar: ")
    
    r = calcRedundantBits(len(data))
    
    arr = posRedundantBits(data, r)
    
    arr = calcParityBits(arr, r)
    
    print("Mensaje en Hamming " + arr) 
 
 
elif menu == 2:
    arr = input("Que mensaje quiere recibir: ")
    correction = detectError(arr, round(math.log2(len(arr))))
    if(correction==0):
        print("No hay error del mensaje!")
    else:
        print("Error. Posición ",len(arr)-correction+1," de izquieda")