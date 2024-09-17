import json

def preprocessing(input_file):
    with open(input_file, 'r') as file_in:
        x = 0
        for obj in file_in:
            if (x <= 10):
                print(obj)
                print('\n')
                x += 1
            else:
                break


preprocessing('Corpus/Original.jsonl')