package plugin.voldemort.lookup;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;

public class LookupStepDialog extends BaseStepDialog implements StepDialogInterface {

	private static Class<?> PKG = LookupStepMeta.class; // for i18n purposes
	
	private LookupStepMeta input;

	// connection settings widgets
	private Label wlVoldemortHost;
	private TextVar wVoldemortHost;
	private Label wlVoldemortPort;
	private TextVar wVoldemortPort;
	private Label wlVoldemortStore;
	private TextVar wVoldemortStore;
	
	// lookup fields settings widgets
	private Label wlKeys;
	private TableView wKeys;
	
	// all fields from the previous steps, used for dropdown selection
	private RowMetaInterface prevFields = null;
	
	// the dropdown column which should contain previous fields from stream
	private ColumnInfo fieldColumn = null; 
	
	// constructor
	public LookupStepDialog(Shell parent, Object in, TransMeta transMeta, String sname) {
		super(parent, (BaseStepMeta) in, transMeta, sname);
		input = (LookupStepMeta) in;
	}

	// builds and shows the dialog 
	public String open() {
		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX);
		props.setLook(shell);
		setShellImage(shell, input);

		ModifyListener lsMod = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				input.setChanged();
			}
		};
		backupChanged = input.hasChanged();

		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = Const.FORM_MARGIN;
		formLayout.marginHeight = Const.FORM_MARGIN;

		shell.setLayout(formLayout);
		shell.setText(BaseMessages.getString(PKG, "VoldemortDialog.Shell.Title")); 

		int middle = props.getMiddlePct();
		int margin = Const.MARGIN;

	    /*************************************************
        // STEP NAME ENTRY
		*************************************************/
		
		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName")); 
		props.setLook(wlStepname);
		fdlStepname = new FormData();
		fdlStepname.left = new FormAttachment(0, 0);
		fdlStepname.right = new FormAttachment(middle, -margin);
		fdlStepname.top = new FormAttachment(0, margin);
		wlStepname.setLayoutData(fdlStepname);
		
		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		fdStepname = new FormData();
		fdStepname.left = new FormAttachment(middle, 0);
		fdStepname.top = new FormAttachment(0, margin);
		fdStepname.right = new FormAttachment(100, 0);
		wStepname.setLayoutData(fdStepname);
		
	    /*************************************************
        // VOLDEMORT CONNECTION GROUP
		*************************************************/
		
        Group gConnect = new Group(shell, SWT.SHADOW_ETCHED_IN);
        gConnect.setText(BaseMessages.getString(PKG, "VoldemortDialog.ConnectGroup.Label")); 
        FormLayout gConnectLayout = new FormLayout();
        gConnectLayout.marginWidth = 3;
        gConnectLayout.marginHeight = 3;
        gConnect.setLayout(gConnectLayout);
        props.setLook(gConnect);

        // Voldemort Host
        wlVoldemortHost = new Label(gConnect, SWT.RIGHT);
        wlVoldemortHost.setText(BaseMessages.getString(PKG, "VoldemortDialog.Host.Label")); 
        props.setLook(wlVoldemortHost);
        FormData fdlVoldemortHost = new FormData();
        fdlVoldemortHost.top = new FormAttachment(0, margin);
        fdlVoldemortHost.left = new FormAttachment(0, 0);
        fdlVoldemortHost.right = new FormAttachment(middle, -margin);
        wlVoldemortHost.setLayoutData(fdlVoldemortHost);
        wVoldemortHost = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wVoldemortHost.addModifyListener(lsMod);
        wVoldemortHost.setToolTipText(BaseMessages.getString(PKG, "VoldemortDialog.Host.Tooltip")); 
        props.setLook(wVoldemortHost);
        FormData fdVoldemortHost = new FormData();
        fdVoldemortHost.top = new FormAttachment(0, margin);
        fdVoldemortHost.left = new FormAttachment(middle, 0);
        fdVoldemortHost.right = new FormAttachment(100, 0);
        wVoldemortHost.setLayoutData(fdVoldemortHost);

        // Voldemort Port
        wlVoldemortPort = new Label(gConnect, SWT.RIGHT);
        wlVoldemortPort.setText(BaseMessages.getString(PKG, "VoldemortDialog.Port.Label")); 
        props.setLook(wlVoldemortPort);
        FormData fdlVoldemortPassword = new FormData();
        fdlVoldemortPassword.top = new FormAttachment(wVoldemortHost, margin);
        fdlVoldemortPassword.left = new FormAttachment(0, 0);
        fdlVoldemortPassword.right = new FormAttachment(middle, -margin);
        wlVoldemortPort.setLayoutData(fdlVoldemortPassword);
        wVoldemortPort = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wVoldemortPort.addModifyListener(lsMod);
        wVoldemortPort.setToolTipText(BaseMessages.getString(PKG, "VoldemortDialog.Port.Tooltip")); 
        props.setLook(wVoldemortPort);
        FormData fdVoldemortPassword = new FormData();
        fdVoldemortPassword.top = new FormAttachment(wVoldemortHost, margin);
        fdVoldemortPassword.left = new FormAttachment(middle, 0);
        fdVoldemortPassword.right = new FormAttachment(100, 0);
        wVoldemortPort.setLayoutData(fdVoldemortPassword);
        
        // Voldemort storage name
        wlVoldemortStore = new Label(gConnect, SWT.RIGHT);
        wlVoldemortStore.setText(BaseMessages.getString(PKG, "VoldemortDialog.Store.Label")); 
        props.setLook(wlVoldemortStore);
        FormData fdlVoldemortStore = new FormData();
        fdlVoldemortStore.top = new FormAttachment(wVoldemortPort, margin);
        fdlVoldemortStore.left = new FormAttachment(0, 0);
        fdlVoldemortStore.right = new FormAttachment(middle, -margin);
        wlVoldemortStore.setLayoutData(fdlVoldemortStore);
        wVoldemortStore = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wVoldemortStore.addModifyListener(lsMod);
        wVoldemortStore.setToolTipText(BaseMessages.getString(PKG, "VoldemortDialog.Store.Tooltip")); 
        props.setLook(wVoldemortStore);
        FormData fdVoldemortStore = new FormData();
        fdVoldemortStore.top = new FormAttachment(wVoldemortPort, margin);
        fdVoldemortStore.left = new FormAttachment(middle, 0);
        fdVoldemortStore.right = new FormAttachment(100, 0);
        wVoldemortStore.setLayoutData(fdVoldemortStore);        
        
        FormData fdConnect = new FormData();
        fdConnect.left = new FormAttachment(0, 0);
        fdConnect.right = new FormAttachment(100, 0);
        fdConnect.top = new FormAttachment(wStepname, margin);
        gConnect.setLayoutData(fdConnect);

	    /*************************************************
        // KEY / LOOKUP TABLE
		*************************************************/
		
        wlKeys=new Label(shell, SWT.NONE);
        wlKeys.setText(BaseMessages.getString(PKG, "VoldemortDialog.Return.Label")); 
        props.setLook(wlKeys);
        FormData fdlReturn=new FormData();
        fdlReturn.left  = new FormAttachment(0, 0);
        fdlReturn.top   = new FormAttachment(gConnect, margin);
        wlKeys.setLayoutData(fdlReturn);
        
        int keyWidgetCols=10;
        int keyWidgetRows= (input.getKeyField()!=null?input.getKeyField().length:1);
        
        ColumnInfo[] ciKeys=new ColumnInfo[keyWidgetCols];
        ciKeys[0]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.KeyField"),    ColumnInfo.COLUMN_TYPE_CCOMBO,  new String[]{}, false); 
        ciKeys[1]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.ValueField"),	ColumnInfo.COLUMN_TYPE_TEXT, false); 
        ciKeys[2]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Default"),  	ColumnInfo.COLUMN_TYPE_TEXT,   false); 
        ciKeys[3]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Type"),     	ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes()); 
        ciKeys[4]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Format"),    	ColumnInfo.COLUMN_TYPE_FORMAT, 4);
        ciKeys[5]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Length"),    	ColumnInfo.COLUMN_TYPE_TEXT,   false);
        ciKeys[6]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Precision"), 	ColumnInfo.COLUMN_TYPE_TEXT,   false);
        ciKeys[7]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Currency"),  	ColumnInfo.COLUMN_TYPE_TEXT,   false);
        ciKeys[8]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Decimal"),   	ColumnInfo.COLUMN_TYPE_TEXT,   false);
        ciKeys[9]=new ColumnInfo(BaseMessages.getString(PKG, "VoldemortDialog.ColumnInfo.Group"),     	ColumnInfo.COLUMN_TYPE_TEXT,   false);
        
        fieldColumn = ciKeys[0];
        
        wKeys=new TableView(transMeta, shell, 
                              SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL, 
                              ciKeys, 
                              keyWidgetRows,  
                              lsMod,
                              props
                              );

        FormData fdReturn=new FormData();
        fdReturn.left  = new FormAttachment(0, 0);
        fdReturn.top   = new FormAttachment(wlKeys, margin);
        fdReturn.right = new FormAttachment(100, 0);
        fdReturn.bottom= new FormAttachment(100, -50);
        wKeys.setLayoutData(fdReturn);        
		      
	    /*************************************************
        // OK AND CANCEL BUTTONS 
		*************************************************/

		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK")); 
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel")); 

		BaseStepDialog.positionBottomButtons(shell, new Button[] { wOK, wCancel }, margin, wKeys);

		
		// Add listeners
		lsCancel = new Listener() {
			public void handleEvent(Event e) {
				cancel();
			}
		};
		lsOK = new Listener() {
			public void handleEvent(Event e) {
				ok();
			}
		};

		wCancel.addListener(SWT.Selection, lsCancel);
		wOK.addListener(SWT.Selection, lsOK);

	    /*************************************************
        // DEFAULT ACTION LISTENERS 
		*************************************************/
		
		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wVoldemortHost.addSelectionListener(lsDef);
		wVoldemortPort.addSelectionListener(lsDef);
		wVoldemortStore.addSelectionListener(lsDef);

		// Detect X or ALT-F4 or something that kills this window...
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});

		
		// Set the shell size, based upon previous time...
		setSize();

	    /*************************************************
        // POPULATE AND OPEN DIALOG
		*************************************************/
		
		getData();
		setComboValues();
		
		input.setChanged(backupChanged);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return stepname;
	}
	
	// Collect data from the meta and place it in the dialog
	public void getData() {
		
		wStepname.selectAll();
		
		if (input.getVoldemortHost() != null){
			wVoldemortHost.setText(input.getVoldemortHost());	
		}
		
		if (input.getVoldemortPort() != null){
			wVoldemortPort.setText(input.getVoldemortPort());	
		}
		
		if (input.getVoldemortStore() != null){
			wVoldemortStore.setText(input.getVoldemortStore());	
		}
		
		if (input.getKeyField()!=null){
			
		
			for (int i=0;i<input.getKeyField().length;i++){
				
				TableItem item = wKeys.table.getItem(i);
				
				if (input.getKeyField()[i] != null){
					item.setText(1, input.getKeyField()[i]);
				} 
				
				if (input.getOutputField()[i] != null){
					item.setText(2, input.getOutputField()[i]);
				} 
				
				if (input.getOutputDefault()[i] != null){
					item.setText(3, input.getOutputDefault()[i]);	
				}
				
				item.setText(4, ValueMeta.getTypeDesc(input.getOutputType()[i]));
				
				if (input.getOutputFormat()[i] != null){
					item.setText(5, input.getOutputFormat()[i]);	
				}
				item.setText(6, input.getOutputLength()[i]<0?"":""+input.getOutputLength()[i]);
				item.setText(7, input.getOutputPrecision()[i]<0?"":""+input.getOutputPrecision()[i]);
				
				if (input.getOutputCurrency()[i] != null){
					item.setText(8, input.getOutputCurrency()[i]);	
				}
				
				if (input.getOutputDecimal()[i] != null){
					item.setText(9, input.getOutputDecimal()[i]);
				} 
				
				if (input.getOutputGroup()[i] != null){
					item.setText(10, input.getOutputGroup()[i]);
				} 
				
			}
		}
		
		wKeys.setRowNums();
		wKeys.optWidth(true);		
	}
	
	// asynchronous filling of the combo boxes
	private void setComboValues() {
		Runnable fieldLoader = new Runnable() {
			public void run() {
				try {
					prevFields = transMeta.getPrevStepFields(stepname);
				} catch (KettleException e) {
					prevFields = new RowMeta();
					String msg = BaseMessages.getString(PKG, "VoldemortDialog.DoMapping.UnableToFindInput");
					logError(msg);
				}
				String[] prevStepFieldNames = prevFields.getFieldNames();
				Arrays.sort(prevStepFieldNames);
				fieldColumn.setComboValues(prevStepFieldNames);
				
			}
		};
		new Thread(fieldLoader).start();
	}	

	private void cancel() {
		stepname = null;
		input.setChanged(backupChanged);
		dispose();
	}
	
	// let the meta know about the entered data
	private void ok() {
		stepname = wStepname.getText(); // return value
		
		input.setVoldemortHost(wVoldemortHost.getText());
		input.setVoldemortPort(wVoldemortPort.getText());
		input.setVoldemortStore(wVoldemortStore.getText());
		
		int nrKeys= wKeys.nrNonEmpty();
		
		input.allocate(nrKeys);
		
		for (int i=0;i<nrKeys;i++)
		{
			TableItem item = wKeys.getNonEmpty(i);
			input.getKeyField()[i] = item.getText(1);
			input.getOutputField()[i] = item.getText(2);
			
			input.getOutputDefault()[i] = item.getText(3);
			input.getOutputType()[i] = ValueMeta.getType(item.getText(4));

			// fix unknowns
			if (input.getOutputType()[i]<0){
				input.getOutputType()[i]=ValueMetaInterface.TYPE_STRING;
			}
			
			input.getOutputFormat()[i] = item.getText(5); 
			input.getOutputLength()[i] = Const.toInt(item.getText(6), -1);
			input.getOutputPrecision()[i] = Const.toInt(item.getText(7), -1);
			input.getOutputCurrency()[i] = item.getText(8);
			input.getOutputDecimal()[i] = item.getText(9);
			input.getOutputGroup()[i] = item.getText(10);
			
		}		
		
		dispose();
	}
}
