# TinyImage

The program classifies given images into categories given by the training data set. 

The method used to classify the images is the k nearest neighbours method where k is 25 in the program. The feature extractor for this program uses the tiny image feature in which the image is first turned into a square image and then is resized into a very small image (32 by 32 in this case). The pixels of the small image is converted into an array of doubles which is used as the feature vector for the classification. This program uses the OpenIMAJ library.
