package net.domanowski.easirius.m2t;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

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

	}

	public static void generateEditor(String pathToEditorDescriptionString, String pathToMetaModelString,
			String pathToOutputFolderString) {
		Objects.requireNonNull(pathToEditorDescriptionString);
		Objects.requireNonNull(pathToMetaModelString);
		Objects.requireNonNull(pathToEditorDescriptionString);

		// sanitize output path
		final String sanitizedOutputFolderString = pathToOutputFolderString.endsWith(File.separator)
				? pathToOutputFolderString
				: pathToOutputFolderString + File.separator;

		log("Generating Editor in the following path: " + sanitizedOutputFolderString);

		File editorDescriptionFile = new File(pathToEditorDescriptionString);
		File metaModelFile = new File(pathToMetaModelString);
		File targetFolderFile = new File(pathToOutputFolderString);

		Runnable generateEditor = () -> {
			executeEcoreTransformations(metaModelFile, targetFolderFile);
			log("Generated Properties Editor");

			executeOdesignTransformations(editorDescriptionFile, targetFolderFile);
			log("Generated Editor Application and translated Viewpoint Specification Model");

			String[] crossEcoreArguments = { "-L", "typescript", "-e", pathToMetaModelString, "-p",
					sanitizedOutputFolderString };
			CrossEcore.main(crossEcoreArguments);
		};

		BusyIndicator.showWhile(PlatformUI.getWorkbench().getDisplay(), generateEditor);
	}

	private static void executeEcoreTransformations(File metaModelFile, File targetFolderFile) {		
		Path pathToEgx = Path
				.of("C:\\Dev\\diplomarbeitWorkspace\\net.domanowski.easirius\\epsilon\\ecoreTransformations.egx");

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

	private static void executeOdesignTransformations(File editorDescriptionFile, File targetFolderFile) {
		Path pathToEgx = Path
				.of("C:\\Dev\\diplomarbeitWorkspace\\net.domanowski.easirius\\epsilon\\odesignTransformations.egx");

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

	protected static String registerMetamodel(String sourceMetamodel) throws URISyntaxException {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());

		// Create a new Resource set for the xml-based metamodel resources
		ResourceSet rs = new ResourceSetImpl();
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);

		// Create a metamodel resource from the metamodel file path by URI
		// Register the metamodel with the NsURI (EMF interne URI) in the EMF Resource
		// registry
		Resource r = rs.getResource(org.eclipse.emf.common.util.URI.createFileURI(sourceMetamodel), true);

		r.getAllContents().forEachRemaining(obj -> {
			if (obj instanceof EPackage) {
				EPackage ePackage = (EPackage) obj;
				log("Registering " + ePackage.getNsURI());
				EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
			}
		});

		return null;
	}

}