package vn.nuce.datn_be.services;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.nuce.datn_be.enity.App;
import vn.nuce.datn_be.enity.Room;
import vn.nuce.datn_be.enity.RoomAppKey;
import vn.nuce.datn_be.model.dto.RoomForm;
import vn.nuce.datn_be.model.enumeration.RoomStatus;
import vn.nuce.datn_be.model.form.RoomSearchForm;
import vn.nuce.datn_be.repositories.AppRepository;
import vn.nuce.datn_be.repositories.RoomAppKeyRepository;
import vn.nuce.datn_be.repositories.RoomRepository;
import vn.nuce.datn_be.utils.DatnUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class RoomService {
    @Autowired
    protected RoomRepository roomRepository;

    @Autowired
    AppRepository appRepository;

    @Autowired
    RoomAppKeyRepository roomAppKeyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Room> getListRoomOwnerById(Long userId) {
        return roomRepository.findByOwnerFk(userId);
    }

    public Room findById(Long roomId) {
        return roomRepository.findById(roomId).orElse(null);
    }

    public boolean existById(Long roomId) {
        return roomRepository.existsById(roomId);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public Room saveByRoomForm(RoomForm roomForm, Long monitorId) throws ParseException {
        Room room = new Room();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        room.setStartTime(DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getStartDate() + " " + roomForm.getStartTime())));
        room.setEndTime(DatnUtils.getTimeSpecifyMinute(dateFormat.parse(roomForm.getEndDate() + " " + roomForm.getEndTime())));
        room.setName(roomForm.getName());
        room.setRoomStatus(RoomStatus.INACTIVE);
        room.setOwnerFk(monitorId);
        room.setUrls(roomForm.getUrls());
        room = roomRepository.save(room);
        if (!roomForm.getApps().isEmpty()) {
            List<App> apps = appRepository.findAllByAppNameIn(roomForm.getApps());
            if (Integer.valueOf(apps.size()).equals(roomForm.getApps().size())) {
                Room finalRoom = room;
                apps.forEach(app -> {
                    RoomAppKey roomAppKey = new RoomAppKey();
                    roomAppKey.setRoomFk(finalRoom.getId());
                    roomAppKey.setAppFk(app.getId());
                    roomAppKey.setRoom(finalRoom);
                    roomAppKey.setApp(app);
                    roomAppKey = roomAppKeyRepository.save(roomAppKey);
                    if (finalRoom.getRoomAppKeys() == null) {
                        List<RoomAppKey> roomAppKeys = new LinkedList<>();
                        finalRoom.setRoomAppKeys(roomAppKeys);
                    }
                    finalRoom.getRoomAppKeys().add(roomAppKey);
                });
//                roomRepository.save(finalRoom);
            } else
                return null;
        }
        return room;
    }

    public List<Room> getAllRoomTimeOn(Date date) {
        return roomRepository.findAllByStartTimeLessThanEqualAndRoomStatusAndEndTimeGreaterThan(date, RoomStatus.INACTIVE, date);
    }

    public List<Room> getAllRoomTimeUp(Date date) {
        return roomRepository.findAllByEndTimeLessThanEqualAndRoomStatus(date, RoomStatus.ACTIVE);
    }

    public List<Room> searchRoom(String key, Long ownerFk) {
        try {
            Long keyToLong = Long.valueOf(key);
            return roomRepository.findAllDistinctByIdOrNameStartingWithAndOwnerFk(keyToLong, key, ownerFk);
        } catch (Exception e) {
            return roomRepository.findAllDistinctByIdOrNameStartingWithAndOwnerFk(null, key, ownerFk);
        }
    }

    public List<Room> findBySearchForm(RoomSearchForm searchForm) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

//        Room room = new Room();
//        room.setName(searchForm.getKeyName());
//        room.setRoomStatus(searchForm.getRoomStatus());
//        room.setStartTime(startTime);
//        room.setOwnerFk(searchForm.getMonitorId());
//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withIgnoreNullValues()
//                .withMatcher("name", startsWith().ignoreCase());
//        return roomRepository.findAll(Example.of(room, matcher));

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Room> query = builder.createQuery(Room.class);
        Root<Room> root = query.from(Room.class);
        List<Predicate> predicates = new LinkedList<>();

        if (searchForm.getKeyName() != null) {
            Predicate name = builder.like(root.get("name"), searchForm.getKeyName() + "%");
            predicates.add(name);
        }

        if (searchForm.getRoomStatus() != null) {
            Predicate status = builder.equal(root.get("roomStatus"), searchForm.getRoomStatus());
            predicates.add(status);
        }

        if (searchForm.getStartDate() != null) {
            Predicate startDate = builder.between(root.get("startTime"), DatnUtils.setTimeStartInDay(dateFormat.parse(searchForm.getStartDate())), DatnUtils.setTimeEndInDay(dateFormat.parse(searchForm.getStartDate())));
            predicates.add(startDate);
        }
        Predicate userFk = builder.equal(root.get("ownerFk"), searchForm.getMonitorId());
        predicates.add(userFk);
        query.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(query.select(root)).getResultList();
    }

    public List<Room> getListRoomNeedSendMailToCandidate() {
        Date startDate = DatnUtils.cvtToGmt(new Date(), 7);;
        Date endDate = DatnUtils.cvtToGmt(new Date(), 7);
        DatnUtils.setTimeStartInDay(startDate);
        DatnUtils.setTimeEndInDay(endDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        startDate = calendar.getTime();

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(endDate);
        calendar1.add(Calendar.DATE, 1);
        endDate = calendar1.getTime();

        return roomRepository.findAllByStartTimeBetween(startDate, endDate);
    }
}
