package models;

import com.google.gson.JsonObject;
import play.db.jpa.JPABase;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
* @author huljas
*/
public class SaleItem {

    public int data_id;
    public String name;
    public int rarity;
    public int level;
    public int max_offer;
    public int min_sale;
    public int offer_count;
    public int sale_count;

    public int type_id;
    public int sub_type_id;

    public float flipGain;

    public SaleItem(JsonObject json) {
        this.data_id = json.getAsJsonPrimitive("data_id").getAsInt();
        this.type_id = json.getAsJsonPrimitive("type_id").getAsInt();
        this.name = json.getAsJsonPrimitive("name").getAsString();
        this.rarity = json.getAsJsonPrimitive("rarity").getAsInt();
        this.level = json.getAsJsonPrimitive("restriction_level").getAsInt();
        this.max_offer = json.getAsJsonPrimitive("max_offer_unit_price").getAsInt();
        this.min_sale = json.getAsJsonPrimitive("min_sale_unit_price").getAsInt();
        this.offer_count = json.getAsJsonPrimitive("offer_availability").getAsInt();
        this.sale_count = json.getAsJsonPrimitive("sale_availability").getAsInt();
        this.flipGain = (min_sale*0.85f - max_offer) / (float) max_offer;
        this.sub_type_id = json.getAsJsonPrimitive("sub_type_id").getAsInt();
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "data_id=" + data_id +
                ", name='" + name + '\'' +
                ", rarity=" + rarity +
                ", level=" + level +
                ", max_offer=" + max_offer +
                ", min_sale=" + min_sale +
                ", offer_count=" + offer_count +
                ", sale_count=" + sale_count +
                ", flipGain=" + flipGain +
                '}';
    }

    public boolean isTracked() {
        return TrackedItem.isTracked(data_id);
    }
}
