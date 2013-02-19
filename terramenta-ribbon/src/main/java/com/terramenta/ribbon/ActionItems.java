/**
 * This file is part of AgroSense. Copyright (C) 2008-2012 AgroSense Foundation.
 *
 * AgroSense is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * AgroSense is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with AgroSense. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.terramenta.ribbon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Merijn Zengers
 */
class ActionItems {

    private static FileObject fileObjectRoot = FileUtil.getConfigRoot();
    private static Map<String, FileObject> fileObjectMap = new TreeMap<String, FileObject>();

    private ActionItems() {
    }

    public static List<? extends ActionItem> forPath(String path) {
        List<ActionItem> actions = new ArrayList<ActionItem>();
        Map<String, FileObject> foMap = createFileObjectMap(path);
        Map<String, ActionItem> actionMap = new TreeMap<String, ActionItem>();

        Collection<? extends Lookup.Item<Object>> items =
                Lookups.forPath(path).lookupResult(Object.class).allItems();
        for (Lookup.Item<Object> item : items) {
            connectToParent(item, path, actionMap, foMap);
            String rootItem = getRootName(getFolderName(item.getId(), path));
            ActionItem actionItem = actionMap.get(rootItem);
            if (!actions.contains(actionItem)) {
                actions.add(actionItem);
            }
        }

        return actions;
    }

    /**
     * creates a map of <String, FileObject> in which the file represents the path to the FileObject
     *
     * @param rootPath the rootPath from which the map is created
     * @return the created fileObjectMap
     */
    static Map<String, FileObject> createFileObjectMap(String rootPath) {
        FileObject folder = fileObjectRoot.getFileObject(rootPath);
        if (folder != null) {
            for (FileObject fo : folder.getChildren()) {
                addFolderToMap("", fo);
            }
        }
        return fileObjectMap;
    }

    /**
     * Adds a fileObject folder to the fileObjectMap. Will also add all the children of the folder to the map
     *
     * @param path the path of the FileObject folder
     * @param folder the FileObject who and who's children are added to the map
     */
    static Map<String, FileObject> addFolderToMap(String path, FileObject folder) {
        StringBuilder absolutePath = new StringBuilder("");
        if (path.length() > 0) {
            absolutePath.append(path);
            absolutePath.append("/");
        }
        absolutePath.append(folder.getName());
        fileObjectMap.put(absolutePath.toString(), folder);
        for (FileObject fo : folder.getChildren()) {
            fileObjectMap = addFolderToMap(absolutePath.toString(), fo);
        }
        return fileObjectMap;
    }

    private static void connectToParent(Lookup.Item<Object> item, String rootPath,
            Map<String, ActionItem> actionMap, Map<String, FileObject> foMap) {
        String name = getFolderName(item.getId(), rootPath);
        ActionItem action = getOrCreateActionItem(item, name, actionMap, foMap);
        if (action != null) {
            String parentName = getParentName(name);
            if (parentName != null) {
                ActionItem parent = getOrCreateFolderItem(parentName, actionMap, foMap);
                parent.addChild(action);
            }
        }

    }

    private static String getRootName(String name) {
        int index = name.indexOf('/');
        if (index > 0) {
            return name.substring(0, index);
        } else {
            return name;
        }

    }

    private static String getParentName(String name) {
        int index = name.lastIndexOf('/');
        if (index > 0) {
            String result = name.substring(0, index);
            return result;
        } else {
            return null;
        }
    }

    private static ActionItem getOrCreateFolderItem(String name, Map<String, ActionItem> actionMap,
            Map<String, FileObject> foMap) {
        ActionItem item = actionMap.get(name);
        if (item == null) {
            item = new ActionItem.Compound();
            actionMap.put(name, item);
            addProperties(item, foMap.get(name));
            item.setText(foMap.get(name).getName());
            String parentName = getParentName(name);
            if (parentName != null) {
                ActionItem parent = getOrCreateFolderItem(parentName, actionMap, foMap);
                parent.addChild(item);
            }
        }
        return item;
    }

    private static ActionItem getOrCreateActionItem(Lookup.Item<Object> item, String name,
            Map<String, ActionItem> actionMap,
            Map<String, FileObject> foMap) {
        ActionItem actionItem = actionMap.get(name);
        if (actionItem == null) {
            if (Action.class.isAssignableFrom(item.getType())) {
                Action instance = (Action) item.getInstance();
                if (instance != null) {
                    actionItem = ActionItem.actions((Action) instance);
                }
            } else if (JSeparator.class.isAssignableFrom(item.getType())) {
                actionItem = ActionItem.separator();
                actionItem.setText(foMap.get(name).getName());
            } else if (JComponent.class.isAssignableFrom(item.getType())) {
                JComponent instance = (JComponent) item.getInstance();
                if (instance != null) {
                    actionItem = ActionItem.component((JComponent) instance);
                }
            } else {
                System.err.println("Unknown item: " + item.getType());
            }
            if (actionItem != null) {
                addProperties(actionItem, foMap.get(name));
                actionMap.put(name, actionItem);
            }
        }
        return actionItem;
    }

    /**
     * Adds the attributes of fileObject to the actionItem
     *
     * @param action the ActionItem to add the attributes to
     * @param fo the fileObject which contains the attributes
     */
    static void addProperties(ActionItem action, FileObject fo) {
        Enumeration<String> attrs = fo.getAttributes();
        while (attrs.hasMoreElements()) {
            String attr = attrs.nextElement();
            if (!"originalFile".equals(attr) && !"position".equals(attr)) {
                Object value = fo.getAttribute(attr);
                action.putValue(attr, value);
            } else if ("originalFile".equals(attr)) { //Reference to other file in menu structure. probably action
                FileObject reference = getRoot(fo).getFileObject(fo.getAttribute(attr).toString());
                addProperties(action, reference);
            }
        }
    }

    /**
     * Gets the path of the parent of item with path param itemPath
     *
     * @param itemPath the path of the item for which the parent must be found
     * @return String parentPath or null if path is not valid
     */
    static FileObject getRoot(FileObject fo) {
        FileObject root;
        if (fo.getParent() != null) {
            root = getRoot(fo.getParent());
        } else {
            return fo;
        }
        return root;

    }

    /**
     * Removes the rootPath from the fileObjectPath. If the rootPath is Menu/Tools and fileObjectPath is
     * Menu/Tools/someAction it will return someAction
     *
     * @param fileObjectPath the path of the fileObject
     * @param rootPath the path of the rootObject
     * @return fileObjectPath - rootPath
     */
    static String getFolderName(String fileObjectPath, String rootPath) {
        String result = fileObjectPath.substring(rootPath.length(), fileObjectPath.length());
        if (result.startsWith("/")) {
            result = result.substring(1, result.length());
        }
        return result;
    }
}
