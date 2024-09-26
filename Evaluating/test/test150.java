public class AmazonGateway { private static final Logger LOG = Logger . getLogger ( AmazonGateway . class . getName ( ) ) ; private static final int ITEMS_PER_PAGE = 10 ; private static final int TIME_BETWEEN_CALLS_MS = 10 ; private static final MessageFormat ITEM_SEARCH_FORMAT ; private static final MessageFormat ITEM_URL_FORMAT ; private String subscriptionId ; private String affilateId ; static { ITEM_SEARCH_FORMAT = new MessageFormat ( "http://webservices.amazon.com/onca/xml" + "?Service=AWSECommerceService" + "&SubscriptionId={0}" + "&Operation=ItemSearch" + "&Keywords={1}" + "&SearchIndex={2}" + "&Sort={3}" + "&ItemPage={4}" + "&ResponseGroup=Small,OfferSummary,Images,ItemAttributes" ) ; ITEM_URL_FORMAT = new MessageFormat ( "http://www.amazon.com/exec/obidos/ASIN/{0}/{1}/" ) ; } public AmazonGateway ( String subscriptionId , String affilateId ) { this . subscriptionId = subscriptionId ; this . affilateId = affilateId ; } public synchronized List < AmazonItem > itemsSearch ( String keywords , AmazonSearchIndex searchIndex , String sort , int maxItems ) throws AmazonException { ArrayList < AmazonItem > items = new ArrayList < AmazonItem > ( maxItems ) ; int pages = ( int ) Math . ceil ( maxItems / ( double ) ITEMS_PER_PAGE ) ; for ( int page = 0 ; ( items . size ( ) == page * ITEMS_PER_PAGE ) && page < pages ; page ++ ) { if ( page > 0 ) sleepBetweenCalls ( ) ; items . addAll ( fetchItemsPage ( keywords , searchIndex , sort , page + 1 ) ) ; } return items ; } private List < AmazonItem > fetchItemsPage ( String keywords , AmazonSearchIndex searchIndex , String sort , int page ) throws AmazonException { List < AmazonItem > itemsOnThePage = new ArrayList < AmazonItem > ( ITEMS_PER_PAGE ) ; String restCallURL = ITEM_SEARCH_FORMAT . format ( new Object [ ] { subscriptionId , keywords . replaceAll ( " " , "+" ) , searchIndex . toString ( ) , sort , Integer . toString ( page ) } ) ; Document doc = getResponseDocument ( restCallURL ) ; Element rootEl = doc . getRootElement ( ) ; clearNamespace ( rootEl ) ; Element itemsEl = rootEl . getChild ( "Items" ) ; if ( itemsEl != null ) { List itemsElements = itemsEl . getChildren ( "Item" ) ; for ( Object itemsElement : itemsElements ) { Element itemElement = ( Element ) itemsElement ; try { itemsOnThePage . add ( convertElementToItem ( itemElement , searchIndex ) ) ; } catch ( MalformedURLException e ) { LOG . log ( Level . WARNING , Strings . error ( "amazon.failed.to.create.item" ) , e ) ; } } } return itemsOnThePage ; } private Document getResponseDocument ( String requestURL ) throws AmazonException { SAXBuilder builder = new SAXBuilder ( false ) ; Document doc = null ; boolean retry = true ; while ( retry ) { try { doc = builder . build ( new URL ( requestURL ) ) ; retry = false ; } catch ( IOException e ) { String message = e . getMessage ( ) ; if ( message != null && message . indexOf ( "code: 5" ) != - 1 ) { LOG . warning ( MessageFormat . format ( Strings . error ( "amazon.ioexception.calling.amazon.service.retrying" ) , message ) ) ; sleepBetweenCalls ( ) ; retry = true ; } else throw new AmazonException ( e ) ; } catch ( JDOMException e ) { throw new AmazonException ( e ) ; } } return doc ; } private void clearNamespace ( Element element ) { if ( element == null ) return ; element . setNamespace ( null ) ; for ( Object o : element . getChildren ( ) ) clearNamespace ( ( Element ) o ) ; } private AmazonItem convertElementToItem ( Element itemElement , AmazonSearchIndex searchIndex ) throws MalformedURLException { String asin = itemElement . getChildText ( "ASIN" ) ; URL url = new URL ( ITEM_URL_FORMAT . format ( new Object [ ] { asin , affilateId } ) ) ; AmazonItem item = new AmazonItem ( asin , url , searchIndex ) ; addAttributesToItem ( item , itemElement . getChild ( "ItemAttributes" ) ) ; addOfferSummaryToItem ( item , itemElement ) ; addImagesToItem ( item , itemElement ) ; return item ; } private void addAttributesToItem ( AmazonItem item , Element attributesElement ) { if ( attributesElement != null ) { List children = attributesElement . getChildren ( ) ; for ( Object aChildren : children ) { Element attributeElement = ( Element ) aChildren ; item . addAttribute ( attributeElement . getName ( ) , attributeElement . getTextTrim ( ) ) ; } } } private void addOfferSummaryToItem ( AmazonItem item , Element itemElement ) { Element itemAttributesElement = itemElement . getChild ( "ItemAttributes" ) ; if ( itemAttributesElement != null ) { item . setListPrice ( getPrice ( itemAttributesElement . getChild ( "ListPrice" ) ) ) ; } Element offerSummaryElement = itemElement . getChild ( "OfferSummary" ) ; if ( offerSummaryElement != null ) { item . setLowestNewPrice ( getPrice ( offerSummaryElement . getChild ( "LowestNewPrice" ) ) ) ; item . setLowestUsedPrice ( getPrice ( offerSummaryElement . getChild ( "LowestUsedPrice" ) ) ) ; } } private String getPrice ( Element priceElement ) { return priceElement == null ? null : priceElement . getChildText ( "FormattedPrice" ) ; } private void addImagesToItem ( AmazonItem item , Element itemElement ) { item . setSmallImage ( getImage ( itemElement , "SmallImage" ) ) ; item . setMediumImage ( getImage ( itemElement , "MediumImage" ) ) ; item . setLargeImage ( getImage ( itemElement , "LargeImage" ) ) ; } private AmazonImageDetails getImage ( Element itemElement , String imageElementName ) { AmazonImageDetails imageDetails = null ; Element imageElement = itemElement . getChild ( imageElementName ) ; if ( imageElement != null ) { String urlString = imageElement . getChildText ( "URL" ) ; String heightString = imageElement . getChildText ( "Height" ) ; String widthString = imageElement . getChildText ( "Width" ) ; try { URL url = new URL ( urlString ) ; int height = Integer . parseInt ( heightString ) ; int width = Integer . parseInt ( widthString ) ; imageDetails = new AmazonImageDetails ( url , width , height ) ; } catch ( MalformedURLException e ) { LOG . log ( Level . WARNING , MessageFormat . format ( Strings . error ( "invalid.url" ) , urlString ) , e ) ; } catch ( NumberFormatException e ) { LOG . warning ( MessageFormat . format ( Strings . error ( "amazon.invalid.image.dimensions" ) , widthString , heightString ) ) ; } } return imageDetails ; } private static void sleepBetweenCalls ( ) { try { Thread . sleep ( TIME_BETWEEN_CALLS_MS ) ; } catch ( InterruptedException e ) { LOG . warning ( Strings . error ( "interrupted" ) ) ; } } } 