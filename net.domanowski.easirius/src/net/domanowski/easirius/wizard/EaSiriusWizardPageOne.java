package net.domanowski.easirius.wizard;

import java.io.File;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EaSiriusWizardPageOne extends WizardPage {

	private static final String MESSAGE_PATH_OUTPUT_FOLDER = "Output folder:";

	private static final String MESSAGE_PATH_METAMODEL = "Path to Metamodel:";

	private static final String MESSAGE_PATH_EDITOR_SPECIFICATION = "Path to Editor Specification";

	private static final String MESSAGE_BUTTON_SELECT = "Select";

	private Text textboxOutputPath;
	private Text textboxMetamodelPath;
	private Text textboxOdesignPath;

	public EaSiriusWizardPageOne() {
		super("");
		setTitle("eaSirius");
		setDescription(
				"Specify paths to the Sirius editor specification, the respective metamodel, and the output folder.");
	}

	@Override
	public void createControl(Composite parent) {
		setPageComplete(false);

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout containerLayout = new GridLayout(3, false);
		container.setLayout(containerLayout);

		GridData gridDataGrabHorizontalExcess = new GridData();
		gridDataGrabHorizontalExcess.grabExcessHorizontalSpace = true;
		gridDataGrabHorizontalExcess.horizontalAlignment = GridData.FILL;

		Label labelOdesign = new Label(container, SWT.NONE);
		labelOdesign.setText(MESSAGE_PATH_EDITOR_SPECIFICATION);
		textboxOdesignPath = new Text(container, SWT.BORDER);
		textboxOdesignPath.setText("");
		textboxOdesignPath.setLayoutData(gridDataGrabHorizontalExcess);
		textboxOdesignPath.setEnabled(false);
		Button textboxSelectOdesign = new Button(container, SWT.NONE);
		textboxSelectOdesign.setText(MESSAGE_BUTTON_SELECT);
		textboxSelectOdesign.addListener(SWT.Selection, l -> openFileDialogAndSetTextboxUponFinish(textboxOdesignPath));

		Label labelMetamodel = new Label(container, SWT.NONE);
		labelMetamodel.setText(MESSAGE_PATH_METAMODEL);
		textboxMetamodelPath = new Text(container, SWT.BORDER);
		textboxMetamodelPath.setText("");
		textboxMetamodelPath.setLayoutData(gridDataGrabHorizontalExcess);
		textboxMetamodelPath.setEnabled(false);
		Button button = new Button(container, SWT.NONE);
		button.setText(MESSAGE_BUTTON_SELECT);
		button.addListener(SWT.Selection, l -> openFileDialogAndSetTextboxUponFinish(textboxMetamodelPath));

		Label labelOutputPath = new Label(container, SWT.NONE);
		labelOutputPath.setText(MESSAGE_PATH_OUTPUT_FOLDER);
		textboxOutputPath = new Text(container, SWT.BORDER);
		textboxOutputPath.setText("");
		textboxOutputPath.setLayoutData(gridDataGrabHorizontalExcess);
		textboxOutputPath.setEnabled(false);
		Button buttonSelectOutputPath = new Button(container, SWT.NONE);
		buttonSelectOutputPath.setText(MESSAGE_BUTTON_SELECT);
		buttonSelectOutputPath.addListener(SWT.Selection, e -> {
			DirectoryDialog dialog = new DirectoryDialog(getShell());
			String dirResult = dialog.open();
			if (dirResult != null) {
				textboxOutputPath.setText(dirResult);
			}
		});

		ModifyListener onInputChangeListener = e -> {
			File metamodelFile = new File(textboxOdesignPath.getText());
			File odesignFile = new File(textboxOdesignPath.getText());
			File outputFolderFile = new File(textboxOutputPath.getText());

			if (metamodelFile.exists() && metamodelFile.isFile() && odesignFile.exists() && odesignFile.isFile()
					&& outputFolderFile.exists() && outputFolderFile.isDirectory()) {
				setPageComplete(true);
			}
		};

		textboxOdesignPath.addModifyListener(onInputChangeListener);
		textboxMetamodelPath.addModifyListener(onInputChangeListener);
		textboxOutputPath.addModifyListener(onInputChangeListener);

		setControl(container);
	}

	private void openFileDialogAndSetTextboxUponFinish(Text textBox) {
		FileDialog dialog = new FileDialog(getShell());
		String filePath = dialog.open();
		if (filePath != null) {
			textBox.setText(filePath);
		}
	}

	protected String getOdesignPathAsString() {
		if (textboxOdesignPath != null) {
			return textboxOdesignPath.getText();
		}
		return null;
	}

	protected String getMetamodelPathAsString() {
		if (textboxMetamodelPath != null) {
			return textboxMetamodelPath.getText();
		}
		return null;
	}

	protected String getOutputFolderPathAsString() {
		if (textboxOutputPath != null) {
			return textboxOutputPath.getText();
		}
		return null;
	}

}