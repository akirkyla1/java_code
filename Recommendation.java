package recomendation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class Recommendation {

	private static final String sq = "'";
	private static final String comma = ",";

	//gets date range to be used
	public static String getDateRange(int offset){
		String formattedDate = "";
		String format = "yyyy-MM-dd";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -offset);
		SimpleDateFormat sf = new SimpleDateFormat(format);
		String sb = sf.format(new Date(cal.getTimeInMillis()));
		formattedDate = sb;

		return formattedDate;
	}
	//gets a count of incidents broken down by type
	public static HashMap <String,Float> getRelatedCounts(Connection con, int type, String database){
		HashMap<String,Float> hm = new HashMap<String,Float>();
		StringBuilder sb = new StringBuilder();
		String front = "select case incident_related_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'National Origin'";
		String when2 = " when 2 then 'Race'";
		String when3 = " when 3 then 'Weight'";
		String when4 = " when 4 then 'Religion'";
		String when5 = " when 5 then 'Disability'";
		String when6 = " when 6 then 'Gender'";
		String when7 = " when 7 then 'Sexual Orientation'";
		String when8 = " when 8 then 'Sexuality'";
		String when9 = " when 9 then 'Ethnic Group'";
		String when10 = " when 10 then 'Socioeconomic Status'";
		String when11 = " when 11 then 'Religion'";
		String when12 = " when 12 then 'Sex'";
		String when20 = " when 20 then 'None of the above'";
		String end = " end as incident, count(report_id) as count";
		String from =  " from "+database+".report_incident_related";
		String where = " where date_created >= str_to_date('";
		String where2 = "', '%Y-%m-%d')";
		String where3 = " and date_created < str_to_date('";
		String group_by = " group by incident_related_id";

		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(when8);
		sb.append(when9);
		sb.append(when10);
		sb.append(when11);
		sb.append(when12);
		sb.append(when20);
		sb.append(end);
		sb.append(from);
		sb.append(where);
		String date1 = getDateRange(30);

		if(type == 1){
			sb.append(date1);
			sb.append(where2);
		}
		else if(type == 2){
			String date2 = getDateRange(60);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date1);
			sb.append(where2);

		}
		else if(type == 3){
			String date3 = getDateRange(60);
			String date2 = getDateRange(90);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date3);
			sb.append(where2);

		}
		else if(type == 4){
			String date3 = getDateRange(90);
			String date2 = getDateRange(120);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date3);
			sb.append(where2);

		}

		sb.append(group_by);
		String query = sb.toString();

		//System.out.println(query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String incident = rs.getString("incident");
					Float count = new Float(rs.getInt("count"));
					hm.put(incident, count);
				}

			}


		}
		catch (SQLException e){

		}
		catch (Exception e){

		}


		return hm;


	}
	//gets counts of bullying types
	public static HashMap <String,Float> getTypeCounts(Connection con, int type, String database){
		HashMap<String,Float> hm = new HashMap<String,Float>();
		StringBuilder sb = new StringBuilder();
		String front = "select case incident_category_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'Physical'";
		String when2 = " when 2 then 'Verbal'";
		String when3 = " when 3 then 'Theft'";
		String when4 = " when 4 then 'Vandalism'";
		String when5 = " when 5 then 'Digital Public'";
		String when6 = " when 6 then 'Digital Private'";
		String when7 = " when 7 then 'Exclusion'";

		String when20 = " when 20 then 'None of the above'";
		String end = " end as incident, count(report_id) as count";
		String from =  " from "+database+".report_incident_category";
		String where = " where date_created >= str_to_date('";
		String where2 = "', '%Y-%m-%d')";
		String where3 = " and date_created < str_to_date('";
		String group_by = " group by incident_category_id";

		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(when20);
		sb.append(end);
		sb.append(from);
		sb.append(where);
		String date1 = getDateRange(30);

		if(type == 1){
			sb.append(date1);
			sb.append(where2);
		}
		else if(type == 2){
			String date2 = getDateRange(60);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date1);
			sb.append(where2);

		}
		else if(type == 3){
			String date3 = getDateRange(60);
			String date2 = getDateRange(90);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date3);
			sb.append(where2);

		}
		else if(type == 4){
			String date3 = getDateRange(90);
			String date2 = getDateRange(120);
			sb.append(date2);
			sb.append(where2);
			sb.append(where3);
			sb.append(date3);
			sb.append(where2);

		}

		sb.append(group_by);
		String query = sb.toString();
		//System.out.println(query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String incident = rs.getString("incident");
					Float count = new Float(rs.getInt("count")*.5);
					hm.put(incident, count);
				}

			}


		}
		catch (SQLException e){

		}
		catch (Exception e){

		}


		return hm;


	}
	//creates a weight for the individual content
	public static HashMap <String,Float> addWeights(HashMap<String,Float> base,
				HashMap<String,Float> additional, float weight ){
		Iterator<String> iterator = base.keySet().iterator();
		while (iterator.hasNext()) {
			   String key = iterator.next().toString();
			   Float value = base.get(key);
			   if(additional.containsKey(key)){
				   Float addF = additional.get(key);
				   float weightF = addF.floatValue()*weight;
				   float newWeight = value.floatValue()+weightF;
				   base.put(key, new Float(newWeight));
			   }


			}



		return base;

	}
	//sorts the weights
	public static HashMap <Float, ArrayList<String> > sortWeights
	(HashMap <String,Float> values){

		HashMap <Float, ArrayList<String> > sorted = new HashMap<Float,ArrayList<String>>();
		Iterator <String> iterator = values.keySet().iterator();


		while(iterator.hasNext()){
			String key = iterator.next();
			Float f = values.get(key).floatValue();
			if(f.floatValue() != 0.0){
				if(sorted.containsKey(f)){
					ArrayList<String> as = sorted.get(f);
					as.add(key);
				}
				else{
					ArrayList<String> as = new ArrayList<String>();
					as.add(key);
					sorted.put(f, as);
				}
			}
		}
		ArrayList<Float> keys = new ArrayList<Float>(sorted.keySet());
		Collections.sort(keys);
		Collections.reverse(keys);

	    List<Float> trimmed;

		if(keys.size() > 10){
			trimmed = keys.subList(0, 10);
		}
		else{
			trimmed = keys;
		}
		HashMap<Float, ArrayList<String>> hm = new HashMap<Float,ArrayList<String>>();

		for(int i = 0; i < trimmed.size(); i++ ){
			Float f = trimmed.get(i);
			ArrayList<String> al = sorted.get(f);
			hm.put(f, al);
		}




		return hm;
	}
	//fornats in clause
	public static String formatIn(ArrayList <String> values){
		StringBuilder sb = new StringBuilder();

		if(values.size() > 0){
			for (int i = 0; i < values.size()-1; i++ ){
				sb.append(sq);
				sb.append(values.get(i));
				sb.append(sq);
				sb.append(comma);
			}
			sb.append(sq);
			sb.append(values.get(values.size()-1));
			sb.append(sq);
		}
		else{
			sb.append(sq);
			sb.append(sq);
		}

		return sb.toString().toLowerCase();

	}
	//formats interger in clause
	public static String formatInInt(ArrayList <Integer> values){
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < values.size()-1; i++ ){
			sb.append(values.get(i));
			sb.append(comma);
		}
		sb.append(values.get(values.size()-1));

		return sb.toString().toLowerCase();

	}

	//returns a list of the top ten incident types
	public static HashMap<Float, String> getTopFields(HashMap<Float,ArrayList <String> > hm){

		HashMap<Float, String> values = new HashMap<Float, String>();
		ArrayList<Float> keys = new ArrayList<Float>(hm.keySet());
		Collections.sort(keys);
		Collections.reverse(keys);

	    List<Float> trimmed;

		if(keys.size() > 10){
			trimmed = keys.subList(0, 10);
		}
		else{
			trimmed = keys;
		}

		for(int i = 0; i < trimmed.size(); i++ ){
			Float f = trimmed.get(i);
			ArrayList <String> temp = hm.get(f);
			String value = formatIn(temp);
			values.put(f, value);

		}

		return values;


	}
	//returns subject list
	public static HashMap<Integer, Float>  getSubjectTypeList(Connection con, String list, Float weight, String database){
		HashMap<Integer, Float> subjects = new HashMap<Integer,Float>();

		StringBuilder sb = new StringBuilder();
		sb.append("select subject_id ");
		sb.append("from "+database+".type_subject ts, "+database+".report_type rt ");
		sb.append("where ts.type_id = rt.rec_id ");
		sb.append("and lower(rt.text) in (");
		sb.append(list);
		sb.append(")");
		String query = sb.toString();
		System.out.println("Query "+query);

		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			if(rs != null){
				while(rs.next()){

					Integer i = new Integer(rs.getInt("subject_id"));
					subjects.put(i,weight);
					//System.out.println("i "+i);

				}
			}
			else{
				System.out.println("RS is null");

			}


		}
		catch (SQLException e){
			System.out.println("Error 1"+e.toString());

		}
		catch (Exception e){
			System.out.println("Error 2"+e.toString());
		}


		return subjects;

	}
	//returns biasd list
	public static HashMap<Integer, Float> getSubjectBiasList(Connection con, String list, Float weight, String database){
		HashMap <Integer, Float> hm = new HashMap<Integer, Float>();
		StringBuilder sb = new StringBuilder();
		sb.append("select subject_id ");
		sb.append("from "+database+".bias_subject ts, "+database+".bias_type rt ");
		sb.append("where ts.bias_id = rt.rec_id ");
		sb.append("and lower(rt.text) in (");
		sb.append(list);
		sb.append(")");
		String query = sb.toString();
		System.out.println("Query "+query);

		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			if(rs != null){
				while(rs.next()){

					Integer i = new Integer(rs.getInt("subject_id"));
					System.out.println("Subject  "+i+"\n");
				    hm.put(i, weight);


				}
			}
			else{
				System.out.println("RS is null");

			}


		}
		catch (SQLException e){
			System.out.println("Error 1"+e.toString());

		}
		catch (Exception e){
			System.out.println("Error 2"+e.toString());
		}
		return hm;


	}
	//add weights to content
	public static HashMap<Integer, Float> addWeights(ArrayList<Integer> bias, ArrayList<Integer> type, float adjustment ){
		HashMap <Integer, Float> hm = new HashMap<Integer,Float>();
		for(int i = 0; i < bias.size(); i++){
			hm.put(bias.get(i), new Float(1.0) );
		}
		for(int j = 0; j < type.size(); j++){
			if(hm.containsKey(type.get(j))){
				Float f = new Float(1.0f+adjustment);
				hm.put(type.get(j), f);
			}
		}
		return hm;

	}
	//gets subjects by category
	public static HashMap<Integer,Float> getSubjectsByCategory(Connection con,
			HashMap <Float, ArrayList<String>>hm, int type, String database ){

		HashMap<Integer, Float> thm = new HashMap<Integer,Float >();

		Iterator<Float> i = hm.keySet().iterator();
		while (i.hasNext()){
			Float f = i.next();
			ArrayList<String> temp = hm.get(f);
			String terms = Recommendation.formatIn(temp);

			if(type == 1){
				HashMap<Integer, Float> btemp = getSubjectBiasList(con, terms, f, database);
				Iterator<Integer> it = btemp.keySet().iterator();
				while(it.hasNext()){
					Integer bi = it.next();
					Float b = btemp.get(bi);
					thm.put(bi, b);
				}

			}
			else{
				HashMap<Integer, Float> ttemp = getSubjectTypeList(con, terms, f, database);
				Iterator<Integer> tt = ttemp.keySet().iterator();
				while(tt.hasNext()){
					Integer bi = tt.next();
					Float b = ttemp.get(bi);
					thm.put(bi, b);
				}

			}


		}

		return thm;
	}
	//combines weights for each addContentScore
	public static HashMap<Float, ArrayList<Integer>> combineWeights
		(HashMap<Float, ArrayList<Integer>> biases, HashMap<Float, ArrayList<Integer>> types, Float weight){
		HashMap<Float, ArrayList<Integer>> combined = new HashMap<Float, ArrayList<Integer>> ();


		Iterator<Float> biasKeys = biases.keySet().iterator();

		while(biasKeys.hasNext()){
			Float f = biasKeys.next();
			ArrayList<Integer> al = biases.get(f);
			Iterator<Float> typeKeys = types.keySet().iterator();
			while(typeKeys.hasNext()){
				Float f2 = typeKeys.next();
				ArrayList<Integer> al2 = types.get(f2);
				for (int i = 0; i < al.size(); i++){
					Integer si = al.get(i);
					if(al2.contains(si)){
						Float nf = f*weight;
						if(combined.containsKey(nf)){
							ArrayList<Integer> al3 = combined.get(nf);
							al3.add(si);
							combined.put(nf, al3);
							al2.remove(si);
							al.remove(si);
						}
						else{
							ArrayList<Integer> al3 = new ArrayList<Integer>();
							al3.add(si);
							combined.put(f, al3);
							al2.remove(si);
							al.remove(si);
						}
					}
					biases.put(f, al);
					types.put(f2, al2);
				}
			}
		}
		Iterator<Float> typeKeys = types.keySet().iterator();
		Iterator<Float> biasKeys1 = biases.keySet().iterator();

		while (biasKeys1.hasNext()){
			Float f4 = biasKeys1.next();
			ArrayList<Integer> al3 = biases.get(f4);
			System.out.print("Weight Bias Key "+f4+" Values "+al3);
			System.out.print("\n");

			if(combined.containsKey(f4)){
				ArrayList<Integer> al4 = combined.get(f4);
				al4.addAll(al3);
			}
			else{
				combined.put(f4, al3);
			}

		}
		while (typeKeys.hasNext()){
			Float f3 = typeKeys.next();
			ArrayList<Integer> al3 = types.get(f3);
			if(combined.containsKey(f3)){
				ArrayList<Integer> al4 = combined.get(f3);
				al4.addAll(al3);
			}
			else{
				combined.put(f3, al3);
			}

		}

		return combined;

	}
	//gets content related tro type and bias
	public static HashMap<Integer, Float> getContentItems(Connection con, HashMap<Integer, Float> subs,
			String age, String type, HashMap<Integer, Float> types, String database ){
		HashMap<Integer, Float> hm = new HashMap<Integer, Float>();
		StringBuilder in = new StringBuilder();
		Iterator<Integer> is = subs.keySet().iterator();
		while(is.hasNext()){
			Integer is1 = is.next();
			in.append(is1.intValue());
			in.append(comma);
		}
		String inVal = in.toString();
		String finalIn ="";
	    if(inVal.length() > 1){
			finalIn = inVal.substring(0,inVal.length()-1);
	    }
		StringBuilder tin = new StringBuilder();
		Iterator<Integer> it = types.keySet().iterator();
		while(it.hasNext()){
			int typeInt = it.next().intValue();
			tin.append(typeInt);
			tin.append(comma);
		}
		String typeS = tin.substring(0, tin.length()-1);



		StringBuilder sb = new StringBuilder();
		String select = "select c.rec_id, c.type, cs.subject_id";
		String from = " from "+database+".content c, "+database+".content_subject cs";
		String where = " where c.target_age like '%";
		sb.append(select);
		sb.append(from);
		sb.append(where);
		sb.append(age);
		sb.append("%'");
		String subIn = " and  cs.subject_id in (";
		sb.append(subIn);
		sb.append(finalIn);
		sb.append(") ");
		String typeIn = " and  c.type in (";
		sb.append(typeIn);
		sb.append(typeS);
		sb.append(") ");
		String userType = " and lower(c.target_group) like '%";
		sb.append(userType);
		sb.append(type);
		sb.append("%'");
		String finalAnd = " and cs.content_id = c.rec_id";
		sb.append(finalAnd);
		String query = sb.toString();
		//System.out.println("Content Query: "+query);

		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			if(rs != null){


				while(rs.next()){
					Integer cid = new Integer(rs.getInt("rec_id"));
					Integer ctype = new Integer(rs.getInt("type"));
					Integer csub = new Integer(rs.getInt("subject_id"));




				    if(hm.containsKey(cid)){
				    	Float old = hm.get(cid);
				    	Float newF = subs.get(csub)*types.get(ctype);
				    	//System.out.println("If Weight "+"Content ID "+cid+"Weight "+newF);
				    	hm.put(cid, formatFloat(old+newF));
				    }
				    else{
				    	Float totalWeight = subs.get(csub)*types.get(ctype);
				    	hm.put(cid, formatFloat(totalWeight));
				    	//System.out.println("Else Weight "+"Content ID "+cid+"Weight "+totalWeight);
				    }

				}
			}
			else{
				System.out.println("RS is null");

			}


		}
		catch (SQLException e){
			System.out.println("Error 1"+e.toString());

		}
		catch (Exception e){
			System.out.println("Error 2"+e.toString());
		}






		return hm;

	}
	//returns weights in xxx.xx format
	public static Float formatFloat(Float valueToFormat) {
	    float rounded = Math.round(valueToFormat*100);
	    return new Float(rounded/100.0);
	 }
	public static boolean addContentScore(Connection con, int org_id, int user_type, int content_id, float score, String database){
		int flag = 0;
		StringBuilder sb = new StringBuilder();
		String insert = "insert into "+database+".recommended_content (org_id,user_type,content_id,weight) values(";
		sb.append(insert);
		sb.append(org_id);
		sb.append(comma);
		sb.append(user_type);
		sb.append(comma);
		sb.append(content_id);
		sb.append(comma);
		sb.append(score);
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
	//adds individual content to database
	public static boolean addIndividualContentScore(Connection con, int user_id, int content_id, float score, String database){
		int flag = 0;
		StringBuilder sb = new StringBuilder();
		String insert = "insert into "+database+".recommended_user_content (user_id,content_id,weight) values(";
		sb.append(insert);
		sb.append(user_id);
		sb.append(comma);
		sb.append(content_id);
		sb.append(comma);
		sb.append(score);
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
	//adds school wide content
	public static boolean addAllIndContentWeights(Connection con, HashMap<Integer, Float> scores, int user_id, String database){
		int flag = 0;
		Iterator<Integer> cs = scores.keySet().iterator();
		while(cs.hasNext()){
			Integer csi = cs.next();
			Float f = scores.get(csi);
			if(addIndividualContentScore(con,user_id,csi.intValue(),f.floatValue(), database)){
				flag++;
			}

		}



		return flag > 0;
	}

	// adds all individual content weight
	public static boolean addAllContentWeights(Connection con, HashMap<Integer, Float> scores, int org_id, int user_type, String database){
		int flag = 0;
		Iterator<Integer> cs = scores.keySet().iterator();
		while(cs.hasNext()){
			Integer csi = cs.next();
			Float f = scores.get(csi);
			if(addContentScore(con,org_id,user_type,csi.intValue(),f.floatValue(),database )){
				flag++;
			}

		}



		return flag > 0;
	}
	//gets all users who filed a report
	public static HashMap<Integer, ArrayList<Integer>> getUsers(Connection con ){
		HashMap<Integer,ArrayList<Integer>> users = new HashMap<Integer, ArrayList<Integer> >();

		StringBuilder sb = new StringBuilder();
		String select = "select ru.user_id, ru.report_id  ";
		String from = " from report_user ru  ";
		String where = " where ru.date_created >= str_to_date('";

		sb.append(select);
		sb.append(from);
		sb.append(where);
		String date1 = getDateRange(30);
		sb.append(date1);
		String where1 = "', '%Y-%m-%d')";
		sb.append(where1);
		String query = sb.toString();
		System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					Integer i = new Integer(rs.getInt(1));
					Integer r = new Integer(rs.getInt(2));
					System.out.println("Report "+r+"\n");
					if(users.containsKey(i)){
						ArrayList<Integer> reports = users.get(i);
						System.out.println("Report1  "+r+"\n");
						if(!reports.contains(r)){
							reports.add(r);
						}
						users.put(i, reports);
						System.out.println("Reports "+reports+"\n");
					}
					else{
						System.out.println("Report2  "+r+"\n");
					   ArrayList<Integer> reports = new ArrayList<Integer>();
					   reports.add(r);
					   users.put(i, reports);
					   System.out.println("Reports2 "+reports+"\n");
					}


				}

			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}



		return users;
	}
	//get individual reports
	public static ArrayList<Integer> getUserReports(Connection con, int user_id){
		ArrayList<Integer> reports = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		String select = "select ru.report_id from report_user ru ";
		String where = " where ru.user_id = ";
		sb.append(select);
		sb.append(where);
		sb.append(user_id);
		String and2 = " and r.date_created >= str_to_date('";
		sb.append(and2);
		String date1 = getDateRange(30);
		sb.append(date1);
		String and3 = "', '%Y-%m-%d')";
		sb.append(and3);
		String query = sb.toString();
		//System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					Integer i = new Integer(rs.getInt(1));
					reports.add(i);
				}

			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return reports;
	}
	//gets roprt count by subjects
    public static HashMap<Integer,ArrayList<String>> getReportSubjects(Connection con, ArrayList<Integer> reports, Integer user_id, String database){


    	ArrayList<String> subjects = new ArrayList<String>();
    	HashMap<Integer,ArrayList<String>> user_subjects = new HashMap<Integer,ArrayList<String>>();

    	String reportsStr = formatInInt(reports);
    	StringBuilder sb = new StringBuilder();
		String front = "select case incident_related_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'National Origin'";
		String when2 = " when 2 then 'Race'";
		String when3 = " when 3 then 'Weight'";
		String when4 = " when 4 then 'Religion'";
		String when5 = " when 5 then 'Disability'";
		String when6 = " when 6 then 'Gender'";
		String when7 = " when 7 then 'Sexual Orientation'";
		String when8 = " when 8 then 'Sexuality'";
		String when9 = " when 9 then 'Ethnic Group'";
		String when10 = " when 10 then 'Socioeconomic Status'";
		String when11 = " when 11 then 'Religion'";
		String when12 = " when 12 then 'Sex'";
		String when20 = " when 20 then 'None of the above'";
		String end = " end as incident";
		String from =  " from "+database+".report_incident_related ";
		String where = " where report_id in (";
		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(when8);
		sb.append(when9);
		sb.append(when10);
		sb.append(when11);
		sb.append(when12);
		sb.append(when20);
		sb.append(end);
		sb.append(from);
		sb.append(where);
		sb.append(reportsStr);
		sb.append(")");
		String query = sb.toString();
		System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String subject = rs.getString(1);
					if(!subjects.contains(subject)){
						subjects.add(subject);
					}
				}

			}
			user_subjects.put(user_id, subjects);


		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}

    	return user_subjects;
    }
// gets report counts by type
public static HashMap<Integer,ArrayList<String>>getReportTypes(Connection con, ArrayList<Integer> reports, Integer user_id, String database){

    	ArrayList<String> types = new ArrayList<String>();
    	HashMap<Integer, ArrayList<String>> user_types = new HashMap<Integer, ArrayList<String>>();

    	StringBuilder sb = new StringBuilder();
    	String reportsStr = formatInInt(reports);
    	String front = "select case incident_category_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'Physical'";
		String when2 = " when 2 then 'Verbal'";
		String when3 = " when 3 then 'Theft'";
		String when4 = " when 4 then 'Vandalism'";
		String when5 = " when 5 then 'Digital Public'";
		String when6 = " when 6 then 'Digital Private'";
		String when7 = " when 7 then 'Exclusion'";
		String end = " end as incident";
		String from =  " from "+database+".report_incident_category";
		String where = " where report_id in (";
		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(end);
		sb.append(from);
		sb.append(where);
		sb.append(reportsStr);
		sb.append(")");
		String query = sb.toString();
		System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String subject = rs.getString(1);
					if(!types.contains(subject)){
						types.add(subject);
					}
				}

			}
			user_types.put(user_id, types);

		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}


    	return  user_types;
    }
// sort indsividual wieghts
	public static HashMap <Float, ArrayList<Integer> > sortIndividualWeights
		(HashMap <Integer,Float> values){

	HashMap <Float, ArrayList<Integer> > sorted = new HashMap<Float,ArrayList<Integer>>();
	Iterator <Integer> iterator = values.keySet().iterator();


	while(iterator.hasNext()){
	    Integer key = iterator.next();
		Float f = values.get(key).floatValue();
		if(f.floatValue() != 0.0){
			if(sorted.containsKey(f)){
				ArrayList<Integer> as = sorted.get(f);
				as.add(key);
			}
			else{
				ArrayList<Integer> as = new ArrayList<Integer>();
				as.add(key);
				sorted.put(f, as);
			}
		}
	}
	ArrayList<Float> keys = new ArrayList<Float>(sorted.keySet());
	Collections.sort(keys);
	Collections.reverse(keys);

    List<Float> trimmed;

	if(keys.size() > 6){
		trimmed = keys.subList(0, 6);
	}
	else{
		trimmed = keys;
	}
	HashMap<Float, ArrayList<Integer>> hm = new HashMap<Float,ArrayList<Integer>>();

	for(int i = 0; i < trimmed.size(); i++ ){
		Float f = trimmed.get(i);
		ArrayList<Integer> al = sorted.get(f);
		hm.put(f, al);
	}




	return hm;
}
 // gets additional content
	public static HashMap<Integer,Float> getAdditionalItems(Connection con, int org_id, int type, String database){
		HashMap<Integer,Float> addedValues = new HashMap<Integer,Float>();
		StringBuilder sb = new StringBuilder();
		String select = "Select distinct content_id, weight ";
		String from = "from "+database+".recommended_content ";
		sb.append(select);
		sb.append(from);
		String where = "where user_type = ";
		sb.append(where);
		sb.append(type);
		String where2 = " and org_id = ";
		sb.append(where2);
		sb.append(org_id);
	    String order_by = " Order by weight desc ";
	    sb.append(order_by);
	    String limit = " limit 25";
	    sb.append(limit);
	    String query = sb.toString();
		System.out.println("Query1 "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
			    	int contId = rs.getInt(1);
			    	float weight = rs.getFloat(2);

			    	addedValues.put(new Integer(contId), new Float(weight));


				}

			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}



		return addedValues;

	}
	//ensure thatat least 25 items are shown
	public static HashMap<Integer, Float> addAdditionalItems(HashMap<Integer,Float> student, HashMap<Integer, Float> addons){

		Iterator<Integer> ints = addons.keySet().iterator();

		while(ints.hasNext()){
			Integer a = ints.next();
			if(!student.containsKey(a) && student.size() < 25){
				Float f = addons.get(a);
				student.put(a, f);
			}

		}


		return student;
	}
	//gets a count of all tyoes through all schools
	public static HashMap <String,Float> getGeneralRelatedCounts(Connection con, String database){
		HashMap<String,Float> hm = new HashMap<String,Float>();
		StringBuilder sb = new StringBuilder();
		String front = "select case report_incident_bias_type_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'National Origin'";
		String when2 = " when 2 then 'Race'";
		String when3 = " when 3 then 'Weight'";
		String when4 = " when 4 then 'Religion'";
		String when5 = " when 5 then 'Disability'";
		String when6 = " when 6 then 'Gender'";
		String when7 = " when 7 then 'Sexual Orientation'";
		String when8 = " when 8 then 'Sexuality'";
		String when9 = " when 9 then 'Ethnic Group'";
		String when10 = " when 10 then 'Socioeconomic Status'";
		String when11 = " when 11 then 'Religion'";
		String when12 = " when 12 then 'Sex'";
		String when20 = " when 20 then 'None of the above'";
		String end = " end as incident, count(report_incident_bias_id) as count";
		String from =  " from "+database+".report_incident_bias";
		String group_by = " group by incident";
		String orderBy = " order by count desc";
		String limit = " limit 25";

		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(when8);
		sb.append(when9);
		sb.append(when10);
		sb.append(when11);
		sb.append(when12);
		sb.append(when20);
		sb.append(end);
		sb.append(from);
		sb.append(group_by);
		sb.append(orderBy);
		sb.append(limit);

		String query = sb.toString();
		System.out.println(query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String incident = rs.getString("incident");
					Float count = new Float(rs.getInt("count"));
					hm.put(incident, count);
				}

			}


		}
		catch (SQLException e){
			e.printStackTrace();

		}
		catch (Exception e){
			e.printStackTrace();

		}


		return hm;


	}
	//get a count of all bullying type through all schools
	public static HashMap <String,Float> getGeneralTypeCounts(Connection con, String database){
		HashMap<String,Float> hm = new HashMap<String,Float>();
		StringBuilder sb = new StringBuilder();
		String front = "select case report_incident_harassment_type_id";
		String when0 = " when 0 then 'Other' ";
		String when1 = " when 1 then 'Physical'";
		String when2 = " when 2 then 'Verbal'";
		String when3 = " when 3 then 'Theft'";
		String when4 = " when 4 then 'Vandalism'";
		String when5 = " when 5 then 'Digital Public'";
		String when6 = " when 6 then 'Digital Private'";
		String when7 = " when 7 then 'Exclusion'";
		String when20 = " when 20 then 'None of the above'";
		String end = " end as incident, count(report_incident_harassment_id) as count";
		String from =  " from "+database+".report_incident_harassment";
		String group_by = " group by incident";
		String orderBy = " order by count desc";
		String limit = " limit 25";


		sb.append(front);
		sb.append(when0);
		sb.append(when1);
		sb.append(when2);
		sb.append(when3);
		sb.append(when4);
		sb.append(when5);
		sb.append(when6);
		sb.append(when7);
		sb.append(when20);
		sb.append(end);
		sb.append(from);
		sb.append(group_by);
		sb.append(orderBy);
		sb.append(limit);
		String query = sb.toString();
		//System.out.println(query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					String incident = rs.getString("incident");
					Float count = new Float(rs.getInt("count")*.5);
					hm.put(incident, count);
				}

			}


		}
		catch (SQLException e){

		}
		catch (Exception e){

		}


		return hm;


	}
	//combines synonyms
	public static HashMap<String, Float> combineTopics(HashMap<String, Float> bias,HashMap<String, Float> type ){

		Iterator<String> typeStrs = type.keySet().iterator();
		while(typeStrs.hasNext()){
			String typeStr = typeStrs.next();
			Float f = type.get(typeStr);
			bias.put(typeStr, f);
		}

		return bias;
	}
	// combines subject synonyms
	public static HashMap<Integer, Float> combineSubjects(HashMap<Integer, Float> biasSubjects, HashMap<Integer, Float > typeSubjects){

		Iterator <Integer> typeKeys = typeSubjects.keySet().iterator();
        while(typeKeys.hasNext()){
        	Integer f = typeKeys.next();
        	if(biasSubjects.containsKey(f)){
        		Float f1 = biasSubjects.get(f);
        		Float f2 = typeSubjects.get(f);
        		biasSubjects.put(f,f1+f2);
        	}
        	else{
        		Float f3 = typeSubjects.get(f);
        		biasSubjects.put(f,f3);
        	}

        }

		return biasSubjects;

	}
	//provides individual content scores
	public static boolean addIndividualGeneralContentScore(Connection con, int user_type, int content_id, float score, String database){
		int flag = 0;
		StringBuilder sb = new StringBuilder();
		String insert = "insert into "+database+".general_recommended_content (user_type,content_id,weight) values(";
		sb.append(insert);
		sb.append(user_type);
		sb.append(comma);
		sb.append(content_id);
		sb.append(comma);
		sb.append(score);
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
	//provides general content weight
	public static boolean addAllIndGenralContentWeights(Connection con, HashMap<Integer, Float> scores, int user_type, String database){
		int flag = 0;
		Iterator<Integer> cs = scores.keySet().iterator();
		while(cs.hasNext()){
			Integer csi = cs.next();
			Float f = scores.get(csi);
			if(addIndividualGeneralContentScore(con,user_type,csi.intValue(),f.floatValue(),database)){
				flag++;
			}

		}



		return flag > 0;
	}
	//gets additional content items
	public static HashMap<Integer, Float> getAdditionalGeneralItems(Connection con,int type, String database){
		HashMap<Integer, Float> addedValues = new HashMap<Integer, Float>();
		StringBuilder sb = new StringBuilder();
		String select = "Select distinct content_id, weight ";
		String from = "from "+database+".general_recommended_content ";
		sb.append(select);
		sb.append(from);
		String where = "where user_type = ";
		sb.append(where);
		sb.append(type);
		String order_by = " Order by weight desc ";
	    sb.append(order_by);
	    String limit = " limit 25";
	    sb.append(limit);
	    String query = sb.toString();
		System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
			    	int contId = rs.getInt(1);
			    	float weight = rs.getFloat(2);

			    	addedValues.put(new Integer(contId), new Float(weight));


				}
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}



		return addedValues;

	}
	//truncates old recomendation tables
	public static boolean truncateTable(Connection con, int type, String database){
		String query = "";
		if(type == 1 ){
			query = "truncate table "+database+".general_recommended_content";
		}
		else if (type == 2){
			query = "truncate table "+database+".recommended_user_content";

		}
		int flag = 0;
		System.out.println("Query "+query);
		try{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			flag++;


		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return flag > 0;



	}
	//generates the weights based on type of individual
	public static HashMap<Integer, Float> generateWeights(int type){
		HashMap<Integer, Float> weights = new HashMap<Integer, Float>();
        switch(type){
        case 1:
        	weights.put(new Integer(6), new Float(1.0));
        	weights.put(new Integer(4), new Float(0.8));
        	weights.put(new Integer(12), new Float(0.6));
        	weights.put(new Integer(13), new Float(0.4));
        	weights.put(new Integer(1), new Float(0.2));
        	break;
        case 2:
        	weights.put(new Integer(1), new Float(1.0));
        	weights.put(new Integer(12), new Float(0.8));
        	weights.put(new Integer(13), new Float(0.6));
        	weights.put(new Integer(8), new Float(0.4));
        	weights.put(new Integer(2), new Float(0.2));
        	break;
        case 3:
        	weights.put(new Integer(6), new Float(1.0));
        	weights.put(new Integer(4), new Float(0.8));
        	weights.put(new Integer(7), new Float(0.6));
        	weights.put(new Integer(11), new Float(0.4));
        	weights.put(new Integer(9), new Float(0.2));
        	break;
        case 4:
        	weights.put(new Integer(1), new Float(1.0));
        	weights.put(new Integer(12), new Float(0.8));
        	weights.put(new Integer(4), new Float(0.6));
        	weights.put(new Integer(13), new Float(0.4));
        	weights.put(new Integer(2), new Float(0.2));
        	break;

		case 5:
			weights.put(new Integer(4), new Float(1.0));
			weights.put(new Integer(6), new Float(0.8));
			weights.put(new Integer(5), new Float(0.6));
			weights.put(new Integer(10), new Float(0.4));
			weights.put(new Integer(11), new Float(0.2));
			break;
		case 6:
        	weights.put(new Integer(5), new Float(1.0));
        	weights.put(new Integer(10), new Float(0.8));
        	weights.put(new Integer(4), new Float(0.6));
        	weights.put(new Integer(7), new Float(0.4));
        	weights.put(new Integer(6), new Float(0.2));
        	break;
		case 7:
        	weights.put(new Integer(7), new Float(1.0));
        	weights.put(new Integer(9), new Float(0.8));
        	weights.put(new Integer(11), new Float(0.6));
        	weights.put(new Integer(10), new Float(0.4));
        	weights.put(new Integer(6), new Float(0.2));
        	break;


        }
        return weights;

	}
	//removes organization rows
	public static boolean removeOrgRecords(Connection con, int org_id, String database){

		StringBuilder sb = new StringBuilder();
		String delete = "delete from "+database+".recommended_content where org_id = ";
		sb.append(delete);
		sb.append(org_id);
		String query = sb.toString();
		int flag = 0;
		System.out.println("Query "+query);
		try{
			Statement stmt = con.createStatement();
			stmt.executeUpdate(query);
			flag++;


		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return flag > 0;

	}
	//createas a base rate of 0.0
	public static HashMap<String, Float> getBaseRate(int type){
		HashMap<String,Float> baseRates = new HashMap<String,Float>();
		switch(type){
        case 1:

        	baseRates.put("Other", new Float(0.0));
        	baseRates.put("National Origin",new Float(0.0));
        	baseRates.put("Race", new Float(0.0));
        	baseRates.put("Religion", new Float(0.0));
        	baseRates.put("Disability", new Float(0.0));
        	baseRates.put("Gender", new Float(0.0));
        	baseRates.put("Sexuality", new Float(0.0));
        	baseRates.put("Sexual Orientation", new Float(0.0));
        	baseRates.put("Socioeconomic Status", new Float(0.0));
        	baseRates.put("Sex", new Float(0.0));
        	break;
        case 2:

        	baseRates.put("Other", new Float(0.0));
            baseRates.put("Physical", new Float(0.0));
            baseRates.put("Verbal", new Float(0.0));
            baseRates.put("Theft", new Float(0.0));
            baseRates.put("Vandalism", new Float(0.0));
            baseRates.put("Digital Public", new Float(0.0));
            baseRates.put("Digital Private", new Float(0.0));
            baseRates.put("Exclusion", new Float(0.0));
        	break;

		}


		return baseRates;

	}
	//combines subject weights
	public static HashMap<String,Float> combineSubjectWeights(HashMap<String,Float> biases, HashMap<String,Float> types){
		Iterator<String> typeStrs = types.keySet().iterator();
		while(typeStrs.hasNext()){
			String typeKey = typeStrs.next();
			Float score = types.get(typeKey);
			biases.put(typeKey, score);
		}

		return biases;
	}
	//combine subject ints
	public static HashMap<Integer, Float > combineSubjectInts(HashMap<Integer, Float > biases, HashMap<Integer, Float > types){
		Iterator <Integer> typeKeys = types.keySet().iterator();
        while(typeKeys.hasNext()){
        	Integer f = typeKeys.next();
        	if(biases.containsKey(f)){
        		Float f1 = biases.get(f);
        		Float f2 = types.get(f);
        		biases.put(f,f1+f2);
        	}
        	else{
        		Float f3 = types.get(f);
        		biases.put(f,f3);
        	}

        }

		return biases;
	}
	//gets a students parents id
	public static ArrayList<Integer> getParentIds(Connection con, int student_id, String database){
		ArrayList<Integer> parents = new ArrayList<Integer>();
		StringBuilder sb = new StringBuilder();
		String select = "select parent_id from "+database+".user_parent ";
		String where = " where user_id = ";
		sb.append(select);
		sb.append(where);
		sb.append(student_id);
		ResultSet rs = null;
		String query = sb.toString();
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
			    	int id = rs.getInt(1);
			    	parents.add(new Integer(id));
			    }
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}





		return parents;
	}
	//returns weight offset
	public static int getWeightKey(int type){
		int weightType = 0;
		switch(type){
        	case 1:
        		weightType = 1;
        		break;
        	case 2:
        		weightType = 3;
        		break;
            default:
            	weightType = 5;
        }

		return weightType;

	}

	//checks to see what user type is using the recomendation engine
	public static int getUserType(Connection con, int user_id, String database ){
		int type = 0;
		StringBuilder sb = new StringBuilder();
		String select = "select type from "+database+".organization_user ";
	    String where = " where user_id = ";
	    sb.append(select);
	    sb.append(where);
	    sb.append(user_id);
	    String query = sb.toString();
	    ResultSet rs = null;

	    try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
			    	type = rs.getInt(1);

			    }
			}
		}
		catch (SQLException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}

		return type;
	}
	//gets staff users
	public static HashMap<Integer, ArrayList<Integer>> getStaffUsers(Connection con ){
		HashMap<Integer,ArrayList<Integer>> users = new HashMap<Integer, ArrayList<Integer> >();

		StringBuilder sb = new StringBuilder();
		String select = "select r.user_id_created, r.rec_id  ";
		String from = " from report r, user u ";
		String where = " where u.user_id = r.user_id_created ";
		String and1 = " and u.type in (3,4) ";
		String and2 = " and r.incident_relationship in (3,4,5) ";
		String date = " and r.date_created >= str_to_date('";
		String date1 = getDateRange(30);
		String date2 = "', '%Y-%m-%d')";
		sb.append(select);
		sb.append(from);
		sb.append(where);
		sb.append(and1);
		sb.append(and2);
		sb.append(date);
		sb.append(date1);
		sb.append(date2);
		String query = sb.toString();


		System.out.println("Query "+query);
		ResultSet rs = null;
		try{
			Statement stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if(rs != null){
				while(rs.next()){
					Integer i = new Integer(rs.getInt(1));
					Integer r = new Integer(rs.getInt(2));
					System.out.println("Report "+r+"\n");
					if(users.containsKey(i)){
						ArrayList<Integer> reports = users.get(i);
						System.out.println("Report1  "+r+"\n");
						if(!reports.contains(r)){
							reports.add(r);
						}
						users.put(i, reports);
						System.out.println("Reports "+reports+"\n");
					}
					else{
						System.out.println("Report2  "+r+"\n");
					   ArrayList<Integer> reports = new ArrayList<Integer>();
					   reports.add(r);
					   users.put(i, reports);
					   System.out.println("Reports2 "+reports+"\n");
					}


				}

			}

		}
		catch(SQLException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}



		return users;
	}











}
