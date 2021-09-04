package net.domanowski.easirius.m2t;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
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

public class EaSiriusApplication {

	private EaSiriusApplication() {

	}

	public static void generateEditor(String pathToEditorDescriptionString, String pathToMetaModelString,
			String pathToOutputFolderString) {
		File editorDescriptionFile = new File(pathToEditorDescriptionString);
		File metaModelFile = new File(pathToMetaModelString);
		File targetFolderFile = new File(pathToOutputFolderString);

		executeEcoreTransformations(metaModelFile, targetFolderFile);

		executeOdesignTransformations(editorDescriptionFile, targetFolderFile);

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