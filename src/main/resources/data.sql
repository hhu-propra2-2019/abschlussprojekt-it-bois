insert into event values
(1,1,'orga','{"type":"CreateGroupEvent","groupId":1,"userId":"orga","groupVisibility":"PUBLIC","groupParent":null,"groupType":"SIMPLE","groupUserMaximum":2}', TRUE),
(2,1,'orga','{"type":"AddUserEvent","groupId":1,"userId":"orga","givenname":"orga","familyname":"orga","email":"blorga@orga.org"}', FALSE),
(3,1,'orga','{"type":"UpdateGroupTitleEvent","groupId":1,"userId":"orga","newGroupTitle":"sdsad"}', FALSE),
(4,1,'orga','{"type":"UpdateGroupDescriptionEvent","groupId":1,"userId":"orga","newGroupDescription":"sadsad"}', FALSE),
(5,1,'orga','{"type":"UpdateRoleEvent","groupId":1,"userId":"orga","newRole":"ADMIN"}', FALSE);