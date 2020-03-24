package mops.gruppen2;

import com.github.javafaker.Faker;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteUserEvent;
import mops.gruppen2.domain.event.Event;
import mops.gruppen2.domain.event.UpdateGroupDescriptionEvent;
import mops.gruppen2.domain.event.UpdateGroupTitleEvent;
import mops.gruppen2.domain.event.UpdateRoleEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestBuilder {

    private static final Faker faker = new Faker();

    /**
     * Baut eine UUID.
     *
     * @param i Zahl von 0 bis 9
     * @return UUID
     */
    public static UUID idFromNumber(int i) {
        if (i > 9) {
            return null;
        }
        return UUID.fromString("00000000-0000-0000-0000-00000000000" + i);
    }

    /**
     * Generiert ein EventLog mit mehreren Gruppen und Usern.
     *
     * @param count       Gruppenanzahl
     * @param membercount Gesamte Mitgliederanzahl
     * @return Eventliste
     */
    public static List<Event> completeGroups(int count, int membercount) {
        int memPerGroup = membercount / count;

        return IntStream.rangeClosed(0, count)
                        .parallel()
                        .mapToObj(i -> completeGroup(memPerGroup))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
    }

    public static List<Event> completeGroup(int membercount) {
        List<Event> eventList = new ArrayList<>();
        UUID groupId = UUID.randomUUID();

        eventList.add(createGroupEvent(groupId));
        eventList.add(updateGroupTitleEvent(groupId));
        eventList.add(updateGroupDescriptionEvent(groupId));
        eventList.addAll(addUserEvents(membercount, groupId));

        return eventList;
    }

    public static List<Event> completeGroup() {
        return completeGroup(100);
    }

    /**
     * Generiert mehrere CreateGroupEvents, 1 <= groupId <= count.
     *
     * @param count Anzahl der verschiedenen Gruppen
     * @return Eventliste
     */
    public static List<CreateGroupEvent> createGroupEvents(int count) {
        return IntStream.rangeClosed(0, count)
                        .parallel()
                        .mapToObj(i -> createGroupEvent())
                        .collect(Collectors.toList());
    }

    public static CreateGroupEvent createGroupEvent(UUID groupId) {
        return new CreateGroupEvent(
                groupId,
                faker.random().hex(),
                null,
                GroupType.SIMPLE,
                Visibility.PUBLIC,
                10000000L
        );
    }

    public static CreateGroupEvent createGroupEvent() {
        return createGroupEvent(UUID.randomUUID());
    }

    /**
     * Generiert mehrere AddUserEvents für eine Gruppe, 1 <= user_id <= count.
     *
     * @param count   Anzahl der Mitglieder
     * @param groupId Gruppe, zu welcher geaddet wird
     * @return Eventliste
     */
    public static List<Event> addUserEvents(int count, UUID groupId) {
        return IntStream.rangeClosed(1, count)
                        .parallel()
                        .mapToObj(i -> addUserEvent(groupId, String.valueOf(i)))
                        .collect(Collectors.toList());
    }

    public static AddUserEvent addUserEvent(UUID groupId, String userId) {
        String firstname = firstname();
        String lastname = lastname();

        return new AddUserEvent(
                groupId,
                userId,
                firstname,
                lastname,
                firstname + "." + lastname + "@mail.de"
        );
    }

    public static AddUserEvent addUserEvent(UUID groupId) {
        return addUserEvent(groupId, faker.random().hex());
    }

    public static List<Event> deleteUserEvents(int count, List<Event> eventList) {
        List<Event> removeEvents = new ArrayList<>();
        List<Event> shuffle = eventList.parallelStream()
                                       .filter(event -> event instanceof AddUserEvent)
                                       .collect(Collectors.toList());

        Collections.shuffle(shuffle);

        for (Event event : shuffle) {
            removeEvents.add(new DeleteUserEvent(event.getGroupId(), event.getUserId()));

            if (removeEvents.size() >= count) {
                break;
            }
        }

        return removeEvents;
    }

    /**
     * Erzeugt mehrere DeleteUserEvents, sodass eine Gruppe komplett geleert wird.
     *
     * @param group Gruppe welche geleert wird
     * @return Eventliste
     */
    public static List<DeleteUserEvent> deleteUserEvents(Group group) {
        return group.getMembers().parallelStream()
                    .map(user -> deleteUserEvent(group.getId(), user.getId()))
                    .collect(Collectors.toList());
    }

    public static DeleteUserEvent deleteUserEvent(UUID groupId, String userId) {
        return new DeleteUserEvent(
                groupId,
                userId
        );
    }

    public static UpdateGroupDescriptionEvent updateGroupDescriptionEvent(UUID groupId) {
        return new UpdateGroupDescriptionEvent(
                groupId,
                faker.random().hex(),
                quote()
        );
    }

    public static UpdateGroupTitleEvent updateGroupTitleEvent(UUID groupId) {
        return new UpdateGroupTitleEvent(
                groupId,
                faker.random().hex(),
                champion()
        );
    }

    public static UpdateRoleEvent randomUpdateRoleEvent(UUID groupId, String userId, Role role) {
        return new UpdateRoleEvent(
                groupId,
                userId,
                role
        );
    }

    private static String firstname() {
        return clean(faker.name().firstName());
    }

    private static String lastname() {
        return clean(faker.name().lastName());
    }

    private static String champion() {
        return clean(faker.leagueOfLegends().champion());
    }

    private static String quote() {
        return clean(faker.leagueOfLegends().quote());
    }

    private static String clean(String string) {
        return string.replaceAll("['\";,]", "");
    }
}
