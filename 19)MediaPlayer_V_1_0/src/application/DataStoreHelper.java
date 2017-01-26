package application;

import javafx.stage.Stage;

public final class DataStoreHelper {

	private static NewInstance ref;
	private static String PROJ_NAME = "MediaPlayer";
	private static String history = "History";
	private static String playList = "Playlist";
	private static String pathForSelection = System.getProperty("user.home")+"/Desktop";
	public static DataStores populateDataStore(NewInstance ps){
		ref = ps;
		DataStores ds = new DataStores();
		ds.setPathRoot("C:\\"+PROJ_NAME);
		ds.setPathForSelection(pathForSelection);
		//TODO - Logic for Populating Data store
		
		return ds;
	}
	public static DataStores updateDataStores(NewInstance ps){
		ref = ps;
		
		DataStores ds = new DataStores();
		
		//TODO - logic for updating data store
		
		return ds;
		
	}
	
}
