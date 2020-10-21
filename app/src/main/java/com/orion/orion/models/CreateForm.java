package com.orion.orion.models;

public class CreateForm {
    public CreateForm(){

    }

    String entryfee,title,descrip,poster,filetype,domain,votetype,rule,regBegin,regEnd,
    voteBegin,voteEnd,winDeclare,numParticipants,maxLimit,pmoney,place_1,place_2,place_3,total_prize,
    numJury,jname_1,jname_2,jname_3,jpic1,jpic2,jpic3,timestamp,institute,contestkey,
            host,openFor,userid,status;

    public CreateForm(String entryfee, String title, String descrip, String poster, String filetype, String domain, String votetype,
                      String rule, String regBegin, String regEnd, String voteBegin, String voteEnd, String winDeclare, String numParticipants,
                      String maxLimit, String pmoney, String place_1, String place_2, String place_3, String total_prize, String numJury, String jname_1,
                      String jname_2, String jname_3, String jpic1, String jpic2, String jpic3, String timestamp,
                      String institute,String contestkey,String host,String openFor,String userid,String status) {
        this.entryfee = entryfee;
        this.title = title;
        this.descrip = descrip;
        this.poster = poster;
        this.filetype = filetype;
        this.domain = domain;
        this.votetype = votetype;
        this.rule = rule;
        this.regBegin = regBegin;
        this.regEnd = regEnd;
        this.voteBegin = voteBegin;
        this.voteEnd = voteEnd;
        this.winDeclare = winDeclare;
        this.numParticipants = numParticipants;
        this.maxLimit = maxLimit;
        this.pmoney = pmoney;
        this.place_1 = place_1;
        this.place_2 = place_2;
        this.place_3 = place_3;
        this.total_prize = total_prize;
        this.numJury = numJury;
        this.jname_1 = jname_1;
        this.jname_2 = jname_2;
        this.jname_3 = jname_3;

        this.jpic1 = jpic1;
        this.jpic2 = jpic2;
        this.jpic3 = jpic3;
        this.timestamp = timestamp;
        this.institute = institute;
        this.contestkey=contestkey;
        this.host=host;
        this.openFor=openFor;
        this.userid=userid;
        this.status=status;
    }


    public String getEntryfee() {
        return entryfee;
    }

    public void setEntryfee(String entryfee) {
        this.entryfee = entryfee;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getVotetype() {
        return votetype;
    }

    public void setVotetype(String votetype) {
        this.votetype = votetype;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
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

    public String getWinDeclare() {
        return winDeclare;
    }

    public void setWinDeclare(String winDeclare) {
        this.winDeclare = winDeclare;
    }



    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }


    public String getPlace_1() {
        return place_1;
    }

    public void setPlace_1(String place_1) {
        this.place_1 = place_1;
    }

    public String getPlace_2() {
        return place_2;
    }

    public void setPlace_2(String place_2) {
        this.place_2 = place_2;
    }

    public String getPlace_3() {
        return place_3;
    }

    public void setPlace_3(String place_3) {
        this.place_3 = place_3;
    }

    public String getTotal_prize() {
        return total_prize;
    }

    public void setTotal_prize(String total_prize) {
        this.total_prize = total_prize;
    }


    public String getJname_1() {
        return jname_1;
    }

    public void setJname_1(String jname_1) {
        this.jname_1 = jname_1;
    }

    public String getJname_2() {
        return jname_2;
    }

    public void setJname_2(String jname_2) {
        this.jname_2 = jname_2;
    }

    public String getJname_3() {
        return jname_3;
    }

    public void setJname_3(String jname_3) {
        this.jname_3 = jname_3;
    }



    public String getJpic1() {
        return jpic1;
    }

    public void setJpic1(String jpic1) {
        this.jpic1 = jpic1;
    }

    public String getJpic2() {
        return jpic2;
    }

    public void setJpic2(String jpic2) {
        this.jpic2 = jpic2;
    }

    public String getJpic3() {
        return jpic3;
    }

    public void setJpic3(String jpic3) {
        this.jpic3 = jpic3;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public String getContestkey() {
        return contestkey;
    }

    public void setContestkey(String contestkey) {
        this.contestkey = contestkey;
    }
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    public String getOpenFor() {
        return openFor;
    }

    public void setOpenFor(String openFor) {
        this.openFor = openFor;
    }
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CreateForm{" +
                "entryfee='" + entryfee + '\'' +
                ", title='" + title + '\'' +
                ", descrip='" + descrip + '\'' +
                ", poster='" + poster + '\'' +
                ", filetype='" + filetype + '\'' +
                ", domain='" + domain + '\'' +
                ", votetype='" + votetype + '\'' +
                ", rule='" + rule + '\'' +
                ", regBegin='" + regBegin + '\'' +
                ", regEnd='" + regEnd + '\'' +
                ", voteBegin='" + voteBegin + '\'' +
                ", voteEnd='" + voteEnd + '\'' +
                ", winDeclare='" + winDeclare + '\'' +
                ", numParticipants='" + numParticipants + '\'' +
                ", maxLimit='" + maxLimit + '\'' +
                ", pmoney='" + pmoney + '\'' +
                ", place_1='" + place_1 + '\'' +
                ", place_2='" + place_2 + '\'' +
                ", place_3='" + place_3 + '\'' +
                ", total_prize='" + total_prize + '\'' +
                ", numJury='" + numJury + '\'' +
                ", jname_1='" + jname_1 + '\'' +
                ", jname_2='" + jname_2 + '\'' +
                ", jname_3='" + jname_3 + '\'' +

                ", jpic1='" + jpic1 + '\'' +
                ", jpic2='" + jpic2 + '\'' +
                ", jpic3='" + jpic3 + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", institute='" + institute + '\'' +
                ", contestkey='" + contestkey + '\'' +
                ", openFor='" + openFor + '\'' +
                ", host='" + host + '\'' +
                ", userid='" + userid + '\'' +
                ", status='" + status + '\'' +




                '}';
    }
}
