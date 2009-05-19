declare namespace response="http://exist-db.org/xquery/response";
declare namespace session="http://exist-db.org/xquery/session";
session:invalidate(), 
response:redirect-to(xs:anyURI('homepage.xquery'))