#Corpus creation
#Sean McClary

''' 
The goal is to make the data more usable by
    1. Making sure each file is unique
    2. removing comments and unnecesary components
    3. removing packages and imports
    4. generalizing variable names (<var>,<method>,<class>)
    5. generalizing strings and numbers (<str>,<num>)  
'''

import json
import csv
from github import Github
from github.GithubException import GithubException
import javalang


#Step 1: downloading data and saving only required portions

git = Github("")
def mining():
    with open('names.csv','r',newline='') as names,open(f"Corpus/processed_raw.jsonl", 'w', encoding='utf-8') as jsonl:
        reader = csv.reader(names)
        for repo_name in reader:
            name = repo_name[0]
            try:
                repo = git.get_repo(name)
                file = get_files(repo,"")
                decoded = file.decoded_content.decode('utf-8')
                data = {"name": name, "content":decoded}
                jsonl.write(json.dumps(data) + '\n')
                print(f"Added {name} to file")
            except GithubException as e:
                print(e)

def get_files(repo, path,):
    try:
        content = repo.get_contents(path)
        for file in content:
            if file.type == 'dir':
                get_files(repo,file.path)
            elif file.type == 'file' and file.path.endswith('.java') and 'test' not in file.path.lower():
                return file
    except GithubException as e:
        print(e)

#mining()



def tokenize(input_file, output_file):
    with open(input_file, 'r') as p_in, open(output_file, 'w') as p_out:
        for obj in p_in:
            json_dict = json.loads(obj)
            content = json_dict['content']
            tokens = list(javalang.tokenizer.tokenize(content))
            tokenList = []
            for token in tokens:
                tokenT = token.__class__.__name__
                tokenV = token.value
                if tokenT == 'DecimalFloatingPoint' or tokenT == 'DecimalInteger':
                    tokenV = '<num>'
                elif tokenT == 'String':
                    tokenV = '<str>'
                elif tokenT == 'Identifier':
                    tokenV = '<identifier>'
                tokenList.append({'type':tokenT,'val': tokenV})
            p_out.write(json.dumps(tokenList) + '\n')
#tokenize('Corpus/processed_raw.jsonl','Corpus/tokenized.jsonl')


def remove_junk(input_file, output_file):
    with open(input_file, 'r') as p_in, open(output_file, 'w') as p_out:
        for tokens in p_in:
            tokenList = json.loads(tokens)
            saveList = []
            skip = False
            for token in tokenList:
                val = token['val']
                if val == "package" or val == "import":
                    skip = True
                if skip:
                    if val == ";":
                        skip = False
                    continue
                saveList.append(token)
            p_out.write(json.dumps(saveList)+'\n')


#remove_junk('Corpus/tokenized.jsonl','Corpus/ready.jsonl')

def just_tokens(input_file, output_file,test_file):
    with open(input_file, 'r') as p_in, open(output_file, 'w') as p_out_c,open(test_file,'w') as p_out_t:
        x = 0
        for tokens in p_in:
            x+=1
            tokenList = json.loads(tokens)
            saveList = []
            for token in tokenList:
                val = token['val']
                saveList.append(val)
            if x <= 4000:
                p_out_c.write(json.dumps(saveList)+'\n')
            else:
                p_out_t.write(json.dumps(saveList)+'\n')

#just_tokens('Corpus/ready.jsonl','Corpus/ready_tokensonly.jsonl','Corpus/test.jsonl')