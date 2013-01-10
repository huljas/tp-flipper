package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import play.cache.Cache;
import play.libs.F;
import play.libs.WS;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    private static Comparator<SaleItem> COMPARATOR = new Comparator<SaleItem>() {
        @Override
        public int compare(SaleItem o1, SaleItem o2) {
            return new Float(o2.flipGain).compareTo(o1.flipGain);
        }
    };

    public static void index() {

        List<TrackedItem> trackedItems = TrackedItem.findAll();

        List<F.T2<Integer, String>> mainTypes = getMainTypes();
        System.out.println("Types: " + mainTypes);
        List<SaleItem> saleItems = new ArrayList<SaleItem>();
        for (F.T2<Integer, String> type : mainTypes) {
            int id = type._1;
            List<SaleItem> items = getAllItemsByType(id);
            saleItems.addAll(items);
        }
        System.out.println("Total items: " + saleItems.size());

        List<SaleItem> tracked = filterTracked(saleItems, trackedItems);

        List<SaleItem> rares = filterRares(saleItems);

        List<SaleItem> crafting = filterCrafting(saleItems);

        List<SaleItem> expensive = filterExpensive(saleItems);

        List<SaleItem> weapons = filterWeapons(saleItems);

        List<SaleItem> armors = filterArmor(saleItems);

        List<SaleItem> trinkets = filterTrinket(saleItems);

        List<SaleItem> ectos = findEctos(saleItems);

        List<SaleItem> precursors = findPrecursors(saleItems);

        render(rares, crafting, expensive, weapons, armors, trinkets, ectos, precursors, tracked);

    }

    private static List<SaleItem> filterTracked(List<SaleItem> saleItems, List<TrackedItem> trackedItems) {
        List<SaleItem> result = new ArrayList<SaleItem>();
        for (SaleItem saleItem : saleItems) {
            for (TrackedItem trackedItem : trackedItems)  {
                if (trackedItem.dataId == saleItem.data_id) {
                    result.add(saleItem);
                }
            }
        }
        return sortByROI(result);
    }

    public static void track(int id) {
        TrackedItem.track(id);
        index();
    }

    public static void untrack(int id) {
        TrackedItem.untrack(id);
        index();
    }

    private static List<SaleItem> findEctos(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.sale_count > 1 && item.offer_count > 1 && (item.type_id == 18 || item.type_id == 0 || item.type_id == 15) && item.level > 70) {
                filtered.add(item);
            }
        }
        Collections.sort(filtered, new Comparator<SaleItem>() {
            @Override
            public int compare(SaleItem o1, SaleItem o2) {
                return new Integer(o1.max_offer).compareTo(o2.max_offer);
            }
        });
        return sublist(20, filtered);
    }

    private static List<SaleItem> findPrecursors(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.sale_count > 1 && item.offer_count > 1 && item.type_id == 18 && item.level > 74) {
                filtered.add(item);
            }
        }
        Collections.sort(filtered, new Comparator<SaleItem>() {
            @Override
            public int compare(SaleItem o1, SaleItem o2) {
                return new Integer(o1.max_offer).compareTo(o2.max_offer);
            }
        });
        return sublist(20, filtered);
    }


    private static List<SaleItem> filterRares(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.max_offer < 20000 && item.min_sale > 2000 && item.sale_count > 20 && item.offer_count > 20 && (item.type_id == 0 || item.type_id == 18 || item.type_id == 15)) {
                filtered.add(item);
            }
        }
        return sublist(20, sortByROI(filtered));
    }

    private static List<SaleItem> sublist(int amount, List<SaleItem> list) {
        if (list.size() <= amount) return list;
        else return list.subList(0, amount);
    }

    private static List<SaleItem> sortByROI(List<SaleItem> list) {
        Collections.sort(list, COMPARATOR);
        return list;
    }


    private static List<SaleItem> filterWeapons(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.max_offer < 20000 && item.min_sale > 2000 && item.sale_count > 20 && item.offer_count > 10 && item.type_id == 18 && (item.sub_type_id == 0 || item.sub_type_id == 5 || item.sub_type_id == 6 || item.sub_type_id == 12 || item.sub_type_id == 8)) {
                filtered.add(item);
            }
        }
        return sublist(100, sortByROI(filtered));
    }

    private static List<SaleItem> filterArmor(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.max_offer < 20000 && item.min_sale > 2000 && item.sale_count > 20 && item.offer_count > 10 && item.type_id == 0) {
                filtered.add(item);
            }
        }
        return sublist(100, sortByROI(filtered));

    }

    private static List<SaleItem> filterTrinket(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity >= 4 && item.max_offer < 20000 && item.min_sale > 2000 && item.sale_count > 20 && item.offer_count > 10 && item.type_id == 15) {
                filtered.add(item);
            }
        }
        return sublist(100, sortByROI(filtered));
    }

    private static List<SaleItem> filterCrafting(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.rarity <= 5 && item.max_offer < 100 && item.sale_count > 20000 && item.offer_count > 20000 && (item.type_id == 5)) {
                filtered.add(item);
            }
        }
        return sublist(20, sortByROI(filtered));
    }

    private static List<SaleItem> filterExpensive(List<SaleItem> saleItems) {
        List<SaleItem> filtered = new ArrayList<SaleItem>();
        for (SaleItem item : saleItems) {
            if (item.level > 70 && item.max_offer > 5000 && item.max_offer < 15000 && item.sale_count > 20 && item.offer_count > 20) {
                filtered.add(item);
            }
        }
        return sublist(40, sortByROI(filtered));
    }


    private static List<SaleItem> getAllItemsByType(int id) {
        String key = "SaleItemsByType-" + id;
        List<SaleItem> list = (List<SaleItem>) Cache.get(key);
        if (list == null) {
            list = new ArrayList<SaleItem>();
            JsonElement json = WS.url("http://www.gw2spidy.com/api/v0.9/json/all-items/" + id).get().getJson();
            JsonArray array = json.getAsJsonObject().get("results").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                list.add(new SaleItem(array.get(i).getAsJsonObject()));
            }
            Cache.add("SaleItemsByType-" + id, list, "10mn");
        }
        return list;
    }

    private static List<F.T2<Integer, String>> getMainTypes() {
        String key = "MainTypes";
        List<F.T2<Integer, String>> mainTypes = (List<F.T2<Integer, String>>) Cache.get(key);
        if (mainTypes == null) {
            JsonElement json = WS.url("http://www.gw2spidy.com/api/v0.9/json/types").get().getJson();
            JsonArray array = json.getAsJsonObject().get("results").getAsJsonArray();
            mainTypes = new ArrayList<F.T2<Integer, String>>();
            for (int i = 0; i < array.size(); i++) {
                String name = array.get(i).getAsJsonObject().get("name").getAsString();
                int id = array.get(i).getAsJsonObject().get("id").getAsInt();
                mainTypes.add(F.T2(id, name));
            }
            Cache.add("MainTypes", mainTypes, "30mn");
        }
        return mainTypes;
    }


}