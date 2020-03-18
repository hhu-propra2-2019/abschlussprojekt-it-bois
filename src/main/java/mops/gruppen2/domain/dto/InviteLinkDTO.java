package mops.gruppen2.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("invite")
@Data
@AllArgsConstructor
public class InviteLinkDTO {

    @Id
    Long link_id;
    Long group_id;
    String invite_link;
}
