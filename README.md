PoC on how to integrate authorizations in elastic searchs.

## Context
In eXo Platform, ACL are attached to wikis, wiki pages and attachments.  

### Principal & Identity
Each search is executed by a Principal (the principal is an authenticated user or group).  
The identity is a string identifying this group or user.

There are 3 reserved identities in eXo Platform:
* **any** : represents any authenticated session.
* **anonim** : represents a principal for non authenticated sessions. (No error, it's really "anonim").
* **system** : represents a principal for system sessions, typically used for administrative purposes. 
System session has full access (all permissions) to all nodes.

### Owner and privileges
eXo Access Control is based on two facets applied to nodes :
* **Privilegeable** : Means that the user or group (also called principal) needs the appropriate privileges to access to this node.
The privileges are defined as (positive) permissions that are granted to users or groups.
* **Ownable** : The node (wiki, wiki page, attachment) has an owner.
The owner has always full access (all permissions) to the node, independent of the privilegeable facet.

### Permission type
The possible values of the Permission type are corresponding to JCR standard actions:
* **read**: The node or its properties can be read.
* **remove**: The node or its properties can be removed.
* **add_node** : Child nodes can be added to this node.
* **set_property** : The node's properties can be modified, added or removed.

**=> In the context of search, only read will be considered and thus indexed in the search engine.**

### ACL Inheritance
In eXo, ACL can be inherited.  
In the search engine, ACL are de-normalized: the ACL for a given node is computed when indexing the document. 

## Important notes
allowedIdentities and owner have to be indexed as "not_analyzed".