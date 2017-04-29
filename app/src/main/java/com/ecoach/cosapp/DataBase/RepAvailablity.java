package com.ecoach.cosapp.DataBase;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by apple on 4/24/17.
 */


@Table(name="RepAvailablity")
public class RepAvailablity extends Model{


    @Column(name = "customer_id")
    private String customer_id;

    @Column(name = "company_id")
    private String company_id;

    @Column(name = "availability")
    private boolean availability;


    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }


    public static RepAvailablity getRepAvailablityByID(String company_id,String userid) {
        return new Select()
                .from(RepAvailablity.class)
                .where("customer_id = ?",userid).and("company_id = ?",company_id)
                .executeSingle();
    }
}
