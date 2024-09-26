public class EditableLabelExtension extends Composite implements HasWordWrap , HasText { protected static final String HOVER_STYLE = "hover" ; private TextBox changeText ; private TextArea changeTextArea ; private Label text ; private String originalText ; private Widget confirmChange ; private Widget cancelChange ; private boolean isEditing = false ; private boolean isEditable = true ; private ChangeListener updater = null ; private String defaultOkButtonText = "OK" ; private String defaultCancelButtonText = "Cancel" ; public void setEditable ( boolean flag ) { isEditable = flag ; } public boolean isFieldEditable ( ) { return isEditable ; } public boolean isInEditingMode ( ) { return isEditing ; } private void changeTextLabel ( ) { if ( isEditable ) { originalText = text . getText ( ) ; text . setVisible ( false ) ; confirmChange . setVisible ( true ) ; cancelChange . setVisible ( true ) ; if ( text . getWordWrap ( ) ) { changeTextArea . setText ( originalText ) ; changeTextArea . setVisible ( true ) ; changeTextArea . setFocus ( true ) ; } else { changeText . setText ( originalText ) ; changeText . setVisible ( true ) ; changeText . setFocus ( true ) ; } isEditing = true ; } } private void restoreVisibility ( ) { text . setVisible ( true ) ; confirmChange . setVisible ( false ) ; cancelChange . setVisible ( false ) ; if ( text . getWordWrap ( ) ) { changeTextArea . setVisible ( false ) ; } else { changeText . setVisible ( false ) ; } isEditing = false ; } private void setTextLabel ( ) { if ( text . getWordWrap ( ) ) { text . setText ( changeTextArea . getText ( ) ) ; } else { text . setText ( changeText . getText ( ) ) ; } restoreVisibility ( ) ; updater . onChange ( this ) ; } public void cancelLabelChange ( ) { text . setText ( originalText ) ; restoreVisibility ( ) ; } private void createEditableLabel ( String labelText , ChangeListener onUpdate , String okButtonText , String cancelButtonText ) { FlowPanel instance = new FlowPanel ( ) ; if ( labelText == null || labelText . length ( ) < 1 ) { labelText = "Click to edit me" ; } text = new Label ( labelText ) ; text . setStylePrimaryName ( "editableLabel-label" ) ; text . addClickListener ( new ClickListener ( ) { public void onClick ( Widget sender ) { changeTextLabel ( ) ; } } ) ; text . addMouseListener ( new MouseListenerAdapter ( ) { public void onMouseEnter ( Widget sender ) { text . addStyleDependentName ( HOVER_STYLE ) ; } public void onMouseLeave ( Widget sender ) { text . removeStyleDependentName ( HOVER_STYLE ) ; } } ) ; changeText = new TextBox ( ) ; changeText . setStyleName ( "editableLabel-textBox" ) ; changeText . addKeyboardListener ( new KeyboardListenerAdapter ( ) { public void onKeyPress ( Widget sender , char keyCode , int modifiers ) { switch ( keyCode ) { case 13 : setTextLabel ( ) ; break ; case 27 : cancelLabelChange ( ) ; break ; } } } ) ; changeTextArea = new TextArea ( ) ; changeTextArea . setStyleName ( "editableLabel-textArea" ) ; changeTextArea . addKeyboardListener ( new KeyboardListenerAdapter ( ) { public void onKeyPress ( Widget sender , char keyCode , int modifiers ) { switch ( keyCode ) { case 27 : cancelLabelChange ( ) ; break ; } } } ) ; confirmChange = createConfirmButton ( okButtonText ) ; if ( ! ( confirmChange instanceof SourcesClickEvents ) ) { throw new RuntimeException ( "Confirm change button must allow for click events" ) ; } ( ( SourcesClickEvents ) confirmChange ) . addClickListener ( new ClickListener ( ) { public void onClick ( Widget sender ) { setTextLabel ( ) ; } } ) ; cancelChange = createCancelButton ( cancelButtonText ) ; if ( ! ( cancelChange instanceof SourcesClickEvents ) ) { throw new RuntimeException ( "Cancel change button must allow for click events" ) ; } ( ( SourcesClickEvents ) cancelChange ) . addClickListener ( new ClickListener ( ) { public void onClick ( Widget sender ) { cancelLabelChange ( ) ; } } ) ; FlowPanel buttonPanel = new FlowPanel ( ) ; buttonPanel . setStyleName ( "editableLabel-buttonPanel" ) ; buttonPanel . add ( confirmChange ) ; buttonPanel . add ( cancelChange ) ; instance . add ( text ) ; instance . add ( changeText ) ; instance . add ( changeTextArea ) ; instance . add ( buttonPanel ) ; text . setVisible ( true ) ; changeText . setVisible ( false ) ; changeTextArea . setVisible ( false ) ; confirmChange . setVisible ( false ) ; cancelChange . setVisible ( false ) ; updater = onUpdate ; text . setWordWrap ( false ) ; initWidget ( instance ) ; } protected Widget createCancelButton ( String cancelButtonText ) { Button result = new Button ( ) ; result . setStyleName ( "editableLabel-buttons" ) ; result . addStyleName ( "editableLabel-cancel" ) ; result . setText ( cancelButtonText ) ; return result ; } protected Widget createConfirmButton ( String okButtonText ) { Button result = new Button ( ) ; result . setStyleName ( "editableLabel-buttons" ) ; result . addStyleName ( "editableLabel-confirm" ) ; result . setText ( okButtonText ) ; return result ; } public void setWordWrap ( boolean b ) { text . setWordWrap ( b ) ; } public boolean getWordWrap ( ) { return text . getWordWrap ( ) ; } public String getText ( ) { return text . getText ( ) ; } public void setText ( String newText ) { text . setText ( newText ) ; } public void setVisibleLines ( int number ) { if ( text . getWordWrap ( ) ) { changeTextArea . setVisibleLines ( number ) ; } else { throw new RuntimeException ( "Cannnot set number of visible lines for a non word-wrapped Editable Label" ) ; } } public int getVisibleLines ( ) { if ( text . getWordWrap ( ) ) { return changeTextArea . getVisibleLines ( ) ; } else { throw new RuntimeException ( "Editable Label that is not word-wrapped has no number of Visible Lines" ) ; } } public void setMaxLength ( int length ) { if ( text . getWordWrap ( ) ) { changeTextArea . setCharacterWidth ( length ) ; } else { changeText . setMaxLength ( length ) ; } } public int getMaxLength ( ) { if ( text . getWordWrap ( ) ) { return changeTextArea . getCharacterWidth ( ) ; } else { return changeText . getMaxLength ( ) ; } } public void setVisibleLength ( int length ) { if ( text . getWordWrap ( ) ) { throw new RuntimeException ( "Cannnot set visible length for a word-wrapped Editable Label" ) ; } else { changeText . setVisibleLength ( length ) ; } } public int getVisibleLength ( ) { if ( text . getWordWrap ( ) ) { throw new RuntimeException ( "Cannnot get visible length for a word-wrapped Editable Label" ) ; } else { return changeText . getVisibleLength ( ) ; } } public EditableLabelExtension ( String labelText , ChangeListener onUpdate , String okText , String cancelText , boolean wordWrap ) { createEditableLabel ( labelText , onUpdate , okText , cancelText ) ; text . setWordWrap ( wordWrap ) ; } public EditableLabelExtension ( String labelText , ChangeListener onUpdate , boolean wordWrap ) { createEditableLabel ( labelText , onUpdate , defaultOkButtonText , defaultCancelButtonText ) ; text . setWordWrap ( wordWrap ) ; } public EditableLabelExtension ( String labelText , ChangeListener onUpdate , String okText , String cancelText ) { createEditableLabel ( labelText , onUpdate , okText , cancelText ) ; } public EditableLabelExtension ( String labelText , ChangeListener onUpdate ) { createEditableLabel ( labelText , onUpdate , defaultOkButtonText , defaultCancelButtonText ) ; } public EditableLabelExtension ( String str ) { this ( str , new ChangeListener ( ) { public void onChange ( Widget sender ) { } } ) ; } } 