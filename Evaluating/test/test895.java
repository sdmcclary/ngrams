public interface PersonDao { List < Person > findAll ( ) ; List < Person > findByLastname ( String lastname ) ; void save ( Person person ) ; Person find ( Long id ) ; } 