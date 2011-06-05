package org.jfugue;

/**
 * Represents layer changes.  A Layer allows multiple sounds to be played at the same
 * time on a single track (also known as a voice), without those notes being specified
 * as a chord.  This is particularly helpful when sing Track 9, the percussion track,
 * so multiple percussion sounds can occur at the same time.  
 *
 *@author David Koelle
 *@version 3.0
 */
public class Layer implements JFugueElement
{
    private byte layer;

    /**
     * Creates a new Layer object, with the specified layer number.
     * @param layer the number of the layer to use
     */
    public Layer(byte layer)
    {
        this.layer = layer;
    }

    /**
     * Sets the value of the layer for this object.
     * @param layer the number of the layer to use
     */
    public void setLayer(byte layer)
    {
        this.layer = layer;
    }

    /**
     * Returns the layer used in this object
     * @return the layer used in this object
     */
    public byte getLayer()
    {
        return layer;
    }

    /**
     * Returns the Music String representing this element and all of its settings.
     * For a Layer object, the Music String is <code>L</code><i>layer-number</i>
     * @return the Music String for this element
     */
    public String musicString()
    {
        String returnString = "L"+layer;
        return returnString;
    }
}