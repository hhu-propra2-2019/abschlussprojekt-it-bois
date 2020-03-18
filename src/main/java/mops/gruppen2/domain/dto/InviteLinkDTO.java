package mops.gruppen2.domain.dto;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@SuppressWarnings("checkstyle:membername")
@Table("invite")
@Value
public class InviteLinkDTO {

    @Id
    Long link_id;
    Long group_id;
    String invite_link;
}
