package gov.boc.trade.plugin;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.*;
import org.pentaho.di.core.database.DatabaseMeta; 
import org.pentaho.di.core.exception.*;
import org.pentaho.di.core.row.*;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.*;
import org.pentaho.di.trans.*;
import org.pentaho.di.trans.step.*;
import org.w3c.dom.Node;

public class LookupStepMeta extends BaseStepMeta implements StepMetaInterface {

	private static Class<?> PKG = LookupStepMeta.class; // for i18n purposes
	
	
	private String voldemortHost;
	private String voldemortPort;
	private String voldemortStore;

	private String keyField[];      
	private String outputField[];          
	private String outputDefault[];    
	private int	outputType[];   	
	private String outputFormat[];
	private String outputCurrency[];
	private String outputDecimal[];
	private String outputGroup[];
	private int outputLength[];
	private int outputPrecision[];
	
	public LookupStepMeta() {
		super(); 
	}
	
	// getters and setters for the step settings

	public String getVoldemortHost() {
		return voldemortHost;
	}

	public void setVoldemortHost(String voldemortHost) {
		this.voldemortHost = voldemortHost;
	}

	public String getVoldemortPort() {
		return voldemortPort;
	}

	public void setVoldemortPort(String voldemortPort) {
		this.voldemortPort = voldemortPort;
	}

	public String getVoldemortStore() {
		return voldemortStore;
	}

	public void setVoldemortStore(String voldemortStore) {
		this.voldemortStore = voldemortStore;
	}
	
	public String[] getKeyField() {
		return keyField;
	}

	public void setKeyField(String[] keyField) {
		this.keyField = keyField;
	}

	public String[] getOutputField() {
		return outputField;
	}

	public void setOutputField(String[] outputField) {
		this.outputField = outputField;
	}

	public String[] getOutputDefault() {
		return outputDefault;
	}

	public void setOutputDefault(String[] outputDefault) {
		this.outputDefault = outputDefault;
	}

	public int[] getOutputType() {
		return outputType;
	}

	public void setOutputType(int[] outputType) {
		this.outputType = outputType;
	}

	public String[] getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String[] outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String[] getOutputCurrency() {
		return outputCurrency;
	}

	public void setOutputCurrency(String[] outputCurrency) {
		this.outputCurrency = outputCurrency;
	}

	public String[] getOutputDecimal() {
		return outputDecimal;
	}

	public void setOutputDecimal(String[] outputDecimal) {
		this.outputDecimal = outputDecimal;
	}

	public String[] getOutputGroup() {
		return outputGroup;
	}

	public void setOutputGroup(String[] outputGroup) {
		this.outputGroup = outputGroup;
	}

	public int[] getOutputLength() {
		return outputLength;
	}

	public void setOutputLength(int[] outputLength) {
		this.outputLength = outputLength;
	}

	public int[] getOutputPrecision() {
		return outputPrecision;
	}

	public void setOutputPrecision(int[] outputPrecision) {
		this.outputPrecision = outputPrecision;
	}
	
	// set sensible defaults for a new step
	public void setDefault() {
		voldemortHost = "localhost";
		voldemortPort = "6666";
		voldemortStore = "test";

		// default is to have no key lookup settings
		allocate(0);
		
	}	
	
	// helper method to allocate the arrays
	public void allocate(int nrkeys){
		
		keyField			= new String[nrkeys];
		outputField			= new String[nrkeys];
		outputDefault		= new String[nrkeys];
		outputType			= new int[nrkeys];
		outputFormat		= new String[nrkeys];
		outputDecimal		= new String[nrkeys];
		outputGroup			= new String[nrkeys];
		
		outputLength		= new int[nrkeys];
		
		outputPrecision		= new int[nrkeys];
		outputCurrency 		= new String[nrkeys];
		
	}
	
	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {

		// append the outputFields to the output
		for (int i=0;i<outputField.length;i++)
		{
			ValueMetaInterface v=new ValueMeta(outputField[i], outputType[i]);
			v.setLength(outputLength[i]);
            v.setPrecision(outputPrecision[i]);
            v.setCurrencySymbol(outputCurrency[i]);
            v.setConversionMask(outputFormat[i]);
            v.setDecimalSymbol(outputDecimal[i]);
            v.setGroupingSymbol(outputGroup[i]);
            
			v.setOrigin(origin);
			r.addValueMeta(v);
		}		
		
	}

	public Object clone() {
		
		// field by field copy is default
		LookupStepMeta retval = (LookupStepMeta) super.clone();
		
		// add proper deep copy for the collections
		int nrKeys   = keyField.length;

		retval.allocate(nrKeys);
		
		for (int i=0;i<nrKeys;i++)
		{
			retval.keyField[i] = keyField[i];
			retval.outputField[i] = outputField[i];
			retval.outputDefault[i] = outputDefault[i];
			retval.outputType[i] = outputType[i];
			retval.outputCurrency[i] = outputCurrency[i];
			retval.outputDecimal[i] = outputDecimal[i];
			retval.outputFormat[i] = outputFormat[i];
			retval.outputGroup[i] = outputGroup[i];
			retval.outputLength[i] = outputLength[i];
			retval.outputPrecision[i] = outputPrecision[i];
		}

		return retval;
	}	
	
	public String getXML() throws KettleValueException {
		
		StringBuffer retval = new StringBuffer(150);
			
		retval.append("    ").append(XMLHandler.addTagValue("host", voldemortHost));
		retval.append("    ").append(XMLHandler.addTagValue("port", voldemortPort));
		retval.append("    ").append(XMLHandler.addTagValue("store", voldemortStore));
	        
		for (int i=0;i<keyField.length;i++)
		{
			retval.append("      <lookup>").append(Const.CR);
			retval.append("        ").append(XMLHandler.addTagValue("keyfield", keyField[i]));
			retval.append("        ").append(XMLHandler.addTagValue("outfield", outputField[i]));
			retval.append("        ").append(XMLHandler.addTagValue("default", outputDefault[i]));
			retval.append("        ").append(XMLHandler.addTagValue("type", ValueMeta.getTypeDesc(outputType[i])));
			retval.append("        ").append(XMLHandler.addTagValue("format", outputFormat[i]));
			retval.append("        ").append(XMLHandler.addTagValue("decimal", outputDecimal[i]));
			retval.append("        ").append(XMLHandler.addTagValue("group", outputGroup[i]));
			retval.append("        ").append(XMLHandler.addTagValue("length", outputLength[i]));
			retval.append("        ").append(XMLHandler.addTagValue("precision", outputPrecision[i]));
			retval.append("        ").append(XMLHandler.addTagValue("currency", outputCurrency[i]));
			
			retval.append("      </lookup>").append(Const.CR);
		}

		return retval.toString();
	}
	
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

		try {
			
			voldemortHost = XMLHandler.getTagValue(stepnode, "host");
			voldemortPort = XMLHandler.getTagValue(stepnode, "port");
			voldemortStore = XMLHandler.getTagValue(stepnode, "store");
			
			int nrKeys = XMLHandler.countNodes(stepnode, "lookup"); 
			allocate(nrKeys);
			
			for (int i=0;i<nrKeys;i++)
			{
				Node knode = XMLHandler.getSubNodeByNr(stepnode, "lookup", i);
				
				keyField[i] 		= XMLHandler.getTagValue(knode, "keyfield"); 
				outputField[i] 		= XMLHandler.getTagValue(knode, "outfield");
				outputDefault[i] 	= XMLHandler.getTagValue(knode, "default");
				outputType[i] 		= ValueMeta.getType(XMLHandler.getTagValue(knode, "type"));
				outputFormat[i] 	= XMLHandler.getTagValue(knode, "format");
				outputDecimal[i]	= XMLHandler.getTagValue(knode, "decimal");
				outputGroup[i] 		= XMLHandler.getTagValue(knode, "group");
				outputLength[i] 	= Const.toInt(XMLHandler.getTagValue(knode, "length"), -1);
				outputPrecision[i] 	= Const.toInt(XMLHandler.getTagValue(knode, "precision"), -1);
				outputCurrency[i] 	= XMLHandler.getTagValue(knode, "currency");
				
				if (outputType[i]<0){
					outputType[i]=ValueMetaInterface.TYPE_STRING;
				}
				
			}
			
		} catch (Exception e) {
			throw new KettleXMLException("Template Plugin Unable to read step info from XML node", e);
		}

	}	


	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
		try
		{
			voldemortHost = rep.getStepAttributeString(id_step, "host");
			voldemortPort = rep.getStepAttributeString(id_step, "port");
			voldemortStore = rep.getStepAttributeString(id_step, "store");
			
			int nrKeys   = rep.countNrStepAttributes(id_step, "lookup_keyfield");
			allocate(nrKeys);
			
			for (int i=0;i<nrKeys;i++)
			{
				keyField[i] 		= rep.getStepAttributeString (id_step, i, "lookup_keyfield");
				outputField[i] 		= rep.getStepAttributeString (id_step, i, "lookup_outfield");
				outputDefault[i] 	= rep.getStepAttributeString (id_step, i, "lookup_default");
				outputType[i]	 	= ValueMeta.getType( rep.getStepAttributeString (id_step, i, "lookup_type") );
				outputFormat[i] 	= rep.getStepAttributeString (id_step, i, "lookup_format");
				outputDecimal[i] 	= rep.getStepAttributeString (id_step, i, "lookup_decimal");
				outputGroup[i] 		= rep.getStepAttributeString (id_step, i, "lookup_group");
				outputLength[i] 	= Const.toInt(rep.getStepAttributeString (id_step, i, "lookup_length"), -1);
				outputPrecision[i] 	= Const.toInt(rep.getStepAttributeString (id_step, i, "lookup_precision"), -1);
				outputCurrency[i] 	= rep.getStepAttributeString (id_step, i, "lookup_currency");

			}
			
		}
		catch(Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnexpectedErrorInReadingStepInfo"), e);
		}
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException
	{
		try
		{
			rep.saveStepAttribute(id_transformation, id_step, "host", voldemortHost); 
			rep.saveStepAttribute(id_transformation, id_step, "port", voldemortPort); 
			rep.saveStepAttribute(id_transformation, id_step, "store", voldemortStore); 
            
			for (int i=0;i<keyField.length;i++)
			{
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_keyfield", keyField[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_outfield", outputField[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_default", outputDefault[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_type", ValueMeta.getTypeDesc(outputType[i]));
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_format", outputFormat[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_decimal", outputDecimal[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_group", outputGroup[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_length", outputLength[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_precision", outputPrecision[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "lookup_currency", outputCurrency[i]);
				
			}

		}
		catch(Exception e)
		{
			throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnableToSaveStepInfoToRepository")+id_step, e); 
		}
	}	
	


	public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info) {
		CheckResult cr;

		// See if we have input streams leading to this step!
		if (input.length > 0) {
			cr = new CheckResult(CheckResult.TYPE_RESULT_OK, BaseMessages.getString(PKG, "VoldemortStep.Check.StepIsReceivingInfoFromOtherSteps"), stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResult.TYPE_RESULT_ERROR, BaseMessages.getString(PKG, "VoldemortStep.Check.NoInputReceivedFromOtherSteps"), stepMeta);
			remarks.add(cr);
		}	
		
		// also check that each expected key fields are acually coming
		if (prev!=null && prev.size()>0)
		{
			boolean first=true;
			String error_message = ""; 
			boolean error_found = false;
			
			for (int i=0;i<keyField.length;i++)
			{
				ValueMetaInterface v = prev.searchValueMeta(keyField[i]);
				if (v==null)
				{
					if (first)
					{
						first=false;
						error_message+=BaseMessages.getString(PKG, "VoldemortStep.Check.MissingFieldsNotFoundInInput")+Const.CR;
					}
					error_found=true;
					error_message+="\t\t"+keyField[i]+Const.CR;
				}
			}
			if (error_found)
			{
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
			}
			else
			{
				cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString(PKG, "VoldemortStep.Check.AllFieldsFoundInInput"), stepMeta);
			}
			remarks.add(cr);
		}
		else
		{
			String error_message=BaseMessages.getString(PKG, "VoldemortStep.Check.CouldNotReadFromPreviousSteps")+Const.CR;
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR, error_message, stepMeta);
			remarks.add(cr);
		}		
    	
	}

	public StepDialogInterface getDialog(Shell shell, StepMetaInterface meta, TransMeta transMeta, String name) {
		return new LookupStepDialog(shell, meta, transMeta, name);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta, Trans disp) {
		return new LookupStep(stepMeta, stepDataInterface, cnr, transMeta, disp);
	}

	public StepDataInterface getStepData() {
		return new LookupStepData();
	}





}
