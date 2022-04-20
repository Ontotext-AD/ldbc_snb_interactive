package com.ldbc.impls.workloads.ldbc.snb.mssql.operationhandlers;

import com.ldbc.driver.DbException;
import com.ldbc.driver.Operation;
import com.ldbc.driver.ResultReporter;
import com.ldbc.impls.workloads.ldbc.snb.operationhandlers.ListOperationHandler;
import com.ldbc.impls.workloads.ldbc.snb.mssql.SQLServerDbConnectionState;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class SQLServerListOperationHandler<TOperation extends Operation<List<TOperationResult>>, TOperationResult>
        extends SQLServerOperationHandler
        implements ListOperationHandler<TOperationResult, TOperation, SQLServerDbConnectionState> {

    @Override
    public void executeOperation(TOperation operation, SQLServerDbConnectionState state,
                                 ResultReporter resultReporter) throws DbException {
        try {
            ResultSet result = null;
            // PreparedStatement stmt = null;
            Connection conn = state.getConnection();
            List<TOperationResult> results = new ArrayList<>();
            int resultCount = 0;
            results.clear();
    
            String queryString = getQueryString(state, operation);
            replaceParameterNamesWithQuestionMarks(operation, queryString);
    
            try {
                final PreparedStatement stmt = prepareAndSetParametersInPreparedStatement(operation, queryString, conn);
                state.logQuery(operation.getClass().getSimpleName(), queryString);
    
                result = stmt.executeQuery();
                if (result != null){
                    while (result.next()) {
                        resultCount++;
        
                        TOperationResult tuple = convertSingleResult(result);
                        if (state.isPrintResults()) {
                            System.out.println(tuple.toString());
                        }
                        results.add(tuple);
                    }
                }
            } catch (SQLException e) {
                throw new DbException(e);
            }
            finally{
                if (result != null){
                    result.close();
                }
                // if (stmt != null){
                //     stmt.close();
                // }
                conn.close();
            }
            resultReporter.report(resultCount, results, operation);
        }
        catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public abstract TOperationResult convertSingleResult(ResultSet result) throws SQLException;

}
