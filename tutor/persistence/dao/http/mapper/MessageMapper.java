package tutor.persistence.dao.http.mapper;

import org.dom4j.Element;

import tutor.model.Message;
import tutor.persistence.dao.http.mapper.iface.XmlRowMapper;

public class MessageMapper implements XmlRowMapper {

	public Object mapXmlRow(Element xmlrow) throws Throwable {

		Element row = xmlrow;
		
		String id = row.elementText("id");
		String messageText = row.elementText("missatge");
		String teacherId = row.elementText("id_professor");
		String typeId = row.elementText("id_tipus");
		
		Message message = new Message();
		message.setId(new Long(id));
		message.setMessage(messageText);
		message.setMessageTypeId(new Long(typeId));
		message.setTeacherId(new Long(teacherId));
		
		return message;
	}

}
