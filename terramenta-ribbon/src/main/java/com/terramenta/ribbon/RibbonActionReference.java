/*
 * Copyright (c) 2012, Bruce Schubert. <bruce@emxsys.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the Emxsys company nor the names of its 
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.terramenta.ribbon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.openide.awt.ActionID;


/**
 * This class defines the elements used to register entries in the Flamingo Ribbon Bar.
 * 
 * Ribbon/AppMenu: Application button registration, AKA File Tab
 * 
 * Ribbon/TaskBar: Mini-bar registration</br>
 * 
 * Ribbon/HelpButton: Help button registration</br>
 * 
 * Ribbon/TaskPanes: Tab registrations</br>
 * {@code 
    <folder name="Ribbon">
        <folder name="TaskPanes">
            <!-- Tab (aka Flamingo TaskPane) -->
            <folder name="Home">
                <!-- Group (aka Flamingo RibbonBand)-->
                <folder name="Project">
                    <attr name="position" intvalue="100"/>
                    <attr name="displayName" stringvalue="Project"/>
                    <!-- CommandButton Popup --> 
                    <folder name="Projects">                   
                        <!-- Popup menu items --> 
                        <file name="org-openide-actions-SaveAllAction.shadow"></file>
                        <file name="SeparatorAfterSaveAll.instance"> </file>
                        <file name="org-netbeans-modules-project-ui-logical-tab-action.shadow"></file>
                    </folder>
                    
                    <!-- Screenshot Compound Action/Popup --> 
                    <folder name="Screenshot">
                        <file name="org-openide-actions-SaveAllAction.shadow">
                            <attr name="defaultAction" boolvalue="true"/>
                        </file>
                        <file name="SeparatorAfterSaveAll.instance"> </file>
                        <file name="org-netbeans-modules-project-ui-logical-tab-action.shadow"></file>
                    </folder>

                    <!-- Basemap Popup --> 
                    <folder name="Basemap">
                        <attr name="position" intvalue="100"/>
                        <attr name="iconBase" stringvalue="com/emxsys/worldwind/resources/map_accept.png"/>
                        <attr name="menuText" bundlevalue="com.emxsys.worldwind.Bundle#CTL_Basemap"/>
                        <attr name="description" bundlevalue="com.emxsys.worldwind.Bundle#CTL_Basemap_Hint"/>
                        <attr name="priority" stringvalue="top"/>
                        <attr name="tooltipTitle" bundlevalue="com.emxsys.worldwind.Bundle#CTL_Basemap_TooltipTitle"/>
                        <attr name="tooltipBody" bundlevalue="com.emxsys.worldwind.Bundle#CTL_Basemap_TooltipBody"/>
                        <attr name="tooltipIcon" stringvalue="com/emxsys/basicui/resources/accept.png"/>
                        <attr name="tooltipFooter" bundlevalue="com.emxsys.basicui.Bundle#CTL_Default_TooltipFooter"/>                        
                        <attr name="tooltipFooterIcon" stringvalue="com/emxsys/basicui/resources/help.png"/>                        
                        <!-- Popup Gallery  --> 
                        <folder name="Satellite">
                            <attr name="position" intvalue="100"/>
                        </folder>
                        <folder name="Aerial">
                            <attr name="position" intvalue="200"/>
                        </folder>
                        <folder name="Topographic">
                            <attr name="position" intvalue="300"/>
                        </folder>
                        <folder name="Street">
                            <attr name="position" intvalue="400"/>
                        </folder>
                        <folder name="User">
                            <attr name="position" intvalue="500"/>
                        </folder>
}                         
 *
 * See the com.pinkmatter.modules.flamingo.ActionItem class for the implementation details.
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(
{
    ElementType.TYPE, ElementType.FIELD, ElementType.METHOD
})
public @interface RibbonActionReference
{

    /**
     * Into which location one wants to place the reference? Translates to
     * {@link FileUtil#getConfigFile(java.lang.String)}.
     */
    String path();


    /**
     * Position in the location.
     */
    int position() default Integer.MAX_VALUE;


    /**
     * Identification of the action this reference shall point to. Usually this is specified as
     * {@link ActionID} peer annotation, but in case one was to create references to actions defined
     * by someone else, one can specify the id() here.
     */
    ActionID id() default @ActionID(id = "", category = "");


    /**
     * One can specify name of the reference. This is not necessary, then it is deduced from
     * associated {@link ActionID}.
     */
    String name() default "";


    /**
     * Shall a separator be placed before the action?
     *
     * @return position that is lower than {@link #position()}
     */
    int separatorBefore() default Integer.MAX_VALUE;


    /**
     * Shall a separator be placed after the action?
     *
     * @return position that is higher than {@link #position()}
     */
    int separatorAfter() default Integer.MAX_VALUE;


    /**
     * One can specify the menu text for the command button. This will be override the display name.
     * If not defined, the action's display name is used.
     */
    String menuText() default "";

    /**
     * One can specify a description for the command button. This will be used as the hint text if a
     * tooltip body is not defined.
     */
    String description() default "";


    /**
     * One can specify a tooltip image for the command button. This will be used with the hint text.
     * If not defined, the iconBase will be used.
     */
    String tooltipIcon() default "";


    /**
     * One can specify a tooltip title for the command button. This will be used with the hint text.
     * If not defined, the menuText or displayName will be used, in that order.
     */
    String tooltipTitle() default "";


    /**
     * One can specify a multi-line tooltip body for the command button. This will be for the hint
     * text. Individual lines should be delimited by a new-line character (\n). If not defined, the
     * description is used.
     */
    String tooltipBody() default "";


    /**
     * One can specify an optional tooltip footer for the command button. This will be used with the
     * hint text. If not defined, there will not be a footer.
     */
    String tooltipFooter() default "";


    /**
     * One can specify an optional icon for the tooltip footer. This will be used with the hint
     * text.
     */
    String tooltipFooterIcon() default "";


    /**
     * One can specify the command button's resizing priority. This will be used as the to set the
     * relative priority of the button as compared to others when the ribbon bar is resizing.
     */
    String priority() default "top";


    /**
     * One can specify the command button's style, e.g., toggle.
     */
    String buttonStyle() default "";


    /**
     * One can specify if the button has an auto-repeat action
     */
    boolean autoRepeatAction() default false;


    /**
     * One can specify if the button is the default within a pop-up list
     */
    boolean defaultAction() default false;
}
