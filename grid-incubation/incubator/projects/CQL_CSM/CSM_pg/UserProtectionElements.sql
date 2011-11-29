-- Copyright © 2010 Emory University
-- 
-- Permission is hereby granted, free of charge, to any person obtaining a
-- copy of this software and associated  documentation files (the "Software"),
-- to deal in the Software without restriction, including without limitation 
-- the rights to use, copy, modify, merge, publish, distribute, sublicense, 
-- and/or sell copies of the Software, and to permit persons to whom the
-- Software is furnished to do so, subject to the following conditions: The
-- above copyright notice and this permission notice shall be included in all
-- copies or substantial portions of the Software. 
-- 
-- THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
-- EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
-- MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
-- EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
-- OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
-- ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
-- DEALINGS IN THE SOFTWARE.

CREATE OR REPLACE VIEW CSM_ROLE_PRIV_VW
  AS SELECT role_priv.ROLE_ID, priv.PRIVILEGE_NAME
       FROM CSM_PRIVILEGE priv, CSM_ROLE_PRIVILEGE role_priv
       WHERE role_priv.PRIVILEGE_ID=priv.PRIVILEGE_ID;
       
CREATE OR REPLACE VIEW CSM_PE_PG_VW
  AS SELECT pe.PROTECTION_ELEMENT_ID, pe.OBJECT_ID, pe.ATTRIBUTE, pe.ATTRIBUTE_VALUE, 
            pe.PROTECTION_ELEMENT_TYPE, pe.APPLICATION_ID, pe_pg.PROTECTION_GROUP_ID
       FROM CSM_PROTECTION_ELEMENT pe, CSM_PG_PE pe_pg
       WHERE pe.PROTECTION_ELEMENT_ID=pe_pg.PROTECTION_ELEMENT_ID;

CREATE OR REPLACE VIEW CSM_USER_PROTECTION_ELEMENTS
  AS SELECT pe_pg.APPLICATION_ID, u.LOGIN_NAME, pe_pg.OBJECT_ID, pe_pg.ATTRIBUTE,
            pe_pg.ATTRIBUTE_VALUE, pe_pg.PROTECTION_ELEMENT_TYPE, role_priv.PRIVILEGE_NAME
       FROM CSM_PE_PG_VW pe_pg, CSM_ROLE_PRIV_VW role_priv, CSM_USER_GROUP_ROLE_PG ug_role_pg, CSM_USER u, CSM_USER_GROUP u_ug
       WHERE pe_pg.PROTECTION_GROUP_ID=ug_role_pg.PROTECTION_GROUP_ID
             AND u.USER_ID=u_ug.USER_ID AND u_ug.GROUP_ID=ug_role_pg.GROUP_ID
             AND ug_role_pg.ROLE_ID=role_priv.ROLE_ID
     UNION
     SELECT pe_pg.APPLICATION_ID, u.LOGIN_NAME, pe_pg.OBJECT_ID, pe_pg.ATTRIBUTE,
            pe_pg.ATTRIBUTE_VALUE, pe_pg.PROTECTION_ELEMENT_TYPE, role_priv.PRIVILEGE_NAME
       FROM CSM_PE_PG_VW pe_pg, CSM_ROLE_PRIV_VW role_priv, CSM_USER_GROUP_ROLE_PG ug_role_pg, CSM_USER u
       WHERE pe_pg.PROTECTION_GROUP_ID=ug_role_pg.PROTECTION_GROUP_ID
             AND u.USER_ID=ug_role_pg.USER_ID
             AND ug_role_pg.ROLE_ID=role_priv.ROLE_ID;

COMMENT on VIEW CSM_USER_PROTECTION_ELEMENTS IS
 'Each row of this view represents a user a combination of protection element and privilege that is associated with the user.';
 
CREATE INDEX idx_PROTECTION_ELEMENT_ATRRIBUTE_VALUE ON CSM_PROTECTION_ELEMENT USING btree (ATTRIBUTE_VALUE);

GRANT SELECT ON CSM_USER_PROTECTION_ELEMENTS TO :APP_USER;
GRANT SELECT ON CSM_PE_PG_VW TO :APP_USER;
GRANT SELECT ON CSM_ROLE_PRIV_VW TO :APP_USER;
