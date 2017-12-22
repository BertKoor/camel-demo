package nl.bertkoor.service;

import org.springframework.stereotype.Service;
import nl.bertkoor.model.TeamMember;

@Service
public class MemberService {
    public static final String MEMBER_SERVICE = MemberService.class.getSimpleName();

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
