import json
import pickle
import javalang
import random

#Creating list of lists from corpus
#input: jsonl file of corpus, number of files to include
#output: list of token lists
def getCorpus(input_file,corpus_size):
    list_list = []
    x = 0
    with open(input_file, 'r') as p_in:
        for token_list_obj in p_in:
            if x >= corpus_size:
                break
            token_list = json.loads(token_list_obj)
            list_list.append(token_list)
            x += 1
    return list_list

#Retrieving test file data
#input: file path
#output: sequence of tokens of file
def getTest(test_file):
    with open(test_file,'r', encoding='utf-8') as file:
        test = file.read()
        tokens = list(javalang.tokenizer.tokenize(test))
        sequence = []
        for token in tokens:
            tokenT = token.__class__.__name__
            tokenV = token.value
            if tokenT == 'DecimalFloatingPoint' or tokenT == 'DecimalInteger':
                tokenV = '<num>'
            elif tokenT == 'String':
                tokenV = '<str>'
            elif tokenT == 'Identifier':
                tokenV = '<identifier>'
            sequence.append(tokenV)
    return sequence


#Creating dictionary of ngrams
#input: list of token lists, context window
#output: dictionary of ngrams
def getNgrams(token_lists, n):
    ngrams = {}
    for tokens in token_lists:
        for x in range(len(tokens) - n):
            check_token = tokens[x+n-1]
            prev_tokens = tuple(tokens[x:x+n-1])
            if prev_tokens in ngrams:
                ngrams[prev_tokens].append(check_token)
            else:
                ngrams[prev_tokens] = [check_token]
    return ngrams

#Replacing the occurance count with an occurance probability
def getProbs(ngrams):
    probs = {}
    for prev_tokens, list in ngrams.items():
        if prev_tokens not in probs:
            probs[prev_tokens] = {}
        for token in list:
            if token not in probs[prev_tokens]:
                probs[prev_tokens][token] = round(list.count(token)/len(list),4)
    return probs

def predictNext(sequence, probs):
    check_token = sequence[-1]


def model(corp_size,cw_lower,cw_upper):
    corp = getCorpus("Corpus/ready_tokensonly.jsonl",corp_size)
    for x in range(cw_lower,cw_upper + 1):
        ngrams = getNgrams(corp,x)
        probs = getProbs(ngrams)
    return probs
#corpus size max is 3993
#model(500,3,3)

#for x in range(1,100):
    #print(x,'\n',getTest(f"Evaluating/test/test{x}.java"),'\n')

def test(start,num_tokens):
    rand = random.randrange(1,1000,1)
    test_input = getTest(f"Evaluating/test/test{rand}.java")
    test_seq = test_input[:start]
    print(test_seq)
    for x in range(start,start + num_tokens):
        temp_model = model(1000,start,start)
        prev_tokens = tuple(test_seq[-(start-1):])
        print(x,prev_tokens)
        possibilities = temp_model[prev_tokens]
        guess_key = None
        guess_prob = 0.0000
        for key,prob in possibilities.items():
            if prob > guess_prob:
                guess_key = key
                guess_prob = prob
        print(f"Possibilities:\n{possibilities}")
        print(f"guess: {guess_key} prob: {guess_prob}")
        test_seq.append(guess_key)

            
    print(test_seq)
    print(test_input[:start+num_tokens])
test(5,10)