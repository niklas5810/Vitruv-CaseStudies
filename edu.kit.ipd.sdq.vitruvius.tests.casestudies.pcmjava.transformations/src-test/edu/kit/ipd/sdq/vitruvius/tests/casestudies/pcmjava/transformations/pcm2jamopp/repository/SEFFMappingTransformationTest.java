package edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.pcm2jamopp.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.ClassMethod;
import org.junit.Test;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.seff.ResourceDemandingSEFF;
import edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.pcm2jamopp.PCM2JaMoPPTransformationTest;
import edu.kit.ipd.sdq.vitruvius.tests.casestudies.pcmjava.transformations.utils.PCM2JaMoPPTestUtils;

public class SEFFMappingTransformationTest extends PCM2JaMoPPTransformationTest {

    @Test
    public void testCreateSEFF() throws Throwable {
        final Repository repo = this.createAndSyncRepository(this.resourceSet, PCM2JaMoPPTestUtils.REPOSITORY_NAME);
        final BasicComponent bc1 = this.addBasicComponentAndSync(repo);
        final OperationInterface opInterface = this.addInterfaceToReposiotryAndSync(repo,
                PCM2JaMoPPTestUtils.INTERFACE_NAME);
        final OperationSignature opSignature = this.createAndSyncOperationSignature(repo, opInterface);
        this.createAndSyncOperationProvidedRole(opInterface, bc1);

        final ResourceDemandingSEFF rdSEFF = this.createAndSyncSeff(bc1, opSignature);

        this.assertSEFFCorrespondenceToMethod(rdSEFF, opSignature.getEntityName());
    }

    private void assertSEFFCorrespondenceToMethod(final ResourceDemandingSEFF rdSEFF, final String expectedName)
            throws Throwable {
        final Set<EObject> correspondingEObjects = this.getCorrespondenceInstance().getAllCorrespondingEObjects(rdSEFF);
        assertEquals("Expected exactly one corresponding EObject for rdSEFF " + rdSEFF, 1, correspondingEObjects.size());
        final EObject correspondingEObject = correspondingEObjects.iterator().next();
        assertTrue("The corresponding EObject for a SEFF has to be a ClassMethod",
                correspondingEObject instanceof ClassMethod);
        final ClassMethod correspondingClassMethod = (ClassMethod) correspondingEObject;
        assertEquals("The corresponding class method has the wrong name", expectedName,
                correspondingClassMethod.getName());
    }
}