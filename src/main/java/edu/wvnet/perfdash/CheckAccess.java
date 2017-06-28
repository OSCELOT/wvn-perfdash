package edu.wvnet.perfdash;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import blackboard.db.ConnectionManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerService;
import blackboard.platform.session.BbSessionManagerServiceFactory;

public class CheckAccess extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// make sure user is logged in
		BbSessionManagerService sessman = BbSessionManagerServiceFactory.getInstance();
		BbSession sess = sessman.getSession(request);
		if(!sess.isAuthenticated()) throw new ServletException("user not authenticated");
		
		// get the user pk1
		String pk1 = sess.getUserId().toExternalString().split("_")[1];
		
		// check if user has access i.e. they are an instructor or admin
		String query = "select 1 from dual where exists (select 1 from users where pk1 = ? and system_role='Z') or exists (select 1 from course_users where users_pk1 = ? and role = 'P')";
		Connection conn = null;
		char userHasAccess = '0';
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			pStatement.setString(1, pk1);
			pStatement.setString(2, pk1);
			ResultSet result = pStatement.executeQuery();
			if(result.next()) userHasAccess = '1';
		} catch (Exception e) {
			// on any error, deny access
			// userHasAccess is already 0, so nothing to do here
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}

		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
		writer.write(userHasAccess);
		writer.flush();
		writer.close();
	}
	
}
