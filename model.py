import json
<<<<<<< HEAD
import javalang
import random

=======
import pickle
import javalang
import random
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6

#Creating list of lists from corpus
#input: jsonl file of corpus, number of files to include
#output: list of token lists
def getCorpus(input_file,corpus_size):
<<<<<<< HEAD
    tokens_list = []
=======
    list_list = []
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
    x = 0
    with open(input_file, 'r',encoding='utf-8') as p_in:
        for obj in p_in:
            if x >= corpus_size:
                break
            tokens = json.loads(obj)
            tokens_list.append(tokens)
            x += 1
    return tokens_list


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

<<<<<<< HEAD
#building n-grams model 
#cw includes token to be checked
def model(corp_size,cw):
    model = [None] * (cw+1)
    corp = getCorpus("Evaluating/corpus.jsonl",corp_size)
    for x in range(2,cw+1):
        ngrams = getNgrams(corp,x)
        probs = getProbs(ngrams)
        model[x-1] = probs
    return model

def predictNext(sequence, probs,cw):
    prev_tokens = tuple(sequence[-cw:])
    if cw == 0:
        sequence.append("failed")
        return sequence
    try:
        possibilities = probs[cw][prev_tokens]
        guess_key = None
        guess_prob = 0.0000
        for key,prob in possibilities.items():
            if prob > guess_prob:
                guess_key = key
                guess_prob = prob
        sequence.append(guess_key)
    except KeyError as e:
        predictNext(sequence,probs,(cw-1))
    return sequence

def test():
    #User configuration
    start = 15 # what token in the file to start on
    num_tokens = 100 #number of tokens to guess
    cw_lower = 5 #minimum context window to test
    cw_upper = 10 #maximum context window to test
    withReplacement = True # replacing incorrect tokens with correct before next guess
    num_tests = 100 # number of tests for each context window
    
    probs = model(500,cw_upper+1) #model
    results = [[] for _ in range(cw_upper + 1)] # store success rates

    for test in range(num_tests):
        #test set
        rand = random.randrange(1,750,1)
        test_input = getTest(f"Evaluating/test/test{rand}.java")
        
        for cw in range(cw_lower,cw_upper+1):
            test_seq = test_input[:start]
            sequence = test_seq
            corr_count = 0
            for x in range(start,start + num_tokens):
                sequence = predictNext(sequence,probs,cw)
                if sequence[-1] == test_input[len(sequence)]:
                    corr_count += 1
                else:
                    if withReplacement:
                        sequence[-1] = test_input[len(sequence)]
            results[cw].append(corr_count / num_tokens)
    
    print("Results:")
    max_ave = 0
    winner = 0
    for p in range(cw_lower,cw_upper+1):
        ave = sum(results[p])/len(results[p])
        print(f"context window: {p} Sucess: {round((ave * 100),2)}%")
        if ave > max_ave:
            max_ave = ave
            winner = p
    print(f" a context window of {winner} worked best with a success of {round(max_ave*100,2)}%")
test()
=======
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
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
