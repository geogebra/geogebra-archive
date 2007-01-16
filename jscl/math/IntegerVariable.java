package jscl.math;


class IntegerVariable extends GenericVariable {
	IntegerVariable(Generic generic) {
		super(generic);
	}

	public Generic substitute(Variable variable, Generic generic) {
		if(isIdentity(variable)) return generic;
		else return content.substitute(variable,generic);
	}

	public Generic elementary() {
		return content.elementary();
	}

	public Generic simplify() {
		return content.simplify();
	}

	public String toString() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("(").append(content).append(")");
		return buffer.toString();
	}

	public String toJava() {
		StringBuffer buffer=new StringBuffer();
		buffer.append("(").append(content.toJava()).append(")");
		return buffer.toString();
	}

	/*
    public void toMathML(Element element, Object data) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        int exponent=data instanceof Integer?((Integer)data).intValue():1;
        if(exponent==1) bodyToMathML(element);
        else {
            Element e1=new ElementImpl(document,"msup");
            bodyToMathML(e1);
            Element e2=new ElementImpl(document,"mn");
            e2.appendChild(new TextImpl(document,String.valueOf(exponent)));
            e1.appendChild(e2);
            element.appendChild(e1);
        }
    }

    void bodyToMathML(Element element) {
        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
        Element e1=new ElementImpl(document,"mfenced");
        content.toMathML(e1,null);
        element.appendChild(e1);
    }
*/
	protected Variable newinstance() {
		return new IntegerVariable(null);
	}
}
