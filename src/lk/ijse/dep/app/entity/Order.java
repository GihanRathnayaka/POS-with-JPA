package lk.ijse.dep.app.entity;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "`Order`")
public class Order extends SuperEntity{

    @Id
    private String id;
    @Temporal(TemporalType.DATE)
    private Date date;
    @ManyToOne
    @JoinColumn(name="customerId", referencedColumnName = "id")
    private Customer customer;

    public Order() {
    }

    public Order(String id, Date date, Customer customer) {
        this.setId(id);
        this.setDate(date);
        this.setCustomer(customer);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", date=" + date +
                ", customer=" + customer +
                '}';
    }
}
