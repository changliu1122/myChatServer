package org.mychat.mychat_server.netty;

import java.util.List;

public class GroupChat {

    private String groupId;
    private String groupName;
    private List<String> groupMembers;

    private String newName;

    private int nameChanged; // 0 no change, 1 changed

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }


    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public int getNameChanged() {
        return nameChanged;
    }

    public void setNameChanged(int nameChanged) {
        this.nameChanged = nameChanged;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<String> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
