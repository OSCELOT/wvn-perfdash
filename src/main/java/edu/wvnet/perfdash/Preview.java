package edu.wvnet.perfdash;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import blackboard.platform.plugin.PlugInUtil;

public class Preview extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// make sure user is logged in
		BbSessionManagerService sessman = BbSessionManagerServiceFactory.getInstance();
		BbSession sess = sessman.getSession(request);
		if(!sess.isAuthenticated()) throw new ServletException("user not authenticated");
		
		// get the user pk1
		String pk1 = sess.getUserId().toExternalString().split("_")[1];

		try {
			request.setAttribute("totals", getTotals(pk1));
		} catch (SQLException e) {
			request.setAttribute("error", e.getMessage());
		}
		//request.setAttribute("stem", PlugInUtil.getUriStem("wvn", "perfdash"));
		RequestDispatcher requetsDispatcherObj = request.getRequestDispatcher("/preview.jsp");
		requetsDispatcherObj.forward(request, response);
	}
	
	private Map<String, Integer> getTotals(String pk1) throws SQLException {
		String query = "select color, sum(users) from (SELECT course_id,       1 users,       case        when nvl(trunc(sum_score / max_possible, 2)*100, 0) >= 70 then 'G'        when nvl(trunc(sum_score / max_possible, 2)*100, 0) >= 50 then 'Y'        else 'R'       end color FROM  (WITH scores AS     (SELECT course_id,             user_id,             sum(nvl(manual_score,                       (SELECT score                        FROM attempt                        WHERE attempt.pk1 = highest_attempt_pk1))) sum_score,             sum(gradebook_main.possible) sum_possible      FROM gradebook_grade      JOIN gradebook_main ON gradebook_main_pk1 = gradebook_main.pk1      AND possible > 0      JOIN course_users ON course_users_pk1 = course_users.pk1      JOIN course_main ON gradebook_main.crsmain_pk1 = course_main.pk1      JOIN users ON users_pk1 = users.pk1      WHERE course_users.available_ind = 'Y'        AND possible > 0        AND (course_main.available_ind = 'Y'             OR (course_main.honor_term_avail_ind = 'Y'                 AND                   (SELECT available_ind                    FROM term                    WHERE term.pk1 =                        (SELECT term_pk1                         FROM course_term                         WHERE course_term.crsmain_pk1 = course_main.pk1)) = 'Y'))        AND course_main.row_status = 0      GROUP BY course_id,               user_id      ORDER BY course_id,               user_id) SELECT course_id,                               user_id,                               sum_score,                               sum_possible,     (SELECT max(sum_possible)      FROM scores b      WHERE b.course_id = a.course_id      GROUP BY course_id) max_possible   FROM scores a) ";
		ResultSet result = null;
		Connection conn = null;
		Map<String, Integer> output = new HashMap<String, Integer>();
		
		String courses;
		try {
			courses = getAccessibleCourses(pk1);
		} catch (SQLException e) {
			throw new SQLException("ERROR CHECKING ACCESS: " + e.getErrorCode() + " : " + e.getMessage());
		}
		if(courses.isEmpty())
			throw new SQLException("Access denied. Sorry, but this tool is only for use by instructors.");
		query += " where course_id in (" + courses + ")) group by color   ";
		
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			result = pStatement.executeQuery();
			
			if (!result.isBeforeFirst() ) {    
				throw new SQLException("No gradebook data was found for any courses you are associated with."); 
			}
			
			while(result.next()) {
				switch(result.getString("color")) {
				case "G": output.put("green", result.getInt(2)); break;
				case "Y": output.put("yellow", result.getInt(2)); break;
				case "R": output.put("red", result.getInt(2)); break;
				}
			}
		} catch (SQLException e) {
			throw new SQLException("ERROR RETREIVING OUTPUT\nError code: " + e.getErrorCode() + "\nMessage: " + e.getMessage());
		} catch (ConnectionNotAvailableException e) {
			throw new SQLException("COULD NOT GET CONNETION " + e.getMessage());
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return output;
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
