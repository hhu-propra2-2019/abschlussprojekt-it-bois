package mops.gruppen2.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("invite")
@Getter
@AllArgsConstructor
public class InviteLinkDTO {

    @Id
    Long link_id;
    String group_id;
    String invite_link;
}
