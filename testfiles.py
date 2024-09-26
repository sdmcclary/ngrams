#Creating the test files
#Sean McClary

import json
import javalang



def convertTestFiles(input_file):
    with open(input_file, 'r', encoding='utf-8') as p_in:
        x = 0
        for obj in p_in:
            x += 1
            with open(f"Evaluating/test/test{x}.java",'w',encoding='utf-8') as p_out:
                tokens = json.loads(obj)
                for token in tokens:   
                    p_out.write(f"{token} ")
            



#convertTestFiles('Evaluating/testfiles.jsonl')
#converts the files set apart for testing back into java files
