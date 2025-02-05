package com.example.appshopping.Helper;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;


import com.example.appshopping.Domain.ItemsDomain;

import java.util.ArrayList;

public class ManagmentCart {

    private Context context;
    private TinyDB tinyDB;

    public ManagmentCart(Context context) {
        this.context = context;
        this.tinyDB = new TinyDB(context);
    }

    public void insertItems(ItemsDomain item, TextView totalItems) {
        ArrayList<ItemsDomain> listItems = getListCart();
        boolean existAlready = false;
        int n = 0;

        // Kiểm tra sản phẩm có trong giỏ chưa
        for (int y = 0; y < listItems.size(); y++) {
            if (listItems.get(y).getTitle().equals(item.getTitle())) {
                existAlready = true;
                n = y;
                break;
            }
        }

        if (existAlready) {
            // Nếu đã có, chỉ cần cập nhật số lượng
            int currentNumber = listItems.get(n).getNumberinCart();
            listItems.get(n).setNumberinCart(currentNumber + item.getNumberinCart());
            Toast.makeText(context, "Updated item quantity in your Cart", Toast.LENGTH_SHORT).show();
        } else {
            // Nếu chưa có, thêm sản phẩm mới
            listItems.add(item);
            Toast.makeText(context, "Added new item to your Cart", Toast.LENGTH_SHORT).show();
        }

        // Cập nhật lại giỏ hàng vào TinyDB
        tinyDB.putListObject("CartList", listItems);

        // Cập nhật số lượng mặt hàng khác nhau trong TextView
        totalItems.setText(String.valueOf(listItems.size()));
    }


    public ArrayList<ItemsDomain> getListCart() {
        return tinyDB.getListObject("CartList");
    }

    public void minusItem(ArrayList<ItemsDomain> listItems, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        if (listItems.get(position).getNumberinCart() == 1) {
            listItems.remove(position);
        } else {
            listItems.get(position).setNumberinCart(listItems.get(position).getNumberinCart() - 1);
        }
        tinyDB.putListObject("CartList", listItems);
        changeNumberItemsListener.changed();
    }

    public void plusItem(ArrayList<ItemsDomain> listItems, int position, ChangeNumberItemsListener changeNumberItemsListener) {
        listItems.get(position).setNumberinCart(listItems.get(position).getNumberinCart() + 1);
        tinyDB.putListObject("CartList", listItems);
        changeNumberItemsListener.changed();
    }

    public Double getTotalFee() {
        ArrayList<ItemsDomain> listItems2 = getListCart();
        double fee = 0;
        for (int i = 0; i < listItems2.size(); i++) {
            fee = fee + (listItems2.get(i).getPrice() * listItems2.get(i).getNumberinCart());
        }
        return fee;
    }
}
