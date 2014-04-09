/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.terramenta.ribbon.options;

import com.terramenta.ribbon.spi.RibbonPreferencesProvider;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Chris.Heidt
 */
public class RibbonPreferencesProviderChildFactory extends ChildFactory.Detachable<RibbonPreferencesProvider> implements LookupListener {

    Result<RibbonPreferencesProvider> result;

    @Override
    protected void addNotify() {
        result = Lookup.getDefault().lookupResult(RibbonPreferencesProvider.class);
        result.addLookupListener(this);
    }

    @Override
    protected void removeNotify() {
        result.removeLookupListener(this);
        result = null;
    }

    @Override
    protected boolean createKeys(List<RibbonPreferencesProvider> keys) {
        keys.addAll(result.allInstances());
        return true;
    }

    @Override
    protected Node createNodeForKey(RibbonPreferencesProvider key) {
        AbstractNode node = new AbstractNode(Children.LEAF, Lookups.singleton(key));
        node.setDisplayName(key.getName());
        node.setShortDescription(key.getDescription());
        return node;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        refresh(true);
    }

}
