/*
 * Copyright 2013 JBoss Inc
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

package org.kie.guvnor.metadata.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.kie.guvnor.metadata.client.resources.i18n.MetaDataConstants;
import org.kie.guvnor.services.metadata.model.Metadata;
import org.uberfire.client.common.DecoratedDisclosurePanel;
import org.uberfire.client.common.DirtyableComposite;
import org.uberfire.client.common.FormStyleLayout;
import org.uberfire.client.common.SmallLabel;

import static org.kie.commons.validation.PortablePreconditions.*;

/**
 * This displays the metadata for a versionable artifact. It also captures
 * edits, but it does not load or save anything itself.
 */
public class MetadataWidget
        extends DirtyableComposite {

    private Metadata metadata = null;
    private boolean readOnly;
    private VerticalPanel layout = new VerticalPanel();

    private FormStyleLayout currentSection;
    private String          currentSectionName;

    private List<DirtyableComposite> compositeList = new ArrayList<DirtyableComposite>();

    public MetadataWidget() {
        layout.setWidth( "100%" );
        initWidget( layout );
    }

    public void setContent( final Metadata metadata,
                            final boolean readOnly ) {
        this.metadata = checkNotNull( "metadata", metadata );
        this.readOnly = readOnly;

        layout.clear();

        startSection( MetaDataConstants.INSTANCE.Metadata() );
        addHeader( metadata.getPath().getFileName() );

        loadData();
    }

    private void addHeader( final String name ) {
        final HorizontalPanel hp = new HorizontalPanel();
        hp.add( new SmallLabel( "<b>" + name + "</b>" ) );
        currentSection.addAttribute( MetaDataConstants.INSTANCE.Title(), hp );
    }

    private void loadData() {
        addAttribute( MetaDataConstants.INSTANCE.CategoriesMetaData(), categories() );

        addAttribute( MetaDataConstants.INSTANCE.LastModified(),
                      readOnlyDate( metadata.getLastModified() ) );
        addAttribute( MetaDataConstants.INSTANCE.ModifiedByMetaData(),
                      readOnlyText( metadata.getLastContributor() ) );
        addAttribute( MetaDataConstants.INSTANCE.NoteMetaData(),
                      readOnlyText( metadata.getCheckinComment() ) );

        if ( !readOnly ) {
            addAttribute( MetaDataConstants.INSTANCE.CreatedOnMetaData(),
                          readOnlyDate( metadata.getDateCreated() ) );
        }

        addAttribute( MetaDataConstants.INSTANCE.CreatedByMetaData(),
                      readOnlyText( metadata.getCreator() ) );

        addAttribute( MetaDataConstants.INSTANCE.IsDisabledMetaData(),
                      editableBoolean( new FieldBooleanBinding() {
                          public boolean getValue() {
                              return metadata.isDisabled();
                          }

                          public void setValue( final boolean val ) {
                              makeDirty();
                              metadata.setDisabled( val );
                          }
                      }, MetaDataConstants.INSTANCE.DisableTip() ) );

        addAttribute( MetaDataConstants.INSTANCE.FormatMetaData(),
                      readOnlyText( metadata.getFormat() ) );
        addAttribute( "URI:",
                      readOnlyText( metadata.getPath().toURI() ) );

        endSection( false );

        startSection( MetaDataConstants.INSTANCE.OtherMetaData() );

        addAttribute( MetaDataConstants.INSTANCE.SubjectMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getSubject();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setSubject( val );
                          }
                      }, MetaDataConstants.INSTANCE.AShortDescriptionOfTheSubjectMatter() ) );

        addAttribute( MetaDataConstants.INSTANCE.TypeMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getType();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setType( val );
                          }

                      }, MetaDataConstants.INSTANCE.TypeTip() ) );

        addAttribute( MetaDataConstants.INSTANCE.ExternalLinkMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getExternalRelation();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setExternalRelation( val );
                          }

                      }, MetaDataConstants.INSTANCE.ExternalLinkTip() ) );

        addAttribute( MetaDataConstants.INSTANCE.SourceMetaData(),
                      editableText( new FieldBinding() {
                          public String getValue() {
                              return metadata.getExternalSource();
                          }

                          public void setValue( final String val ) {
                              makeDirty();
                              metadata.setExternalSource( val );
                          }

                      }, MetaDataConstants.INSTANCE.SourceMetaDataTip() ) );

        endSection( true );

        if ( !readOnly ) {
            startSection( MetaDataConstants.INSTANCE.VersionHistory() );
            addRow( new VersionBrowser( metadata ) );
            endSection( true );
        }

        layout.add( commentWidget() );

        layout.add( discussionWidget() );
    }

    private Widget commentWidget() {
        final CommentWidget widget = new CommentWidget( metadata, readOnly );
        compositeList.add( widget );

        return widget;
    }

    private Widget discussionWidget() {
        final DiscussionWidget widget = new DiscussionWidget( metadata, readOnly );
        compositeList.add( widget );

        return widget;
    }

    private void addRow( Widget widget ) {
        this.currentSection.addRow( widget );
    }

    private void addAttribute( final String string,
                               final Widget widget ) {
        this.currentSection.addAttribute( string, widget );
    }

    private void endSection( final boolean collapsed ) {
        final DecoratedDisclosurePanel advancedDisclosure = new DecoratedDisclosurePanel( currentSectionName );
        advancedDisclosure.setWidth( "100%" );
        advancedDisclosure.setOpen( !collapsed );
        advancedDisclosure.setContent( this.currentSection );
        layout.add( advancedDisclosure );
    }

    private void startSection( final String name ) {
        currentSection = new FormStyleLayout();
        currentSectionName = name;
    }

    private Widget readOnlyDate( final Date date ) {
        if ( date == null ) {
            return null;
        } else {
            return new SmallLabel( DateTimeFormat.getFormat( DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT ).format( date ) );
        }
    }

    private Label readOnlyText( final String text ) {
        SmallLabel lbl = new SmallLabel( text );
        lbl.setWidth( "100%" );
        return lbl;
    }

    private Widget categories() {
        return new CategorySelectorWidget( metadata, this.readOnly );
    }

    public boolean isDirty() {
        for ( final DirtyableComposite widget : compositeList ) {
            if ( widget.isDirty() ) {
                return true;
            }
        }

        return dirtyflag;
    }

    public void resetDirty() {
        for ( final DirtyableComposite widget : compositeList ) {
            widget.resetDirty();
        }
        this.dirtyflag = false;
    }

    /**
     * This binds a field, and returns a check box editor for it.
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableBoolean( final FieldBooleanBinding bind,
                                    final String toolTip ) {
        if ( !readOnly ) {
            final CheckBox box = new CheckBox();
            box.setTitle( toolTip );
            box.setValue( bind.getValue() );
            box.addClickHandler( new ClickHandler() {
                public void onClick( ClickEvent w ) {
                    boolean b = box.getValue();
                    bind.setValue( b );
                }
            } );
            return box;
        } else {
            final CheckBox box = new CheckBox();

            box.setValue( bind.getValue() );
            box.setEnabled( false );

            return box;
        }
    }

    /**
     * This binds a field, and returns a TextBox editor for it.
     * @param bind Interface to bind to.
     * @param toolTip tool tip.
     * @return
     */
    private Widget editableText( final FieldBinding bind,
                                 String toolTip ) {
        if ( !readOnly ) {
            final TextBox tbox = new TextBox();
            tbox.setTitle( toolTip );
            tbox.setText( bind.getValue() );
            tbox.setVisibleLength( 10 );
            tbox.addChangeHandler( new ChangeHandler() {
                public void onChange( final ChangeEvent event ) {
                    bind.setValue( tbox.getText() );
                }
            } );
            return tbox;
        } else {
            return new Label( bind.getValue() );
        }
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBinding {

        void setValue( String val );

        String getValue();
    }

    /**
     * used to bind fields in the meta data DTO to the form
     */
    static interface FieldBooleanBinding {

        void setValue( boolean val );

        boolean getValue();
    }

    /**
     * Return the data if it is to be saved.
     */
    public Metadata getContent() {
        return metadata;
    }
}
