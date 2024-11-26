package org.sounfury.portal.dto.rep;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.sounfury.jooq.tables.pojos.SiteCreatorInfo;

@Data
@AllArgsConstructor
public class SiteCreatorInfoRep {

    private String nickName;
    private String avatarUrl;
    private String authorDescription;
    private String homepageUrl;

    public SiteCreatorInfoRep(SiteCreatorInfo siteCreatorInfo) {
        this.nickName = siteCreatorInfo.getNickName();
        this.avatarUrl = siteCreatorInfo.getAvatarUrl();
        this.authorDescription = siteCreatorInfo.getAuthorDescription();
        this.homepageUrl = siteCreatorInfo.getHomepageUrl();
    }

}
