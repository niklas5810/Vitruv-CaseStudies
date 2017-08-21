package mir.reactions.reactionsPcmToJava.pcm2java;

import mir.routines.pcm2java.RoutinesFacade;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.xbase.lib.Extension;
import org.palladiosimulator.pcm.repository.CompositeDataType;
import org.palladiosimulator.pcm.repository.InnerDeclaration;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractReactionRealization;
import tools.vitruv.extensions.dslsruntime.reactions.AbstractRepairRoutineRealization;
import tools.vitruv.extensions.dslsruntime.reactions.ReactionExecutionState;
import tools.vitruv.extensions.dslsruntime.reactions.structure.CallHierarchyHaving;
import tools.vitruv.framework.change.echange.EChange;
import tools.vitruv.framework.change.echange.compound.CreateAndInsertNonRoot;
import tools.vitruv.framework.change.echange.feature.reference.InsertEReference;

@SuppressWarnings("all")
class CreatedInnerDeclarationReaction extends AbstractReactionRealization {
  public void executeReaction(final EChange change) {
    InsertEReference<CompositeDataType, InnerDeclaration> typedChange = ((CreateAndInsertNonRoot<CompositeDataType, InnerDeclaration>)change).getInsertChange();
    CompositeDataType affectedEObject = typedChange.getAffectedEObject();
    EReference affectedFeature = typedChange.getAffectedFeature();
    InnerDeclaration newValue = typedChange.getNewValue();
    mir.routines.pcm2java.RoutinesFacade routinesFacade = new mir.routines.pcm2java.RoutinesFacade(this.executionState, this);
    mir.reactions.reactionsPcmToJava.pcm2java.CreatedInnerDeclarationReaction.ActionUserExecution userExecution = new mir.reactions.reactionsPcmToJava.pcm2java.CreatedInnerDeclarationReaction.ActionUserExecution(this.executionState, this);
    userExecution.callRoutine1(affectedEObject, affectedFeature, newValue, routinesFacade);
  }
  
  public static Class<? extends EChange> getExpectedChangeType() {
    return CreateAndInsertNonRoot.class;
  }
  
  private boolean checkChangeProperties(final EChange change) {
    InsertEReference<CompositeDataType, InnerDeclaration> relevantChange = ((CreateAndInsertNonRoot<CompositeDataType, InnerDeclaration>)change).getInsertChange();
    if (!(relevantChange.getAffectedEObject() instanceof CompositeDataType)) {
    	return false;
    }
    if (!relevantChange.getAffectedFeature().getName().equals("innerDeclaration_CompositeDataType")) {
    	return false;
    }
    if (!(relevantChange.getNewValue() instanceof InnerDeclaration)) {
    	return false;
    }
    return true;
  }
  
  public boolean checkPrecondition(final EChange change) {
    if (!(change instanceof CreateAndInsertNonRoot)) {
    	return false;
    }
    getLogger().debug("Passed change type check of reaction " + this.getClass().getName());
    if (!checkChangeProperties(change)) {
    	return false;
    }
    getLogger().debug("Passed change properties check of reaction " + this.getClass().getName());
    getLogger().debug("Passed complete precondition check of reaction " + this.getClass().getName());
    return true;
  }
  
  private static class ActionUserExecution extends AbstractRepairRoutineRealization.UserExecution {
    public ActionUserExecution(final ReactionExecutionState reactionExecutionState, final CallHierarchyHaving calledBy) {
      super(reactionExecutionState);
    }
    
    public void callRoutine1(final CompositeDataType affectedEObject, final EReference affectedFeature, final InnerDeclaration newValue, @Extension final RoutinesFacade _routinesFacade) {
      _routinesFacade.createInnerDeclarationImplementation(newValue);
    }
  }
}
