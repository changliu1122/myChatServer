package org.mychat.mychat_server.pojo;

public class GroupChatInfo {
    private String id;

    private String groupname;

    private String groupmembers;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getGroupmembers() {
        return groupmembers;
    }

    public void setGroupmembers(String groupmembers) {
        this.groupmembers = groupmembers;
    }
}