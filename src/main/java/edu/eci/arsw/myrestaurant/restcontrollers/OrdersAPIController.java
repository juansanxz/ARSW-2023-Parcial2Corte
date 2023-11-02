/*
 * Copyright (C) 2016 Pivotal Software, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.arsw.myrestaurant.restcontrollers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import edu.eci.arsw.myrestaurant.model.Order;
import edu.eci.arsw.myrestaurant.model.ProductType;
import edu.eci.arsw.myrestaurant.model.RestaurantProduct;
import edu.eci.arsw.myrestaurant.services.OrderServicesException;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServices;
import edu.eci.arsw.myrestaurant.services.RestaurantOrderServicesStub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.CollectionUtils.hasUniqueObject;
import static org.springframework.util.CollectionUtils.toArray;

/**
 *
 * @author hcadavid
 */

@RestController
@RequestMapping(path="/orders")
public class OrdersAPIController {

    @Autowired
    RestaurantOrderServices restaurantOrSer;

    @RequestMapping(method = RequestMethod.GET)
    @ExceptionHandler(OrderServicesException.class)
    public ResponseEntity handleGetOrders() {
        Gson gson = new Gson();

        Set<Integer> tableNumbers = restaurantOrSer.getTablesWithOrders();
        JSONArray allOrdersArray = new JSONArray();
        for (Integer tableNumber : tableNumbers) {
            try {
                JSONObject jsonObject = new JSONObject();
                int bill = restaurantOrSer.calculateTableBill(tableNumber);
                jsonObject.put("Table number: ", tableNumber);
                jsonObject.put("order", restaurantOrSer.getTableOrder(tableNumber).getOrderAmountsMap());
                jsonObject.put("Total: ", bill);


                allOrdersArray.add(jsonObject);
            } catch (OrderServicesException e) {
                throw new RuntimeException(e);
            }

        }


        return new ResponseEntity(allOrdersArray, HttpStatus.OK);
    }
    
}
