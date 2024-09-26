<<<<<<< HEAD
class Mode { static final int ATTRIBUTE_PROCESSING_NONE = 0 ; static final int ATTRIBUTE_PROCESSING_QUALIFIED = 1 ; static final int ATTRIBUTE_PROCESSING_FULL = 2 ; static final Mode CURRENT = new Mode ( "#current" , null ) ; private static final String ANONYMOUS_MODE_NAME_PREFIX = "#anonymous#" ; private static int anonymousModeCounter = 0 ; private boolean anonymous ; private final String name ; private Mode baseMode ; private boolean defined ; private Locator whereDefined ; private Locator whereUsed ; private final Hashtable elementMap = new Hashtable ( ) ; private final Hashtable attributeMap = new Hashtable ( ) ; private int attributeProcessing = - 1 ; private final Hashtable nssElementMap = new Hashtable ( ) ; private final Hashtable nssAttributeMap = new Hashtable ( ) ; private List includedModes = new ArrayList ( ) ; void addIncludedMode ( Mode mode ) { includedModes . add ( mode ) ; } Mode ( String name , Mode baseMode ) { this . name = name ; this . baseMode = baseMode ; } public Mode ( Mode baseMode ) { this ( ANONYMOUS_MODE_NAME_PREFIX + anonymousModeCounter ++ , baseMode ) ; anonymous = true ; } String getName ( ) { return name ; } Mode getBaseMode ( ) { return baseMode ; } void setBaseMode ( Mode baseMode ) { this . baseMode = baseMode ; } ActionSet getElementActions ( String ns ) { ActionSet actions = getElementActionsExplicit ( ns ) ; if ( actions == null ) { actions = getElementActionsExplicit ( NamespaceSpecification . ANY_NAMESPACE ) ; } return actions ; } private ActionSet getElementActionsExplicit ( String ns ) { ActionSet actions = ( ActionSet ) elementMap . get ( ns ) ; if ( actions == null ) { for ( Enumeration e = nssElementMap . keys ( ) ; e . hasMoreElements ( ) && actions == null ; ) { NamespaceSpecification nssI = ( NamespaceSpecification ) e . nextElement ( ) ; if ( nssI . covers ( ns ) ) { actions = ( ActionSet ) nssElementMap . get ( nssI ) ; } } if ( actions != null ) { elementMap . put ( ns , actions ) ; } } if ( actions == null && includedModes != null ) { Iterator i = includedModes . iterator ( ) ; while ( actions == null && i . hasNext ( ) ) { Mode includedMode = ( Mode ) i . next ( ) ; actions = includedMode . getElementActionsExplicit ( ns ) ; } if ( actions != null ) { actions = actions . changeCurrentMode ( this ) ; elementMap . put ( ns , actions ) ; } } if ( actions == null && baseMode != null ) { actions = baseMode . getElementActionsExplicit ( ns ) ; if ( actions != null ) { actions = actions . changeCurrentMode ( this ) ; elementMap . put ( ns , actions ) ; } } if ( actions != null && actions . getCancelNestedActions ( ) ) { actions = null ; } return actions ; } AttributeActionSet getAttributeActions ( String ns ) { AttributeActionSet actions = getAttributeActionsExplicit ( ns ) ; if ( actions == null ) { actions = getAttributeActionsExplicit ( NamespaceSpecification . ANY_NAMESPACE ) ; } return actions ; } private AttributeActionSet getAttributeActionsExplicit ( String ns ) { AttributeActionSet actions = ( AttributeActionSet ) attributeMap . get ( ns ) ; if ( actions == null ) { for ( Enumeration e = nssAttributeMap . keys ( ) ; e . hasMoreElements ( ) && actions == null ; ) { NamespaceSpecification nssI = ( NamespaceSpecification ) e . nextElement ( ) ; if ( nssI . covers ( ns ) ) { actions = ( AttributeActionSet ) nssAttributeMap . get ( nssI ) ; } } if ( actions != null ) { attributeMap . put ( ns , actions ) ; } } if ( actions == null && includedModes != null ) { Iterator i = includedModes . iterator ( ) ; while ( actions == null && i . hasNext ( ) ) { Mode includedMode = ( Mode ) i . next ( ) ; actions = includedMode . getAttributeActionsExplicit ( ns ) ; } if ( actions != null ) { attributeMap . put ( ns , actions ) ; } } if ( actions == null && baseMode != null ) { actions = baseMode . getAttributeActionsExplicit ( ns ) ; if ( actions != null ) attributeMap . put ( ns , actions ) ; } if ( actions != null && actions . getCancelNestedActions ( ) ) { actions = null ; } return actions ; } int getAttributeProcessing ( ) { if ( attributeProcessing == - 1 ) { if ( baseMode != null ) attributeProcessing = baseMode . getAttributeProcessing ( ) ; else attributeProcessing = ATTRIBUTE_PROCESSING_NONE ; for ( Enumeration e = nssAttributeMap . keys ( ) ; e . hasMoreElements ( ) && attributeProcessing != ATTRIBUTE_PROCESSING_FULL ; ) { NamespaceSpecification nss = ( NamespaceSpecification ) e . nextElement ( ) ; AttributeActionSet actions = ( AttributeActionSet ) nssAttributeMap . get ( nss ) ; if ( ! actions . getAttach ( ) || actions . getReject ( ) || actions . getSchemas ( ) . length > 0 ) attributeProcessing = ( ( nss . ns . equals ( "" ) || nss . ns . equals ( NamespaceSpecification . ANY_NAMESPACE ) ) ? ATTRIBUTE_PROCESSING_FULL : ATTRIBUTE_PROCESSING_QUALIFIED ) ; } } return attributeProcessing ; } Locator getWhereDefined ( ) { return whereDefined ; } boolean isDefined ( ) { return defined ; } boolean isAnonymous ( ) { return anonymous ; } Locator getWhereUsed ( ) { return whereUsed ; } void noteUsed ( Locator locator ) { if ( whereUsed == null && locator != null ) whereUsed = new LocatorImpl ( locator ) ; } void noteDefined ( Locator locator ) { defined = true ; if ( whereDefined == null && locator != null ) whereDefined = new LocatorImpl ( locator ) ; } boolean bindElement ( String ns , String wildcard , ActionSet actions ) { NamespaceSpecification nss = new NamespaceSpecification ( ns , wildcard ) ; if ( nssElementMap . get ( nss ) != null ) return false ; for ( Enumeration e = nssElementMap . keys ( ) ; e . hasMoreElements ( ) ; ) { NamespaceSpecification nssI = ( NamespaceSpecification ) e . nextElement ( ) ; if ( nss . compete ( nssI ) ) { return false ; } } nssElementMap . put ( nss , actions ) ; return true ; } boolean bindAttribute ( String ns , String wildcard , AttributeActionSet actions ) { NamespaceSpecification nss = new NamespaceSpecification ( ns , wildcard ) ; if ( nssAttributeMap . get ( nss ) != null ) return false ; for ( Enumeration e = nssAttributeMap . keys ( ) ; e . hasMoreElements ( ) ; ) { NamespaceSpecification nssI = ( NamespaceSpecification ) e . nextElement ( ) ; if ( nss . compete ( nssI ) ) { return false ; } } nssAttributeMap . put ( nss , actions ) ; return true ; } } 
=======
public abstract class AbstractSchema implements Schema { private final PropertyMap properties ; public AbstractSchema ( ) { this ( PropertyMap . EMPTY ) ; } public AbstractSchema ( PropertyMap properties ) { this . properties = properties ; } public AbstractSchema ( PropertyMap properties , PropertyId < ? > [ ] supportedPropertyIds ) { this ( filterProperties ( properties , supportedPropertyIds ) ) ; } public PropertyMap getProperties ( ) { return properties ; } static public PropertyMap filterProperties ( PropertyMap properties , PropertyId < ? > [ ] supportedPropertyIds ) { PropertyMapBuilder builder = new PropertyMapBuilder ( ) ; for ( int i = 0 ; i < supportedPropertyIds . length ; i ++ ) copy ( builder , supportedPropertyIds [ i ] , properties ) ; return builder . toPropertyMap ( ) ; } static private < T > void copy ( PropertyMapBuilder builder , PropertyId < T > pid , PropertyMap properties ) { T value = properties . get ( pid ) ; if ( value != null ) builder . put ( pid , value ) ; } } 
>>>>>>> ab1fba6c6e93a4331abe98d3c4c0cdc860e899a6
