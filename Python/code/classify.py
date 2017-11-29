# using python's nltk toolkit and numpy
import nltk
from nltk.stem.lancaster import LancasterStemmer
import os
import json
import datetime
stemmer = LancasterStemmer()
import numpy as np
import sys

inputString = str(sys.argv[1])

weights_file = "C:\\Users\\Jay\\git\\295A-Server\\Python\\code\\weights.json"

with open(weights_file) as data_file: 
    weights = json.load(data_file) 
    weights_0 = np.asarray(weights['weights0']) 
    weights_1 = np.asarray(weights['weights1'])
    words = np.asarray(weights['words'])
    classes = np.asarray(weights['classes'])

def sigmoid(x):
    output = 1/(1+np.exp(-x))
    return output

def sigmoid_output_to_derivative(output):
    return output*(1-output)


def feature(sentence, words, show_details=False):
    sentence_words = nltk.word_tokenize(sentence)
    sentence_words = [stemmer.stem(word.lower()) for word in sentence_words]
    bag = [0]*len(words)  
    for s in sentence_words:
        for i,w in enumerate(words):
            if w == s: 
                bag[i] = 1
                if show_details:
                    print ("found in bag: %s" % w)

    return(np.array(bag))

def predictusing_ann(sentence, show_details=False):

    x = feature(sentence.lower(), words, show_details)
    if show_details:
        print ("sentence:", sentence, "\n feature set:", x)
    l0 = x
    l1 = sigmoid(np.dot(l0, weights_0))
    l2 = sigmoid(np.dot(l1, weights_1))
    return l2

#copy our persisted weight values into the system


# main classification initiator
# will be used by our validation an testing functions

def classify(sentence, show_details=False):
    results = predictusing_ann(sentence, show_details)
    ERROR_THRESHOLD = 0.2
    
    results = [[i,r] for i,r in enumerate(results) if r>ERROR_THRESHOLD ] 
    results.sort(key=lambda x: x[1], reverse=True) 
    return_results =[[classes[r[0]],r[1]] for r in results]
    print (return_results)

classify(inputString)
