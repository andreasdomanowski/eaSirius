package net.domanowsi.easirius.m2t;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
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
import org.osgi.framework.Bundle;

import net.domanowski.easirius.Activator;

public class EaSiriusApplication {

	private EaSiriusApplication() {

	}

	private static final String CLI_OPTION_PATH_EDITOR_DESCRIPTION = "d";
	private static final String CLI_OPTION_PATH_META_MODEL = "mm";
	private static final String CLI_OPTION_PATH_TARGET_FOLDER = "t";

	private static final String ERROR_MESSAGE_ILLEGAL_EDITOR_DESCRIPTION_FILE = "The specified path to the editor description does not exist or is not a file";
	private static final String ERROR_MESSAGE_ILLEGAL_META_MODEL_FILE = "The specified path to the editor description does not exist or is not a file";
	private static final String ERROR_MESSAGE_ILLEGAL_OUTPUT_FOLDER = "The specified path to the output folder does not exist or is no folder";

	public static void generateEditor(String pathToEditorDescriptionString, String pathToMetaModelString,
			String pathToOutputFolderString) {
		File editorDescriptionFile = new File(pathToEditorDescriptionString);
		File metaModelFile = new File(pathToMetaModelString);
		File targetFolderFile = new File(pathToOutputFolderString);

		if (!editorDescriptionFile.exists() || !editorDescriptionFile.isFile()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_ILLEGAL_EDITOR_DESCRIPTION_FILE);
		}

		if (!metaModelFile.exists() || !metaModelFile.isFile()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_ILLEGAL_META_MODEL_FILE);
		}

		if (!targetFolderFile.exists() || !targetFolderFile.isDirectory()) {
			throw new IllegalArgumentException(ERROR_MESSAGE_ILLEGAL_OUTPUT_FOLDER);
		}

		executeEcoreTransformations(metaModelFile, targetFolderFile);

		executeOdesignTransformations(editorDescriptionFile, targetFolderFile);

	}

	private static void executeEcoreTransformations(File metaModelFile, File targetFolderFile) {

		Path pathToEgx = Path
				.of("C:\\Dev\\diplomarbeitWorkspace\\net.domanowski.easirius\\epsilon\\ecoreTransformations.egx");

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(EmfModel.PROPERTY_NAME, "Model");
		;
		modelProperties.setProperty(EmfModel.PROPERTY_METAMODEL_URI, "http://www.eclipse.org/emf/2002/Ecore");
		modelProperties.setProperty(EmfModel.PROPERTY_MODEL_URI,
				metaModelFile.toPath().toAbsolutePath().toUri().toString());
		modelProperties.setProperty(EmfModel.PROPERTY_CACHED, "true");
		modelProperties.setProperty(EmfModel.PROPERTY_CONCURRENT, "true");

		EgxRunConfiguration runConfig = EgxRunConfiguration.Builder().withScript(pathToEgx)
				.withModel(new EmfModel(), modelProperties)
				.withParameter("pathPrefix", targetFolderFile.toPath().toAbsolutePath().toString()).withProfiling()
				.build();

		runConfig.run();
	}

	private static void executeOdesignTransformations(File editorDescriptionFile, File targetFolderFile) {
		Path pathToEgx = Path
				.of("C:\\Dev\\diplomarbeitWorkspace\\net.domanowski.easirius\\epsilon\\odesignTransformations.egx");

		StringProperties modelProperties = new StringProperties();
		modelProperties.setProperty(EmfModel.PROPERTY_NAME, "Model");
		modelProperties.setProperty(EmfModel.PROPERTY_METAMODEL_URI,
				"http://www.eclipse.org/sirius/1.1.0,http://www.eclipse.org/emf/2002/Ecore,http://www.eclipse.org/sirius/diagram/1.1.0");
		modelProperties.setProperty(EmfModel.PROPERTY_MODEL_URI,
				editorDescriptionFile.toPath().toAbsolutePath().toUri().toString());
		modelProperties.setProperty(EmfModel.PROPERTY_READONLOAD, "false");
		modelProperties.setProperty(EmfModel.PROPERTY_STOREONDISPOSAL, "true");

		EgxRunConfiguration runConfig = EgxRunConfiguration.Builder().withScript(pathToEgx)
				.withModel(new EmfModel(), modelProperties)
				// .withParameter("pathPrefix",
				// targetFolderFile.toPath().toAbsolutePath().toString()).withProfiling()
				.build();

		EPackage.Registry.INSTANCE.entrySet().forEach(c -> {
			if (c.getKey().equals("http://www.eclipse.org/sirius/1.1.0")) {
				System.out.println("in here!");
			}
		});
		// System.out.println(modelProperties.get(EmfModel.PROPERTY_METAMODEL_URI));
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
				System.out.println("Registering " + ePackage.getNsURI());
				EPackage.Registry.INSTANCE.put(ePackage.getNsURI(), ePackage);
			}
		});

		return null;
	}
}