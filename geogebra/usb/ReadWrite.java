package geogebra.usb;

/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */


import geogebra.main.Application;
import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;

/**
 * Demo class to demonstrate simple read and write operations to an USB device.<br>
 * 
 * @author schlaepfer
 * 
 */
public class ReadWrite {

  private static void logData(byte[] data) {
    System.out.print("Data: ");
    for (int i = 0; i < data.length; i++) {
    	char c = (char) data[i];
    	System.out.print(c);
    }
    System.out.println('\n');
    for (int i = 0; i < data.length; i++) {
    	char c = (char) data[i];
      System.out.print(" 0x" + Integer.toHexString(data[i] & 0xff));
    }
    System.out.println('\n');
  }

  public static void main(String[] args) {
    // get a device instance with vendor id and product id
    Device dev = USB.getDevice((short) 0x0403, (short) 0xFE5B);
    try {
        // check what's connected to port 1, returns 0x25 bytes
        byte[] check1 = new byte[] { 0x41, 0x4a, (byte) 0xff, 0x00, 0x00, 0x00, (byte) 0x8A };
        // check what's connected to port 2
        byte[] check2 = new byte[] { 0x41, 0x4a, (byte) 0xff, 0x00, 0x00, 0x00, (byte) 0x8B };
        // check what's connected to port 3
        byte[] check3 = new byte[] { 0x41, 0x4a, (byte) 0xff, 0x00, 0x00, 0x00, (byte) 0x8C };
        
        byte[] test = new byte[] { 0x41, 0x41, 0x00, 0x00, 00, 00, 00, (byte) 0x82 };
        
      // data read from the device
      byte[] readData = new byte[0x25];
      
      for (int i = 0 ; i < readData.length ; i++) {
    	  readData[0] = 0;
      }
      
      // http://da.vidr.cc/2010/07/17/reverse-engineering-the-brother-mfc-7400c/
      // http://comments.gmane.org/gmane.comp.lib.libusb.devel.windows/2616
      //http://svn.chrilly.net/java/USBbridge/src/net/chrilly/java/usbbridge/USBreadThread.java
      //NUM_BYTES_READ = libusb_control_transfer(DEVICE_HANDLE,
    //	        LIBUSB_ENDPOINT_IN |
    //	        LIBUSB_REQUEST_TYPE_VENDOR |
    //	        LIBUSB_RECIPIENT_DEVICE,
    //	        0x01, 0x02, 0x00, BUFFER, 0xff, TIMEOUT);
    	/* BUFFER = { 0x05, 0x10, 0x01, 0x02, 0x00 } */
      

      // open the device with configuration 1, interface 0 and without
      // altinterface
      // this will initialise Libusb for you
      dev.open(1, 0, -1);
      
      byte[] nb = new byte[8];
      
      Application.debug(dev.getMaxPacketSize());
      
      int bytes = dev.controlMsg(USB.REQ_TYPE_DIR_DEVICE_TO_HOST | USB.REQ_TYPE_TYPE_VENDOR
				| USB.REQ_TYPE_RECIP_DEVICE, 0, 0, 0, nb, nb.length, 1000, false);
      
      System.out.println("bytes="+bytes);
      logData(nb);
      
      byte[] buff2 = new byte[2];

      
      // write some data to the device
      // 0x03 is the endpoint address of the OUT endpoint 3 (from PC to
      // device)
      //dev.writeBulk(0x02, test, test.length, 2000, false);
      // read some data from the device
      // 0x84 is the endpoint address of the IN endpoint 4 (from PC to
      // device)
      // bit 7 (0x80) is set in case of an IN endpoint
      bytes = dev.readBulk(0x81, buff2, buff2.length, 2000, false);
      System.out.println("bytes="+bytes);
      // log the data from the device
      logData(buff2);
      // close the device
      dev.close();
    } catch (USBException e) {
      // if an exception occures during connect or read/write an exception
      // is thrown
      e.printStackTrace();
    }
  }
}
