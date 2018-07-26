//import java.io.File;
//import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.hadoop.fs.*;
import org.apache.hadoop.conf.Configuration;

public class SearchEngine_old{
   
    public static void main(String[] args) throws Exception{
    	// Read InvertedIndex File From Local
        //File inFile = new File("/home/hadoop/hadoopLocalDir/", "InvIndex");
        //BufferedReader br = new BufferedReader(new FileReader(inFile));
    	
    	// OR Read ... Form HDFS
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
	        if(searchTo.equals("exit"))
	        	break;
	        StringTokenizer strToken = new StringTokenizer(searchTo, " \t");
	        ArrayList<ArrayList<String>> searchingWords = new ArrayList<ArrayList<String>>();
	        
	        //int i=0;
	        while(strToken.hasMoreTokens())
	        {
	        	String key = strToken.nextToken().toLowerCase();
	        	if(invertedIndicesMap.containsKey(key))
	        		searchingWords.add(invertedIndicesMap.get(key));
	        		
	        }
	        
	        
	        // Calculate priority
	        HashMap<String, Long> samesame;
	        samesame = CalculateDuplication(searchingWords);
	        
	        // Print Result
	    	Iterator iter = SortByValue(samesame).iterator();
		    while(iter.hasNext())
		    	System.out.print(iter.next() + " ");
		    System.out.println();
	    }
	    scanner.close();
    }
    

	static private List<String> SortByValue(final HashMap<String, Long> map) {
	    List<String> list = new ArrayList<String>();
	    list.addAll(map.keySet());
	     
	    Collections.sort(list,new Comparator() {
	        public int compare(Object o1,Object o2) {
	            Object v1 = map.get(o1);
	            Object v2 = map.get(o2);
	            return ((Comparable) v1).compareTo(v2);
	        }
	    });
	    
	    return list;
	}
	
	
	static private HashMap<String, Long> CalculateDuplication(ArrayList<ArrayList<String>> keywords)
	{
        HashMap<String, Long> result = new HashMap<String, Long>();
		if(keywords.isEmpty())
		{
			result.put("No Result", -1L);
			return result;
		}

		Iterator<ArrayList<String>> keywordIter = keywords.iterator();
		ArrayList<String> firstEntry = keywordIter.next();
		for(int i=0; i<firstEntry.size(); ++i)
		{
	        StringTokenizer strToken = new StringTokenizer(firstEntry.get(i), ":");
	        String doc = strToken.nextToken();
	        Long freq = Long.parseLong(strToken.nextToken());
			result.put(doc, freq);
		}
			
	    while(keywordIter.hasNext())
	    {
	    	ArrayList<String> docsFreqs = keywordIter.next();
    		Iterator<String> resultKeyIter = ((HashMap<String, Long>)result.clone()).keySet().iterator();
    		while(resultKeyIter.hasNext())
        	{
    			String docOfResult = resultKeyIter.next();
        		boolean isDup = false;
        		for(int i=0; i< docsFreqs.size(); ++i)
        		{
        	        StringTokenizer strToken = new StringTokenizer(docsFreqs.get(i), ":");
        	        String doc = strToken.nextToken();
        	        Long freq = Long.parseLong(strToken.nextToken());
        			if(docOfResult.equals(doc))
        			{
        				isDup = true;
        				result.put(docOfResult, result.get(docOfResult)+freq);
        				break;
        			}
        		}
        		if(!isDup)
        			result.remove(docOfResult);
        	}
	    }
	    
    	return result;
	}
}
