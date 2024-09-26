<<<<<<< HEAD
public class ClonePathsWizardPanel implements WizardDescriptor . Panel { private ClonePathsPanel component ; private URIish repositoryOrig ; private Listener listener ; private URIish pullUrl , pushUrl ; private URIish defaultUrl ; private String defaultUrlString ; public Component getComponent ( ) { if ( component == null ) { component = new ClonePathsPanel ( ) ; initInteraction ( ) ; } return component ; } private void initInteraction ( ) { listener = new Listener ( ) ; component . defaultValuesButton . addActionListener ( listener ) ; component . changePullPathButton . addActionListener ( listener ) ; component . changePushPathButton . addActionListener ( listener ) ; } final class Listener implements ActionListener { public void actionPerformed ( ActionEvent e ) { URIish changedUrl ; Object source = e . getSource ( ) ; if ( source == component . defaultValuesButton ) { setDefaultValues ( ) ; } else if ( source == component . changePullPathButton ) { changedUrl = changeUrl ( "changePullPath.Title" ) ; if ( changedUrl != null ) { component . defaultPullPathField . setText ( changedUrl . toString ( ) ) ; } } else if ( source == component . changePushPathButton ) { changedUrl = changeUrl ( "changePushPath.Title" ) ; if ( changedUrl != null ) { component . defaultPushPathField . setText ( changedUrl . toString ( ) ) ; } } else { assert false ; } } } private URIish changeUrl ( String titleMsgKey ) { int repoModeMask = GitRepositoryUI . FLAG_URL_ENABLED | GitRepositoryUI . FLAG_SHOW_HINTS ; String title = getMessage ( titleMsgKey ) ; final JButton set = new JButton ( ) ; final JButton clear = new JButton ( ) ; Mnemonics . setLocalizedText ( set , getMessage ( "changePullPushPath.Set" ) ) ; Mnemonics . setLocalizedText ( clear , getMessage ( "changePullPushPath.Clear" ) ) ; final GitRepositoryUI repository = new GitRepositoryUI ( repoModeMask , title , true ) ; set . setEnabled ( repository . isValid ( ) ) ; clear . setDefaultCapable ( false ) ; final DialogDescriptor dialogDescriptor = new DialogDescriptor ( GitUtils . addContainerBorder ( repository . getPanel ( ) ) , title , true , new Object [ ] { set , clear , CANCEL_OPTION } , set , DEFAULT_ALIGN , new HelpCtx ( ClonePathsWizardPanel . class . getName ( ) + ".change" ) , null ) ; dialogDescriptor . setClosingOptions ( new Object [ ] { clear , CANCEL_OPTION } ) ; final NotificationLineSupport notificationLineSupport = dialogDescriptor . createNotificationLineSupport ( ) ; class RepositoryChangeListener implements ChangeListener , ActionListener { private Dialog dialog ; public void setDialog ( Dialog dialog ) { this . dialog = dialog ; } public void stateChanged ( ChangeEvent e ) { assert e . getSource ( ) == repository ; boolean isValid = repository . isValid ( ) ; dialogDescriptor . setValid ( isValid ) ; set . setEnabled ( isValid ) ; if ( isValid ) { notificationLineSupport . clearMessages ( ) ; } else { String errMsg = repository . getMessage ( ) ; if ( ( errMsg != null ) && ( errMsg . length ( ) != 0 ) ) { notificationLineSupport . setErrorMessage ( errMsg ) ; } else { notificationLineSupport . clearMessages ( ) ; } } } public void actionPerformed ( ActionEvent e ) { if ( e . getSource ( ) != set ) { return ; } try { dialogDescriptor . setValue ( repository . getUrl ( ) ) ; dialog . setVisible ( false ) ; dialog . dispose ( ) ; } catch ( MalformedURLException ex ) { repository . setInvalid ( ) ; notificationLineSupport . setErrorMessage ( ex . getMessage ( ) ) ; } catch ( URISyntaxException ex ) { repository . setInvalid ( ) ; notificationLineSupport . setErrorMessage ( ex . getMessage ( ) ) ; } } } RepositoryChangeListener optionListener = new RepositoryChangeListener ( ) ; repository . addChangeListener ( optionListener ) ; dialogDescriptor . setButtonListener ( optionListener ) ; Dialog dialog = DialogDisplayer . getDefault ( ) . createDialog ( dialogDescriptor ) ; optionListener . setDialog ( dialog ) ; dialog . pack ( ) ; dialog . setVisible ( true ) ; Object selectedValue = dialogDescriptor . getValue ( ) ; assert ( selectedValue instanceof URIish ) || ( selectedValue == clear ) || ( selectedValue == CANCEL_OPTION ) || ( selectedValue == CLOSED_OPTION ) ; if ( selectedValue instanceof URIish ) { return ( URIish ) selectedValue ; } else if ( selectedValue == clear ) { return new URIish ( ) ; } else { return null ; } } public boolean isValid ( ) { return true ; } public HelpCtx getHelp ( ) { return new HelpCtx ( ClonePathsWizardPanel . class ) ; } public final void addChangeListener ( ChangeListener l ) { } public final void removeChangeListener ( ChangeListener l ) { } private void setDefaultValues ( ) { setDefaultValues ( true , true ) ; } private void setDefaultValues ( boolean pullPath , boolean pushPath ) { if ( pullPath ) { component . defaultPullPathField . setText ( getDefaultPath ( ) ) ; pullUrl = repositoryOrig ; } if ( pushPath ) { component . defaultPushPathField . setText ( getDefaultPath ( ) ) ; pushUrl = repositoryOrig ; } } private String getDefaultPath ( ) { if ( defaultUrlString == null ) { defaultUrlString = repositoryOrig . toString ( ) ; } return defaultUrlString ; } public void readSettings ( Object settings ) { assert ( settings instanceof WizardDescriptor ) ; defaultUrl = ( URIish ) ( ( WizardDescriptor ) settings ) . getProperty ( "repository" ) ; URIish repository = defaultUrl ; boolean repoistoryChanged = ! repository . equals ( repositoryOrig ) ; repositoryOrig = repository ; defaultUrlString = null ; boolean resetPullPath = repoistoryChanged || ( pullUrl == null ) ; boolean resetPushPath = repoistoryChanged || ( pushUrl == null ) ; setDefaultValues ( resetPullPath , resetPushPath ) ; } public void storeSettings ( Object settings ) { if ( settings instanceof WizardDescriptor ) { ( ( WizardDescriptor ) settings ) . putProperty ( "defaultPullPath" , pullUrl ) ; ( ( WizardDescriptor ) settings ) . putProperty ( "defaultPushPath" , pushUrl ) ; } } private static String getMessage ( String msgKey ) { return NbBundle . getMessage ( ClonePathsWizardPanel . class , msgKey ) ; } } 
=======
public interface RegexEngine { Regex compile ( String str ) throws RegexSyntaxException ; } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
