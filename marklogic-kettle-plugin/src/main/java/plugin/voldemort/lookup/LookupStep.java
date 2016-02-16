package plugin.voldemort.lookup;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.*;

import voldemort.client.ClientConfig;
import voldemort.client.SocketStoreClientFactory;
import voldemort.client.StoreClientFactory;


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

		if (first) {
			
			first = false;
			
			// the size of the incoming rows 
			data.inputSize = getInputRowMeta().size();
			
			// determine output field structure
			data.outputRowMeta = (RowMetaInterface) getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);
			
			// stores default values in correct format
            data.defaultObjects = new Object[meta.getKeyField().length];

            // stores the indices where to look for the key fields in the input rows
            data.keyFieldIndex = new int[meta.getKeyField().length];
            data.conversionMeta = new ValueMetaInterface[meta.getKeyField().length];

            
            for (int i=0;i<meta.getKeyField().length;i++){
            	
            	// get output and from-string conversion format for each field
                ValueMetaInterface returnMeta = data.outputRowMeta.getValueMeta(i+data.inputSize);
                
                ValueMetaInterface conversionMeta = returnMeta.clone();
                conversionMeta.setType(ValueMetaInterface.TYPE_STRING);
                data.conversionMeta[i] = conversionMeta;

                // calculate default values
                if (!Const.isEmpty(meta.getOutputDefault()[i])){
                    data.defaultObjects[i] = returnMeta.convertData(data.conversionMeta[i], meta.getOutputDefault()[i]);
                }
                else{
                    data.defaultObjects[i] = null;
                }
                
                // calc key field indices
                data.keyFieldIndex[i] = data.outputRowMeta.indexOfValue(meta.getKeyField()[i]);
                if (data.keyFieldIndex[i]<0)
                {
                    throw new KettleStepException(BaseMessages.getString(PKG, "VoldemortStep.Error.UnableFindField",meta.getKeyField()[i],""+(i+1)));
                }
                
            }

		}
		
		// generate output row, make it correct size
		Object[] outputRow = RowDataUtil.resizeArray(r, data.outputRowMeta.size());
		
		// fill the output fields with look up data
        for (int i = 0, outi=data.inputSize;i<meta.getKeyField().length;outi++, i++){
        	
        	// try to get the value from voldemort
            String value = data.voldemortClient.getValue(r[data.keyFieldIndex[i]]);

            // if nothing is there, return the default
            if (value == null){
            	outputRow[outi] = data.defaultObjects[i];	
            }
            // else convert the value to desired format
            else{
            	outputRow[outi] = data.outputRowMeta.getValueMeta(outi).convertData(data.conversionMeta[i], value);	
            }
        	
        }

        // copy row to possible alternate rowset(s)
		putRow(data.outputRowMeta, outputRow); 

		// Some basic logging
		if (checkFeedback(getLinesRead())) {
			if (log.isBasic()) logBasic("Linenr " + getLinesRead()); 
		}

		return true;
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (LookupStepMeta) smi;
		data = (LookupStepData) sdi;
					
	    // get a voldemort client
	    String bootstrapUrl = "tcp://"+environmentSubstitute(meta.getVoldemortHost())+":"+environmentSubstitute(meta.getVoldemortPort());
	    StoreClientFactory factory = new SocketStoreClientFactory(new ClientConfig().setBootstrapUrls(bootstrapUrl));
	    data.voldemortClient = factory.getStoreClient(environmentSubstitute(meta.getVoldemortStore()));
	    
	    if (data.voldemortClient == null){
	    	return false;
	    }
	       
		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (LookupStepMeta) smi;
		data = (LookupStepData) sdi;
		
		data.voldemortClient = null;

		super.dispose(smi, sdi);
	}

}
