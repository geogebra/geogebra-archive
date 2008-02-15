package tutor.net.util.iface;

import java.util.List;

public interface FileUpload {

	public int send(String url, List params) throws Exception;
}
