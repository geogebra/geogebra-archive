package jscl.text;

public class IndentedBuffer {
	StringBuffer buffer=new StringBuffer();

	public IndentedBuffer append(Object s) {
		buffer.append(s);
		return this;
	}

	public IndentedBuffer append(int s) {
		buffer.append(s);
		return this;
	}

	public IndentedBuffer append(int indent, String s) {
		for(int n=0;n<s.length();) {
			int p=s.indexOf('\n',n);
			for(int i=0;i<indent;i++) buffer.append(" ");
			if(p>-1) {
				buffer.append(s.substring(n,p+1));
				n=p+1;
			} else {
				buffer.append(s.substring(n));
				n=s.length();
			}
		}
		return this;
	}

	public String toString() {
		return buffer.toString();
	}
}
