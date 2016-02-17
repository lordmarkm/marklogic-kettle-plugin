package gov.boc.trade.plugin;

import java.util.List;
import java.util.Map;

import org.dom4j.io.OutputFormat;
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

import com.jcabi.xml.XMLDocument;

public class LookupStepMeta extends BaseStepMeta implements StepMetaInterface {

    private static Class<?> PKG = LookupStepMeta.class; // for i18n purposes


    private String marklogicOdbcName;
    private String viewName;
    private String keyField[];
    private String[] outputField;
    private int[] outputType;   

    public LookupStepMeta() {
        super(); 
    }

    // set sensible defaults for a new step
    @Override
    public void setDefault() {
        marklogicOdbcName = "MarkLogicSQL";
        viewName = "import_items";

        // default is to have no key lookup settings
        allocate(0);
    }

    // helper method to allocate the arrays
    public void allocate(int nrkeys){
        keyField            = new String[nrkeys];
        outputField         = new String[nrkeys];
        outputType          = new int[nrkeys];
    }

    public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
        // append the outputFields to the output
        for (int i=0;i< outputField.length; i++) {
            ValueMetaInterface v=new ValueMeta(outputField[i], outputType[i]);
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

        for (int i=0;i<nrKeys;i++) {
            retval.keyField[i] = keyField[i];
            retval.outputField[i] = outputField[i];
            retval.outputType[i] = outputType[i];
        }

        return retval;
    }

    public String getXML() throws KettleValueException {
        StringBuffer retval = new StringBuffer(150);
        retval.append("    ").append(XMLHandler.addTagValue("marklogicOdbcName", marklogicOdbcName));
        retval.append("    ").append(XMLHandler.addTagValue("viewName", viewName));
        for (int i=0;i<keyField.length;i++) {
            retval.append("      <lookup>").append(Const.CR);
            retval.append("        ").append(XMLHandler.addTagValue("keyfield", keyField[i]));
            retval.append("        ").append(XMLHandler.addTagValue("outfield", outputField[i]));
            retval.append("        ").append(XMLHandler.addTagValue("type", ValueMeta.getTypeDesc(outputType[i])));
            retval.append("      </lookup>").append(Const.CR);
        }
        return retval.toString();
    }

    public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

        String xml = new XMLDocument(stepnode).toString();
        System.out.println(xml);

        try {

            marklogicOdbcName = XMLHandler.getTagValue(stepnode, "marklogicOdbcName");
            int nrKeys = XMLHandler.countNodes(stepnode, "lookup"); 
            allocate(nrKeys);

            for (int i=0;i<nrKeys;i++)
            {
                Node knode = XMLHandler.getSubNodeByNr(stepnode, "lookup", i);

                keyField[i] 		= XMLHandler.getTagValue(knode, "keyfield"); 
                outputField[i] 		= XMLHandler.getTagValue(knode, "outfield");
                outputType[i] 		= ValueMeta.getType(XMLHandler.getTagValue(knode, "type"));

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
            marklogicOdbcName = rep.getStepAttributeString(id_step, "marklogicOdbcName");
            viewName = rep.getStepAttributeString(id_step, "viewName");

            int nrKeys   = rep.countNrStepAttributes(id_step, "lookup_keyfield");
            allocate(nrKeys);

            for (int i=0;i<nrKeys;i++) {
                keyField[i] = rep.getStepAttributeString (id_step, i, "lookup_keyfield");
                outputField[i] = rep.getStepAttributeString (id_step, i, "lookup_outfield");
                outputType[i] = ValueMeta.getType( rep.getStepAttributeString (id_step, i, "lookup_type") );
            }

        } catch(Exception e) {
            throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnexpectedErrorInReadingStepInfo"), e);
        }
    }

    public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        try {
            rep.saveStepAttribute(id_transformation, id_step, "marklogicOdbcName", marklogicOdbcName);
            rep.saveStepAttribute(id_transformation, id_step, "viewName", viewName);

            for (int i=0;i<keyField.length;i++) {
                rep.saveStepAttribute(id_transformation, id_step, i, "lookup_keyfield", keyField[i]);
                rep.saveStepAttribute(id_transformation, id_step, i, "lookup_outfield", outputField[i]);
                rep.saveStepAttribute(id_transformation, id_step, i, "lookup_type", ValueMeta.getTypeDesc(outputType[i]));
            }

        } catch(Exception e) {
            throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnableToSaveStepInfoToRepository")+id_step, e); 
        }
    }



    public void check(List<CheckResultInterface> remarks, TransMeta transmeta, StepMeta stepMeta, RowMetaInterface prev, String input[], String output[], RowMetaInterface info) {
        //Do nothing
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

    // getters and setters for the step settings
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

    public int[] getOutputType() {
        return outputType;
    }

    public void setOutputType(int[] outputType) {
        this.outputType = outputType;
    }

    public String getMarklogicOdbcName() {
        return marklogicOdbcName;
    }

    public void setMarklogicOdbcName(String marklogicOdbcName) {
        this.marklogicOdbcName = marklogicOdbcName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

}
