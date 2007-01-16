package jscl.math;

class JSCLBoolean extends ModularInteger {
    static final JSCLBoolean ZERO=new JSCLBoolean(0);
    static final JSCLBoolean ONE=new JSCLBoolean(1);

    JSCLBoolean(long content) {
        super(content,2);
    }

    protected ModularInteger newinstance(long content) {
        return content%2==0?ZERO:ONE;
    }
}
