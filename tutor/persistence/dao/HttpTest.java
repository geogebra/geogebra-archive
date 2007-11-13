package tutor.persistence.dao;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;


public class HttpTest {

    public static Document getDocument() throws DocumentException {
        return DocumentHelper.parseText( 
            "<root> <child id='1'>James</child> </root>"
        );
    }
    
	public static void main(String[] args) throws Throwable {

		/*
		Document doc = getDocument();
		Element e = doc.getRootElement();
		System.out.println(e.elementText("child"));
		*/
		
		URL url = new URL("");
		URLConnection connection = (HttpURLConnection)url.openConnection();
		
	}

}
