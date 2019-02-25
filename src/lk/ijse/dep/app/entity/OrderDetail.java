package lk.ijse.dep.app.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class OrderDetail extends SuperEntity{

    @EmbeddedId
    private OrderDetailPK orderDetailPK;
    private int qty;
    private double unitPrice;

    @ManyToOne
    @JoinColumn(name = "orderId",referencedColumnName = "id",updatable = false,insertable = false)
    Order orderId;

    @ManyToOne
    @JoinColumn(name = "itemCode",referencedColumnName = "code",updatable = false,insertable = false)
    Item itemCode;

    public OrderDetail() {
    }

    public OrderDetail(OrderDetailPK orderDetailPK, int qty, double unitPrice) {
        this.orderDetailPK = orderDetailPK;
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public OrderDetail(String orderId, String itemCode, int qty, double unitPrice) {
        this.orderDetailPK = new OrderDetailPK(orderId, itemCode);
        this.qty = qty;
        this.unitPrice = unitPrice;
    }

    public Order getOrderId() {
        return orderId;
    }

    public Item getItemCode() {
        return itemCode;
    }

    public OrderDetailPK getOrderDetailPK() {
        return orderDetailPK;
    }

    public void setOrderDetailPK(OrderDetailPK orderDetailPK) {
        this.orderDetailPK = orderDetailPK;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
