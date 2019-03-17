package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null,null,null);
		buildTrieHelper(root,0,allWords,root);
		return root;
	}
	
	private static void buildTrieHelper(TrieNode root, int index, String[] allWords, TrieNode origRoot) {
		if(index<allWords.length) {
			if(root.substr==null&&root.firstChild==null) {
				Indexes sub = new Indexes(index,(short)0,(short)(allWords[index].length()-1));
				TrieNode first = new TrieNode(sub,null,null);
				root.firstChild = first;
				origRoot = root.firstChild;
				buildTrieHelper(root.firstChild,index+1,allWords,origRoot);
			}
			else {
				TrieNode ptr = root;
				int wordInd = ptr.substr.wordIndex;
				int startInd = ptr.substr.startIndex;
				int endInd = ptr.substr.endIndex;
				String cut1 = allWords[wordInd].substring(startInd,endInd+1);
				String cut2 = allWords[index].substring(startInd);
				if(cut1.substring(0,1).equals(cut2.substring(0,1))==false) {
					if(ptr.sibling==null) {
						Indexes sub = new Indexes(index,(short)startInd,(short)(allWords[index].length()-1));
						TrieNode sibl = new TrieNode(sub,null,null);
						ptr.sibling = sibl;
						buildTrieHelper(origRoot,index+1,allWords,origRoot);
					}
					else {
						buildTrieHelper(ptr.sibling,index,allWords,origRoot);
					}
				}
				else {
					boolean charsEqual = true;
					int counter = 0;
					while(charsEqual&&counter<cut1.length()&&counter<cut2.length()) {
						if(cut1.substring(counter,counter+1).equals(cut2.substring(counter,counter+1))){
							counter++;
						}
						else {
							charsEqual = false;
						}
					}
					if(ptr.firstChild==null) {
						Indexes oneInds = new Indexes(wordInd,(short)(startInd+counter),(short)endInd);
						TrieNode one = new TrieNode(oneInds,null,null);
						Indexes twoInds = new Indexes(index,(short)(startInd+counter),(short)(allWords[index].length()-1));
						TrieNode two = new TrieNode(twoInds,null,null);
						one.sibling = two;
						Indexes rootInds = new Indexes(wordInd,(short)startInd,(short)(startInd+counter-1));
						ptr.substr = rootInds;
						ptr.firstChild = one;
						buildTrieHelper(origRoot,index+1,allWords,origRoot);
					}
					else {
						if(allWords[wordInd].substring(startInd,endInd+1).equals(allWords[index].substring(startInd, endInd+1))) {
							buildTrieHelper(ptr.firstChild,index,allWords,origRoot);
						}
						else {
							Indexes sub = new Indexes(wordInd,(short)(startInd+counter),(short)endInd);
							TrieNode between = new TrieNode(sub,ptr.firstChild,null);
							ptr.firstChild = between;
							Indexes ptrInds = new Indexes(wordInd,(short)startInd,(short)(startInd+counter-1));
							ptr.substr = ptrInds;
							Indexes sibInds = new Indexes(index,(short)(startInd+counter),(short)(allWords[index].length()-1));
							TrieNode sib = new TrieNode(sibInds,null,null);
							between.sibling = sib;
							buildTrieHelper(origRoot,index+1,allWords,origRoot);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root,
										String[] allWords, String prefix) {
		ArrayList<TrieNode> list = new ArrayList<TrieNode>();
		if(root!=null) {
			if(root.firstChild!=null) {
				compList(root.firstChild,allWords,prefix,list);
			}
		}
		if(list.size()==0) {
			return null;
		}
		return list;
	}
	
	private static void compList(TrieNode root, String[] allWords, String prefix, ArrayList<TrieNode> list) {
		if(root!=null) {
			String wordPrefix = allWords[root.substr.wordIndex].substring(root.substr.startIndex,root.substr.endIndex+1);
			boolean prefixLonger = wordPrefix.length()<prefix.length();
			if(!prefixLonger) {
				String pr = wordPrefix.substring(0,prefix.length());
				if(pr.equals(prefix)) {
					TrieNode ptr = root.sibling;
					root.sibling = null;
					addAllLeafNodes(list,root);
					root.sibling = ptr;
				}
				else {
					if(root.sibling!=null) {
						compList(root.sibling,allWords,prefix,list);
					}
				}
			}
			else {
				String pr = prefix.substring(0,wordPrefix.length());
				if(pr.equals(wordPrefix)) {
					String newPrefix = prefix.substring(pr.length(),prefix.length());
					compList(root.firstChild,allWords,newPrefix,list);
				}
				else {
					compList(root.sibling,allWords,prefix,list);
				}
			}
		}
	}
	
	private static void addAllLeafNodes(ArrayList<TrieNode> list, TrieNode root) {
		if(root!=null) {
			addAllLeafNodes(list,root.sibling);
			if(root.firstChild==null) {
				list.add(root);
			}
			else {
				addAllLeafNodes(list, root.firstChild);
			}
		}
	}
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
