package recomendation;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

public class ManageIndividualRecommendations {

	static String dbURL_test ="";
	static String database_test ="test_data";
    static String dbUser_test = "test_data";
	static String dbPass_test = "test_data";
	static String resource_database_test ="test_data";
	static String test_userDatabase = "test_data";


	static String dbURL_pixolio ="";
	static String database_pixolio ="test_data";
    static String dbUser_pixolio = "test_data";
	static String dbPass_pixolio = "test_data";
	static String resource_database_pixolio ="test_data";
	static String pixolio_userDatabase = "test_data";

	static String dbURL_prod ="";
	static String database_prod ="test_data";
    static String dbUser_prod = "test_data";
	static String dbPass_prod = "test_data";
	static String resource_database_prod ="test_data";
	static String prod_userDatabase = "test_data";

	static String dbURL_stage ="";
	static String database_stage ="test_data";
    static String dbUser_stage = "test_data";
	static String dbPass_stage = "test_data";
	static String resource_database_stage ="test_data";
	static String stage_userDatabase = "test_data";



	static String sage = "8-12";
	static String stype = "student";
	static int studentInt = 1;
	static int parentInt = 2;
	static int staffInt = 3;
	static int modInt = 4;
	static int staff_involved_int = 5;
    static int studentContent = 2;
    static int parentContent = 3;
    static int staffContent = 5;
    static int modContent = 7;
    static String adultAge = "18+";
	static String parentType = "parent";
	static String staffType = "staff";
	static String modType = "moderator";
	static String sub1 = "digital public";
	static String sub2 = "race";


//This function is called by the cron to update the Recommendation database.
public static void main(String[] args) {



		String environment = "";
		String org = "";
		String userDatabase = "";
		HashMap<String,String> orgIDs = new HashMap<String,String>();
		orgIDs.put("test","110");
		orgIDs.put("pixolio","110");
		orgIDs.put("ervine_test","1");
		orgIDs.put("ervine_stage","1");
		orgIDs.put("bayside_stage","110");


		//production environments
		orgIDs.put("ervine","15");
		orgIDs.put("new_visions","17");
		orgIDs.put("boody","18");
		orgIDs.put("albertus","23");
		orgIDs.put("ms53","24");
		orgIDs.put("is281","25");
		orgIDs.put("humes","26");
		orgIDs.put("east_bronx","27");
		orgIDs.put("brooklyn_studio","28");
		orgIDs.put("meca","30");
		orgIDs.put("dewey","31");
		orgIDs.put("mott","32");
		orgIDs.put("bronxdale","33");
		orgIDs.put("riverside","22");

		HashMap<String,String> orgEnv = new HashMap<String,String>();
		orgEnv.put("test","test");
		orgEnv.put("pixolio","pixolio");
		orgEnv.put("ervine_test","pixolio");
		orgEnv.put("ervine","production");
		orgEnv.put("new_visions","production");
		orgEnv.put("boody","production");
		orgEnv.put("albertus","production");
		orgEnv.put("ms53","production");
		orgEnv.put("is281","production");
		orgEnv.put("humes","production");
		orgEnv.put("brooklyn_studio","production");
		orgEnv.put("meca","production");
		orgEnv.put("dewey","production");
		orgEnv.put("mott","production");
		orgEnv.put("bronxdale","production");
		orgEnv.put("east_bronx","production");
		orgEnv.put("riverside","production");
		orgEnv.put("ervine_stage","stage");
		orgEnv.put("bayside_stage","stage");

		/**
		if (args.length != 2){
			System.out.println("Please an environment");
			return;
		}
		else{
			environment = args[0];
			org = args[1];
			System.out.println("Organization ID "+environment);
		}
		if(!StringUtils.isNumeric(org.trim())){
			System.out.println("Organization ID must be numeric");
			return;
		}
		**/

		String env = args[0];
	    System.out.println("Organization ENV "+env);
		environment = orgEnv.get(env.trim());
		System.out.println("Organization Environment "+environment);
		org = orgIDs.get(env.trim());

		try {
			// load the database driver (make sure this is in your classpath!)
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("mysql class loaded");

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		String fullDatabase = "";
	    String resource_database = "";
	    String orgDatabase = "";

		DBConnection db = new DBConnection();
		if(environment.trim().equalsIgnoreCase("test")){
			fullDatabase = dbURL_test+database_test+org;
			db.setJdbcUrl(fullDatabase);
			db.setUserPassword(dbPass_test);
			db.setUserName(dbUser_test);
			resource_database = resource_database_test;
			userDatabase = test_userDatabase;
			orgDatabase = database_test+org.trim();
		}
		else if(environment.trim().equalsIgnoreCase("pixolio")){
			fullDatabase = dbURL_pixolio+database_pixolio+org;
			db.setJdbcUrl(fullDatabase);
			db.setUserPassword(dbPass_pixolio);
			db.setUserName(dbUser_pixolio);
			resource_database = resource_database_pixolio;
			userDatabase = pixolio_userDatabase;
			orgDatabase = database_pixolio+org.trim();
		}
		else if(environment.trim().equalsIgnoreCase("production")){
			fullDatabase = dbURL_prod+database_prod+org;
			db.setJdbcUrl(fullDatabase);
			db.setUserPassword(dbPass_prod);
			db.setUserName(dbUser_prod);
			resource_database = resource_database_prod;
			userDatabase = prod_userDatabase;
			orgDatabase = database_prod+org.trim();
		}
		else if(environment.trim().equalsIgnoreCase("stage")){
			fullDatabase = dbURL_stage+database_stage+org;
			db.setJdbcUrl(fullDatabase);
			db.setUserPassword(dbPass_stage);
			db.setUserName(dbUser_stage);
			resource_database = resource_database_stage;
			userDatabase = stage_userDatabase;
			orgDatabase = database_stage+org.trim();
		}



		BoneCPConfig config = db.newBPConfig();
		BoneCP bcp = db.newConnectionPool(config);
		if(bcp == null){
			System.out.println("Connection is Null");
			return;
		}

		Connection con = db.newConnection(bcp);
		/**
		boolean flag = Recommendation.truncateTable(con, parentInt, resource_database);
		if(flag){
			System.out.println("Table Trunctated");
		}
		else{
			System.out.println("Table Not Trunctated");
		}
		**/

		HashMap <Integer, ArrayList<Integer>> users = Recommendation.getUsers(con);




		   if(users.size() > 0){
			   Iterator<Integer> user_ids  = users.keySet().iterator();
			   HashMap<Integer, ArrayList<Integer>> parents = new HashMap<Integer, ArrayList<Integer>>();

			   while(user_ids.hasNext()){

				   Integer test = user_ids.next();
				   int userType = Recommendation.getUserType(con, test.intValue(), userDatabase);
				   if(userType == 1){
					   ArrayList<Integer> parent_ids = Recommendation.getParentIds(con, test.intValue(), userDatabase);
					   for(int i = 0; i < parent_ids.size(); i++){
						   parents.put(parent_ids.get(i), users.get(test));
					   }

				   }

				   int weightType = Recommendation.getWeightKey(userType);
				   HashMap<Integer, Float> weights = Recommendation.generateWeights(weightType);


				   ArrayList <Integer> reports = users.get(test);
				   HashMap<Integer, ArrayList<String>> userReportSubjects = Recommendation.getReportSubjects(con, reports, test, orgDatabase);
				   HashMap<Integer, ArrayList<String>> userReportTypes = Recommendation.getReportTypes(con, reports, test, orgDatabase);
				   ArrayList<String> subjects = userReportSubjects.get(test);
				   String subFields = "";
				   if(subjects.size() < 1){
					 subjects.add(sub1);
					 subjects.add(sub2);
				   }
				   subFields = Recommendation.formatIn(subjects);

				   ArrayList<String> types = userReportTypes.get(test);
				   String typeFields = Recommendation.formatIn(types);
				   HashMap<Integer,Float> biasWeights = Recommendation.getSubjectBiasList(con, subFields, new Float(1.0), resource_database);
				   Iterator<Integer> bw = biasWeights.keySet().iterator();
				   ArrayList<Integer> biases = new ArrayList<Integer>();
				   while (bw.hasNext()){
					   Integer s = bw.next();
					   biases.add(s);
				   //System.out.print("Subject "+s+" "+biasWeights.get(s)+"\n");
				   }
				   HashMap<Integer,Float> typeWeights = Recommendation.getSubjectTypeList(con, typeFields, new Float(1.0), resource_database);
				   Iterator<Integer> tw = typeWeights.keySet().iterator();
				   ArrayList<Integer> typeList = new ArrayList<Integer>();
				   while (tw.hasNext()){
					   Integer t = tw.next();
					   typeList.add(t);
					   //System.out.print("Type "+t+" "+typeWeights.get(t)+"\n");
				   }
				   float f = 0.75f;
				   HashMap<Integer, Float> combinedWeights = Recommendation.addWeights(biases, typeList,f);




				   HashMap<Integer, Float> contentWeights = Recommendation.getContentItems(con, combinedWeights, sage, stype, weights, resource_database);



				   if(contentWeights.size() < 25){

					   int org_id = Integer.parseInt(org);
					   HashMap<Integer, Float> allContent = Recommendation.getAdditionalItems(con,org_id,studentInt, resource_database);
					   contentWeights = Recommendation.addAdditionalItems(contentWeights,allContent);
				   }

				   Recommendation.addAllIndContentWeights(con,contentWeights,test.intValue(), resource_database);
				   if(parents.size() > 0){
					   Iterator<Integer> pi = parents.keySet().iterator();
					   while(pi.hasNext()){

						   HashMap<Integer, Float> parentWeights = Recommendation.generateWeights(parentContent);
						   HashMap<Integer, Float> pContentWeights = Recommendation.getContentItems(con, combinedWeights, adultAge, parentType, parentWeights, resource_database);
						   if(pContentWeights.size() < 25){
							   int org_id = Integer.parseInt(org);

							   HashMap<Integer, Float> allContent = Recommendation.getAdditionalItems(con,org_id,parentInt,resource_database);

							   contentWeights = Recommendation.addAdditionalItems(contentWeights,allContent);
						   }

						   int parentId = pi.next().intValue();
						   Recommendation.addAllIndContentWeights(con,contentWeights,parentId,resource_database);

					   }
				  }




			   }

		   }
		   HashMap <Integer, ArrayList<Integer>> staffUsers = Recommendation.getStaffUsers(con);
		   if(staffUsers.size() > 0){

			   System.out.println("Staff User Size:  "+staffUsers.size());
			   Iterator<Integer> staff_user_ids  = staffUsers.keySet().iterator();


			   while(staff_user_ids.hasNext()){

				   Integer staffId = staff_user_ids.next();


				   int weightType = Recommendation.getWeightKey(staff_involved_int);
				   HashMap<Integer, Float> weights = Recommendation.generateWeights(weightType);


				   ArrayList <Integer> reports = staffUsers.get(staffId);
				   HashMap<Integer, ArrayList<String>> userReportSubjects = Recommendation.getReportSubjects(con, reports, staffId,orgDatabase);
				   HashMap<Integer, ArrayList<String>> userReportTypes = Recommendation.getReportTypes(con, reports, staffId,orgDatabase);
				   ArrayList<String> subjects = userReportSubjects.get(staffId);
				   String subFields = "";

				   if(subjects.size() < 1){
					  subjects.add(sub1);
					  subjects.add(sub2);
				   }
				   subFields = Recommendation.formatIn(subjects);


				   ArrayList<String> types = userReportTypes.get(staffId);
				   String typeFields = Recommendation.formatIn(types);
				   HashMap<Integer,Float> biasWeights = Recommendation.getSubjectBiasList(con, subFields, new Float(1.0), resource_database);
				   Iterator<Integer> bw = biasWeights.keySet().iterator();
				   ArrayList<Integer> biases = new ArrayList<Integer>();
				   while (bw.hasNext()){
					   Integer s = bw.next();
					   biases.add(s);
				   //System.out.print("Subject "+s+" "+biasWeights.get(s)+"\n");
				   }
				   HashMap<Integer,Float> typeWeights = Recommendation.getSubjectTypeList(con, typeFields, new Float(1.0), resource_database);
				   Iterator<Integer> tw = typeWeights.keySet().iterator();
				   ArrayList<Integer> typeList = new ArrayList<Integer>();
				   while (tw.hasNext()){
					   Integer t = tw.next();
					   typeList.add(t);
					   //System.out.print("Type "+t+" "+typeWeights.get(t)+"\n");
				   }
				   float f = 0.75f;
				   HashMap<Integer, Float> combinedWeights = Recommendation.addWeights(biases, typeList,f);




				   HashMap<Integer, Float> contentWeights = Recommendation.getContentItems(con, combinedWeights, adultAge, staffType, weights, resource_database);



				   if(contentWeights.size() < 25){

					   int org_id = Integer.parseInt(org);
					   HashMap<Integer,Float> allContent = Recommendation.getAdditionalItems(con,org_id,staffInt, resource_database);
					   contentWeights = Recommendation.addAdditionalItems(contentWeights,allContent);
				   }

				   Recommendation.addAllIndContentWeights(con,contentWeights,staffId.intValue(),resource_database);





			   }




		   }






	}


}
