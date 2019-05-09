package com.bridgit.student_matriculation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Date;
import java.util.Random;
import java.util.UUID;










import java.util.Set;
import java.util.StringTokenizer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;



import java.sql.*;








public class UserHandler {


	private static HashMap<String, Row> badRecords = new HashMap<String, Row>();



	private static ArrayList<Row> goodRecords = new ArrayList<Row>();
	private String fileName = null;
	private static String errorFile = "C:\\User-matriculation\\error.xlsx";
	private static String nameFile = "C:\\User-matriculation\\nameList.xlsx";
	private static String sq = "'";
	private static String comma = ",";

	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return this.fileName;
	}

	/**
	* This method opens an Excel File extracts the information and returns a HashMap
	* of good and bad records
	**/


	public static HashMap<String, Object> createUserRecords(String filename){
		HashMap<String, Object> records = new HashMap<String, Object>();

		try {

		    FileInputStream file = new FileInputStream(new File(filename));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	String error = validateRow(row);
		        	if(error.length() > 0){
		        		String key = error+String.valueOf(row.getRowNum());
		        		badRecords.put(key, row);
		        	}
		        	else{
		        		goodRecords.add(row);
		        	}

		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		records.put("badRecords", badRecords);
		records.put("goodRecords", goodRecords);

		return records;


	}
	// This function validates each row of a spreadsheet to make sure that it is valid.
	public static String validateRow(Row r){
		StringBuilder sb = new StringBuilder();

		String fname_error = "Missing First Name ";
		String lname_error = "Missing Last Name ";
		String email_missing_error = "Email Missing ";
		String b_date_error = "Birthday Error ";
		String gender_error = "Gender Error ";
		String grade_error = "Grade Error";
		String parent_error = "Parent Error";
		String phone_error = "Invalid Phone";


		Cell fname = r.getCell(0);
		if(fname == null || fname.getCellType() == Cell.CELL_TYPE_BLANK){

			sb.append(fname_error);
			System.out.println("First name missing");
		}
		Cell lname = r.getCell(2);
		if(lname == null || lname.getCellType() == Cell.CELL_TYPE_BLANK){

			sb.append(lname_error);
			System.out.println("Last name missing");
		}
		Cell email = r.getCell(3);
		if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK){
			String emailVal  = email.getStringCellValue();


			if(emailVal.trim().length() != 0){
				EmailValidator ev = EmailValidator.getInstance();
				boolean emailTest = ev.isValid(emailVal);
				if(!emailTest){

				sb.append(email_missing_error);
				System.out.println("Email is invalid");
				}
			}
		}
		Cell bdate = r.getCell(4);
		if(bdate == null || bdate.getCellType() == Cell.CELL_TYPE_BLANK){

			sb.append(b_date_error);
			System.out.println("Birthday is missing");
		}
		else{
			if(!validateDate(bdate.getStringCellValue())){
				sb.append(b_date_error);
				System.out.println("Birthday is not valid");
			}
		}
		Cell gender = r.getCell(5);
		if(gender == null || gender.getCellType() == Cell.CELL_TYPE_BLANK){

			sb.append(gender_error);
			System.out.println("Gender is missing");
		}
		else{
			if(!isNumericValue(gender)){
				sb.append(gender_error);
				System.out.println("Gender is invalid");
			}
		}
		Cell grade = r.getCell(6);
		if(grade == null || grade.getCellType() == Cell.CELL_TYPE_BLANK){

			sb.append(grade_error);
			System.out.println("Grade is missing");
		}
		else{
			if(!isNumericValue(grade)){
				sb.append(grade_error);

			}
		}
		if(checkForParent(r)){
			if(!validateParent(r)){
				sb.append(parent_error);
			}
		}

		return sb.toString();
	}


	public static boolean validateDate(String date){

		String format = "yyyy-MM-dd";
		boolean flag = true;
		try {
		      SimpleDateFormat sdf = new SimpleDateFormat(format);
		      sdf.setLenient(false);
		      sdf.parse(date);
		    }
		    catch (ParseException e) {
		      flag = false;
		    }
		    catch (IllegalArgumentException e) {
		      flag = false;
		    }

		return flag;
	}

	public static boolean isRowEmpty(Row row) {
	    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
	        Cell cell = row.getCell(c);
	        if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK)
	            return false;
	    }
	    return true;
	}

	public static boolean isNumericValue(Cell cell){
		boolean flag = false;
		if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){
			flag = true;
		}else{
			if (cell.getCellType() != Cell.CELL_TYPE_STRING){
			  flag = false;
			}
			else{
			  flag = StringUtils.isNumeric(cell.getStringCellValue());
			}
		}

		return flag;
	}
	//This function checks to see if the optional parent information is valid
	public static boolean checkForParent(Row row){
		int flag = 0;
		Cell cFname = row.getCell(15);
		Cell cLname = row.getCell(17);
		Cell cFname1 = row.getCell(22);
		Cell cLname1 = row.getCell(24);
		if(cFname != null && cFname.getCellType() != Cell.CELL_TYPE_BLANK){
			flag++;
		}
		if(cLname != null && cLname.getCellType() != Cell.CELL_TYPE_BLANK){
			flag++;
		}
		if(cFname1 != null && cFname1.getCellType() != Cell.CELL_TYPE_BLANK){
			flag++;
		}
		if(cLname1 != null && cLname1.getCellType() != Cell.CELL_TYPE_BLANK){
			flag++;
		}

		return flag > 0;
	}

	public static boolean validateParent(Row row){
		int flag = 0;
		Cell cFname = row.getCell(15);
		Cell cLname = row.getCell(17);
		Cell cFname1 = row.getCell(22);
		Cell cLname1 = row.getCell(24);
		EmailValidator ev = EmailValidator.getInstance();

		if(checkPairs(cFname,cLname)){
			Cell email1 = row.getCell(18);
			Cell phone1 = row.getCell(19);
			if(!checkForOne(email1,phone1)){
				flag++;
			}
			else{

				if(email1 != null && email1.getCellType() != Cell.CELL_TYPE_BLANK ){
					String emailVal = email1.getStringCellValue();
						if(!ev.isValid(emailVal)){
							flag++;
						}
				}
				if(phone1 != null && phone1.getCellType() != Cell.CELL_TYPE_BLANK ){
					if(!checkPhone(phone1)){
						flag++;
					}

				}
			}

		}
		else{
			if(checkPairs(cFname1,cLname1)){
				Cell email2 = row.getCell(25);
				Cell phone2 = row.getCell(26);
				if(!checkForOne(email2,phone2)){
					flag++;
				}
				else{
					if(email2 != null && email2.getCellType() != Cell.CELL_TYPE_BLANK ){
						String emailVal = email2.getStringCellValue();
							if(!ev.isValid(emailVal)){
								flag++;
							}
					}
					if(phone2 != null && phone2.getCellType() != Cell.CELL_TYPE_BLANK ){
						if(!checkPhone(phone2)){
							flag++;
						}
					}
				}

			}
			else{
				flag++;

			}
		}


		return flag  != 0;

	}
	public static boolean checkPairs(Cell c1, Cell c2){
		boolean flag = true;
		if( (c1 == null || c1.getCellType() == Cell.CELL_TYPE_BLANK) ||
				(c2 == null || c2.getCellType() == Cell.CELL_TYPE_BLANK))
		{
			flag = false;
		}
		return flag;
	}
	public static boolean checkForOne(Cell c1, Cell c2){
		boolean flag = true;
		if(c1 == null || c1.getCellType() == Cell.CELL_TYPE_BLANK ){
			if(c2 == null || c2.getCellType() == Cell.CELL_TYPE_BLANK ){
				flag = false;
			}
		}
		if(c2 == null || c2.getCellType() == Cell.CELL_TYPE_BLANK ){
			if(c1 == null || c1.getCellType() == Cell.CELL_TYPE_BLANK ){
				flag = false;
			}
		}

		return flag;

	}
	//validates phone
	public static boolean checkPhone(Cell c){
		boolean flag = true;

		String test = "";
		c.setCellType(Cell.CELL_TYPE_STRING);

		test = c.getStringCellValue();

		if(test.length() != 11){
			flag = false;
		}
		if(!isNumericValue(c)){
			flag = false;
		}

		return flag;
	}
	//Generates error report
	public static void createErrorReport(HashMap<String, Row> hm, String filename){

		XSSFWorkbook workbook = new XSSFWorkbook ();
		XSSFSheet sheet = workbook.createSheet("Error Sheet");
		Row header = sheet.createRow(0);
		ArrayList<String> headers = createErrorReportHeaders();
		for(int i = 0; i < headers.size(); i++ ){
			Cell c = header.createCell(i);
			c.setCellValue((String)headers.get(i));
		}
		Iterator <String> keys = hm.keySet().iterator();
		int rowNum = 1;
		while(keys.hasNext()){
			String error = (String)keys.next();
			Row data = sheet.createRow(rowNum);
			Cell errorValue = data.createCell(0);
			errorValue.setCellValue(error);
			Row r = (Row)hm.get(error);
			Iterator<Cell> cellIterator = r.cellIterator();
			while(cellIterator.hasNext()) {
				Cell org = cellIterator.next();
				int ci = org.getColumnIndex();
				Cell copy = data.createCell(ci+1);
				switch(org.getCellType()) {
                case Cell.CELL_TYPE_BOOLEAN:
                    copy.setCellValue(org.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    copy.setCellValue(org.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_STRING:
                    copy.setCellValue(org.getStringCellValue());
                    break;
            }





			}

			rowNum++;


		}




		try {
		    FileOutputStream out =
		            new FileOutputStream(new File(filename));
		    workbook.write(out);
		    out.close();
		    System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}





	}
	//Generates Student letter file
	public static void createStudentLetterFile(ArrayList<User> users, String fileName){

		BufferedOutputStream bs = null;
		StringBuilder sb = new StringBuilder();
		String fname = "first_name";
		String lname = "last_name";
		String UUID = "UUID";
		String uuid_code = "UUID_Code";
		String username = "UserName";
		String t = "\t";
		String nl = "\n";
		sb.append(fname);
		sb.append(t);
		sb.append(lname);
		sb.append(t);
		sb.append(UUID);
		sb.append(t);
		sb.append(uuid_code);
		sb.append(t);
		sb.append(username);
		sb.append(nl);

			try {

				FileOutputStream fs = new FileOutputStream(new File(fileName),true);
				bs = new BufferedOutputStream(fs);
				bs.write(sb.toString().getBytes());
				//bs.write(row);

				for(int i= 0; i < users.size(); i++){
				   User u = users.get(i);
				   StringBuilder sb2 = new StringBuilder();
				   String ufname = u.getfName();
				   String ulname = u.getlName();
				   String u_uuid_code = u.getUuid_code();
				   String uname = u.getUserName();
				   sb2.append(ufname);
				   sb2.append(t);
				   sb2.append(ulname);
				   sb2.append(t);
				   String studentInfo = createUserString(u);
				   HashMap<String,Object> hm = createUUID(studentInfo);
				   UUID uuid = (UUID)hm.get("uuid");
				   sb2.append(uuid.toString());
				   sb2.append(t);
				   //String u_uuid_code = uuid.toString().substring(0,6);
				   //System.out.println("u_uuid_code: "+u_uuid_code+"\n");

				   sb2.append(u_uuid_code);
				   sb2.append(t);
				   sb2.append(uname);
				   sb2.append(nl);
				   bs.write(sb2.toString().getBytes());

				}

				bs.close();
				bs = null;

			} catch (Exception e) {
				e.printStackTrace();
			}


		if (bs != null) try { bs.close(); } catch (Exception e) {e.printStackTrace(); }



	}
	//Creates the header for error report
	public static ArrayList<String> createErrorReportHeaders(){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(0, "Error Message");
		headers.add(1, "First Name");
		headers.add(2, "Middle Name");
		headers.add(3, "Last Name");
		headers.add(4, "Email");
		headers.add(5, "Birthday");
		headers.add(6, "Gender");
		headers.add(7, "Grade");
		headers.add(8, "Address Line 1");
		headers.add(9, "Address Line 2");
		headers.add(10, "Address Line 3");
		headers.add(11, "City");
		headers.add(12, "State");
		headers.add(13, "Zip");
		headers.add(14, "Mobile Phone Number");
		headers.add(15, "Home Phone Number");
		headers.add(16, "Parent 1 First Name");
		headers.add(17, "Parent 1 Middle Name");
		headers.add(18, "Parent 1 Last Name");
		headers.add(19, "Parent 1 Email");
		headers.add(20, "Parent 1 Mobile Phone Number");
		headers.add(21, "Parent 1 Home Phone Number");
		headers.add(22, "Parent 1 Work Phone Number");
		headers.add(23, "Parent 2 First Name");
		headers.add(24, "Parent 2 Middle Name");
		headers.add(25, "Parent 2 Last Name");
		headers.add(26, "Parent 2 Email");
		headers.add(27, "Parent 2 Mobile Phone Number");
		headers.add(28, "Parent 2 Home Phone Number");
		headers.add(29, "Parent 2 Work Phone Number");
		headers.add(30, "Organization ID");
		headers.add(31, "UserName");



		return headers;


	}
	//Creates user info header for student letters
	public static ArrayList<String> createUserInfoHeaders(){
		ArrayList <String> headers = new ArrayList<String>();
		headers.add(0,"First Name");
		headers.add(1, "Last Name");
		headers.add(2, "User Name");
		headers.add(3, "Grade");
		headers.add(4, "Gender");
		headers.add(5, "Birthday");
		return headers;
	}

	//Creates the header for the new imporet template
	public static ArrayList<String> createNewTemplateHeaders(){
		ArrayList<String> headers = new ArrayList<String>();

		headers.add(0, "UserName");
		headers.add(1, "First Name");
		headers.add(2, "Middle Name");
		headers.add(3, "Last Name");
		headers.add(4, "Email");
		headers.add(5, "Birthday");
		headers.add(6, "Gender");
		headers.add(7, "Grade");
		headers.add(8, "Class");
		headers.add(9, "Address Line 1");
		headers.add(10, "Address Line 2");
		headers.add(11, "Address Line 3");
		headers.add(12, "City");
		headers.add(13, "State");
		headers.add(14, "Zip");
		headers.add(15, "Mobile Phone Number");
		headers.add(16, "Home Phone Number");
		headers.add(17, "Parent 1 First Name");
		headers.add(18, "Parent 1 Middle Name");
		headers.add(19, "Parent 1 Last Name");
		headers.add(20, "Parent 1 Email");
		headers.add(21, "Parent 1 Mobile Phone Number");
		headers.add(22, "Parent 1 Home Phone Number");
		headers.add(23, "Parent 1 Work Phone Number");
		headers.add(24, "Parent 2 First Name");
		headers.add(25, "Parent 2 Middle Name");
		headers.add(26, "Parent 2 Last Name");
		headers.add(27, "Parent 2 Email");
		headers.add(28, "Parent 2 Mobile Phone Number");
		headers.add(29, "Parent 2 Home Phone Number");
		headers.add(30, "Parent 2 Work Phone Number");
		//headers.add(30, "Organization ID");

		//headers.add(31, "UUID_Code");



		return headers;


	}
	 //Creates Template header
	public static ArrayList<String> createClassTemplateHeaders(){
		ArrayList<String> headers = new ArrayList<String>();

		headers.add(0, "UserName");
		headers.add(1, "First Name");
		headers.add(2, "Middle Name");
		headers.add(3, "Last Name");
		headers.add(4, "Class Room");



		return headers;


	}
	// Missing info header
	public static ArrayList<String> createMissingHeaders(){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(0,"First Name");
		headers.add(1,"Last Name");
		headers.add(2,"Email");
		headers.add(3,"Username");
		headers.add(4,"Grade");

		return headers;

	}
	//Test method
	public static ArrayList<String> createTestNameHeaders(){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(0,"First Name");
		headers.add(1,"School Value");
		headers.add(2,"Bridgit Value");

		return headers;

	}
	//Creates the header for the staff import file
	public static ArrayList<String> createStaffHeaders(){
		ArrayList<String> headers = new ArrayList<String>();
		headers.add(0, "First Name");
		headers.add(1, "Middle Name");
		headers.add(2, "Last Name");
		headers.add(3, "Email");
		headers.add(4, "Home Phone Number");
		headers.add(5, "Work Phone Number");
		headers.add(6, "Mobile Phone Number");
		headers.add(7, "Grade");
		headers.add(8, "Staff Type");

		return headers;
	}

	//Checks to see if a record exists
	public static boolean checkRecord(Connection con, String table, String parameter, String field){
		boolean flag = true;
		StringBuilder sb = new StringBuilder();


			String front = "select fname from ";
			sb.append(front);
			sb.append(table);
			String where = " where ";
			sb.append(where);
			sb.append(field);
			String clause1 = " = '";
			sb.append(clause1);
			sb.append(parameter);
			sb.append("'");




		String query = sb.toString();
		System.out.println("Check Query: "+query);

		try{
			ResultSet rs = null;
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			if(!rs.next()){
				flag = false;
			}

		}
		catch(SQLException e){
			e.printStackTrace();

		}
		catch(Exception e){
			e.printStackTrace();

		}

		return flag;
	}


	//Creates a new user to add to system
	public static ArrayList<User> createUsers(ArrayList<Row> row){


		ArrayList<User> Users = new ArrayList<User>();
		for(int i = 0; i < row.size(); i++){
			User s = new User();
			//System.out.println("Test aa");
			Row r = (Row)row.get(i);

			//System.out.println("Test bb");
			String fname = getStringValue(r,0);
			s.setfName(fname);
			String mname = getStringValue(r,1);
			s.setmName(mname);
			String lname = getStringValue(r,2);
			s.setlName(lname);
			String email = getStringValue(r,3);
			s.setEmail(email);
			String bday = getStringValue(r,4);
			s.setBirthday(bday);


			//Double genderD = r.getCell(5).getNumericCellValue();
			//s.setGender(genderD.intValue());

			String genderS = getStringValue(r,5);

			s.setGender(Integer.parseInt(genderS));

			String gradeS = getStringValue(r,6);

			s.setGrade(Integer.parseInt(gradeS));

			String addr1 = getStringValue(r,7);
			s.setAddrLn1(addr1);
			String addr2 = getStringValue(r,8);
			s.setAddrLn2(addr2);
			String addr3 = getStringValue(r,9);
			s.setAddrLn3(addr3);
			String city = getStringValue(r,10);
			s.setCity(city);
			String state = getStringValue(r,11);
			s.setState(state);
			String zip = getStringValue(r,12);
			s.setZip(zip);
			String mobile = getStringValue(r,13);
			s.setMobilePhone(mobile);
			String home = getStringValue(r,14);
			s.setHomePhone(home);
			Double orgD = r.getCell(29).getNumericCellValue();
			s.setOrg(orgD.intValue());
			String uname = getStringValue(r,30);
			s.setUserName(uname);
			String uuid_code = getStringValue(r,31);
			s.setUuid_code(uuid_code);


			Users.add(s);


		}


		return Users;
	}
	// Converts an Excel cell value into a String
	public static String getStringValue(Row r, int index){
		String blank = "";
		String val = null;
		Cell c = r.getCell(index);

		if(c == null || c.getCellType() == Cell.CELL_TYPE_BLANK){
			val = blank;


		}
		else if(c.getCellType() == Cell.CELL_TYPE_NUMERIC){

			Double d = c.getNumericCellValue();
			int i = d.intValue();
			val = String.valueOf(i);

		}

		else{
			val = c.getStringCellValue();
		}

		return val;

	}
	//Creates a temporary table of students
	public static boolean createTempTable(Connection con, String table_name){
		 StringBuilder sb = new StringBuilder();
		 String create1 = "create table ";
		 sb.append(create1);
		 sb.append(table_name);
		 String create2 = " ( id bigint(20) NOT NULL AUTO_INCREMENT, ";
	     String create3 = "fname varchar(50), mname varchar(50), lname varchar(50), email varchar(100), ";
	     String create4 = "gender int(10),  grade int(10),  primary key (id) )";
	     sb.append(create2);
	     sb.append(create3);
	     sb.append(create4);
	     String query = sb.toString();
	     //System.out.println("Query: "+query);


	     boolean flag = true;
	     try{
	    	 Statement st = con.createStatement();
	    	 st.executeUpdate(query);

			}
			catch(SQLException e){
				e.printStackTrace();
				flag = false;
			}
			catch(Exception e){
				e.printStackTrace();
				flag = false;
			}
			finally{
				/**
				try{

					if(con != null){
						con.close();
					}

				}
				catch(SQLException e){
					e.printStackTrace();
				}
				**/
			}
	     return flag;


	}
 // Adds a student to a temporary table
	public static boolean addUser(Connection con, User s, String table){
		boolean flag = true;
		StringBuilder sb = new StringBuilder();
		String insert1 = "insert into ";
		sb.append(insert1);
		sb.append(table);
		String insert2 = "( fname, mname, lname, email, gender, grade) values ( ";
		sb.append(insert2);
		sb.append(formatString(s.getFName()));
		sb.append(formatString(s.getmName()));
		sb.append(formatString(s.getlName()));
		sb.append(formatString(s.getEmail()));
		sb.append(s.getGender());
		sb.append(", ");
		sb.append(s.getGrade());
		String insert3 = " )";
		sb.append(insert3);

		String query = sb.toString();
		System.out.println(query);

		try{
			Statement st = con.createStatement();
			st.executeUpdate(query);

		}
		catch(SQLException e){
			e.printStackTrace();
			flag = false;
		}
		catch(Exception e){
			e.printStackTrace();
			flag = false;
		}
		finally{
			/**
			try{

				if(con != null){
					con.close();
				}

			}
			catch(SQLException e){
				e.printStackTrace();
			}
			**/
		}



		return flag;

	}
	private static String formatString(String str){
		StringBuilder sb = new StringBuilder();
		String quote = "'";
		String comma = ",";
		sb.append(quote);
		sb.append(str);
		sb.append(quote);
		sb.append(comma);

		return sb.toString();

	}
	//Creates a table name to used for temporarily storing data
	public static String createTableName(int org){
		StringBuilder sb = new StringBuilder();
		SimpleDateFormat df = new SimpleDateFormat("MM_dd_YYYY");
		sb.append("User_Import_");
		Date today = Calendar.getInstance().getTime();
		String reportDate = df.format(today);
		sb.append(reportDate);
		sb.append("_");
		sb.append(org);

		return sb.toString();


	}
	public static void addUserRecords(Connection con, ArrayList<User> Users,
			String table ){
            for(int i = 0; i < Users.size(); i++){
            	User s = Users.get(i);
            	addUser(con, s, table);
            }
	}
	//Gets Prent user's information
	public static ArrayList<User> getPresentUserInfo(Connection con, int org){
		ArrayList<User> Users = new ArrayList<User>();
		StringBuilder sb = new StringBuilder();

		String part1 = "select u.fname, u.mname, u.lname, u.email, up.gender, ou.grade ";
		String part2 = "from user u, user_profile up, organization_user ou ";
		String part3 = "where u.rec_id = up.user_id ";
		String part4 = "and ou.user_id = u.rec_id ";
		String part5 = "and ou.organization_id = ";
		String part6 = " and ou.type = 1";
		sb.append(part1);
		sb.append(part2);
		sb.append(part3);
		sb.append(part4);
		sb.append(part5);
		sb.append(org);
		sb.append(part6);
		String query = sb.toString();
		System.out.println("Query: "+query+"\n");
		try{
			ResultSet rs = null;
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					User s = new User();
					String fname = rs.getString("u.fname");
					String mname = rs.getString("u.mname");
					String lname = rs.getString("u.lname");
					String email = rs.getString("u.email");
					int gender = rs.getInt("up.gender");
					int grade = rs.getInt("ou.grade");
					s.setfName(fname);
					s.setmName(mname);
					s.setlName(lname);
					s.setGrade(grade);
					s.setGender(gender);
					s.setEmail(email);
					Users.add(s);
				}
			}


		}
		catch(SQLException e){
			e.printStackTrace();

		}
		catch(Exception e){
			e.printStackTrace();

		}

		return Users;
	}
	//gets a list of missing users
	public static ArrayList<User> getMissingUsers(Connection con, String table, ArrayList<User> Users){
		ArrayList<User> missing = new ArrayList<User>();

		for(int i = 0; i < Users.size(); i++){
			String parameter = "";
			String field = "";
			User s = Users.get(i);
			if(s.getEmail() != null){
				parameter = "email";
				field = s.getEmail();
			}
			else{
				parameter = "username";
				field = s.getUserName();
			}
			if(!checkRecord(con, table, field, parameter)){
				missing.add(s);
			}

		}

		return missing;
	}
	//creates a spread sheet of missing users
	public static void createMissingReport(ArrayList<User> Users, String filename){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Missing Users");
		Row  header = sheet.createRow(0);
		ArrayList<String> headers = createMissingHeaders();
		for(int i =0; i < headers.size(); i++){
			Cell c = header.createCell(i);
			c.setCellValue(headers.get(i));
		}
		int row = 1;
		for(int i = 0; i < Users.size(); i++){
			User s = Users.get(i);
			Row data = sheet.createRow(row);
			Cell fname = data.createCell(0);
			fname.setCellValue(s.getfName());
			Cell lname = data.createCell(1);
			lname.setCellValue(s.getlName());
			Cell email = data.createCell(2);
			email.setCellValue(s.getEmail());
			Cell username = data.createCell(3);
			username.setCellValue(s.getUserName());
			Cell grade = data.createCell(4);
			grade.setCellValue(s.getGrade());
			row++;

		}
	    sheet.autoSizeColumn(0);
	    sheet.autoSizeColumn(1);
	    sheet.autoSizeColumn(2);
	    sheet.autoSizeColumn(3);
	    sheet.autoSizeColumn(4);
		try{
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}
	//updates the gardes of students at the end of the year
	public static boolean updateGrades(String table, HashMap<String,String> grades, int school, Connection con){
		boolean flag = true;
		StringBuilder sb = new StringBuilder();
	    sb.append("Update ");
	    sb.append(table);
	    sb.append(" set grade = ");
	    sb.append("Case grade ");
	    Iterator<String> keys = grades.keySet().iterator();
	    while(keys.hasNext()){
	    	String key = keys.next();
	    	sb.append("when ");
	    	sb.append("'"+key+"'");
	    	sb.append(" then ");
	    	String value = (String)grades.get(key);
	    	sb.append("'"+value+"' ");
	    }
	    sb.append("End, ");
	    sb.append("date_start = STR_TO_DATE('08-01-2015', '%m-%d-%Y'), ");
	    sb.append("date_end = STR_TO_DATE('07-31-2016', '%m-%d-%Y') ");
	    sb.append("WHERE organization_id = ");
	    sb.append(school);
	    sb.append(" AND type = 1");
	    String query = sb.toString();
	    //System.out.println(query);
	    try{
			Statement st = con.createStatement();
			st.executeUpdate(query);
   	 	}
		catch(SQLException e){
			e.printStackTrace();
			flag = false;
		}
		catch(Exception e){
			e.printStackTrace();
			flag = false;
		}

	 	return flag;


	}
	//Splits First Name and Last name if in the same cell
	public static ArrayList<String> splitName(String name){
		ArrayList <String> names = new ArrayList<String>();
		String token = " ";
		StringTokenizer strTkn = new StringTokenizer(name, token);
		while(strTkn.hasMoreElements()){
			names.add(strTkn.nextToken());
		}

		return names;

	}
	public static String convertDate(String oldFormat, String date){
		String newFormat = "yyyy-MM-dd";

		DateFormat userDateFormat = new SimpleDateFormat(oldFormat);
		DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		Date userDate = null;
		try {
			userDate = userDateFormat.parse(date);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		String convertedDate = dateFormatNeeded.format(userDate);

	    return convertedDate;


	}
	//converts gender m or male = 1 else o
	public static String convertGender(String gender){
		String newGender = "";
		if(gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("male")){
			newGender = "1";
		}

		else if(gender.equalsIgnoreCase("f") || gender.equalsIgnoreCase("female")){
			newGender = "0";
		}
		//System.out.println("New Gender: "+newGender);

		return newGender;

	}
	//converts school file
	public static ArrayList<User> convertMS281File(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(0);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	name = splitName(fname.getStringCellValue());
		            }

		            //System.out.println("Name size: "+name.size()+"\n");
		            s.setlName(name.get(0));
		            s.setfName(name.get(1));
		            if(name.size() > 2){
		            	s.setmName(name.get(2));
		            }

		            //s.setfName(fname.getStringCellValue());
		            Cell uname = row.getCell(1);
		            if(uname != null || uname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setUserName(getStringValue(row,1));
		            }

		            Cell dob = row.getCell(2);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);


		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }

		            Cell gender = row.getCell(3);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(4);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,4);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String uuid_code = RandomStringUtils.randomAlphanumeric(6);
		           s.setUuid_code(uuid_code);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}


	//converts school file
	public static ArrayList<User> convertMS53File(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(2);

		            s.setfName(WordUtils.capitalizeFully(fname.getStringCellValue()));
		            Cell lname = row.getCell(1);
		            s.setlName(WordUtils.capitalizeFully(lname.getStringCellValue()));
		            String uname = createUserName(fname.getStringCellValue(), lname.getStringCellValue());
		            s.setUserName(uname);




		            Cell dob = row.getCell(5);
		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Double d = dob.getNumericCellValue();
		            	int i = d.intValue();
		            	String bday = convertDate(i);
		            	s.setBirthday(bday);
		            }

		            Cell gender = row.getCell(4);
		            if(gender != null && gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(8);
		            if(grade != null && grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,8);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String uuid_code = RandomStringUtils.randomAlphanumeric(6);
		           s.setUuid_code(uuid_code);

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	//converts school file
	public static ArrayList<User> convertAlbertusStudentsFile(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            System.out.println("albertus 1");
		            Cell fname = row.getCell(1);

		            s.setfName(WordUtils.capitalizeFully(fname.getStringCellValue()));
		            Cell lname = row.getCell(0);
		            s.setlName(WordUtils.capitalizeFully(lname.getStringCellValue()));
		            String uname = createUserName(fname.getStringCellValue(), lname.getStringCellValue());
		            s.setUserName(uname);
		            System.out.println("lname "+WordUtils.capitalizeFully(lname.getStringCellValue()));



		            Cell dob = row.getCell(3);
		            System.out.println("albertus 2"+dob);
		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String bday = "";
		            	if(dob.getCellType() == Cell.CELL_TYPE_STRING){
		            		bday = convertStringDate(dob.getStringCellValue(), "/");
		            	}
		            	else{
		            		String format = "YYYY-MM-dd";
			            	SimpleDateFormat sdf = new SimpleDateFormat(format);
			            	Date d = dob.getDateCellValue();
			            	bday = sdf.format(d);
		            	}

		            	s.setBirthday(bday);
		            }

		            Cell gender = row.getCell(2);
		            System.out.println("albertus 3");
		            if(gender != null && gender.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	String newGender = "";
		            	if(gender.getCellType() == Cell.CELL_TYPE_STRING){
		            		newGender = convertGender(gender.getStringCellValue());
		            	}
		            	int val;

		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(4);
		            System.out.println("albertus 4");
		            if(grade != null && grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,4);

		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String uuid_code = RandomStringUtils.randomAlphanumeric(6);
		           s.setUuid_code(uuid_code);

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	//converts school file
	public static ArrayList<User> convertBoodyStudentsFile(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            System.out.println("boody 1");
		            Cell fname = row.getCell(1);


		            s.setfName(WordUtils.capitalizeFully(fname.getStringCellValue()));
		            Cell lname = row.getCell(0);
		            s.setlName(WordUtils.capitalizeFully(lname.getStringCellValue()));

		           String uname = createUserName(fname.getStringCellValue(), lname.getStringCellValue());
		           s.setUserName(uname);
		           System.out.println("Username: "+uname+"\n");

		            Cell dob = row.getCell(3);

		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Double d = dob.getNumericCellValue();
		            	int i = d.intValue();
		            	String bday = convertDate(i);
		            	s.setBirthday(bday);

		            }

		            Cell gender = row.getCell(2);

		            if(gender != null && gender.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	String newGender = "";
		            	if(gender.getCellType() == Cell.CELL_TYPE_STRING){
		            		newGender = convertGender(gender.getStringCellValue());
		            	}
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }

		           Cell email = row.getCell(8);
		           if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		        	   String emailStr = email.getStringCellValue();
		        	   s.setEmail(emailStr);

		           }
		           Cell parentEmail = row.getCell(9);
		           if(parentEmail != null && parentEmail.getCellType() != Cell.CELL_TYPE_BLANK ){
		        	   String pEmailStr = parentEmail.getStringCellValue();
		        	   s.setParent1Email(pEmailStr);
		           }




		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           s.setGrade(6);
		           s.setParent1Fname("Parent");
		           s.setParent1Lname(s.getlName());

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	//converts school file
	public static ArrayList<User> convertAdditionalBoodyStudentsFile(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            System.out.println("boody 1");
		            Cell fname = row.getCell(0);


		            s.setfName(WordUtils.capitalizeFully(fname.getStringCellValue()));
		            Cell lname = row.getCell(1);
		            s.setlName(WordUtils.capitalizeFully(lname.getStringCellValue()));

		           String uname = createUserName(fname.getStringCellValue(), lname.getStringCellValue());
		           s.setUserName(uname);
		           System.out.println("Username: "+uname+"\n");

		            Cell dob = row.getCell(3);

		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);

		            }

		            Cell gender = row.getCell(4);

		            if(gender != null && gender.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	String newGender = "";
		            	if(gender.getCellType() == Cell.CELL_TYPE_STRING){
		            		newGender = convertGender(gender.getStringCellValue());
		            	}
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }

		           Cell email = row.getCell(5);
		           if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		        	   String emailStr = email.getStringCellValue();
		        	   s.setEmail(emailStr);

		           }





		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           s.setGrade(6);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}

	//converts school file
	public static ArrayList<User> convertHumesStudentsFile(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

		            Cell fname = row.getCell(1);


		            s.setfName(WordUtils.capitalizeFully(fname.getStringCellValue()));
		            Cell lname = row.getCell(3);
		            s.setlName(WordUtils.capitalizeFully(lname.getStringCellValue()));
		            Cell mname = row.getCell(2);
		            if(mname != null && mname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setmName(WordUtils.capitalizeFully(mname.getStringCellValue()));
		            }




		           String uname = createUserName(fname.getStringCellValue(), lname.getStringCellValue());
		           s.setUserName(uname);


		            Cell dob = row.getCell(4);

		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String bday = "";
		            	if(dob.getCellType() == Cell.CELL_TYPE_STRING){
		            		bday = convertStringDate(dob.getStringCellValue(), "/");
		            	}
		            	else{
		            		String format = "YYYY-MM-dd";
			            	SimpleDateFormat sdf = new SimpleDateFormat(format);
			            	Date d = dob.getDateCellValue();
			            	bday = sdf.format(d);
		            	}

		            	s.setBirthday(bday);
		            }

		            Cell gender = row.getCell(5);

		            if(gender != null && gender.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	System.out.println("Gender value: "+gender);
		            	String newGender = "";
		            	if(gender.getCellType() == Cell.CELL_TYPE_STRING){
		            		System.out.println("Inside Gender If");
		            		newGender = convertGender(gender.getStringCellValue().trim());
		            	}
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(6);
		            System.out.println("boody 4");
		            if(grade != null && grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,6);
		            	String gradeSub = newGrade.substring(0,1);

		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(gradeSub);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		           Cell email = row.getCell(11);
		           if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		        	   String emailStr = email.getStringCellValue();
		        	   s.setEmail(emailStr);

		           }

		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String uuid_code = RandomStringUtils.randomAlphanumeric(6);
		           s.setUuid_code(uuid_code);

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}


	//converts school file
	public static ArrayList<User> convertNVFile(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(0);

		            s.setfName(fname.getStringCellValue());
		            Cell lname = row.getCell(1);
		            s.setlName(lname.getStringCellValue());
		            String username = createUserName(fname.getStringCellValue(),lname.getStringCellValue());
		            s.setUserName(username);

		            //s.setfName(fname.getStringCellValue());

		            Cell email = row.getCell(2);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setEmail(email.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }


		            Cell dob = row.getCell(4);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String dobStr = convertStringDate(dob.getStringCellValue(), "/");
		            	s.setBirthday(dobStr);

		            }

		            Cell gender = row.getCell(5);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(3);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setGrade(new Double(grade.getNumericCellValue()).intValue());

		            }

		           s.setUserType(1);
		           s.setOrg(orgId);
		           s.setStatus("PA");
		           String uuid_code = RandomStringUtils.randomAlphanumeric(6);
		           s.setUuid_code(uuid_code);

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}

		return Users;
	}
	//converts school file
	public static ArrayList<User> convertEastBronxFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	   s.setfName(fname.getStringCellValue());
		            }
		            Cell lname = row.getCell(2);
		            if(lname != null || lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	   s.setlName(lname.getStringCellValue());
		            }



		            //System.out.println("Name size: "+name.size()+"\n");



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(17);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setBirthday(dob.getStringCellValue());

		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }

		            Cell gender = row.getCell(5);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(6);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,6);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		           Cell email = row.getCell(13);
		           if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setUserName(email.getStringCellValue());
		            	s.setEmail(email.getStringCellValue());

		            	System.out.print("Username: "+s.getUserName()+"\n");
		            	System.out.print("Email: "+s.getEmail()+"\n");
		            }

		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");



		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	//converts school file
	public static ArrayList<User> convertBrooklynStudioFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(0);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	name = splitName(fname.getStringCellValue());
		            }

		            //System.out.println("Name size: "+name.size()+"\n");
		            s.setlName(name.get(0));
		            s.setfName(name.get(1));
		            if(name.size() > 2){
		            	s.setmName(name.get(2));
		            }

		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);


		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }

		            Cell gender = row.getCell(4);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(3);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,3);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }
		            Cell classRoom = row.getCell(2);
		            double classRoomDB = 0;
		            if(classRoom != null || classRoom.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	classRoomDB = classRoom.getNumericCellValue();
		            	int room = new Double(classRoomDB).intValue();
		            	s.setClassRoom(room);

		            }

		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String username = createUserName(s.getfName(),s.getlName());
		           s.setUserName(username);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	//converts school file
	public static ArrayList<User> convertDeweyFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            String lnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            }
		            Cell lname = row.getCell(3);
		            if(lname != null || lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            }

		            //System.out.println("Name size: "+name.size()+"\n");

		            s.setfName(fnameStr);
		            s.setlName(lnameStr);

		            String emailStr = null;
		            Cell email = row.getCell(4);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	emailStr = email.getStringCellValue();
		            }
		            s.setEmail(emailStr);


		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);


		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }

		            Cell gender = row.getCell(6);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(7);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,7);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String username = getStringValue(row,0);

		           s.setUserName(username);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}


	//converts school file
	public static ArrayList<User> convertStudentFile(String source){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            ArrayList<String> name = null;
		            Cell fname = row.getCell(0);
		            s.setfName(fname.getStringCellValue());
		            Cell mname = row.getCell(1);
		            s.setmName(mname.getStringCellValue());
		            Cell lname = row.getCell(2);
		            s.setlName(lname.getStringCellValue());
		            Cell email = row.getCell(3);
		            s.setEmail(email.getStringCellValue());
		            Cell bday = row.getCell(4);
		            s.setBirthday(bday.getStringCellValue());
		            Cell gender = row.getCell(5);
		            s.setGender(new Double(gender.getNumericCellValue()).intValue());
		            Cell grade = row.getCell(6);
		            s.setGrade(new Double(grade.getNumericCellValue()).intValue());
		            Cell orgId = row.getCell(29);
		            s.setOrg(new Double(orgId.getNumericCellValue()).intValue());
		            Cell username = row.getCell(30);
		            s.setUserName(username.getStringCellValue());
		            Cell uuid_code = row.getCell(31);
		            s.setUuid_code(uuid_code.getStringCellValue());
		            s.setUserType(1);
		            s.setStatus("PA");


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}

	//converts school  staff file
	public static ArrayList<User> convertAlbertusStaff(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		   //System.out.println("convert 2");
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

		            Cell fname = row.getCell(0);
		            //System.out.println("convert 3");
		            if(fname != null && fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setfName(fname.getStringCellValue());
		            }


		            Cell lname = row.getCell(2);
		            //System.out.println("convert 4");
		            if(lname != null && lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setlName(lname.getStringCellValue());
		            }
		            Cell mname = row.getCell(1);
		            //System.out.println("convert 5");
		            if(mname != null && mname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setmName(mname.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }


		            //s.setfName(fname.getStringCellValue());

		            Cell email = row.getCell(3);
		            System.out.println("convert 6");
		            if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setEmail(email.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }
		            Cell mobilePhone = row.getCell(6);

		            if(mobilePhone != null && mobilePhone.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	String mobile = cleanUpPhone(mobilePhone.getStringCellValue());
		            	s.setMobilePhone(mobile);
		            	//s.setUserName(email.getStringCellValue());
		            }

		           Cell staffType = row.getCell(8);

		           if(staffType != null && staffType.getCellType() != Cell.CELL_TYPE_BLANK){
		        	   String rawStaffType = staffType.getStringCellValue();
		        	   String newValue = getStaffString(rawStaffType);
		        	   s.setTypeString(newValue);
		        	   int type = convertUserType(newValue);
		        	   s.setUserType(type);

		           }


		           s.setOrg(orgId);
		           s.setStatus("PA");

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		 System.out.println("convert 10");
		return Users;
	}

		//converts school  staff file
	public static ArrayList<User> convertMS281Staff(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		   //System.out.println("convert 2");
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

		            Cell fname = row.getCell(0);
		            //System.out.println("convert 3");
		            if(fname != null && fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setfName(fname.getStringCellValue());
		            }


		            Cell lname = row.getCell(1);
		            //System.out.println("convert 4");
		            if(lname != null && lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setlName(lname.getStringCellValue());
		            }

		            Cell email = row.getCell(2);

		            if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setEmail(email.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }


		           Cell staffType = row.getCell(5);

		           if(staffType != null && staffType.getCellType() != Cell.CELL_TYPE_BLANK){
		        	   String rawStaffType = staffType.getStringCellValue();
		        	   String newValue = getStaffString(rawStaffType);
		        	   s.setTypeString(newValue);
		        	   int type = convertUserType(newValue);
		        	   s.setUserType(type);
		           }


		           s.setOrg(orgId);
		           s.setStatus("PA");

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		 System.out.println("convert 10");
		return Users;
	}
		//converts school  staff file
	public static ArrayList<User> convertMS53Staff(String source, int orgId){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		   //System.out.println("convert 2");
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

		            Cell fname = row.getCell(0);
		            //System.out.println("convert 3");
		            if(fname != null && fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setfName(fname.getStringCellValue());
		            }


		            Cell lname = row.getCell(1);
		            //System.out.println("convert 4");
		            if(lname != null && lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setlName(lname.getStringCellValue());
		            }

		            Cell email = row.getCell(2);

		            if(email != null && email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setEmail(email.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }


		           Cell staffType = row.getCell(5);

		           if(staffType != null && staffType.getCellType() != Cell.CELL_TYPE_BLANK){
		        	   String rawStaffType = staffType.getStringCellValue();
		        	   String newValue = getStaffString(rawStaffType);
		        	   s.setTypeString(newValue);
		        	   int type = convertUserType(newValue);
		        	   s.setUserType(type);
		           }


		           s.setOrg(orgId);
		           s.setStatus("PA");

		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		 System.out.println("convert 10");
		return Users;
	}





	//Checks if a name is gernerally male or female and assingsd gender if missing
	public static HashMap<String, Object>  getGenderNames(String nameList){

		HashMap<String, Object> nameHash = new HashMap<String, Object>();
		HashMap<String, Double> boyNames = new HashMap<String, Double>();
		HashMap<String, Double> girlNames = new HashMap<String, Double>();

		try {

		    FileInputStream nameFile = new FileInputStream(new File(nameList));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (nameFile);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		  //Get first sheet from the workbook
		   XSSFSheet sheet1 = workbook.getSheetAt(1);


		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		  //Iterate through each rows from second sheet
		    Iterator<Row> rowIterator1 = sheet1.iterator();

		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();
		        Cell fname = row.getCell(0);
		        String fnameStr = fname.getStringCellValue();
		        Cell rank = row.getCell(1);
		        Double d = new Double(rank.getNumericCellValue());
		        boyNames.put(fnameStr, d);
		    }
		    while(rowIterator1.hasNext()) {
		        Row row = rowIterator1.next();
		        Cell fname = row.getCell(0);
		        String fnameStr = fname.getStringCellValue();
		        Cell rank = row.getCell(1);
		        Double d = new Double(rank.getNumericCellValue());
		        girlNames.put(fnameStr, d);
		    }


		    nameFile.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		nameHash.put("boyNames", boyNames);
		nameHash.put("girlNames", girlNames);


		return nameHash;

	}





	public static String convertDate(int date){
		StringBuilder  sb = new StringBuilder();
		String d = "-";
		String intStr = String.valueOf(date);
		String yr = intStr.substring(0,4);
		String mn = intStr.substring(4,6);
		String dy = intStr.substring(6);
		sb.append(yr);
		sb.append(d);
		sb.append(mn);
		sb.append(d);
		sb.append(dy);

		return sb.toString();
	}
	public static String convertStringDate(String date, String token){
		StringBuilder  sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(date,token);
		ArrayList<String> dateFields = new ArrayList<String>();
		while(st.hasMoreTokens()){
			dateFields.add(st.nextToken());
		}
		if(dateFields.size() != 3){
			return date;
		}
		String d = "-";
		String pad = "0";
		String year = dateFields.get(2);
		sb.append(year);
		sb.append(d);
		String month = dateFields.get(0);
		if(month.length() == 1){
			sb.append(pad);
		}
		sb.append(month);
		sb.append(d);
		String day = dateFields.get(1);
		if(day.length() == 1){
			sb.append(pad);
		}
		sb.append(day);


		return sb.toString();


	}


	public static int getGenderVal(String name, HashMap<String, Double> boysNames, HashMap<String, Double> girlsNames ){
		int gender = 3;
		String name1 = name.toUpperCase();


		double db = 0.0;
		double dg = 0.0;

		if(boysNames.containsKey(name1) && girlsNames.containsKey(name1)){

			db = ((Double)boysNames.get(name1)).doubleValue();
			dg = ((Double)girlsNames.get(name1)).doubleValue();
			if(db > dg){

				gender = 4;
			}
			else{

				gender = 5;
			}
		}
		else if(boysNames.containsKey(name1)){

			gender = 1;
		}
		else if(girlsNames.containsKey(name1)){
			gender = 0;
		}

		return gender;

	}


	//Creates a template for new reports

	public static void createNewReportTemplate(ArrayList<User> Users, String filename){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Added Students");
		Row  header = sheet.createRow(0);
		ArrayList<String> headers = createNewTemplateHeaders();
		for(int i =0; i < headers.size(); i++){
			Cell c = header.createCell(i);
			c.setCellValue(headers.get(i));
		}
		int row = 1;
		for(int i = 0; i < Users.size(); i++){
			User s = Users.get(i);
			Row data = sheet.createRow(row);
			Cell userName = data.createCell(0);
			userName.setCellValue(s.getUserName());
			Cell fname = data.createCell(1);
			fname.setCellValue(s.getfName());
			Cell mname = data.createCell(2);
			mname.setCellValue(s.getmName());
			Cell lname = data.createCell(3);
			lname.setCellValue(s.getlName());
			Cell email = data.createCell(4);
			email.setCellValue(s.getEmail());
			Cell bday = data.createCell(5);
			bday.setCellValue(s.getBirthday());
			Cell gender = data.createCell(6);
			gender.setCellValue(s.getGender());
			Cell grade = data.createCell(7);
			grade.setCellValue(s.getGrade());
			Cell class_rm = data.createCell(8);
			class_rm.setCellValue(s.getClassRoom());
			//Cell orgId = data.createCell(29);
			//orgId.setCellValue(s.getOrg());
			//Cell userName = data.createCell(30);
			//userName.setCellValue(s.getUserName());
			//Cell uuid_code = data.createCell(31);
			//uuid_code.setCellValue(s.getUuid_code());

			Cell parent1_fname = data.createCell(17);
			parent1_fname.setCellValue(s.getParent1Fname());
			Cell parent1_lname = data.createCell(19);
			parent1_lname.setCellValue(s.getParent1Lname());
			Cell parent1_email = data.createCell(20);
			parent1_email.setCellValue(s.getParent1Email());


			row++;

		}
	    sheet.autoSizeColumn(0);
	    sheet.autoSizeColumn(1);
	    sheet.autoSizeColumn(2);
	    sheet.autoSizeColumn(3);
	    sheet.autoSizeColumn(4);
	    sheet.autoSizeColumn(5);
	    sheet.autoSizeColumn(6);
	    sheet.autoSizeColumn(29);
	    sheet.autoSizeColumn(30);
	    sheet.autoSizeColumn(17);
	    sheet.autoSizeColumn(19);
	    sheet.autoSizeColumn(20);

		try{
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}
	//creates a report based on school grade
	public static void createNewClassReport(ArrayList<User> Users, String filename){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Class Students");
		Row  header = sheet.createRow(0);
		ArrayList<String> headers = createClassTemplateHeaders();
		for(int i =0; i < headers.size(); i++){
			Cell c = header.createCell(i);
			c.setCellValue(headers.get(i));
		}
		int row = 1;
		for(int i = 0; i < Users.size(); i++){
			User s = Users.get(i);
			Row data = sheet.createRow(row);
			Cell userName = data.createCell(0);
			userName.setCellValue(s.getUserName());
			Cell fname = data.createCell(1);
			fname.setCellValue(s.getfName());
			Cell mname = data.createCell(2);
			mname.setCellValue(s.getmName());
			Cell lname = data.createCell(3);
			lname.setCellValue(s.getlName());
			Cell classRoom = data.createCell(4);
			classRoom.setCellValue(s.getClassRoom());

			row++;

		}
	    sheet.autoSizeColumn(0);
	    sheet.autoSizeColumn(1);
	    sheet.autoSizeColumn(2);
	    sheet.autoSizeColumn(3);
	    sheet.autoSizeColumn(4);
	    sheet.autoSizeColumn(5);
	    sheet.autoSizeColumn(6);
	    sheet.autoSizeColumn(29);
	    //sheet.autoSizeColumn(30);
		try{
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}


	//creates a new staff status report
	public static void createNewStaffReportTemplate(ArrayList<User> Users, String filename){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Staff Template");
		Row  header = sheet.createRow(0);
		ArrayList<String> headers = createStaffHeaders();
		for(int i =0; i < headers.size(); i++){
			Cell c = header.createCell(i);
			c.setCellValue(headers.get(i));
		}
		int row = 1;
		for(int i = 0; i < Users.size(); i++){
			User s = Users.get(i);
			Row data = sheet.createRow(row);
			Cell fname = data.createCell(0);
			fname.setCellValue(s.getfName());
			Cell mname = data.createCell(1);
			mname.setCellValue(s.getmName());
			Cell lname = data.createCell(2);
			lname.setCellValue(s.getlName());
			Cell email = data.createCell(3);
			email.setCellValue(s.getEmail());
			Cell mobile = data.createCell(6);
			mobile.setCellValue(s.getMobilePhone());
			Cell staffType = data.createCell(8);
			staffType.setCellValue(s.getTypeString());


			row++;

		}
	    sheet.autoSizeColumn(0);
	    sheet.autoSizeColumn(1);
	    sheet.autoSizeColumn(2);
	    sheet.autoSizeColumn(3);
	    sheet.autoSizeColumn(4);
	    sheet.autoSizeColumn(5);
	    sheet.autoSizeColumn(6);
	    sheet.autoSizeColumn(29);
	    sheet.autoSizeColumn(30);
		try{
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}

	//creates a standard format for phone numbers
	public static String cleanUpPhone(String phone){

		System.out.println("Clean Up Phone");
		StringBuffer sb = new StringBuffer();
		phone = StringUtils.remove(phone, "(");
		phone = StringUtils.remove(phone, ")");
		phone = StringUtils.remove(phone, "-");
		if(phone.length() != 11){
			sb.append("1");
		}
		sb.append(phone);

		return sb.toString();
	}

	public static void testNameReport(ArrayList<User> Users, String filename){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Name Test");
		Row  header = sheet.createRow(0);
		ArrayList<String> headers = createTestNameHeaders();
		for(int i =0; i < headers.size(); i++){
			Cell c = header.createCell(i);
			c.setCellValue(headers.get(i));
		}
		int row = 1;
		for(int i = 0; i < Users.size(); i++){
			User s = Users.get(i);
			Row data = sheet.createRow(row);
			Cell fname = data.createCell(0);
			fname.setCellValue(s.getfName());
			Cell gender1 = data.createCell(1);
			gender1.setCellValue(s.getGender());
			Cell gender2 = data.createCell(2);

			row++;

		}

		try{
			FileOutputStream out = new FileOutputStream(new File(filename));
			workbook.write(out);
			out.close();

		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}



	}
	//creates a new username
	public static String createUserName(String fname, String lname){
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		int i = 100+r.nextInt(900);
		String suff = String.valueOf(i);
		String first = fname.substring(0,1);
		String last = "";
		sb.append(first);
		if(lname.length() > 4){
			last = lname.substring(0, 4);
		}
		else{
			last = lname;
		}
		sb.append(last);
		sb.append(suff);
		String u1 = sb.toString();
		String u2 = u1.replace(" ", "");
		StringBuilder sb1 = new StringBuilder(u2);

		if(sb1.length() == 5){
			int j = r.nextInt(9);
			sb1.append(j);
		}


		return sb1.toString().toLowerCase();

	}
	public static String createDate(){
		StringBuilder sb = new StringBuilder();
		String df = "yyyy-MM-dd kk:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(df);
		Date today = new Date();
		String todayFormat = sdf.format(today);
		sb.append(todayFormat);

		return sb.toString();

	}
   	public static String createUserString(User s){
   		StringBuilder sb = new StringBuilder();
   		String sl = "/";
   		sb.append(s.getfName());
   		sb.append(sl);
   		sb.append(s.getlName());
   		sb.append(sl);
   		sb.append(s.getBirthday());
   		sb.append(sl);
   		sb.append(s.getUserName());
   		sb.append(sl);
   		sb.append(s.getOrg());
   		sb.append(sl);
   		sb.append("s");

   		return sb.toString();
   	}
		creates a uuid staoring student information
   	public  static HashMap<String, Object> createUUID(String str){
   		HashMap<String,Object> hm = new HashMap<String,Object>();


   		UUID uuid = null;
   		byte[] byteAr = str.getBytes();
   		BigInteger bi = new BigInteger(byteAr);
   		String str1 = bi.toString();
   		System.out.println("str1: "+str1+"\n");

   		String front = str1.substring(0, str1.length()/2);
   		System.out.println("front: "+front+"\n");
   		String back = str1.substring(str1.length()/2);
   		System.out.println("back: "+back+"\n");

   		long mv = Long.MAX_VALUE;
   		BigInteger mvbi = BigInteger.valueOf(mv);

   		BigInteger frontBi = new BigInteger(front);

   		BigInteger frontQ = frontBi.divide(mvbi);
   		BigInteger frontMod = frontBi.mod(mvbi);

   		BigInteger frontD = frontQ.multiply(mvbi);
   		BigInteger frontT = frontD.add(frontMod);

   		if(frontBi.equals(frontT)){
   			System.out.println("Front BI passed \n");
   		}
   		else{
   			System.out.println("Front BI failed \n");
   		}


   		BigInteger backBi = new BigInteger(back);

   		BigInteger backQ = backBi.divide(mvbi);
   		BigInteger backMod = backBi.mod(mvbi);

   		BigInteger backD = backQ.multiply(mvbi);
   		BigInteger backT = backD.add(backMod);

   		if(backBi.equals(backT)){
   			System.out.println("Back BI passed \n");
   		}
   		else{
   			System.out.println("Back BI failed \n");
   		}
   		String frontTs = frontT.toString();
   		String backTs = backT.toString();
   		String totalS = frontTs+backTs;
   		System.out.println("totalS: "+totalS+"\n");
   		BigInteger test = new BigInteger(frontTs+backTs);
   		byte [] bat = test.toByteArray();
   		String test1 = new String(bat);
   		if(test1.equals(str)){
   			System.out.println("String Test passed \n");
   		}
   		else{
   			System.out.println("String Test failed \n");
   		}
   		System.out.println("Test 1: "+test1+"\n");

   		long uuid_front = frontMod.longValue();
   		long uuid_back = backMod.longValue();
   		uuid = new UUID(uuid_front, uuid_back);
   		hm.put("uuid", uuid);
   		hm.put("front", frontQ.toString());
   		//System.out.println("frontQ: "+frontQ.toString());


   		hm.put("back", backQ.toString());
   		/**
   		String fq1 = (String)hm.get("front");
   		String bq1 = (String)hm.get("back");
   		BigInteger bifq1 = new BigInteger(fq1);
   		BigInteger bibq1 = new BigInteger(bq1);
   		BigInteger frontD1 = bifq1.multiply(mvbi);
   		BigInteger backD1 = bibq1.multiply(mvbi);
   		UUID uuid1 = (UUID)hm.get("uuid");
   		long uuidf1 = uuid1.getMostSignificantBits();
   		long uuidb1 = uuid1.getLeastSignificantBits();
   		BigInteger umodf = BigInteger.valueOf(uuidf1);
   		BigInteger umodb = BigInteger.valueOf(uuidb1);
   		BigInteger frontT1 = frontD1.add(umodf);
   		BigInteger backT1 = backD1.add(umodb);
   		String frontTS = frontT1.toString();
   		String backTS = backT1.toString();
   		String total = frontTS+backTS;
   		System.out.println(total);
   		BigInteger totalF = new BigInteger(total);
   		byte [] bat1 = totalF.toByteArray();
   	    String test1a = new String(bat1);
   	    System.out.println("test1a: "+test1a+"\n");
   		**/




   		return hm;

   	}
	// creates a user for a UUID string
	public static String recreateUserString(HashMap<String, Object> hm){
		String st = "";
		long mv = Long.MAX_VALUE;
		BigInteger mvbi = BigInteger.valueOf(mv);
		String fq1 = (String)hm.get("front");
   		String bq1 = (String)hm.get("back");
   		BigInteger bifq1 = new BigInteger(fq1);
   		BigInteger bibq1 = new BigInteger(bq1);
   		BigInteger frontD1 = bifq1.multiply(mvbi);
   		BigInteger backD1 = bibq1.multiply(mvbi);
   		UUID uuid1 = (UUID)hm.get("uuid");
   		long uuidf1 = uuid1.getMostSignificantBits();
   		long uuidb1 = uuid1.getLeastSignificantBits();
   		BigInteger umodf = BigInteger.valueOf(uuidf1);
   		BigInteger umodb = BigInteger.valueOf(uuidb1);
   		BigInteger frontT1 = frontD1.add(umodf);
   		BigInteger backT1 = backD1.add(umodb);
   		String frontTS = frontT1.toString();
   		String backTS = backT1.toString();
   		String total = frontTS+backTS;
   		System.out.println(total);
   		BigInteger totalF = new BigInteger(total);
   		byte [] bat1 = totalF.toByteArray();
   	    String test1a = new String(bat1);
   	    System.out.println("test1a: "+test1a+"\n");

		return st =  new String(bat1);

	}
 // checks if a username exsits
	public static int checkUserName(Connection con, String uname){
		int flag = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("Select rec_id from user where username = ");
		sb.append(sq);
		sb.append(uname);
		sb.append(sq);
		String query = sb.toString();


		try{
			ResultSet rs = null;

			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					flag = rs.getInt("rec_id");
				}
			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}



		return flag;
	}
	public static String generateHash(String seed){

		String hash = "";

		int ranLen = 30 - seed.length();
		String salt = RandomStringUtils.randomAlphanumeric(ranLen);
		String front = salt.substring(0,5);
		String back = salt.substring(5);
		hash = front+StringUtils.reverse(seed)+back;

		return hash;

	}
 //Inserts a user in the PHP database
	public static boolean addBridgitUser(User s, Connection con){
		int flag = 0;
		String hash = generateHash(s.getUserName());
		String email = s.getEmail();
		String userName = s.getUserName();
		String fname = s.getfName();
		String mname = s.getmName();

		String lname = s.getlName();
		int last_org = s.getOrg();

		StringBuilder sb = new StringBuilder();
		sb.append("insert into user (hash, email,username,fname,mname,lname, last_organization_id) values(");
		sb.append(format(hash));
		sb.append(format(email));
		sb.append(format(userName));
		sb.append(format(fname));
		sb.append(format(mname));
		sb.append(format(lname));
		sb.append(last_org);
		sb.append(")");

		String query = sb.toString();
		System.out.println(query);



		try{

			Statement stmt = con.createStatement();
			flag = stmt.executeUpdate(query);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


	    return flag > 0;
	}
	public static HashMap<String, Object> addBridgitUsers(ArrayList<User> users, Connection con){
		ArrayList<User> addedUsers = new ArrayList<User>();
		ArrayList<User> duplicateUsers = new ArrayList<User>();
		HashMap<String, Object> userRecords = new HashMap<String,Object>();
		for(int i =0; i < users.size(); i++){
			User u = users.get(i);
			String uname = u.getUserName();
			if(checkUserName(con, uname) == 0){
				 addBridgitUser(u,con);
				 addedUsers.add(u);
                 //System.out.println("Added User "+uname+"\n");
			}
			else{
				duplicateUsers.add(u);
				 //System.out.println("Dupplicate User "+uname+"\n");
			}

		}
		userRecords.put("addedUsers", addedUsers);
		userRecords.put("duplicateUsers", duplicateUsers);

		return userRecords;




	}
 //Creates user java side
	public static boolean addBridgitOrgUser(User s, Connection con, String hash, int userId){
		int flag = 0;

		String email = s.getEmail();
		String userName = s.getUserName();
		String fname = s.getfName();
		String mname = s.getmName();

		String lname = s.getlName();

		StringBuilder sb = new StringBuilder();
		sb.append("insert into user (user_id, hash, username, email,fname,mname,lname,status,type) values(");
		sb.append(userId);
		sb.append(comma);
		sb.append(format(hash));
		sb.append(format(userName));
		sb.append(format(email));
		sb.append(format(fname));
		sb.append(format(mname));
		sb.append(format(lname));
		sb.append(format(s.getStatus()));
		sb.append(s.getUserType());
		sb.append(")");

		String query = sb.toString();
		System.out.println(query);



		try{

			Statement stmt = con.createStatement();
			flag = stmt.executeUpdate(query);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


	    return flag > 0;
	}

    public static boolean addBridgitGroupUser(Connection con, int recId, int groupId){
    	int i = 0;

    	StringBuilder sb = new StringBuilder();
		sb.append("insert into group_user (group_id, user_id, date_created) values(");
		sb.append(groupId);
		sb.append(comma);
		sb.append(recId);
		sb.append(comma);
		sb.append("current_timestamp");
		sb.append(")");
		String query = sb.toString();

		try{

			Statement stmt = con.createStatement();
			i = stmt.executeUpdate(query);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


    	return i > 0;
    }


	private static String format(String str){
		StringBuilder sb = new StringBuilder();
		sb.append(sq);
		sb.append(str);
		sb.append(sq);
		sb.append(comma);

		return sb.toString();

	}
	public static int getRecId(Connection con, String username){
		int rec = 0;
		StringBuilder sb = new StringBuilder();
		String front = "select rec_id from user where username = ";
		sb.append(front);
		sb.append(sq);
		sb.append(username);
		sb.append(sq);
        String query = sb.toString();
        ResultSet rs = null;



		try{

			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null ){
				while(rs.next()){
					rec = rs.getInt(1);
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return rec;
	}
	public static String getUserHash(Connection con, String username){
		String rec = "";
		StringBuilder sb = new StringBuilder();
		String front = "select hash from user where username = ";
		sb.append(front);
		sb.append(sq);
		sb.append(username);
		sb.append(sq);
        String query = sb.toString();
        ResultSet rs = null;



		try{

			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null ){
				while(rs.next()){
					rec = rs.getString("hash");
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return rec;
	}


	public static boolean addUserProfile (Connection con, User s){
	    int flag = 0;
	    int recId = 0;
	    StringBuilder sb = new StringBuilder();
	    String bdayEnc = PgpEncryption.encryptString(s.getBirthday());
	    String front = "insert into user_profile (user_id, gender, birthday) values(";



	    recId = getRecId(con,s.getUserName());
	    if(recId != 0){
	    	sb.append(front);
	    	sb.append(recId);
	    	sb.append(comma);
	    	sb.append(s.getGender());
	    	sb.append(comma);
	    	sb.append(sq);
	    	sb.append(bdayEnc);
	    	sb.append(sq);
	    	sb.append(")");
	    	String query = sb.toString();
	    	try{

				Statement stmt = con.createStatement();
				flag = stmt.executeUpdate(query);
			}
			catch(SQLException e){
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}

	    }

	    return flag > 0;

	}
	public static boolean addUserOrganization (Connection con, User s){
	    int flag = 0;
	    int recId = 0;
	    StringBuilder sb = new StringBuilder();
	    String bdayEnc = PgpEncryption.encryptString(s.getBirthday());
	    String front = "insert into organization_user (organization_id, user_id, type, grade, allow_add_report) values(";



	    recId = getRecId(con,s.getUserName());
	    if(recId != 0){
	    	sb.append(front);
	       	sb.append(s.getOrg());
	    	sb.append(comma);
	    	sb.append(recId);
	    	sb.append(comma);
	    	sb.append(s.getUserType());
	    	sb.append(comma);
	    	sb.append(sq);
	    	sb.append(Integer.toString(s.getGrade()));
	    	sb.append(sq);
	    	sb.append(comma);
	    	sb.append(1);
	    	sb.append(")");
	    	String query = sb.toString();
	    	try{

				Statement stmt = con.createStatement();
				flag = stmt.executeUpdate(query);
			}
			catch(SQLException e){
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}

	    }

	    return flag > 0;

	}

	public static HashMap<String, String> getOrganizationSettings(Connection con, int id){
		HashMap <String,String> hm = new HashMap<String,String>();
		StringBuilder sb = new StringBuilder();
		String front = "select language_code, time_zone, format_date, format_time, targets, aggressors from settings where rec_id = ";
		sb.append(front);
		sb.append(id);
		String query = sb.toString();
		ResultSet rs = null;

		try{

			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null ){
				while(rs.next()){
					String lang = rs.getString("language_code");
					String time_zone = rs.getString("time_zone");
					String format_date = rs.getString("format_date");
                    String format_time = rs.getString("format_time");
					String targets = rs.getString("targets");
					String aggressors = rs.getString("aggressors");

					hm.put("lang", lang);
					if(time_zone == null ){
						time_zone = "";
					}
					hm.put("time_zone", time_zone);
					hm.put("format_date", format_date);
					hm.put("format_time", format_time);
					hm.put("targets", targets);
					hm.put("aggressors", aggressors);
				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}



		return hm;
	}

	public static boolean addUserSetting(Connection con, HashMap<String,String> values, int userId){

		int flag = 0;
		StringBuilder sb = new StringBuilder();
		String front1 = "insert into user_settings (user_id, language_code, time_zone, format_date, ";
		String front2 = "format_time, targets, aggressors) values( ";
		sb.append(front1);
		sb.append(front2);
		sb.append(userId);
		sb.append(comma);
		String lang = values.get("lang");
		String timezone = values.get("time_zone");
		String format_date = values.get("format_date");
		String format_time = values.get("format_time");
		String targets = values.get("targets");
		String aggressors = values.get("aggressors");
		sb.append(format(lang));
		sb.append(format(timezone));
		sb.append(format(format_date));
		sb.append(format(format_time));
		sb.append(format(targets));
		sb.append(sq);
		sb.append(aggressors);
		sb.append(sq);
		sb.append(")");
		String query = sb.toString();
		System.out.println(query);
		try{

			Statement stmt = con.createStatement();
			flag = stmt.executeUpdate(query);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return flag > 0;

	}

	public static String getStaffString(String staffType){
		String staffStr = "";
		if(staffType == null){
			staffType = "staff";
		}

		else if(staffType.toLowerCase().contains("president")){
			staffStr = "Principal";
		}
		else if(staffType.toLowerCase().contains("principal")){
			staffStr = "Principal";
		}
		else if(staffType.toLowerCase().contains("staff")){
			staffStr = "Staff";
		}
		else if(staffType.toLowerCase().contains("teacher")){
			staffStr = "Staff";
		}
		else if(staffType.toLowerCase().contains("moderator")){
			staffStr = "Moderator";
		}
		else if(staffType.toLowerCase().contains("guidance")){
			staffStr = "Guidance";
		}
		else{
			staffType = "staff";
		}

		return staffStr;

	}


	private static int convertUserType(String type){
		int typeInt = 0;
		if(type.equalsIgnoreCase("student")){
			typeInt = 1;
		}
		if(type.equalsIgnoreCase("parent")){
			typeInt = 2;
		}
		if(type.equalsIgnoreCase("teacher")){
			typeInt = 3;
		}
		if(type.equalsIgnoreCase("staff")){
			typeInt = 3;
		}
		if(type.equalsIgnoreCase("moderator")){
			typeInt = 4;
		}
		if(type.equalsIgnoreCase("principal")){
			typeInt = 6;
		}
		if(type.equalsIgnoreCase("security")){
			typeInt = 7;
		}
		if(type.equalsIgnoreCase("guidance")){
			typeInt = 8;
		}

		return typeInt;
	}
	public static boolean addUserUUID(Connection con, HashMap<String, Object> hm, String uuid_code){
		int flag = 0;
		UUID uuid = (UUID)hm.get("uuid");
		String uuidStr = uuid.toString();
		String front = (String)hm.get("front");
		System.out.println("Front: "+front);
		String back = (String)hm.get("back");
		StringBuilder sb = new StringBuilder();


		String frontQ = "insert into bridgit_uuid (uuid, front_quotient, back_quotient, uuid_code) values ( ";
		sb.append(frontQ);
		sb.append(formatString(uuidStr));
		sb.append(formatString(front));
		sb.append(formatString(back));
		sb.append(sq);
		sb.append(uuid_code);
		sb.append(sq);
		sb.append(")");
		String query = sb.toString();
		try{

			Statement stmt = con.createStatement();
			flag = stmt.executeUpdate(query);
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}






		return flag > 0;

	}
	public static HashMap<String, Object> getUUIDInfo(Connection con, String searchTerm){
		HashMap<String, Object> hm = new HashMap<String, Object>();
		StringBuilder sb = new StringBuilder();
		String uuidT = "uuid";
		String uuidC = "uuid_code";
		String queryF = "SELECT uuid, front_quotient, back_quotient, accessed, revoked FROM bridgit_uuid WHERE ";
		sb.append(queryF);
		if(searchTerm.length() == 6){
			sb.append(uuidC);
		}
		else{
			sb.append(uuidT);
		}
		sb.append(" = ");
		sb.append(sq);
		sb.append(searchTerm);
		sb.append(sq);
		String query = sb.toString();
		System.out.println("Query "+query);

		ResultSet rs = null;

		try{

			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null ){
				while(rs.next()){
					String uuids = rs.getString("uuid");
					UUID uuid = UUID.fromString(uuids);
					String front = rs.getString("front_quotient");
					String back = rs.getString("back_quotient");
                    String accessed = rs.getString("accessed");
					String revoked = rs.getString("revoked");
					hm.put("uuid", uuid);
				    hm.put("front", front);
				    hm.put("back", back);
				    hm.put("accessed", accessed);
				    hm.put("revoked", revoked);



				}
			}
		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}


		return hm;

	}
	public static void convertToCSV(File inputFile, File outputFile) {
        // For storing data into CSV files
        StringBuffer data1 = new StringBuffer();

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            // Get the workbook object for XLSX file
            XSSFWorkbook wBook = new XSSFWorkbook(new FileInputStream(inputFile));
            // Get first sheet from the workbook
            XSSFSheet sheet = wBook.getSheetAt(0);
            Row row;

            // Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                StringBuffer data = new StringBuffer();
                // For each row, iterate through each columns
                for(int i = 0; i < 31; i++){
                	Cell cell = row.getCell(i);
                	if (cell == null){
                		data.append(""+",");
                	}
                	else{

                		switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_BOOLEAN:
                            data.append(cell.getBooleanCellValue() + ",");

                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                        	double d = cell.getNumericCellValue();
                        	int a = (int)d;
                            data.append(a + ",");

                            break;
                        case Cell.CELL_TYPE_STRING:
                            data.append(cell.getStringCellValue() + ",");
                            break;

                        case Cell.CELL_TYPE_BLANK:
                            data.append("" + ",");
                            break;
                        default:
                            data.append("" + ",");

                		}

                	}


                }

                String rowData = data.substring(0, data.length()-1);
                data1.append(rowData);
				data1.append("\r\n");

            }

            fos.write(data1.toString().getBytes());
            fos.close();

        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

	public static void createUserInfoFile(String output, String grade){
		XSSFWorkbook workbook = new XSSFWorkbook();
		ArrayList <String> headers = createUserInfoHeaders();


			XSSFSheet sheet = workbook.createSheet(grade+"th Grade Students");
			Row header = sheet.createRow(0);
			for(int j = 0; j < headers.size(); j++){
				Cell c = header.createCell(j);
				c.setCellValue(headers.get(j));

			}


		try{
			FileOutputStream out = new FileOutputStream(new File(output));
			workbook.write(out);
			out.close();
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}

	}

	public static ArrayList<User> convertTemplatetoUsers(String source){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		   //System.out.println("convert 2");
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

		            Cell fname = row.getCell(1);
		            //System.out.println("convert 3");
		            if(fname != null && fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setfName(fname.getStringCellValue());
		            }


		            Cell lname = row.getCell(3);
		            //System.out.println("convert 4");
		            if(lname != null && lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setlName(lname.getStringCellValue());
		            }

		            Cell username = row.getCell(0);

		            if(username != null && username.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	s.setUserName(username.getStringCellValue());
		            	//s.setUserName(email.getStringCellValue());
		            }



		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		 System.out.println("convert 10");
		return Users;
	}

	public static ArrayList<User> convertMecaFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(3);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String emailStr = null;
		            Cell email = row.getCell(4);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	emailStr = email.getStringCellValue();
		            	s.setEmail(emailStr);
		            }



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);


		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }

		            Cell gender = row.getCell(6);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(7);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,7);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           String username = createUserName(s.getfName(),s.getlName());
		           s.setUserName(username);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	public static ArrayList<User> convertMottFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(3);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String emailStr = null;
		            Cell email = row.getCell(4);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	emailStr = email.getStringCellValue();
		            	s.setEmail(emailStr);
		            }



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Double d = dob.getNumericCellValue();
		            	int i = d.intValue();
		            	String bday = convertDate(i);
		            	s.setBirthday(bday);

		            }

		            Cell gender = row.getCell(6);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(7);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGrade = getStringValue(row,7);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);

		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");
		           //String username = createUserName(s.getfName(),s.getlName());
		           Cell username = row.getCell(0);
		           String usernameStr = "";
		           if(username != null || username.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	usernameStr = username.getStringCellValue();
		            	s.setEmail(emailStr);
		            }

		           s.setUserName(usernameStr);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	public static ArrayList<User> convertLAAllianceFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();
		String oldFormat = "MM/dd/yyyy";

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();

	                Cell userName = row.getCell(0);
		        	String userStr = null;
		        	 if(userName != null || userName.getCellType() != Cell.CELL_TYPE_BLANK ){

			             userStr = getStringValue(row,0);
			             s.setUserName(userStr);
		        	 }

		        	String fnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(3);
		            if(lname != null || lname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }



		            //s.setfName(fname.getStringCellValue());
		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);


		            	System.out.print("Date: "+s.getBirthday()+"\n");

		            }


		            Cell gender = row.getCell(6);
		            System.out.print("Gender "+gender);

		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }

		            Cell grade = row.getCell(7);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){

		            	String newGrade = getStringValue(row,7);
		            	System.out.println("New Grade: "+newGrade);
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGrade);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 99;
		            	}
		            	s.setGrade(val);


		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");






		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	public static ArrayList<User> convertBronxdaleFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();
		String oldFormat = "MM/dd/yyyy";

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(2);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String emailStr = null;
		            Cell email = row.getCell(0);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	emailStr = email.getStringCellValue();
		            	s.setEmail(emailStr);
		            }



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String oldBday = dob.getStringCellValue();
		            	System.out.println("Bday : "+oldBday);

		            	String newBday = convertDate(oldFormat,oldBday);


		            	s.setBirthday(newBday);

		            }

		            Cell gender = row.getCell(4);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(3);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String valstr = grade.getStringCellValue();
		            	int val = 0;
		            	try
		            	{
		            	   val = Integer.parseInt(valstr);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}


		            	s.setGrade(val);

		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");



		           s.setUserName(emailStr);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	public static ArrayList<User> convertLaTechFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();
		String oldFormat = "MM/dd/yyyy";

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(0);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String emailStr = null;
		            Cell email = row.getCell(2);
		            if(email != null || email.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	emailStr = email.getStringCellValue();
		            	s.setEmail(emailStr);
		            }



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(3);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);



		            }

		            Cell gender = row.getCell(4);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(5);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Double gradeD = grade.getNumericCellValue();
		            	int val = gradeD.intValue();


		            	s.setGrade(val);

		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");



		           s.setUserName(emailStr);


		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}

	public static HashMap<String,String> getDeweyEmails(String source){
		ArrayList<User> Users = new ArrayList<User>();
        HashMap<String,String> emails = new HashMap<String,String>();

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	Cell id_cell = row.getCell(4);
		        	String id_str = null;
		        	if(id_cell != null || id_cell.getCellType() != Cell.CELL_TYPE_BLANK ){
		        		Double id_int = id_cell.getNumericCellValue();
		           		Integer i = id_int.intValue();
		           		id_str = i.toString();
		        	}

		        	Cell email_cell = row.getCell(2);
		        	String email_str = null;
		        	if(email_cell != null || email_cell.getCellType() != Cell.CELL_TYPE_BLANK ){
		        		email_str = email_cell.getStringCellValue();
		        	}

		        	emails.put(id_str, email_str);

		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return emails;
	}
	public static HashMap<String,User> getDeweyInfo(String source){
		//ArrayList<User> Users = new ArrayList<User>();
        HashMap<String,User> users = new HashMap<String, User>();
        String oldFormat = "MM/dd/yy";

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User u = new User();
		        	u.setGrade(6);
		        	u.setStatus("PA");
		        	u.setUserType(1);
                    u.setOrg(31);


		        	Cell id_cell = row.getCell(8);
		        	String id_str = null;
		        	if(id_cell != null || id_cell.getCellType() != Cell.CELL_TYPE_BLANK ){
		        		//Double id_int = id_cell.getNumericCellValue();
		           		//Integer i = id_int.intValue();
		           		id_str = id_cell.getStringCellValue();
		        	}
		        	u.setUserName(id_str);

		        	Cell name_cell = row.getCell(7);
		        	String name_str = null;

		        	if(name_cell != null || name_cell.getCellType() != Cell.CELL_TYPE_BLANK ){
		        		name_str = name_cell.getStringCellValue();
		        		ArrayList<String> name_array = splitName(name_str);
		        		String fname = name_array.get(name_array.size()-1);
		        		String lname = name_array.get(0);
		        		u.setfName(fname);
		        		u.setlName(lname);

		        	}
		        	Cell gender = row.getCell(9);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	u.setGender(val);

		            }
		            Cell grade = row.getCell(0);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String grade_str = grade.getStringCellValue();
		            	int classRoom = Integer.parseInt(grade_str);

		            	u.setClassRoom(classRoom);

		            }
		            Cell dob = row.getCell(10);
		            if(dob != null || dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String oldBday = dob.getStringCellValue();
		            	//System.out.println("Bday : "+oldBday);
		            	String newBday = convertDate(oldFormat,oldBday);
		            	//System.out.println("New Bday : "+newBday);
		            	u.setBirthday(newBday);
		            }
		            users.put(id_str, u);





		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return users;
	}
	public static ArrayList<User> convertHewlettFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();
		String oldFormat = "MM/dd/yyyy";

		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(3);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(2);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String userNameStr = null;
		            Cell uname = row.getCell(4);
		            if(uname != null || uname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	userNameStr = uname.getStringCellValue();
		            	s.setUserName(userNameStr);
		            	if(orgId == 50){
		            		s.setEmail(userNameStr);
		            	}
		            }



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(5);
		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);

		            }

		            Cell gender = row.getCell(6);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(7);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Double d = grade.getNumericCellValue();
		    			int i = d.intValue();

		                s.setGrade(i);
		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");






		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}
	public static ArrayList<User> convertConnetquotFile(String source, int orgId  ){
		ArrayList<User> Users = new ArrayList<User>();
		ArrayList<String> userNames = new ArrayList<String>();


		try {

		    FileInputStream file = new FileInputStream(new File(source));

		    //Get the workbook instance for XLS file
		    XSSFWorkbook workbook = new XSSFWorkbook (file);

		    //Get first sheet from the workbook
		    XSSFSheet sheet = workbook.getSheetAt(0);

		    //Iterate through each rows from first sheet
		    Iterator<Row> rowIterator = sheet.iterator();
		    while(rowIterator.hasNext()) {
		        Row row = rowIterator.next();

		        if(row.getRowNum() != 0 && !isRowEmpty(row) ){
		        	User s = new User();
		            String fnameStr = null;
		            Cell fname = row.getCell(1);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	fnameStr = fname.getStringCellValue();
		            	s.setfName(fnameStr);
		            }
		            String lnameStr = null;
		            Cell lname = row.getCell(0);
		            if(fname != null || fname.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	lnameStr = lname.getStringCellValue();
		            	s.setlName(lnameStr);
		            }
		            String userNameStr = null;
		            String fi = fnameStr.substring(0,1);
		            userNameStr = fi+lnameStr;
		            if(userNames.contains(userNameStr)){
		            	String ran = RandomStringUtils.randomAlphanumeric(3);
		            	userNameStr = userNameStr.concat(ran);
		                userNames.add(userNameStr);
		            }
		            else{
		            	userNames.add(userNameStr);
		            }
		            s.setUserName(userNameStr.toLowerCase());



		            //s.setfName(fname.getStringCellValue());

		            Cell dob = row.getCell(3);
		            if(dob != null && dob.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	Date d = dob.getDateCellValue();
		            	String newFormat = "yyyy-MM-dd";
		            	DateFormat dateFormatNeeded = new SimpleDateFormat(newFormat);
		            	String dateStr = dateFormatNeeded.format(d);
		            	s.setBirthday(dateStr);

		            }

		            Cell gender = row.getCell(4);
		            if(gender != null || gender.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String newGender = convertGender(gender.getStringCellValue());
		            	int val;
		            	try
		            	{
		            	   val = Integer.parseInt(newGender);
		            	}
		            	catch (NumberFormatException nfe)
		            	{

		            	   val = 3;
		            	}
		            	s.setGender(val);

		            }
		            Cell grade = row.getCell(2);
		            if(grade != null || grade.getCellType() != Cell.CELL_TYPE_BLANK ){
		            	String gradeStr = grade.getStringCellValue();
		    			int i = Integer.parseInt(gradeStr);

		                s.setGrade(i);
		            }


		           s.setOrg(orgId);
		           s.setUserType(1);
		           s.setStatus("PA");






		           Users.add(s);



		        }



		    }

		    file.close();




		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}




		return Users;
	}





	public static void main( String[] args ){

       /**
       User s1 = new User();
       s1.setBridgit_org("bt1");
       s1.setOrg(1);
       s1.setGrade(9);
       s1.setBridgit_id(2720);
       s1.setfName("Andy");
       s1.setlName("Student");
       s1.setBirthday("2000-08-21");
       s1.setUserName("AndStu2720");

       String str2 = createUserString(s1);




       System.out.print(str2+"\n");
       HashMap<String, Object> hm = createUUID(str2);

       UUID uuid = (UUID)hm.get("uuid");
       System.out.println(uuid.toString());
       String front = (String)hm.get("front");
       System.out.print("Front "+front+"\n");

       String str3 = recreateUserString(hm);
       System.out.print(str3+"\n");
       if(str2.equals(str3)){
    	   System.out.print(" String test passed\n");
       }
       else{
    	   System.out.print(" String test failed\n");
       }

       String ms53File = "C:\\User-matriculation\\import_files\\ms_53\\ms_53_test.xlsx";
       String ms53Template = "C:\\User-matriculation\\import_files\\ms_53\\ms_53_template.xlsx";
       ArrayList<User>  ms53Users = convertMS53File(ms53File, 21);
       createNewReportTemplate(ms53Users, ms53Template);

       String username = "akirk234";
       String hash = generateHash(username);
       System.out.println("hash "+hash);
       **/
       String out = "C:\\student-matriculation\\import_files\\testing\\student_info.xlsx";
	   String grade = "9";

	   createUserInfoFile(out, grade);



	}




}
