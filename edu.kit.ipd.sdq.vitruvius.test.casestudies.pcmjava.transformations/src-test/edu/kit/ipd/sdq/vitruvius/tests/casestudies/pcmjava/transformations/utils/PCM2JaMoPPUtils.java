package edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.utils;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.PCMJaMoPPNamespace;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.EMFChangeResult;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.EMFModelChange;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.ModelInstance;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.TransformationChangeResult;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.feature.reference.containment.ContainmentFactory;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.feature.reference.containment.CreateNonRootEObjectInList;
import edu.kit.ipd.sdq.vitruvius.framework.util.bridges.EMFBridge;
import edu.kit.ipd.sdq.vitruvius.framework.util.bridges.EcoreResourceBridge;
import edu.kit.ipd.sdq.vitruvius.framework.util.datatypes.Pair;
import edu.kit.ipd.sdq.vitruvius.tests.util.TestUtil;

public class PCM2JaMoPPUtils {

    public static final String REPOSITORY_NAME = "testRepository";
    public static final String BASIC_COMPONENT_NAME = "TestBasicComponent";
    public static final String IMPLEMENTING_CLASS_NAME = BASIC_COMPONENT_NAME + "Impl";
    public static final String INTERFACE_NAME = "TestInterface";
    public static final String RENAME = "Rename";

    private PCM2JaMoPPUtils() {
    }

    public static Repository createRepository(final ResourceSet resourceSet, final String repositoryName)
            throws IOException {
        final VURI repoVURI = VURI.getInstance(TestUtil.PROJECT_URI + "/model/" + repositoryName + "."
                + PCMJaMoPPNamespace.PCM.REPOSITORY_FILE_EXTENSION);
        final Resource resource = resourceSet.createResource(repoVURI.getEMFUri());
        final Repository repo = RepositoryFactory.eINSTANCE.createRepository();
        repo.setEntityName(repositoryName);
        resource.getContents().add(repo);
        EcoreResourceBridge.saveResource(resource);
        return repo;
    }

    public static BasicComponent createBasicComponent(final Repository repo) {
        final BasicComponent basicComponent = RepositoryFactory.eINSTANCE.createBasicComponent();
        basicComponent.setRepository__RepositoryComponent(repo);
        basicComponent.setEntityName(BASIC_COMPONENT_NAME);
        return basicComponent;
    }

    public static VURI createDummyVURI(final String name) {
        return VURI.getInstance(EMFBridge.createPlatformResourceURI("test/" + name));
    }

    public static Resource createResource(final String resourceName, final ResourceSet resourceSet) {
        final URI uri = EMFBridge.createPlatformResourceURI(resourceName);
        // final URI uri = URI.createPlatformResourceURI("test/" + resourceName, true);
        final Resource resource = resourceSet.createResource(uri);
        return resource;
    }

    public static ModelInstance createModelInstance(final String name, final ResourceSet resourceSet) {
        final Resource resource = createResource(name, resourceSet);
        final VURI vuri = VURI.getInstance(name);
        final ModelInstance modelInstance = new ModelInstance(vuri, resource);
        return modelInstance;
    }

    public static EMFModelChange createCreateChange(final EObject changedEObject, final EObject newAffectedEObject,
            final EObject oldAffectedEObject, final String featureName) {
        final CreateNonRootEObjectInList<EObject> createChange = ContainmentFactory.eINSTANCE
                .createCreateNonRootEObjectInList();
        createChange.setNewValue(changedEObject);
        createChange.setNewAffectedEObject(newAffectedEObject);
        createChange.setOldAffectedEObject(oldAffectedEObject);
        createChange.setAffectedFeature(getEReferenceByName(newAffectedEObject, featureName));
        createChange.setNewValue(changedEObject);
        final EMFModelChange emfModelChange = new EMFModelChange(createChange, VURI.getInstance(oldAffectedEObject
                .eResource()));
        return emfModelChange;
    }

    public static EReference getEReferenceByName(final EObject eObject, final String featureName) {
        return (EReference) eObject.eClass().getEStructuralFeature(featureName);
    }

    public static void saveEMFChangeResult(final EMFChangeResult emfChangeResult, final ResourceSet resourceSet)
            throws IOException {
        for (final VURI vuri : emfChangeResult.getExistingObjectsToSave()) {
            saveVURI(vuri, resourceSet);
        }
        for (final Pair<EObject, VURI> eObjectVURIPair : emfChangeResult.getNewRootObjectsToSave()) {
            final Resource resource = resourceSet.createResource(eObjectVURIPair.getSecond().getEMFUri());
            resource.getContents().add(eObjectVURIPair.getFirst());
            EcoreResourceBridge.saveResource(resource);
        }
    }

    public static Resource saveVURI(final VURI vuri, final ResourceSet resourceSet) throws IOException {
        final Resource resource = resourceSet.createResource(vuri.getEMFUri());
        EcoreResourceBridge.saveResource(resource);
        return resource;
    }

    public static boolean isEmptyTransformationChangeResult(final TransformationChangeResult transformationChangeResult) {
        if (null == transformationChangeResult) {
            return true;
        }
        return 0 == transformationChangeResult.getExistingObjectsToDelete().size()
                && 0 == transformationChangeResult.getExistingObjectsToSave().size()
                && 0 == transformationChangeResult.getNewRootObjectsToSave().size();
    }

}
