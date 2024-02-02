package tools.vitruv.applications.viewfilter.util.framework.selectors;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;

import com.google.common.base.Preconditions;

import tools.vitruv.applications.viewfilter.util.framework.impl.FilteredViewCreatingViewType;
import tools.vitruv.applications.viewfilter.util.framework.selection.ElementViewSelection;
import tools.vitruv.applications.viewfilter.viewbuild.ViewFilter;
import tools.vitruv.applications.viewfilter.viewbuild.ViewFilterImpl;
import tools.vitruv.applications.viewfilter.viewbuild.ViewFilterImpl.ViewFilterBuilder;
import tools.vitruv.framework.views.ChangeableViewSource;
import tools.vitruv.framework.views.ModifiableViewSelection;
import tools.vitruv.framework.views.View;
import tools.vitruv.framework.views.ViewSelection;
import tools.vitruv.framework.views.ViewSelector;

public class FilterSupportingViewElementSelectorImpl<Id extends Object> implements FilterSupportingViewElementSelector {

	private ModifiableViewSelection viewSelection;

	private final ChangeableViewSource viewSource;

	private final FilteredViewCreatingViewType<FilterSupportingViewElementSelectorImpl<Id>, Id> viewType;

	private ViewFilterBuilder viewFilterBuilder;

	private ViewFilterImpl viewFilter;
	
	private View createdView;

	public FilterSupportingViewElementSelectorImpl(
			FilteredViewCreatingViewType<FilterSupportingViewElementSelectorImpl<Id>, Id> viewType,
			ChangeableViewSource viewSource, Collection<EObject> selectableElements) {
		Preconditions.checkArgument((selectableElements != null), "selectable elements must not be null");
		Preconditions.checkArgument((viewType != null), "view type must not be null");
		Preconditions.checkArgument((viewSource != null), "view source must not be null");
		this.viewType = viewType;
		this.viewSource = viewSource;
		this.viewSelection = new ElementViewSelection(selectableElements);
		selectableElements.forEach(element -> viewSelection.setSelected(element, true));
		viewFilterBuilder = new ViewFilterBuilder();
	}

	@Override
	public View createView() {
		viewFilter = viewFilterBuilder.build();
		Preconditions.checkState(this.isValid(), "the current selection is invalid, thus a view cannot be created");
		//TODO nbr do I need "this"?
		createdView = this.viewType.createView(this);
		return createdView;
	}

	public void selectElementsOfRootType(Collection<Class<?>> rootTypes) {
		getSelectableElements().stream().filter(element -> rootTypes.stream().anyMatch(it -> it.isInstance(element)))
				.forEach(element -> setSelected(element, true));
	}
	
	public void deselectElementsExceptForRootType(Collection<Class<?>> rootTypes) {
		getSelectableElements().stream().filter(element -> (isViewObjectSelected(element) && !(rootTypes.stream().anyMatch(it -> it.isInstance(element)))))
				.forEach(element -> setSelected(element, false));
	}

	public void filterModelElementsByLambda(Function<EObject, Boolean> filter) {
		viewFilterBuilder.filterByLambda(filter);
	}

	public void removeOwnedAttributesFromUmlClasses() {
		viewFilterBuilder.removeOwnedUmlAttributes();
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public ViewSelection getSelection() {
		return new ElementViewSelection(this.viewSelection);
	}

	public Collection<EObject> getSelectableElements() {
		return this.viewSelection.getSelectableElements();
	}

	public boolean isSelectable(final EObject eObject) {
		return this.viewSelection.isSelectable(eObject);
	}

	public boolean isSelected(final EObject eObject) {
		return this.viewSelection.isSelected(eObject);
	}

	public boolean isViewObjectSelected(final EObject eObject) {
		return this.viewSelection.isViewObjectSelected(eObject);
	}

	public void setSelected(final EObject eObject, final boolean selected) {
		this.viewSelection.setSelected(eObject, selected);
	}

	public ChangeableViewSource getViewSource() {
		return this.viewSource;
	}

	public FilteredViewCreatingViewType<FilterSupportingViewElementSelectorImpl<Id>, Id> getViewType() {
		return this.viewType;
	}

	public ViewFilter getViewFilter() {
		return viewFilter;
	}

}
