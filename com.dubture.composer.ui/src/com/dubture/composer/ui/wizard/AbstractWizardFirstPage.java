package com.dubture.composer.ui.wizard;

import java.io.File;
import java.net.URI;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.php.internal.core.PHPVersion;
import org.eclipse.php.internal.ui.wizards.CompositeData;
import org.eclipse.php.internal.ui.wizards.DetectGroup;
import org.eclipse.php.internal.ui.wizards.IPHPProjectCreateWizardPage;
import org.eclipse.php.internal.ui.wizards.LocationGroup;
import org.eclipse.php.internal.ui.wizards.NameGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.dubture.composer.ui.converter.String2KeywordsConverter;
import com.dubture.composer.ui.wizard.project.VersionGroup;
import com.dubture.getcomposer.core.ComposerPackage;

@SuppressWarnings("restriction")
abstract public class AbstractWizardFirstPage extends WizardPage implements IPHPProjectCreateWizardPage, Observer {

	public NameGroup nameGroup;
	public LocationGroup PHPLocationGroup;
	public VersionGroup versionGroup;
	
	protected String fInitialName;
	protected Composite composite;
	protected AbstractValidator validator;
	protected DetectGroup detectGroup;
	protected ComposerPackage composerPackage;
	protected String2KeywordsConverter keywordConverter;
	
	protected AbstractWizardFirstPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		
		composite = new Composite(parent, SWT.NULL);
		composite.setFont(parent.getFont());
		composite.setLayout(initGridLayout(new GridLayout(1, false), false));
		composite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		fInitialName = "";
		// create UI elements
		nameGroup = new NameGroup(composite, fInitialName, getShell());
		nameGroup.addObserver(this);
		validator = getValidator();
		
		beforeLocationGroup();
		
		PHPLocationGroup = new LocationGroup(composite, nameGroup, getShell());
		
		CompositeData data = new CompositeData();
		data.setParetnt(composite);
		data.setSettings(getDialogSettings());
		data.setObserver(PHPLocationGroup);

		versionGroup = new VersionGroup(this, composite);
		detectGroup = new DetectGroup(composite, PHPLocationGroup, nameGroup);

		nameGroup.addObserver(PHPLocationGroup);

		PHPLocationGroup.addObserver(detectGroup);
		// initialize all elements
		nameGroup.notifyObservers();
		// create and connect validator

		nameGroup.addObserver(validator);
		PHPLocationGroup.addObserver(validator);

		Dialog.applyDialogFont(composite);
		
		afterLocationGroup();
		setControl(composite);
		
		composerPackage = new ComposerPackage();
		keywordConverter = new String2KeywordsConverter(composerPackage);
		
		finishControlSetup();
	}

	public void performFinish(IProgressMonitor monitor) {
		
	}

	abstract protected void finishControlSetup();
	abstract protected void beforeLocationGroup();
	abstract protected void afterLocationGroup();
	abstract protected AbstractValidator getValidator();
	abstract public void update(Observable o, Object arg);
	abstract public void initPage();
	abstract protected void installHelp();
	
	public GridLayout initGridLayout(GridLayout layout, boolean margins) {
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		if (margins) {
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		} else {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		return layout;
	}
	
	
	public IProject getProjectHandle() {
		return ResourcesPlugin.getWorkspace().getRoot().getProject(nameGroup.getName());
	}


	public IEnvironment getEnvironment() {
		return PHPLocationGroup.getEnvironment();
	}

	public boolean isInWorkspace() {
		return PHPLocationGroup.isInWorkspace();
	}

	public boolean isInLocalServer() {
		return PHPLocationGroup.isInLocalServer();
	}

	public boolean canCreate(File file) {
		while (!file.exists()) {
			file = file.getParentFile();
			if (file == null)
				return false;
		}

		return file.canWrite();
	}
	
	public URI getLocationURI() {
		IEnvironment environment = getEnvironment();
		return environment.getURI(PHPLocationGroup.getLocation());
	}

	public boolean getDetect() {
		return detectGroup.mustDetect();
	}

	public boolean hasPhpSourceFolder() {
		return true;
	}

	public boolean isDefaultVersionSelected() {
		return false;
	}

	public boolean getUseAspTagsValue() {
		return versionGroup != null && versionGroup.fConfigurationBlock.getUseAspTagsValue();
	}

	public PHPVersion getPHPVersionValue() {
		if (versionGroup != null) {
			return versionGroup.fConfigurationBlock.getPHPVersionValue();
		}
		return null;
	}
	
	public ComposerPackage getPackage() {
		return composerPackage;
	}
	
	
}
