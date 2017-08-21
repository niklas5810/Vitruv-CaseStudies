package tools.vitruv.applications.pcmjava.pojotransformations.java2pcm

import org.apache.log4j.Logger
import org.emftext.language.java.members.Method
import org.emftext.language.java.types.TypeReference
import org.palladiosimulator.pcm.repository.Repository
import tools.vitruv.applications.pcmjava.util.java2pcm.TypeReferenceCorrespondenceHelper
import tools.vitruv.framework.correspondence.CorrespondenceModel
import tools.vitruv.framework.userinteraction.UserInteracting

import static extension tools.vitruv.framework.correspondence.CorrespondenceModelUtil.*
import org.eclipse.emf.common.util.EList
import java.util.ArrayList
import org.emftext.language.java.classifiers.Interface
import org.emftext.language.java.containers.Package
import org.eclipse.emf.ecore.EObject
import java.util.Set
import org.palladiosimulator.pcm.repository.OperationInterface
import org.emftext.language.java.classifiers.Classifier
import org.eclipse.emf.ecore.util.EcoreUtil
import org.emftext.language.java.types.NamespaceClassifierReference
import org.emftext.language.java.types.ClassifierReference
import org.emftext.language.java.types.PrimitiveType
import org.palladiosimulator.pcm.repository.PrimitiveDataType
import org.palladiosimulator.pcm.repository.CollectionDataType
import org.palladiosimulator.pcm.repository.CompositeDataType

public class Java2PcmHelper {
	private static val logger = Logger.getLogger(Java2PcmHelper)

	//Copied from Java2PCMUtils (getRepository)
	def static Repository findPcmRepository(CorrespondenceModel correspondenceModel) {
		val Set<Repository> repos = correspondenceModel.getAllEObjectsOfTypeInCorrespondences(Repository)
		if (repos.nullOrEmpty) {
			return null
		}
		if (1 != repos.size) {
			logger.warn("found more than one repository. Retruning the first")
		}
		return repos.get(0)
	}

	def static boolean hasCorrespondance(EObject eObject, CorrespondenceModel correspondenceModel) {
		return !correspondenceModel.getCorrespondingEObjects(eObject).isNullOrEmpty
	}
	
	def static Set<EObject> foos(EObject object, CorrespondenceModel correspondenceModel) {
		return correspondenceModel.getCorrespondingEObjects(object)
	}
	
	def static Set<OperationInterface> getCorrespondingOperationInterface(EObject eObject, CorrespondenceModel correspondenceModel) {
		return correspondenceModel.getCorrespondingEObjectsByType(eObject, OperationInterface)
	}
	
	/**
	 * Signatures are considered equal if methods have the same name, the same parameter types and the same return type
	 * We do not consider modifiers (e.g. public or private here)
	 */
	 //Copied from pcmJavaUtils
	def static sameSignature(Method method1, Method method2) {
		if (method1 == method2) {
			return true
		}
		if (!method1.name.equals(method2.name)) {
			return false
		}
		if (!method1.typeReference.hasSameTargetReference(method2.typeReference)) {
			return false
		}
		if (method1.parameters.size != method2.parameters.size) {
			return false
		}
		var int i = 0
		for (param1 : method1.parameters) {
			if (!hasSameTargetReference(param1.typeReference, method2.parameters.get(i).typeReference)) {
				return false
			}
			i++
		}
		return true
	
	}
	//Copied from pcmJavaUtils
	def private static boolean hasSameTargetReference(TypeReference reference1, TypeReference reference2) {
		if (reference1 == reference2 || reference1.equals(reference2)) {
			return true
		}
		val target1 = getTargetClassifierFromTypeReference(reference1)
		val target2 = getTargetClassifierFromTypeReference(reference2)
		return target1 == target2 || target1.equals(target2)
	}
	
	/**
	 * Check if no Repository exists in correspondence model.
	 * @param correspondenceModel the correspondenceModel in which the PCM-Repository should be searched
	 * @return true if no exists, false otherwise.
	 */
	def static boolean noCorrespondenceRepository(CorrespondenceModel correspondenceModel) {
		return correspondenceModel.getAllEObjectsOfTypeInCorrespondences(Repository).isNullOrEmpty
	}
	
	def static getPCMDataTypeForTypeReference(TypeReference typeReference, CorrespondenceModel correspondenceModel, UserInteracting userInteracting, Repository repository, Method newMethod) {
		return TypeReferenceCorrespondenceHelper.
					getCorrespondingPCMDataTypeForTypeReference(typeReference,
						correspondenceModel, userInteracting, repository, newMethod.arrayDimension)
	}
	
	def static String getRootPackageName(String packageName) {
		val index = packageName.indexOf('.')
		if (index < 0) {
			return packageName
		}
		return packageName.substring(0, packageName.indexOf('.'))
	}
	
	def static String getLastPackageName(String packageName) {
		return packageName.substring(packageName.indexOf('.') + 1)
	}
	
	def static findImplementingInterfacesFromTypeRefs(EList<TypeReference> typeReferences) {
		val implementingInterfaces = new ArrayList<Interface>
		for(typeRef : typeReferences){
			val classifier = getTargetClassifierFromImplementsReferenceAndNormalizeURI(typeRef)
			if(classifier instanceof Interface){
				implementingInterfaces.add(classifier)
			}
		}
		return implementingInterfaces
	}
	
	//Copied from Java2PCMUtils
	def static Classifier getTargetClassifierFromImplementsReferenceAndNormalizeURI(TypeReference reference) {
		var interfaceClassifier = getTargetClassifierFromTypeReference(reference)
		if (null === interfaceClassifier) {
			return null
		}

		if (interfaceClassifier.eIsProxy) {
			val resSet = reference.eResource.resourceSet
			interfaceClassifier = EcoreUtil.resolve(interfaceClassifier, resSet) as Classifier
		}
		normalizeURI(interfaceClassifier)
		return interfaceClassifier
	}
	
	def dispatch static Classifier getTargetClassifierFromTypeReference(TypeReference reference) {
		return null
	}

	def dispatch static Classifier getTargetClassifierFromTypeReference(NamespaceClassifierReference reference) {
		if (reference.classifierReferences.nullOrEmpty) {
			return null
		}
		return getTargetClassifierFromTypeReference(reference.classifierReferences.get(0))
	}

	def dispatch static Classifier getTargetClassifierFromTypeReference(ClassifierReference reference) {
		return reference.target
	}

	def dispatch static Classifier getTargetClassifierFromTypeReference(PrimitiveType reference) {
		return null
	}

	//Copied from Java2PCMUtils
	def private static normalizeURI(EObject eObject) {
		if (null === eObject.eResource || null === eObject.eResource.resourceSet) {
			return false
		}
		val resource = eObject.eResource
		val resourceSet = resource.resourceSet
		val uri = resource.getURI
		val uriConverter = resourceSet.getURIConverter
		val normalizedURI = uriConverter.normalize(uri)
		resource.URI = normalizedURI
		return true
	}
	
	//Copied from PCM2JavaUtils
	def static Package getContainingPackageFromCorrespondenceModel(Classifier classifier,
		CorrespondenceModel correspondenceModel) {
		var namespace = classifier.containingCompilationUnit.namespacesAsString
		if (namespace.endsWith("$") || namespace.endsWith(".")) {
			namespace = namespace.substring(0, namespace.length - 1)
		}
		val finalNamespace = namespace
		var Set<Package> packagesWithCorrespondences = correspondenceModel.
			getAllEObjectsOfTypeInCorrespondences(Package)
		val packagesWithNamespace = packagesWithCorrespondences.filter [ pack |
			finalNamespace.equals(pack.namespacesAsString + pack.name)
		]
		if (null !== packagesWithNamespace && 0 < packagesWithNamespace.size &&
			null !== packagesWithNamespace.iterator.next) {
			return packagesWithNamespace.iterator.next
		}
		return null
	}
	
	//PCMJavaUtils
	public dispatch static def getNameFromPCMDataType(PrimitiveDataType primitiveDataType) {
		return primitiveDataType.type.getName
	}

	public dispatch static def getNameFromPCMDataType(CollectionDataType collectionDataType) {
		return collectionDataType.entityName
	}

	public dispatch static def getNameFromPCMDataType(CompositeDataType compositeDataType) {
		return compositeDataType.entityName
	}
}
