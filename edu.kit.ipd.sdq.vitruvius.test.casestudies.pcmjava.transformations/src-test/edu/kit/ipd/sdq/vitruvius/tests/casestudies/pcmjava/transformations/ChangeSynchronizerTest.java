package edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.emftext.language.java.JavaFactory;
import org.emftext.language.java.containers.CompilationUnit;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.JaMoPPTUIDCalculatorAndResolver;
import edu.kit.ipd.sdq.vitruvius.casestudies.pcmjava.transformations.ChangeSynchronizer;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.CorrespondenceInstance;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Mapping;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.Metamodel;
import edu.kit.ipd.sdq.vitruvius.framework.contracts.datatypes.VURI;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.ChangeFactory;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.CreateNonRootEObject;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.CreateRootEObject;
import edu.kit.ipd.sdq.vitruvius.framework.meta.change.UpdateEAttribute;

public class ChangeSynchronizerTest {

    private static Logger logger = Logger.getLogger(ChangeSynchronizerTest.class.getSimpleName());

    private ChangeSynchronizer changeSynchronizer;
    private ResourceSet resourceSet;

    @Before
    public void setUp() throws Exception {
        // init synchronizer
        this.changeSynchronizer = new ChangeSynchronizer();
        final Repository repFac = RepositoryFactory.eINSTANCE.createRepository();
        final Metamodel pcm = new Metamodel(repFac.eClass().getEPackage().getNsURI(), VURI.getInstance(repFac.eClass()
                .getEPackage().getNsURI()), "repository");
        final Metamodel jamopp = new Metamodel(JavaFactory.eINSTANCE.getJavaPackage().getNsURI(),
                VURI.getInstance(JavaFactory.eINSTANCE.getJavaPackage().getNsURI()),
                new JaMoPPTUIDCalculatorAndResolver(), "java");
        final Mapping mapping = new Mapping(pcm, jamopp);
        final VURI correspondenceInstanceURI = VURI.getInstance("/tmp/correspondence.xmi");
        this.resourceSet = new ResourceSetImpl();
        final Resource resource = this.resourceSet.createResource(correspondenceInstanceURI.getEMFUri());
        final CorrespondenceInstance correspondenceInstance = new CorrespondenceInstance(mapping,
                correspondenceInstanceURI, resource);
        this.changeSynchronizer.setCorrespondenceInstance(correspondenceInstance);
    }

    /**
     * Test change synchronizing
     */
    @Test
    public void testAddRepository() {
        this.createAndSyncRepository();
        this.moveCreatedFilesToPath("addRepository");

        // change name of root EObject
        // repo.setEntityName("TestSetName");
        // final UpdateEAttribute<Object> repoNameChange =
        // ChangeFactory.eINSTANCE.createUpdateEAttribute();
        // repoNameChange.setAffectedEObject(repo);
        // final EStructuralFeature nameFeature = repo.eG
        // repoNameChange.setAffectedFeature(nameFeature);
        // repoNameChange.setNewValue(repo.getEntityName());

        // create Interface in reopsitory

        // change InterfaceName

        // remove Interface

        // remove root EObject
    }

    @Test
    public void testRepositoryNameChange() {
        final Repository repo = this.createAndSyncRepository();
        repo.setEntityName("TestNameChange");

        final UpdateEAttribute<Object> repoNameChange = ChangeFactory.eINSTANCE.createUpdateEAttribute();
        repoNameChange.setAffectedEObject(repo);
        final EAttribute affectedFeature = (EAttribute) repo.eClass().getEStructuralFeature("entityName");
        repoNameChange.setAffectedFeature(affectedFeature);
        repoNameChange.setNewValue(repo.getEntityName());
        System.out.println(repoNameChange.getNewValue());
        this.changeSynchronizer.synchronizeChange(repoNameChange);
        this.moveCreatedFilesToPath("testRepositoryNameChange");
    }

    @Test
    public void testAddInterface() {
        final Repository repo = this.createAndSyncRepository();
        this.addInterfaceToReposiotry(repo);
        this.moveCreatedFilesToPath("testAddInterface");
    }

    @Test
    public void testRenameInterface() {
        final Repository repo = this.createAndSyncRepository();
        final OperationInterface opInterface = this.addInterfaceToReposiotry(repo);
        this.renameInterfaceAndSync(opInterface);
        this.moveCreatedFilesToPath("testRenameInterface");
    }

    @Test
    public void testAddBasicComponent() {
        final Repository repo = this.createAndSyncRepository();
        this.addBasicComponent(repo);
    }

    private void renameInterfaceAndSync(final OperationInterface opInterface) {

        final String newValue = "TestRenameInterface";
        final UpdateEAttribute<Object> renameInterface = ChangeFactory.eINSTANCE.createUpdateEAttribute();
        renameInterface.setAffectedEObject(opInterface);
        renameInterface.setAffectedFeature((EAttribute) opInterface.eClass().getEStructuralFeature("entityName"));
        renameInterface.setNewValue(newValue);
        final EObject eObject = this.changeSynchronizer.synchronizeChange(renameInterface);
        opInterface.setEntityName(newValue);
        this.saveEObjectIfCompilationUnit(eObject);
    }

    private BasicComponent addBasicComponent(final Repository repo) {
        final BasicComponent basicComponent = RepositoryFactory.eINSTANCE.createBasicComponent();
        basicComponent.setRepository__RepositoryComponent(repo);
        final CreateNonRootEObject<EObject> createBasicComponentChange = ChangeFactory.eINSTANCE
                .createCreateNonRootEObject();
        createBasicComponentChange.setChangedEObject(basicComponent);
        createBasicComponentChange.setAffectedEObject(repo);
        createBasicComponentChange.setAffectedFeature((EReference) repo.eClass().getEStructuralFeature(
                "components__Repository"));
        createBasicComponentChange.setNewValue(basicComponent);
        final EObject eObject = this.changeSynchronizer.synchronizeChange(createBasicComponentChange);
        this.saveEObjectIfCompilationUnit(eObject);
        return basicComponent;
    }

    private OperationInterface addInterfaceToReposiotry(final Repository repo) {
        final OperationInterface opInterface = RepositoryFactory.eINSTANCE.createOperationInterface();
        opInterface.setRepository__Interface(repo);
        final CreateNonRootEObject<EObject> createInterfaceChange = ChangeFactory.eINSTANCE
                .createCreateNonRootEObject();
        createInterfaceChange.setChangedEObject(opInterface);
        createInterfaceChange.setAffectedEObject(repo);
        createInterfaceChange.setAffectedFeature((EReference) repo.eClass().getEStructuralFeature(
                "interfaces__Repository"));
        createInterfaceChange.setNewValue(opInterface);
        final EObject eObject = this.changeSynchronizer.synchronizeChange(createInterfaceChange);
        this.saveEObjectIfCompilationUnit(eObject);
        return opInterface;
    }

    private Repository createAndSyncRepository() {
        final VURI repoVURI = VURI.getInstance("/tmp/repository.repository");
        final Resource resource = this.resourceSet.createResource(repoVURI.getEMFUri());
        final Repository repo = RepositoryFactory.eINSTANCE.createRepository();
        resource.getContents().add(repo);
        final CreateRootEObject createRootEObj = ChangeFactory.eINSTANCE.createCreateRootEObject();
        createRootEObj.setChangedEObject(repo);
        this.changeSynchronizer.synchronizeChange(createRootEObj);
        return repo;
    }

    private void saveEObjectIfCompilationUnit(final EObject eObject) {
        if (!(eObject instanceof CompilationUnit)) {
            return;
        }
        final CompilationUnit compilationUnit = (CompilationUnit) eObject;
        String compilationUnitName = compilationUnit.getNamespacesAsString();
        compilationUnitName = compilationUnitName.replace(".", "/").replace("$", "/") + compilationUnit.getName();
        final VURI resourceURI = VURI.getInstance("MockupProject/src/" + compilationUnitName);
        final Resource resource = this.resourceSet.createResource(resourceURI.getEMFUri());
        resource.getContents().add(compilationUnit);
        try {
            resource.save(null);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves the created model and src folder files to a specific folder/path.
     * 
     * @param destinationPathAsString
     *            destinationPath in test workspace
     * @throws URISyntaxException
     */
    private void moveCreatedFilesToPath(final String destinationPathAsString) {
        // IResource iResource = Wor
        final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        final IProject project = root.getProject("MockupProject");
        final IResource member = project.findMember("src");
        if (null == member) {
            logger.warn("member not found moveCreatedFilesToPath will not work");
            return;
        }
        final IPath destinationPath = new Path(destinationPathAsString);
        try {
            member.move(destinationPath, true, new NullProgressMonitor());
        } catch (final CoreException e) {
            logger.warn("Could not move src folder do destination folder " + destinationPathAsString + ": "
                    + e.getMessage());
        }
    }
}
