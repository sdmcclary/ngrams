final class HsqlArticlesPM { private static final Logger LOG = Logger . getLogger ( HsqlArticlesPM . class . getName ( ) ) ; private final HsqlPersistenceManager context ; private static final String MSG_UNSUPPORTED_TYPE = Strings . error ( "db.unsupported.article.type" ) ; private static final String MSG_NOT_IN_DB = Strings . error ( "db.article.is.not.in.database" ) ; private static final String MSG_SHOULD_BE_SPECIFIED = Strings . error ( "unspecified.article" ) ; private static final String MSG_ALREADY_IN_DB = Strings . error ( "db.article.is.already.in.database" ) ; private static final String MSG_NO_FEED = Strings . error ( "db.article.is.not.assigned.to.any.feed" ) ; private static final String MSG_TRANSIENT_FEED = Strings . error ( "db.article.is.assigned.to.transient.feed" ) ; public HsqlArticlesPM ( HsqlPersistenceManager aContext ) { context = aContext ; } public void insertArticle ( IArticle article ) throws SQLException { if ( article == null ) throw new NullPointerException ( MSG_SHOULD_BE_SPECIFIED ) ; if ( article . getID ( ) != - 1L ) throw new IllegalStateException ( MSG_ALREADY_IN_DB ) ; IFeed feed = article . getFeed ( ) ; if ( feed == null ) throw new IllegalStateException ( MSG_NO_FEED ) ; if ( feed . getID ( ) == - 1L ) throw new IllegalStateException ( MSG_TRANSIENT_FEED ) ; if ( ! ( article instanceof StandardArticle ) ) throw new IllegalArgumentException ( MSG_UNSUPPORTED_TYPE ) ; StandardArticle standardArticle = ( StandardArticle ) article ; PreparedStatement stmt = context . getPreparedStatement ( "INSERT INTO ARTICLES (AUTHOR, TEXT, PLAINTEXT, SIMPLEMATCHKEY, PUBLICATIONDATE, TITLE, " + "SUBJECT, READ, PINNED, LINK, FEEDID) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" ) ; try { stmt . setString ( 1 , standardArticle . getAuthor ( ) ) ; stmt . setString ( 2 , standardArticle . getText ( ) ) ; stmt . setString ( 3 , standardArticle . getPlainText ( ) ) ; stmt . setString ( 4 , standardArticle . getSimpleMatchKey ( ) ) ; Date publicationDate = standardArticle . getPublicationDate ( ) ; stmt . setLong ( 5 , publicationDate == null ? - 1L : publicationDate . getTime ( ) ) ; stmt . setString ( 6 , standardArticle . getTitle ( ) ) ; stmt . setString ( 7 , standardArticle . getSubject ( ) ) ; stmt . setBoolean ( 8 , standardArticle . isRead ( ) ) ; stmt . setBoolean ( 9 , standardArticle . isPinned ( ) ) ; URL link = standardArticle . getLink ( ) ; stmt . setString ( 10 , link == null ? null : link . toString ( ) ) ; stmt . setLong ( 11 , feed . getID ( ) ) ; stmt . executeUpdate ( ) ; long id = context . getInsertedID ( ) ; article . setID ( id ) ; stmt = context . getPreparedStatement ( "INSERT INTO ARTICLE_PROPERTIES (ARTICLEID, POSITIVE_SENTIMENTS, NEGATIVE_SENTIMENTS) " + "VALUES (?, ?, ?)" ) ; stmt . setLong ( 1 , id ) ; stmt . setInt ( 2 , article . getPositiveSentimentsCount ( ) ) ; stmt . setInt ( 3 , article . getNegativeSentimentsCount ( ) ) ; stmt . executeUpdate ( ) ; } finally { stmt . close ( ) ; } } public void removeArticle ( IArticle article ) throws SQLException { if ( article == null ) throw new NullPointerException ( MSG_SHOULD_BE_SPECIFIED ) ; if ( article . getID ( ) == - 1L ) { LOG . log ( Level . SEVERE , MessageFormat . format ( Strings . error ( "0.title.1" ) , MSG_NOT_IN_DB , article . getTitle ( ) ) , new Exception ( "Dump" ) ) ; return ; } PreparedStatement stmt = context . getPreparedStatement ( "DELETE FROM ARTICLES WHERE ID=?" ) ; try { stmt . setLong ( 1 , article . getID ( ) ) ; int rows = stmt . executeUpdate ( ) ; if ( rows == 0 ) { IFeed feed = article . getFeed ( ) ; IGuide guide = null ; if ( feed != null ) { IGuide [ ] guides = feed . getParentGuides ( ) ; guide = guides . length == 0 ? null : guides [ 0 ] ; } String feedId = feed == null ? "no feed" : Long . toString ( feed . getID ( ) ) ; String guideId = guide == null ? "no guide" : Long . toString ( guide . getID ( ) ) ; throw new SQLException ( MessageFormat . format ( Strings . error ( "db.hsql.removed.0.rows.for.articleid.0.feedid.1.guideid.2" ) , article . getID ( ) , feedId , guideId ) ) ; } article . setID ( - 1L ) ; } finally { stmt . close ( ) ; } } public void updateArticle ( IArticle article ) throws SQLException { if ( ! checkArticle ( article ) ) return ; StandardArticle standardArticle = ( StandardArticle ) article ; PreparedStatement stmt = context . getPreparedStatement ( "UPDATE ARTICLES SET " + "AUTHOR=?, SIMPLEMATCHKEY=?, PUBLICATIONDATE=?, TITLE=?, SUBJECT=?, READ=?," + "PINNED=?, LINK=? WHERE ID=?" ) ; try { stmt . setString ( 1 , standardArticle . getAuthor ( ) ) ; stmt . setString ( 2 , standardArticle . getSimpleMatchKey ( ) ) ; Date publicationDate = standardArticle . getPublicationDate ( ) ; stmt . setLong ( 3 , publicationDate == null ? - 1L : publicationDate . getTime ( ) ) ; stmt . setString ( 4 , standardArticle . getTitle ( ) ) ; stmt . setString ( 5 , standardArticle . getSubject ( ) ) ; stmt . setBoolean ( 6 , standardArticle . isRead ( ) ) ; stmt . setBoolean ( 7 , standardArticle . isPinned ( ) ) ; URL link = standardArticle . getLink ( ) ; stmt . setString ( 8 , link == null ? null : link . toString ( ) ) ; stmt . setLong ( 9 , article . getID ( ) ) ; int rows = stmt . executeUpdate ( ) ; if ( rows == 0 ) { IFeed feed = article . getFeed ( ) ; IGuide [ ] guides = feed . getParentGuides ( ) ; IGuide guide = guides . length == 0 ? null : guides [ 0 ] ; String feedId = feed == null ? "no feed" : Long . toString ( feed . getID ( ) ) ; String guideId = guide == null ? "no guide" : Long . toString ( guide . getID ( ) ) ; LOG . log ( Level . SEVERE , MessageFormat . format ( Strings . error ( "db.hsql.updated.0.rows.for.articleid.0.feedid.1.guideid.2" ) , article . getID ( ) , feedId , guideId ) ) ; } } finally { stmt . close ( ) ; } } public void updateArticleProperties ( IArticle article ) throws SQLException { if ( ! checkArticle ( article ) ) return ; PreparedStatement stmt = context . getPreparedStatement ( "UPDATE ARTICLE_PROPERTIES SET " + "POSITIVE_SENTIMENTS = ?, NEGATIVE_SENTIMENTS = ? " + "WHERE ARTICLEID = ?" ) ; try { stmt . setInt ( 1 , article . getPositiveSentimentsCount ( ) ) ; stmt . setInt ( 2 , article . getNegativeSentimentsCount ( ) ) ; stmt . setLong ( 3 , article . getID ( ) ) ; stmt . executeUpdate ( ) ; } finally { stmt . close ( ) ; } } private static boolean checkArticle ( IArticle article ) { if ( article == null ) throw new NullPointerException ( MSG_SHOULD_BE_SPECIFIED ) ; if ( ! ( article instanceof StandardArticle ) ) throw new IllegalArgumentException ( MSG_UNSUPPORTED_TYPE ) ; if ( article . getID ( ) == - 1L ) { LOG . log ( Level . SEVERE , MessageFormat . format ( Strings . error ( "0.title.1" ) , MSG_NOT_IN_DB , article . getTitle ( ) ) , new Exception ( "Dump" ) ) ; return false ; } return true ; } } 