package org.kie.guvnor.explorer.client;

import java.util.Collections;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.kie.guvnor.explorer.client.util.FoldersFirstAlphabeticalComparator;
import org.kie.guvnor.explorer.client.widget.FileWidget;
import org.kie.guvnor.explorer.client.widget.FolderWidget;
import org.kie.guvnor.explorer.client.widget.ProjectWidget;
import org.kie.guvnor.explorer.client.widget.RepositoryWidget;
import org.kie.guvnor.explorer.model.Item;

/**
 * The ExplorerPresenter's view implementation
 */
public class ExplorerView extends Composite implements ExplorerPresenter.View {

    private final FoldersFirstAlphabeticalComparator sorter = new FoldersFirstAlphabeticalComparator();

    private ExplorerPresenter presenter;

    private final VerticalPanel container = new VerticalPanel();

    private final VerticalPanel itemWidgetsContainer = new VerticalPanel();

    public ExplorerView() {
        container.add( new Label( "Breadcrumbs to go here..." ) );
        container.add( itemWidgetsContainer );
        initWidget( container );
    }

    @Override
    public void init( final ExplorerPresenter presenter ) {
        this.presenter = presenter;
    }

    @Override
    public void setItems( final List<Item> items ) {

        Collections.sort( items,
                          sorter );
        itemWidgetsContainer.clear();

        for ( final Item item : items ) {
            IsWidget itemWidget = null;
            switch ( item.getType() ) {
                case PARENT_FOLDER:
                    itemWidget = new FolderWidget( item.getPath(),
                                                   item.getCaption(),
                                                   presenter );
                    break;
                case REPOSITORY:
                    itemWidget = new RepositoryWidget( item.getPath(),
                                                       item.getCaption(),
                                                       presenter );
                    break;
                case PROJECT:
                    itemWidget = new ProjectWidget( item.getPath(),
                                                    item.getCaption(),
                                                    presenter );
                    break;
                case FOLDER:
                    itemWidget = new FolderWidget( item.getPath(),
                                                   item.getCaption(),
                                                   presenter );
                    break;
                case FILE:
                    itemWidget = new FileWidget( item.getPath(),
                                                 item.getCaption(),
                                                 presenter );
                    break;
            }
            if ( itemWidget != null ) {
                itemWidgetsContainer.add( itemWidget );
            }
        }

    }

}
