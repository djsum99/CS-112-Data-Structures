package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		ArrayList<String> keyWords = new ArrayList<String>();
		Scanner sc = new Scanner(new FileReader(docFile));
		while(sc.hasNext()) {
			String phrase = sc.next();
			if(getKeyword(phrase)!=null) {
				keyWords.add(getKeyword(phrase));
			}
		}
		sc.close();
		HashMap<String,Occurrence> map = new HashMap<String,Occurrence>(1000,2.0f);
		while(!keyWords.isEmpty()) {
			String word = keyWords.get(0);
			int counter = 0;
			for(int i=0;i<keyWords.size();i++) {
				if(word.equals(keyWords.get(i))) {
					counter++;
				}
			}
			map.put(word, new Occurrence(docFile,counter));
			while(keyWords.remove(word)) {}
		}
		return map;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for(String word: kws.keySet()) {
			Occurrence o = kws.get(word);
			if(keywordsIndex.containsKey(word)) {
				ArrayList<Occurrence> arr = keywordsIndex.get(word);
				arr.add(o);
				insertLastOccurrence(arr);
				keywordsIndex.put(word,arr);
			}
			else {
				ArrayList<Occurrence> arr = new ArrayList<Occurrence>();
				arr.add(o);
				keywordsIndex.put(word,arr);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		if(word.length()==0) {
			return null;
		}
		if(noiseWords.contains(word)) {
			return null;
		}
		word = word.trim();
		word = word.toLowerCase();
		while((word.substring(0,1).equals("'")||word.substring(0,1).equals("(")||word.substring(0,1).equals("\""))&&word.length()>1) {
			word=word.substring(1);
		}
		if(word.length()==0) {
			return null;
		}
		if(noiseWords.contains(word)) {
			return null;
		}
		boolean isLetter = true;
		int i = 0;
		while(isLetter&&i<word.length()) {
			if(Character.isLetter(word.charAt(i))) {
				i++;
			}
			else {
				isLetter = false;
			}
		}
		if(i==word.length()) {
			if(noiseWords.contains(word)) {
				return null;
			}
			return word;
		}
		String wordEnd = word.substring(i);
		word = word.substring(0,i);
		if(word.length()==0) {
			return null;
		}
		for(int j=0;j<wordEnd.length();j++) {
			if(!(wordEnd.substring(j,j+1).equals(".")||wordEnd.substring(j,j+1).equals(",")||wordEnd.substring(j,j+1).equals(":")||
					wordEnd.substring(j,j+1).equals(";")||wordEnd.substring(j,j+1).equals("?")||wordEnd.substring(j,j+1).equals("!")||
					wordEnd.substring(j,j+1).equals("\"")||wordEnd.substring(j,j+1).equals(")")||wordEnd.substring(j,j+1).equals("'"))) {
				return null;
			}
		}
		if(noiseWords.contains(word)) {
			return null;
		}
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		if(occs.size()==1) {
			return null;
		}
		ArrayList<Integer> ints = new ArrayList<Integer>();
		Occurrence o = occs.get(occs.size()-1);
		ArrayList<Occurrence> withoutLast = occs;
		withoutLast.remove(withoutLast.size()-1);
		int freq = o.frequency;
		int beginning = 0, end = withoutLast.size()-1, mid = 0;
		while(beginning<=end) {
			mid = beginning+(end-beginning)/2;
			ints.add(mid);
			if(withoutLast.get(mid).frequency==freq) {
				withoutLast.add(mid,o);
				occs = withoutLast;
				return ints;
			}
			if(withoutLast.get(mid).frequency<freq) {
				end = mid-1;
			}
			else {
				beginning = mid+1;
			}
		}
		if(freq>withoutLast.get(mid).frequency) {
			withoutLast.add(mid,o);
		}
		else {
			withoutLast.add(mid+1,o);
		}
		occs = withoutLast;
		return ints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		if(!(keywordsIndex.containsKey(kw1)||keywordsIndex.containsKey(kw2))) {
			return null;
		}
		if(!keywordsIndex.containsKey(kw1)) {
			return top5ForOne(kw2);
		}
		if(!keywordsIndex.containsKey(kw2)) {
			return top5ForOne(kw1);
		}
		ArrayList<String> top5 = new ArrayList<String>();
		ArrayList<Occurrence> occs1 = keywordsIndex.get(kw1);
		ArrayList<Occurrence> occs2 = keywordsIndex.get(kw2);
		while(top5.size()<5&&!(occs1.isEmpty()&&occs2.isEmpty())) {
			int freq = 0;
			Occurrence o = null;
			for(int i=0;i<occs1.size();i++) {
				if(freq<occs1.get(i).frequency) {
					freq=occs1.get(i).frequency;
					o=occs1.get(i);
				}
			}
			for(int i=0;i<occs2.size();i++) {
				if(freq<occs2.get(i).frequency) {
					freq=occs2.get(i).frequency;
					o=occs2.get(i);
				}
			}
			top5.add(o.document);
			for(int i=0;i<occs1.size();i++) {
				if(occs1.get(i).document.equals(o.document)) {
					occs1.remove(i);
				}
			}
			for(int i=0;i<occs2.size();i++) {
				if(occs2.get(i).document.equals(o.document)) {
					occs2.remove(i);
				}
			}
		}
		return top5;
	}
	private ArrayList<String> top5ForOne(String kw){
		ArrayList<String> top5 = new ArrayList<String>();
		ArrayList<Occurrence> occs = keywordsIndex.get(kw);
		while(top5.size()<5&&!occs.isEmpty()) {
			int freq = 0;
			Occurrence o = null;
			for(int i=0;i<occs.size();i++) {
				if(freq<occs.get(i).frequency) {
					freq=occs.get(i).frequency;
					o=occs.get(i);
				}
			}
			top5.add(o.document);
			occs.remove(o);
		}
		return top5;
	}
}
