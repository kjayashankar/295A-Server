# using python's nltk toolkit and numpy
import nltk
from nltk.stem.lancaster import LancasterStemmer
import os
import json
import datetime
stemmer = LancasterStemmer()
import numpy as np
import time

#2 classes of training data
training_data = []
training_data.append({"class":"eat", "sentence":"how about a lunch?"})
training_data.append({"class":"eat", "sentence":"up for a snack?"})
training_data.append({"class":"eat", "sentence":"let's go out for a lunch"})
training_data.append({"class":"eat", "sentence":"let's go for a bite"})
training_data.append({"class":"eat","sentence":"Is it noon already? I want eat something"})
training_data.append({"class":"eat","sentence":"Did you eat lunch today"})
training_data.append({"class":"eat","sentence":"Do you like to eat beef?"})
training_data.append({"class":"eat","sentence":"Do you like fruits?"})
training_data.append({"class":"eat","sentence":"Do you eat lunch at school?"})
training_data.append({"class":"eat","sentence":"Do you like to eat rice?"})
training_data.append({"class":"eat","sentence":"Do you bring lunch to the school?"})
training_data.append({"class":"eat","sentence":"Do you like Thai food?"})
training_data.append({"class":"eat","sentence":"How about Chinese food?"})
training_data.append({"class":"eat","sentence":"Will you say yes for Spanish food?"})
training_data.append({"class":"eat","sentence":"Shall we have some French food?"})
training_data.append({"class":"eat","sentence":"I love Italian food, how about you?"})
training_data.append({"class":"eat","sentence":"How about indian food?"})
training_data.append({"class":"eat","sentence":"Lets have mexican food"})
training_data.append({"class":"eat","sentence":"Are you up for some wings?"})
training_data.append({"class":"eat","sentence":"Would you like to have some food?"})
training_data.append({"class":"eat","sentence":"would you like to go for a dinner today?"})
training_data.append({"class":"eat","sentence":"What's your favorite junk food?"})

training_data.append({"class":"noeat","sentence":"Indian food is very spicy. I don't like spicy food"})
training_data.append({"class":"noeat","sentence":"Most people hate mexican food because it is spicy"})
training_data.append({"class":"noeat","sentence":"I really much preference toward spanish cuisine"})
training_data.append({"class":"noeat","sentence":"It is not possible now"})
training_data.append({"class":"noeat","sentence":"I have some urgent business"})
training_data.append({"class":"noeat","sentence":"I'm in a meeting now."})
training_data.append({"class":"noeat","sentence":"I'm not in a mood to eat"})
training_data.append({"class":"noeat","sentence":"I don't like chinese food"})
training_data.append({"class":"noeat","sentence":"I don't like french cuisine"})
training_data.append({"class":"noeat","sentence":"Italian cuisine is something I really hate"})
training_data.append({"class":"noeat", "sentence":"i got some work to do"})
training_data.append({"class":"noeat", "sentence":"i'm not hungry"})
training_data.append({"class":"noeat", "sentence":"i'm full, someother time"})
training_data.append({"class":"noeat", "sentence":"not now"})
training_data.append({"class":"noeat", "sentence":"lets not eat"})
training_data.append({"class":"noeat", "sentence":"I don't feel like eating"})
training_data.append({"class":"noeat", "sentence":"I'm not hungry anymore"})
training_data.append({"class":"noeat", "sentence":"I don't want to eat at this time"})
training_data.append({"class":"noeat", "sentence":"I don't have appetite"})
training_data.append({"class":"noeat", "sentence":"I am full right"})
training_data.append({"class":"noeat", "sentence":"Sorry, I'm on a fast"})

# cross validation data set
validation_data = []

validation_data.append({"class":"noeat","sentence":"I dont like to eat right now"})
validation_data.append({"class":"noeat","sentence":"I'm not in the mood to eat"})
validation_data.append({"class":"noeat","sentence":"I have no intention of eating"})
validation_data.append({"class":"noeat","sentence":"I just had my stomach full, so can't eat anything"})
validation_data.append({"class":"noeat","sentence":"I have other tasks to do, sorry"})

validation_data.append({"class":"eat","sentence":"I heard a new lunch place, what say?"})
validation_data.append({"class":"eat","sentence":"I would eat anything you feed"})
validation_data.append({"class":"eat","sentence":"I'm super hungry right now"})
validation_data.append({"class":"eat","sentence":"I would like to eat burrito"})
validation_data.append({"class":"eat","sentence":"How about some snacks with coffee"})

print ("%s sentences of training data" % len(training_data))

validation_data = []

validation_data.append({"class":"noeat","sentence":"I dont like to eat right now"})
validation_data.append({"class":"noeat","sentence":"I'm not in the mood to eat"})
validation_data.append({"class":"noeat","sentence":"I have no intention of eating"})
validation_data.append({"class":"noeat","sentence":"I just had my stomach full, so can't eat anything"})
validation_data.append({"class":"noeat","sentence":"I have other tasks to do, sorry"})

validation_data.append({"class":"eat","sentence":"I heard a new lunch place, what say?"})
validation_data.append({"class":"eat","sentence":"I would eat anything you feed"})
validation_data.append({"class":"eat","sentence":"I'm super hungry right now"})
validation_data.append({"class":"eat","sentence":"I would like to eat burrito"})
validation_data.append({"class":"eat","sentence":"How about some snacks with coffee"})

words = []
classes = []
documents = []
ignore_words = ['?']
# loop through each sentence in our training data and tokenize them with space as delimeter
# add all words, classes 
# add all the documents to our existing corpus
# stem all the words, convert them to lower case and remove duplicates
for pattern in training_data:
    w = nltk.word_tokenize(pattern['sentence'])
    words.extend(w)
    documents.append((w, pattern['class']))
    if pattern['class'] not in classes:
        classes.append(pattern['class'])

words = [stemmer.stem(w.lower()) for w in words if w not in ignore_words]
words = list(set(words))

classes = list(set(classes))

print (len(documents), "documents")
print (len(classes), "classes", classes)
print (len(words), "unique stemmed words", words)

# this module is about creating our training data from sentences above
# iterate over each data point and apply stemming and bag of words
# the output is based on the classification value, 1 = eat and 0 = noeat
# bag of words approach is used in this model generation
training = []
output = []
output_empty = [0] * len(classes)

for doc in documents:
    bag = []
    pattern_words = doc[0]
    pattern_words = [stemmer.stem(word.lower()) for word in pattern_words]
    for w in words:
        bag.append(1) if w in pattern_words else bag.append(0)

    training.append(bag)
    output_row = list(output_empty)
    output_row[classes.index(doc[1])] = 1
    output.append(output_row)

# compute sigmoid function
# compute derivative of sigmoid function

def sigmoid(x):
    output = 1/(1+np.exp(-x))
    return output

def sigmoid_output_to_derivative(output):
    return output*(1-output)

# 1st part in learning is to modify the incoming data into required format
# tokenize the words and stem them
# return bag of words array: 0 or 1 for each word in the bag that exists in the sentence
# returned array contains all our corpus in the array with 0s and 1s depending on the existance of the word in corpus

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

# convert the sentence into the way we want, covert the sentence into bag of words
# get 1s and 0s based on the words available in our corpus
# x is the input layer, 
# l1 is a hidden layer which performs dot product of input sentence and the layer1 weight values
# l2 is the output layer which performs dot product of output of layer1 and the weight values
# l2 returns the classifier 1 or 0

def predictusing_ann(sentence, show_details=False):

    x = feature(sentence.lower(), words, show_details)
    if show_details:
        print ("sentence:", sentence, "\n feature set:", x)
    l0 = x
    l1 = sigmoid(np.dot(l0, weights_0))
    l2 = sigmoid(np.dot(l1, weights_1))
    return l2

# main learning algorithm with one hidden layer
# computes weights_0 and weights_1 values for the 1ts hidden layer and the output layer respectively
# weights_0 and weights_1 are randomly assigned with values of mean 0
# layer_0 contains the feature set data
# layer_1 contains the hidden layer 1
# layer_2 is our output layer
# FeedForward is done through layers 0, 1, 2
# weights are adjusted in the backpropagation by minimising the error


def train(X, y, hidden_neurons=10, alpha=0.5, epochs=50000, dropout=False, dropout_percent=0.05):

    print ("Training with %s neurons, alpha:%s, dropout:%s %s" % (hidden_neurons, str(alpha), dropout, dropout_percent if dropout else '') )
    print ("Input matrix: %sx%s    Output matrix: %sx%s" % (len(X),len(X[0]),1, len(classes)) )
    np.random.seed(1)

    last_mean_error = 1
    weights_0 = 2*np.random.random((len(X[0]), hidden_neurons)) - 1
    weights_1 = 2*np.random.random((hidden_neurons, len(classes))) - 1

    prev_weights_0_weight_update = np.zeros_like(weights_0)
    prev_weights_1_weight_update = np.zeros_like(weights_1)

    weights_0_direction_count = np.zeros_like(weights_0)
    weights_1_direction_count = np.zeros_like(weights_1)
        
    for j in iter(range(epochs+1)):

        layer_0 = X
        layer_1 = sigmoid(np.dot(layer_0, weights_0))
                
        if(dropout):
            layer_1 *= np.random.binomial(
                [np.ones((len(X),hidden_neurons))],
                1-dropout_percent)[0] * (1.0/(1-dropout_percent) )

        layer_2 = sigmoid(np.dot(layer_1, weights_1))

        # error calculation
        layer_2_error = y - layer_2

        if (j% 10000) == 0 and j > 5000:
            # if this 10k iteration's error is greater than the last iteration, break out
            if np.mean(np.abs(layer_2_error)) < last_mean_error:
                print ("delta after "+str(j)+" iterations:" + str(np.mean(np.abs(layer_2_error))) )
                last_mean_error = np.mean(np.abs(layer_2_error))
            else:
                print ("break:", np.mean(np.abs(layer_2_error)), ">", last_mean_error )
                break
        
        #Back propagation - adjust the weights based on the error
        

        layer_2_delta = layer_2_error * sigmoid_output_to_derivative(layer_2)
        layer_1_error = layer_2_delta.dot(weights_1.T)
        layer_1_delta = layer_1_error * sigmoid_output_to_derivative(layer_1)
        weights_1_weight_update = (layer_1.T.dot(layer_2_delta))
        weights_0_weight_update = (layer_0.T.dot(layer_1_delta))
        
        if(j > 0):
            weights_0_direction_count += np.abs(((weights_0_weight_update > 0)+0) - ((prev_weights_0_weight_update > 0) + 0))
            weights_1_direction_count += np.abs(((weights_1_weight_update > 0)+0) - ((prev_weights_1_weight_update > 0) + 0))        
        
        weights_1 += alpha * weights_1_weight_update
        weights_0 += alpha * weights_0_weight_update
        
        prev_weights_0_weight_update = weights_0_weight_update
        prev_weights_1_weight_update = weights_1_weight_update

    now = datetime.datetime.now()

    # copy weights into json format and save in a file
    weights = {'weights0': weights_0.tolist(), 'weights1': weights_1.tolist(),
               'datetime': now.strftime("%Y-%m-%d %H:%M"),
               'words': words,
               'classes': classes
              }
    weights_file = "weights.json"

    with open(weights_file, 'w') as outfile:
        json.dump(weights, outfile, indent=4, sort_keys=True)
    print ("saved weights to:", weights_file)

#initiate training

X = np.array(training)
y = np.array(output)

start_time = time.time()

train(X, y, hidden_neurons=20, alpha=0.2, epochs=100000, dropout=False, dropout_percent=0.05)

elapsed_time = time.time() - start_time
print ("processing time:", elapsed_time, "seconds")

#copy our persisted weight values into the system
weights_file = 'weights.json' 
with open(weights_file) as data_file: 
    weights = json.load(data_file) 
    weights_0 = np.asarray(weights['weights0']) 
    weights_1 = np.asarray(weights['weights1'])

# main classification initiator
# will be used by our validation an testing functions

def classify(sentence, show_details=True):
    results = predictusing_ann(sentence, show_details)

    results = [[i,r] for i,r in enumerate(results) if r>ERROR_THRESHOLD ] 
    results.sort(key=lambda x: x[1], reverse=True) 
    return_results =[[classes[r[0]],r[1]] for r in results]
    #print ("%s \n classification: %s" % (sentence, return_results))
    return return_results

# use model to check it's performance on our cross validation data set
# load cross validation data set into the model and check it's accuracy
# we have 10 records set aside for cross validation, accuracy will be calculated for this specific dataset
# for all values of alpha or different dropout percentages
# specify error threshold
ERROR_THRESHOLD = 0.2
counter = 0
pos = 0
print("###### VALIDATION DATA ######")
for sentence in validation_data:
    if(classify(sentence['sentence'])[0][0] == sentence['class']):
        pos = pos + 1
    counter += 1
    print(classify(sentence['sentence'])[0][0],sentence['class'])
print('ACCURACY',pos/counter)

# test set! test our model
test_data = []
test_data.append({"class":"eat","sentence":"can we go out for a sandwich"})
test_data.append({"class":"noeat","sentence":"i dont want to eat anything"})
test_data.append({"class":"eat","sentence":"I will talk to you over lunch"})
test_data.append({"class":"eat","sentence":"food is fine but im not sure with french"})
test_data.append({"class":"noeat","sentence":"i dont know about chinese"})
test_data.append({"class":"noeat","sentence":"i dont like chinese and french"})

counter = 0
pos = 0
print("###### TEST DATA ######")
for sentence in test_data:
    if(classify(sentence['sentence'])[0][0] == sentence['class']):
        pos = pos + 1
    counter += 1
    print(classify(sentence['sentence'])[0][0],sentence['class'])
print('ACCURACY',pos/counter)


print()
