package abs.ixi.server.common;

import java.io.Serializable;

public class Pair<FIRST, SECOND> implements Serializable {
    private static final long serialVersionUID = 6885535307435002848L;
    
    private FIRST first;
    private SECOND second;

    public Pair(FIRST first, SECOND second) {
	this.first = first;
	this.second = second;
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

    @Override
    public String toString() {
	return "{" + first + ":" + second + "}";
    }

    @Override
    public int hashCode() {
	int hash = 0;

	if (first != null) {
	    hash = first.hashCode();
	}

	if (second != null) {
	    hash = hash * second.hashCode();
	}

	return hash;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
	if (obj == null)
	    return false;
	if (this == obj)
	    return true;

	Pair<Object, Object> pair = (Pair<Object, Object>) obj;

	if (this.first == pair.getFirst()) {
	    if (this.getSecond() == pair.getSecond()) {
		return true;
	    } else {
		if (this.second == null || pair.getSecond() == null)
		    return false;
	    }

	} else {
	    if (this.first == null || pair.getFirst() == null)
		return false;
	}

	return this.first.equals(pair.getFirst()) && this.getSecond().equals(pair.getSecond());
    }

}
