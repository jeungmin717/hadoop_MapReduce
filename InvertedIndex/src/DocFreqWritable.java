import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableUtils;

public class DocFreqWritable implements Writable
{
	public String doc;
	public long freq;
	
	public DocFreqWritable()
	{
		doc = "";
		freq = 1;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		doc = WritableUtils.readString(in);
		freq = in.readLong();
	}

	@Override
	public void write(DataOutput out) throws IOException {
		WritableUtils.writeString(out, doc);
		out.writeLong(freq);
	}
}
