package net.domanowski.easirius.wizard;

import java.util.Objects;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import net.domanowski.easirius.m2t.EaSiriusApplication;

public class EaSiriusWizard extends Wizard implements INewWizard {

	protected EaSiriusWizardPageOne wizardPageOne;

	public EaSiriusWizard() {
		super();
	}

	@Override
	public String getWindowTitle() {
		return "eaSirius Wizard";
	}

	@Override
	public void addPages() {
		wizardPageOne = new EaSiriusWizardPageOne();
		addPage(wizardPageOne);
	}

	@Override
	public boolean performFinish() {
		String metamodelPath = wizardPageOne.getMetamodelPathAsString();
		String odesignPath = wizardPageOne.getOdesignPathAsString();
		String outputFolderPath = wizardPageOne.getOutputFolderPathAsString();

		Objects.requireNonNull(metamodelPath);
		Objects.requireNonNull(odesignPath);
		Objects.requireNonNull(outputFolderPath);

		EaSiriusApplication.generateEditor(odesignPath, metamodelPath, outputFolderPath);
		MessageDialog.openInformation(getShell(), "eaSirius", "Generated eaSirius Editor in path " + outputFolderPath);

		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}