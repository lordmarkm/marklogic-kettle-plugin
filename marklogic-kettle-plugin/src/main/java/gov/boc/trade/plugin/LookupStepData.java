package gov.boc.trade.plugin;

import java.sql.Connection;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

/**
 * 
 * @author Mark Martinez, created Feb 17, 2016
 *
 */
public class LookupStepData extends BaseStepData implements StepDataInterface {

    public RowMetaInterface outputRowMeta;
    public Connection marklogicOdbcConnection;

    public LookupStepData() {
        super();
    }

}

