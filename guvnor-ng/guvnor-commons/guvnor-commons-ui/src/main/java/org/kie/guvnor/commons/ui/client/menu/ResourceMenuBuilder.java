/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.commons.ui.client.menu;

import org.kie.guvnor.commons.ui.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.menu.MenuBar;
import org.uberfire.client.workbench.widgets.menu.MenuItem;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuBar;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemSubMenu;

/**
 *
 */
public final class ResourceMenuBuilder {

    public static ResourceMenuBuilder newResourceMenuBuilder() {
        return new ResourceMenuBuilder();
    }

    private ResourceMenuBuilder() {
    }

    private Command saveCommand     = null;
    private Command restoreCommand  = null;
    private Command validateCommand = null;

    public ResourceMenuBuilder addValidation( final Command command ) {
        this.validateCommand = command;
        return this;
    }

    public ResourceMenuBuilder addSave( final Command command ) {
        this.saveCommand = command;
        return this;
    }

    public ResourceMenuBuilder addRestoreVersion( final Command command ) {
        this.restoreCommand = command;
        return this;
    }

    public MenuBar build() {
        final MenuBar menuBar = new DefaultMenuBar();
        final MenuBar subMenuBar = new DefaultMenuBar();
        menuBar.addItem( new DefaultMenuItemSubMenu( CommonConstants.INSTANCE.File(),
                                                     subMenuBar ) );

        if ( validateCommand != null ) {
            final MenuItem validate = new DefaultMenuItemCommand( CommonConstants.INSTANCE.Validate(),
                                                                  validateCommand );
            subMenuBar.addItem( validate );
        }

        if ( saveCommand != null ) {
            final MenuItem save = new DefaultMenuItemCommand( CommonConstants.INSTANCE.Save(),
                                                              saveCommand );
            subMenuBar.addItem( save );
        }

        if ( restoreCommand != null ) {
            final MenuItem restore = new DefaultMenuItemCommand( CommonConstants.INSTANCE.Restore(),
                                                                 restoreCommand );
            subMenuBar.addItem( restore );
        }

        return menuBar;

    }

}
