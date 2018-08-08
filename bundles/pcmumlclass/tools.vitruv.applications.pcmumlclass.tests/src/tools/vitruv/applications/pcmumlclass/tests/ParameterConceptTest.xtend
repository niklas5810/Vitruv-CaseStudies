package tools.vitruv.applications.pcmumlclass.tests

import org.apache.log4j.Logger
import org.eclipse.uml2.uml.ParameterDirectionKind
import org.junit.Test
import org.palladiosimulator.pcm.repository.Parameter
import org.palladiosimulator.pcm.repository.ParameterModifier
import org.palladiosimulator.pcm.repository.Repository
import org.palladiosimulator.pcm.repository.RepositoryFactory
import tools.vitruv.applications.pcmumlclass.PcmUmlClassHelper
import tools.vitruv.applications.pcmumlclass.TagLiterals
import tools.vitruv.framework.correspondence.CorrespondenceModel

import static org.junit.Assert.*
import org.eclipse.uml2.uml.Type
import org.eclipse.emf.ecore.util.EcoreUtil
import org.junit.Ignore
import org.eclipse.uml2.uml.LiteralUnlimitedNatural
import org.palladiosimulator.pcm.repository.DataType

/**
 * This test class tests the reactions and routines that are supposed to synchronize a pcm::Parameter 
 * in an pcm::OperationSignature (regular Parameter) with an uml::Parameter in an uml::Operation corresponding to the signature.
 * <br><br>
 * Related files: PcmParameter.reactions, UmlRegularParameter.reactions, UmlReturnAndRegularParameterType.reactions
 */
class ParameterConceptTest extends PcmUmlClassApplicationTest {

    protected static val final Logger logger = Logger.getLogger(typeof(ParameterConceptTest).simpleName);

	private static val TEST_PARAMETER_NAME = "testParameter"
	 
	def private static boolean checkParameterModifiers(ParameterModifier pcmModifier, ParameterDirectionKind umlDirection){
		return umlDirection == PcmUmlClassHelper.getMatchingParameterDirection(pcmModifier)
	}
	
	def public static checkParameterConcept(CorrespondenceModel cm, 
			Parameter pcmParam, 
			org.eclipse.uml2.uml.Parameter umlParam
	){
		assertNotNull(pcmParam)
		assertNotNull(umlParam)
		assertTrue(corresponds(cm, pcmParam, umlParam, TagLiterals.PARAMETER__REGULAR_PARAMETER))
		assertTrue(pcmParam.parameterName == umlParam.name) 
		assertTrue(checkParameterModifiers(pcmParam.modifier__Parameter, umlParam.direction))
		assertTrue(isCorrect_DataType_Parameter_Correspondence(cm, pcmParam.dataType__Parameter, umlParam))
		assertTrue(corresponds(cm, pcmParam.operationSignature__Parameter, umlParam.operation, TagLiterals.SIGNATURE__OPERATION))
	}
	
	def protected checkParameterConcept(Parameter pcmParam ){
		val mUmlParam = helper.getModifiableCorr(pcmParam , org.eclipse.uml2.uml.Parameter, TagLiterals.PARAMETER__REGULAR_PARAMETER)
		checkParameterConcept(correspondenceModel, pcmParam , mUmlParam)
	}
	
	def protected checkParameterConcept(org.eclipse.uml2.uml.Parameter umlParam){
		val mPcmParam = helper.getModifiableCorr(umlParam, Parameter, TagLiterals.PARAMETER__REGULAR_PARAMETER)
		checkParameterConcept(correspondenceModel, mPcmParam , umlParam)
	}

	def private Repository createRepositoryWithSignature(){
		val pcmRepository = helper.createRepository()
		helper.createCompositeDataType(pcmRepository)
		val pcmCompositeType_2 = helper.createCompositeDataType_2(pcmRepository)
		helper.createCollectionDataType(pcmRepository, pcmCompositeType_2)
		val pcmInterface = helper.createOperationInterface(pcmRepository)
		helper.createOperationSignature(pcmInterface)
		
		userInteractor.addNextTextInput(PcmUmlClassApplicationTestHelper.UML_MODEL_FILE)
		createAndSynchronizeModel(PcmUmlClassApplicationTestHelper.PCM_MODEL_FILE, pcmRepository)
		assertModelExists(PcmUmlClassApplicationTestHelper.PCM_MODEL_FILE)
		assertModelExists(PcmUmlClassApplicationTestHelper.UML_MODEL_FILE)

		return reloadResourceAndReturnRoot(pcmRepository) as Repository 
	}	

	
	private def void testCreateParameterConcept_UML(Repository inPcmRepository, Type umlType, int lower, int upper) {
		var pcmRepository = inPcmRepository
		var pcmInterface = helper.getPcmOperationInterface(pcmRepository)
		var umlOperation = helper.getUmlOperation(pcmInterface)
		startRecordingChanges(umlOperation)
		
		// If there is any unnamed parameter already inserted and synchronized, 
		// the correspondence model might not be able to differentiate the new from he old parameter and return false correspondences,
		// because the name change for the new Parameter is applied later. Until then, the new Parameter has the same unnamed-TUID as the already existing one.
		var umlParameter = umlOperation.createOwnedParameter(TEST_PARAMETER_NAME, null) 
		umlParameter.direction = ParameterDirectionKind.INOUT_LITERAL
		umlParameter.type = umlType
		umlParameter.lower = lower
		umlParameter.upper = upper
		saveAndSynchronizeChanges(umlParameter)
		
		reloadResourceAndReturnRoot(umlParameter)
		pcmRepository = reloadResourceAndReturnRoot(pcmRepository) as Repository
		pcmInterface = helper.getPcmOperationInterface(pcmRepository)
		umlOperation = helper.getUmlOperation(pcmInterface)
		
		umlParameter = umlOperation.ownedParameters.findFirst[it.name == TEST_PARAMETER_NAME]
		assertNotNull(umlParameter)
		checkParameterConcept(umlParameter)
		assertTrue(EcoreUtil.equals(umlParameter.type, helper.getModifiableInstance(umlType)))
		assertTrue(umlParameter.lower == lower)
		assertTrue(umlParameter.upper == upper)
	}

	@Test @Ignore
	def void testCreateParameterConcept_UML_primitiveType() {
		var pcmRepository = createRepositoryWithSignature
		testCreateParameterConcept_UML(pcmRepository, helper.UML_INT, 1, 1)
	}
	
	@Test
	def void testCreateParameterConcept_UML_compositeType() {
		var pcmRepository = createRepositoryWithSignature
		testCreateParameterConcept_UML(pcmRepository, helper.getUmlCompositeDataTypeClass(pcmRepository), 1, 1)
	}
	
	@Test
	def void testCreateParameterConcept_UML_collectionType() {
		var pcmRepository = createRepositoryWithSignature
		testCreateParameterConcept_UML(pcmRepository, helper.getUmlCompositeDataTypeClass_2(pcmRepository), 0, LiteralUnlimitedNatural.UNLIMITED)
	}
	
	
	private def void _testCreateParameterConcept_PCM_withType(Repository inPcmRepository, DataType pcmType) {
		var pcmRepository = inPcmRepository
		var pcmInterface = helper.getPcmOperationInterface(pcmRepository)
		var pcmSignature = helper.getPcmOperationSignature(pcmInterface)
		
		var pcmParameter = RepositoryFactory.eINSTANCE.createParameter
		pcmParameter.parameterName = tools.vitruv.applications.pcmumlclass.tests.ParameterConceptTest.TEST_PARAMETER_NAME
		pcmParameter.dataType__Parameter = pcmType
		pcmSignature.parameters__OperationSignature += pcmParameter
		
		saveAndSynchronizeChanges(pcmParameter)
		pcmRepository = reloadResourceAndReturnRoot(pcmRepository) as Repository
		pcmInterface = helper.getPcmOperationInterface(pcmRepository)
		pcmSignature = helper.getPcmOperationSignature(pcmInterface)
		
		pcmParameter = pcmSignature.parameters__OperationSignature.findFirst[it.parameterName == tools.vitruv.applications.pcmumlclass.tests.ParameterConceptTest.TEST_PARAMETER_NAME]
		assertNotNull(pcmParameter)
		checkParameterConcept(pcmParameter)
		assertTrue(EcoreUtil.equals(pcmParameter.dataType__Parameter, helper.getModifiableInstance(pcmType)))
	}
	
	@Test @Ignore
	def void testCreateParameterConcept_PCM_primitiveType() {
		var pcmRepository = createRepositoryWithSignature
		_testCreateParameterConcept_PCM_withType(pcmRepository, helper.PCM_INT)
	}
	
	@Test
	def void testCreateParameterConcept_PCM_compositeType() {
		var pcmRepository = createRepositoryWithSignature
		_testCreateParameterConcept_PCM_withType(pcmRepository, helper.getPcmCompositeDataType(pcmRepository))
	}
	
	@Test
	def void testCreateParameterConcept_PCM_collectionType() {
		var pcmRepository = createRepositoryWithSignature
		_testCreateParameterConcept_PCM_withType(pcmRepository, helper.getPcmCollectionDataType(pcmRepository))
	}
	
}
