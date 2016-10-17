package com.example.activitysharedviews;

import java.io.Serializable;

/**
 * Created by caiquetb on 17/10/16.
 */

public class AlphaData implements Serializable {
    private float alphaBy;
    private float alphaTo;

    public AlphaData(float alphaBy, float alphaTo) {
        this.alphaBy = alphaBy;
        this.alphaTo = alphaTo;
    }

    public float getAlphaBy() { return alphaBy; }

    public float getAlphaTo() { return alphaTo; }
}
