package gov.boc.trade.plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
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

    public LookupStep(StepMeta s, StepDataInterface stepDataInterface, int c, TransMeta t, Trans dis) {
        super(s, stepDataInterface, c, t, dis);
    }

    @Override
    public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
        LookupStepMeta meta = (LookupStepMeta) smi;
        LookupStepData data = (LookupStepData) sdi;

        //get an odbc connection
        Connection conn = getConnection(meta);
        data.marklogicOdbcConnection = conn;

        System.out.println("Preparing output row meta");

        RowMetaInterface outputRowMeta = new RowMeta();
        String[] outputFields = meta.getOutputField();
        int[] outputTypes = meta.getOutputType();
        for (int i = 0; i < outputFields.length; i++) {
            String outputFieldName = outputFields[i];
            int outputType = outputTypes[i];
            outputRowMeta.addValueMeta(new ValueMeta(outputFieldName, outputType));
        }
        data.outputRowMeta = outputRowMeta;

        return super.init(smi, sdi);
    }

    @Override
    public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

        LookupStepMeta meta = (LookupStepMeta) smi;
        LookupStepData data = (LookupStepData) sdi;

        Object[] r = getRow(); // get row, blocks when needed!
        if (r == null) {
            // no more input to be expected...
            setOutputDone();
            return false;
        }

        System.out.println("Got data from previous steps: " + Arrays.asList(r));
        System.out.println("Getting data from marklogic");
        getRows(r, meta, data);

        return true;
    }

    private Connection getConnection(LookupStepMeta meta) {
        Connection conn = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", meta.getUsername());
        connectionProps.put("password", meta.getPassword());

        try {
            conn = DriverManager.getConnection("jdbc:odbc:" + meta.getMarklogicOdbcName(), connectionProps);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to connect to database.", e);
        }

        System.out.println("Connected to database");

        return conn;
    }

    private void getRows(Object[] input, LookupStepMeta meta, LookupStepData data) {
        Connection con = data.marklogicOdbcConnection;
        Statement stmt = null;
        String query = "select * from " + meta.getViewName();

        //Append formatted qualifier
        if (meta.getQualifier() != null) {
            query += (" " + MessageFormat.format(meta.getQualifier(), input));
        }

        System.out.println("Executing query: " + query);
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("Reading marklogic result set");
            try {
                while (rs.next()) {
                    // generate output row, make it correct size
                    Object[] outputRow = new Object[data.outputRowMeta.size()];
                    for (int i = 0; i < data.outputRowMeta.size(); i++) {
                        insertData(outputRow, i, data.outputRowMeta, rs);
                    }

                    // copy row to possible alternate rowset(s)
                    try {
                        putRow(data.outputRowMeta, outputRow);
                    } catch (KettleStepException e) {
                        e.printStackTrace();
                    } 
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e ) {
            e.printStackTrace();
        } finally {
            if (stmt != null) { try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } }
        }
    }

    private void insertData(Object[] outputRow, int index, RowMetaInterface rowMeta, ResultSet rs) throws SQLException {
        ValueMetaInterface vmi = rowMeta.getValueMeta(index);
        String rowName = vmi.getName();
        int type = vmi.getType();
        System.out.println("Processing row. name=" + vmi.getName() + ", type=" + type);
        Object data = null;

        switch (type) {
        case ValueMeta.TYPE_STRING:
            data = rs.getString(rowName);
            break;
        case ValueMeta.TYPE_BIGNUMBER:
            data = rs.getFloat(rowName);
            break;
        case ValueMeta.TYPE_INTEGER:
            data = rs.getLong(rowName);
            break;
        case ValueMeta.TYPE_BOOLEAN:
            data = rs.getBoolean(rowName);
            break;
        case ValueMeta.TYPE_DATE:
            data = rs.getDate(rowName);
            break;
        }

        if (null != data) {
            outputRow[index] = data;
        }
    }

    @Override
    public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
        //LookupStepMeta meta = (LookupStepMeta) smi;
        LookupStepData data = (LookupStepData) sdi;

        try {
            data.marklogicOdbcConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        super.dispose(smi, sdi);
    }

}
