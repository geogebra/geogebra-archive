// Copyright 2003, FreeHEP
package org.freehep.graphicsio.raw;

import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;

import org.freehep.util.images.ImageUtilities;

/**
 * 
 * @version $Id: RawImageWriter.java,v 1.1 2008-02-25 21:18:29 murkle Exp $
 */
public class RawImageWriter extends ImageWriter {

    public RawImageWriter(RawImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    public void write(IIOMetadata streamMetadata, IIOImage image,
            ImageWriteParam param) throws IOException {
        if (image == null)
            throw new IllegalArgumentException("image == null");

        if (image.hasRaster())
            throw new UnsupportedOperationException("Cannot write rasters");

        Object output = getOutput();
        if (output == null)
            throw new IllegalStateException("output was not set");

        if (param == null)
            param = getDefaultWriteParam();

        ImageOutputStream ios = (ImageOutputStream) output;
        RenderedImage ri = image.getRenderedImage();

        RawImageWriteParam rawParam = (RawImageWriteParam) param;
        byte[] bytes = ImageUtilities.getBytes(ri, rawParam.getBackground(),
                rawParam.getCode(), rawParam.getPad());
        ios.write(bytes);
    }

    public IIOMetadata convertStreamMetadata(IIOMetadata inData,
            ImageWriteParam param) {
        return null;
    }

    public IIOMetadata convertImageMetadata(IIOMetadata inData,
            ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType,
            ImageWriteParam param) {
        return null;
    }

    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    public ImageWriteParam getDefaultWriteParam() {
        return new RawImageWriteParam(getLocale());
    }
}
