package mir.routines.packageMappingIntegration;

import edu.kit.ipd.sdq.vitruvius.extensions.dslsruntime.response.AbstractEffectRealization;
import edu.kit.ipd.sdq.vitruvius.extensions.dslsruntime.response.ResponseExecutionState;
import edu.kit.ipd.sdq.vitruvius.extensions.dslsruntime.response.structure.CallHierarchyHaving;
import edu.kit.ipd.sdq.vitruvius.framework.change.echange.feature.reference.RemoveEReference;
import java.io.IOException;
import mir.routines.packageMappingIntegration.RoutinesFacade;
import org.eclipse.xtext.xbase.lib.Extension;
import org.emftext.language.java.containers.JavaRoot;
import org.emftext.language.java.imports.Import;

@SuppressWarnings("all")
public class RemoveImportResponseEffect extends AbstractEffectRealization {
  public RemoveImportResponseEffect(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy, final RemoveEReference<JavaRoot, Import> change) {
    super(responseExecutionState, calledBy);
    				this.change = change;
  }
  
  private RemoveEReference<JavaRoot, Import> change;
  
  protected void executeRoutine() throws IOException {
    getLogger().debug("Called routine RemoveImportResponseEffect with input:");
    getLogger().debug("   RemoveEReference: " + this.change);
    
    
    preprocessElementStates();
    new mir.routines.packageMappingIntegration.RemoveImportResponseEffect.EffectUserExecution(getExecutionState(), this).executeUserOperations(
    	change);
    postprocessElementStates();
  }
  
  private static class EffectUserExecution extends AbstractEffectRealization.UserExecution {
    @Extension
    private RoutinesFacade effectFacade;
    
    public EffectUserExecution(final ResponseExecutionState responseExecutionState, final CallHierarchyHaving calledBy) {
      super(responseExecutionState);
      this.effectFacade = new mir.routines.packageMappingIntegration.RoutinesFacade(responseExecutionState, calledBy);
    }
    
    private void executeUserOperations(final RemoveEReference<JavaRoot, Import> change) {
    }
  }
}
