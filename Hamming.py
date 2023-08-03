def generate_hamming_code(data):
    n = len(data)
    m = 0  # Number of redundant bits required
    while 2 ** m < n + m + 1:
        m += 1

    # Insert 0s in the positions of the redundant bits
    hamming_code = [0] * (n + m)
    j = 0  # Index for the data bits
    for i in range(1, n + m + 1):
        if i == 2 ** m:
            m += 1
        else:
            hamming_code[i - 1] = data[j]
            j += 1

    # Calculate the values of the redundant bits
    for i in range(m):
        pos = 2 ** i - 1
        parity = 0
        for j in range(pos, n + m, 2 * pos + 2):
            parity ^= hamming_code[j]
        hamming_code[pos] = parity

    return hamming_code


def fix_hamming_code(code):
    m = 0  # Number of redundant bits
    while 2 ** m < len(code):
        m += 1

    error_pos = 0
    for i in range(m):
        pos = 2 ** i - 1
        parity = 0
        for j in range(pos, len(code), 2 * pos + 2):
            parity ^= code[j]
        error_pos += parity * pos

    if error_pos != 0:
        # Flip the erroneous bit
        code[error_pos - 1] ^= 1

    # Check if the sum of ones is even and add the parity bit accordingly
    count_ones = sum(code)
    code.append(count_ones % 2)

    return code


def main():
    data = [1, 0, 1, 0]  # Replace this with your own data (binary representation)
    print("Original Data:", data)

    hamming_code = generate_hamming_code(data)
    print("Generated Hamming Code:", hamming_code)

    # Introduce an error to the code
    hamming_code[2] = 0
    print("Hamming Code with Error:", hamming_code)

    # Fix the code
    fixed_code = fix_hamming_code(hamming_code)
    print("Fixed Hamming Code:", fixed_code)


if __name__ == "__main__":
    main()
