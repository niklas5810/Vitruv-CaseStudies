package mir.routines.umlToJavaMethod;

import java.io.IOException;
import mir.routines.umlToJavaMethod.RoutinesFacade;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Type;
import org.emftext.language.java.containers.CompilationUnit;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.types.TypeReference;
import tools.vitruv.applications.umljava.uml2java.UmlToJavaHelper;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;

@SuppressWarnings("all")
public class SetJavaMethodReturnTypeRoutine extends AbstractRepairRoutineRealization {
  private RoutinesFacade actionsFacade;
  
  private SetJavaMethodReturnTypeRoutine.ActionUserExecution userExecution;
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public EObject getElement1(final Operation uOperation, final Method javaMethod, final org.emftext.language.java.classifiers.Class returnType) {
      return javaMethod;
    }
    
    public void update0Element(final Operation uOperation, final Method javaMethod, final org.emftext.language.java.classifiers.Class returnType) {
      Type _type = uOperation.getType();
      CompilationUnit _containingCompilationUnit = javaMethod.getContainingCompilationUnit();
      TypeReference _createTypeReferenceAndUpdateImport = UmlToJavaHelper.createTypeReferenceAndUpdateImport(_type, returnType, _containingCompilationUnit, this.userInteracting);
      javaMethod.setTypeReference(_createTypeReferenceAndUpdateImport);
    }
    
    public EObject getCorrepondenceSourceJavaMethod(final Operation uOperation) {
      return uOperation;
    }
    
    public EObject getCorrepondenceSourceReturnType(final Operation uOperation, final Method javaMethod) {
      Type _type = uOperation.getType();
      return _type;
    }
  }
  
  public SetJavaMethodReturnTypeRoutine(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy, final Operation uOperation) {
    super(reactionExecutionState, calledBy);
    this.userExecution = new mir.routines.umlToJavaMethod.SetJavaMethodReturnTypeRoutine.ActionUserExecution(getExecutionState(), this);
    this.actionsFacade = new mir.routines.umlToJavaMethod.RoutinesFacade(getExecutionState(), this);
    this.uOperation = uOperation;
  }
  
  private Operation uOperation;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine SetJavaMethodReturnTypeRoutine with input:");
    getLogger().debug("   Operation: " + this.uOperation);
    
    Method javaMethod = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceJavaMethod(uOperation), // correspondence source supplier
    	Method.class,
    	(Method _element) -> true, // correspondence precondition checker
    	null);
    if (javaMethod == null) {
    	return;
    }
    registerObjectUnderModification(javaMethod);
    org.emftext.language.java.classifiers.Class returnType = getCorrespondingElement(
    	userExecution.getCorrepondenceSourceReturnType(uOperation, javaMethod), // correspondence source supplier
    	org.emftext.language.java.classifiers.Class.class,
    	(org.emftext.language.java.classifiers.Class _element) -> true, // correspondence precondition checker
    	null);
    registerObjectUnderModification(returnType);
    // val updatedElement userExecution.getElement1(uOperation, javaMethod, returnType);
    userExecution.update0Element(uOperation, javaMethod, returnType);
    
    postprocessElements();
  }
}