//import java.io.File;
//import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;

public class SearchEngine{
   
    public static void main(String[] args) throws Exception{
    	// Read InvertedIndex File  From Local
        //File inFile = new File("/home/hadoop/hadoopLocalDir/", "InvIndex");
        //BufferedReader br = new BufferedReader(new FileReader(inFile));
    	
    	/// OR ///
    	
    	// Read InvertedIndex File From HDFS
    	Configuration conf = new Configuration();
    	conf.set("fs.default.name", "hdfs://localhost:9000");
    	FileSystem fs = FileSystem.get(conf);
    	FSDataInputStream is = fs.open(new Path("/output/InvertedIndex/part-r-00000"));
    	BufferedReader br = new BufferedReader(new InputStreamReader(is));
    	
    	// Make inverted indices map
        HashMap<String, ArrayList<String>> invertedIndicesMap = new HashMap<String, ArrayList<String>>();
        String line;
        while ((line = br.readLine()) != null) {
        	StringTokenizer st1 = new StringTokenizer(line, "\t");
        	String key = st1.nextToken();
        	StringTokenizer st2 = new StringTokenizer(st1.nextToken(), ";");
        	ArrayList<String> value = new ArrayList<String>();
            while(st2.hasMoreTokens())
            	value.add(st2.nextToken());

            invertedIndicesMap.put(key, value);
        }

        br.close();
        

        // Enter search keyword input
        Scanner scanner = new Scanner(System.in);
	    while(true)
	    {
	    	System.out.print("Search >> ");
	        String searchTo = scanner.nextLine();
	        if(searchTo.equals(""))
	        	continue;
	        if(searchTo.equals("exit"))
	        	break;
	        StringTokenizer strToken = new StringTokenizer(searchTo, " \t");
	        ArrayList<String> searchingWords = new ArrayList<String>();
	        while(strToken.hasMoreTokens())
	        {
	        	searchingWords.add(strToken.nextToken());
	        }

	        ArrayList<String> resultsInOrder;
	        resultsInOrder = SearchByInvertedIndices(searchingWords, invertedIndicesMap);
	        
	        // Print Result
		    for(String doc : resultsInOrder)
		    	System.out.print(doc + " ");
		    System.out.println();
	    }
	    scanner.close();
    }
    

	static private ArrayList<String> SortByValue(final HashMap<String, Long> map) {
	    ArrayList<String> list = new ArrayList<String>();
	    list.addAll(map.keySet());
	     
	    Collections.sort(list,new Comparator() {
	        public int compare(Object o1,Object o2) {
	            Object v1 = map.get(o1);
	            Object v2 = map.get(o2);
	            return ((Comparable) v2).compareTo(v1);
	        }
	    });
	    
	    return list;
	}
	
	
	static private ArrayList<String> SearchByInvertedIndices(
											ArrayList<String> keywords,
											HashMap<String, ArrayList<String>> invertedIndices)
	//precondition : keywords is not empty
	{
		ArrayList<String> noResult = new ArrayList<String>();
		noResult.add("No Result!!");

		if(keywords.isEmpty())
			return noResult;
		
        // Calculate priority
        HashMap<String, Long> docFreqSums = new HashMap<String, Long>();
		Iterator<String> keywordsIter = keywords.iterator();
		String keyword;
		
		
		keyword = keywordsIter.next().toLowerCase();
		if( !invertedIndices.containsKey(keyword))
			return noResult;
		
		ArrayList<String> firstKeywordDocFreqs = invertedIndices.get(keyword);
		for(int i=0; i<firstKeywordDocFreqs.size(); ++i)
		{
	        StringTokenizer strToken = new StringTokenizer(firstKeywordDocFreqs.get(i), ":");
	        String doc = strToken.nextToken();
	        Long freq = Long.parseLong(strToken.nextToken());
	        docFreqSums.put(doc, freq);
		}
			
	    while(keywordsIter.hasNext())
	    {
			keyword = keywordsIter.next().toLowerCase();
			if( !invertedIndices.containsKey(keyword))
				return noResult;
			
	    	ArrayList<String> docFreqs = invertedIndices.get(keyword);
	    	for(String docOfResult : ((HashMap<String, Long>)docFreqSums.clone()).keySet() )
	    	{
        		boolean isDup = false;
        		for(int i=0; i< docFreqs.size(); ++i)
        		{
        	        StringTokenizer strToken = new StringTokenizer(docFreqs.get(i), ":");
        	        String doc = strToken.nextToken();
        	        Long freq = Long.parseLong(strToken.nextToken());
        			if(docOfResult.equals(doc))
        			{
        				isDup = true;
        				docFreqSums.put(docOfResult, docFreqSums.get(docOfResult)+freq);
        				break;
        			}
        		}
        		if(!isDup)
        			docFreqSums.remove(docOfResult);
        	}
	    }
	    
        // Print Result
    	return SortByValue(docFreqSums);
	}
}
