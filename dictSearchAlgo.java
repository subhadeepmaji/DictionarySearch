import java.io.*;
import java.lang.*;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

/*

  Anagram Search
	Prefix and suffix search on dictionary 
	
	Author : Subhadeep Maji


*/

class dictSlot {
	
	ArrayList<String> words;
	
	dictSlot()
	{
		words = new ArrayList<String>();
		
	}

	void addWord(String s)
	{
		words.add(s);
	}
	
	ArrayList<String> getWords()
	{
		return words;
	}
		

}

class dictHash {
	
	private HashMap hashWords;
	
	dictHash()
	{	
		hashWords = new HashMap(100); 
	}
	
	long computeHashKey(String s)
	{
		int len = s.length();
		char [] word = new char[len];
		long key = 0 ;
		
		word = s.toCharArray();
		Arrays.sort(word);
		
		for(int i = 0; i < len ; i++)
			{
				key += Math.pow(26,len-i-1)*((int)word[i] % 26); 
				
			}
		return key;
	
	}

	void insertHash(String s)
	{
		long key;
		key = computeHashKey(s);
		if (hashWords.containsKey(key))
			{
				dictSlot cmn;
				cmn = (dictSlot)hashWords.get(key);
				cmn.addWord(s);
			}
		else
			{
				dictSlot cmn = new dictSlot();
				cmn.addWord(s);
				hashWords.put(key,cmn);		
		
			}
	}

	ArrayList<String> searchHash(String s)
	{
		long key;
		ArrayList<String>retWords;
		key = computeHashKey(s);
		
		if (hashWords.containsKey(key))
			{
				dictSlot data;
				data = (dictSlot)hashWords.get(key);
				retWords = data.getWords();
				return retWords;
			
			}
		return null;	
		
	}

	

}

class suffixTreeNode {

	private char nodeVal;
	private String leafPtr;
	private suffixTreeNode[] child;
	
	suffixTreeNode(char val)
	{
		nodeVal = val;
		leafPtr = null;
		child = new suffixTreeNode[26];
	
	}

	suffixTreeNode(char val, String word)
	{
		nodeVal = val;
		leafPtr = word;
		child = new suffixTreeNode[26];
		
	}
	
	String getWord()
	{
		return leafPtr; 
	}
	
	suffixTreeNode hasChild(int index)
	{
		if (child[index] != null)
			return child[index];
		else
			return null;
	
	}
	
	void setChild(int index, suffixTreeNode childN)
	{
		child[index] = childN;
	}
	
}
	
class suffixTree {

	suffixTreeNode root;
	boolean isprefix;
	
	suffixTree(boolean isprefix)
	{
		root = new 	suffixTreeNode((char)1);
		this.isprefix = isprefix;
	}	
		
	void addWordTree(String word)
	{
		suffixTreeNode temp;
		int wordLen = word.length();
		int inc,i;	
		char [] wordArr = new char[wordLen];
		wordArr = word.toCharArray();
		suffixTreeNode inode;
		temp = root;
		
		inc = (isprefix) ? 1 : -1 ;
		i = (isprefix) ? 0 : wordLen - 1;	
		
		while(i < wordLen && i >= 0)
		{
			
			if (temp.hasChild((int)wordArr[i]-97) != null)
			{
				temp = temp.hasChild((int)wordArr[i]-97);
					
			}
			else
			{
				
				if (i == wordLen - 1 && isprefix)
					inode = new suffixTreeNode(wordArr[i], word);
				else if (i == 0 && !isprefix)
					inode = new suffixTreeNode(wordArr[i], word);
				else
					inode = new suffixTreeNode(wordArr[i]);
				
				
				temp.setChild((int)wordArr[i]-97,inode);
				temp = inode;	
				
			}
			i += inc;
	
		}
	
	}
	
	ArrayList<String> searchTree(String prefix)
	{
	
		suffixTreeNode temp;
		int wordLen = prefix.length(); 
		char [] wordArr = new char[wordLen];
		wordArr = prefix.toCharArray();
		int i = 0;
		int j = wordLen - 1;
		
		if (!isprefix) 
		{
			char c;
			while(i<j)
			{
				c = wordArr[i];
				wordArr[i] = wordArr[j];
				wordArr[j] = c;
				i++;
				j--;
			}			
			
		}
		
		ArrayList<String> wordMat;
		temp = root;
		i = 0;
		while(i < wordLen)	
		{
			if (temp.hasChild((int)wordArr[i]-97) == null) 
				return null;
			else
				temp = temp.hasChild((int)wordArr[i]-97);
			i++;	
		}
		
		wordMat = new ArrayList<String>(10);
		getAllWords(temp,wordMat);
		return wordMat;
	
	}
	
	void getAllWords(suffixTreeNode preRoot, ArrayList<String> wordMat)
	{
		if(preRoot == null)
			return;
		
		if(preRoot.getWord() != null)
			wordMat.add(preRoot.getWord());
			
		for(int i = 0; i < 26 ; i++)
			getAllWords(preRoot.hasChild(i),wordMat);
	
	}		
	
}	

public class dictSearchAlgo {
	
	private FileInputStream fs;
	private DataInputStream ds;
	private BufferedReader  br;

	public void readDict(Object H)
	{
		int k=0;
		try {
			fs = new FileInputStream("dictclean");
			}
		catch(Exception e)
			{
				
			}
		
		ds = new DataInputStream(fs);
		String word;
		
		br = new BufferedReader(new InputStreamReader(ds));
		
		try {
			while((word = br.readLine()) != null)
			{
				k++;
				//System.out.println(word);
				if (H instanceof dictHash)
					((dictHash)H).insertHash(word);
				if (H instanceof suffixTree)
					((suffixTree)H).addWordTree(word);
		
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}		
	
	public ArrayList<String> anagramSearch(String word, dictHash H)
	{
		return H.searchHash(word);
	}
	
	public ArrayList<String> partialSearch(String word, suffixTree T)
	{
		return T.searchTree(word);
	}
	

	public static void main(String[] a)
	{
		
		dictSearchAlgo DA = new dictSearchAlgo();	
		dictHash H = new dictHash();
		suffixTree pft = new suffixTree(true);
		suffixTree sft = new suffixTree(false);
		ArrayList<String>words;
		DA.readDict(H);
		DA.readDict(pft);
		DA.readDict(sft);
		
		String searchword = new String("cat");
		words = DA.anagramSearch(searchword,H);
		
		for(int i=0;i<words.size();i++)
			System.out.println(words.get(i));
		
		searchword = new String("eep");	
		words = DA.partialSearch(searchword,sft);
		
		for(int i=0;i<words.size();i++)
			System.out.println(words.get(i));	
	}

}
