/*
 * package com.etcc.mendix.camunda;
 * 
 * import java.sql.CallableStatement; import java.sql.Connection;
 * 
 * import javax.sql.DataSource;
 * 
 * import org.camunda.bpm.engine.delegate.DelegateExecution; import
 * org.camunda.bpm.engine.delegate.JavaDelegate; import
 * org.camunda.bpm.engine.impl.context.Context; import
 * org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.beans.factory.annotation.Qualifier; import
 * org.springframework.stereotype.Service;
 * 
 * @Service("sqlExecutor") public class SQLExecutorDelegate implements
 * JavaDelegate {
 * 
 * @Autowired
 * 
 * @Qualifier("riteDs") private DataSource riteDs;
 * 
 * @Override public void execute(DelegateExecution execution) throws Exception {
 * String sqlQuery = (String) execution.getVariable("sqlQuery");
 * System.out.println("sqlQuery " + sqlQuery);
 * 
 * }
 * 
 * 
 * public void equeue(String varName, String queueName, String
 * fromEscalationCodeVar,String toEscalationCode) throws Exception { String sql
 * = "{call tag_owner.ESCALATE_INVOICES_API.queueEscalationInvoice(?,?,?,?)}";
 * 
 * ActivityExecution execution = (ActivityExecution) Context
 * .getBpmnExecutionContext().getExecution(); Object var =
 * execution.getVariable(varName); Object fromEscalationCode =
 * execution.getVariable(fromEscalationCodeVar);
 * 
 * 
 * try (Connection conn = riteDs.getConnection(); CallableStatement cs =
 * conn.prepareCall(sql);) { cs.setLong(1, Long.valueOf(var.toString()));
 * cs.setString(2, queueName); cs.setString(3, fromEscalationCode.toString());
 * cs.setString(4, toEscalationCode.toString());
 * 
 * cs.execute();
 * 
 * }
 * 
 * }
 * 
 * }
 */