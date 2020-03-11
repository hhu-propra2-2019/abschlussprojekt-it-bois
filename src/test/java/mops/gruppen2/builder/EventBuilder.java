package mops.gruppen2.builder;

import com.github.javafaker.Faker;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {

    public static CreateGroupEvent randomCreateGroupEvent() {
        Faker faker = new Faker();

        return null;/*new CreateGroupEvent(
                faker.random().nextLong(),
                faker.random().nextLong(),
                faker.random().hex(),
                faker.leagueOfLegends().champion(),
                faker.leagueOfLegends().quote()
        );*/
    }

    public static AddUserEvent randomAddUserEvent(long group_id) {
        Faker faker = new Faker();

        String firstname = faker.name().firstName();
        String lastname = faker.name().lastName();

        return new AddUserEvent(
                faker.random().nextLong(),
                group_id,
                faker.random().hex(),
                firstname,
                lastname,
                firstname + "." + lastname + "@mail.de"
        );
    }

    public static List<Event> randomAddUserEvents(int count, long group_id) {
        List<Event> eventList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            eventList.add(EventBuilder.randomAddUserEvent(group_id));
        }

        return eventList;
    }

    public static DeleteUserEvent randomDeleteUserEvent(long group_id, String user_id) {
        Faker faker = new Faker();

        return new DeleteUserEvent(
                faker.random().nextLong(),
                group_id,
                user_id
        );
    }

    public static UpdateGroupDescriptionEvent randomUpdateGroupDescriptionEvent(long group_id) {
        Faker faker = new Faker();

        return new UpdateGroupDescriptionEvent(
                faker.random().nextLong(),
                group_id,
                faker.random().hex(),
                faker.leagueOfLegends().quote()
        );
    }

    public static UpdateGroupTitleEvent randomUpdateGroupTitleEvent(long group_id) {
        Faker faker = new Faker();

        return new UpdateGroupTitleEvent(
                faker.random().nextLong(),
                group_id,
                faker.random().hex(),
                faker.leagueOfLegends().champion()
        );
    }

    public static UpdateRoleEvent randomUpdateRoleEvent(long group_id, String user_id, Role role) {
        Faker faker = new Faker();

        return new UpdateRoleEvent(
                faker.random().nextLong(),
                group_id,
                user_id,
                role
        );
    }
}
