package com.orion.orion.models;

public class ContestDetail {


    String entryfee,doman,userId,contestId,voteType,timestamp,regBegin,regEnd,voteBegin,voteEnd,winDec,maxLimit;
    Boolean result;

 public ContestDetail(){}

    public ContestDetail(String entryfee, String doman, String userId, String contestId, String voteType,
                         String timestamp, String regBegin, String regEnd, String voteBegin, String voteEnd, String winDec,Boolean result,String maxLimit) {
        this.entryfee = entryfee;
        this.doman = doman;
        this.userId = userId;
        this.contestId = contestId;
        this.voteType = voteType;
        this.timestamp = timestamp;
        this.regBegin = regBegin;
        this.regEnd = regEnd;
        this.voteBegin = voteBegin;
        this.voteEnd = voteEnd;
        this.winDec = winDec;
        this.result=result;
        this.maxLimit=maxLimit;
    }

    public String getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(String entryfee) {
        this.entryfee = entryfee;
    }

    public String getDoman() {
        return doman;
    }

    public void setDoman(String doman) {
        this.doman = doman;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getVoteType() {
        return voteType;
    }

    public void setVoteType(String voteType) {
        this.voteType = voteType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRegBegin() {
        return regBegin;
    }

    public void setRegBegin(String regBegin) {
        this.regBegin = regBegin;
    }

    public String getRegEnd() {
        return regEnd;
    }

    public void setRegEnd(String regEnd) {
        this.regEnd = regEnd;
    }

    public String getVoteBegin() {
        return voteBegin;
    }

    public void setVoteBegin(String voteBegin) {
        this.voteBegin = voteBegin;
    }

    public String getVoteEnd() {
        return voteEnd;
    }

    public void setVoteEnd(String voteEnd) {
        this.voteEnd = voteEnd;
    }

    public String getWinDec() {
        return winDec;
    }

    public void setWinDec(String winDec) {
        this.winDec = winDec;
    }
    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }


    @Override
    public String toString() {
        return "ContestDetail{" +
                "entryfee='" + entryfee + '\'' +
                ", doman='" + doman + '\'' +
                ", userId='" + userId + '\'' +
                ", contestId='" + contestId + '\'' +
                ", voteType='" + voteType + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", regBegin='" + regBegin + '\'' +
                ", regEnd='" + regEnd + '\'' +
                ", voteBegin='" + voteBegin + '\'' +
                ", voteEnd='" + voteEnd + '\'' +
                ", winDec='" + winDec + '\'' +
                ", result='" + result + '\'' +

                '}';
    }
}
