package com.bridgit.export.export_data;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class DspaceItem {

	private String[] columnHeads = new String[31];
	private int itemId;
	private Map<String,String> DspaceValues = new HashMap <String,String>();
	private static String name = "DspaceItem";
	private static String agePad = " ||";
	private static String oldURL = "oldUrl";
	private static String newURL = "newURL";


	public void setItemId(int itemId){
		this.itemId = itemId;
	}
	public int getItemId(){
		return this.itemId;
	}



	private void initializeColumns(){
		this.columnHeads[0] = "id";
		this.columnHeads[1] = "collection";
		this.columnHeads[2] = "dc.type";
		this.columnHeads[3] = "bridgit-terms.harrasmentType";
		this.columnHeads[4] = "bridgit-terms.targetAge";
		this.columnHeads[5] = "bridgit-terms.targetBias";
		this.columnHeads[6] = "bridgit-terms.targetGender";
		this.columnHeads[7] = "bridgit-terms.targetGrade";
		this.columnHeads[8] = "bridgit-terms.targetGroup";
		this.columnHeads[9] = "dc.contributor.author";
		this.columnHeads[10] = "dc.date.issued";
		this.columnHeads[11] = "dc.description";
		this.columnHeads[12] = "dc.identifier.uri";
		this.columnHeads[13] = "dc.content.source.uri";
		this.columnHeads[14] = "dc.language.iso";
		this.columnHeads[15] = "dc.publisher";
		this.columnHeads[16] = "dc.subject";
		this.columnHeads[17] = "dc.title";
		this.columnHeads[18] = "bridgit-terms.articleType";
		this.columnHeads[19] = "bridgit-terms.bookType";
		this.columnHeads[20] = "bridgit-terms.creativeType";
		this.columnHeads[21] = "bridgit-terms.helpType";
		this.columnHeads[22] = "bridgit-terms.videoType";
		this.columnHeads[23] = "content.image.url";
		this.columnHeads[24] = "content.file.url";
		this.columnHeads[25] = "content.image.main";
		this.columnHeads[26] = "content.image.landing";
		this.columnHeads[27] = "bridgit-terms.hoverText";
		this.columnHeads[28] = "bridgit-terms.licenseFlag";
		this.columnHeads[29] = "bridgit-terms.displayFlag";
		this.columnHeads[30] = "bridgit-terms.commonCoreFlag";


	}
	public DspaceItem(int itemId){
		this.itemId = itemId;
		initializeColumns();
		intitialzeMap(this.columnHeads);
	}

	public DspaceItem(){
		initializeColumns();
		intitialzeMap(this.columnHeads);
	}
	public String[] getColumnHeads(){
		return this.columnHeads;
	}
	public void intitialzeMap(String[] columnHeads){
		for(int i = 0; i < columnHeads.length; i++){
			this.DspaceValues.put(columnHeads[i],"");

		}
	}
	public Map<String,String> populateRow(Connection con, int itemId){
		Map	<String,String> m = new HashMap<String,String>();
		m.put("id", String.valueOf(itemId));
		m.put("collection", getCollectionData(con, itemId));
		m.put("dc.type", getFieldData(con, itemId, 66));
		m.put("bridgit-terms.harrasmentType", getFieldData(con, itemId, 130));
		m.put("bridgit-terms.targetAge", addPad(getFieldData(con, itemId, 144)));
		m.put("bridgit-terms.targetBias", getFieldData(con, itemId, 145));
		m.put("bridgit-terms.targetGender", getFieldData(con, itemId, 143));
		m.put("bridgit-terms.targetGrade", getFieldData(con, itemId, 146));
		m.put("bridgit-terms.targetGroup", getFieldData(con, itemId, 142));
		m.put("dc.contributor.author", getFieldData(con, itemId, 3));
		m.put("dc.date.issued", getFieldData(con, itemId, 15));
		//m.put("dc.description", encapsulateString(getItemDescription(con, itemId)));
		m.put("dc.description",getItemDescription(con, itemId));
		String allURIs = getFieldData(con, itemId, 25);
		m.put("dc.identifier.uri", correctHandle(isItemURI(allURIs)));
		m.put("dc.content.source.uri", correctHandle(isExternalURI(allURIs)));
		m.put("dc.language.iso",getFieldData(con, itemId, 38));
		m.put("dc.publisher",getFieldData(con, itemId, 39));
		//m.put("dc.subject",getFieldData(con, itemId, 116));
		m.put("dc.subject",getFieldData(con, itemId, 57));
		m.put("dc.title",getFieldData(con, itemId, 64));
		m.put("bridgit-terms.articleType",getFieldData(con, itemId, 127));
		m.put("bridgit-terms.bookType",getFieldData(con, itemId, 128));
		m.put("bridgit-terms.creativeType",getFieldData(con, itemId, 135));
		m.put("bridgit-terms.helpType",getFieldData(con, itemId, 136));
		m.put("bridgit-terms.videoType",getFieldData(con, itemId, 141));
		m.put("content.image.url",getImageInfo(con, itemId,0));
		m.put("content.file.url",getFileInfo(con, itemId));
		m.put("content.image.main",getImageInfo(con, itemId,1));
		m.put("content.image.landing",getImageInfo(con, itemId,2));
		m.put("bridgit-terms.hoverText",getFieldData(con, itemId, 147));
		m.put("bridgit-terms.licenseFlag",getDefaultValue(getFieldData(con, itemId, 148),148));
		m.put("bridgit-terms.displayFlag",getDefaultValue(getFieldData(con, itemId, 149),149));
		m.put("bridgit-terms.commonCoreFlag",getDefaultValue(getFieldData(con, itemId, 150),150));



		return m;
	}


	public ResultSet getCollectionItems(Connection con, int collectionID){
    	ResultSet rs = null;
    	String query = "select item_id from item where owning_collection = ?";

    	try{
    		PreparedStatement getItems = con.prepareStatement(query);
    		getItems.setInt(1, collectionID);
    		rs = getItems.executeQuery();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
        return rs;

    }

    public ResultSet getItemMetaData(Connection con, int itemID){
	    ResultSet rs = null;
	    String query = "Select mdr.metadata_field_id, mdr.element, mv.text_value from metadatavalue mv, metadatafieldregistry mdr where mv.item_id = ? and mv.metadata_field_id = mdr.metadata_field_id";

	    try{
	    	PreparedStatement getItems = con.prepareStatement(query);
	    	getItems.setInt(1, itemID);
	    	rs = getItems.executeQuery();
		}
		catch (SQLException e) {
				e.printStackTrace();
		}
	    return rs;

    }

    public String replaceLast(String string, String substring, String replacement) {
  	  int index = string.lastIndexOf(substring);
  	  if (index == -1)
  	    return string;
  	  return string.substring(0, index) + replacement
  	          + string.substring(index+substring.length());
  	}

    public String getFieldData(Connection con, int itemID, int metaId){
		ResultSet rs = null;
		String query = "Select mv.text_value from metadatavalue mv, metadatafieldregistry mdr where mv.item_id =  ? and mdr.metadata_field_id = ? and mv.metadata_field_id = mdr.metadata_field_id";
		String result = "";
		String delimiter = " || ";
		try{
			PreparedStatement getMetaField = con.prepareStatement(query);
			getMetaField.setInt(1, itemID);
			getMetaField.setInt(2, metaId);
	    	rs = getMetaField.executeQuery();
	    	if (!rs.next() ) {
			    return result;
			} else {

				StringBuffer sb = new StringBuffer();
				do {
			       sb.append(rs.getString(1));
			       sb.append(delimiter);
			    } while (rs.next());
				result = sb.toString();
			}


		}
		catch (SQLException e) {
			e.printStackTrace();
		}


		return replaceLast(result, "||","").trim();
	}

	public Vector<Integer> getBundleIds(Connection con, int itemID){
		ResultSet rs = null;
		Vector<Integer> ids =  new Vector<Integer>();
		String query = "Select bundle_id  from item2bundle where item_id =  ? order by bundle_id DESC";
		try{

			PreparedStatement getBundleIds = con.prepareStatement(query);

			getBundleIds.setInt(1, itemID);
			rs = getBundleIds.executeQuery();

			while(rs.next()){
				Integer i = new Integer(rs.getInt("bundle_id"));
				ids.add(i);
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return ids;
	}
	public String createParameters(Vector<Integer> v){
		String parameterString = "";
		String question = "?,";
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < v.size(); i++){
			sb.append(question);
		}
		parameterString = sb.toString();
		parameterString = replaceLast(parameterString,",","");

		return parameterString;
	}
	public Vector<Integer> getBitStreamIds(Connection con, Vector<Integer> v){
		String queryStart = "Select bitstream_id from bundle2bitstream where bundle_id in (";
		String parameters = createParameters(v);
		String queryEnd = ")";
		String query = queryStart+parameters+queryEnd;
		ResultSet rs = null;
		Vector<Integer> ids =  new Vector<Integer>();
		try{
			PreparedStatement getBitStreamIds = con.prepareStatement(query);
			for(int i = 0; i < v.size(); i++){
				getBitStreamIds.setInt(i+1,v.get(i).intValue());
			}

			rs = getBitStreamIds.executeQuery();

			while(rs.next()){
				Integer i = new Integer(rs.getInt("bitstream_id"));
				ids.add(i);
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return ids;
	}
	public boolean isImage(String name){
		String lowername = name.toLowerCase();
		int flag = 0;
		String[] imageType = new String[4];
		imageType[0] ="gif";
		imageType[1] ="jpg";
		imageType[2] ="png";
		imageType[3] ="jpeg";

		for(int i = 0; i < imageType.length; i++){
			if(lowername.indexOf(imageType[i]) != -1){
			     flag++;
			}
		}

		return flag > 0;
	}
	public boolean isFile(String name){
		String lowername = name.toLowerCase();
		int flag = 0;
		String[] imageType = new String[4];
		imageType[0] ="pdf";
		imageType[1] ="doc";
		imageType[2] = "mp4";
		imageType[3] = "zip";

		for(int i = 0; i < imageType.length; i++){
			if(lowername.indexOf(imageType[i]) != -1){
			     flag++;
			}
		}

		return flag > 0;
	}

	public String getImageName(Connection con, Vector<Integer> v, int type){
		String fileName = "";
		String queryStart = "select name  from bitstream where bitstream_id in (";
		String parameters = createParameters(v);
		String queryEnd = ")";
		String query = queryStart+parameters+queryEnd;
		ResultSet rs = null;
		Vector<String> filenames =  new Vector<String>();
		try{
			PreparedStatement getFileNames = con.prepareStatement(query);
			for(int i = 0; i < v.size(); i++){
				getFileNames.setInt(i+1,v.get(i).intValue());
			}

			rs = getFileNames.executeQuery();

			while(rs.next()){
			    String file = new String(rs.getString("name"));
				filenames.add(file);
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < filenames.size(); i++){
			if(isImage(filenames.get(i))){

				fileName = filenames.get(i).toLowerCase();
			    if(type == 0){
			    	return filenames.get(i);
			    }
			    if(type == 1){
			    	if(fileName.indexOf("_main") != -1){
			    		return filenames.get(i);
			    	}
			    }
			    if(type == 2){
			    	if(fileName.indexOf("_landing") != -1){
			    		return filenames.get(i);
			    	}
			    }
			    fileName = "";
			}
		}

		return fileName;
 	}

	public String getFileName(Connection con, Vector<Integer> v){
		String fileName = "";
		String queryStart = "select name  from bitstream where bitstream_id in (";
		String parameters = createParameters(v);
		String queryEnd = ")";
		String query = queryStart+parameters+queryEnd;
		ResultSet rs = null;
		Vector<String> filenames =  new Vector<String>();
		try{
			PreparedStatement getFileNames = con.prepareStatement(query);
			for(int i = 0; i < v.size(); i++){
				getFileNames.setInt(i+1,v.get(i).intValue());
			}

			rs = getFileNames.executeQuery();

			while(rs.next()){
			    String file = new String(rs.getString("name"));
				filenames.add(file);
			}

		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		for(int i = 0; i < filenames.size(); i++){
			if(isFile(filenames.get(i))){
				fileName = filenames.get(i);
				return fileName;
			}
		}

		return fileName;
 	}

	public String isItemURI(String url){
		String path = "";

		String[] tokens = url.split("\\|\\|");
		for(int i = 0; i < tokens.length; i ++){


			if(tokens[i].indexOf("handle") != -1){
				path = tokens[i];
				return path;
			}
		}
		return path;
	}
	public String isExternalURI(String url){
		String path = "";

		String[] tokens = url.split("\\|\\|");
		for(int i = 0; i < tokens.length; i ++){


			if(tokens[i].indexOf("handle") == -1){
				path = tokens[i];
				return path;
			}
		}
		return path;
	}



	public String getImageInfo(Connection con, int itemId, int type){
		String completePath = "";
		String separator = "/";
		String bitstream = "/bitstream";
		int metaId = 25;
		String uris = getFieldData(con, itemId,metaId);
		//System.out.println(uris);
		String handle = "";
		if(uris.indexOf("||") != -1){
			 handle = isItemURI(uris);
		}
		else{
			handle = uris;
		}
		if(handle.indexOf("handle") != -1 ){
			String front = handle.substring(0, handle.indexOf("/handle"));
			String back = handle.substring(handle.indexOf("/handle"));
			handle = front+bitstream+back;
		}
		handle = handle.trim();
		String handleNew = correctHandle(handle);
		Vector<Integer> bundleIds = getBundleIds(con, itemId);
		Vector<Integer> bitStreamIds = getBitStreamIds(con, bundleIds);
		String imageName = getImageName(con, bitStreamIds, type);
		completePath = handleNew+separator+imageName;
		if(!isImage(completePath)){
			completePath = "";
		}

		return completePath;
	}
	public String getImageType (String path, int type){
		System.out.println("Main path: "+path);

		String file = "";
		String lowerPath = path.toLowerCase();
		if(type == 0){
			//System.out.println("Main path: "+path);
			return path;

		}
		else if(type == 1){
			if(lowerPath.indexOf("_main") != -1){
				//System.out.println("Popular path: "+lowerPath);
				return path;
			}
		}
		else if(type == 2){
			if(lowerPath.indexOf("_landing") != -1){
				//System.out.println("Detail path: "+lowerPath);
				return path;
			}
		}


		return file;
	}


	public String getFileInfo(Connection con, int itemId){
		String completePath = "";
		String separator = "/";
		String bitstream = "/bitstream";
		int metaId = 25;
		String uris = getFieldData(con, itemId,metaId);
		//System.out.println(uris);
		String handle = "";
		if(uris.indexOf("||") != -1){
			 handle = isItemURI(uris);
		}
		else{
			handle = uris;
		}
		if(handle.indexOf("handle") != -1 ){
			String front = handle.substring(0, handle.indexOf("/handle"));
			String back = handle.substring(handle.indexOf("/handle"));
			handle = front+bitstream+back;
		}
		handle = handle.trim();
		String handleNew =correctHandle(handle);
		Vector<Integer> bundleIds = getBundleIds(con, itemId);
		Vector<Integer> bitStreamIds = getBitStreamIds(con, bundleIds);
		String imageName = getFileName(con, bitStreamIds);

		completePath = handleNew+separator+imageName;

		return completePath;
	}
	public String getCollectionData(Connection con, int itemID){
		ResultSet rs = null;
		String query = "select collection_id from collection2item where item_id = ?";
		String result = "";
		String delimiter = " || ";
		try{
			PreparedStatement getCollectionId = con.prepareStatement(query);
			getCollectionId.setInt(1, itemID);
			rs = getCollectionId.executeQuery();
	    	if (!rs.next() ) {
			    return result;
			} else {

				StringBuffer sb = new StringBuffer();
				do {
			       sb.append(rs.getString(1));
			       sb.append(delimiter);
			    } while (rs.next());
				result = sb.toString();
			}


		}
		catch (SQLException e) {
			e.printStackTrace();
		}


		return replaceLast(result, "||","").trim();
	}

	public static String cleanString(String str){

		str =  str.replaceAll("[\\s&&[^\\n]]+", " ");
	    str = str.replaceAll("(?m)^\\s|\\s$", "");
	    str = str.replaceAll("\\n+", "\n");
	    str = str.replaceAll("^\n|\n$", "");
	   return str;

	}

	public String getItemDescription(Connection con, int itemID){
		String description = "";
		//String place = "||||||";
		String place = " ";
		int descMetaId = 26;
		int abMetaId = 27;
		description = getFieldData(con, itemID, descMetaId);
		if(description.trim().equals("") ){
			description = getFieldData(con, itemID, abMetaId);
		}

		//String remove_lines = description.replaceAll(System.getProperty("line.separsepraator"), "||||||");
		String remove_n = description.replaceAll("\n", place);
		String remove_r = remove_n.replaceAll("\r", place);
		String remove_both = remove_r.replaceAll("\n\r", place);

		String clean = remove_both.replaceAll("\t", " ");

		return clean;

	}

	public byte[] convertColumns(String[] columns){

		byte[] columnBytes;
		String tab = "\t";
		String newLine = "\n";
		StringBuffer sb = new StringBuffer();

		for(int i=0; i < columns.length-1; i++){
			sb.append(columns[i]);
			sb.append(tab);
		}
		sb.append(columns[columns.length-1]);
		sb.append(newLine);

		columnBytes = String.valueOf(sb).getBytes();
		return columnBytes;

	}
	public byte[] convertRowData(Map<String,String> m){
		byte[] rowBytes;
		String tab = "\t";
		String newLine = "\n";
		StringBuilder sb = new StringBuilder();
		sb.append(m.get("id"));
		sb.append(tab);
		sb.append(m.get("collection"));
		sb.append(tab);
		sb.append(m.get("dc.type"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.harrasmentType"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.targetAge"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.targetBias"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.targetGender"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.targetGrade"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.targetGroup"));
		sb.append(tab);
		sb.append(m.get("dc.contributor.author"));
		sb.append(tab);
		sb.append(m.get("dc.date.issued"));
		sb.append(tab);
		sb.append(m.get("dc.description"));
		sb.append(tab);
		sb.append(m.get("dc.identifier.uri"));
		sb.append(tab);
		sb.append(m.get("dc.content.source.uri"));
		sb.append(tab);
		sb.append(m.get("dc.language.iso"));
		sb.append(tab);
		sb.append(m.get("dc.publisher"));
		sb.append(tab);
		sb.append(m.get("dc.subject"));
		sb.append(tab);
		sb.append(m.get("dc.title"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.articleType"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.bookType"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.creativeType"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.helpType"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.videoType"));
		sb.append(tab);
		sb.append(m.get("content.image.url"));
		sb.append(tab);
		sb.append(m.get("content.file.url"));
		sb.append(tab);
		sb.append(m.get("content.image.main"));
		sb.append(tab);
		sb.append(m.get("content.image.landing"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.hoverText"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.licenseFlag"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.displayFlag"));
		sb.append(tab);
		sb.append(m.get("bridgit-terms.commonCoreFlag"));
		sb.append(newLine);
		rowBytes = sb.toString().getBytes();
		return rowBytes;
	}

	public void createFileWithHeader(byte[] columns, String path){
		//String path = "columns.tab";

		BufferedOutputStream bs = null;
		try {

		    FileOutputStream fs = new FileOutputStream(new File(path));
		    bs = new BufferedOutputStream(fs);
		    bs.write(columns);
		    bs.close();
		    bs = null;

		} catch (Exception e) {
		    e.printStackTrace();
		}

		if (bs != null) try { bs.close(); } catch (Exception e) {e.printStackTrace(); }


	}
	public void addRowData(byte[] row, String fileName){

		BufferedOutputStream bs = null;
		try {

		    FileOutputStream fs = new FileOutputStream(new File(fileName),true);
		    bs = new BufferedOutputStream(fs);
		    bs.write(row);
		    bs.close();
		    bs = null;

		} catch (Exception e) {
		    e.printStackTrace();
		}

		if (bs != null) try { bs.close(); } catch (Exception e) {e.printStackTrace(); }



	}
	public static ArrayList <Integer> retrieveItemIds(Connection con, String date){
		//There are two queries here a query that only gets items from a certain time frame and one that gets all the records
		Statement stmt = null;
		ArrayList <Integer> id = new ArrayList <Integer>();
		String queryFront = "select item_id from item where in_archive = \'t\' and owning_collection NOTNULL and last_modified > to_timestamp('";
		String queryBack = "', 'YYYY-MM-DD')";
		String completeQuery = queryFront+date+queryBack;
		String intitialQuery = "select item_id from item where in_archive = \'t\' and owning_collection NOTNULL";
		try{
			stmt = con.createStatement();
			ResultSet rs = null;
			if(date.length() > 1){
				rs = stmt.executeQuery(completeQuery);
			}
			else{
				rs = stmt.executeQuery(intitialQuery);
			}
			while(rs.next()){
				id.add(Integer.valueOf(rs.getInt(1)));
			}
		}

		catch (SQLException e){
			//System.out.println(name+" ::retrieveItemIds+ \n");
			//System.out.println(e.toString());


		}

		return id;

	}
	public static String addPad(String age){

		if(age.indexOf("||") == -1 ){
			age = age.concat(agePad);
		}

		return age;
	}

	public static String encapsulateString(String desc){
		String frontquotes = " \" XXXX ";
		String backquotes = " XXXXX \" ";
		String safe = frontquotes+desc+backquotes;
		return safe;

	}
	public String getDefaultValue(String value, int id ){
		Map<String,String> defaultValues = new HashMap<String,String>();
		defaultValues.put("148", "No");
		defaultValues.put("149", "No");
		defaultValues.put("150", "No");
		String flag = value;
		if(flag.trim().equals("")){
			flag = (String)defaultValues.get(String.valueOf(id));
		}
		return flag;
	}
	public String correctHandle(String handle){

		String correctHandle = handle.replaceAll(oldURL, newURL);
		return correctHandle;
	}



}
