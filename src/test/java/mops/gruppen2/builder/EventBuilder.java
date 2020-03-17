package mops.gruppen2.builder;

import com.github.javafaker.Faker;
import mops.gruppen2.domain.*;
import mops.gruppen2.domain.event.*;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {

    /**
     * Generiert ein EventLog mit mehreren Gruppen nud Usern
     *
     * @param count Gruppenanzahl
     * @param membercount Gesamte Mitgliederanzahl
     * @return
     */
    public static List<Event> completeGroups(int count, int membercount) {
        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.addAll(EventBuilder.completeGroup(i, membercount / count));
        }

        return eventList;
    }

    public static List<Event> completeGroup(long group_id, int membercount) {
        List<Event> eventList = new ArrayList<>();

        eventList.add(EventBuilder.createGroupEvent(group_id));
        eventList.add(EventBuilder.updateGroupTitleEvent(group_id));
        eventList.add(EventBuilder.updateGroupDescriptionEvent(group_id));

        eventList.addAll(EventBuilder.addUserEvents(membercount, group_id));

        return eventList;
    }

    public static CreateGroupEvent createGroupEvent(long group_id) {
        Faker faker = new Faker();

        return new CreateGroupEvent(
                group_id,
                faker.random().hex(),
                null,
                GroupType.SIMPLE,
                Visibility.PRIVATE
        );
    }

    /**
     * Generiert mehrere CreateGroupEvents, 1 <= group_id <= count
     *
     * @param count Anzahl der verschiedenen Gruppen.
     * @return
     */
    public static List<CreateGroupEvent> createGroupEvents(int count) {
        List<CreateGroupEvent> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.add(createGroupEvent(i));
        }

        return eventList;
    }

    public static AddUserEvent addUserEvent(long group_id, String user_id) {
        Faker faker = new Faker();

        String firstname = faker.name().firstName();
        String lastname = faker.name().lastName();

        return new AddUserEvent(
                group_id,
                user_id,
                firstname,
                lastname,
                firstname + "." + lastname + "@mail.de"
        );
    }

    /**
     * Generiert mehrere AddUserEvents für eine Gruppe, 1 <= user_id <= count
     *
     * @param count
     * @param group_id
     * @return
     */
    public static List<Event> addUserEvents(int count, long group_id) {
        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.add(EventBuilder.addUserEvent(group_id, "" + i));
        }

        return eventList;
    }

    public static DeleteUserEvent deleteUserEvent(long group_id, String user_id) {
        Faker faker = new Faker();

        return new DeleteUserEvent(
                group_id,
                user_id
        );
    }

    /**
     * Erzeugt mehrere DeleteUserEvents, sodass eine Gruppe komplett geleert wird
     *
     * @param group Gruppe welche geleert wird
     * @return
     */
    public static List<DeleteUserEvent> deleteUserEvents(Group group) {
        List<DeleteUserEvent> eventList = new ArrayList<>();

        for (User user : group.getMembers()) {
            eventList.add(EventBuilder.deleteUserEvent(group.getId(), user.getUser_id()));
        }

        return eventList;
    }

    public static UpdateGroupDescriptionEvent updateGroupDescriptionEvent(long group_id) {
        Faker faker = new Faker();

        return new UpdateGroupDescriptionEvent(
                group_id,
                faker.random().hex(),
                faker.leagueOfLegends().quote()
        );
    }

    public static UpdateGroupTitleEvent updateGroupTitleEvent(long group_id) {
        Faker faker = new Faker();

        return new UpdateGroupTitleEvent(
                group_id,
                faker.random().hex(),
                faker.leagueOfLegends().champion()
        );
    }

    public static UpdateRoleEvent randomUpdateRoleEvent(long group_id, String user_id, Role role) {
        Faker faker = new Faker();

        return new UpdateRoleEvent(
                group_id,
                user_id,
                role
        );
    }
}
