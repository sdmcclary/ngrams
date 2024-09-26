public class FormDataBroker extends ChartBroker { public static final int PLOT_ALL_MESSAGES_FOR_FORM = 0 ; public static final int PLOT_NUMERIC_FIELD_VALUE = 1 ; public static final int PLOT_NUMERIC_FIELD_ADDITIVE = 2 ; public static final int PLOT_WORD_HISTOGRAM = 3 ; public static final int PLOT_NUMERIC_FIELD_COUNT_HISTOGRAM = 4 ; private Form mForm ; private Field fieldToPlot ; public FormDataBroker ( Activity parentActivity , WebView appView , Form form , Date startDate , Date endDate ) { super ( parentActivity , appView , startDate , endDate ) ; mForm = form ; mVariableStrings = new String [ mForm . getFields ( ) . length + 1 ] ; mVariableStrings [ 0 ] = "Messages over time" ; for ( int i = 1 ; i < mVariableStrings . length ; i ++ ) { Field f = mForm . getFields ( ) [ i - 1 ] ; mVariableStrings [ i ] = f . getName ( ) + "  [" + f . getFieldType ( ) . getParsedDataType ( ) + "]" ; } } @ Override public void doLoadGraph ( ) { JSONGraphData allData = null ; if ( fieldToPlot == null ) { allData = loadMessageOverTimeHistogram ( ) ; } else if ( fieldToPlot . getFieldType ( ) . getParsedDataType ( ) . toLowerCase ( ) . equals ( "word" ) ) { allData = loadHistogramFromField ( ) ; } else if ( fieldToPlot . getFieldType ( ) . getParsedDataType ( ) . toLowerCase ( ) . equals ( "boolean" ) || fieldToPlot . getFieldType ( ) . getParsedDataType ( ) . toLowerCase ( ) . equals ( "yes/no" ) ) { allData = loadBooleanPlot ( ) ; } else { allData = loadNumericLine ( ) ; } if ( allData != null ) { mGraphData = allData . getData ( ) ; mGraphOptions = allData . getOptions ( ) ; } Log . d ( "FormDataBroker" , mGraphData . toString ( ) ) ; Log . d ( "FormDataBroker" , mGraphOptions . toString ( ) ) ; } private JSONGraphData loadBooleanPlot ( ) { Date startDateToUse = getStartDate ( ) ; DateDisplayTypes displayType = this . getDisplayType ( startDateToUse , mEndDate ) ; String selectionArg = getSelectionString ( displayType ) ; StringBuilder rawQuery = new StringBuilder ( ) ; String fieldcol = RapidSmsDBConstants . FormData . COLUMN_PREFIX + fieldToPlot . getName ( ) ; rawQuery . append ( "select time, " + fieldcol + ", count(*) from  " ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( " join rapidandroid_message on (" ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( ".message_id = rapidandroid_message._id" ) ; rawQuery . append ( ") " ) ; if ( startDateToUse . compareTo ( Constants . NULLDATE ) != 0 && mEndDate . compareTo ( Constants . NULLDATE ) != 0 ) { rawQuery . append ( " WHERE rapidandroid_message.time > '" + Message . SQLDateFormatter . format ( startDateToUse ) + "' AND rapidandroid_message.time < '" + Message . SQLDateFormatter . format ( mEndDate ) + "' " ) ; } rawQuery . append ( " group by " ) . append ( selectionArg ) . append ( ", " + fieldcol ) ; rawQuery . append ( " order by " ) . append ( "time" ) . append ( " ASC" ) ; SQLiteDatabase db = rawDB . getReadableDatabase ( ) ; Log . d ( "query" , rawQuery . toString ( ) ) ; Cursor cr = db . rawQuery ( rawQuery . toString ( ) , null ) ; int barCount = cr . getCount ( ) ; Date [ ] allDates = new Date [ barCount ] ; if ( barCount == 0 ) { db . close ( ) ; cr . close ( ) ; } else { List < Date > xValsTrue = new ArrayList < Date > ( ) ; List < Integer > yValsTrue = new ArrayList < Integer > ( ) ; List < Date > xValsFalse = new ArrayList < Date > ( ) ; List < Integer > yValsFalse = new ArrayList < Integer > ( ) ; cr . moveToFirst ( ) ; int i = 0 ; do { String trueFalse = cr . getString ( 1 ) ; Date thisDate = getDate ( displayType , cr . getString ( 0 ) ) ; Log . d ( "FormDataBroker: " , cr . getString ( 0 ) + ", " + trueFalse + " , " + cr . getInt ( 2 ) ) ; if ( trueFalse . equals ( "true" ) ) { xValsFalse . add ( thisDate ) ; yValsFalse . add ( new Integer ( cr . getInt ( 2 ) ) ) ; } else { xValsTrue . add ( thisDate ) ; yValsTrue . add ( new Integer ( cr . getInt ( 2 ) ) ) ; } allDates [ i ] = thisDate ; i ++ ; } while ( cr . moveToNext ( ) ) ; try { String legend = this . getLegendString ( displayType ) ; int [ ] yVals = getIntsFromList ( yValsTrue ) ; JSONArray trueArray = getJSONArrayForValues ( displayType , xValsTrue . toArray ( new Date [ 0 ] ) , yVals ) ; yVals = getIntsFromList ( yValsFalse ) ; JSONArray falseArray = getJSONArrayForValues ( displayType , xValsFalse . toArray ( new Date [ 0 ] ) , yVals ) ; JSONArray finalValues = new JSONArray ( ) ; JSONObject trueElem = new JSONObject ( ) ; trueElem . put ( "data" , trueArray ) ; trueElem . put ( "label" , "Yes" ) ; trueElem . put ( "lines" , getShowTrue ( ) ) ; finalValues . put ( trueElem ) ; JSONObject falseElem = new JSONObject ( ) ; falseElem . put ( "data" , falseArray ) ; falseElem . put ( "label" , "No" ) ; falseElem . put ( "lines" , getShowTrue ( ) ) ; finalValues . put ( falseElem ) ; return new JSONGraphData ( finalValues , loadOptionsForDateGraph ( allDates , true , displayType ) ) ; } catch ( Exception ex ) { } finally { if ( ! cr . isClosed ( ) ) { cr . close ( ) ; } if ( db . isOpen ( ) ) { db . close ( ) ; } } } return new JSONGraphData ( getEmptyData ( ) , new JSONObject ( ) ) ; } private int [ ] getIntsFromList ( List < Integer > values ) { int [ ] toReturn = new int [ values . size ( ) ] ; for ( int i = 0 ; i < values . size ( ) ; i ++ ) { toReturn [ i ] = values . get ( i ) ; } return toReturn ; } private JSONGraphData loadNumericLine ( ) { Date startDateToUse = getStartDate ( ) ; SQLiteDatabase db = rawDB . getReadableDatabase ( ) ; String fieldcol = RapidSmsDBConstants . FormData . COLUMN_PREFIX + fieldToPlot . getName ( ) ; StringBuilder rawQuery = new StringBuilder ( ) ; rawQuery . append ( "select rapidandroid_message.time, " + fieldcol ) ; rawQuery . append ( " from " ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( " join rapidandroid_message on (" ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( ".message_id = rapidandroid_message._id" ) ; rawQuery . append ( ") " ) ; if ( startDateToUse . compareTo ( Constants . NULLDATE ) != 0 && mEndDate . compareTo ( Constants . NULLDATE ) != 0 ) { rawQuery . append ( " WHERE rapidandroid_message.time > '" + Message . SQLDateFormatter . format ( startDateToUse ) + "' AND rapidandroid_message.time < '" + Message . SQLDateFormatter . format ( mEndDate ) + "' " ) ; } rawQuery . append ( " order by rapidandroid_message.time ASC" ) ; Cursor cr = db . rawQuery ( rawQuery . toString ( ) , null ) ; int barCount = cr . getCount ( ) ; if ( barCount == 0 ) { cr . close ( ) ; } else { Date [ ] xVals = new Date [ barCount ] ; int [ ] yVals = new int [ barCount ] ; cr . moveToFirst ( ) ; int i = 0 ; do { try { xVals [ i ] = Message . SQLDateFormatter . parse ( cr . getString ( 0 ) ) ; yVals [ i ] = cr . getInt ( 1 ) ; } catch ( Exception ex ) { } i ++ ; } while ( cr . moveToNext ( ) ) ; try { return new JSONGraphData ( prepareDateData ( xVals , yVals ) , loadOptionsForDateGraph ( xVals , false , DateDisplayTypes . Daily ) ) ; } catch ( Exception ex ) { } finally { if ( ! cr . isClosed ( ) ) { cr . close ( ) ; } } } return new JSONGraphData ( getEmptyData ( ) , new JSONObject ( ) ) ; } private JSONArray prepareDateData ( Date [ ] xvals , int [ ] yvals ) { JSONArray outerArray = new JSONArray ( ) ; JSONArray innerArray = new JSONArray ( ) ; int datalen = xvals . length ; for ( int i = 0 ; i < datalen ; i ++ ) { JSONArray elem = new JSONArray ( ) ; elem . put ( xvals [ i ] . getTime ( ) ) ; elem . put ( yvals [ i ] ) ; innerArray . put ( elem ) ; } outerArray . put ( innerArray ) ; return outerArray ; } private JSONGraphData loadMessageOverTimeHistogram ( ) { Date startDateToUse = getStartDate ( ) ; DateDisplayTypes displayType = this . getDisplayType ( startDateToUse , mEndDate ) ; String selectionArg = getSelectionString ( displayType ) ; StringBuilder rawQuery = new StringBuilder ( ) ; rawQuery . append ( "select time, count(*) from  " ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( " join rapidandroid_message on (" ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( ".message_id = rapidandroid_message._id" ) ; rawQuery . append ( ") " ) ; if ( startDateToUse . compareTo ( Constants . NULLDATE ) != 0 && mEndDate . compareTo ( Constants . NULLDATE ) != 0 ) { rawQuery . append ( " WHERE rapidandroid_message.time > '" + Message . SQLDateFormatter . format ( startDateToUse ) + "' AND rapidandroid_message.time < '" + Message . SQLDateFormatter . format ( mEndDate ) + "' " ) ; } rawQuery . append ( " group by " ) . append ( selectionArg ) ; rawQuery . append ( "order by " ) . append ( selectionArg ) . append ( " ASC" ) ; SQLiteDatabase db = rawDB . getReadableDatabase ( ) ; Cursor cr = db . rawQuery ( rawQuery . toString ( ) , null ) ; return getDateQuery ( displayType , cr , db ) ; } private Date getStartDate ( ) { Date firstDateFromForm = ParsedDataReporter . getOldestMessageDate ( rawDB , mForm ) ; if ( firstDateFromForm . after ( mStartDate ) ) { return firstDateFromForm ; } else { return mStartDate ; } } private JSONGraphData loadHistogramFromField ( ) { SQLiteDatabase db = rawDB . getReadableDatabase ( ) ; String fieldcol = RapidSmsDBConstants . FormData . COLUMN_PREFIX + fieldToPlot . getName ( ) ; StringBuilder rawQuery = new StringBuilder ( ) ; rawQuery . append ( "select " + fieldcol ) ; rawQuery . append ( ", count(*) from " ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( " join rapidandroid_message on (" ) ; rawQuery . append ( RapidSmsDBConstants . FormData . TABLE_PREFIX + mForm . getPrefix ( ) ) ; rawQuery . append ( ".message_id = rapidandroid_message._id" ) ; rawQuery . append ( ") " ) ; if ( mStartDate . compareTo ( Constants . NULLDATE ) != 0 && mEndDate . compareTo ( Constants . NULLDATE ) != 0 ) { rawQuery . append ( " WHERE rapidandroid_message.time > '" + Message . SQLDateFormatter . format ( mStartDate ) + "' AND rapidandroid_message.time < '" + Message . SQLDateFormatter . format ( mEndDate ) + "' " ) ; } rawQuery . append ( " group by " + fieldcol ) ; rawQuery . append ( " order by " + fieldcol ) ; Cursor cr = db . rawQuery ( rawQuery . toString ( ) , null ) ; int barCount = cr . getCount ( ) ; if ( barCount != 0 ) { String [ ] xVals = new String [ barCount ] ; int [ ] yVals = new int [ barCount ] ; cr . moveToFirst ( ) ; int i = 0 ; do { xVals [ i ] = cr . getString ( 0 ) ; yVals [ i ] = cr . getInt ( 1 ) ; i ++ ; } while ( cr . moveToNext ( ) ) ; try { return new JSONGraphData ( prepareHistogramData ( xVals , yVals ) , loadOptionsForHistogram ( xVals ) ) ; } catch ( Exception ex ) { } finally { if ( ! cr . isClosed ( ) ) { cr . close ( ) ; } if ( db . isOpen ( ) ) { db . close ( ) ; } } } return new JSONGraphData ( getEmptyData ( ) , new JSONObject ( ) ) ; } private JSONArray prepareHistogramData ( String [ ] names , int [ ] counts ) throws JSONException { JSONArray arr = new JSONArray ( ) ; int datalen = names . length ; for ( int i = 0 ; i < datalen ; i ++ ) { JSONObject elem = new JSONObject ( ) ; JSONArray values = new JSONArray ( ) ; JSONArray value = new JSONArray ( ) ; value . put ( i ) ; value . put ( counts [ i ] ) ; values . put ( value ) ; elem . put ( "data" , values ) ; elem . put ( "bars" , getShowTrue ( ) ) ; elem . put ( "label" , names [ i ] ) ; arr . put ( elem ) ; } return arr ; } @ Override public String getGraphTitle ( ) { return "Form Data" ; } @ Override public void setVariable ( int id ) { if ( id == 0 ) { this . fieldToPlot = null ; } else { this . fieldToPlot = mForm . getFields ( ) [ id - 1 ] ; } mChosenVariable = id ; this . mGraphData = null ; this . mGraphOptions = null ; } @ Override public String getName ( ) { return "graph_form" ; } } 