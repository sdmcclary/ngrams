# N-Grams_CC
Using n-gram models to offer code completion for java code
By Sean McClary

Setting up Corpus:
 If you wish to complete the whole process i did, you will need a git key

 If you wish to just complete testing, skip to model.py

within corpus.py, un comment and run the following, changing file names approapriately if you don't want to override whats already there
1) mining()
    after entering the git key, this wil pull repo names from a csv and pull all the java files from them
2) tokenize('Corpus/javafiles3.jsonl','Corpus/tokenized.jsonl')
    this will convert the files to tokens, removing comments as well
3) remove_junk('Corpus/tokenized.jsonl','Corpus/ready.jsonl')
    this will remove the package and import statements
4) tokencount("Corpus/ready.jsonl")
    this is optional, and only fo if yo want to know the sizes of the files
5) just_tokens('Corpus/ready.jsonl','Evaluating/corpus.jsonl','Evaluating/testfiles.jsonl')
    this will divide up he set of files into a corpus and a test set, as well as normalizing the corpus

Then in testfiles.py:
1) un comment and run convertTestFiles('Evaluating/testfiles.jsonl')
    this turns the jsonl file back into java files

To run the modelling:
1) in the test function, edit the user configuration parameters as you desire
2) if you wish to use your own test files, replace the test files path with our own.
3) run the program and the results will be printed
