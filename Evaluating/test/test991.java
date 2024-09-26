public class DocumentImpl implements Document , Serializable { private String printRepresentation ; private Type type ; private String id ; private Calendar modTime ; private String reference ; private List < Item > items ; private Currency cur ; private long totalCent ; public DocumentImpl ( ) { super ( ) ; this . items = new ArrayList < Item > ( ) ; } public void addItem ( Item item ) { this . items . add ( item ) ; } public Currency getCur ( ) { return cur ; } public void setCur ( Currency cur ) { this . cur = cur ; } public String getId ( ) { return id ; } public void setId ( String id ) { this . id = id ; } public List < Item > getItems ( ) { return items ; } public void setItems ( List < Item > items ) { this . items = items ; } public String getReference ( ) { return reference ; } public void setReference ( String reference ) { this . reference = reference ; } public long getTotalCent ( ) { return totalCent ; } public void setTotalCent ( long totalCent ) { this . totalCent = totalCent ; } public Type getType ( ) { return type ; } public void setType ( Type type ) { this . type = type ; } public String getPrintRepresentation ( ) { return printRepresentation ; } public void setPrintRepresentation ( String printRepresentation ) { this . printRepresentation = printRepresentation ; } public Calendar getModTime ( ) { return modTime ; } public void setModTime ( Calendar modTime ) { this . modTime = modTime ; } } 