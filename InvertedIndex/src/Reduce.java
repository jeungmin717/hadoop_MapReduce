import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Reduce extends Reducer<Text, DocFreqWritable, Text, Text>
{
	HashMap<String, Long> docs = new HashMap<String, Long>();
	
	public void reduce(Text word, Iterable<DocFreqWritable> values, Context context)
		throws IOException, InterruptedException
	{
		StringBuilder docFreqFormat = new StringBuilder();
		
		docs.clear();
		for (DocFreqWritable docFreq : values)
		{
			String doc = docFreq.doc;
			if(docs.containsKey(doc))
				docs.put(doc, docs.get(doc)+docFreq.freq);
			else
				docs.put(doc, docFreq.freq);
		}
	    Iterator<Entry<String, Long>> it = docs.entrySet().iterator();
	    while(it.hasNext())
	    {
	    	Entry<String, Long> docfrq = it.next();
	    	docFreqFormat.append(docfrq.getKey() +":"+ docfrq.getValue() + ";");
	    }
	    context.write(word, new Text(docFreqFormat.toString()));
	}

}
