package com.camel.redpenguin.greendao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DATA_REL_FAMILY.
 */
public class DataRelFamily {

    private Long id;
    private String dataRelSubject;
    private String dataRelBranch;
    private String dataRelNick;
    private String dataRelAccount;
    private String dataRelAdministrator;

    public DataRelFamily() {
    }

    public DataRelFamily(Long id) {
        this.id = id;
    }

    public DataRelFamily(Long id, String dataRelSubject, String dataRelBranch, String dataRelNick, String dataRelAccount, String dataRelAdministrator) {
        this.id = id;
        this.dataRelSubject = dataRelSubject;
        this.dataRelBranch = dataRelBranch;
        this.dataRelNick = dataRelNick;
        this.dataRelAccount = dataRelAccount;
        this.dataRelAdministrator = dataRelAdministrator;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataRelSubject() {
        return dataRelSubject;
    }

    public void setDataRelSubject(String dataRelSubject) {
        this.dataRelSubject = dataRelSubject;
    }

    public String getDataRelBranch() {
        return dataRelBranch;
    }

    public void setDataRelBranch(String dataRelBranch) {
        this.dataRelBranch = dataRelBranch;
    }

    public String getDataRelNick() {
        return dataRelNick;
    }

    public void setDataRelNick(String dataRelNick) {
        this.dataRelNick = dataRelNick;
    }

    public String getDataRelAccount() {
        return dataRelAccount;
    }

    public void setDataRelAccount(String dataRelAccount) {
        this.dataRelAccount = dataRelAccount;
    }

    public String getDataRelAdministrator() {
        return dataRelAdministrator;
    }

    public void setDataRelAdministrator(String dataRelAdministrator) {
        this.dataRelAdministrator = dataRelAdministrator;
    }

}
