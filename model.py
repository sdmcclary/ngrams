import json
from collections import defaultdict
import pickle


#Creating dictionary of ngrams
#input: list of token lists, context window
#output: dictionary of ngrams with count
def n_grams(token_lists, n):
    ngram_dict = defaultdict(dict)
    for tokens in token_lists:
        for x in range(len(tokens) - n + 1):
            key_token = tokens[x]
            other_tokens = tuple(tokens[x+1:x+n])
            if other_tokens in ngram_dict[key_token]:
                ngram_dict[key_token][other_tokens] += 1
            else:
                ngram_dict[key_token][other_tokens] = 1
    return ngram_dict


#Creating list of lists from corpus
#input: jsonl file of corpus, number of files to include
#output: list of token lists
def get_corpus(input_file,corpus_size):
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

#Replacing the occurance count with an occurance probability
def get_probs(ngrams):
    probs = defaultdict(dict)
    for key_token, following_tokens in ngrams.items():
        total = sum(following_tokens.values())
        for other_tokens, count in following_tokens.items():
            percentage = round(count / total, 5)
            probs[key_token][other_tokens] = percentage
    return probs




def model(corp_size,cw_lower,cw_upper):
    corp = get_corpus("Corpus/ready_tokensonly.jsonl",corp_size)
    for x in range(cw_lower,cw_upper + 1):
        ngrams = n_grams(corp,x)
        probs = get_probs(ngrams)
        print(f"\n\nCorpus size: {corp_size}\n CW: {x}")
        for kt, ft in probs.items():
            print (f"{kt}:")
            for t,p in ft.items():
                print(f"  {t}:{p}")
    
#corpus size max is 3993
model(500,3,5)