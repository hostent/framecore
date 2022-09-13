package io.framecore.Codebuild;

 
import java.util.List;

public class Entity {
	
	private String name;
	private String nameBig;
	
	private List<Column> cols;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		
		nameBig = name.substring(0, 1).toUpperCase()+name.substring(1, name.length());
	}

	public List<Column> getCols() {
		return cols;
	}

	public void setCols(List<Column> cols) {
		this.cols = cols;
	}


	
	public String getCode()
	{
		String format ="import com.fasterxml.jackson.annotation.JsonProperty;\r\nimport io.framecore.Frame.ViewStore;\r\nimport io.framecore.Orm.Identity;\r\nimport io.framecore.Orm.Table;\r\n\r\n\r\n@Identity\r\n@Table (Name=\"{name}\",Key=\"id\", UniqueKey = \"xxx\")\r\npublic class {nameBig} extends ViewStore  {\r\n\r\n{cols}}";
		
		
		
		
		String colStr = "";
		
		for (Column column : cols) {
			colStr+="\r\n"+column.getCode();
		}
		
		String str = format.replace("{name}", name).replace("{nameBig}", nameBig).replace("{cols}", colStr);
		
		
		return str;
		
		
	}

 
	
/*	private void tt()
	{
		for (String key : cols.keySet()) {
			cols.get(key);
		}
	}*/

}
