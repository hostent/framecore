package io.framecore.Saas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ICronFaceJob extends IFaceJob {
	
	String getCron();
	
	List<HashMap<String, Object>> getParList();

}
