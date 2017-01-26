package application;

import java.io.Serializable;

public class DataStores implements Serializable {
	
	
	/**
	 * 
	 */
	
	private String pathRoot;
	private String pathForSelection;
	public DataStores(){
		
	}
	
//Getter and Setter
	
	public String getPathRoot(){
		return this.pathRoot;
	}
	public void setPathRoot(String pathRoot){
		this.pathRoot = pathRoot;
	}
	
	public String getPathForSelection() {
		return pathForSelection;
	}

	public void setPathForSelection(String pathForSelection) {
		this.pathForSelection = pathForSelection;
	}

	public String toString(){
		String s = "";
		s = s+"Path : "+this.pathRoot;
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
