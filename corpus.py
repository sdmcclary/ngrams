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

<<<<<<< HEAD
#git = Github("")# blank as git won't let you push files with keys in them
def mining():
    with open('Corpus/names.csv','r',newline='') as names,open(f"Corpus/processed_raw.jsonl", 'w', encoding='utf-8') as jsonl:
=======
git = Github("")
def mining():
    with open('names.csv','r',newline='') as names,open(f"Corpus/processed_raw.jsonl", 'w', encoding='utf-8') as jsonl:
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
        reader = csv.reader(names)
        for repo_name in reader:
            name = repo_name[0]
            try:
                repo = git.get_repo(name)
<<<<<<< HEAD
                files = get_files(repo)
                for file in files:
                    decoded = file.decoded_content.decode('utf-8')
                    data = {"name": name, "file_path": file.path, "content": decoded}
                    jsonl.write(json.dumps(data) + '\n')
                    print(f"Added {name} ({file.path}) to file")
            except GithubException as e:
                print(f"Failed to process {name}: {e}")

def get_files(repo):
    files =[]
    try:
        tree = repo.get_git_tree(sha=(repo.default_branch), recursive=True)
        for item in tree.tree:
            if item.type == 'blob' and item.path.endswith('.java') and 'test' not in item.path.lower():
                file_content = repo.get_contents(item.path)
                files.append(file_content)
                print(f"found file: {item.path}")
    except GithubException as e:
        print(f"Error accessing path: {e}")
    return files

=======
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
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6



def tokenize(input_file, output_file):
<<<<<<< HEAD
    with open(input_file, 'r',encoding='utf-8') as p_in, open(output_file, 'w',encoding='utf-8') as p_out:
        for obj in p_in:
            json_dict = json.loads(obj)
            content = json_dict['content']
            try:
                tokens = list(javalang.tokenizer.tokenize(content))
                tokenList = []
                for token in tokens:
                    tokenList.append({'type':token.__class__.__name__,'val': token.value})
                p_out.write(json.dumps(tokenList) + '\n')
            except javalang.tokenizer.LexerError as e:
                print(f"Problem with tokenizer: {e}")


def remove_junk(input_file, output_file):
    with open(input_file, 'r',encoding='utf-8') as p_in, open(output_file, 'w',encoding='utf-8') as p_out:
=======
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
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
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

<<<<<<< HEAD
def tokencount(input_file):
    with open(input_file, 'r') as p_in:
        countOT = 0 #count of thousand plus token files
        countFH = 0 #count of five hundred plu token files
        countOH = 0 #count of hundred plus token files
        count = 0   #count of files(to ensure counting is properly done)
        for obj in p_in:
            tokens = json.loads(obj)
            if len(tokens) >= 1000:
                countOT += 1
            if len(tokens) >= 500:
                countFH += 1
            if len(tokens) >= 100:
                countOH += 1
            if len(tokens) >= 1:
                count += 1
        print(f"files wiht 1000+: {countOT}\nfiles with 500+: {countFH}\nfiles with 100+: {countOH}\n files: {count}")



def just_tokens(input_file, output_file,test_file):
    with open(input_file, 'r',encoding='utf-8') as p_in, open(output_file, 'w',encoding='utf-8') as p_out_c,open(test_file,'w',encoding='utf-8') as p_out_t:
        x = 0 #total count
        y = 0 #temp for splitting files in cycles rather than chunks
        for obj in p_in:
            tokens = json.loads(obj)
            if len(tokens) >= 1000:
                x += 1
                y += 1
                if x <= 1500:
                    
                    if y < 2 :
                        save_list = []
                        for token in tokens:
                            tokenT = token['type']
                            tokenV = token['val']
                            if tokenT == 'DecimalFloatingPoint' or tokenT == 'DecimalInteger':
                                tokenV = '<num>'
                            elif tokenT == 'String':
                                tokenV = '<str>'
                            elif tokenT == 'Identifier':
                                tokenV = '<identifier>'
                            save_list.append(tokenV)
                        p_out_c.write(json.dumps(save_list)+'\n')
                    else:
                        save_list = []
                        for token in tokens:
                            val = token['val']
                            save_list.append(val)
                        y = 0
                        p_out_t.write(json.dumps(save_list)+'\n')

#mining()
#Did three rounds of git pulling, got 13688 files

#tokenize('Corpus/javafiles3.jsonl','Corpus/tokenized.jsonl')
#lost one file to error, 13687 remaining

#remove_junk('Corpus/tokenized.jsonl','Corpus/ready.jsonl')
#no loss, just removed packages and imports

#tokencount("Corpus/ready.jsonl")
#Created this to determine how small i wanted to limit my corpus files too
#26 files were now empty, meaning they had contained comments and imports
#files wiht 1000+: 1753
#files with 500+: 3553
#files with 100+: 9082
#files: 13661

#just_tokens('Corpus/ready.jsonl','Evaluating/corpus.jsonl','Evaluating/testfiles.jsonl')
#Usng 100 as minimum tokens
#8000 files in corpus, 1000 for testing
#taken in cycles of 9 rather than all at once so files from all repos are in both corpus and test

#changed to 1000 as i didnt need as many classes for corpus
=======

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
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
