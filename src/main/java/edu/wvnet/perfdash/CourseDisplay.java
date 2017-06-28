package edu.wvnet.perfdash;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import blackboard.db.ConnectionManager;
import blackboard.db.ConnectionNotAvailableException;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerService;
import blackboard.platform.session.BbSessionManagerServiceFactory;

public class CourseDisplay extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// make sure user is logged in
		BbSessionManagerService sessman = BbSessionManagerServiceFactory.getInstance();
		BbSession sess = sessman.getSession(request);
		if(!sess.isAuthenticated()) throw new ServletException("user not authenticated");
		
		// get the user pk1
		String pk1 = sess.getUserId().toExternalString().split("_")[1];
		
		// if we're exporting, output the file
		if(request.getParameter("export") != null) {
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "inline; filename=spd.csv");
			PrintWriter writer = response.getWriter();
			writer.write(exportSPDTable(pk1));
			writer.flush();
			writer.close();
		}
		
		// otherwise forward to the jsp to output the page
		String pageHelp = "Use <b>Ctrl+f</b> to find a course within the list. Use <b>Ctrl+p</b> to print.";
		request.setAttribute("pageHelp", pageHelp);
		request.setAttribute("spdtable", buildSPDTable(pk1));
		RequestDispatcher requetsDispatcherObj = request.getRequestDispatcher("/courseDisplay.jsp");
		requetsDispatcherObj.forward(request, response);
	}
	
	private String buildSPDTable(String pk1) {
		String query = "select pk1, course_id, course_name, lastname, firstname, user_id, last_access_date,        sum_score, sum_possible, max_possible,        nvl(trunc(sum_score / sum_possible, 2)*100, 0) personal_percent,        nvl(trunc(sum_score / max_possible, 2)*100, 0) peer_percent from (   with scores as (     select course_main.pk1 , course_id , course_name , lastname , firstname , user_id , last_access_date ,            sum(nvl(decode(NULL,        manual_grade, NULL,        manual_score, NULL,        manual_score), (select score from attempt where attempt.pk1 = highest_attempt_pk1))) sum_score,            sum(gradebook_main.possible) sum_possible     from gradebook_grade     join gradebook_main on gradebook_main_pk1 = gradebook_main.pk1 and possible > 0     join course_users on course_users_pk1 = course_users.pk1     join course_main on gradebook_main.crsmain_pk1 = course_main.pk1     join users on users_pk1 = users.pk1     where       course_users.available_ind = 'Y' and       possible > 0 and (course_main.available_ind = 'Y' or (course_main.honor_term_avail_ind = 'Y' and (select available_ind from term where term.pk1 = (select term_pk1 from course_term where course_term.crsmain_pk1 = course_main.pk1)) = 'Y' )) and course_main.row_status = 0     group by course_main.pk1 , course_id , course_name , lastname , firstname , user_id , last_access_date     order by course_id , lastname , firstname , user_id , last_access_date   )   select pk1, course_id, course_name, lastname, firstname, user_id, last_access_date, sum_score, sum_possible,     (select max(sum_possible) from scores b where b.course_id = a.course_id group by course_id) max_possible   from scores a )";
		ResultSet result = null;
		Connection conn = null;
		
		if(!isAdmin(pk1)) {
			String courses;
			try {
				courses = getAccessibleCourses(pk1);
			} catch (SQLException e) {
				return "ERROR CHECKING ACCESS: " + e.getErrorCode() + " : " + e.getMessage();
			}
			if(courses.isEmpty())
				return "Access denied. Sorry, but this tool is only for use by instructors.";
			query += " where course_id in (" + courses + ")";
		}
		
		float class_sum_score = 0;
		float class_max_possible = 0;
		float class_average = 0;
		String prevclass = "";
		String output = "";
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			result = pStatement.executeQuery();
			
			if (!result.isBeforeFirst() ) {    
				return "No gradebook data was found for any courses you are associated with."; 
			}

			while(true) {
				if(!result.isAfterLast()) result.next(); //this way we do stuff after last row without an SQLException
				//header row and footer
				if(result.isAfterLast() || !result.getString("COURSE_ID").equals(prevclass)) { //class has changed or we finished last class
					// close table and compute average
					if(!prevclass.equals("")) { // make sure this isn't the first class
						// footer of table
						class_average = class_sum_score / class_max_possible * 100;
						output += "\n</table>";
						output += "\n<a class='classlink' href='/webapps/blackboard/execute/launcher?type=Course&id=_" + result.getString("PK1") + "_1' target='_blank'>Visit course</a>";
						output += "\n</div>";
						output += "\n<h2 class='classaverage' style='color: " + percentToColor(class_average) + "'>" + String.format("%d", Math.round(class_average)) + "%</h2>";						
						output += "\n</div>";
						if(result.isAfterLast()) break;
						class_sum_score = 0;
						class_max_possible = 0;
					}
					// header row of table
					prevclass = result.getString("COURSE_ID");
					output += "<div class='accordion'><h2 class='classheader'><a class='headerlink' href='#'><div class='expando expando-plus'><span class='sechead'>" + result.getString("COURSE_ID") + "<span class='coursename'>&nbsp;: " + result.getString("COURSE_NAME") + "</span></span></div></a></h2><div>";
					output += "\n<table class='classtable' id=" + result.getString("COURSE_ID") + ">";
					output += "\n  <tr>";
					output += "\n    <th>LASTNAME</th>";
					output += "\n    <th>FIRSTNAME</th>";
					output += "\n    <th>USER_ID</th>";
					output += "\n    <th>LAST_ACCESS_DATE</th>";
					output += "\n    <th>PERSONAL_PERCENT</th>";
					output += "\n    <th>PEER_PERCENT</th>";
					output += "\n  </tr>";
				}

				// remaining rows
				class_sum_score += result.getFloat("SUM_SCORE");
				class_max_possible += result.getFloat("MAX_POSSIBLE");
				output += "\n  <tr style='background-color:" + percentToColor(result.getFloat("PEER_PERCENT")) + "'>";
				output += "\n    <td>" + result.getString("LASTNAME") + "</td>";
				output += "\n    <td>" + result.getString("FIRSTNAME") + "</td>";
				output += "\n    <td>" + result.getString("USER_ID") + "</td>";
				try {
				if(result.getString("LAST_ACCESS_DATE") != null)
					output += "\n    <td>" + result.getString("LAST_ACCESS_DATE").split("\\.")[0] + "</td>";
				else
					output += "\n    <td></td>";
				} catch (Exception e) {
					output += "<td>" + e.toString() + e.getMessage() + " noooo</td>";
				}
				output += "\n    <td>" + result.getString("PERSONAL_PERCENT") + "%</td>";
				output += "\n    <td>" + result.getString("PEER_PERCENT") + "%</td>";
				output += "\n  </tr>";
			}
		} catch (SQLException e) {
			output += "ERROR RETREIVING OUTPUT";
			output += "\nError code: " + e.getErrorCode();
			output += "\nMessage: " + e.getMessage();
		} catch (ConnectionNotAvailableException e) {
		    output += "COULD NOT GET CONNETION";
			output += "\nMessage: " + e.getMessage();
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return output;
	}
	
	private String exportSPDTable(String pk1) {
		// output as csv instead of html
		String query = "select course_id, course_name, lastname, firstname, user_id, last_access_date,        sum_score, sum_possible, max_possible,        nvl(trunc(sum_score / sum_possible, 2)*100, 0) personal_percent,        nvl(trunc(sum_score / max_possible, 2)*100, 0) peer_percent from (   with scores as (     select course_id , course_name , lastname , firstname , user_id , last_access_date ,            sum(nvl(manual_score, (select score from attempt where attempt.pk1 = highest_attempt_pk1))) sum_score,            sum(gradebook_main.possible) sum_possible     from gradebook_grade     join gradebook_main on gradebook_main_pk1 = gradebook_main.pk1 and possible > 0     join course_users on course_users_pk1 = course_users.pk1     join course_main on gradebook_main.crsmain_pk1 = course_main.pk1     join users on users_pk1 = users.pk1     where       course_users.available_ind = 'Y' and       possible > 0 and (course_main.available_ind = 'Y' or (course_main.honor_term_avail_ind = 'Y' and (select available_ind from term where term.pk1 = (select term_pk1 from course_term where course_term.crsmain_pk1 = course_main.pk1)) = 'Y' )) and course_main.row_status = 0     group by course_id , course_name , lastname , firstname , user_id , last_access_date     order by course_id , lastname , firstname , user_id , last_access_date   )   select course_id, course_name, lastname, firstname, user_id, last_access_date, sum_score, sum_possible,     (select max(sum_possible) from scores b where b.course_id = a.course_id group by course_id) max_possible   from scores a )";
		ResultSet result = null;
		Connection conn = null;
		
		if(!isAdmin(pk1)) {
			String courses;
			try {
				courses = getAccessibleCourses(pk1);
			} catch (SQLException e) {
				return "ERROR CHECKING ACCESS: " + e.getErrorCode() + " : " + e.getMessage();
			}
			if(courses.isEmpty())
				return "Access denied. Sorry, but this tool is only for use by instructors.";
			query += " where course_id in (" + courses + ")";
		}
	
		String output = "";
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			result = pStatement.executeQuery(query);
			
			if (!result.isBeforeFirst() ) {    
				return "No gradebook data was found for any courses you are associated with."; 
			}

			output = "course_id,course_name,lastname,firstname,user_id,last_access_date,sum_score,sum_possible,max_possible\r\n";
			while(result.next()) {
				output += result.getString("COURSE_ID") + ",";
				output += result.getFloat("PEER_PERCENT") + ",";
				output += result.getString("LASTNAME") + ",";
				output += result.getString("FIRSTNAME") + ",";
				output += result.getString("USER_ID") + ",";
				try {
					if(result.getString("LAST_ACCESS_DATE") != null)
						output += result.getString("LAST_ACCESS_DATE").split("\\.")[0] + ",";
					else
						output += ",";
				} catch (Exception e) {
					output += "ERROR:" + e.toString() + e.getMessage() + ",";
				}
				output += result.getString("PERSONAL_PERCENT") + "%,";
				output += result.getString("PEER_PERCENT") + "%\r\n";
			}
		} catch (SQLException e) {
			output += "ERROR RETREIVING OUTPUT";
			output += "\nError code: " + e.getErrorCode();
			output += "\nMessage: " + e.getMessage();
		} catch (ConnectionNotAvailableException e) {
		    output += "COULD NOT GET CONNETION";
			output += "\nMessage: " + e.getMessage();
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return output;
	}
	
	private String percentToColor(float percent) {
		String hexstring;
		if(percent >= 70)      hexstring = "#A6FFA6";
		else if(percent >= 50) hexstring = "#FFFF80";
		else                   hexstring = "#D06B64";
		return hexstring;
	}
	
	private boolean isAdmin(String pk1) {
		String query = "select 1 from users where pk1 = '" + pk1 + "' and system_role='Z'";
		ResultSet result = null;
		Connection conn = null;
		
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query);
			result = pStatement.executeQuery(query);
			
			if (!result.isBeforeFirst() ) {    
				return false; 
			}
		} catch (Exception e) {
			return false;
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return true;
	}
	
	/**
	 * Return a comma-separated list of courses the user may admin. Courses
	 * are given as course_ids from the bblearn.course_main table. Intended
	 * to be fed to an SQL where clause. Assumes the user is not a sysadmin.
	 * An empty return means the user has no access.
	 * @param pk1	the pk1 of this user in the bblearn.users table
	 * @return		a string containing a comma-separated list of course_ids
	 */
	private String getAccessibleCourses(String pk1) throws SQLException {
		String query = "select distinct course_id from course_main where pk1 in (select course_main_pk1 from domain_admin join domain_course_coll on parent_domain_pk1 = domain_admin.domain_pk1 where system_role = 'C' and user_pk1 = ? union select crsmain_pk1 from course_users where role = 'P' and users_pk1 = ?)";
		ResultSet result = null;
		Connection conn = null;
		String courses = "";
		
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pStatement.setString(1, pk1);
			pStatement.setString(2, pk1);
			result = pStatement.executeQuery();
			
			if (!result.isBeforeFirst()) return ""; //empty result
			
			while(result.next()) {
				courses += "'" + result.getString(1) + "'";
				if(!result.isLast()) courses += ",";
			}
		} catch (ConnectionNotAvailableException e) {
			return ""; // let's just deny access if there's a problem
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return courses;
	}
			
}
