package io.framecore.Saas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IIntervalFaceJob extends IFaceJob {
	
	Integer getIntervalSeconds();
	Integer getStartAtSeconds();
	List<HashMap<String, Object>> getParList();

}
