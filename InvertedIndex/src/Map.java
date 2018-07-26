import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Map extends Mapper<Text, Text, Text, DocFreqWritable>
{
	private Text word = new Text();
	private DocFreqWritable docFreq = new DocFreqWritable();
	
	public void map(Text docName, Text value, Context context)
			throws IOException, InterruptedException
	{
		String line = value.toString();
		StringTokenizer tokenizer = new StringTokenizer(line, " \t\r\n\f|,.'`?!(){}[]<>");
		docFreq.doc = docName.toString();
		// docFreq.freq = 1;
	
		while (tokenizer.hasMoreTokens())
		{
			word.set(tokenizer.nextToken().toLowerCase());
			context.write(word, docFreq);
		}
	}
}
