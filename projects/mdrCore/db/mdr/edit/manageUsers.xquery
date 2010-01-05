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
        
        if(local:validateUser()=true()) then
        (
           
               <div xmlns="http://www.w3.org/1999/xhtml">
	           <p>This section will allow you to manage the different users wih their respective roles.
                A user can exist in one of thollowing three different roles :
               <br/>1. dba
               <br/>2. user
               <br/>3. guest
               </p>
	           
	           <table cellpadding="5" id="browse">
            <tr>
                
                <th>Action</th>
                <th>dba</th>
                <th>user</th>
                <th>guest</th>
            </tr>
            <tr>
                <td>New User</td>
                <td>Yes</td>
                <td>No</td>
                <td>No</td>
            </tr>            

            <tr>
                <td>Edit User</td>
                <td>Yes</td>
                <td>No</td>
                <td>No</td>
            </tr> 
            
            <tr>
                <td>Remove User</td>
                <td>Yes</td>
                <td>No</td>
                <td>No</td>
            </tr> 
            </table>
         <p><b>Note:</b> "Admin" is the superuser with dba role and has got all the privileges. Any other user with 
         dba role cannot Edit or Remove Admin user.</p>    
         {local:process()}
	     </div>
        )
        else(
        
        <div xmlns="http://www.w3.org/1999/xhtml">
        <table>
          <tr>
            <td><span style="color:red"><b>Error : Access Denied !! Only users of "dba" group have access</b></span>
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


declare function local:create-user() as element()*
{
    
    let $name := request:get-parameter("name", ()),
    $grp := request:get-parameter("groups", ()),
    $groups := if ($grp) then
        (
            if(contains($grp, ",")) then
            (
                tokenize($grp, "\s*,\s*")
            )
            else
            (
                $grp
            )
        )else(),
    $pass1 := request:get-parameter("pass1", ""),
    $pass2 := request:get-parameter("pass2", "")
    return
    
    if($name eq "") then
    (
    <div xmlns="http://www.w3.org/1999/xhtml">
	<span style="color:red"><b>Error : UserName is missing !!!!</b></span>
	<br/><br/> {local:correct-user(-1, $name, $grp)}
	</div>
    )
   (:
   else if($name != "") then
   (
                for $user in doc("/db/system/users.xml")//users/user
                    let $stored_username := xs:string($user/@name)
 	 return(
 	 	if($name eq $stored_username) then
 	 	(
 	 	    <div xmlns="http://www.w3.org/1999/xhtml">
			<span style="color:red"><b>Error : UserName Already Exists !! Chooose a different Username!!!!</b></span>
			<br/><br/> {local:correct-user(-1, "", "")}
			</div>
		)
		else()
	   )
	
   ) :)
    
    else if($grp eq "") then
    (
    	<div xmlns="http://www.w3.org/1999/xhtml">
	<span style="color:red"><b>Error : GroupName is missing !! Please specify atleast one group!!!!!</b></span>
	<br/><br/> {local:correct-user(-1, $name, $grp)}
	</div>
		            
    	
    )
    
    else if($pass1 != $pass2 or $pass1 eq "") then
        (
    	<div xmlns="http://www.w3.org/1999/xhtml">
	<span style="color:red"><b>Error : Either Password is missing or Passwords are not identical !!!!</b></span>
	<br/><br/> {local:correct-user(-1, $name, $grp)}
	</div>
	
        )
        else(
        
            xmldb:create-user($name, $pass1, $groups, ""),
            <div xmlns="http://www.w3.org/1999/xhtml">
            <span style="color:green"><b>Success: User "{$name}" Created !!! </b></span>
            <br/><br/>{local:display()}
            </div>
	
          )  
};

declare function local:display() 
{
     <div>
      <form>
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
         
         {
            if($uid ge 0) then
            (
                <td colspan="3"><input type="checkbox" name="nopass"/> Leave password unchanged.</td>
            )else()
        }

        <tr>
        {
            if($uid lt 0) then
            (
                <td colspan="3">
                <input type="submit" name="action" value="Create"/>
	            <input type="submit" name="action" value="Cancel"/></td>
            )
            else
            (
                <td colspan="3">
                <input type="submit" name="action" value="Change"/>
	 <input type="submit" name="action" value="Cancel"/></td>                
            )
             
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
            local:create-user()
        )
        else if($action eq "New User") then
        (
            local:correct-user(-1, "", "")
        )
       else if($action eq "Change") then
        (
            local:update-user()
        )
        else if($action eq "Edit") then
        (
            let $uid := request:get-parameter("uid", ()) return
            if(empty($uid) or xs:integer($uid) le 0)then
 	        (
		            <div xmlns="http://www.w3.org/1999/xhtml">
		            <br/><span style="color:red"><b>Error : Select a User to Update !!!!</b></span><br/><br/>{local:display()}
		            </div>
	         )
      	     else
	         (  
               		let $user := doc("/db/system/users.xml")//users/user[@uid = $uid] return
        			if( ($user/@name) eq "admin" and xmldb:get-current-user() != "admin" ) then
	      	        (
		            		<div xmlns="http://www.w3.org/1999/xhtml">
		           		    <br/><br/><span style="color:red"><b>Error : You do not have permission to Edit the Admin User !!!!</b></span>
		            		<br/><br/>{local:display()}
		            		</div>
	       	        ) 
	       	        (:else if(($user/@name) eq xmldb:get-current-user() ) then
	       	        (
		            		<div xmlns="http://www.w3.org/1999/xhtml">
		           		    <br/><br/><span style="color:red"><b>Error : You cannot Edit the Current Logged In User !!!!</b></span>
		            		<br/><br/>{local:display()}
		            		</div>	       	       
	       	        ) :)
       	            else(  
       	             	     local:correct-user(xs:integer($uid), $user/@name, string-join($user/group, ", "))
		            )
                           
              )

            )
        else if($action eq "Remove") then
        (
            local:remove-user()
        )  
        else if($action eq "Cancel") then
        (
    	   <div xmlns="http://www.w3.org/1999/xhtml">
	       {local:display()}
	   </div>
            
        )      
        else(
    	   <div xmlns="http://www.w3.org/1999/xhtml">
	       {local:display()}
	       </div>
        )
        
};



declare function local:validateUser() as xs:boolean
{
   let $hostname :=  request:get-hostname() 
   
   let $database-uri := xs:string("xmldb:exist:///db")
   let $user := session:get-attribute("username")
   let $password := session:get-attribute("password")
   let $is-ssl :=  lib-util:checkSSL()
   return
           if(xmldb:is-admin-user($user)) then
           (
            	if (xmldb:login($database-uri, $user, $password))
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

declare function local:update-user() as element()*
{

    let $name := request:get-parameter("name", ()),
    $grp := request:get-parameter("groups", ()),
    $groups := if ($grp) then
        (
            if(contains($grp, ",")) then
            (
                tokenize($grp, "\s*,\s*")
            )
            else
            (
                $grp
            )
        )else(),
    $pass1 := request:get-parameter("pass1", ""),
    $pass2 := request:get-parameter("pass2", ""),
    $nopass := request:get-parameter("nopass", ()),
    $pass := if($nopass) then () else $pass1,
    $uid := request:get-parameter("uid", ()) return
    
        if(not($nopass) and $pass1 ne $pass2)then
        (
            <div xmlns="http://www.w3.org/1999/xhtml">
        	<span style="color:red"><b>Error : Passwords are not identical!!!!</b></span>
        	<br/><br/> {local:correct-user(xs:integer($uid), $name, $grp)}
        	</div>      
        )
        else if(not($nopass) and $pass eq "" )then
        (
            <div xmlns="http://www.w3.org/1999/xhtml">
        	<span style="color:red"><b>Error : Please enter the Password or check the Leave Password Unchanged!!!!</b></span>
        	<br/><br/> {local:correct-user(xs:integer($uid), $name, $grp)}
        	</div>       
        )
        else if(empty($groups)) then
        (
             <div xmlns="http://www.w3.org/1999/xhtml">
        	<span style="color:red"><b>Error : Please specify one group at least!!!!</b></span>
        	<br/><br/> {local:correct-user(xs:integer($uid), $name, $grp)}
        	</div>        
        )
        else
        (
            xmldb:change-user($name, $pass, $groups, ""),
            
            if(xmldb:get-current-user() eq $name) then
            (
                session:set-attribute("password", $pass)
            )else(),
            
            <div xmlns="http://www.w3.org/1999/xhtml">
            <span style="color:green"><b>Success : User "{$name}" Updated !!! </b></span><br/><br/>{local:display()}
            </div>
        )
       
};

declare function local:remove-user() 
{
    if(empty(request:get-parameter("uid", ())) or xs:integer(request:get-parameter("uid", ())) le 0 )then
    (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <span style="color:red"><b>Error : Select a User to Remove !!!!</b></span>
            <br/><br/>{local:display()}
            </div>
    )
   else
   (    
    let $uid := request:get-parameter("uid", ()),
    $name := doc("/db/system/users.xml")//user[@uid = $uid]/@name cast as xs:string return
        
        if($name eq xmldb:get-current-user() or $name eq "admin") then
        (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <span style="color:red"><b>Error : You cannot remove the Admin User or Current User!!!!</b></span>
            <br/><br/>{local:display()}
            </div>
        )
        (:
        else if (xmldb:is-admin-user($name) and xmldb:get-current-user() != "admin") then
        (
            <div xmlns="http://www.w3.org/1999/xhtml">
            <span style="color:red"><b>Error : You do not have the permission to remove the user of "dba" group!!!!</b></span>
            <br/><br/>{local:display()}
            </div>    
        	
      ) :)
      else
      (

      	   <div xmlns="http://www.w3.org/1999/xhtml">
           { xmldb:delete-user($name) }
           <span style="color:green"><b>Success : User "{$name}" Removed !!! </b></span>
           <br/><br/>{local:display()}
           </div>
            
        )
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
       


