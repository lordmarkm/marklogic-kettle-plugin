package gov.boc.trade.plugin;

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
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.core.widget.TextVar;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

/**
 * 
 * @author Mark Martinez, created Feb 17, 2016
 *
 */
public class LookupStepDialog extends BaseStepDialog implements StepDialogInterface {

    private static Class<?> PKG = LookupStepMeta.class; // for i18n purposes

    private LookupStepMeta input;

    // connection settings widgets
    private Label wlMarkLogicOdbcName;
    private TextVar wMarkLogicOdbcName;
    private Label wlMarkLogicViewName;
    private TextVar wMarkLogicViewName;
    private Label wlMarkLogicUserName;
    private TextVar wMarkLogicUserName;
    private Label wlMarkLogicPassword;
    private TextVar wMarkLogicPassword;

    // lookup fields settings widgets
    private Label wlKeys;
    private TableView wKeys;

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
        shell.setText("MarkLogic ODBC Lookup"); 

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
        gConnect.setText("MarkLogic OBDC Connection information"); 
        FormLayout gConnectLayout = new FormLayout();
        gConnectLayout.marginWidth = 3;
        gConnectLayout.marginHeight = 3;
        gConnect.setLayout(gConnectLayout);
        props.setLook(gConnect);

        // Voldemort Host
        wlMarkLogicOdbcName = new Label(gConnect, SWT.RIGHT);
        wlMarkLogicOdbcName.setText("MarkLogic ODBC name"); 
        props.setLook(wlMarkLogicOdbcName);
        FormData fdlVoldemortHost = new FormData();
        fdlVoldemortHost.top = new FormAttachment(0, margin);
        fdlVoldemortHost.left = new FormAttachment(0, 0);
        fdlVoldemortHost.right = new FormAttachment(middle, -margin);
        wlMarkLogicOdbcName.setLayoutData(fdlVoldemortHost);
        wMarkLogicOdbcName = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wMarkLogicOdbcName.addModifyListener(lsMod);
        wMarkLogicOdbcName.setToolTipText("MarkLogic ODBC name"); 
        props.setLook(wMarkLogicOdbcName);
        FormData fdOdbcName = new FormData();
        fdOdbcName.top = new FormAttachment(0, margin);
        fdOdbcName.left = new FormAttachment(middle, 0);
        fdOdbcName.right = new FormAttachment(100, 0);
        wMarkLogicOdbcName.setLayoutData(fdOdbcName);

        // Voldemort Port
        wlMarkLogicViewName = new Label(gConnect, SWT.RIGHT);
        wlMarkLogicViewName.setText("View name"); 
        props.setLook(wlMarkLogicViewName);
        FormData fdlVoldemortPassword = new FormData();
        fdlVoldemortPassword.top = new FormAttachment(wMarkLogicOdbcName, margin);
        fdlVoldemortPassword.left = new FormAttachment(0, 0);
        fdlVoldemortPassword.right = new FormAttachment(middle, -margin);
        wlMarkLogicViewName.setLayoutData(fdlVoldemortPassword);
        wMarkLogicViewName = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wMarkLogicViewName.addModifyListener(lsMod);
        wMarkLogicViewName.setToolTipText("View name"); 
        props.setLook(wMarkLogicViewName);
        FormData fdViewName = new FormData();
        fdViewName.top = new FormAttachment(wMarkLogicOdbcName, margin);
        fdViewName.left = new FormAttachment(middle, 0);
        fdViewName.right = new FormAttachment(100, 0);
        wMarkLogicViewName.setLayoutData(fdViewName);

        //Username
        wlMarkLogicUserName = new Label(gConnect, SWT.RIGHT);
        wlMarkLogicUserName.setText("Username"); 
        props.setLook(wlMarkLogicUserName);
        FormData fdlMarkLogicUsername = new FormData();
        fdlMarkLogicUsername.top = new FormAttachment(wMarkLogicViewName, margin);
        fdlMarkLogicUsername.left = new FormAttachment(0, 0);
        fdlMarkLogicUsername.right = new FormAttachment(middle, -margin);
        wlMarkLogicUserName.setLayoutData(fdlMarkLogicUsername);
        wMarkLogicUserName = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wMarkLogicUserName.addModifyListener(lsMod);
        wMarkLogicUserName.setToolTipText("Username"); 
        props.setLook(wMarkLogicViewName);
        FormData fdUsername = new FormData();
        fdUsername.top = new FormAttachment(wMarkLogicViewName, margin);
        fdUsername.left = new FormAttachment(middle, 0);
        fdUsername.right = new FormAttachment(100, 0);
        wMarkLogicUserName.setLayoutData(fdUsername);

        //Password
        wlMarkLogicPassword = new Label(gConnect, SWT.RIGHT);
        wlMarkLogicPassword.setText("Password"); 
        props.setLook(wlMarkLogicPassword);
        FormData fdlPassword = new FormData();
        fdlPassword.top = new FormAttachment(wMarkLogicUserName, margin);
        fdlPassword.left = new FormAttachment(0, 0);
        fdlPassword.right = new FormAttachment(middle, -margin);
        wlMarkLogicPassword.setLayoutData(fdlPassword);
        wMarkLogicPassword = new TextVar(transMeta, gConnect, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
        wMarkLogicPassword.addModifyListener(lsMod);
        wMarkLogicPassword.setToolTipText("Password"); 
        props.setLook(wMarkLogicPassword);
        FormData fdPassword = new FormData();
        fdPassword.top = new FormAttachment(wMarkLogicUserName, margin);
        fdPassword.left = new FormAttachment(middle, 0);
        fdPassword.right = new FormAttachment(100, 0);
        wMarkLogicPassword.setLayoutData(fdPassword);

        FormData fdConnect = new FormData();
        fdConnect.left = new FormAttachment(0, 0);
        fdConnect.right = new FormAttachment(100, 0);
        fdConnect.top = new FormAttachment(wStepname, margin);
        gConnect.setLayoutData(fdConnect);

        /*************************************************
        // KEY / LOOKUP TABLE
         *************************************************/

        wlKeys=new Label(shell, SWT.NONE);
        wlKeys.setText("Fields to retrieve"); 
        props.setLook(wlKeys);
        FormData fdlReturn=new FormData();
        fdlReturn.left  = new FormAttachment(0, 0);
        fdlReturn.top   = new FormAttachment(gConnect, margin);
        wlKeys.setLayoutData(fdlReturn);

        int keyWidgetCols=2;
        int keyWidgetRows= (input.getOutputField()!=null?input.getOutputField().length:1);

        ColumnInfo[] ciKeys=new ColumnInfo[keyWidgetCols];
        ciKeys[0]=new ColumnInfo("Column name",  ColumnInfo.COLUMN_TYPE_TEXT, false); 
        ciKeys[1]=new ColumnInfo("Data type", ColumnInfo.COLUMN_TYPE_CCOMBO, ValueMeta.getTypes()); 

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
        wMarkLogicOdbcName.addSelectionListener(lsDef);
        wMarkLogicViewName.addSelectionListener(lsDef);
        wMarkLogicUserName.addSelectionListener(lsDef);
        wMarkLogicPassword.addSelectionListener(lsDef);

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
        //setComboValues();

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

        if (input.getMarklogicOdbcName() != null) {
            wMarkLogicOdbcName.setText(input.getMarklogicOdbcName());   
        }
        if (input.getViewName() != null) {
            wMarkLogicViewName.setText(input.getViewName());
        }
        if (input.getUsername() != null) {
            wMarkLogicUserName.setText(input.getUsername());
        }
        if (input.getPassword() != null) {
            wMarkLogicPassword.setText(input.getPassword());
        }

        if (input.getOutputField()!=null){


            for (int i=0;i<input.getOutputField().length;i++){

                TableItem item = wKeys.table.getItem(i);

                if (input.getOutputField()[i] != null){
                    item.setText(1, input.getOutputField()[i]);
                } 

                item.setText(2, ValueMeta.getTypeDesc(input.getOutputType()[i]));

            }
        }

        wKeys.setRowNums();
        wKeys.optWidth(true);       
    }

    private void cancel() {
        stepname = null;
        input.setChanged(backupChanged);
        dispose();
    }

    // let the meta know about the entered data
    private void ok() {
        stepname = wStepname.getText(); // return value

        input.setMarklogicOdbcName(wMarkLogicOdbcName.getText());
        input.setViewName(wMarkLogicViewName.getText());
        input.setUsername(wMarkLogicUserName.getText());
        input.setPassword(wMarkLogicPassword.getText());

        int nrKeys= wKeys.nrNonEmpty();

        input.allocate(nrKeys);

        for (int i=0;i<nrKeys;i++) {
            TableItem item = wKeys.getNonEmpty(i);
            input.getOutputField()[i] = item.getText(1);
            input.getOutputType()[i] = ValueMeta.getType(item.getText(2));

            // fix unknowns
            if (input.getOutputType()[i]<0){
                input.getOutputType()[i]=ValueMetaInterface.TYPE_STRING;
            }


        }

        dispose();
    }
}
