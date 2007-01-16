package jscl.math.function;

import jscl.math.DoubleVariable;
import jscl.math.Generic;
import jscl.math.JSCLInteger;
import jscl.math.NotIntegerException;
import jscl.math.NotIntegrableException;
import jscl.math.NotVariableException;
import jscl.math.NumericWrapper;
import jscl.math.Variable;

public class Comparison extends Function {
    int operator;

    public Comparison(String name, Generic expression1, Generic expression2) {
        super(name,new Generic[] {expression1,expression2});
        for(int i=0;i<easo.length;i++) if(name.compareTo(easo[i])==0) operator=i;
    }

    public Generic antiderivative(int n) throws NotIntegrableException {
        throw new NotIntegrableException();
    }

    public Generic derivative(int n) {
        return JSCLInteger.valueOf(0);
    }

    public Generic evaluate() {
        try {
            int n1=parameter[0].integerValue().intValue();
            int n2=parameter[1].integerValue().intValue();
            return JSCLInteger.valueOf(compare(true)?1:0);
        } catch (NotIntegerException e) {
            try {
                Variable v1=parameter[0].variableValue();
                Variable v2=parameter[1].variableValue();
                return JSCLInteger.valueOf(compare(v1 instanceof DoubleVariable && v2 instanceof DoubleVariable)?1:0);
            } catch (NotVariableException e2) {
                return JSCLInteger.valueOf(compare(false)?1:0);
            }
        }
    }

    boolean compare(boolean numeric) {
        switch(operator) {
            case 0:
                return parameter[0].compareTo(parameter[1])==0;
            case 1:
                return numeric?parameter[0].compareTo(parameter[1])<0:false;
            case 2:
                return numeric?parameter[0].compareTo(parameter[1])>0:false;
            case 3:
                return parameter[0].compareTo(parameter[1])!=0;
            case 4:
                return numeric?parameter[0].compareTo(parameter[1])<=0:false;
            case 5:
                return numeric?parameter[0].compareTo(parameter[1])>=0:false;
            case 6:
                return parameter[0].compareTo(parameter[1])==0;
            default:
                return false;
        }
    }

    public Generic evalelem() {
        return evaluate();
    }

    public Generic evalsimp() {
        return evaluate();
    }

    public Generic evalnum() {
        return new NumericWrapper((JSCLInteger)evaluate());
    }

    public String toJava() {
        StringBuffer buffer=new StringBuffer();
        buffer.append(parameter[0].toJava()).append(easj[operator]).append(parameter[1].toJava());
        return buffer.toString();
    }

//    public void toMathML(Element element, Object data) {
//        CoreDocumentImpl document=(CoreDocumentImpl)element.getOwnerDocument();
//        parameter[0].toMathML(element,null);
//        Element e1=new ElementImpl(document,"mo");
//        e1.appendChild(new TextImpl(document,easm[operator]));
//        element.appendChild(e1);
//        parameter[1].toMathML(element,null);
//    }

    protected Variable newinstance() {
        return new Comparison(name,null,null);
    }

    private static final String eass[]={"=","<=",">=","<>","<",">","~"};
    private static final String easj[]={"==","<=",">=","!=","<",">","=="};
    private static final String easm[]={"=","\u2264","\u2265","\u2260","<",">","\u2248"};
    private static final String easo[]={"eq","le","ge","ne","lt","gt","ap"};
}
