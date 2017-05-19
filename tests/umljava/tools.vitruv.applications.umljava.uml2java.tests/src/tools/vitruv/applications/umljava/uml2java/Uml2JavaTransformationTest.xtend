package tools.vitruv.applications.umljava.uml2java

import org.apache.log4j.Logger
import org.apache.log4j.PropertyConfigurator
import org.eclipse.uml2.uml.Model
import org.eclipse.uml2.uml.Property
import org.eclipse.uml2.uml.UMLFactory
import org.emftext.language.java.classifiers.ConcreteClassifier

import static tools.vitruv.domains.java.util.JavaPersistenceHelper.*
import org.eclipse.uml2.uml.Operation
import tools.vitruv.applications.umljava.testutil.AbstractUmlJavaTest

abstract class Uml2JavaTransformationTest extends AbstractUmlJavaTest {
    protected static val final Logger logger = Logger.getLogger(typeof(Uml2JavaTransformationTest).simpleName);
    
	private static val MODEL_FILE_EXTENSION = "uml";
	private static val MODEL_NAME = "model";

	private def String getProjectModelPath(String modelName) {
		"model/" + modelName + "." + MODEL_FILE_EXTENSION;
	}
	
	protected def Model getRootElement() {
		return getFirstRootElement(MODEL_NAME.projectModelPath) as Model;
	}
	
	override protected createChangePropagationSpecifications() {
		return #[new UmlToJavaChangePropagationSpecification()]; 
	}

	override protected cleanup() {
        
    }
    
    override protected setup() {
        val umlModel = UMLFactory.eINSTANCE.createModel();
        umlModel.name = MODEL_NAME;
        createAndSynchronizeModel(MODEL_NAME.projectModelPath, umlModel);
        PropertyConfigurator.configure("log4j.properties")
    }

	def protected assertJavaFileExists(String fileName, String[] namespaces) {
	    assertModelExists(buildJavaFilePath(fileName, namespaces));
	}
    def protected assertJavaFileNotExists(String fileName, String[] namespaces) {
        assertModelNotExists(buildJavaFilePath(fileName, namespaces));
    }
    
    def protected getCorrespondingAttribute(Property uAttribute) {
    	return getFirstCorrespondingObjectWithClass(uAttribute, org.emftext.language.java.members.Field)
    }
    
    def protected getCorrespondingClassMethod(Operation uOperation) {
    	return getFirstCorrespondingObjectWithClass(uOperation, org.emftext.language.java.members.ClassMethod)
    }
    
    def protected getCorrespondingInterfaceMethod(Operation uOperation) {
    	return getFirstCorrespondingObjectWithClass(uOperation, org.emftext.language.java.members.InterfaceMethod)
    }
    
    def protected getCorrespondingClass(org.eclipse.uml2.uml.Classifier uClass) {
    	return getFirstCorrespondingObjectWithClass(uClass, org.emftext.language.java.classifiers.Class)
    }
    
    def protected getCorrespondingCompilationUnit(org.eclipse.uml2.uml.Class uClass) {
        return getFirstCorrespondingObjectWithClass(uClass, org.emftext.language.java.containers.CompilationUnit)
    }
    
    def protected getCorrespondingInterface(org.eclipse.uml2.uml.Interface uInterface) {
    	return getFirstCorrespondingObjectWithClass(uInterface, org.emftext.language.java.classifiers.Interface)
    }
    
    def protected getCorrespondingEnum(org.eclipse.uml2.uml.Enumeration uEnumeration) {
    	return getFirstCorrespondingObjectWithClass(uEnumeration, org.emftext.language.java.classifiers.Enumeration)
    }
    def protected getCorrespondingParameter(org.eclipse.uml2.uml.Parameter uParam) {
    	return getFirstCorrespondingObjectWithClass(uParam, org.emftext.language.java.parameters.OrdinaryParameter)
    }
    
    def protected getCorrespondingPackage(org.eclipse.uml2.uml.Package uPackage) {
        return getFirstCorrespondingObjectWithClass(uPackage, org.emftext.language.java.containers.Package)
    }
    
    def protected getCorrespondingConstructor(org.eclipse.uml2.uml.Operation uOperation) {
        return getFirstCorrespondingObjectWithClass(uOperation, org.emftext.language.java.members.Constructor)
    }
    
    
    
}
