package tutor.net.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;

import tutor.net.util.iface.FileUpload;

public class HttpMultiPartFileUpload implements FileUpload {
	
    public static void main(String[] args) throws Throwable {
    	
    	String url = "http://localhost/test/upload_file.php";
		File file = new File("/home/albert/workspace/geogebraCVS/sqlnet.log");
		
		HttpParam param = new HttpParam();
		param.setName("file");
		param.setValue(file);
		
		List params = new ArrayList();
		params.add(param);

		HttpMultiPartFileUpload mpfu = new HttpMultiPartFileUpload();
		
		mpfu.send(url, params);
    }
    
    public int send(String url, List params) throws Exception {
    	
    	PostMethod filePost = new PostMethod(url);
    	
    	Part[] parts = buildParts(params);
    	
		filePost.setRequestEntity(
				new MultipartRequestEntity(parts, filePost.getParams()));
		
		HttpClient client = new HttpClient();
		int status = client.executeMethod(filePost);
		
		return status;
    }
    
    protected Part[] buildParts(List params) throws Exception {
    	
    	Part[] parts = null;
    	List partsList = new ArrayList();
    	
    	for(Iterator it = params.iterator(); it.hasNext();) {
    		
    		HttpParam param = (HttpParam) it.next();
    		Part part = createPart(param.getName(), param.getValue());
			partsList.add(part);
    	}
    	
    	parts = (Part[]) partsList.toArray(new Part[0]);
    	
    	return parts;
    }
    
    protected Part createPart(String name, Object obj) throws FileNotFoundException {
    	
    	Part part = null;
    	
		if (obj instanceof String) {
			
			String stringPart = (String) obj;
			part = new StringPart(name, stringPart);
		}
		else if (obj instanceof File) {
			
			File filePart = (File) obj;
			try {
				part = new FilePart(name, filePart);
			} catch (FileNotFoundException fnfe) {
				throw new FileNotFoundException(fnfe.getMessage());
			}
		}
		
		return part;
    }
}