<?xml version="1.0" encoding="UTF-8"?>
<md:mspec xmlns:md="http://www.eclipse.org/buckminster/MetaData-1.0" 
    name="org.eclipse.ecf" 
    materializer="p2" 
    url="org.eclipse.ecf.tests.cquery">
    
    <md:mspecNode namePattern="org.eclipse.ecf.*" materializer="workspace"/>
    
    <md:mspecNode namePattern=".*" installLocation="${target.location}"/>
</md:mspec>
	
