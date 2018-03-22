package com.ldbc.impls.workloads.ldbc.snb.jdbc;

import com.ldbc.driver.DbException;
import com.ldbc.driver.Operation;
import com.ldbc.driver.OperationHandler;
import com.ldbc.driver.ResultReporter;
import com.ldbc.driver.workloads.ldbc.snb.interactive.LdbcNoResult;
import com.ldbc.impls.workloads.ldbc.snb.SnbQueryStore;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class PostgresUpdateOperationHandler<OperationType extends Operation<LdbcNoResult>, OperationResult, QueryStore extends SnbQueryStore>
implements OperationHandler<OperationType, PostgresDbConnectionState<QueryStore>> {

@Override
public void executeOperation(OperationType operation, PostgresDbConnectionState<QueryStore> state,
		ResultReporter resultReporter) throws DbException {
	Connection conn = state.getConnection();
	String queryString = getQueryString(state, operation);
	try {
		Statement stmt = conn.createStatement();
		
		state.logQuery(operation.getClass().getSimpleName(), queryString);

		stmt.execute(queryString);
		stmt.close();
	} catch (SQLException e) {
		throw new RuntimeException(queryString+e);
	} catch (Exception e) {
		throw new RuntimeException(e);
	} finally {
		try {
			conn.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		resultReporter.report(0, LdbcNoResult.INSTANCE, operation);
	}
}

public abstract String getQueryString(PostgresDbConnectionState<QueryStore> state, OperationType operation);
}