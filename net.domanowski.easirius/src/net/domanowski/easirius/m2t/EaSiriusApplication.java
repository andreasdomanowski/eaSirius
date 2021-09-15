package net.domanowski.easirius.m2t;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.epsilon.egl.launch.EgxRunConfiguration;
import org.eclipse.epsilon.emc.emf.EmfModel;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.crossecore.CrossEcore;

public class EaSiriusApplication {
	private static final String RELATIVE_PATH_TO_ODESIGN_TRANSFORMATIONS = "epsilon/odesignTransformations.egx";
	private static final String RELATIVE_PATH_TO_ECORE_TRANSFORMATIONS = "epsilon/ecoreTransformations.egx";
	// TODO - replace with SL4J
	private static final Bundle BUNDLE = FrameworkUtil.getBundle(EaSiriusApplication.class);
	private static final ILog LOGGER = Platform.getLog(BUNDLE);

	public static void log(String msg) {
		log(msg, null);
	}

	public static void log(String msg, Exception e) {
		LOGGER.log(new Status((e == null ? IStatus.INFO : IStatus.ERROR), BUNDLE.getSymbolicName(), msg, e));
	}

	private EaSiriusApplication() {
		// hide non arg-constructor
	}

	public static void generateEditor(String pathToEditorDescription, String pathToMetaModel,
			String pathToOutputFolder) {
		Objects.requireNonNull(pathToEditorDescription);
		Objects.requireNonNull(pathToMetaModel);
		Objects.requireNonNull(pathToEditorDescription);

		// sanitize output path
		final String sanitizedOutputFolderString = pathToOutputFolder.endsWith(File.separator) ? pathToOutputFolder
				: pathToOutputFolder + File.separator;

		log("Generating Editor in the following path: " + sanitizedOutputFolderString);

		File editorDescriptionFile = new File(pathToEditorDescription);
		File metaModelFile = new File(pathToMetaModel);
		File targetFolderFile = new File(pathToOutputFolder);

		Runnable generateEditor = () -> {
			try {
				executeOdesignTransformations(editorDescriptionFile, targetFolderFile);
				log("Generated Editor Application and translated Viewpoint Specification Model");
			} catch (IOException | URISyntaxException e) {
				log("Error while generating editor and translating viewpoint specification model");
				e.printStackTrace();
			}

			try {
				executeEcoreTransformations(metaModelFile, targetFolderFile);
				log("Generated properties editor");
			} catch (URISyntaxException | IOException e) {
				log("Error while generating properties editor");
				e.printStackTrace();
			}

			String[] crossEcoreArguments = { "-L", "typescript", "-e", pathToMetaModel, "-p",
					sanitizedOutputFolderString };
			CrossEcore.main(crossEcoreArguments);
			log("Generated Metamodel implementation");

		};

		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), generateEditor);
	}

	private static void executeEcoreTransformations(File metaModelFile, File targetFolderFile)
			throws URISyntaxException, IOException {
		URL fileURL = BUNDLE.getEntry(RELATIVE_PATH_TO_ECORE_TRANSFORMATIONS);

		URL newUrl = FileLocator.toFileURL(fileURL);
		Path pathToEgx = Path.of(newUrl.toURI());

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(EmfModel.PROPERTY_NAME, "Model");
		modelProperties.setProperty(EmfModel.PROPERTY_METAMODEL_URI, "http://www.eclipse.org/emf/2002/Ecore");
		modelProperties.setProperty(EmfModel.PROPERTY_MODEL_URI,
				metaModelFile.toPath().toAbsolutePath().toUri().toString());
		modelProperties.setProperty(EmfModel.PROPERTY_CACHED, "true");
		modelProperties.setProperty(EmfModel.PROPERTY_CONCURRENT, "true");

		EgxRunConfiguration runConfig = EgxRunConfiguration.Builder().withScript(pathToEgx)
				.withModel(new EmfModel(), modelProperties)
				.withParameter("pathPrefix", targetFolderFile.toPath().toAbsolutePath().toString() + File.separator)
				.withProfiling().build();

		runConfig.run();
	}

	private static void executeOdesignTransformations(File editorDescriptionFile, File targetFolderFile)
			throws IOException, URISyntaxException {
		URL fileURL = BUNDLE.getEntry(RELATIVE_PATH_TO_ODESIGN_TRANSFORMATIONS);

		URL newUrl = FileLocator.toFileURL(fileURL);
		Path pathToEgx = Path.of(newUrl.toURI());

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(EmfModel.PROPERTY_NAME, "Model");

		modelProperties.setProperty(EmfModel.PROPERTY_CACHED, "true");

		modelProperties.setProperty(EmfModel.PROPERTY_REUSE_UNMODIFIED_FILE_BASED_METAMODELS, "true");

		modelProperties.setProperty(EmfModel.PROPERTY_METAMODEL_URI,
				"http://www.eclipse.org/sirius/diagram/1.1.0,http://www.eclipse.org/sirius/1.1.0,http://www.eclipse.org/emf/2002/Ecore");
		modelProperties.setProperty(EmfModel.PROPERTY_MODEL_URI,
				editorDescriptionFile.toPath().toAbsolutePath().toUri().toString());
		modelProperties.setProperty(EmfModel.PROPERTY_READONLOAD, "true");
		modelProperties.setProperty(EmfModel.PROPERTY_STOREONDISPOSAL, "true");

		EgxRunConfiguration runConfig = EgxRunConfiguration.Builder().withScript(pathToEgx)
				.withModel(new EmfModel(), modelProperties)
				.withParameter("pathPrefix", targetFolderFile.toPath().toAbsolutePath().toString() + File.separator)
				.withProfiling().build();

		EPackage.Registry.INSTANCE.entrySet().forEach(c -> {
			if (c.getKey().equals("http://www.eclipse.org/sirius/1.1.0")) {
			}
		});
		runConfig.run();
	}

}