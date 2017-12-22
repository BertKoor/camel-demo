package nl.bertkoor.service;

import nl.bertkoor.model.TeamMember;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    public static final String MEMBER_SERVICE = "memberService";

    public TeamMember getMember(String id) {
        TeamMember result = new TeamMember();
        result.setName(id);
        if (id.contains("orrit")) {
            result.setJobTitle("Business Analist");
        } else if (id.contains("ehme")) {
            result.setJobTitle("Automated Tester (YP)");
        } else {
            result.setJobTitle("Java Developer");
        }
        return result;
    }
}
