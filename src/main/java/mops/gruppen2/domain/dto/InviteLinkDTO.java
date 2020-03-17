package mops.gruppen2.domain.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("invite")
@Data
public class InviteLinkDTO {
    @Id
    private Long group_id;
    private String invite_link;
}
