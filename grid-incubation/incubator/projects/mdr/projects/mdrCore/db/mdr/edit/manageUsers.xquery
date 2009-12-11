


xquery version "1.0";

(: ~
 : Module Name:             edit context webpage and XQuery
 :
 : Module Version           2.0
 :
 : Date                     31st July 2009
 :
 : Copyright                The cagrid consortium
 :
 : Module overview          edit context information
 :
 :)
 
(:~
 :    @author Rakesh Dhaval
      @author Puneet Mathur
 :    @version 0.2
 :
 :    User Management Page
~ :)

  import module namespace 
  lib-forms="http://www.cagrid.org/xquery/library/forms"
  at "../edit/m-lib-forms.xquery";
  
  import module namespace 
  lib-util="http://www.cagrid.org/xquery/library/util" 
  at "../library/m-lib-util.xquery";
  
  import module namespace 
  lib-rendering="http://www.cagrid.org/xquery/library/rendering"
  at "../web/m-lib-rendering.xquery";   
  
  import module namespace 
  lib-make-admin-item="http://www.cagrid.org/xquery/library/make-admin-item" 
  at "../edit/m-lib-make-admin-item.xquery";     

declare namespace openMDR = "http://www.cagrid.org/schema/openMDR";
declare namespace ISO11179= "http://www.cagrid.org/schema/ISO11179";  
declare namespace session="http://exist-db.org/xquery/session";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace response="http://exist-db.org/xquery/response"; 
declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace util="http://exist-db.org/xquery/util";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";
   
declare function local:success-page() 
{
   let $calling-page := request:get-parameter("calling-page","")
   return
   
      	(:
      	
      	 <div xmlns="http://www.w3.org/1999/xhtml" >
               <tr>
              		  <td> { local: validateUser() }
               	 </td>
               </tr>
      	 
  
              
               <tr>
              		  <td> { local:process() }
               	 </td>
              </tr>
             
      	</div>   
        :)
        
        if(local: validateUser()=true()) then
        (
        	local:process()
        )
        else(
        
        <div xmlns="http://www.w3.org/1999/xhtml">
       <table>
          <tr>
            <td><b>Access Denied !! Only users of "dba" group have access</b>
            </td>
          </tr>
          <tr/><tr/><tr/><tr/>
          <tr>
            <td><a href='maintenance.xquery'>Return to maintenance menu</a>
            </td>
          </tr>
         </table>
       </div>

        )

};

declare function local:new-user() as element()*
{
    let $name := request:get-parameter("name", ()),
    $grp := request:get-parameter("groups", ()),
    $groups := $grp,
 
    $pass1 := request:get-parameter("pass1", ""),
    $pass2 := request:get-parameter("pass2", "")
    return
    
    if($pass1 != $pass2) then
        (
               local:correct-user(-1, $name, $grp)
        )
        else(
            (:  xmldb:create-user($name, $pass1, $grp, $home) 
            xmldb:create-user("cde", "newtest", "qwert", "") 
	local:testform(),
	local:display():)
	
	 <div xmlns="http://www.w3.org/1999/xhtml" >
               <tr>
              		  <td> { xmldb:create-user($name, $pass1, $grp, "") }
               	 </td>
               </tr>
      	 
  
              
               <tr>
              		  <td>{ local:display() }
               	 </td>
              </tr>
      	</div>
	
          )  
           (:
        local:testform(),
        
         <div xmlns="http://www.w3.org/1999/xhtml">
        <table class="layout">  
          <tr>
            <b> Account Created</b>
            
          </tr>
          <tr/><tr/>
          <tr>
            <a href='manageUsers.xquery'>Return to User Management</a>
            
          </tr>
         </table>
         </div>	:)
         
 	
        
};


declare function local:display() 
{
          
      <div>
      <form  method="get" action="{session:encode-url(request:get-uri())}">
        <table cellpadding="5" id="browse">
            <tr>
                <th/>
                <th>Name</th>
                <th>Groups</th>
            </tr>
            {
                for $user in doc("/db/system/users.xml")//users/user
                    let $name := xs:string($user/@name),
                    $groups := string-join($user/group, ", ")
                order by $name return
                    <tr>
                        <td><input type="radio" name="uid" value="{$user/@uid}"/></td>
                        <td>{$name}</td>
                        <td>{$groups}</td>
                    </tr>
            }
        </table>
        
       <table class="actions">
            <tr>
                <td>
                    <input type="submit" name="action" value="Edit"/>
                    <input type="submit" name="action" value="New User"/>
                    <input type="submit" name="action" value="Remove"/>
                </td>
            </tr>
        </table>
        {
                let $action := request:get-parameter("action", ""),
                $uid := request:get-parameter("uid", "") return
	 if($action eq "New User") then
                (
                       local:edit-user(-1, "", "")
                       
                 )else
                 (
                  	(:        local:testform()  :)
                 )
        }
         <br/><a href='maintenance.xquery'>Return to maintenance menu</a>
     </form>
     </div>
} ;

declare function local:correct-user($uid as xs:integer, $name as xs:string, $groups as xs:string)  as element()
{
    <div xmlns="http://www.w3.org/1999/xhtml" >
    <form action="{session:encode-url(request:get-uri())}" method="get">
        {local:edit-user($uid, $name, $groups)}
        <input type="hidden" name="panel" value="users"/>
    </form>
    </div>
};

declare function local:edit-user($uid as xs:integer, $name as xs:string, $groups as xs:string) as element()
{
      <div xmlns="http://www.w3.org/1999/xhtml">
    <table border="0" cellspacing="5">
        <tr>
            <td>User:</td>
            {
                if($name eq "") then
                (
                    <td colspan="2"><input type="text" name="name"/></td>
                )
                else
                (
                    <td colspan="2"><input type="hidden" name="name" value="{$name}"/>{$name}</td>
                )
            }
        </tr>
        <tr>
            <td>Groups:</td>
            <td><input type="text" name="groups" value="{$groups}"/></td>
            <td class="help">A comma-separated list of groups.
            Note: non-existing groups will be created automatically.</td>
        </tr>
        <tr>
            <td>Password:</td>
            <td colspan="2"><input type="password" name="pass1"/></td>
        </tr>
        <tr>
            <td>Repeat:</td>
            <td colspan="2"><input type="password" name="pass2"/></td>
        </tr>

        <tr>
        {
            if($uid lt 0) then
            (
                <td colspan="3"><input type="submit" name="action" value="Create"/></td>
            )
            else()
        }
        </tr>
        <input type="hidden" name="uid" value="{$uid}"/>
    </table>
    </div>
 };

declare function local:process()  as element()*
{
    let $action := request:get-parameter("action", "") ,
    $session_user  := session:get-attribute("username"),
    $current_user := xmldb:get-current-user()
    return
        if($action eq "Create") then
        (
            local:new-user()
        )
        (:
        else
        (
        	request:set-current-user($session_user,$current_user) ,  
        	
      	local:display()
      	<div xmlns="http://www.w3.org/1999/xhtml" >
               <tr>
              		  <td> Current User : { xmldb:get-current-user() }
               	 </td>
               </tr>
      	 
  
              
               <tr>
              		  <td> Session User : {$session_user }
               	 </td>
              </tr>
              
               <tr>
              		  <td>{xmldb:get-permissions('/db/system')}
               	 </td>
              </tr>
              
               <tr>
              		  <td>{xmldb:permissions-to-string(511)}
               	 </td>
              </tr>
              
              
      	</div>
      	
        )   
        else
        (
              	<div xmlns="http://www.w3.org/1999/xhtml" >
               <tr>
              		  <td> Current User : { xmldb:get-current-user() }
               	 </td>
               </tr>
      	 
  
              
               <tr>
              		  <td>{xmldb:get-permissions('/db/system')}
               	 </td>
              </tr>
              
               <tr>
              		  <td>{xmldb:permissions-to-string(504)}
               	 </td>
              </tr>
    
              
               <tr>
              		  <td>{ xmldb:set-collection-permissions('/db/system', 'rty','"dba', 511) }
               	 </td>
              </tr>
                           
              
      	</div>
        )
:)
        
        else(
              	<div xmlns="http://www.w3.org/1999/xhtml" >

      	 
  
                <tr>
              		  <td>  { local:display() }
               	 </td>
               </tr>
             
              
      	</div>
        )
        
};

declare function local:testform() 
{

        <div xmlns="http://www.w3.org/1999/xhtml" >
           <form name="login" method="post" class="cagridForm" action="{session:encode-url(request:get-uri())}">
               <table class="login" cellpadding="5">
                   <tr>
                       <th colspan="2" align="left">Please Login</th>
                   </tr>
                   <tr>
                       <td align="left">Username:</td>
                       <td><input name="user" type="text" size="20"/></td>
                   </tr>
                   <tr>
                       <td align="left">Password:</td>
                       <td><input name="pass" type="password" size="20"/></td>
                   </tr>
                   <tr>
                       <td colspan="2" align="left"><input type="submit" value="login"/></td>
                   </tr>
               </table>
           </form>
        </div>
};

declare function local:validateUser() as xs:boolean
{
   let $hostname :=  request:get-hostname() 
   
   let $resource_location := concat("xmldb:exist://", $hostname, ":8080/exist/xmlrpc/db")
   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   return
           if(xmldb:is-admin-user($user)) then
           (
            	if (xmldb:login($resource_location, $user, $password))
              then (
		true()
	)
	else(
		false()
	)
          )
          else(
	          false()
          )
          

};

declare option exist:serialize "media-type=text/html method=xhtml doctype-public=-//W3C//DTD&#160;XHTML&#160;1.0&#160;Transitional//EN doctype-system=http://www.w3.org/TR/2002/REC-xhtml1-20020801/DTD/xhtml1-transitional.dtd";
 
   session:create(),
   let $id := request:get-parameter('id','')
   let $title as xs:string := concat(" User Management", $id)
   

   return 
      lib-rendering:txfrm-webpage(
      $title,
            local:success-page() 
         )
       


