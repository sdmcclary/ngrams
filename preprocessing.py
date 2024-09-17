#Preprocessing part fo n-grams modeling for java code completion
#Sean McClary

''' 
The goal is to make the data more usable by
    1. Removing duplicates
    2. Removing unneccesary results from search
    3. Splitting the data set into the corpus and the test sets
    
'''


import json

#Building a set of names to ensure no duplication occurs
unique_names = set()

#Function to remove duplicates and unnecesary information, leaving only name and content from a single version from each project
def preprocessing(input_file, output_file):
    with open(input_file, 'r', encoding='utf-8') as p_in, open(output_file, 'w', encoding='utf-8') as p_out:
        for obj in p_in:#Grabbing each json element individually from the jsonl file
            json_dict = json.loads(obj)
            name = json_dict['repo']['name']
            if name not in unique_names:#checking to ensure we don't already have this project
                unique_names.add(name)
                p_out.write(json.dumps({'name': name,'content': json_dict['content']}))
        #print(unique_names)
'''My original corpus size was 7GB
after running through this process,
the output file is 90MG'''

preprocessing('Corpus/Original.jsonl','Corpus/processedv1.json')

