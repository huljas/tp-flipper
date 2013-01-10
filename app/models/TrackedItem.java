package models;

import play.db.jpa.JPABase;
import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * @author huljas
 */
@Entity
public class TrackedItem extends Model {

    public int dataId;

    public int bought;
    public int sold;

    public TrackedItem(Integer dataId) {
        this.dataId = dataId;
    }

    public TrackedItem() {
    }

    public static TrackedItem findByDataId(Integer id) {
        List<TrackedItem> list = find("byDataId", id).fetch();
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public static boolean isTracked(int id) {
        return findByDataId(id) != null;
    }

    public static void track(int id) {
       if (!isTracked(id)) {
           TrackedItem item = new TrackedItem(id);
           item.save();
       }

    }

    public static void untrack(int id) {
        if (isTracked(id)) {
            TrackedItem item = findByDataId(id);
            item.delete();
        }
    }
}
