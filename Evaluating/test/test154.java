public class Atom10ParserV2 extends Atom10Parser { private static final String ATOM_10_URI = "http://www.w3.org/2005/Atom" ; Namespace ns = Namespace . getNamespace ( ATOM_10_URI ) ; protected WireFeed parseFeed ( Element eFeed ) throws FeedException { com . sun . syndication . feed . atom . Feed feed = new com . sun . syndication . feed . atom . Feed ( getType ( ) ) ; String baseURI ; try { baseURI = findBaseURI ( eFeed ) ; } catch ( Exception e ) { throw new FeedException ( "ERROR while finding base URI of feed" , e ) ; } String xmlBase = eFeed . getAttributeValue ( "base" , Namespace . XML_NAMESPACE ) ; if ( xmlBase != null ) { feed . setXmlBase ( xmlBase ) ; } Element e = eFeed . getChild ( "title" , getAtomNamespace ( ) ) ; if ( e != null ) { Content c = new Content ( ) ; c . setValue ( parseTextConstructToString ( e ) ) ; c . setType ( e . getAttributeValue ( "type" ) ) ; feed . setTitleEx ( c ) ; } List < Element > eList = getChildren ( eFeed , "link" ) ; feed . setAlternateLinks ( parseAlternateLinks ( feed , null , baseURI , eList ) ) ; feed . setOtherLinks ( parseOtherLinks ( feed , null , baseURI , eList ) ) ; List cList = eFeed . getChildren ( "category" , getAtomNamespace ( ) ) ; feed . setCategories ( parseCategories ( baseURI , cList ) ) ; eList = getChildren ( eFeed , "author" ) ; if ( eList . size ( ) > 0 ) { feed . setAuthors ( parsePersons ( baseURI , eList ) ) ; } eList = getChildren ( eFeed , "contributor" ) ; if ( eList . size ( ) > 0 ) { feed . setContributors ( parsePersons ( baseURI , eList ) ) ; } e = eFeed . getChild ( "subtitle" , getAtomNamespace ( ) ) ; if ( e != null ) { Content subtitle = new Content ( ) ; subtitle . setValue ( parseTextConstructToString ( e ) ) ; subtitle . setType ( e . getAttributeValue ( "type" ) ) ; feed . setSubtitle ( subtitle ) ; } e = eFeed . getChild ( "id" , getAtomNamespace ( ) ) ; if ( e != null ) { feed . setId ( e . getText ( ) ) ; } e = eFeed . getChild ( "generator" , getAtomNamespace ( ) ) ; if ( e != null ) { Generator gen = new Generator ( ) ; gen . setValue ( e . getText ( ) ) ; String att = e . getAttributeValue ( "uri" ) ; if ( att != null ) { gen . setUrl ( att ) ; } att = e . getAttributeValue ( "version" ) ; if ( att != null ) { gen . setVersion ( att ) ; } feed . setGenerator ( gen ) ; } e = eFeed . getChild ( "rights" , getAtomNamespace ( ) ) ; if ( e != null ) { feed . setRights ( parseTextConstructToString ( e ) ) ; } e = eFeed . getChild ( "icon" , getAtomNamespace ( ) ) ; if ( e != null ) { feed . setIcon ( e . getText ( ) ) ; } e = eFeed . getChild ( "logo" , getAtomNamespace ( ) ) ; if ( e != null ) { feed . setLogo ( e . getText ( ) ) ; } e = eFeed . getChild ( "updated" , getAtomNamespace ( ) ) ; if ( e != null ) { feed . setUpdated ( DateParser . parseDate ( e . getText ( ) ) ) ; } feed . setModules ( parseFeedModules ( eFeed ) ) ; eList = getChildren ( eFeed , "entry" ) ; if ( eList . size ( ) > 0 ) { feed . setEntries ( parseEntries ( feed , baseURI , eList ) ) ; } List foreignMarkup = extractForeignMarkup ( eFeed , feed , getAtomNamespace ( ) ) ; if ( foreignMarkup . size ( ) > 0 ) { feed . setForeignMarkup ( foreignMarkup ) ; } return feed ; } private List < Element > getChildren ( Element eFeed , String name ) { return ( List < Element > ) eFeed . getChildren ( name , getAtomNamespace ( ) ) ; } private Link parseLink ( Feed feed , Entry entry , String baseURI , Element eLink ) { Link link = new Link ( ) ; String att = eLink . getAttributeValue ( "rel" ) ; if ( att != null ) { link . setRel ( att ) ; } att = eLink . getAttributeValue ( "type" ) ; if ( att != null ) { link . setType ( att ) ; } att = eLink . getAttributeValue ( "href" ) ; if ( att != null ) { if ( isRelativeURI ( att ) ) { link . setHref ( resolveURI ( baseURI , eLink , att ) ) ; } else { link . setHref ( att ) ; } } att = eLink . getAttributeValue ( "title" ) ; if ( att != null ) { link . setTitle ( att ) ; } att = eLink . getAttributeValue ( "hreflang" ) ; if ( att != null ) { link . setHreflang ( att ) ; } att = eLink . getAttributeValue ( "length" ) ; if ( att != null ) { link . setLength ( Long . parseLong ( att ) ) ; } return link ; } private List < Link > parseAlternateLinks ( Feed feed , Entry entry , String baseURI , List < Element > eLinks ) { List < Link > links = new ArrayList < Link > ( ) ; for ( Element eLink : eLinks ) { Link link = parseLink ( feed , entry , baseURI , eLink ) ; if ( link . getRel ( ) == null || "" . equals ( link . getRel ( ) . trim ( ) ) || "alternate" . equals ( link . getRel ( ) ) ) { links . add ( link ) ; } } return ( links . size ( ) > 0 ) ? links : null ; } private List < Link > parseOtherLinks ( Feed feed , Entry entry , String baseURI , List < Element > eLinks ) { List < Link > links = new ArrayList < Link > ( ) ; for ( Element eLink : eLinks ) { Link link = parseLink ( feed , entry , baseURI , eLink ) ; if ( ! "alternate" . equals ( link . getRel ( ) ) ) { links . add ( link ) ; } } return ( links . size ( ) > 0 ) ? links : null ; } private Person parsePerson ( String baseURI , Element ePerson ) { Person person = new Person ( ) ; Element e = ePerson . getChild ( "name" , getAtomNamespace ( ) ) ; if ( e != null ) { person . setName ( e . getText ( ) ) ; } e = ePerson . getChild ( "uri" , getAtomNamespace ( ) ) ; if ( e != null ) { person . setUri ( resolveURI ( baseURI , ePerson , e . getText ( ) ) ) ; } e = ePerson . getChild ( "email" , getAtomNamespace ( ) ) ; if ( e != null ) { person . setEmail ( e . getText ( ) ) ; } return person ; } private List < Person > parsePersons ( String baseURI , List < Element > ePersons ) { List < Person > persons = new ArrayList < Person > ( ) ; for ( Element ePerson : ePersons ) persons . add ( parsePerson ( baseURI , ePerson ) ) ; return ( persons . size ( ) > 0 ) ? persons : null ; } private Content parseContent ( Element e ) { String value = parseTextConstructToString ( e ) ; String src = e . getAttributeValue ( "src" ) ; String type = e . getAttributeValue ( "type" ) ; Content content = new Content ( ) ; content . setSrc ( src ) ; content . setType ( type ) ; content . setValue ( value ) ; return content ; } private String parseTextConstructToString ( Element e ) { String value ; String type = e . getAttributeValue ( "type" ) ; type = ( type != null ) ? type : Content . TEXT ; if ( type . equals ( Content . XHTML ) ) { XMLOutputter outputter = new XMLOutputter ( ) ; List < org . jdom . Content > eContent = ( List < org . jdom . Content > ) e . getContent ( ) ; for ( org . jdom . Content c : eContent ) { if ( c instanceof Element ) { Element eC = ( Element ) c ; if ( eC . getNamespace ( ) . equals ( getAtomNamespace ( ) ) ) { ( ( Element ) c ) . setNamespace ( Namespace . NO_NAMESPACE ) ; } } } value = outputter . outputString ( eContent ) ; } else { value = e . getText ( ) ; } return value ; } protected List parseEntries ( Feed feed , String baseURI , List eEntries ) { List < Entry > entries = new ArrayList < Entry > ( ) ; for ( Element eEntry : ( List < Element > ) eEntries ) { entries . add ( parseEntry ( feed , eEntry , baseURI ) ) ; } return ( entries . size ( ) > 0 ) ? entries : null ; } protected Entry parseEntry ( Feed feed , Element eEntry , String baseURI ) { Entry entry = new Entry ( ) ; String xmlBase = eEntry . getAttributeValue ( "base" , Namespace . XML_NAMESPACE ) ; if ( xmlBase != null ) { entry . setXmlBase ( xmlBase ) ; } Element e = eEntry . getChild ( "title" , getAtomNamespace ( ) ) ; if ( e != null ) { Content c = new Content ( ) ; c . setValue ( parseTextConstructToString ( e ) ) ; c . setType ( e . getAttributeValue ( "type" ) ) ; entry . setTitleEx ( c ) ; } List < Element > eList = getChildren ( eEntry , "link" ) ; entry . setAlternateLinks ( parseAlternateLinks ( feed , entry , baseURI , eList ) ) ; entry . setOtherLinks ( parseOtherLinks ( feed , entry , baseURI , eList ) ) ; eList = getChildren ( eEntry , "author" ) ; if ( eList . size ( ) > 0 ) { entry . setAuthors ( parsePersons ( baseURI , eList ) ) ; } eList = getChildren ( eEntry , "contributor" ) ; if ( eList . size ( ) > 0 ) { entry . setContributors ( parsePersons ( baseURI , eList ) ) ; } e = eEntry . getChild ( "id" , getAtomNamespace ( ) ) ; if ( e != null ) { entry . setId ( e . getText ( ) ) ; } e = eEntry . getChild ( "updated" , getAtomNamespace ( ) ) ; if ( e != null ) { entry . setUpdated ( DateParser . parseW3CDateTime ( e . getText ( ) ) ) ; } e = eEntry . getChild ( "published" , getAtomNamespace ( ) ) ; if ( e != null ) { entry . setPublished ( DateParser . parseW3CDateTime ( e . getText ( ) ) ) ; } e = eEntry . getChild ( "summary" , getAtomNamespace ( ) ) ; if ( e != null ) { entry . setSummary ( parseContent ( e ) ) ; } e = eEntry . getChild ( "content" , getAtomNamespace ( ) ) ; if ( e != null ) { List < Content > contents = new ArrayList < Content > ( ) ; contents . add ( parseContent ( e ) ) ; entry . setContents ( contents ) ; } e = eEntry . getChild ( "rights" , getAtomNamespace ( ) ) ; if ( e != null ) { entry . setRights ( e . getText ( ) ) ; } List < Element > cList = getChildren ( eEntry , "category" ) ; entry . setCategories ( parseCategories ( baseURI , cList ) ) ; entry . setModules ( parseItemModules ( eEntry ) ) ; List foreignMarkup = extractForeignMarkup ( eEntry , entry , getAtomNamespace ( ) ) ; if ( foreignMarkup . size ( ) > 0 ) { entry . setForeignMarkup ( foreignMarkup ) ; } return entry ; } private List < Category > parseCategories ( String baseURI , List < Element > eCategories ) { List < Category > cats = new ArrayList < Category > ( ) ; for ( Element eCategory : eCategories ) cats . add ( parseCategory ( baseURI , eCategory ) ) ; return ( cats . size ( ) > 0 ) ? cats : null ; } private Category parseCategory ( String baseURI , Element eCategory ) { Category category = new Category ( ) ; String att = eCategory . getAttributeValue ( "term" ) ; if ( att != null ) { category . setTerm ( att ) ; } att = eCategory . getAttributeValue ( "scheme" ) ; if ( att != null ) { category . setScheme ( resolveURI ( baseURI , eCategory , att ) ) ; } att = eCategory . getAttributeValue ( "label" ) ; if ( att != null ) { category . setLabel ( att ) ; } return category ; } static Pattern absoluteURIPattern = Pattern . compile ( "^[a-z0-9]*:.*$" ) ; private boolean isAbsoluteURI ( String uri ) { return absoluteURIPattern . matcher ( uri ) . find ( ) ; } private boolean isRelativeURI ( String uri ) { return ! isAbsoluteURI ( uri ) ; } private String resolveURI ( String baseURI , Parent parent , String url ) { if ( isRelativeURI ( url ) ) { url = ( ! "." . equals ( url ) && ! "./" . equals ( url ) ) ? url : "" ; if ( parent != null && parent instanceof Element ) { String xmlbase = ( ( Element ) parent ) . getAttributeValue ( "base" , Namespace . XML_NAMESPACE ) ; if ( xmlbase != null && xmlbase . trim ( ) . length ( ) > 0 ) { if ( isAbsoluteURI ( xmlbase ) ) { if ( url . startsWith ( "/" ) ) { int slashslash = xmlbase . indexOf ( "//" ) ; int nextslash = xmlbase . indexOf ( "/" , slashslash + 2 ) ; if ( nextslash != - 1 ) xmlbase = xmlbase . substring ( 0 , nextslash ) ; return formURI ( xmlbase , url ) ; } if ( ! xmlbase . endsWith ( "/" ) ) { xmlbase = xmlbase . substring ( 0 , xmlbase . lastIndexOf ( "/" ) + 1 ) ; } return formURI ( xmlbase , url ) ; } else { return resolveURI ( baseURI , parent . getParent ( ) , stripTrailingSlash ( xmlbase ) + "/" + stripStartingSlash ( url ) ) ; } } return resolveURI ( baseURI , parent . getParent ( ) , url ) ; } else if ( parent == null || parent instanceof Document ) { return formURI ( baseURI , url ) ; } } return url ; } private String findBaseURI ( Element root ) throws MalformedURLException { String ret = findAtomLink ( root , "alternate" ) ; if ( ret != null && isRelativeURI ( ret ) ) { String self = findAtomLink ( root , "self" ) ; if ( self != null ) { self = resolveURI ( null , root , self ) ; ret = resolveURI ( self , root , ret ) ; } } return ret ; } private String findAtomLink ( Element parent , String rel ) { String ret = null ; List < Element > linksList = ( List < Element > ) parent . getChildren ( "link" , ns ) ; if ( linksList != null ) { for ( Element link : linksList ) { Attribute relAtt = link . getAttribute ( "rel" ) ; Attribute hrefAtt = link . getAttribute ( "href" ) ; if ( ( relAtt == null && "alternate" . equals ( rel ) ) || ( relAtt != null && relAtt . getValue ( ) . equals ( rel ) ) ) { ret = hrefAtt . getValue ( ) ; break ; } } } return ret ; } private static String stripStartingSlash ( String s ) { if ( s != null && s . startsWith ( "/" ) ) { s = s . substring ( 1 , s . length ( ) ) ; } return s ; } private static String stripTrailingSlash ( String s ) { if ( s != null && s . endsWith ( "/" ) ) { s = s . substring ( 0 , s . length ( ) - 1 ) ; } return s ; } static String formURI ( String base , String child ) { if ( base == null ) return child ; if ( child == null ) return base ; try { return new URI ( base ) . resolve ( child ) . toString ( ) ; } catch ( URISyntaxException e ) { return null ; } } } 