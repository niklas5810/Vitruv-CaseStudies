package tools.vitruv.applications.umljava.java2uml.tests

import static tools.vitruv.applications.umljava.util.java.JavaStandardType.*
import tools.vitruv.applications.umljava.java2uml.Java2UmlTransformationTest
import org.junit.Before
import org.emftext.language.java.members.Field
import org.junit.Test
import static org.junit.Assert.*
import static extension tools.vitruv.applications.umljava.util.java.JavaMemberAndParameterUtil.*
import static tools.vitruv.applications.umljava.util.java.JavaTypeUtil.*
import static extension tools.vitruv.applications.umljava.util.java.JavaModifierUtil.*
import static tools.vitruv.applications.umljava.util.uml.UmlClassifierAndPackageUtil.*
import static tools.vitruv.applications.umljava.testutil.UmlTestUtil.*
import static tools.vitruv.applications.umljava.testutil.TestUtil.*
import tools.vitruv.applications.umljava.util.java.JavaVisibility
import org.eclipse.uml2.uml.VisibilityKind
import org.eclipse.emf.ecore.util.EcoreUtil
import tools.vitruv.applications.umljava.util.java.JavaStandardType


/**
 * Test class for testing the attribute reactions.
 * 
 */
class JavaToUmlAttributeTest extends Java2UmlTransformationTest {
    private static val ATTRIBUTE_NAME = "attributName"
    private static val ATTRIBUTE_RENAME = "attributeRenamed"
    private static val STANDARD_ATTRIBUTE_NAME = "standardAttributName"
    private static val CLASS_NAME = "ClassName"
    private static val TYPE_CLASS = "TypeClass"
    
    private static var Field jAttr
    private static var org.emftext.language.java.classifiers.Class jClass
    private static var org.emftext.language.java.classifiers.Class typeClass
    
    /**
     * Initializes two java classes. One class contains an attribute.
     */
    @Before
    def void before() {
        jClass = createSimpleJavaClassWithCompilationUnit(CLASS_NAME)
        typeClass = createSimpleJavaClassWithCompilationUnit(TYPE_CLASS)
        jAttr = createJavaAttribute(ATTRIBUTE_NAME, createJavaPrimitiveType(JavaStandardType.INT), JavaVisibility.PRIVATE, false, false)
        jClass.members += jAttr
        saveAndSynchronizeChanges(jClass);
    }
    
    /**
     * Tests the creation of an attribute with primitive type.
     * Checks if a corresponding uml attribute exists afterwards.
     */
    @Test
    def testCreatePrimitiveAttribute() {
        val attr = createJavaAttribute(STANDARD_ATTRIBUTE_NAME, createJavaPrimitiveType(JavaStandardType.INT), JavaVisibility.PRIVATE, false, false)
        jClass.members += attr
        val getter = createJavaGetterForAttribute(attr, JavaVisibility.PRIVATE)
        jClass.members += getter
        saveAndSynchronizeChanges(jClass)
        
        val uAttr = getCorrespondingAttribute(attr)
        val uClass = getCorrespondingClass(jClass)
        assertUmlPropertyTraits(uAttr, STANDARD_ATTRIBUTE_NAME, VisibilityKind.PRIVATE_LITERAL, createUmlPrimitiveType(JavaStandardType.INT),
            false, false, uClass, null, null)
        assertAttributeEquals(uAttr, attr)
    }
    
    /**
     * Tests the creation of an attribute with a type that references a class.
     * Checks if a corresponding uml attribute exists afterwards.
     */
    @Test
    def testCreateAttribute() {
        val attr = createJavaAttribute(STANDARD_ATTRIBUTE_NAME, createNamespaceReferenceFromClassifier(typeClass), JavaVisibility.PRIVATE, false, false)
        jClass.members += attr
        saveAndSynchronizeChanges(jClass)
        
        val uAttr = getCorrespondingAttribute(attr)
        val uClass = getCorrespondingClass(jClass)
        val uTypeClass = getCorrespondingClass(typeClass)
        assertUmlPropertyTraits(uAttr, STANDARD_ATTRIBUTE_NAME, VisibilityKind.PRIVATE_LITERAL, uTypeClass,
            false, false, uClass, null, null)
        assertAttributeEquals(uAttr, attr)
    }
    
    /**
     * Tests if an attribute rename is correctly synchronized with the uml attribute.
     */
    @Test
    def void testRenameAttribute() {
        jAttr.name = ATTRIBUTE_RENAME
        saveAndSynchronizeChanges(jAttr)
        
        val uAttr = getCorrespondingAttribute(jAttr)
        val uClass = getCorrespondingClass(jClass)
        assertEquals(ATTRIBUTE_RENAME, uAttr.name)
        assertUmlClassDontHaveOperation(uClass, ATTRIBUTE_NAME)
        assertAttributeEquals(uAttr, jAttr)
    }
    
    /**
     * Test if deleting the java attribute also deletes the corresponding uml attribute.
     */
    @Test
    def testDeleteAttribute() {
        assertNotNull(getCorrespondingAttribute(jAttr))
        
        EcoreUtil.delete(jAttr)
        saveAndSynchronizeChanges(jClass);

        val uClass = getCorrespondingClass(jClass)
        assertUmlClassDontHaveOperation(uClass, ATTRIBUTE_NAME)
    }
    
    /**
     * Checks if a type change is correctly reflected on the uml attribute.
     */
    @Test
    def testChangeAttributeType() {
        jAttr.typeReference = createNamespaceReferenceFromClassifier(typeClass)
        saveAndSynchronizeChanges(jAttr)
        
        val uAttr = getCorrespondingAttribute(jAttr)
        val uClass = getCorrespondingClass(jClass)
        val uTypeClass = getCorrespondingClass(typeClass)
        assertUmlPropertyTraits(uAttr, ATTRIBUTE_NAME, VisibilityKind.PRIVATE_LITERAL, uTypeClass,
            false, false, uClass, null, null)
        assertAttributeEquals(uAttr, jAttr)
    }

    /**
     *Tests if a change to static is correctly reflected on the uml attribute.
     */
    @Test
    def testStaticAttribute() {
        jAttr.static = true
        saveAndSynchronizeChanges(jAttr);
         
        val uAttr = getCorrespondingAttribute(jAttr)
        assertTrue(uAttr.static)
        assertAttributeEquals(uAttr, jAttr)
    }
    /**
     *Tests if a change to final is correctly reflected on the uml attribute.
     */
    @Test
    def testFinalAttribute() {
        jAttr.final = true
        saveAndSynchronizeChanges(jAttr);
         
        val uAttr = getCorrespondingAttribute(jAttr)
        assertTrue(uAttr.readOnly)
        assertAttributeEquals(uAttr, jAttr)
    }
    
    /**
     *Tests if visibility changes are correctly reflected on the uml attribute.
     */
    @Test
    def testAttributeVisibility() {
        jAttr.makePublic
        saveAndSynchronizeChanges(jAttr);
         
        var uAttr = getCorrespondingAttribute(jAttr)
        assertUmlNamedElementHasVisibility(uAttr, VisibilityKind.PUBLIC_LITERAL)
        assertAttributeEquals(uAttr, jAttr)
        
        jAttr.makeProtected
        saveAndSynchronizeChanges(jAttr);
         
        uAttr = getCorrespondingAttribute(jAttr)
        assertUmlNamedElementHasVisibility(uAttr, VisibilityKind.PROTECTED_LITERAL)
        assertAttributeEquals(uAttr, jAttr)
    }
}