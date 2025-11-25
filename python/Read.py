file_path = "C:\\repos\\heb-bible\\bible-teamim-27.txt"

try:
    with open(file_path, 'r', encoding = 'utf-8') as file:
        book = 27
        prk = -1
        psk = 0
        for line in file:
            processed_line = line.strip()
            if processed_line.startswith("("):
                psk = psk + 1
            elif not (processed_line.startswith("(")):
                try:
                    if (processed_line.strip().index(" ") == 0):
                        book = book + 1
                        prk = 1
                        psk = 0
                        continue
                except ValueError as e:
                    prk = prk + 1
                    psk = 0
                    continue
            processed_line = processed_line.replace("(פ)", "")
            processed_line = processed_line.replace("(ס)", "")
            processed_line = processed_line.replace("־", " ")
            processed_line = processed_line.replace("׃", "")
            processed_line = processed_line.replace(" ׀ ", " ")
            processed_line = processed_line[processed_line.index(" "):len(processed_line)]
            print(str(book) + ":" + str(prk) + ":" + str(psk) + "," + processed_line.strip())
except FileNotFoundError:
    print(f"Error: The file '{file_path}' was not found.")
except Exception as e:
    print(f"An unexpected error occurred: {prk} {e} ")
