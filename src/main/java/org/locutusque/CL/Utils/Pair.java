package org.locutusque.CL.Utils;

import org.locutusque.CL.Dynamic;

public class Pair<V1, V2> extends Dynamic {
    private V1 v1;
    private V2 v2;

    public Pair(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getFirst() {
        return v1;
    }

    public V2 getSecond() {
        return v2;
    }

    public void setFirst(V1 v1) {
        this.v1 = v1;
    }

    public void setSecond(V2 v2) {
        this.v2 = v2;
    }
    public void setBoth(Pair<V1, V2> pair) {
        this.v1 = pair.getFirst();
        this.v2 = pair.getSecond();
    }
    public void setBoth(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    public Pair<V1, V2> get() {
        return this;
    }
}
