public class YamlUtils { public static Yaml buildYaml ( ) { Loader myLoader = new Loader ( ) ; Yaml yaml = new Yaml ( myLoader ) ; myLoader . setResolver ( new Resolver ( ) { @ Override public String resolve ( NodeId kind , String value , boolean implicit ) { String tag = super . resolve ( kind , value , implicit ) ; if ( implicit ) { if ( tag . equals ( "tag:yaml.org,2002:bool" ) || tag . equals ( "tag:yaml.org,2002:float" ) || tag . equals ( "tag:yaml.org,2002:int" ) || tag . equals ( "tag:yaml.org,2002:timestamp" ) || tag . equals ( "tag:yaml.org,2002:value" ) ) { return "tag:yaml.org,2002:str" ; } } return tag ; } } ) ; return yaml ; } } 