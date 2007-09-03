package tutor;
import geogebra.kernel.Construction;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;


public class Strategy {
	
private  long idstrategy;
private URL url;
private String title;
private long idproblema;


//private String element ;

private LinkedList messages = new LinkedList();
private Construction construction;

public Strategy() {
	super();
	// TODO Auto-generated constructor stub
}
/*public String getElement() {
	return element;
}

public void setElement(String element) {
	this.element = element;
}
*/

public long getIdstrategy() {
	return idstrategy;
}
public void setIdstrategy(long idstrategy) {
	this.idstrategy = idstrategy;
}
public LinkedList getMessages() {
	return messages;
}
public void addMessage(Message message) {
	this.messages.add(message);
}

/**
 * @return the construction
 */
public Construction getConstruction() {
	return construction;
}
/**
 * @param construction the construction to set
 */
public void setConstruction(Construction construction) {
	this.construction = construction;
}
public Message getMessage(long id)
{
	 
	Iterator it = messages.iterator();
	while (it.hasNext())
	{
		Message m = (Message) it.next();
		if (m.getId()==id) return m;		
	}
	return null;
}

/**
 * TODO where is the URL of XML in database
 * @return
 */
public URL getURL ()
{
	return null;
}
/**
 * TODO WRITE SQL SENTENCE
 * @param bdi
 * @param stratgey
 */
public void fillData(DataBaseInterface bdi, String strategy){

	Connection con = bdi.connectURL();
	String query = "select * from est_missatges_estrategies as e, est_missatges as m where " +
			"e.id_missatge= m.id and e.id="+strategy;
	ResultSet rs = bdi.execQuery(con, query);
try {
	while (rs.next())
	{
	// this.element = rs.getString("element");	
	 String message = rs.getString("missatge");
	 Message m = new Message(message);
	 this.messages.add(m);
	}
}
catch (SQLException e){
	System.err.println("ERROR on Query"+e.getMessage());
}

}

/**
 * @return the idproblema
 */
public long getIdproblema() {
	return idproblema;
}

/**
 * @param idproblema the idproblema to set
 */
public void setIdproblema(long idproblema) {
	this.idproblema = idproblema;
}

/**
 * @return the title
 */
public String getTitle() {
	return title;
}

/**
 * @param title the title to set
 */
public void setTitle(String title) {
	this.title = title;
}

/**
 * @return the url
 */
public URL getUrl() {
	return url;
}

/**
 * @param url the url to set
 */
public void setUrl(URL url) {
	this.url = url;
}

}