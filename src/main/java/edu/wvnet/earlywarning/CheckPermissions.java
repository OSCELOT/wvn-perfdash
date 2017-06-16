package edu.wvnet.earlywarning;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import blackboard.db.ConnectionManager;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerService;
import blackboard.platform.session.BbSessionManagerServiceFactory;

public class CheckPermissions {

	public String getPage( HttpServletRequest request, HttpServletResponse response ) {
		BbSessionManagerService sessman = BbSessionManagerServiceFactory.getInstance();
		BbSession sess = sessman.getSession(request);
		if(!sess.isAuthenticated()) return "error";
		
		String pk1 = sess.getUserId().toExternalString().split("_")[1];
		String query = "select 1 from dual where exists (select 1 from users where pk1 = ? and system_role='Z') or exists (select 1 from course_users where users_pk1 = ? and role = 'P')";
		Connection conn = null;
		try {
			conn = ConnectionManager.getDefaultConnection();
			PreparedStatement pStatement = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			pStatement.setString(1, pk1);
			pStatement.setString(2, pk1);
			ResultSet result = pStatement.executeQuery(query);
			if(result.next()) request.setAttribute("access", 1);
			else request.setAttribute("access", 0);
		} catch (Exception e) {
			request.setAttribute("access", 0);
		} finally {
		    if(conn != null) {
		        ConnectionManager.releaseDefaultConnection(conn);
		    }
		}
		
		return "checkPermissions";
	}
	
}
