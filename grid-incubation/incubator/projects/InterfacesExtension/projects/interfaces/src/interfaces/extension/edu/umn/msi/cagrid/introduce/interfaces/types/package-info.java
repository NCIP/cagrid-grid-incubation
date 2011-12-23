/**
 * This package contains an abstraction meant to encode Introduce's type
 * bindings in an way that can be uniformly accessed by extensions in both
 * the precodegen and postcodegen phases. Introduce provides these bindings 
 * out of the box (via the ServiceInformation data structure) to the extension 
 * in the postcodegen phase only. 
 * 
 * An individual mapping of an XML type to a Java class is defined by a 
 * TypeBean object. Collections of the mappings are described via the 
 * TypeBeanCollectionSupplier interface. There are two implementations of 
 * this interface one for the Precodegen stage and one for the Postcodegen 
 * stage. Programming againist this simple interface allows the extension to
 * access the relevant type information in an uniform way.   
 */
package edu.umn.msi.cagrid.introduce.interfaces.types;
