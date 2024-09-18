#Preprocessing part fo n-grams modeling for java code completion
#Sean McClary

''' 
The goal is to make the data more usable by
    1. Makign sure each file is unique
    2. removing comments and unnecesary components
    3. tokenizing the data
    4. Splitting the data set into the corpus and the test sets
    
'''

import json
import re
import csv
from github import Github
from github.GithubException import GithubException

#git = Github("")
'''
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

mining()
'''

def remove_junk(input_file, output_file):
    with open(input_file, 'r') as p_in, open(output_file, 'w') as p_out:
        for obj in p_in:
            json_dict = json.loads(obj)
            name = json_dict['name']
            content = json_dict['content']

            content = content.strip()
            content = re.sub(r'\s+', ' ', content)
            content = re.sub(r'//.*', '', content)
            content = re.sub(r'/\*[\s\S]*?\*/', '', content)
            
            data = {'name': name, 'content': content}
            p_out.write(json.dumps(data) + '\n')

remove_junk('Corpus/processed_raw.jsonl','Corpus/processed_v2.jsonl')
