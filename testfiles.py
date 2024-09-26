#Creating the test files
#Sean McClary

import json
import javalang



def convertTestFiles(input_file):
    with open(input_file, 'r', encoding='utf-8') as p_in:
        x = 0
        for obj in p_in:
<<<<<<< HEAD
            x += 1
            with open(f"Evaluating/test/test{x}.java",'w',encoding='utf-8') as p_out:
                tokens = json.loads(obj)
                for token in tokens:   
                    p_out.write(f"{token} ")
=======
            x+= 1
            if x > 4000:
                with open(f"Evaluating/test/test{x-4000}.java",'w',encoding='utf-8') as p_out:
                    json_dict = json.loads(obj)
                    content = json_dict['content']
                    tokens = list(javalang.tokenizer.tokenize(content))
                    tokenList = []
                    skip = False
                    for token in tokens:
                        if token.value == "package" or token.value == "import":
                            skip = True
                        if skip:
                            if token.value == ";":
                                skip = False
                            continue
                        token_value = token.value.encode('utf-8', 'ignore').decode('utf-8')
                        
                        p_out.write(f"{token_value} ")
            else:
                continue
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
            



<<<<<<< HEAD
#convertTestFiles('Evaluating/testfiles.jsonl')
#converts the files set apart for testing back into java files
=======
convertTestFiles('Corpus/processed_raw.jsonl')

>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
