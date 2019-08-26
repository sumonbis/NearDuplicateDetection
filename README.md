# Near Duplicate Detection Using Simhash
Near duplicate detection in a large collection of files is a well-studied problem in data science. Many Locality Sensitive Hashing (LSH) algorithms have been recently developed to solve this problem. Among them simhash is a very efficient LSH algorithm that uses probabilistic method to generate similar fingerprints for similar objects. In this project, we have implemented simhash algorithm to evaluate approximate cosine similarity between two documents from a large collection of files. We have preprocessed the documents, created word vectors with weight and then implemented simhash algorithm to generate 64-bit fingerprint of each document. Then we have implemented block permuted hamming search in our fingerprint space. Block permuted Hamming search helps us to reduce the time to find similar pairs significantly. However, we have to consider a few false negatives in this result. By designing the block permutation in a better way, we can reduce the false negative rate. The hamming distance solution still takes a substantial amount of time for million o documents which might be infeasible for web crawler. In addition, we need to store multiple copies of the fingerprints in memory. Therefore, Hamming search in simhash space is still an open problem.

## Problem Definition
Suppose, we have millions of documents and given a new document we have to find all the near duplicates (e.g., 95% or more similar) from the collection in a reasonable amount of time. We can divide the problem into two parts: how to measure similarity between two documents and how to find the similar documents form a large collection efficiently? Therefore, our goal is to solve the following problems:

1. Given two documents D\_a and D\_b, what is the similarity measure between them?
2. Given a document D\_a, find all the documents that are similar to D\_a.
3. Identify all the pairs in the collection that are near duplicate of each other.

There are a few challenges related to the above problems. First, our algorithm should be designed for millions of documents. Second, the files should be compressed enough to fit in memory. Finally, the algorithm must be efficient to find near duplicate in small amount of time.

## Simhash
Charikar’s simhash [1] is a dimensionality reduction technique which maps high dimensional documents to very small sized fingerprints. We can compute the Hamming distance of two finger- prints to measure the cosine similarity.

## Algorithm

The basic sketch of using simhash algorithm to measure similarity is:

1. Step 1: Convert the document into set of features associated with weights.
2. Step 2: Create f-bit fingerprint for each document.
3. Step 3: Calculate Hamming distance between two fingerprints to measure similarity between corresponding documents.

First, we have to choose features for each document. Feature selection also depends on the application. Word is a very obvious choice as features. We can also choose shingle as feature. A k-shingle is every k-length adjacent set of characters. Consider the sentence - “The earth is moving.” The set of k-shingles for k = 5: {The\_e, he\_ea, e\_ear, \_eart, . . . . }. If the application requires such similarity measure that demands the order of appearance then shingle can be a good choice as feature. In this project, we used word as our feature. There are some preprocessing before converting them as set of features. We converted the whole document to lowercase as case sensitivity does not contribute to similarity score. Then we have removed the stop words (e.g., a, an, the etc.) and punctuation symbols which are common in every document. Next, the weight of each word is calculated. There are several ways to calculate the weight of each feature. The feature with more weight will contribute more to the similarity score. A very intuitive weight measure is the frequency of each term. The term which appears more in a document carries more weight. We can also use TF-IDF (Term Frequency-Inverse Document Frequency) as weight.
After the preprocessing is done, we create f-bit binary fingerprint of each document using simhash algorithm. The value of f is 32 or 64 in practice. First, each feature is converted to a f-bit binary hash value using a uniformly distributed hash function (e.g., MD5, FNV, Murmur). Then we define a vector of length f, initially with all zero values. Now, we iterate through each bit position (1 to f). If the bit position is 1 then we add the weight and if the bit position is 0 then we subtract the weight. After all the iterations, we get a vector of real values of length f. Finally, if the ith value is negative we convert it to 0, otherwise we convert it to 1. Thus, we get f-bit fingerprint of a document

## References
1. Moses S Charikar. Similarity estimation techniques from rounding algorithms. Proceed- ings of the thiry-fourth annual ACM symposium on Theory of computing-ACM, pages 380–388, 2002.
