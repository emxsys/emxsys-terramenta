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

import java.util.Set;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import org.openide.awt.ActionID;
import org.openide.filesystems.annotations.LayerBuilder.File;
import org.openide.filesystems.annotations.LayerGeneratingProcessor;
import org.openide.filesystems.annotations.LayerGenerationException;
import org.openide.util.Utilities;
import org.openide.util.lookup.ServiceProvider;


/**
 * This class creates XML layer entries for the Flamingo Ribbon Bar Integration module. This class is
 * modeled after the org.netbeans.modules.openide.awt.ActionProcessor by Jaroslav Tulach.
 *
 * @author Bruce Schubert <bruce@emxsys.com>
 *
 * @see ActionProcessor
 */
@ServiceProvider(service = Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes(
{
    "com.terramenta.ribbon.RibbonActionReference",
    "com.terramenta.ribbon.RibbonActionReferences"
})
public final class RibbonActionProcessor extends LayerGeneratingProcessor
{
    public static final String PRIORITY = "priority";
    public static final String PRIORITY_TOP = "top";
    public static final String PRIORITY_MEDIUM = "medium";
    public static final String PRIORITY_LOW = "low";
    public static final String MENU_TEXT = "menuText";
    public static final String DESCRIPTION = "description";
    public static final String ICON_BASE = "iconBase";
    public static final String TOOLTIP_BODY = "tooltipBody";
    public static final String TOOLTIP_TITLE = "tooltipTitle";
    public static final String TOOLTIP_ICON = "tooltipIcon";
    public static final String TOOLTIP_FOOTER = "tooltipFooter";
    public static final String TOOLTIP_FOOTER_ICON = "tooltipFooterIcon";
    public static final String DEFAULT_ACTION = "defaultAction";
    public static final String BUTTON_STYLE = "buttonStyle";
    public static final String AUTO_REPEAT_ACTION = "autoRepeatAction";

    @Override
    protected boolean handleProcess(Set<? extends TypeElement> annotations,
        RoundEnvironment env) throws LayerGenerationException
    {
        for (Element e : env.getElementsAnnotatedWith(ActionID.class))
        {
            ActionID aid = e.getAnnotation(ActionID.class);
            if (aid == null)
            {
                throw new LayerGenerationException("@RibbonActionReference(s) can only be used together with @ActionID annotation", e);
            }
            if (aid.category().startsWith("Actions/"))
            {
                throw new LayerGenerationException("@ActionID category() cannot contain /", e);
            }
            String id = aid.id().replace('.', '-');
            File f = layer(e).file("Actions/" + aid.category() + "/" + id + ".instance");

            RibbonActionReference asup = e.getAnnotation(RibbonActionReference.class);
            if (asup != null)
            {
                processReferences(e, asup, aid);
            }
            RibbonActionReferences refs = e.getAnnotation(RibbonActionReferences.class);
            if (refs != null)
            {
                for (RibbonActionReference actionSupplemental : refs.value())
                {
                    processReferences(e, actionSupplemental, aid);
                }
            }
        }

        return true;
    }


    private void processReferences(Element e, RibbonActionReference ref, ActionID aid) throws
        LayerGenerationException
    {
        if (!ref.id().category().isEmpty() && !ref.id().id().isEmpty())
        {
            if (!aid.id().equals(ref.id().id()) || !aid.category().equals(ref.id().category()))
            {
                throw new LayerGenerationException("Can't specify id() attribute when @ActionID provided on the element", e);
            }
        }
        String name = ref.name();
        if (name.isEmpty())
        {
            name = aid.id().replace('.', '-');
        }

        if (ref.path().startsWith("Shortcuts"))
        {
            KeyStroke[] stroke = Utilities.stringToKeys(name);
            if (stroke == null)
            {
                throw new LayerGenerationException(
                    "Registrations in Shortcuts folder need to represent a key. "
                    + "Specify value for 'name' attribute.\n"
                    + "See org.openide.util.Utilities.stringToKeys for possible values. Current "
                    + "name=\"" + name + "\" is not valid.\n");
            }
        }

        File f = layer(e).file(ref.path() + "/" + name + ".shadow");
        f.stringvalue("originalFile", "Actions/" + aid.category() + "/" + aid.id().replace('.', '-') + ".instance");
        f.position(ref.position());
        
        if (!ref.description().isEmpty())
        {
            f.bundlevalue(DESCRIPTION, ref.description());
        }       
        if (!ref.tooltipTitle().isEmpty())
        {
            f.bundlevalue(TOOLTIP_TITLE, ref.tooltipTitle());
        }
        if (!ref.tooltipBody().isEmpty())
        {
            f.bundlevalue(TOOLTIP_BODY, ref.tooltipBody());
        }
        if (!ref.tooltipIcon().isEmpty())
        {
            f.bundlevalue(TOOLTIP_ICON, ref.tooltipIcon());
        }
        if (!ref.tooltipFooter().isEmpty())
        {
            f.bundlevalue(TOOLTIP_FOOTER, ref.tooltipFooter());
        }
        if (!ref.tooltipFooterIcon().isEmpty())
        {
            f.bundlevalue(TOOLTIP_FOOTER_ICON, ref.tooltipFooterIcon());
        }
        if (!ref.priority().isEmpty())
        {
            if (!(ref.priority().equalsIgnoreCase(PRIORITY_TOP) || ref.priority().equalsIgnoreCase(PRIORITY_MEDIUM) || ref.priority().equalsIgnoreCase(PRIORITY_LOW)))
            {
                throw new LayerGenerationException("priority must top, medium or low", e);
            }
            f.stringvalue(PRIORITY, ref.priority().toUpperCase());
        }        
        // Command button style
        if (!ref.buttonStyle().isEmpty())
        {
            if (!ref.buttonStyle().equals("toggle"))
            {
                throw new LayerGenerationException("buttonStyle must be toggle or blank.", e);
            }
            f.stringvalue(BUTTON_STYLE, ref.buttonStyle());
        }
        // Command button auto-repeat 
        if (ref.autoRepeatAction())
        {
            f.boolvalue(AUTO_REPEAT_ACTION, true);
        }
        // Default action 
        if (ref.defaultAction())
        {
            f.boolvalue(DEFAULT_ACTION, true);
        }
        
        f.write();

        if (ref.separatorAfter() != Integer.MAX_VALUE)
        {
            if (ref.position() == Integer.MAX_VALUE || ref.position() >= ref.separatorAfter())
            {
                throw new LayerGenerationException("separatorAfter() must be greater than position()", e);
            }
            File after = layer(e).file(ref.path() + "/" + name + "-separatorAfter.instance");
            after.newvalue("instanceCreate", JSeparator.class.getName());
            after.position(ref.separatorAfter());
            after.write();
        }
        if (ref.separatorBefore() != Integer.MAX_VALUE)
        {
            if (ref.position() == Integer.MAX_VALUE || ref.position() <= ref.separatorBefore())
            {
                throw new LayerGenerationException("separatorBefore() must be lower than position()", e);
            }
            File before = layer(e).file(ref.path() + "/" + name + "-separatorBefore.instance");
            before.newvalue("instanceCreate", JSeparator.class.getName());
            before.position(ref.separatorBefore());
            before.write();
        }
    }
//    @Override
//    public Set<String> getSupportedAnnotationTypes()
//    {
//        return new HashSet<String>();   // Using @SupportedAnnotationsTypes -- return empty set
//        
////        return new HashSet<String>(Arrays.asList(
////                RibbonActionReference.class.getCanonicalName(),
////                RibbonActionReferences.class.getCanonicalName()));
//    }
}
