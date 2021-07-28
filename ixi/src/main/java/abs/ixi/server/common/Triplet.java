package abs.ixi.server.common;

import java.io.Serializable;

public class Triplet<FIRST, SECOND, THIRD> implements Serializable {
    private static final long serialVersionUID = 5264414251076740298L;

    private FIRST first;
    private SECOND second;
    private THIRD third;

    public Triplet(FIRST first, SECOND second, THIRD third) {
	this.first = first;
	this.second = second;
	this.third = third;
    }

    public FIRST getFirst() {
	return first;
    }

    public void setFirst(FIRST first) {
	this.first = first;
    }

    public SECOND getSecond() {
	return second;
    }

    public void setSecond(SECOND second) {
	this.second = second;
    }

    public THIRD getThird() {
	return third;
    }

    public void setThird(THIRD third) {
	this.third = third;
    }

    @Override
    public String toString() {
	return "{" + first + ":" + second + ":" + third + "}";
    }
}
