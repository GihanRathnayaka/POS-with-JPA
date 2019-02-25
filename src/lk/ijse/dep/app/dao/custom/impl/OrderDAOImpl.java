package lk.ijse.dep.app.dao.custom.impl;

import lk.ijse.dep.app.dao.CrudDAO;
import lk.ijse.dep.app.dao.CrudDAOImpl;
import lk.ijse.dep.app.dao.custom.OrderDAO;
import lk.ijse.dep.app.entity.Order;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class OrderDAOImpl extends CrudDAOImpl<Order,String> implements OrderDAO {


}
