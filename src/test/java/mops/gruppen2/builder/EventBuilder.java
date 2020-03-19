package mops.gruppen2.builder;

import com.github.javafaker.Faker;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.User;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;

import java.util.ArrayList;
import java.util.List;

public class EventBuilder {

    /**
     * Generiert ein EventLog mit mehreren Gruppen nud Usern.
     *
     * @param count       Gruppenanzahl
     * @param membercount Gesamte Mitgliederanzahl
     * @return Eventliste
     */
    public static List<Event> completeGroups(int count, int membercount) {
        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.addAll(completeGroup(i, membercount / count));
        }

        return eventList;
    }

    public static List<Event> completeGroup(long groupId, int membercount) {
        List<Event> eventList = new ArrayList<>();

        eventList.add(createGroupEvent(groupId));
        eventList.add(updateGroupTitleEvent(groupId));
        eventList.add(updateGroupDescriptionEvent(groupId));

        eventList.addAll(addUserEvents(membercount, groupId));

        return eventList;
    }

    public static CreateGroupEvent createGroupEvent(long groupId) {
        Faker faker = new Faker();

        return new CreateGroupEvent(
                groupId,
                faker.random().hex(),
                null,
                GroupType.SIMPLE,
                Visibility.PRIVATE,
                null
        );
    }

    /**
     * Generiert mehrere CreateGroupEvents, 1 <= groupId <= count.
     *
     * @param count Anzahl der verschiedenen Gruppen
     * @return Eventliste
     */
    public static List<CreateGroupEvent> createGroupEvents(int count) {
        List<CreateGroupEvent> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.add(createGroupEvent(i));
        }

        return eventList;
    }

    public static AddUserEvent addUserEvent(long groupId, String userId) {
        Faker faker = new Faker();

        String firstname = faker.name().firstName();
        String lastname = faker.name().lastName();

        return new AddUserEvent(
                groupId,
                userId,
                firstname,
                lastname,
                firstname + "." + lastname + "@mail.de"
        );
    }

    /**
     * Generiert mehrere AddUserEvents für eine Gruppe, 1 <= user_id <= count.
     *
     * @param count   Anzahl der Mitglieder
     * @param groupId Gruppe, zu welcher geaddet wird
     * @return Eventliste
     */
    public static List<Event> addUserEvents(int count, long groupId) {
        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            eventList.add(addUserEvent(groupId, String.valueOf(i)));
        }

        return eventList;
    }

    public static DeleteUserEvent deleteUserEvent(long groupId, String userId) {
        return new DeleteUserEvent(
                groupId,
                userId
        );
    }

    /**
     * Erzeugt mehrere DeleteUserEvents, sodass eine Gruppe komplett geleert wird.
     *
     * @param group Gruppe welche geleert wird
     * @return Eventliste
     */
    public static List<DeleteUserEvent> deleteUserEvents(Group group) {
        List<DeleteUserEvent> eventList = new ArrayList<>();

        for (User user : group.getMembers()) {
            eventList.add(deleteUserEvent(group.getId(), user.getId()));
        }

        return eventList;
    }

    public static UpdateGroupDescriptionEvent updateGroupDescriptionEvent(long groupId) {
        Faker faker = new Faker();

        return new UpdateGroupDescriptionEvent(
                groupId,
                faker.random().hex(),
                faker.leagueOfLegends().quote()
        );
    }

    public static UpdateGroupTitleEvent updateGroupTitleEvent(long groupId) {
        Faker faker = new Faker();

        return new UpdateGroupTitleEvent(
                groupId,
                faker.random().hex(),
                faker.leagueOfLegends().champion()
        );
    }

    public static UpdateRoleEvent randomUpdateRoleEvent(long groupId, String userId, Role role) {
        return new UpdateRoleEvent(
                groupId,
                userId,
                role
        );
    }
}
