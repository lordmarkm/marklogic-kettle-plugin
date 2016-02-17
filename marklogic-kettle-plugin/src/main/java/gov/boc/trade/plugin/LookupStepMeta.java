package gov.boc.trade.plugin;

import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

/**
 * 
 * @author Mark Martinez, created Feb 17, 2016
 *
 */
public class LookupStepMeta extends BaseStepMeta implements StepMetaInterface {

    private static Class<?> PKG = LookupStepMeta.class; // for i18n purposes


    private String marklogicOdbcName;
    private String viewName;
    private String username;
    private String password;
    private String qualifier;
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
        username = "username";
        password = "password";
        qualifier = "";

        // default is to have no key lookup settings
        allocate(0);
    }

    // helper method to allocate the arrays
    public void allocate(int nrkeys){
        outputField         = new String[nrkeys];
        outputType          = new int[nrkeys];
    }

    @Override
    public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep, VariableSpace space) {
        // append the outputFields to the output
        for (int i=0;i< outputField.length; i++) {
            ValueMetaInterface v=new ValueMeta(outputField[i], outputType[i]);
            v.setOrigin(origin);
            r.addValueMeta(v);
        }
    }

    @Override
    public Object clone() {

        // field by field copy is default
        LookupStepMeta retval = (LookupStepMeta) super.clone();

        // add proper deep copy for the collections
        int nrKeys = outputField.length;

        retval.allocate(nrKeys);

        for (int i=0;i<nrKeys;i++) {
            retval.outputField[i] = outputField[i];
            retval.outputType[i] = outputType[i];
        }

        return retval;
    }

    @Override
    public String getXML() throws KettleValueException {
        StringBuffer retval = new StringBuffer(150);
        retval.append("    ").append(XMLHandler.addTagValue("marklogicOdbcName", marklogicOdbcName));
        retval.append("    ").append(XMLHandler.addTagValue("viewName", viewName));
        retval.append("    ").append(XMLHandler.addTagValue("username", username));
        retval.append("    ").append(XMLHandler.addTagValue("password", password));
        retval.append("    ").append(XMLHandler.addTagValue("qualifier", qualifier));
        for (int i=0;i<outputField.length;i++) {
            retval.append("      <lookup>").append(Const.CR);
            retval.append("        ").append(XMLHandler.addTagValue("outfield", outputField[i]));
            retval.append("        ").append(XMLHandler.addTagValue("type", ValueMeta.getTypeDesc(outputType[i])));
            retval.append("      </lookup>").append(Const.CR);
        }
        return retval.toString();
    }

    @Override
    public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleXMLException {

        try {
            marklogicOdbcName = XMLHandler.getTagValue(stepnode, "marklogicOdbcName");
            viewName = XMLHandler.getTagValue(stepnode, "viewName");
            username = XMLHandler.getTagValue(stepnode, "username");
            password = XMLHandler.getTagValue(stepnode, "password");
            qualifier = XMLHandler.getTagValue(stepnode, "qualifier");

            int nrKeys = XMLHandler.countNodes(stepnode, "lookup"); 
            allocate(nrKeys);

            for (int i=0; i < nrKeys; i++) {
                Node knode = XMLHandler.getSubNodeByNr(stepnode, "lookup", i);

                outputField[i] = XMLHandler.getTagValue(knode, "outfield");
                outputType[i] = ValueMeta.getType(XMLHandler.getTagValue(knode, "type"));

                if (outputType[i]<0){
                    outputType[i]=ValueMetaInterface.TYPE_STRING;
                }
            }

        } catch (Exception e) {
            throw new KettleXMLException("Template Plugin Unable to read step info from XML node", e);
        }

    }

    @Override
    public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters) throws KettleException {
        try {
            marklogicOdbcName = rep.getStepAttributeString(id_step, "marklogicOdbcName");
            viewName = rep.getStepAttributeString(id_step, "viewName");
            username = rep.getStepAttributeString(id_step, "username");
            password = rep.getStepAttributeString(id_step, "password");
            qualifier = rep.getStepAttributeString(id_step, "qualifier");

            int nrKeys   = rep.countNrStepAttributes(id_step, "lookup_outfield");
            allocate(nrKeys);

            for (int i=0;i<nrKeys;i++) {
                outputField[i] = rep.getStepAttributeString (id_step, i, "lookup_outfield");
                outputType[i] = ValueMeta.getType( rep.getStepAttributeString (id_step, i, "lookup_type") );
            }

        } catch(Exception e) {
            throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnexpectedErrorInReadingStepInfo"), e);
        }
    }

    @Override
    public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step) throws KettleException {
        try {
            rep.saveStepAttribute(id_transformation, id_step, "marklogicOdbcName", marklogicOdbcName);
            rep.saveStepAttribute(id_transformation, id_step, "viewName", viewName);
            rep.saveStepAttribute(id_transformation, id_step, "username", username);
            rep.saveStepAttribute(id_transformation, id_step, "password", password);
            rep.saveStepAttribute(id_transformation, id_step, "qualifier", qualifier);

            for (int i=0;i<outputField.length;i++) {
                rep.saveStepAttribute(id_transformation, id_step, i, "lookup_outfield", outputField[i]);
                rep.saveStepAttribute(id_transformation, id_step, i, "lookup_type", ValueMeta.getTypeDesc(outputType[i]));
            }

        } catch(Exception e) {
            throw new KettleException(BaseMessages.getString(PKG, "VoldemortStep.Exception.UnableToSaveStepInfoToRepository")+id_step, e); 
        }
    }

    @Override
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

}
