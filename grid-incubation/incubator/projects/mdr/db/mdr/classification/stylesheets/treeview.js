
/***************************************************************
*  Copyright notice
*
*  (c) 2003-2004 Jean-Michel Garnier (garnierjm@yahoo.fr)
*  All rights reserved
*
*  This script is part of the phpXplorer project. The phpXplorer 
project is
*  free software; you can redistribute it and/or modify
*  it under the terms of the GNU General Public License as published by
*  the Free Software Foundation; either version 2 of the License, or
*  (at your option) any later version.
*
*  The GNU General Public License can be found at
*  http://www.gnu.org/copyleft/gpl.html.
*  A copy is found in the textfile GPL.txt distributed with these 
scripts.
*
*  This script is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU General Public License for more details.
*
*  This copyright notice MUST APPEAR in all copies of the script!
***************************************************************/

// ---------------------------------------------------------------------------
// --- Name:    Easy DHTML Treeview                                         --
// --- Original idea by : D.D. de Kerf                  --
// --- Updated by Jean-Michel Garnier, garnierjm@yahoo.fr                   --
// ---------------------------------------------------------------------------

                    
                    
/*****************************************************************************

Name : toggle
Parameters :  node , DOM element (<a> tag)
Description :     Description, collapse or unfold a branch
Author : Jean-Michel Garnier /  D.D. de Kerf

*****************************************************************************/

function toggle(node) {

    // Get the next tag (read the HTML source)   
	var nextDIV = node.nextSibling;
	
	// find the next DIV

   while(nextDIV.nodeName.toLowerCase() != "div") {
   	nextDIV = nextDIV.nextSibling;
   	}

	// Unfold the branch if it isn't visible
	if (nextDIV.style.display == 'none') {
		nextDIV.style.display = 'block';
		// Change the image (if there is an image)
		if (node.childNodes.length > 0) {
			if (node.childNodes.item(0).nodeName == "IMG") {
				node.childNodes.item(0).src = getImgDirectory(node.childNodes.item(0).src) + "minus.gif";
			}
		};


	}

	// Collapse the branch if it IS visible

	else {
		// Change the image (if there is an image)
		nextDIV.style.display = 'none';
		if (node.childNodes.length > 0) {
			if (node.childNodes.item(0).nodeName == "IMG") {
  				node.childNodes.item(0).src = getImgDirectory(node.childNodes.item(0).src) + "plus.gif";
			}
		};

	}
}

/*****************************************************************************

Name : openTree
Parameters :  myNode , DOM element
Description :     Recurses up the tree until it finds the root node and
sets the style elements of any div nodes visited to be 'nblock' on the
way back down

Author : Steve Harris

*****************************************************************************/

function openTree(myNode) {
    if(myNode.parentNode.id!='treeroot')
        {
        openTree(myNode.parentNode);
        };

        if (myNode.tagName=='DIV' && myNode.style.display=='none') {

		// Change the image (if there is an image)
		if (myNode.previousSibling.childNodes.length > 0) {
			if (myNode.previousSibling.childNodes.item(0).nodeName == "IMG") {
				myNode.previousSibling.childNodes.item(0).src = getImgDirectory(myNode.previousSibling.childNodes.item(0).src) + "minus.gif";
			};
		};

		myNode.style.display = 'block';
	};
       }
    

/*****************************************************************************

Name : findElementInTreeview
Parameters :  adminItemID, string
Description :     receives the admin item id of the node you wish to find

Author : Steve Harris

*****************************************************************************/

function findElementInTreeview(ID)
{
    var myNode=document.getElementById(ID);
    openTree(myNode);
}



/*****************************************************************************

Name : toggle2
Parameters :  node DOM element (<a> tag), folderCode String
Description :    if you use the "code" attribute in a folder element, toggle2 is called
instead of toggle. The consequence is that you MUST implement a selectFolder function in your page.
Author : Jean-Michel Garnier

*****************************************************************************/

function toggle2(node, folderCode) {
    toggle(node);
    selectFolder(folderCode);
}



/*****************************************************************************

Name : getImgDirectory
Parameters : Image source path
Return : Image source Directory
Author : Jean-Michel Garnier

*****************************************************************************/
function getImgDirectory(source) {
    return source.substring(0, source.lastIndexOf('/') + 1);
}

/*****************************************************************************

Name : selectLeaf
Parameters : text, id
Return : opens document with administered item id
Author : Steve

*****************************************************************************/
function selectLeaf(code) {
    myRef = window.open('../web/classification.xquery?classifier='+code,'_parent');
}
