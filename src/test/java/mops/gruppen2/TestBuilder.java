package mops.gruppen2;

import com.github.javafaker.Faker;
import mops.gruppen2.domain.Account;
import mops.gruppen2.domain.Group;
import mops.gruppen2.domain.GroupType;
import mops.gruppen2.domain.Role;
import mops.gruppen2.domain.Visibility;
import mops.gruppen2.domain.event.AddUserEvent;
import mops.gruppen2.domain.event.CreateGroupEvent;
import mops.gruppen2.domain.event.DeleteGroupEvent;
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

    public static Account account(String name) {
        return new Account(name,
                           "",
                           "",
                           "",
                           "",
                           null);
    }

    public static Group apply(Group group, Event... events) {
        for (Event event : events) {
            event.apply(group);
        }

        return group;
    }

    public static Group apply(Event... events) {
        return apply(new Group(), events);
    }

    /**
     * Baut eine UUID.
     *
     * @param id Integer id
     *
     * @return UUID
     */
    public static UUID uuidMock(int id) {
        String idString = String.valueOf(Math.abs(id + 1));
        return UUID.fromString("00000000-0000-0000-0000-"
                               + "0".repeat(11 - idString.length())
                               + idString);
    }

    /**
     * Generiert ein EventLog mit mehreren Gruppen und Usern.
     *
     * @param count       Gruppenanzahl
     * @param membercount Mitgliederanzahl pro Gruppe
     *
     * @return Eventliste
     */
    public static List<Event> completePublicGroups(int count, int membercount) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> completePublicGroup(membercount))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
    }

    public static List<Event> completePrivateGroups(int count, int membercount) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> completePrivateGroup(membercount))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
    }

    public static List<Event> completePublicGroup(int membercount) {
        List<Event> eventList = new ArrayList<>();
        UUID groupId = UUID.randomUUID();

        eventList.add(createPublicGroupEvent(groupId));
        eventList.add(updateGroupTitleEvent(groupId));
        eventList.add(updateGroupDescriptionEvent(groupId));
        eventList.addAll(addUserEvents(membercount, groupId));

        return eventList;
    }

    public static List<Event> completePrivateGroup(int membercount) {
        List<Event> eventList = new ArrayList<>();
        UUID groupId = UUID.randomUUID();

        eventList.add(createPrivateGroupEvent(groupId));
        eventList.add(updateGroupTitleEvent(groupId));
        eventList.add(updateGroupDescriptionEvent(groupId));
        eventList.addAll(addUserEvents(membercount, groupId));

        return eventList;
    }

    public static List<Event> completePublicGroup() {
        return completePublicGroup(100);
    }

    public static List<Event> completePrivateGroup() {
        return completePrivateGroup(100);
    }

    /**
     * Generiert mehrere CreateGroupEvents, 1 <= groupId <= count.
     *
     * @param count Anzahl der verschiedenen Gruppen
     *
     * @return Eventliste
     */
    public static List<Event> createPublicGroupEvents(int count) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> createPublicGroupEvent())
                        .collect(Collectors.toList());
    }

    public static List<Event> createPrivateGroupEvents(int count) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> createPublicGroupEvent())
                        .collect(Collectors.toList());
    }

    public static List<Event> createMixedGroupEvents(int count) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> faker.random().nextInt(0, 1) > 0.5
                                ? createPublicGroupEvent()
                                : createPrivateGroupEvent())
                        .collect(Collectors.toList());
    }

    public static Event createPrivateGroupEvent(UUID groupId) {
        return createGroupEvent(groupId, Visibility.PRIVATE);
    }

    public static Event createPrivateGroupEvent() {
        return createPrivateGroupEvent(UUID.randomUUID());
    }

    public static Event createPublicGroupEvent(UUID groupId) {
        return createGroupEvent(groupId, Visibility.PUBLIC);
    }

    public static Event createPublicGroupEvent() {
        return createPublicGroupEvent(UUID.randomUUID());
    }

    public static Event createGroupEvent(UUID groupId, Visibility visibility) {
        return new CreateGroupEvent(
                groupId,
                faker.random().hex(),
                null,
                GroupType.SIMPLE,
                visibility,
                10000000L
        );
    }

    public static Event createLectureEvent() {
        return createLectureEvent(UUID.randomUUID());
    }

    public static Event createLectureEvent(UUID groupId) {
        return new CreateGroupEvent(
                groupId,
                faker.random().hex(),
                null,
                GroupType.LECTURE,
                Visibility.PUBLIC,
                10000000L
        );
    }

    /**
     * Generiert mehrere AddUserEvents für eine Gruppe, 1 <= user_id <= count.
     *
     * @param count   Anzahl der Mitglieder
     * @param groupId Gruppe, zu welcher geaddet wird
     *
     * @return Eventliste
     */
    public static List<Event> addUserEvents(int count, UUID groupId) {
        return IntStream.range(0, count)
                        .parallel()
                        .mapToObj(i -> addUserEvent(groupId, String.valueOf(i)))
                        .collect(Collectors.toList());
    }

    public static Event addUserEvent(UUID groupId, String userId) {
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

    public static Event addUserEvent(UUID groupId) {
        return addUserEvent(groupId, faker.random().hex());
    }

    // Am besten einfach nicht benutzen
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
     *
     * @return Eventliste
     */
    public static List<Event> deleteUserEvents(Group group) {
        return group.getMembers().parallelStream()
                    .map(user -> deleteUserEvent(group.getId(), user.getId()))
                    .collect(Collectors.toList());
    }

    public static Event deleteUserEvent(UUID groupId, String userId) {
        return new DeleteUserEvent(
                groupId,
                userId
        );
    }

    public static Event updateGroupDescriptionEvent(UUID groupId) {
        return updateGroupDescriptionEvent(groupId, quote());
    }

    public static Event updateGroupDescriptionEvent(UUID groupId, String description) {
        return new UpdateGroupDescriptionEvent(
                groupId,
                faker.random().hex(),
                description
        );
    }

    public static Event updateGroupTitleEvent(UUID groupId) {
        return updateGroupTitleEvent(groupId, champion());
    }

    public static Event updateGroupTitleEvent(UUID groupId, String title) {
        return new UpdateGroupTitleEvent(
                groupId,
                faker.random().hex(),
                title
        );
    }

    public static Event updateRoleEvent(UUID groupId, String userId, Role role) {
        return new UpdateRoleEvent(
                groupId,
                userId,
                role
        );
    }

    public static Event deleteGroupEvent(UUID groupId) {
        return new DeleteGroupEvent(groupId, faker.random().hex());
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
