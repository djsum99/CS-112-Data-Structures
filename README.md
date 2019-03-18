# CS-112-Data-Structures

These are some of the projects I did while taking Data Structures (CS 112) at Rutgers. All of these projects were done in Java.

Here are a few short descriptions of each of the projects:

1. Expression Evaluation
      - Simplifies a complex expression of numbers to a single number
      - Variables and arrays can be included in these expressions if they are defined in a txt file
      - The classes Expression, Evaluator, Array, and Variable have been included
      
2. Friends
      - Uses a txt file to create a graph representing the relationships between several people
      - The program can search for:
          - the shortest chain between two people
          - "cliques" of people who are connected together and have the same characteristics
          - connectors, which are people that, when removed from the graph, will completely disconnect the chain between two other people
      - The classes Friends and Graph have been included
      
3. Little Search Engine
      - Uses a the words in a txt file to create a word index in the form of a hash table
      - Categorizes words based on the documents they are in and the frequencies of their occurrences in each of those documents
      - The classes Occurrence and LittleSearchEngine have been included
4. Trie
      - Uses the words in a txt file to generate a trie based on prefixes
      - The program can:
          - search for a word in the trie
          - output all of the words in the trie that start with a certain prefix
      - The classes Trie, TrieApp, and TrieNode have been included
