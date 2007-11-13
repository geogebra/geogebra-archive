package tutor.persistence.dao.http.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import tutor.net.util.HttpConnection;
import tutor.persistence.dao.http.iface.HttpDao;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

/**
 * 
 * @author albert
 *
 */
public abstract class HttpDaoImpl implements HttpDao {

	/**
	 * 
	 * @param service
	 * @return
	 */
	protected Document invoke(String service) {
		
		Document document  = null;
		try {
			URL url = new URL(service);
			
			HttpConnection conn = new HttpConnection();
			conn.connect(url);
			
			InputStream is = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String s = "";
			while ((s = br.readLine()) != null) {
				sb.append(s.trim());
			}
			
			StringReader sr = new StringReader(sb.toString());
			
			SAXReader reader = new SAXReader();
			document = reader.read(sr);
		}
		catch (Throwable t) {
			t.printStackTrace();
		}
		
		return document;
	}
	
	public Object queryForObject(String serviceName, Map params, Class mapper) {
		
		Object o = null;
		
		Document doc = query(serviceName, params, mapper);
		
		XmlRowMapper mapperInstance = null;
		try {
			mapperInstance = (XmlRowMapper) mapper.newInstance();
			try {
				Element e = doc.getRootElement();
				o = mapperInstance.mapXmlRow(e);
			}
			catch (Throwable t) {t.printStackTrace();}
		}
		catch (IllegalAccessException iae) {iae.printStackTrace();}
		catch (InstantiationException ie) {ie.printStackTrace();}
		
		return o;
	}

	public List queryForList(String serviceName, Map params, Class mapper) {
		
		List l = new ArrayList();
		
		Document doc = query(serviceName, params, mapper);
		
		XmlRowMapper mapperInstance = null;
		try {
			mapperInstance = (XmlRowMapper) mapper.newInstance();
			try {
				Element root = doc.getRootElement();
				for (Iterator it = root.elementIterator("row"); it.hasNext();) {
					Element e = (Element) it.next();
					Object o = mapperInstance.mapXmlRow(e);
					l.add(o);
				}
			}
			catch (Throwable t) {t.printStackTrace();}
		}
		catch (IllegalAccessException iae) {iae.printStackTrace();}
		catch (InstantiationException ie) {ie.printStackTrace();}
		
		return l;
	}
	
	/**
	 * 
	 * @param serviceName
	 * @param params
	 * @param mapper
	 * @return
	 */
	private Document query(String serviceName, Map params, Class mapper) {
	
		String serviceUrl = serviceName + "?" + resolveParams(params);
		Document doc = invoke(serviceUrl);
		
		return doc;
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	private String resolveParams(Map params) {
		
		String paramUrl = "";
		if (params != null) {
			Set ks = params.keySet();
			for (Iterator it = ks.iterator(); it.hasNext();) {
				String param = (String) it.next();
				paramUrl += param + "=" + params.get(param) + "&";
			}
			paramUrl = paramUrl.substring(0, paramUrl.length()-1);
		}
		
		return paramUrl;
	}
}
