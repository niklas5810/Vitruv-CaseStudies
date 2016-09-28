package mir.routines.pcm2java;

import java.io.IOException;
import mir.routines.pcm2java.RoutinesFacade;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.classifiers.Interface;
import org.emftext.language.java.members.InterfaceMethod;
import org.emftext.language.java.members.Member;
import org.emftext.language.java.members.impl.MembersFactoryImpl;
import org.palladiosimulator.pcm.repository.DataType;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.OperationSignature;
import tools.vitruv.extensions.dslsruntime.response.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.response.ResponseExecutionState;
import tools.vitruv.extensions.dslsruntime.response.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class CreateMethodForOperationSignatureRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade effectFacade;
  
  private CreateMethodForOperationSignatureRoutine.EffectUserExecution userExecution;
  
  private static class EffectUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public EffectUserExecution(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy) {
      super(responseExecutionState);
    }
    
    public EObject getElement1(final OperationSignature operationSignature, final Interface javaInterface, final InterfaceMethod interfaceMethod) {
      return interfaceMethod;
    }
    
    public EObject getCorrepondenceSourceJavaInterface(final OperationSignature operationSignature) {
      OperationInterface _interface__OperationSignature = operationSignature.getInterface__OperationSignature();
      return _interface__OperationSignature;
    }
    
    public EObject getElement2(final OperationSignature operationSignature, final Interface javaInterface, final InterfaceMethod interfaceMethod) {
      return operationSignature;
    }
    
    public void callRoutine1(final OperationSignature operationSignature, final Interface javaInterface, final InterfaceMethod interfaceMethod, @Extension final RoutinesFacade _routinesFacade) {
      String _entityName = operationSignature.getEntityName();
      interfaceMethod.setName(_entityName);
      DataType _returnType__OperationSignature = operationSignature.getReturnType__OperationSignature();
      _routinesFacade.changeInterfaceMethodReturnType(interfaceMethod, _returnType__OperationSignature);
      EList<Member> _members = javaInterface.getMembers();
      _members.add(interfaceMethod);
    }
  }
  
  public CreateMethodForOperationSignatureRoutine(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy, final OperationSignature operationSignature) {
    super(responseExecutionState, calledBy);
    this.userExecution = new mir.routines.pcm2java.CreateMethodForOperationSignatureRoutine.EffectUserExecution(getExecutionState(), this);
    this.effectFacade = new mir.routines.pcm2java.RoutinesFacade(getExecutionState(), this);
    this.operationSignature = operationSignature;
  }
  
  private OperationSignature operationSignature;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine CreateMethodForOperationSignatureRoutine with input:");
    getLogger().debug("   OperationSignature: " + this.operationSignature);
    
    Interface javaInterface = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceJavaInterface(operationSignature), // correspondence source supplier
    	Interface.class,
    	(Interface _element) -> true, // correspondence precondition checker
    	null);
    if (javaInterface == null) {
    	return;
    }
    initializeRetrieveElementState(javaInterface);
    InterfaceMethod interfaceMethod = MembersFactoryImpl.eINSTANCE.createInterfaceMethod();
    initializeCreateElementState(interfaceMethod);
    
    addCorrespondenceBetween(userExecution.getElement1(operationSignature, javaInterface, interfaceMethod), userExecution.getElement2(operationSignature, javaInterface, interfaceMethod), "");
    
    userExecution.callRoutine1(operationSignature, javaInterface, interfaceMethod, effectFacade);
    
    postprocessElementStates();
  }
}
