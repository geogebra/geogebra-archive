package com.heatonresearch.OCR;
import java.awt.*;

/**
 * Java Neural Network Example Handwriting Recognition 
 * Copyright 2005 by Heaton Research, Inc. 
 * by Jeff Heaton (http://www.heatonresearch.com) 10-2005
 * ------------------------------------------------- 
 * This source code is copyrighted.
 * You may reuse this code in your own compiled projects. 
 * However, if you would like to redistribute this source code
 * in any form, you must obtain permission from Heaton Research. 
 * (support@heatonresearch.com). 
 * ------------------------------------------------- 
 * 
 * This class displays a character in a "downsampled" form.
 * 
 * ------------------------------------------------- 
 * Want to learn more about Neural Network Programming in Java?
 * Have a look at our e-book:
 * 
 * http://www.heatonresearch.com/articles/series/1/
 *  
 * @author Jeff Heaton (http://www.jeffheaton.com)
 * @version 1.0
 */

public class Sample extends Panel
{

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
/**
   * The image data.
   */
  SampleData data;

  /**
   * The constructor.
   * 
   * @param width
   *          The width of the downsampled image
   * @param height
   *          The height of the downsampled image
   */
  Sample(int width, int height)
  {
    data = new SampleData(' ', width, height);
  }

  /**
   * The image data object.
   * 
   * @return The image data object.
   */
  SampleData getData()
  {
    return data;
  }

  /**
   * Assign a new image data object.
   * 
   * @param data
   *          The image data object.
   */

  void setData(SampleData data)
  {
    this.data = data;
  }

  /**
   * @param g
   *          Display the downsampled image.
   */
  public void paint(Graphics g)
  {
    if (data == null)
      return;

    int x, y;
    int vcell = getBounds().height / data.getHeight();
    int hcell = getBounds().width / data.getWidth();

    g.setColor(Color.white);
    g.fillRect(0, 0, getBounds().width, getBounds().height);

    g.setColor(Color.black);
    for (y = 0; y < data.getHeight(); y++)
      g.drawLine(0, y * vcell, getBounds().width, y * vcell);
    for (x = 0; x < data.getWidth(); x++)
      g.drawLine(x * hcell, 0, x * hcell, getBounds().height);

    for (y = 0; y < data.getHeight(); y++)
    {
      for (x = 0; x < data.getWidth(); x++)
      {
        if (data.getData(x, y))
          g.fillRect(x * hcell, y * vcell, hcell, vcell);
      }
    }

    g.setColor(Color.black);
    g.drawRect(0, 0, getBounds().width - 1, getBounds().height - 1);

  }

}