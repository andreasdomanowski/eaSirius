package net.domanowski.easirius.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import net.domanowski.easirius.wizard.EaSiriusWizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

public class OpenEaSiriusWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		IWizard wizard = new EaSiriusWizard();
		WizardDialog wizardDialog = new WizardDialog(activeShell, wizard);
		wizardDialog.open();
		return null;
	}
}
