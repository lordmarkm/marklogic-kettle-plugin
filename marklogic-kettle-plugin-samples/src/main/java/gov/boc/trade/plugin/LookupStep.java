package gov.boc.trade.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

/**
 * 
 * @author Mark Martinez, created Feb 15, 2016
 *
 */
public class LookupStep extends BaseStep implements StepInterface {

    private LookupStepData data;
    private LookupStepMeta meta;

    private static Class<?> PKG = LookupStep.class; // for i18n purposes

    public LookupStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
        super(s, stepDataInterface, c, t, dis);
    }

    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

        meta = (LookupStepMeta) smi;
        data = (LookupStepData) sdi;

        Object[] r = getRow(); // get row, blocks when needed!
        if (r == null) // no more input to be expected...
        {
            setOutputDone();
            return false;
        }


        System.out.println("Getting data from marklogic");
        ResultSet rs = getData(data.marklogicOdbcConnection);

        System.out.println("Reading marklogic result set");
        try {
            while (rs.next()) {
                // generate output row, make it correct size
                Object[] outputRow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());
                outputRow[0] = rs.getString("employee_uri");
                outputRow[1] = rs.getString("employee_collection");
                outputRow[2] = rs.getLong("employee_id");
                outputRow[3] = rs.getString("employee_name");
                outputRow[4] = rs.getString("employee_role");
                outputRow[5] = rs.getString("employee_operatorcode");

                // copy row to possible alternate rowset(s)
                putRow(data.outputRowMeta, outputRow); 
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Some basic logging
        if (checkFeedback(getLinesRead())) {
            if (log.isBasic()) logBasic("Linenr " + getLinesRead()); 
        }

        return true;
    }

    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (LookupStepMeta) smi;
        data = (LookupStepData) sdi;

        //get an odbc connection
        Connection conn = getConnection();
        data.marklogicOdbcConnection = conn;

        System.out.println("Preparing output row meta");
        RowMetaInterface outputRowMeta = new RowMeta();
        outputRowMeta.addValueMeta(new ValueMeta("employee_uri", ValueMeta.TYPE_STRING));
        outputRowMeta.addValueMeta(new ValueMeta("employee_collection", ValueMeta.TYPE_STRING));
        outputRowMeta.addValueMeta(new ValueMeta("employee_id", ValueMeta.TYPE_STRING));
        outputRowMeta.addValueMeta(new ValueMeta("employee_name", ValueMeta.TYPE_STRING));
        outputRowMeta.addValueMeta(new ValueMeta("employee_role", ValueMeta.TYPE_STRING));
        outputRowMeta.addValueMeta(new ValueMeta("employee_operatorcode", ValueMeta.TYPE_STRING));
        data.outputRowMeta = outputRowMeta;

        return super.init(smi, sdi);
    }

    private Connection getConnection() {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "root");

        try {
            conn = DriverManager.getConnection("jdbc:odbc:MarkLogicSQL", connectionProps);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("Connected to database");

        return conn;
    }

    private ResultSet getData(Connection con) {
        Statement stmt = null;
        String query = "select * from employees";
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (stmt != null) { try {
                stmt.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } }
        }
        return null;
    }

    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        meta = (LookupStepMeta) smi;
        data = (LookupStepData) sdi;

        try {
            data.marklogicOdbcConnection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        super.dispose(smi, sdi);
    }

}
